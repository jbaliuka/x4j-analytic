
/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/

package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.poi.xssf.usermodel.XSSFFont;

import com.exigeninsurance.x4j.analytic.api.ReportException;
import com.exigeninsurance.x4j.analytic.xlsx.transform.ThemeColor;
import com.exigeninsurance.x4j.analytic.xlsx.transform.XLSXFill;


public class PDFHelper {
	
	public static final PDFont TIMES_NORMAL = PDType1Font.TIMES_ROMAN;
	public static final PDFont TIMES_BOLD = PDType1Font.TIMES_BOLD;
	public static final PDFont TIMES_BOLD_ITALIC = PDType1Font.TIMES_BOLD_ITALIC;
	public static final PDFont TIMES_ITALIC = PDType1Font.TIMES_ITALIC;

    public static final ThemeColor WHITE_COLOR = new ThemeColor(0, 0);
    public static final XLSXFill WHITE_FILL = new XLSXFill(WHITE_COLOR, WHITE_COLOR);

    public static final float PDF_UNITS_TO_EMU_RATIO = (1f / 914400f) * 72;
    public static final float CM_PER_INCH = 2.54f;
    public static final int PDF_POINTS_PER_INCH = 72;
    public static final int EMU_PER_POINT = 12700;
    public static final int EMU_PER_CM = 360000;

    private PDFHelper() {

    }

    private static final float CHARACTER_WIDTH;

    static {
        try {
            CHARACTER_WIDTH = TIMES_NORMAL.getFontDescriptor().getAverageWidth() / 1000 * 11;
        } catch (IOException e) {
            throw new ReportException(e);
        }
    }

    public static PDDocument createScratchFileBasedDocument() throws IOException {
		return new PDDocument(createCosDocument());
	}
	
	public static PDFont findFont(XSSFFont font) {
		boolean bold = font.getBold();
		boolean italic = font.getItalic();
		
		if (bold && italic) {
			return TIMES_BOLD_ITALIC;
		}
		
		else if (bold) {
			return TIMES_BOLD;
		}
		
		else if (italic) {
			return TIMES_ITALIC;
		}
		
		else {
			return TIMES_NORMAL;
		}
	}
	
	public static float findMaxFontHeigth(PDFont font, int fontSize) {
		return (font.getFontDescriptor().getAscent() / 1000 * fontSize) 
					- (font.getFontDescriptor().getDescent() / 1000 * fontSize);
		
	}
	
	public static float excelToPixel(float charSpaces) {
		return charSpaces / 256f * CHARACTER_WIDTH;
	}

    public static float emuToPdfUnits(float emu) {
        return emu * PDF_UNITS_TO_EMU_RATIO;
    }
	
	public static float findCellTextLength(int fontHeigth, PDFont font, String value) {
		
		float length;
		try {
			length = font.getStringWidth(value) / 1000 * fontHeigth;
		} catch (IOException e) {
			throw new ReportException(e);
		}

		return length;
	}
	
	private static COSDocument createCosDocument() throws IOException {
		COSDocument document = new COSDocument(new File(System.getProperty("java.io.tmpdir")));

        COSDictionary trailer = new COSDictionary();
        document.setTrailer( trailer );

        COSDictionary rootDictionary = new COSDictionary();
        trailer.setItem( COSName.ROOT, rootDictionary );
        rootDictionary.setItem( COSName.TYPE, COSName.CATALOG );
        rootDictionary.setItem( COSName.VERSION, COSName.getPDFName( "1.4" ) );

        COSDictionary pages = new COSDictionary();
        rootDictionary.setItem( COSName.PAGES, pages );
        pages.setItem( COSName.TYPE, COSName.PAGES );
        COSArray kidsArray = new COSArray();
        pages.setItem( COSName.KIDS, kidsArray );
        pages.setItem( COSName.COUNT, COSInteger.ZERO );
        
        return document;
	}

    public static int pointsToEmu(float points) {
        return (int) (points * EMU_PER_POINT);
    }
    public static float mmToPdfUnits(float mm) {
        return mm / 10 / CM_PER_INCH * PDF_POINTS_PER_INCH;
    }
    public static float inchesToPdfUnits(float inches) {
        return inches * 72;
    }}
