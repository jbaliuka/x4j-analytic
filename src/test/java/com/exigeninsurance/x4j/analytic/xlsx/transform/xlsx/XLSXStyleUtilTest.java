
/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/

package com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Locale;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.exigeninsurance.x4j.analytic.model.Money;
import com.exigeninsurance.x4j.analytic.xlsx.core.expression.SimpleExpression;
import com.exigeninsurance.x4j.analytic.xlsx.core.localization.FormatProvider;
import com.exigeninsurance.x4j.analytic.xlsx.transform.AlignmentFinder;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLSXCellNode;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLSXStyleUtil;

public class XLSXStyleUtilTest {

	private static final Money USD = new Money("USD", new BigDecimal("1.00"));
	private static final Money USD2 = new Money("USD", new BigDecimal("2.00"));
	private static final Money BRL = new Money("BRL", new BigDecimal("2.00"));

	@Mock private FormatProvider formatProvider;
	@Mock private AlignmentFinder alignmentFinder;

	private XSSFWorkbook wb;
	private XSSFSheet sheet;
	private XLSXStyleUtil util;

	private XSSFCell cell;
	private XSSFCell cell2;

	private XLSXCellNode node;

	@Before
	public void setup() {
		initMocks(this);
		wb = new XSSFWorkbook();
		sheet = wb.createSheet();
		util = new XLSXStyleUtil(sheet, formatProvider, Locale.US, alignmentFinder);
		when(formatProvider.getExcelDateFormat("US")).thenReturn("dateFormat");
		when(formatProvider.getExcelDateTimeFormat("US")).thenReturn("dateTimeFormat");
		when(formatProvider.getExcelCurrencyFormat(anyString(), anyString())).thenAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
				Object currency = invocationOnMock.getArguments()[0];
				return currency;
			}
		});

		cell = sheet.createRow(0).createCell(0);
		cell2 = sheet.createRow(1).createCell(1);
	}

	@Test
	public void dateValuesAreLocalized() {
		initNode(cell);
		long newStyle = util.getLocalizedDateStyle(new Date(), node);
		assertDataFormatEqualTo((short) newStyle, "dateFormat");
	}

	@Test
	public void allDatesForTheSameNode_useSameCellStyle() {
		initNode(cell);
		long newStyle = util.getLocalizedDateStyle(new Date(), node);
		assertThat(util.getLocalizedDateStyle(new Date(), node), equalTo(newStyle));
	}

	@Test
	public void dateStylingIsCopiedOver() {
		wrapText(cell);
		initNode(cell);
		assertTextIsWrapped((short) util.getLocalizedDateStyle(new Date(), node));
	}

	@Test
	public void currencyFormatIsApplied() {
		initNode(cell);
		assertDataFormatEqualTo(util.getLocalizedCurrencyStyle(USD, node), "USD");
	}

	@Test
	public void currencyStylingIsCopiedOver() {
		wrapText(cell);
		initNode(cell);
		assertTextIsWrapped((short) util.getLocalizedCurrencyStyle(USD, node));
	}

	@Test
	public void consecutiveCurrenciesUseSameStyle_ifItsTheSameCurrency() {
		initNode(cell);
		long newStyle = util.getLocalizedCurrencyStyle(USD, node);
		assertThat(util.getLocalizedCurrencyStyle(USD2, node), equalTo(newStyle));
	}

	@Test
	public void dateStyleIsntAppliedToCurrency() {
		initNode(cell);
		long dateStyle = util.getLocalizedDateStyle(new Date(), node);
		initNode(cell2);
		long currencyStyle = util.getLocalizedCurrencyStyle(USD, node);
		assertThat(dateStyle, not(equalTo(currencyStyle)));
		assertDataFormatEqualTo(currencyStyle, "USD");
	}

	@Test
	public void consecutiveCurrenciesUseDifferentStyles_ifCurrenciesAreDifferent() {
		initNode(cell);
		long usdStyle = util.getLocalizedCurrencyStyle(USD, node);
		long brlStyle = util.getLocalizedCurrencyStyle(BRL, node);
		assertThat(usdStyle, not(equalTo(brlStyle)));
		assertDataFormatEqualTo(brlStyle, "BRL");
	}

	@Test
	public void testDateTimeFormat() {
		initNode(cell);
		long dateTimeStyle = util.getLocalizedDateStyle(new Timestamp(System.currentTimeMillis()), node);
		assertDataFormatEqualTo(dateTimeStyle, "dateTimeFormat");
	}

	private void assertTextIsWrapped(short newStyle) {
		XSSFCellStyle cellStyle = wb.getCellStyleAt(newStyle);
		assertThat(cellStyle.getWrapText(), is(true));
	}

	private void wrapText(XSSFCell cell) {
		CellStyle wrapped = sheet.getWorkbook().getStylesSource().createCellStyle();
		wrapped.setWrapText(true);
		cell.setCellStyle(wrapped);
	}

	private void assertDataFormatEqualTo(long newStyle, String dataFormat) {
		XSSFCellStyle cellStyle = wb.getCellStyleAt((short) newStyle);
		assertThat(cellStyle.getDataFormatString(), equalTo(dataFormat));
	}

	private void initNode(XSSFCell cell) {
		node = new XLSXCellNode(sheet, cell, 0, new SimpleExpression("ignored"));
	}
}
