/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/

package com.exigeninsurance.x4j.analytic.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.zip.GZIPOutputStream;

import com.exigeninsurance.x4j.analytic.api.Cursor;
import com.exigeninsurance.x4j.analytic.api.ReportException;


public class PersistingCursor implements Cursor {
	private File file;
	private OutputStream fileOut;
	private ObjectOutputStream objectOut;
	private Cursor rs;
	private final CursorMetadata metadata;

	public PersistingCursor(File file, Cursor rs) {
		this.file = file;
		this.rs = rs;
		metadata = rs.getMetadata();
		initStreams();
		writeMetadata();
	}

	

	private void initStreams() {
		try {
			fileOut = new GZIPOutputStream( new FileOutputStream(file) );
			objectOut = new ObjectOutputStream(fileOut);
		} catch (Exception e) {
			throw new ReportException(e);
		}
	}

	private void writeMetadata() {
		checkOpen();
		try {
			objectOut.writeObject(metadata);
		} catch (IOException e) {
			close();
			throw new ReportException(e);
		}
	}

	@Override
	public boolean next() {
		boolean next = hasNext();
		if (next) {
			writeRow();
		}            
		return next;
	}



	private boolean hasNext() {
		
			return rs.next();
		
	}

	private void writeRow() {
		checkOpen();    	
		try {
			metadata.writeRow(objectOut,rs);
		} catch (IOException e) {
			throw new ReportException(e);
		} catch (SQLException e) {
			throw new ReportException(e);
		}              

	}

	@Override
	public CursorMetadata getMetadata() {
		return metadata;
	}

	@Override
	public Object getObject(int i) {
		
			return rs.getObject(i);
		
	}

	@Override
	public void reset() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void close() {
		try {
			if (objectOut != null) {
				objectOut.close();
			}
			if (fileOut != null) {
				fileOut.close();
			}
			fileOut = null;
		} catch (IOException e) {
			throw new ReportException(e);
		}
	}

	void checkOpen(){
		if(isClosed()){
			throw new ReportException("Cursor is closed");
		}
	}

	@Override
	public boolean isClosed() {		
		return fileOut == null;
	}
}
