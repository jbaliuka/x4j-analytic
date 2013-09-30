/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.util;

import static junit.framework.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STOrientation;

import com.exigeninsurance.x4j.analytic.xlsx.utils.Dimension;
import com.exigeninsurance.x4j.analytic.xlsx.utils.PaperDimensionsProvider;

public class PaperDimensionsProviderTest {

    private PaperDimensionsProvider provider;

    @Before
    public void setUp() throws Exception {
        provider = new PaperDimensionsProvider();
    }

    @Test
    public void testProvider() throws IOException, URISyntaxException {
        FileInputStream fin = new FileInputStream(new File(getClass().getResource("/testDimensions.xlsx").toURI()));
        try {
            Dimension paperDimensions = provider.getPaperDimensions(new XSSFWorkbook(fin).getSheetAt(0));
            assertEquals(215.9, paperDimensions.getWidth(), 0.01);
            Assert.assertEquals(356, paperDimensions.getHeight(), 0.01);
        } finally {
            fin.close();
        }
    }

    @Test
    public void testLandscapeOrientation() throws IOException, URISyntaxException {
        FileInputStream fin = new FileInputStream(new File(getClass().getResource("/testDimensions.xlsx").toURI()));
        try {
            XSSFWorkbook book = new XSSFWorkbook(fin);
            XSSFSheet sheet = book.getSheetAt(0);
            sheet.getCTWorksheet().getPageSetup().setOrientation(STOrientation.Enum.forInt(STOrientation.INT_LANDSCAPE));
            Dimension paperDimensions = provider.getPaperDimensions(sheet);
            Assert.assertEquals(356, paperDimensions.getWidth(), 0.01);
            Assert.assertEquals(215.9, paperDimensions.getHeight(), 0.01);
        } finally {
            fin.close();
        }
    }

}
