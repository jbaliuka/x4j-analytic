/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf;

import static com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.DocumentPageCountMatcher.pageCount;
import static com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.DocumentPageDimensionsMatcher.dimensionsForPages;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;

import com.exigeninsurance.x4j.analytic.xlsx.core.node.Node;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.Constant;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.NoOpNotifier;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.Processor;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.Renderer;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.geometry.SimpleRange;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLXContext;

public class PdfRendererTest {

    private PdfRenderer renderer;
    PDDocument document;

    @Mock XSSFSheet sheet;
    @Mock PdfContext context;

    @Mock PdfContainer mockContainer;
    @Mock PdfContainer mockContainer2;
    @Mock PdfContainer mockContainer3;
	private RenderingContext renderingContext;

	@Before
    public void setup() throws IOException {
        initMocks(this);
        document = new PDDocument();
		renderingContext = new RenderingContext(context, RenderingParameters.empty());
    }

    @After
    public void cleanup() throws IOException {
        document.close();
    }

    @Test
    public void testPageGeneration() throws Exception {
        renderer = createRenderer(20, 20);
        renderer.prepareNewLine(10);
        renderer.prepareNewLine(10);
        assertThat(document, pageCount(0));
        renderer.prepareNewLine(10);
        assertThat(document, pageCount(1));
    }

    @Test
    public void verifyItemRenderingCallback() throws Exception {
        renderer =  createRenderer(20, 20);
        PdfContainer container = createContainer(0, 11);
        renderer.prepareNewLine(container.estimateHeight(renderingContext));
        renderer.scheduleForDrawing(container, RenderingParameters.empty());
        renderer.renderCurrentPage();
        verify(context).drawText(eq(""), any(Color.class), anyInt(), anyInt());
    }

    @Test
    public void testPageScaling() throws Exception {
        renderer =  createRenderer(10, 50);
        renderer.scalePageSizeTo(20f);
        renderer.renderCurrentPage();
        assertThat(document, dimensionsForPages(20, 100));
    }

    @Test
    public void pageStretchingWithMargins() throws Exception {
        renderer =  createRenderer(10, 10);
        renderer.setLeftHorizontalMargin(1);
        renderer.setRightHorizontalMargin(1);
        renderer.scalePageSizeTo(10);
        renderer.renderCurrentPage();
        assertThat(document, dimensionsForPages(12, 12));
    }

    @Test
    public void testItemSchedulingAfterStretching() throws Exception {
        renderer =  createRenderer(10, 50);
        renderer.scalePageSizeTo(20);
        renderer.prepareNewLine(40);
        renderer.prepareNewLine(40);
        assertThat(document, pageCount(0));
        renderer.prepareNewLine(30);
        assertThat(document, pageCount(1));
    }

    @Test
    public void ifNewLineDoesntFitIntoCurrentPage_itIsPreparedForTheNextOne() throws Exception {
        renderer =  createRenderer(20, 20);
        renderer.prepareNewLine(10);
        renderer.scheduleForDrawing(createContainer(11, 11), RenderingParameters.empty());
        renderer.prepareNewLine(11);
        assertThat(renderer.getRemainingVerticalSpace(), equalTo(9f));
    }

    @Test
    public void scheduledItemsAreRenderedFirst_thenRepeatingSheetItems_thenManuallyRepeatedItems() throws Exception {
        renderer =  createRenderer(20, 20);
        when(context.getRepeatingItems()).thenReturn(new ArrayList<PdfContainer>(Arrays.asList(mockContainer2)));
        renderer.repeat(mockContainer3);

        InOrder inOrder = inOrder(mockContainer, mockContainer2, mockContainer3);
        renderer.scheduleForDrawing(mockContainer, RenderingParameters.empty());
        renderer.renderCurrentPage();

        inOrder.verify(mockContainer).draw(any(RenderingContext.class));
        inOrder.verify(mockContainer2).process(context);
        inOrder.verify(mockContainer3).process(context);
    }

    @Test
    public void manuallyRepeatedItemsAreProcessedOnce() throws Exception {
        renderer =  createRenderer( 20, 20);
        renderer.repeat(mockContainer);
        renderer.renderCurrentPage();
        renderer.renderCurrentPage();
        verify(mockContainer, times(1)).process(context);
    }

    @Test
    public void allItemsAreNotifiedAfterPageIsRendered() throws Exception {
        renderer =  createRenderer(100, 100);
        renderer.scheduleForDrawing(mockContainer, RenderingParameters.empty());
        renderer.scheduleForDrawing(mockContainer, RenderingParameters.empty());
        renderer.renderCurrentPage();
        verify(mockContainer, times(2)).notify(context);
    }

	@Test
	public void whenPageIsOpenedForAppending_gettersReturnThatPageData() throws Exception {
		renderer = createRenderer(100, 100);
		renderer.renderCurrentPage();
		renderer.setPageDimensions(50, 50);
		renderer.renderCurrentPage();
		PDPage firstPage = (PDPage) document.getDocumentCatalog().getAllPages().get(0);
		PDPage secondPage = (PDPage) document.getDocumentCatalog().getAllPages().get(1);
		renderer.openPageForAppending(firstPage);
		assertThat(renderer.getPageWidth(), equalTo(100f));
		renderer.openPageForAppending(secondPage);
		assertThat(renderer.getPageWidth(), equalTo(50f));
	}

	@Test
	public void ifTheresEnoughSpace_nothingHappens() throws Exception {
		renderer = createRenderer(100, 100);
		renderer.prepareNewLine(50);

		renderer.ensureEnoughSpace(50);
		assertThat(document, pageCount(0));
		assertThat(renderer.getRemainingVerticalSpace(), equalTo(50f));
	}

	@Test
	public void ifThereIsntEnoughSpace_componentIsRenderedInNewPage() throws Exception {
		renderer = createRenderer(100, 100);
		renderer.prepareNewLine(50);

		renderer.ensureEnoughSpace(60);
		assertThat(document, pageCount(1));
		assertThat(renderer.getRemainingVerticalSpace(), equalTo(40f));
	}

    private PdfRenderer createRenderer(float width, float height) {
        PdfRenderer renderer = new PdfRenderer(context, document);
        renderer.setPageDimensions(width, height);
        return renderer;
    }

    private PdfContainer createContainer(float width, float height) {
        PdfContainer container = new PdfContainer(sheet, new SimpleRange(0, 0));
        container.setHeigthEstimator(new Constant(height));
        container.setWidthEstimator(new Constant(width));
        container.setProcessor(new ChildrenProcessor(container));
        container.setNotifier(new NoOpNotifier());
        container.setRenderer(new Renderer() {
            @Override
            public void render(RenderingContext context) throws IOException {
                context.getPdfContext().drawText("",Color.BLACK, 0, 0);
            }
        });
        return container;
    }

    private class ChildrenProcessor implements Processor {

        private PdfContainer parent;

        private ChildrenProcessor(PdfContainer parent) {
            this.parent = parent;
        }

        @Override
        public void process(XLXContext context) throws Exception {
            for (Node node : parent.getChildren()) {
                node.process(context);
            }
        }
    }
}
