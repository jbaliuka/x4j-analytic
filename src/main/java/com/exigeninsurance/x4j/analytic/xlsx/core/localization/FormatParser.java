/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.core.localization;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.exigeninsurance.x4j.analytic.model.Attribute;
import com.exigeninsurance.x4j.analytic.model.ReportMetadata;
import com.exigeninsurance.x4j.analytic.xlsx.utils.CSVParseContext;

/**
 * 
 * @author astanzys
 *
 */


public class FormatParser {
	
	public List<CsvLine> parseFormatsFromMetadata(ReportMetadata metadata, FormatType formatType) throws IOException {
	
		List<CsvLine> formatList = new ArrayList<CsvLine>();
		Attribute attribute = metadata.getAttributeByName(formatType.getFormatAttributeName());
		if (attribute == null) {
			return parseFormatsFromFile(formatType);
		}
		String formats = attribute.getValue();
		String[] lines = formats.split("\n");
		for (String line : lines) {
			if (line.trim().isEmpty()) {
				continue;
			}
			List <String> tokens = new ArrayList<String> ();
			parseLine(line, tokens);
			formatList.add(createFormat(tokens, formatType));
		}


		return formatList;
	}
	
	private List<CsvLine> parseFormatsFromFile(FormatType formatType) throws IOException {

		List<CsvLine> formats = new ArrayList<CsvLine>();
		BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(formatType.getMissingMetadataFallbackFile())));

		try {
			String strLine = br.readLine();
			while (strLine  != null) {
				List <String> tokens = new ArrayList<String> ();
				parseLine(strLine, tokens);
				formats.add(createFormat(tokens, formatType));
				strLine = br.readLine();
			}
		} finally {
			br.close();
		}
		return formats;
	}

	private void parseLine(String strLine, List<String> tokens) {		
		
		tokens.addAll( new CSVParseContext(',').parse(strLine));
		
	}
	
	private CsvLine createFormat(List<String> tokens, FormatType formatType) {
		switch (formatType) {
		case CURRENCY:
			return new CsvCurrencyFormatLine(tokens);
		case DATE:
		case DATE_TIME:
			return new CsvDateFormatLine(tokens);
		default:
			throw new IllegalStateException("illegal format");
		}
		
	}
}
