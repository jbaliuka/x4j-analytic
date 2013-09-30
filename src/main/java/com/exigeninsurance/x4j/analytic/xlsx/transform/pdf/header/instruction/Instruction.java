/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.header.instruction;

import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.font.PDFont;

import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.DrawablePdfElement;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfContainer;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.PDFHelper;


public abstract class Instruction implements DrawablePdfElement {

	private static final int DEFAULT_FONT_SIZE = 11;
	private static final String DEFAULT_FONT_STYLE = "-,Regular";
	protected String contents;
	private PdfContainer parent;
	private int fontSize;
	private String fontStyle;
	private List<Instruction> children = new ArrayList<Instruction>();
	
	public Instruction(String contents) {
		this.contents = contents;
	}

	public String getContents() {
		return contents;
	}

	public List<Instruction> getChildren() {
		return children;
	}

	public void setChildren(List<Instruction> children) {
		this.children = children;
	}

	public int getFontSize() {
		return fontSize == 0 ? DEFAULT_FONT_SIZE : fontSize;
	}

	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}

	public String getFontStyle() {
		return fontStyle == null ? DEFAULT_FONT_STYLE : fontStyle;
	}

	public void setFontStyle(String fontStyle) {
		this.fontStyle = fontStyle;
	}
	
	protected PDFont getPDFont(String fontStyle) {
		if (fontStyle.equals("-,Regular")) {
			return PDFHelper.TIMES_NORMAL;
		}
		else if (fontStyle.equals("-,Bold")) {
			return PDFHelper.TIMES_BOLD;
		}
		else if (fontStyle.equals("-,Italic")) {
			return PDFHelper.TIMES_ITALIC;
		}
		else if (fontStyle.equals("-,Bold Italic")) {
			return PDFHelper.TIMES_BOLD_ITALIC;
		}
		else {
			throw new IllegalStateException("Illegal font style: " + fontStyle);
		}
	}

    public void process(PdfContext context) {
        for (Instruction child : getChildren()) {
            child.process(context);
        }
    }

	@Override
	public void setParent(PdfContainer element) {
		parent = element;
	}

	@Override
	public PdfContainer getParent() {
		return parent;
	}

	@Override
	public float getHeigth() {
		return parent.getHeigth();
	}
}