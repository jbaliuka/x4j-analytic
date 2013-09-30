/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.util.Internal;
import org.apache.poi.xssf.usermodel.XSSFName;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDefinedName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDefinedNames;

@Internal
public class XSSFSheetHelper {

    private static final Pattern ROWS_RANGE = Pattern.compile("\\$\\d*:\\$\\d*");
	private static final Pattern COLUMN_RANGE = Pattern.compile("\\$[a-zA-Z]*:\\$[a-zA-Z]*");

    private CTDefinedName name;

    private XSSFSheetHelper(XSSFWorkbook workbook, int sheet) {
        CTDefinedNames definedNames = workbook.getCTWorkbook().getDefinedNames();
        if (definedNames != null) {
            CTDefinedName[] definedNameArray = definedNames.getDefinedNameArray();
            for (CTDefinedName name : definedNameArray) {
                if (name.getName().equals(XSSFName.BUILTIN_PRINT_TITLE) && name.getLocalSheetId() == sheet) {
                    this.name = name;
                }
            }
        }
    }

	/**
	 * @return 0-based repeating row numbers
	 */
	public static int[] getRepeatingRows(XSSFWorkbook book, int sheet) {
		return new XSSFSheetHelper(book, sheet).parseRows();
	}

    private int[] parseRows() {
        if (name == null) {
            return new int[] {};
        }

        String[] rangeValues = parseRowsRange(name.getStringValue()).split(":");
        if (rangeValues.length != 2) {
            throw new IllegalStateException();
        }

        int start = new CellReference(rangeValues[0]).getRow();
        int end = new CellReference(rangeValues[1]).getRow();

        int rangeLength = end - start + 1;
        if (rangeLength == 1) {
            return new int[]{start, start};
        } else {
            return fillMultipleRowArray(start, rangeLength);
        }
    }

    private int[] fillMultipleRowArray(int start, int rangeLength) {
        int[] rows = new int[rangeLength];
        for (int i = 0; i < rangeLength; i++) {
            rows[i] = i + start;
        }
        return rows;
    }

    private String parseRowsRange(String value) {
        Matcher matcher = ROWS_RANGE.matcher(value);
        boolean matches = matcher.find();
        if (!matches) {
            throw new IllegalStateException("Named range contains no row range: " + value);
        }
        return value.substring(matcher.start(), matcher.end());
    }

    public static boolean rowIsInPrintArea(XSSFSheet sheet, int rowNum) {
        CTDefinedNames definedNames = sheet.getWorkbook().getCTWorkbook().getDefinedNames();
        if (definedNames != null) {
            for (CTDefinedName name : definedNames.getDefinedNameArray()) {
                if (nameIsSheetsPrintArea(sheet, name)) {
                    return rowIsInsideDefinedArea(rowNum, name);
                }
            }
        }
        return true;
    }

    private static boolean nameIsSheetsPrintArea(XSSFSheet sheet, CTDefinedName name) {
        return name.getName().equals(XSSFName.BUILTIN_PRINT_AREA) && name.getLocalSheetId() == sheet.getWorkbook().getSheetIndex(sheet);
    }

    private static boolean rowIsInsideDefinedArea(int rowNum, CTDefinedName printArea) {
		if (isWholeColumnReference(printArea)) {
			return true;
		}
        AreaReference areaRef = new AreaReference(printArea.getStringValue());
        int start = areaRef.getFirstCell().getRow();
        int end = areaRef.getLastCell().getRow();

        return start <= rowNum && end >= rowNum;
    }

	private static boolean isWholeColumnReference(CTDefinedName printArea) {
		String ref = printArea.getStringValue();
		return COLUMN_RANGE.matcher(ref).find();
	}
}
