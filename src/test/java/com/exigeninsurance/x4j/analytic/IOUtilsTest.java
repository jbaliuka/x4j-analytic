package com.exigeninsurance.x4j.analytic;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.exigeninsurance.x4j.analytic.util.IOUtils;

import junit.framework.TestCase;

public class IOUtilsTest extends TestCase {

	public void testToStringInputStream() throws IOException {
		
		ByteArrayInputStream in = new ByteArrayInputStream("test".getBytes());
		assertEquals("test",IOUtils.toString(in));
	}

	public void testCopy() throws IOException {
		ByteArrayInputStream in = new ByteArrayInputStream("test".getBytes());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		IOUtils.copy(in, out);
		assertEquals("test",new String(out.toByteArray()));
		
		
	}

}
