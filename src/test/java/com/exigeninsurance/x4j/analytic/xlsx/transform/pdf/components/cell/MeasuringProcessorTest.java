/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.cell;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.MockitoAnnotations.initMocks;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;

import com.exigeninsurance.x4j.analytic.api.ReportContext;
import com.exigeninsurance.x4j.analytic.model.ReportMetadata;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfCellNode;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.Constant;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.Processor;

public class MeasuringProcessorTest {

    private PdfCellNode node;
    private PdfContext context;

    @Before
    public void setup() {
        XSSFWorkbook book = new XSSFWorkbook();
        XSSFSheet sheet = book.createSheet();
        XSSFRow row = sheet.createRow(0);
        XSSFCell cell = row.createCell(0);
        node = new PdfCellNode(sheet, cell, null);
        node.setWidthEstimator(new Constant(10f));

        initMocks(this);
		context = new PdfContext(null, sheet, new ReportContext(new ReportMetadata()));
    }

    @Test
    public void uponProcessingWidthIsReported() throws Exception {
        Processor processor = new MeasuringProcessor(node, new Constant(50f));
        processor.process(context);
        assertThat(context.getColumnWidth("A"), equalTo(50f));
    }

}
