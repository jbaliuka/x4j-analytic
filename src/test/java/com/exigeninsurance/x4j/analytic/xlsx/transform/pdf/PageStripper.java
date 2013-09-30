/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.util.PDFOperator;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.pdfbox.util.TextPosition;

import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.geometry.Rectangle;

public class PageStripper extends PDFTextStripper {

    public static final String RG_NON_STROKING = "rg";
    public static final String FILL_NON_ZERO = "f";
    public static final String RECTANGLE = "re";
    public static final String STROKE = "S";
	
	private final List<TextInfo> textInfos = new ArrayList<TextInfo>();
    private final List<ColoredRegion> rects = new ArrayList<ColoredRegion>();

    private float pageHeight;
    private Color currentNonStrokingColor = Color.BLACK;
    private List<Rectangle> rectsInPath = new ArrayList<Rectangle>();
    
	
    private PageStripper(PDPage page) throws IOException {
        super.setSortByPosition(true);
        pageHeight = page.getMediaBox().getHeight();
    }

    public static PageContents extractPageContents(PDDocument document, int pageNumber) throws IOException {
        List<?> allPages = document.getDocumentCatalog().getAllPages();
        PDPage page = (PDPage)allPages.get(pageNumber);
        PageStripper stripper = new PageStripper(page);
        PDStream contents = page.getContents();
        if( contents != null ) {
            stripper.processStream(page, page.findResources(), page.getContents().getStream());
        }
        return stripper.getContents();
    }

    @Override
    protected void processTextPosition(TextPosition text) {
    	TextInfo info = new TextInfo();
    	info.setX(text.getXDirAdj());
    	info.setY(pageHeight - text.getYDirAdj());
    	info.setFontSize((int) text.getFontSize());
    	info.setWidth(text.getWidthDirAdj());
    	info.setHeigth(text.getHeight());
    	info.setString(text.getCharacter());
    	info.setFont(text.getFont());
    	textInfos.add(info);
    }

    @Override
    protected void processOperator(PDFOperator operator, List<COSBase> arguments) throws IOException {
        String operation = operator.getOperation();
        processOperation(operation, arguments);
        super.processOperator(operator, arguments);
    }

    private void processOperation(String operation, List<COSBase> arguments) {
        if (operation.equals(RG_NON_STROKING)) {
            setColor(arguments);
        } else if (operation.equals(FILL_NON_ZERO)) {
            fill();
        } else if (operation.equals(RECTANGLE)) {
            addRect(arguments);
        }
    }

    private void addRect(List<COSBase> arguments) {
        if (arguments.size() != 4) {
            throw new IllegalArgumentException("Expecting 4 arguments for rectangle");
        }
        float x = ((COSNumber)arguments.get(0)).floatValue();
        float y = ((COSNumber)arguments.get(1)).floatValue();
        float width = ((COSNumber)arguments.get(2)).floatValue();
        float height = ((COSNumber)arguments.get(3)).floatValue();
        rectsInPath.add(new Rectangle(x, y, width, height));
    }

    private void fill() {
        Color copy = new Color(currentNonStrokingColor.getRGB());
        for (Rectangle rect : rectsInPath) {
            rects.add(new ColoredRegion(rect, copy));
        }
        rectsInPath.clear();
    }

    private void setColor(List<COSBase> arguments) {
        if (arguments.size() != 3) {
            throw new IllegalArgumentException("Expecting 3 arguments for RGB color");
        }
        float r = ((COSNumber)arguments.get(0)).floatValue();
        float g = ((COSNumber)arguments.get(1)).floatValue();
        float b = ((COSNumber)arguments.get(2)).floatValue();
        currentNonStrokingColor = new Color(r, g, b);
    }

    private List<List<TextInfo>> sortToLines() {
    	List <List<TextInfo>> lines = new ArrayList<List<TextInfo>>();
        if (!textInfos.isEmpty()) {
            TextInfo previousInfo = textInfos.get(0);
            List <TextInfo> line = new ArrayList<TextInfo>();
            line.add(previousInfo);
            for (int i = 1; i < textInfos.size(); i++) {
                TextInfo currentInfo = textInfos.get(i);
                if (isNewLine(currentInfo, previousInfo)) {
                    if (!line.isEmpty()) {
                        lines.add(line);
                        line = new ArrayList <TextInfo>();
                    }
                    else {
                        line = new ArrayList <TextInfo>();
                    }

                }
                line.add(currentInfo);
                previousInfo = currentInfo;
            }
            if (!line.isEmpty()) {
                lines.add(line);
            }
        }
    	return lines;
    }

    private Boolean isNewLine(TextInfo currentInfo, TextInfo previousInfo) {
        float previousY = previousInfo.getY();
        float currentY = currentInfo.getY();
        return Float.compare(currentY, previousY) != 0;
    }
    
    private List<List<Word>> createWords(List<List<TextInfo>> list) {
    	List <List<Word>> lines = new ArrayList<List<Word>>();
    	for (List<TextInfo> line : list) {
    		TextInfo previous = line.get(0);
    		List <Word> words = new ArrayList<Word>();
    		boolean inWord;
    		Word word = new Word(previous);
    		for (int i = 1; i < line.size(); i++) {
    			float previousX = round(previous.getX());
    			float previousWidth = round(previous.getWidth());
    			TextInfo currentInfo = line.get(i);
    			float x = round(currentInfo.getX());
    			float previousSum = previousX + previousWidth;
    			float errorMargin = previous.getFontSize() / 10f;
    			inWord = ((previousSum >= x - errorMargin) && (previousSum <= x + errorMargin));
    			
    			if (inWord) {
    				word.addElement(currentInfo);
    			}
    			else {
    				words.add(word);
    				word = new Word(currentInfo);
    			}
    			previous = currentInfo;
    		}
    		if (!word.getElements().isEmpty()) {
    			words.add(word);
    		}
    		if (!words.isEmpty()) {
    			lines.add(words);
    		}
    	}
    		
    	return lines;
    }
    
    private PageContents getContents() {
    	return new PageContents(createWords(sortToLines()), rects);
    }
    
    private float round(float value) {
    	return Math.round(value * 10.0f) / 10.0f;
    }
}
