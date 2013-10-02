/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.TestCase;

import org.junit.Assert;

public class UTF8OutputStreamTest extends TestCase {

	public void testWriteChar() throws IOException {
        String testdata = "\u0000ABCD\u0002\u007F\u120C";
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		UTF8OutputStream utf = new UTF8OutputStream(out);

		utf.writeUTF(testdata);
		Assert.assertEquals(testdata, new String(out.toByteArray(), "UTF8"));

		for(int i = 0; i < 8; i++){
			testdata = testdata + testdata + "293" + i;
		}

		out = new ByteArrayOutputStream();
		utf = new UTF8OutputStream(out);	
		utf.writeUTF(testdata);
		Assert.assertEquals(testdata, new String(out.toByteArray(), "UTF8"));


	}
	/**
	 * unsupported characters are commented out
	 * @throws IOException
	 */
	public void testEastAsianTable() throws IOException {

		InputStream eastAsianTable = getClass().getResourceAsStream("/EastAsianWidth.txt");
		Assert.assertNotNull(eastAsianTable);

		BufferedReader reader = new BufferedReader(new InputStreamReader(eastAsianTable));

		String line = reader.readLine();
		int lineNo = 0;
		while(line != null){
			lineNo++;			
			if(isComment(line)){
				line = reader.readLine();
				continue;
			}

			String str = line.substring(0,line.indexOf(';'));


			int chr = Integer.parseInt(str, 16); 


			ByteArrayOutputStream out = new ByteArrayOutputStream();
			UTF8OutputStream utf = new UTF8OutputStream(out);		

			String input = new String(new char[]{(char)chr}); 
			utf.writeUTF(input);
			String actual =  new String(out.toByteArray(),"UTF8");


			Assert.assertEquals("Line " + lineNo, input, actual);


			line = reader.readLine();
		}



	}


	private boolean isComment(String line) {
		return line.trim().startsWith("#") || line.trim().isEmpty();
	}

}
