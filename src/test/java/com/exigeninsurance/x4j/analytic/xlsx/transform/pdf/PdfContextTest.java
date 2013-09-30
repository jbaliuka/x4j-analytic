/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;

import com.exigeninsurance.x4j.analytic.api.ReportContext;
import com.exigeninsurance.x4j.analytic.model.ReportMetadata;

public class PdfContextTest {

    private PdfContext context;

    @Before
    public void setUp() throws Exception {
		XSSFSheet sheet = new XSSFWorkbook().createSheet();
		context = new PdfContext(null, sheet,
				new ReportContext(new ReportMetadata()));
    }

    @Test
    public void canReportAndRetrieveColumnWidth() {
        context.reportColumnWidth("A", 50f);
        assertThat(context.getColumnWidth("A"), equalTo(50f));
    }

    @Test(expected = IllegalArgumentException.class)
    public void onMissingColumnWidth_IllegalArgumentExceptionIsThrown() {
        context.getColumnWidth("AAAA");
    }

    @Test
    public void maximumReportedColumnWidthIsReturned() {
        context.reportColumnWidth("A", 60f);
        context.reportColumnWidth("A", 70f);
        assertThat(context.getColumnWidth("A"), equalTo(70f));
    }
}
