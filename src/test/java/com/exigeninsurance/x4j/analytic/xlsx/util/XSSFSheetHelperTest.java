/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.util;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import com.exigeninsurance.x4j.analytic.xlsx.utils.XSSFSheetHelper;

public class XSSFSheetHelperTest {

    @Test
    public void testParseSingleRow() throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        workbook.createSheet();
        workbook.setRepeatingRowsAndColumns(0, -1, -1, 0, 0);
        int[] repeatingRows = XSSFSheetHelper.getRepeatingRows(workbook, 0);
        assertEquals(2, repeatingRows.length);
        assertEquals(0, repeatingRows[0]);
        assertEquals(0, repeatingRows[1]);
    }

    @Test
    public void testParseMultipleRows() {
        XSSFWorkbook workbook = new XSSFWorkbook();
        workbook.createSheet();
        workbook.setRepeatingRowsAndColumns(0, -1, -1, 0, 3);
        int[] repeatingRows = XSSFSheetHelper.getRepeatingRows(workbook, 0);
        assertEquals(4, repeatingRows.length);
        for (int i = 0; i < repeatingRows.length; i++) {
            assertEquals(i, repeatingRows[i]);
        }
    }

    @Test
    public void testParseMultipleRowsMultipleSheets() {
        XSSFWorkbook workbook = new XSSFWorkbook();
        workbook.createSheet();
        workbook.createSheet();
        workbook.createSheet();
        workbook.setRepeatingRowsAndColumns(0, -1, -1, 0, 3);
        workbook.setRepeatingRowsAndColumns(2, -1, -1, 2, 3);

        int[] repeatingRows = XSSFSheetHelper.getRepeatingRows(workbook, 0);
        assertEquals(4, repeatingRows.length);
        for (int i = 0; i < repeatingRows.length; i++) {
            assertEquals(i, repeatingRows[i]);
        }

        repeatingRows = XSSFSheetHelper.getRepeatingRows(workbook, 1);
        assertEquals(0, repeatingRows.length);

        repeatingRows = XSSFSheetHelper.getRepeatingRows(workbook, 2);
        assertEquals(2, repeatingRows.length);
        assertEquals(2, repeatingRows[0]);
        assertEquals(3, repeatingRows[1]);
    }
    
    @Test
    public void whenBothRowsAndColsAreRepeated_colsAreIgnored() {
        XSSFWorkbook workbook = new XSSFWorkbook();
        workbook.createSheet();
        workbook.setRepeatingRowsAndColumns(0, 2, 2, 1, 1);

        int[] repeatingRows = XSSFSheetHelper.getRepeatingRows(workbook, 0);
        assertEquals(2, repeatingRows.length);
        assertEquals(1, repeatingRows[0]);
        assertEquals(1, repeatingRows[1]);
    }

    @Test
    public void rowIsInPrintArea() {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet();
        workbook.setPrintArea(0, 0, 10, 0, 10);

        assertThat(XSSFSheetHelper.rowIsInPrintArea(sheet, 0), is(true));
        assertThat(XSSFSheetHelper.rowIsInPrintArea(sheet, 5), is(true));
        assertThat(XSSFSheetHelper.rowIsInPrintArea(sheet, 10), is(true));
        assertThat(XSSFSheetHelper.rowIsInPrintArea(sheet, 11), is(false));
    }
}
