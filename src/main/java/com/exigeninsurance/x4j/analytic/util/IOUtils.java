/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/

package com.exigeninsurance.x4j.analytic.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class IOUtils {

	private static final String OPREPORTS = "opreports";
	private static final int BUFFER_SIZE = 1024*80;
	private static final Logger log = LoggerFactory.getLogger(IOUtils.class);

	public static void delete(File temtFile) {
		if(!temtFile.delete()){
			log.warn("unable to delete " + temtFile);
		}
	}

	public static String toString(File file) throws IOException {

		FileInputStream in = new FileInputStream(file);
		try{
			return toString(in);
		}finally{
			in.close();
		}

	}

	public static String toString(InputStream in) throws IOException {
		byte [] buffer = new byte[BUFFER_SIZE];
		StringBuilder stringBuffer = new StringBuilder();
		int len = in.read(buffer);
		while( len > 0 ){
			stringBuffer.append(new String(buffer,0,len,"UTF8"));
			len = in.read(buffer);
		}

		return stringBuffer.toString();
	}


	public static void copy(InputStream in,OutputStream out) throws IOException {
		byte [] buffer = new byte[BUFFER_SIZE];		
		int len = in.read(buffer);
		while( len  > 0 ){
			out.write(buffer,0,len);
			len = in.read(buffer);
		}
	}


	public static void copy(InputStream in, File file)
			throws IOException {
		FileOutputStream tout = new FileOutputStream(file);
		try{
			IOUtils.copy(in, tout);
		}finally{
			tout.close();
		}
	}

	public static void copy(File file,OutputStream out)
			throws IOException {
		FileInputStream in = new FileInputStream(file);
		try{
			IOUtils.copy(in, out);
		}finally{
			in.close();
		}
	}

	public static void copy(File inFile,File outFile)
			throws IOException {
		FileInputStream in = new FileInputStream(inFile);
		try{
			IOUtils.copy(in, outFile);
		}finally{
			in.close();
		}
	}

	/**
	 *  Creates temporal file
	 * @param prefix
	 * @return
	 * @throws IOException
	 */
	public static File createTempFile(String prefix) throws IOException {
		return File.createTempFile(prefix, OPREPORTS,  new File(getTempDir()));
	}

	public static void clearTempDir(final long within, int max){
		File tmpDir = new File(getTempDir());
		final int maxFiles[] = {max};
		
		tmpDir.listFiles(new FileFilter() {			
			@Override
			public boolean accept(File pathname) {				
				if(maxFiles[0] > 0 && pathname.getName().endsWith(OPREPORTS) && pathname.isFile()){
					if(pathname.lastModified() <= System.currentTimeMillis() - within){
							IOUtils.delete(pathname);
					}
					maxFiles[0]--;
				}
				return false;
			}
		});
	}

	public static String getTempDir() {
		String defaultTmpDir = System.getProperty("java.io.tmpdir");
		if(defaultTmpDir != null && new File(defaultTmpDir).exists() ){
			return defaultTmpDir;
		}else {			
			return System.getProperty("user.home");
		}
	}
	
	public static byte[] toByteArray(InputStream is) throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		copy(is, os);
		return os.toByteArray();
	}
}
