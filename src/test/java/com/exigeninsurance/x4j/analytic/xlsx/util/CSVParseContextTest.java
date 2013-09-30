/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.util;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.exigeninsurance.x4j.analytic.xlsx.utils.CSVParseContext;

public class CSVParseContextTest {

	@Test
	public void testQuoted() {

		CSVParseContext context = new CSVParseContext(',');
		List<String> list = context.parse("\"ABC\"");		
		assertEquals(Collections.singletonList("ABC"), list);

	}

	@Test
	public void testEscaped() {

		CSVParseContext context = new CSVParseContext(',');
		List<String> list = context.parse("\"A\"\"C\"");		
		assertEquals(Collections.singletonList("A\"C"), list);

	}

	@Test
	public void testSeparated() {

		CSVParseContext context = new CSVParseContext(',');
		List<String> list = context.parse("A,B");		
		assertEquals(Arrays.asList("A","B"), list);

	}

	@Test
	public void testSeparatedQuated() {

		CSVParseContext context = new CSVParseContext(',');
		List<String> list = context.parse("A,\"B\"");		
		assertEquals(Arrays.asList("A","B"), list);

	}

	@Test
	public void testEmptyTail() {

		CSVParseContext context = new CSVParseContext(',');
		List<String> list = context.parse("A,");		
		assertEquals(Arrays.asList("A",""), list);

	}

	@Test
	public void testEmptyHead() {		
		CSVParseContext context = new CSVParseContext(',');
		List<String> list = context.parse(",B");		
		assertEquals(Arrays.asList("","B"), list);

	}


	@Test
	public void testEscapedSeparator() {		
		CSVParseContext context = new CSVParseContext(',');
		List<String> list = context.parse("\"A,B\"");		
		assertEquals(Arrays.asList("A,B"), list);

	}

	@Test
	public void testEmppty() {		
		CSVParseContext context = new CSVParseContext(',');
		List<String> list = context.parse("");		
		assertEquals(Arrays.asList(""), list);

	}

	@Test
	public void testDefaults(){

		String[] data={
				"\"DEFAULT\", \"DEFAULT\",\"#,##0.00\", \"#,##0.00\"", 
				"\"USD\", \"DEFAULT\",\"[$$-409]#,##0.00\", \"$ #,##0.00\"",
				"\"USD\", \"US\",\"[$$-409]#,##0.00\", \"$ #,##0.00\"",
				"\"USD\", \"LT\",\"[$$-409]#,##0.00\", \"$ #,##0.00\"",
				"\"USD\", \"JP\",\"#,##0.00\\ [$USD]\", \"#,##0.00 USD\"",
				"\"BRL\", \"DEFAULT\", \"#,##0.00\\ [$BRL]\", \"#,##0.00 BRL\"",
				"\"BRL\", \"US\", \"#,##0.00\\ [$BRL]\", \"#,##0.00 BRL\"",
				"\"BRL\", \"US\", \"#,##0.00\\ \"$BRL\"\", \"#,##0.00 BRL\""
		};
		
		for(String test : data){
            assertEquals(test,4, new CSVParseContext(',').parse(test).size());
		}

	}


}
