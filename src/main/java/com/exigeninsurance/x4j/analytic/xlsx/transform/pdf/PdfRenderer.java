/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDJpeg;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFPictureData;

import com.exigeninsurance.x4j.analytic.util.StringUtils;
import com.exigeninsurance.x4j.analytic.xlsx.core.node.Node;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.PDFHelper;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.geometry.Line;


public class PdfRenderer {

	public static final float ROW_MARGIN = 5f;
	private static final Color DEFAULT_TEXT_COLOR = Color.BLACK;

	private PageContentStream stream;
	private final PDDocument document;
	private PDPage currentPage;
	
	private float x;
	private float y;
	
	private float spaceLeft;
	private int pageCount;
	
	private float pageWidth;
	private float pageHeight;
	
	private float topVerticalMargin;
	private float bottomVerticalMargin;
    private float leftHorizontalMargin;
    private float rightHorizontalMargin;
    private float headerMargin;
    private float footerMargin;
	
	private final List<PdfDrawingInstruction> items = new ArrayList<PdfDrawingInstruction>();
    private final List<Node> repeatingItems = new ArrayList<Node>();
	
	private final Map<DrawablePdfElement, PDXObjectImage> images = new HashMap<DrawablePdfElement, PDXObjectImage>();
	private final Map<DrawablePdfElement, XSSFPictureData> pictureData = new HashMap<DrawablePdfElement, XSSFPictureData>();
	
	private final PdfContext context;

    private Set<ImageInfo> imageLocations = new HashSet<ImageInfo>();
    private List<Border> borders = new ArrayList<Border>();
	private byte underline = Font.U_NONE;

	public PdfRenderer(PdfContext context, PDDocument document) {
		this.context = context;
        this.document = document;
        resetState();
	}

    public void setPageDimensions(float pageWidth, float pageHeight) {
        this.pageWidth = pageWidth;
        this.pageHeight = pageHeight;
        resetState();
    }
	
	public void prepareNewLine(float componentHeight) throws Exception {
		if (!enoughVerticalSpaceToRender(componentHeight)) {
			renderContentsOnNewPage();
			pageCount++;
		}
		shrinkRemainingVerticalSpace(componentHeight);
	}

	public void ensureEnoughSpace(float componentHeight) throws Exception {
		if (!enoughVerticalSpaceToRender(componentHeight)) {
			renderContentsOnNewPage();
			pageCount++;
			shrinkRemainingVerticalSpace(componentHeight);
		}
	}
	
	private boolean enoughVerticalSpaceToRender(float componentHeight) {
		return (spaceLeft - componentHeight) >= 0;
	}

    public void scalePageSizeTo(float maxRowWidth) {
        float totalWidth = maxRowWidth + leftHorizontalMargin + rightHorizontalMargin;
        if (totalWidth > pageWidth) {
            float ratio = totalWidth / pageWidth;
            pageWidth = totalWidth;
            pageHeight = pageHeight * ratio;
            resetState();
        }
    }
	
	private void renderContentsOnNewPage() throws Exception {
		prepareNewPage();
		renderItems();
		document.addPage(currentPage);
		stream.close();
		resetState();
		repeatItems();
	}
	
	private void prepareNewPage() throws Exception {
		createPage();
		y = pageHeight - topVerticalMargin ;
		x = leftHorizontalMargin;
	}
	
	private void createPage() throws Exception {
		processPictureData();
		currentPage = new PDPage();
		currentPage.setMediaBox(new PDRectangle(pageWidth, pageHeight));
		stream = new PageContentStream(document, currentPage, true, true);
	}
	
	private void processPictureData() throws IOException {
		for (DrawablePdfElement pictureNode : pictureData.keySet()) {
			images.put(pictureNode, createImage(pictureData.get(pictureNode)));
		}
	}

	public PDXObjectImage createImage(XSSFPictureData pictureData) throws IOException {
		return new PDJpeg(document, new ByteArrayInputStream(pictureData.getData()));
	}
	
