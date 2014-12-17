/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.utils;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.poi.util.Internal;

import com.exigeninsurance.x4j.analytic.model.Money;
import com.exigeninsurance.x4j.analytic.xlsx.core.localization.FormatProvider;

@Internal
public class FormatUtil {

	private final FormatProvider formatProvider;
	private Locale locale;

	private DateFormat defaultDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private DateFormat dateTimeFormat;

	public FormatUtil(FormatProvider formatProvider, Locale locale) {
		this.formatProvider = formatProvider;
		this.locale = locale;
		initDateFormat(formatProvider);
		initDateTimeFormat(formatProvider);
	}

	private void initDateFormat(FormatProvider formatProvider) {
		String dateFormat = formatProvider.getDateFormat(locale.getCountry());
		defaultDateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, locale);
		if (defaultDateFormat instanceof SimpleDateFormat) {
			((SimpleDateFormat) defaultDateFormat).applyPattern(dateFormat);
		}
	}

	private void initDateTimeFormat(FormatProvider formatProvider) {
		String dateTFormat = formatProvider.getDateTimeFormat(locale.getCountry());
		dateTimeFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, locale);
		if (dateTimeFormat instanceof SimpleDateFormat) {
			((SimpleDateFormat) dateTimeFormat).applyPattern(dateTFormat);
		}
	}

	public String defaultDateFormat(Date value) {
		return defaultDateFormat.format(value);
	}

	public String dateTimeFormat(Date value) {
		return dateTimeFormat.format(value);
	}

	public String formatMoney(Money money) {
		NumberFormat nf = NumberFormat.getInstance(locale);
		if (nf instanceof DecimalFormat) {
			String pattern = getDecimalCurrencyFormat(money.getCurrencyCd());	
			((DecimalFormat) nf).applyPattern(pattern);
		}
		return nf.format(money.getValue().doubleValue());
	}

	private String getDecimalCurrencyFormat(String currencyCd) {
		return formatProvider.getDecimalCurrencyFormat(currencyCd, locale.getCountry());
	}

	
}
