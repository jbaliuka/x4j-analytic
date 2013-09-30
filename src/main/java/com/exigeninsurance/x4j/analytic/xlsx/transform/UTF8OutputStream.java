/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform;

import java.io.IOException;
import java.io.OutputStream;
/**
 * UTF16 to UTF8 encoding converter implementation for performance optimizations, 
 * supplementary characters are not supported.
 * No string copy and garbage memory is created by write methods, buffered
 * stream wrapper should be used for buffering
 *  
 * @see java.io.BufferedOutputStream   
 * @author jbaliuka
 *
 */

public final class UTF8OutputStream extends OutputStream {

	private final OutputStream out;

	/**
	 * Wrap  output stream for UTF8 output 
	 * @param out
	 */
	public UTF8OutputStream(OutputStream out) {
		this.out = out;
	}

	/**
	 * Write string to stream using UTF8 encoding
	 * @param str
	 * @throws IOException
	 */

	public void writeUTF(String str) throws IOException {

		writeUTF(str,0,str.length());

	}

	/**
	 * 
	 * Write len characters starting from off to stream as UTF8
	 * 
	 * @param str
	 * @param off
	 * @param len
	 * @throws IOException
	 */

	public void writeUTF(String str,int off, int len) throws IOException {
		for (int i = off;i < off + len; i++){			
			writeUTF( str.charAt(i) );			
		}

	}

	/**
	 *  Write single character as UTF8
	 * @param c UTF16 character (internal JAVA character representation)
	 * @throws IOException
	 */
	public void writeUTF(char c) throws IOException {

		if ((c >= 0x0000) && (c <= 0x007F)) {
			write( (byte) c);
		} else if (c > 0x07FF) {
			write( (byte) (0xE0 | ((c >> 12) & 0x0F)));
			write( (byte) (0x80 | ((c >>  6) & 0x3F)));
			write( (byte) (0x80 | ((c >>  0) & 0x3F)));
		} else {
			write( (byte) (0xC0 | ((c >>  6) & 0x1F)));
			write( (byte) (0x80 | ((c >>  0) & 0x3F)));
		}
	}
	
	@Override
	public void write(byte[] cbuf, int off, int len) throws IOException {

		out.write(cbuf,off,len);
	}

	@Override
	public void write(byte[] cbuf) throws IOException {

		out.write(cbuf);
	}
	
	
	
	@Override
	public void write(int b) throws IOException {

		out.write((byte)b);
	}

	@Override
	public void flush() throws IOException {

		out.flush();

	}

	@Override
	public void close() throws IOException {
		flush();
		out.close();
	}

	

}
