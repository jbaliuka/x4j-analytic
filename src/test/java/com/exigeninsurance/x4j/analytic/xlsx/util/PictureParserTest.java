/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.util;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import com.exigeninsurance.x4j.analytic.xlsx.transform.Picture;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.PDFHelper;
import com.exigeninsurance.x4j.analytic.xlsx.utils.PictureParser;

public class PictureParserTest {

    @Test
    public void testParsePicture() throws Exception {
        InputStream stream = getClass().getResourceAsStream("/headerPicture.xlsx");
        assertNotNull(stream);

        List<Picture> pictures;
        try {
            XSSFWorkbook book = new XSSFWorkbook(stream);
            pictures = PictureParser.getSheetPictures(book.getSheetAt(0));
        } finally {
            stream.close();
        }

        assertThat(pictures, hasSize(1));
        Picture picture = pictures.get(0);

        assertEquals(0, picture.getFromCol());
        assertEquals(9, picture.getFromRow());
        assertEquals(1905165, picture.getEmuHeight());
        assertEquals(7887384, picture.getEmuWidth());
    }

    @Test
    public void testParseLegacyPictures() throws Exception {
        Map<String,Picture> pictures;
        InputStream stream = getClass().getResourceAsStream("/headerPicture.xlsx");
        assertNotNull(stream);

        try {
            XSSFWorkbook book = new XSSFWorkbook(stream);
            pictures = PictureParser.parseLegacyPicturesFromSheet(book.getSheetAt(0));
        } finally {
            stream.close();
        }

        assertEquals(3, pictures.size());

        assertTrue(pictures.containsKey("CF"));
        assertTrue(pictures.containsKey("LF"));
        assertTrue(pictures.containsKey("RH"));

        Picture leftFooterPicture = pictures.get("LF");
        float expectedWidthAndHeightInCm = 192f * PDFHelper.EMU_PER_POINT;
        assertEquals(expectedWidthAndHeightInCm, leftFooterPicture.getEmuHeight(), 0.1f);
        assertEquals(expectedWidthAndHeightInCm, leftFooterPicture.getEmuWidth(), 0.1f);
    }
}
