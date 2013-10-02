/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.util;

import static com.exigeninsurance.x4j.analytic.xlsx.utils.WrappingUtil.htmlWrap;
import static com.exigeninsurance.x4j.analytic.xlsx.utils.WrappingUtil.pdfWrap;
import static com.exigeninsurance.x4j.analytic.xlsx.utils.WrappingUtil.wrap;
import static com.exigeninsurance.x4j.analytic.xlsx.utils.WrappingUtil.wrapFormula;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.exigeninsurance.x4j.analytic.api.ReportContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfCellNode;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLXContext;

public class WrappingUtilTest {

    private static final float DEFAULT_SYMBOL_WIDTH = 1f;

    @Mock private PdfCellNode cellNode;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(cellNode.findTextLength(anyString())).thenAnswer(new Answer<Float>() {
            @Override
            public Float answer(InvocationOnMock invocation) throws Throwable {
                String text = (String) invocation.getArguments()[0];
                return text.length() * DEFAULT_SYMBOL_WIDTH;
            }
        });
    }

    @Test
    public void testWrappingWithNoBreaks() {
        assertThat("one", equalTo(wrap("one")));
		assertThat("one\ntwo", equalTo(wrap("one two")));
		assertThat("one\ntwo three", equalTo(wrap("one two three")));
		assertThat("one two\nthree four", equalTo(wrap("one two three four")));
    }

    @Test
    public void testWrappingWithBreaks() {
        assertThat("one\ntwo", equalTo(wrap("one\ntwo")));
        assertThat("one\ntwo\nthree four", equalTo(wrap("one\ntwo three four")));
    }

    @Test
    public void testWrappingWithBreaksAndWhitespace() {
        String expected = "one  \n \n  two";
        assertThat(expected, equalTo(wrap(expected)));
    }

    @Test
    public void testPdfWrappingEmptyString() {
        List<String> lines = pdfWrap("", cellNode, 10f);
        assertThat(lines, hasSize(0));
    }

    @Test
    public void testPdfWrappingOneWord() {
        List<String> lines = pdfWrap("oneword", cellNode, 10f);
		assertThat(lines, hasSize(1));
        assertThat(lines, hasItem("oneword"));
    }

    @Test
    public void testPdfWrappingOneWordTooLargeToFit() {
        List<String> lines = pdfWrap("012345678901", cellNode, 10f);
        assertThat(lines, hasSize(1));
        assertThat(lines, hasItem("012345678901"));
    }

    @Test
    public void testPdfWrappingMultipleLines() {
        List<String> lines = pdfWrap("123 567 8901", cellNode, 10f);
        assertThat(lines, hasSize(2));
		
    }

    @Test
    public void testPdfWrappingWithBreaks() {
        List<String> lines = pdfWrap(wrap("123 123 12345 78 9012"), cellNode, 10f);
        assertThat(lines, hasSize(3));
        assertEquals("123", lines.get(0));
		assertEquals("123 12345", lines.get(1));
		assertEquals("78 9012", lines.get(2));
    }

    @Test
    public void testHtmlWrapping() {
		assertThat(htmlWrap("123\n123"), equalTo("123</br>123"));
        assertThat(htmlWrap("123 123\n123 123"), equalTo("123&nbsp;123</br>123&nbsp;123"));
    }

    @Test
    public void testFormulaWrapping() throws Exception {
        Map<String,Object> params = new HashMap<String, Object>();
        Map<String, Object> translation = new HashMap<String, Object>();
        translation.put("earnedPremium", "Earned Premium");
        translation.put("totalIncurred", "Total Incurred");
        params.put("translation", translation);
        ReportContext reportContext = new ReportContext(null);
        reportContext.getParameters().putAll(params);
        XLXContext context = new XLXContext(null, new XSSFWorkbook().createSheet(), reportContext, null);

        String expected = "IFERROR('Earned _x000a_Premium'/'Total _x000a_Incurred',0)";
        assertEquals(expected, wrapFormula("IFERROR('${translation.earnedPremium}'/'${translation.totalIncurred}',0)", context));

    }
}
