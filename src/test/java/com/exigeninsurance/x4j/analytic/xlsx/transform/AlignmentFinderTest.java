/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;

import com.exigeninsurance.x4j.analytic.api.ReportContext;
import com.exigeninsurance.x4j.analytic.model.Attribute;
import com.exigeninsurance.x4j.analytic.model.Money;
import com.exigeninsurance.x4j.analytic.model.ReportMetadata;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.Alignment;

public class AlignmentFinderTest {

	private XSSFWorkbook wb;
	private ReportMetadata metadata;

	private Money money;
	private Date date;
	private AlignmentFinder finder;
	private ReportContext reportContext;
	private XSSFCellStyle cellStyle;

	@Before
	public void setup() {
		wb = new XSSFWorkbook();
		metadata = new ReportMetadata();
		reportContext = new ReportContext(metadata);
		money = new Money("USD", new BigDecimal(1));
		date = new Date();
		setAlignment(XSSFCellStyle.ALIGN_LEFT);
	}

	@Test
	public void testCellAlignment() {
		finder = new AlignmentFinder(reportContext);
		assertThat(finder.determineAlignment("String", cellStyle), is(Alignment.LEFT));
		setAlignment(XSSFCellStyle.ALIGN_CENTER);
		assertThat(finder.determineAlignment("String", cellStyle), is(Alignment.CENTER));
	}

	@Test
	public void datesAreCentered_unlessOverriden() {
		finder = new AlignmentFinder(reportContext);
		assertThat(finder.determineAlignment(date, cellStyle), is(Alignment.CENTER));
	}

	@Test
	public void currencyIsRightAligned_unlessOverriden() {
		finder = new AlignmentFinder(reportContext);
		assertThat(finder.determineAlignment(money, cellStyle), is(Alignment.RIGHT));
	}

	@Test
	public void datesUseCellAlignment_ifOverriden() {
		disableAutoAlign();
		finder = new AlignmentFinder(reportContext);
		assertThat(finder.determineAlignment(date, cellStyle), is(Alignment.LEFT));
	}

	@Test
	public void currencyUseCellAlignment_ifOverriden() {
		disableAutoAlign();
		finder = new AlignmentFinder(reportContext);
		assertThat(finder.determineAlignment(money, cellStyle), is(Alignment.LEFT));
	}

	private void setAlignment(short alignment) {
		cellStyle = wb.createCellStyle();
		cellStyle.setAlignment(alignment);
	}

	private void disableAutoAlign() {
		List<Attribute> attrs = new ArrayList<Attribute>();
		Attribute dateAlign = new Attribute();
		dateAlign.setName("date-alignment");
		dateAlign.setValue("disabled");
		attrs.add(dateAlign);

		Attribute currencyAlign = new Attribute();
		currencyAlign.setName("currency-alignment");
		currencyAlign.setValue("disabled");
		attrs.add(currencyAlign);

		metadata.setAttribute(attrs);
	}
}
