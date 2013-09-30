/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.core.localization;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.exigeninsurance.x4j.analytic.api.ReportException;
import com.exigeninsurance.x4j.analytic.model.ReportMetadata;

/**
 * 
 * @author astanzys
 *
 */


public class FormatProvider {
	
	private static final String DEFAULT = "DEFAULT";
	
	private final List<CurrencyTreeNode> nodes;
	private final Map<String, DatesFormat> dateFormats;
	private final Map<String, DatesFormat> dateTimeFormats;
	
	public FormatProvider(ReportMetadata metadata) {
		nodes = new ArrayList<CurrencyTreeNode>();
		dateFormats = new HashMap<String, DatesFormat>();
		dateTimeFormats = new HashMap<String, DatesFormat>();
		parseCurrencyFormats(metadata);
		parseDateFormats(metadata);
		parseDateTimeFormats(metadata);
	}
	
	private void parseCurrencyFormats(ReportMetadata metadata) {
		FormatParser parser = new FormatParser();
		List <CsvLine> csvLines;
		try {
			csvLines = parser.parseFormatsFromMetadata(metadata, FormatType.CURRENCY);
		} catch (IOException e) {
			throw new ReportException(e);
		}
		createNodes(csvLines);
	}
	
	private void parseDateFormats(ReportMetadata metadata) {
		FormatParser parser = new FormatParser();
		List <CsvLine> csvLines;
		try {
			csvLines = parser.parseFormatsFromMetadata(metadata, FormatType.DATE);
		} catch (IOException e) {
			throw new ReportException(e);
		}
		
		for (CsvLine csvLine : csvLines) {
			dateFormats.put(csvLine.getCountryCd(), (DatesFormat) csvLine.valueOf());
		}
	}
	private void parseDateTimeFormats(ReportMetadata metadata) {
		FormatParser parser = new FormatParser();
		List <CsvLine> csvLines;
		try {
			csvLines = parser.parseFormatsFromMetadata(metadata, FormatType.DATE_TIME);
		} catch (IOException e) {
			throw new ReportException(e);
		}

		for (CsvLine csvLine : csvLines) {
			dateTimeFormats.put(csvLine.getCountryCd(), (DatesFormat) csvLine.valueOf());
		}
	}
	private void createNodes(List<CsvLine> lines) {
		List <String> distinctCurrencies = getDistintCurrencies(lines);
		for (String currencyCd : distinctCurrencies) {
			nodes.add(new CurrencyTreeNode(currencyCd));
		}
		
		for (CsvLine line : lines) {
			CsvCurrencyFormatLine currencyLine = (CsvCurrencyFormatLine)line;
			CurrencyTreeNode node = getNode(currencyLine.getCurrencyCd());
			if (node != null) {
				node.getFormats().add((CurrencyFormat) currencyLine.valueOf());
			}
			else {
				throw new IllegalStateException();
			}
		}
	}
	
	private List<String> getDistintCurrencies(List<CsvLine> lines) {
		List<String> distinctCurrencies = new ArrayList<String>();
		for (CsvLine line : lines) {
			String currencyCd = ((CsvCurrencyFormatLine)line).getCurrencyCd();
			if (!distinctCurrencies.contains(currencyCd)) {
				distinctCurrencies.add(currencyCd);
			}
		}
		return distinctCurrencies;
	}
	
	private CurrencyTreeNode getNode(String currencyCd) {
		for (CurrencyTreeNode node : nodes) {
			if (node.getCurrencyCd().equals(currencyCd)) {
				return node;
			}
		}
		return null;
	}
	
	private CurrencyFormat getCurrencyFormat(String currencyCd, String countryCd) {
		
		CurrencyTreeNode node;
		if (getNode(currencyCd) != null) {
			node = getNode(currencyCd);
		}
		else {
			node = getNode(DEFAULT);
		}
		if (node.getCurrencyFormat(countryCd) != null) {
			return node.getCurrencyFormat(countryCd);
		}
		else {
			return node.getCurrencyFormat(DEFAULT);
		}
	}
	
	public String getExcelCurrencyFormat(String currencyCd, String countryCd) {
		return getCurrencyFormat(currencyCd, countryCd).getExcelFormat();
	}
	
	public String getDecimalCurrencyFormat(String currencyCd, String countryCd) {
		return getCurrencyFormat(currencyCd, countryCd).getDecimalFormat();
	}
	
	public String getExcelCurrencyFormat(String currencyCd) {
		return getCurrencyFormat(currencyCd, DEFAULT).getExcelFormat();
	}
	
	public String getDecimalCurrencyFormat(String currencyCd) {
		return getCurrencyFormat(currencyCd, DEFAULT).getDecimalFormat();
	}

	private DatesFormat getInternalDateFormat(String countryCd, FormatType dateType) {
		Map<String, DatesFormat> formats = 
				dateType == FormatType.DATE ? dateFormats: dateTimeFormats;
		return formats.containsKey(countryCd) ?
				formats.get(countryCd) : formats.get(DEFAULT);
	}
	
	public String getExcelDateFormat(String countryCd) {
		return getInternalDateFormat(countryCd, FormatType.DATE).getExcelDateFormat();
	}
	
	public String getDateFormat(String countryCd) {
		return getInternalDateFormat(countryCd, FormatType.DATE).getDateFormat();
	}
	
	public String getExcelDateTimeFormat(String countryCd) {
		return getInternalDateFormat(countryCd, FormatType.DATE_TIME).getExcelDateFormat();
	}

	public String getDateTimeFormat(String countryCd) {
		return getInternalDateFormat(countryCd, FormatType.DATE_TIME).getDateFormat();
	}
}
