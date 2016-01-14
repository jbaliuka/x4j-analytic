/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf;

import java.awt.Color;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFPictureData;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.exigeninsurance.x4j.analytic.api.ReportContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.Picture;
import com.exigeninsurance.x4j.analytic.xlsx.transform.SST;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.PDFHelper;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.geometry.Rectangle;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.header.PdfFooter;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.header.PdfHeader;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLXContext;
import com.exigeninsurance.x4j.analytic.xlsx.utils.Dimension;
import com.exigeninsurance.x4j.analytic.xlsx.utils.PaperDimensionsProvider;


public class PdfContext extends XLXContext {
	
	private PdfRenderer renderer;
	
	private PDDocument document;
	
	private Map<XSSFSheet, List<PdfHeader>> headerMap;
	private Map<XSSFSheet, List<PdfFooter>> footerMap;
    private Map<XSSFSheet, Map<String, Picture>> headerFooterPictureMap;
    private List<PdfContainer> repeatingItems;

    private int currentPage;
	private Map<XSSFSheet, Map<String, Float>> workbookColumnWidths = new HashMap<XSSFSheet, Map<String, Float>>();
    private Map<String, Float> maxColumnWidths = new HashMap<String, Float>();
    private float maxRowWidth;
    private Dimension paperDimensions;

    public PdfContext(SST sst, XSSFSheet sheet, ReportContext reportContext) {
		super(sst, sheet, reportContext, null);
	}

	public void prepareForRendering(XSSFSheet sheet) throws IOException {
		document = PDFHelper.createScratchFileBasedDocument();
        paperDimensions = new PaperDimensionsProvider().getPaperDimensions(sheet);
        renderer = new PdfRenderer(this, document);

        updatePageSetup(sheet);
	}

    public void updatePageSetup(XSSFSheet sheet) {
        renderer.setPageDimensions(PDFHelper.mmToPdfUnits(paperDimensions.getWidth()),
                                    PDFHelper.mmToPdfUnits(paperDimensions.getHeight()));
        setMargins(sheet);
        scalePageSize();
		maxColumnWidths = workbookColumnWidths.get(sheet);
    }

	public void setColumnWidths(XSSFWorkbook wb) {
		for (int i = 0; i < wb.getNumberOfSheets(); i++) {
			XSSFSheet sheet = wb.getSheetAt(i);
			Map<String, Float> maxColumnWidths = new HashMap<String, Float>();
			for (int col = 0; col < 256; col++) {
				maxColumnWidths.put(CellReference.convertNumToColString(col), PDFHelper.excelToPixel(sheet.getColumnWidth(col)));
			}
			workbookColumnWidths.put(sheet, maxColumnWidths);
		}
	}

	private void scalePageSize() {
        renderer.scalePageSizeTo(maxRowWidth);
    }
	
	private void setMargins(XSSFSheet sheet) {
		renderer.setTopVerticalMargin(PDFHelper.inchesToPdfUnits((float) sheet.getMargin(Sheet.TopMargin)));
		renderer.setBottomVerticalMargin(PDFHelper.inchesToPdfUnits((float) sheet.getMargin(Sheet.BottomMargin)));
        renderer.setLeftHorizontalMargin(PDFHelper.inchesToPdfUnits((float) sheet.getMargin(Sheet.LeftMargin)));
        renderer.setRightHorizontalMargin(PDFHelper.inchesToPdfUnits((float) sheet.getMargin(Sheet.RightMargin)));
        renderer.setFooterMargin(PDFHelper.inchesToPdfUnits((float) sheet.getMargin(Sheet.FooterMargin)));
        renderer.setHeaderMargin(PDFHelper.inchesToPdfUnits((float) sheet.getMargin(Sheet.HeaderMargin)));
	}
	
	public void insertHeadersAndFooters(XSSFSheet sheet, int fromPage, int toPage) throws Exception {
		List <?> pages = document.getDocumentCatalog().getAllPages();
        setSheet(sheet);
        for (int pageNumber = fromPage; pageNumber < toPage + 1; pageNumber++) {
                currentPage = pageNumber + 1;
				addHeaderFooter((PDPage) pages.get(pageNumber), pageNumber + 1);
			}
		}
	
	private void addHeaderFooter(PDPage page, int pageNumber) throws Exception {
		XSSFSheet sheet = getSheet();
		List<PdfHeader> headers = headerMap.get(sheet);
		List<PdfFooter> footers = footerMap.get(sheet);

        for (PdfHeader header : headers) {
            if (header.isApplicable(pageNumber, sheet)) {
                header.process(this);
            }
        }

        for (PdfFooter footer : footers) {
            if (footer.isApplicable(pageNumber, sheet)) {
                footer.process(this);
            }
        }
		
		renderer.openPageForAppending(page);
		RenderingContext context = new RenderingContext(this, RenderingParameters.empty());
		for (PdfHeader header : headers) {
			if (header.isApplicable(pageNumber, sheet)) {
				header.draw(context);
			}
		}
		for (PdfFooter footer : footers) {
			if (footer.isApplicable(pageNumber, sheet)) {
				footer.draw(context);
			}
		}
		renderer.closePage();
	}
	
	public void saveDocument(String fileName) throws COSVisitorException, IOException {
		document.save(fileName);
	}
	
	public void cleanUp() throws IOException {
		if (document != null) {
			document.close();
		}
	}

	public void prepareNewLine(float componentHeigth) throws Exception {
		renderer.prepareNewLine(componentHeigth);
	}
	
	public void renderCurrentPage() throws Exception {
		renderer.renderCurrentPage();
	}
	
	@Override
	public void addPageBreak() throws Exception {
		renderer.renderCurrentPage();
	}

	public void resetRenderingState() {
		renderer.resetState();
	}
	
