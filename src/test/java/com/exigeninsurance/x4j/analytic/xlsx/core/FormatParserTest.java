/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.core;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.exigeninsurance.x4j.analytic.model.Attribute;
import com.exigeninsurance.x4j.analytic.model.ReportMetadata;
import com.exigeninsurance.x4j.analytic.xlsx.core.localization.CsvCurrencyFormatLine;
import com.exigeninsurance.x4j.analytic.xlsx.core.localization.CsvLine;
import com.exigeninsurance.x4j.analytic.xlsx.core.localization.FormatParser;
import com.exigeninsurance.x4j.analytic.xlsx.core.localization.FormatType;

public class FormatParserTest {
	
	private static final String CURRENCY_FORMATS = "/currencyFormats.csv";
	
	@Test
	public void testParsing() throws Exception {
		String formatsString = parseFormatsFromFile(CURRENCY_FORMATS);
		FormatParser parser = new FormatParser();
		ReportMetadata metadata = new ReportMetadata();
		List<Attribute> attributes = new ArrayList<Attribute>();
		Attribute attribute = new Attribute();
		attribute.setName("currency-formats");
		attribute.setValue(formatsString);
		attributes.add(attribute);
		metadata.setAttribute(attributes );
		
		List<CsvLine> formats = parser.parseFormatsFromMetadata(metadata, FormatType.CURRENCY);
		
		int actualSize = formats.size();
		int expectedSize = 8;
		
		assertEquals(expectedSize, actualSize);
		
		CsvCurrencyFormatLine format = (CsvCurrencyFormatLine) formats.get(0);
		
		assertEquals("DEFAULT", format.getCurrencyCd());
		assertEquals("DEFAULT", format.getCountryCd());
		assertEquals("#,##0.00", format.getExcelDataFormat());
		assertEquals("#,##0.00", format.getDecimalDataPattern());
	}
	
	private String parseFormatsFromFile(String fileName) throws IOException {
		StringBuilder builder = new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(fileName)));
		try {
			String strLine = br.readLine();
			while (strLine != null) {
				builder.append(strLine);
				builder.append("\n");
				strLine = br.readLine();
			}
			
		} finally {
			br.close();
		}
		return builder.toString();
	}
}
