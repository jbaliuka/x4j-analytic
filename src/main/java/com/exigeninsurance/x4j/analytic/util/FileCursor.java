/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/

package com.exigeninsurance.x4j.analytic.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.slf4j.Logger;

import com.exigeninsurance.x4j.analytic.api.Cursor;
import com.exigeninsurance.x4j.analytic.api.ReportException;
import org.slf4j.LoggerFactory;


public class FileCursor implements Cursor {

	private static final Logger logger = LoggerFactory.getLogger(FileCursor.class);

	private CursorMetadata metadata;
	private InputStream fileIn;
	private ObjectInputStream objectIn;
	private File file;

	private Object[] currentRow;

	private int rowCount;
	private int currentRowNumber = 0;

	public FileCursor(File file, int rowCount) {
		this.file = file;
		this.rowCount = rowCount;
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
		fileIn = new GZIPInputStream(new FileInputStream(file));
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
			currentRowNumber = 0;
		} catch (IOException e) {
			logger.warn("unable to close stream",e);
		}
	}

	@Override
	public boolean next()  {
		try {		
			boolean hasNext = currentRowNumber < rowCount;
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
			currentRowNumber ++;
		
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
