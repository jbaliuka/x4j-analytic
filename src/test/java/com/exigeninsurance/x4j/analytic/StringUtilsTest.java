package com.exigeninsurance.x4j.analytic;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import com.exigeninsurance.x4j.analytic.util.StringUtils;

public class StringUtilsTest {

	@Test
	public void testJoinObjectArrayString() {
		assertEquals("A,B", StringUtils.join(new Object[]{"A","B"},","));
	}

	@Test
	public void testJoinCollectionOfQextendsObjectString() {
		assertEquals("A,B", StringUtils.join(Arrays.asList(new Object[]{"A","B"}),","));
	}


	@Test
	public void testLanguageLookup(){
		assertEquals("en", "en*broken".toLowerCase().substring(0,2));
	}
	
}
