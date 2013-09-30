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

import org.junit.Before;
import org.junit.Test;

import com.exigeninsurance.x4j.analytic.model.Attribute;
import com.exigeninsurance.x4j.analytic.model.ReportMetadata;
import com.exigeninsurance.x4j.analytic.xlsx.core.localization.FormatProvider;

public class FormatsTest {
	
	private static final String CURRENCY_FORMATS = "/currencyFormats.csv";
	private static final String DATE_FORMATS = "/dateFormats.csv";
	private static final String DATE_TIME_FORMATS = "/dateTimeFormats.csv";
	
	private ReportMetadata metadata;
	
	@Before
	public void parseFiles() throws IOException {
		String currencies = parseFormatsFromFile(CURRENCY_FORMATS);
		String dates = parseFormatsFromFile(DATE_FORMATS);
		List<Attribute> attributes = new ArrayList<Attribute>();
		String dateTimes = parseFormatsFromFile(DATE_TIME_FORMATS);
		Attribute currencyAttribute = new Attribute();
		currencyAttribute.setName("currency-formats");
		currencyAttribute.setValue(currencies);
		Attribute dateAttribute = new Attribute();
		dateAttribute.setName("date-formats");
		dateAttribute.setValue(dates);
		Attribute dateTimeAttribute = new Attribute();
		dateTimeAttribute.setName("date-time-formats");
		dateTimeAttribute.setValue(dateTimes);
		
		attributes.add(dateAttribute);
		attributes.add(currencyAttribute);
		attributes.add(dateTimeAttribute);
		
		metadata = new ReportMetadata();
		metadata.setAttribute(attributes);
	}
	
	@Test
	public void testCurrencyFormats() {
		FormatProvider provider = new FormatProvider(metadata);
		
		String actualExcelFormat = provider.getExcelCurrencyFormat("USD", "US");
		String expectedExcelFormat = "[$$-409]#,##0.00";
		assertEquals(expectedExcelFormat, actualExcelFormat);
		
		String actualDecimalFormat = provider.getDecimalCurrencyFormat("USD", "US");
		String expectedDecimalFormat = "$ #,##0.00";
		assertEquals(expectedDecimalFormat, actualDecimalFormat);
	}
	
	@Test
	public void testCurrencyFormatsWithoutCountry() {
		FormatProvider provider = new FormatProvider(metadata);
		
		String actualExcelFormat = provider.getExcelCurrencyFormat("USD");
		String expectedExcelFormat = "[$$-409]#,##0.00";
		assertEquals(expectedExcelFormat, actualExcelFormat);
		
		String actualDecimalFormat = provider.getDecimalCurrencyFormat("USD");
		String expectedDecimalFormat = "$ #,##0.00";
		assertEquals(expectedDecimalFormat, actualDecimalFormat);
		
	}
	
	@Test
	public void testNonExistantCurrencyFormats() {
		FormatProvider provider = new FormatProvider(metadata);
		
		String actualExcelFormat = provider.getExcelCurrencyFormat("LT");
		String expectedExcelFormat = "#,##0.00";
		assertEquals(expectedExcelFormat, actualExcelFormat);
		
		String actualDecimalFormat = provider.getDecimalCurrencyFormat("LT");
		String expectedDecimalFormat = "#,##0.00";
		assertEquals(expectedDecimalFormat, actualDecimalFormat);
	}
	
	@Test
	public void testDateTimeFormats() {
		FormatProvider provider = new FormatProvider(metadata);

		assertEquals( "mm/dd/yyyy h:mm AM/PM", provider.getExcelDateTimeFormat("US"));
		assertEquals( "MM/dd/yyyy h:mm a", provider.getDateTimeFormat("US"));
	}

	@Test
	public void testNonExistantDateTimeFormat() {
		FormatProvider provider = new FormatProvider(metadata);

		assertEquals(  "mm/dd/yyyy h:mm AM/PM", provider.getExcelDateTimeFormat("LT"));
		assertEquals( "MM/dd/yyyy h:mm a", provider.getDateTimeFormat("LT"));
	}	
	
	@Test
	public void testDateFormats() {
		FormatProvider provider = new FormatProvider(metadata);
		
		assertEquals("mm\\/dd\\/yyyy", provider.getExcelDateFormat("US"));
		assertEquals("MM/dd/yyyy", provider.getDateFormat("US"));
	}
	
	@Test
	public void testNonExistantDateFormat() {
		FormatProvider provider = new FormatProvider(metadata);
		
		assertEquals("mm\\/dd\\/yyyy", provider.getExcelDateFormat("LT"));
		assertEquals("MM/dd/yyyy", provider.getDateFormat("LT"));
	}
	
	@Test
	public void testLocalizationFallback() {
		metadata.setAttribute(new ArrayList<Attribute>());
		FormatProvider provider = new FormatProvider(metadata);
		
		assertEquals("mm\\/dd\\/yyyy", provider.getExcelDateFormat("US"));
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