	public void drawLater(DrawablePdfElement element, RenderingParameters params) {
		renderer.scheduleForDrawing(element, params);
	}

    public void setTextOptions(int fontSize, PDFont font, byte underline) throws IOException {
        renderer.setDrawingOptions(fontSize, font, underline);
    }
	
	public void drawText(String text,Color color, float x, float y) throws IOException {
		renderer.drawText(text,color, x, y);
	}
	
	public void drawTextAtPointer(String text,Color color) throws IOException {
		renderer.drawTextAtPointer(text,color);
	}

    public void drawBorder(Border border) {
        renderer.drawBorder(border);
    }
	
	public void movePointerTo(float x, float y) throws IOException {
		renderer.movePointerTo(x, y);
	}
	
	public void movePointerBy(float dx, float dy) throws IOException {
		renderer.movePointerBy(dx, dy);
	}
	
	public void fill(float x, float y, float width, float heigth, Color color) throws IOException {
		renderer.fill(x, y, width, heigth, color);
	}

    public void fill(Rectangle area, Color color) throws IOException {
        fill(area.getLowerLeft().getX(), area.getLowerLeft().getY(), area.getWidth(), area.getHeight(), color);
    }
	
	public void movePointerToNewLine(float byY) throws IOException {
		renderer.movePointerToNewLine(byY);
	}
	
	public void drawImage(DrawablePdfElement pictureNode, float dy) throws Exception {
		renderer.drawImage(pictureNode, dy);
	}

    public void drawImage(DrawablePdfElement pictureNode, float dy, float width, float height) {
        renderer.drawImage(pictureNode, dy, width, height);
    }

    public void drawImageAt(DrawablePdfElement pictureNode, float x, float y, float width, float height) {
        renderer.drawImageAt(pictureNode, x, y, width, height);
    }

	public boolean allItemsRendered() {
		return !renderer.itemsScheduledForRendering();
	}

	public PDXObjectImage createImage(XSSFPictureData pictureData) throws IOException {
		return renderer.createImage(pictureData);
	}

	public void createAndCacheImage(DrawablePdfElement pdfPictureNode,
			XSSFPictureData pictureData) {
		renderer.createAndCacheImage(pdfPictureNode, pictureData);
	}

    public Picture getHeaderFooterPicture(String sectionCode) {
        Map<String, Picture> currentSheetPictures = headerFooterPictureMap.get(getSheet());
        return currentSheetPictures.get(sectionCode);
    }

	public float getX() {
		return renderer.getX();
	}

	public float getY() {
		return renderer.getY();
	}

	public int getTotalPageCount() {
		return renderer.getPageCount();
	}

    public int getCurrentPageNumber() {
        return currentPage;
    }

	public float getPageWidth() {
		return renderer.getPageWidth();
	}

	public float getPageHeigth() {
		return renderer.getPageHeight();
	}

	public void setHeaderMap(Map<XSSFSheet, List<PdfHeader>> headerMap) {
		this.headerMap = headerMap;
	}

	public void setFooterMap(Map<XSSFSheet, List<PdfFooter>> footerMap) {
		this.footerMap = footerMap;
	}

    public void setHeaderFooterPictureMap(Map<XSSFSheet, Map<String, Picture>> headerFooterPictureMap) {
        this.headerFooterPictureMap = headerFooterPictureMap;
    }

    public float getLeftHorizontalMargin() {
        return renderer.getLeftHorizontalMargin();
    }

    public float getRightHorizontalMargin() {
        return renderer.getRightHorizontalMargin();
    }

    public float getTopOfDrawablePage() {
        return renderer.getPageHeight() - renderer.getTopVerticalMargin() - renderer.getHeaderMargin();
    }

    public void repeat(PdfContainer element) {
        renderer.repeat(element);
    }

    public void setRepeatingItems(List<PdfContainer> repeatingItems) {
        this.repeatingItems = repeatingItems;
    }

    public List<PdfContainer> getRepeatingItems() {
        return repeatingItems;
    }

    public void sheetOver() {
        renderer.removeScheduledItems();
}

    public float getMargins() {
        return PdfRenderer.ROW_MARGIN;
    }

    public float getColumnWidth(String column) {
        if (maxColumnWidths.containsKey(column)) {
            return maxColumnWidths.get(column);
        }
        throw new IllegalArgumentException("column not found");
    }

    public boolean isExpanded(String column) {
        return maxColumnWidths.containsKey(column);
    }

    public void reportColumnWidth(String column, float width) {
        if (maxColumnWidths.containsKey(column)) {
            float current = maxColumnWidths.get(column);
            if (current < width) {
                maxColumnWidths.put(column, width);
            }
        } else {
			float colWidth = PDFHelper.excelToPixel(getSheet().getColumnWidth(CellReference.convertColStringToIndex(column)));
			maxColumnWidths.put(column, Math.max(width, colWidth));
        }
    }

    public void reportRowWidth(PdfContainer container) {
        float rowWidth = container.estimateWidth(new RenderingContext(this, RenderingParameters.empty()));
        if (maxRowWidth < rowWidth) {
            maxRowWidth = rowWidth;
        }
    }

    public Map<String, Float> getMaxColumnWidths() {
        return maxColumnWidths;
    }

    public float getMaxRowWidth() {
        return maxRowWidth;
    }

    public void setMaxRowWidth(float maxRowWidth) {
        this.maxRowWidth = maxRowWidth;
    }

	public void ensureEnoughSpace(float componentHeight) throws Exception {
		renderer.ensureEnoughSpace(componentHeight);
	}
	
	public float getHeaderMargin(){
		return renderer.getHeaderMargin();
	}
	
	public float getFooterMargin(){
		return renderer.getFooterMargin();
	}
	
}
