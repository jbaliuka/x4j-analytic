/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.utils;

import java.util.StringTokenizer;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.util.Internal;
import org.apache.poi.xssf.usermodel.XSSFCell;

import com.exigeninsurance.x4j.analytic.xlsx.core.expression.ConcatExpression;
import com.exigeninsurance.x4j.analytic.xlsx.core.expression.ConstantExpression;
import com.exigeninsurance.x4j.analytic.xlsx.core.expression.NoOpExpression;
import com.exigeninsurance.x4j.analytic.xlsx.core.expression.SimpleExpression;
import com.exigeninsurance.x4j.analytic.xlsx.core.expression.XLSXExpression;


@Internal
public final class CellExpressionParser {

	private CellExpressionParser(){}
	
	public static XLSXExpression parse(final XSSFCell cell) {
		if(isExpression(cell)){
			String value = cell.getStringCellValue();
			return parseExpression(value);
		}else {
			return new NoOpExpression(cell);
		}
	}

	public static XLSXExpression parseExpression(String value) {
		try {
			StringTokenizer tokens = new StringTokenizer(value.trim(), "${}", true);

			if (!tokens.hasMoreTokens()) {
				return new ConstantExpression(value);
			}

			ConcatExpression concat = new ConcatExpression();
			while (tokens.hasMoreElements()) {
				String token = tokens.nextToken();
				nextToken(tokens, concat, token);

			}
			if (concat.getExpressions().size() == 1) {
				return concat.getExpressions().get(0);
			}
			return concat;
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid expression " + value,e);
		}
	}

	private static void nextToken(StringTokenizer tokens, ConcatExpression concat,
			String token) {
		if("$".equals(token) && tokens.hasMoreTokens()){
			token = tokens.nextToken();
			if("{".equals(token)){				
				token = tokens.nextToken();
				concat.append(new SimpleExpression(token));
				tokens.nextToken();
			}
			else {
				concat.append(new ConstantExpression("$" + token));
			}
		}else {
			concat.append(new ConstantExpression(token));
		}
	}

	public static boolean  isExpression( XSSFCell cell ) {
		if(cell.getCellType() != Cell.CELL_TYPE_STRING){
			return false;
		}
		String value = cell.getStringCellValue();
		if(value == null){
			return false;
		}else {
			return value.contains("${");
		}
	}
}
