/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.exigeninsurance.x4j.analytic.xlsx.transform.PdfStyle;
import com.exigeninsurance.x4j.analytic.xlsx.transform.PdfStylesTable;
import com.exigeninsurance.x4j.analytic.xlsx.transform.TableStyle;
import com.exigeninsurance.x4j.analytic.xlsx.transform.XLSXFill;
import org.w3c.dom.Node;

public class PdfStylesParserTest {
	
	private static final String TEST_XML = "/tableStyles.xml";	
	private static final int DXFS_SIZE = 434;
	private static final int TABLE_STYLES_SIZE = 60;
	private static final String DEFAULT_TABLE_STYLE = "TableStyleMedium9";
	private static final String NAME = "TableStyleDark10copy";
	private static final Boolean PIVOT = false;
	
	private InputStream stream;
	private PdfStylesParser parser;
	
	@Before
	public void setup() throws IOException {
		stream = getClass().getResourceAsStream(TEST_XML);
	}
	
	@Test
	public void testParseDfxs() throws IOException {
        try {
            parser = new PdfStylesParser(stream);
            List<Node> result = parser.parseDxfs();
            int expectedSize = DXFS_SIZE;
            int actualSize = result.size();
            assertEquals(expectedSize, actualSize);
        } finally {
            stream.close();
        }
	}
	
	@Test
	public void testParseTableStyles() throws IOException {
        try {
            parser = new PdfStylesParser(stream);
            List<Node> result = parser.parseTableStyles();
            int expectedSize = TABLE_STYLES_SIZE;
            int actualSize = result.size();
            assertEquals(expectedSize, actualSize);
        } finally {
            stream.close();
        }
	}
	
	@Test
	public void testParseDefaultTableStyle() throws IOException {
        try {
            parser = new PdfStylesParser(stream);
            String actual = parser.parseDefaultTableStyle();
            String expected = DEFAULT_TABLE_STYLE;
            assertEquals(expected, actual);
        } finally {
            stream.close();
        }
	}
	
	@Test
	public void testProducePdfStyle() throws IOException {
        try {
            parser = new PdfStylesParser(stream);
            List<Node> dxfs = parser.parseDxfs();
            PdfStyle style = parser.produceStyle(dxfs.get(0));

            assertNotNull(style);

            int expectedTheme = 8;
            double expectedTint = 0.59999389629810485;

            int actualTheme = style.getFill().getFgColor().getTheme();
            double actualTint = style.getFill().getFgColor().getTint();

            assertEquals(expectedTheme, actualTheme);
            assertEquals(expectedTint, actualTint, 0.01);

            actualTheme = style.getFill().getBgColor().getTheme();
            actualTint = style.getFill().getBgColor().getTint();

            assertEquals(expectedTheme, actualTheme);
            assertEquals(expectedTint, actualTint, 0.01);

        } finally {
            stream.close();
        }
	}

    @Test
    public void testProducePdfStyleNoFill() throws IOException {
        try {
            parser = new PdfStylesParser(stream);
            List<Node> dxfs = parser.parseDxfs();
            PdfStyle style = parser.produceStyle(dxfs.get(2));
            assertNotNull(style);

            XLSXFill fill = style.getFill();
            assertEquals(0, fill.getFgColor().getTheme());

        } finally {
            stream.close();
        }
    }
	
	@Test
	public void testProduceTableStyle() throws IOException {
        try {
            parser = new PdfStylesParser(stream);
            List<Node> tableStyles = parser.parseTableStyles();
            // need to parse dxfs manually
            parser.parseDxfs();
            parser.producePdfStyles();
            TableStyle tableStyle = parser.produceTableStyle(tableStyles.get(0));

            String expectedName = NAME;
            Boolean expectedPivot = PIVOT;
            PdfStyle expectedWholeTableStyle = parser.getPdfStyles().get(13);
            PdfStyle expectedHeaderRowStyle = parser.getPdfStyles().get(12);
            PdfStyle expectedTotalRowStyle = parser.getPdfStyles().get(11);
            PdfStyle expectedFirstColumnStyle = parser.getPdfStyles().get(10);
            PdfStyle expectedLastColumnStyle = parser.getPdfStyles().get(9);
            PdfStyle expectedFirstRowStripeStyle = parser.getPdfStyles().get(8);
            PdfStyle expectedFirstColumnStripeStyle = parser.getPdfStyles().get(7);

            String actualName = tableStyle.getName();
            Boolean actualPivot = tableStyle.isPivot();
            PdfStyle actualWholeTableStyle = tableStyle.getWholeTableStyle();
            PdfStyle actualHeaderRowStyle = tableStyle.getHeaderRowStyle();
            PdfStyle actualTotalRowStyle = tableStyle.getTotalRowStyle();
            PdfStyle actualFirstColumnStyle = tableStyle.getFirstColumnStyle();
            PdfStyle actualLastColumnStyle = tableStyle.getLastColumnStyle();
            PdfStyle actualFirstRowStripeStyle = tableStyle.getFirstRowStripeStyle();
            PdfStyle actualFirstColumnStripeStyle = tableStyle.getFirstColumnStripeStyle();

            assertEquals(expectedName, actualName);
            assertEquals(expectedPivot, actualPivot);
            assertEquals(expectedWholeTableStyle, actualWholeTableStyle);
            assertEquals(expectedHeaderRowStyle, actualHeaderRowStyle);
            assertEquals(expectedTotalRowStyle, actualTotalRowStyle);
            assertEquals(expectedFirstColumnStyle, actualFirstColumnStyle);
            assertEquals(expectedLastColumnStyle, actualLastColumnStyle);
            assertEquals(expectedFirstRowStripeStyle, actualFirstRowStripeStyle);
            assertEquals(expectedFirstColumnStripeStyle, actualFirstColumnStripeStyle);

        } finally {
            stream.close();
        }
	}
	
	@Test
	public void testProducePdfStyleTable() throws IOException {
        try {
            parser = new PdfStylesParser(stream);
            PdfStylesTable table = parser.produceStylesTable();
            int expectedSize = 60;
            int actualSize = table.getTableStyles().size();
            assertEquals(expectedSize, actualSize);

            String expectedDefaultStyleName = DEFAULT_TABLE_STYLE;
            String actualDefaultStyleName = table.getDefaultTableStyle();
            assertEquals(expectedDefaultStyleName, actualDefaultStyleName);

        } finally {
            stream.close();
        }
	}
}
