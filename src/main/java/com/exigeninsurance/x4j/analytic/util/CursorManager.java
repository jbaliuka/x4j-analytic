/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/

package com.exigeninsurance.x4j.analytic.util;

import java.io.File;
import java.io.IOException;
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
	
	
	private static class ManagedFile{
		
		int rowCount;
		File file;
	}

	private static final Logger log = LoggerFactory.getLogger(CursorManager.class);

	private ReportContext reportContext;
	private ReportDataProvider dataProvider;

	private Map<Query, ManagedFile> managedFiles = new HashMap<Query, ManagedFile>();
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
		for (ManagedFile file : managedFiles.values()) {			
			delete(file);
		}
	}

	public Cursor createCursor(Query query) {
		query.setMetadata(reportContext.getMetadata());
		ManagedFile file = managedFiles.get(query);
        return managedFiles.containsKey(query) ? new FileCursor(file.file,file.rowCount) : createManagedCursor(query);
	}

    private Cursor createManagedCursor(Query query) {
       
        int rowCount = 0;
    	final File file = createFile();
        try {
        	rowCount = writeRsToFile(query,reportContext, file);
        } catch (Exception e) {
        	file.delete();
            throw new ReportException(e);
        }

        ManagedFile managedFile = new ManagedFile();
        managedFile.file = file;
        managedFile.rowCount = rowCount;
        managedFiles.put(query, managedFile);
        FileCursor cursor = new FileCursor(file,rowCount);
        managedCursors.add(cursor);
        return cursor;
    }

	private void delete(final ManagedFile file) {
		if (!file.file.delete()) {
			log.warn("Failed to delete file {}", file.file.getAbsolutePath());
		}
	}

    private int writeRsToFile(Query query,ReportContext data, final File file) throws SQLException {
    	
    	final int rowCount[] = {0};
    	
		dataProvider.execute(query,data, new ReportDataCallback() {

			public void process(Cursor rs) throws Exception {
				Cursor cursor = new PersistingCursor(file, rs);
				try {
					while (cursor.next()) {
						rowCount[0] = rowCount[0] + 1;
					}
				}
				finally {
					cursor.close();
				}
			}
		});
		
		return rowCount[0];
	}

	private File createFile() {
		try {
			return IOUtils.createTempFile("cursor");
		} catch (IOException e) {
			throw new ReportException("Failed to create temp file for reusable cursor",e);
		}
	}
}
