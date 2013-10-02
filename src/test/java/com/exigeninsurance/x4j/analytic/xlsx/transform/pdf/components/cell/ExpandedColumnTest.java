/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.cell;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.exigeninsurance.x4j.analytic.api.ReportContext;
import com.exigeninsurance.x4j.analytic.model.ReportMetadata;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfCellNode;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingParameters;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.Constant;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.Estimator;

public class ExpandedColumnTest {

	private RenderingContext renderingContext;
    private PdfContext context;
    private Estimator estimator;
    @Mock private PdfCellNode node;

    @Before
    public void setUp() {
        initMocks(this);
		XSSFSheet sheet = new XSSFWorkbook().createSheet();
		ReportContext reportContext = new ReportContext(new ReportMetadata());
		context = new PdfContext(null, sheet, reportContext);
        Estimator wrappedEstimator = new Constant(10f);
        estimator = new ExpandedColumn(node, wrappedEstimator);

        when(node.getColRef()).thenReturn("A");

		renderingContext = new RenderingContext(context, RenderingParameters.empty());
    }

    @Test
    public void notExpanded() {
        assertThat(estimator.estimate(renderingContext), equalTo(10f));
    }

    @Test
    public void expanded() {
        context.reportColumnWidth("A", 60f);
        assertThat(estimator.estimate(renderingContext), equalTo(60f));
    }
}
