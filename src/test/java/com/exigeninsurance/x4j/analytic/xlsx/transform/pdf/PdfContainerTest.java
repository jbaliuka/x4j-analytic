/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.Estimator;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.geometry.SimpleRange;

public class PdfContainerTest {
	
	private static final float ROW_HEIGTH = 10f;
	
	@Mock private Estimator heigthEstimator;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		when(heigthEstimator.estimate(any(RenderingContext.class))).thenReturn(ROW_HEIGTH);
	}
	
	@Test
	public void testFindHeightFromParent() throws IOException {
		PdfContainer firstContainer = new PdfContainer(null, new SimpleRange(0, 0));
		PdfContainer secondContainer = new PdfContainer(null, new SimpleRange(1, 1));
		
		SimpleRange wrapperRange = new SimpleRange(0, 1);
		PdfContainer wrapper = new PdfContainer(null, wrapperRange);
		
		firstContainer.setParent(wrapper);
		firstContainer.setHeigthEstimator(heigthEstimator);
		secondContainer.setParent(wrapper);
		secondContainer.setHeigthEstimator(heigthEstimator);
		
		wrapper.getChildren().add(firstContainer);
		wrapper.getChildren().add(secondContainer);
		
		PdfContext context = new PdfContext(null, new XSSFWorkbook().createSheet(), null);
		RenderingContext renderingContext = new RenderingContext(context, RenderingParameters.empty());
		
		float heigth = firstContainer.getHeigth(renderingContext , wrapperRange);
		
		assertEquals(2 * ROW_HEIGTH, heigth, 0.001f);
	}
}