	private void renderItems() throws Exception {
		for (PdfDrawingInstruction item : items) {
			item.draw(context);
		}
        drawBorders();
        drawPictures();
	}

    private void drawBorders() throws IOException {
        for (Border border : borders) {
            drawLine(border.getLine(), border.getColor(), border.getWidth(), border.getPattern());
        }
        borders.clear();
    }

    public void resetState() {
		currentPage = null;
		spaceLeft = pageHeight - topVerticalMargin - footerMargin - bottomVerticalMargin - headerMargin;
	}
	
	private void repeatItems() throws Exception {
        notifyAndClearItems();
        processRepeatingItems();
    }

    private void notifyAndClearItems() {
        for (PdfDrawingInstruction item : items) {
            item.getDrawable().notify(context);
        }
        items.clear();
    }

    private void processRepeatingItems() throws Exception {
        processTemplateRepeatingItems();
        processAndClearManuallyRepeatedItems();
    }

    private void processTemplateRepeatingItems() throws Exception {
        for (Node item: context.getRepeatingItems()) {
            item.process(context);
        }
    }

    private void processAndClearManuallyRepeatedItems() throws Exception {
        for (Node item : repeatingItems) {
            item.process(context);
        }
        repeatingItems.clear();
    }

    private void shrinkRemainingVerticalSpace(float componentHeight) {
		spaceLeft -= componentHeight;
	}
	
	public void renderCurrentPage() throws Exception {
		renderContentsOnNewPage();
		pageCount++;
	}

    public void removeScheduledItems() {
        items.clear();
        pictureData.clear();
        repeatingItems.clear();
        images.clear();
        imageLocations.clear();
        borders.clear();
    }
	
	public void scheduleForDrawing(DrawablePdfElement element, RenderingParameters params) {
		items.add(new PdfDrawingInstruction(element, params));
	}
	
	public void drawText(String text,Color color, float x, float y) throws IOException {
		setDrawingColor(color);
		stream.beginText();
        stream.moveTextPositionByAmount(x, y) ;
        stream.drawString(text);
        stream.endText();
		drawUnderline(text,color);
	}

	private void drawUnderline(String text, Color color) {
		if (underline != Font.U_NONE && !StringUtils.isEmpty(text)) {
			int fontSize = (int) stream.getFontSize();
			PDFont font = stream.getFont();
			float descend = font.getFontDescriptor().getDescent() / 1000 * fontSize;

			drawUnderline(text,color, descend);
			if (underline == Font.U_DOUBLE || underline == Font.U_DOUBLE_ACCOUNTING) {
				drawUnderline(text,color, descend / 2);
			}
		}
	}

	private void drawUnderline(String text,Color color, float descend) {
		drawBorder(new Border(color,
				new Line(x, y + descend, x + PDFHelper.findCellTextLength((int) stream.getFontSize(), stream.getFont(), text), y + descend),
				DashPattern.SOLID_LINE,
				0.3f));
	}

	public void drawTextAtPointer(String text,Color color) throws IOException {
		drawText(text,color, x, y);
	}

    private void drawLine(Line line, Color color, float width, DashPattern pattern) throws IOException {
    	stream.setStrokingColor(color);
        stream.setLineWidth(width);
        stream.setDashPattern(pattern);
        stream.drawLine(line.getStart().getX(), line.getStart().getY(), line.getEnd().getX(), line.getEnd().getY());
    }
	
	

	public void movePointerTo(float x, float y) throws IOException {
		stream.beginText();
		stream.moveTextPositionByAmount(x, y);
		stream.endText();
		this.x = x;
		this.y = y;
	}
	
	public void movePointerBy(float dx, float dy) throws IOException {
		stream.beginText();
		stream.moveTextPositionByAmount(x + dx, y + dy);
		stream.endText();
        x += dx;
        y += dy;
	}
	
	public void fill(float x, float y, float width, float height, Color color) throws IOException {
        setDrawingColor(color);
		stream.fillRect(x, y, width, height);
	}
	
