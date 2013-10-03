/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/

package com.exigeninsurance.x4j.analytic.util;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.exigeninsurance.x4j.analytic.api.Cursor;
import com.exigeninsurance.x4j.analytic.api.ReportContext;
import com.exigeninsurance.x4j.analytic.api.ReportDataCallback;
import com.exigeninsurance.x4j.analytic.api.ReportDataProvider;
import com.exigeninsurance.x4j.analytic.api.ReportException;
import com.exigeninsurance.x4j.analytic.model.Query;


public class CursorManager {

	private static final Logger log = LoggerFactory.getLogger(CursorManager.class);

	private ReportContext reportContext;
	private ReportDataProvider dataProvider;

	private Map<Query, File> managedFiles = new HashMap<Query, File>();
	private List<Cursor> managedCursors = new ArrayList<Cursor>();

	public CursorManager(ReportContext reportContext, ReportDataProvider dataProvider) {
		this.reportContext = reportContext;
		this.dataProvider = dataProvider;
	}

	public void releaseManagedResources() {
		for (Cursor cursor : managedCursors) {
			if(!cursor.isClosed()){
				cursor.close();
			}
		}
		for (File file : managedFiles.values()) {			
			delete(file);
		}
	}

	public Cursor createCursor(Query query) {
		query.setMetadata(reportContext.getMetadata());
        return managedFiles.containsKey(query) ? new FileCursor(managedFiles.get(query)) : createManagedCursor(query);
	}

    private Cursor createManagedCursor(Query query) {
       
        final File file = createFile();
        try {
            writeRsToFile(query,reportContext, file);
        } catch (Exception e) {
        	delete(file);
            throw new ReportException(e);
        }

        managedFiles.put(query, file);
        FileCursor cursor = new FileCursor(file);
        managedCursors.add(cursor);
        return cursor;
    }

	private void delete(final File file) {
		if (!file.delete()) {
			log.warn("Failed to delete file {}", file.getAbsolutePath());
		}
	}

    private void writeRsToFile(Query query,ReportContext data, final File file) throws SQLException {
		dataProvider.execute(query,data, new ReportDataCallback() {

			public void process(Cursor rs) throws Exception {
				Cursor cursor = new PersistingCursor(file, rs);
				try {
					while (cursor.next()) {}
				}
				finally {
					cursor.close();
				}
			}
		});
	}

	private File createFile() {
		try {
			return IOUtils.createTempFile("cursor");
		} catch (IOException e) {
			throw new ReportException("Failed to create temp file for reusable cursor",e);
		}
	}
}
