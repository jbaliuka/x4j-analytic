/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/

package com.exigeninsurance.x4j.analytic.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import org.slf4j.Logger;

import com.exigeninsurance.x4j.analytic.api.Cursor;
import com.exigeninsurance.x4j.analytic.api.ReportException;


public class FileCursor implements Cursor {

	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(FileCursor.class);

	private CursorMetadata metadata;
	private FileInputStream fileIn;
	private ObjectInputStream objectIn;
	private File file;

	private Object[] currentRow;

	public FileCursor(File file) {
		this.file = file;
		init();
	}

	private void init() {
		try {
			initStreams();
			metadata = readMetadata();
			currentRow = new Object[metadata.getColumnCount()];
		} catch (Exception e) {
			close();
			throw new ReportException(e);
		}
	}

	private void initStreams() throws IOException {
		fileIn = new FileInputStream(file);
		objectIn = new ObjectInputStream(fileIn);
	}

	private CursorMetadata readMetadata() throws ClassNotFoundException, IOException {
		return (CursorMetadata) objectIn.readObject();
	}

	@Override
	public void close() {
		try {
			if (objectIn != null) {
				objectIn.close();
			}
			if (fileIn != null) {
				fileIn.close();
			}
			fileIn = null;
		} catch (IOException e) {
			logger.warn("unable to close stream",e);
		}
	}

	@Override
	public boolean next()  {
		try {		
			boolean hasNext = objectIn.available() > 0;
			if (hasNext) {
				try {
					readRow();
				} catch (ClassNotFoundException e) {
					throw new ReportException(e);
				}
			}
			return hasNext;
		} catch (IOException e) {
			throw new ReportException(e);
		}
	}

	private void readRow() throws IOException, ClassNotFoundException {
		
			currentRow = metadata.readRow(objectIn);
		
	}


	@Override
	public CursorMetadata getMetadata() {
		return metadata;
	}

	@Override
	public Object getObject(int i) {
		return currentRow[i - 1];
	}

	@Override
	public void reset() {
		close();
		init();
	}



	@Override
	public boolean isClosed() {

		return fileIn == null;
	}
}
