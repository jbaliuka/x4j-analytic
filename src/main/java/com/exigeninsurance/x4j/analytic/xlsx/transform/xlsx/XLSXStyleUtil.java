/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Locale;

import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import com.exigeninsurance.x4j.analytic.api.ReportException;
import com.exigeninsurance.x4j.analytic.model.Money;
import com.exigeninsurance.x4j.analytic.xlsx.core.localization.FormatProvider;
import com.exigeninsurance.x4j.analytic.xlsx.transform.AlignmentFinder;


public final class XLSXStyleUtil {

	private final StyleCache styleCache = new StyleCache();
	private final XSSFSheet sheet;
	private FormatProvider formatProvider;
	private Locale locale;
	private AlignmentFinder alignment;

	public XLSXStyleUtil(XSSFSheet sheet, FormatProvider formatProvider, Locale locale, AlignmentFinder alignment) {
		this.sheet = sheet;
		this.formatProvider = formatProvider;
		this.locale = locale;
		this.alignment = alignment;
	}

	public long getLocalizedDateStyle(Object value, XLSXCellNode node) {
		StyleTransformation st = getDateTransform(value);
		return styleCache.containsDateStyle(node) ?
				styleCache.getDateStyle(node) : createNewStyle(st, node, value);
	}

	public long getLocalizedCurrencyStyle(Money money, XLSXCellNode node) {
		StyleTransformation st = new MoneyTransform(money);
		String currencyCd = money.getCurrencyCd();
		return styleCache.containsCurrencyStyle(node, currencyCd) ?
				styleCache.getCurrencyStyle(node, currencyCd) : createNewStyle(st, node, money);
	}

	private StyleTransformation getDateTransform(Object value) {
		if (value instanceof Timestamp) {
			return new DateTimeTransform(locale, (Timestamp) value);
		}
		if (value instanceof Date) {
			return new DateTransform(locale, (Date)value);
		}

		throw new ReportException("Attempted to localize non-date value");
	}

	private long createNewStyle(StyleTransformation st, XLSXCellNode node, Object value) {
		return node.hasStyleSet() ?
				cacheAndPersistStyle(value, node, createStyleFromOriginal(st, node)) :
				cacheAndPersistStyle(value, node, createStyle(st));
	}

	private long cacheAndPersistStyle(Object value, XLSXCellNode node, XSSFCellStyle cellStyle) {
		long id = (long) sheet.getWorkbook().getStylesSource().putStyle(cellStyle);
		styleCache.putStyle(value, node, id);
		return id;
	}

	private XSSFCellStyle createStyleFromOriginal(StyleTransformation st, XLSXCellNode node) {
		XSSFCellStyle originalCellStyle = sheet.getWorkbook().getCellStyleAt((short) node.getStyle());
		XSSFCellStyle cellStyle = sheet.getWorkbook().getStylesSource().createCellStyle();
		cellStyle.cloneStyleFrom(originalCellStyle);
		st.transform(cellStyle);
		return cellStyle;
	}

	private XSSFCellStyle createStyle(StyleTransformation st) {
		XSSFCellStyle cellStyle = sheet.getWorkbook().getStylesSource().createCellStyle();
		st.transform(cellStyle);
		return cellStyle;
	}

	private String getExcelCurrencyFormat(String currencyCd) {
		return formatProvider.getExcelCurrencyFormat(currencyCd, locale.getCountry());
	}

	private interface StyleTransformation {
		void transform(XSSFCellStyle cellStyle);
	}

	private class MoneyTransform implements StyleTransformation {

		private Money money;

		private MoneyTransform(Money money) {
			this.money = money;
		}

		@Override
		public void transform(XSSFCellStyle cellStyle) {
			XSSFDataFormat format = sheet.getWorkbook().getCreationHelper().createDataFormat();
			cellStyle.setAlignment(alignment.excelAlignment(money, cellStyle));
			cellStyle.setDataFormat(format.getFormat(getExcelCurrencyFormat(money.getCurrencyCd())));
		}
	}

	private class DateTransform implements StyleTransformation {
		private Locale locale;
		private Date date;

		public DateTransform(Locale locale, Date date) {
			this.locale = locale;
			this.date = date;
		}

		@Override
		public void transform(XSSFCellStyle cellStyle) {
			XSSFDataFormat format = sheet.getWorkbook().getCreationHelper().createDataFormat();
			cellStyle.setAlignment(alignment.excelAlignment(date, cellStyle));
			cellStyle.setDataFormat(format.getFormat(formatProvider.getExcelDateFormat(locale.getCountry())));
		}
	}

	private class DateTimeTransform implements StyleTransformation {
		private final Locale locale;
		private final Timestamp timestamp;

		public DateTimeTransform(Locale locale, Timestamp timestamp) {
			this.locale = locale;
			this.timestamp = timestamp;
		}

		@Override
		public void transform(XSSFCellStyle cellStyle) {
			XSSFDataFormat format = sheet.getWorkbook().getCreationHelper().createDataFormat();
			cellStyle.setAlignment(alignment.excelAlignment(timestamp, cellStyle));
			cellStyle.setDataFormat(format.getFormat(formatProvider.getExcelDateTimeFormat(locale.getCountry())));
		}
	}
}
