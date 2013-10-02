/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.IOException;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTHeaderFooter;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPageSetup;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STOrientation;

import com.exigeninsurance.x4j.analytic.xlsx.core.node.Node;

public class XLSXSheetTailTest {

    private XSSFSheet sheet;
    private Node tail;

    @Mock private XLXContext context;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        XSSFWorkbook workbook = new XSSFWorkbook();
        sheet = workbook.createSheet();
        tail = new XLSXSheetTail(sheet);
    }

    @Test
    public void testLandscapeOrientation() throws Exception {
        setLandscapeOrientation(sheet);
        tail.process(context);
        wasWritten("orientation=\"landscape\"");
    }

    @Test
    public void verifyEmptyHeaderFooterIsNotIncluded() throws Exception {
        tail.process(context);
        wasNotWritten("<headerFooter");
    }

    @Test
    public void verifyHeaderFooterAttributesAreSetOnDifferentFirstAndOddEven() throws Exception {
        setEvenAndOddAndFirstHeaders("even", "odd", "first");
        tail.process(context);
        wasWritten("<headerFooter " +
                "differentFirst=\"1\" differentOddEven=\"1\">");
    }

    @Test
    public void testOddHeader() throws Exception {
        setOddHeader(sheet, "odd");
        tail.process(context);
        wasWritten("<headerFooter>" +
                "<oddHeader>odd</oddHeader>" +
                "</headerFooter>");
    }

    @Test
    public void testPageSetup() throws Exception {
        CTPageSetup pageSetup = createPageSetupIfAbsent();
        pageSetup.setPaperSize(3);
        pageSetup.setOrientation(STOrientation.LANDSCAPE);
        pageSetup.setId("1");
        pageSetup.setFitToHeight(2);
        pageSetup.setFitToWidth(4);
        tail.process(context);
        wasWritten("<pageSetup r:id=\"1\" orientation=\"landscape\" fitToHeight=\"2\" fitToWidth=\"4\" paperSize=\"3\"/>");
    }

    @Test
    public void testAllHeadersAndFooters() throws Exception {
        setAllHeadersAndFooters("text");
        tail.process(context);
        wasWritten("<headerFooter differentFirst=\"1\" differentOddEven=\"1\">" +
                "<oddHeader>text</oddHeader>" +
                "<oddFooter>text</oddFooter>" +
                "<evenHooter>text</evenHooter>" +
                "<evenFooter>text</evenFooter>" +
                "<firstHeader>text</firstHeader>" +
                "<firstFooter>text</firstFooter></headerFooter>");
    }

    private void setEvenAndOddAndFirstHeaders(String even, String odd, String first) {
        CTHeaderFooter headerFooter = createHeaderFooterIfAbsent(sheet);
        headerFooter.setOddHeader(odd);
        headerFooter.setEvenHeader(even);
        headerFooter.setFirstHeader(first);
        headerFooter.setDifferentOddEven(true);
        headerFooter.setDifferentFirst(true);
    }

    private void setAllHeadersAndFooters(String text) {
        CTHeaderFooter headerFooter = createHeaderFooterIfAbsent(sheet);
        headerFooter.setOddHeader(text);
        headerFooter.setEvenHeader(text);
        headerFooter.setFirstHeader(text);
        headerFooter.setOddFooter(text);
        headerFooter.setEvenFooter(text);
        headerFooter.setFirstFooter(text);
        headerFooter.setDifferentOddEven(true);
        headerFooter.setDifferentFirst(true);
    }

    private void setOddHeader(XSSFSheet sheet, String text) {
        CTHeaderFooter headerFooter = createHeaderFooterIfAbsent(sheet);
        headerFooter.setOddHeader(text);
    }

    private CTHeaderFooter createHeaderFooterIfAbsent(XSSFSheet sheet) {
        if (sheet.getCTWorksheet().getHeaderFooter() == null) {
            sheet.getCTWorksheet().setHeaderFooter(CTHeaderFooter.Factory.newInstance());
        }
        return sheet.getCTWorksheet().getHeaderFooter();
    }

    private CTPageSetup createPageSetupIfAbsent() {
        if (sheet.getCTWorksheet().getPageSetup() == null) {
            sheet.getCTWorksheet().setPageSetup(CTPageSetup.Factory.newInstance());
        }
        return sheet.getCTWorksheet().getPageSetup();
    }

    private void setLandscapeOrientation(XSSFSheet sheet) {
        CTPageSetup ctPageSetup = CTPageSetup.Factory.newInstance();
        ctPageSetup.setOrientation(STOrientation.Enum.forInt(STOrientation.INT_LANDSCAPE));
        sheet.getCTWorksheet().setPageSetup(ctPageSetup);
    }

    private void wasWritten(String substring) throws IOException {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(context).write(captor.capture());
        assertThat(captor.getValue(), containsString(substring));
    }

    private void wasNotWritten(String substring) throws IOException {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(context).write(captor.capture());
        assertThat(captor.getValue(), not(containsString(substring)));
    }
}