	public void movePointerToNewLine(float byY) throws IOException {
		stream.beginText();
		stream.moveTextPositionByAmount(leftHorizontalMargin, y - byY);
		stream.endText();
        x = leftHorizontalMargin;
        y -= byY;
	}
	
	public void drawImage(DrawablePdfElement pictureNode, float dy) throws Exception {
		PDXObjectImage image = images.get(pictureNode);
		stream.drawImage(image, x, y - image.getHeight() + dy);
		images.remove(pictureNode);
	}

    public void drawImage(DrawablePdfElement pictureNode, float dy, float width, float height) {
        PDXObjectImage image = images.get(pictureNode);
        saveImageForRendering(image, x, y - height + dy, width, height);
        images.remove(pictureNode);
    }

    public void drawImageAt(DrawablePdfElement pictureNode, float x, float y, float imageWidth, float imageHeight) {
        PDXObjectImage image = images.get(pictureNode);
        saveImageForRendering(image, x, y, imageWidth, imageHeight);
        images.remove(pictureNode);
    }

    private void saveImageForRendering(PDXObjectImage image, float x, float y, float width, float height) {
        imageLocations.add(new ImageInfo(image, x, y, width, height));
    }

    private void drawPictures() throws IOException {
        for (ImageInfo imageInfo : imageLocations) {
            stream.drawXObject(imageInfo.getImage(), imageInfo.getX(), imageInfo.getY(), imageInfo.getWidth(), imageInfo.getHeight());
        }
        imageLocations.clear();
    }

	public boolean itemsScheduledForRendering() {
		return !items.isEmpty();
	}
	
	public void createAndCacheImage(DrawablePdfElement pdfPictureNode,
			XSSFPictureData pictureData) {
		this.pictureData.put(pdfPictureNode, pictureData);

	}
	
	public void openPageForAppending(PDPage page) throws IOException {
        processPictureData();
        resetState();
		currentPage = page;
		pageWidth = page.getMediaBox().getWidth();
		pageHeight = page.getMediaBox().getHeight();
		stream = new PageContentStream(document, page, true, true);
	}
	
	public void closePage() throws IOException {
        drawPictures();
		stream.close();
	}
	
	
	public void setTopVerticalMargin(float topVerticalMargin) {
		this.topVerticalMargin = topVerticalMargin;
	}

	public void setBottomVerticalMargin(float bottomVerticalMargin) {
		this.bottomVerticalMargin = bottomVerticalMargin;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getPageWidth() {
		return pageWidth;
	}

	public float getPageHeight() {
		return pageHeight;
	}

	public int getPageCount() {
		return pageCount;
	}

    public void setDrawingOptions(int fontSize, PDFont font, byte underline) throws IOException {
        stream.setFont(font, fontSize);
		this.underline = underline;
    }

    public void setDrawingColor(Color color) throws IOException {
        stream.setNonStrokingColor(color);
    }

    public float getLeftHorizontalMargin() {
        return leftHorizontalMargin;
    }

    public void setLeftHorizontalMargin(float leftHorizontalMargin) {
        this.leftHorizontalMargin = leftHorizontalMargin;
    }

    public float getRightHorizontalMargin() {
        return rightHorizontalMargin;
    }

    public void setRightHorizontalMargin(float rightHorizontalMargin) {
        this.rightHorizontalMargin = rightHorizontalMargin;
    }

    public void setHeaderMargin(float headerMargin) {
        this.headerMargin = headerMargin;
    }

    public void setFooterMargin(float footerMargin) {
        this.footerMargin = footerMargin;
    }
    
    public float getFooterMargin() {
		return footerMargin;
	}

    public float getTopVerticalMargin() {
        return topVerticalMargin;
    }

	public float getHeaderMargin() {
        return headerMargin;
    }

	public void repeat(PdfContainer element) {
        repeatingItems.add(element);
    }

    public void drawBorder(Border border) {
        borders.add(border);
    }

    public float getRemainingVerticalSpace() {
        return spaceLeft;
    }
}
