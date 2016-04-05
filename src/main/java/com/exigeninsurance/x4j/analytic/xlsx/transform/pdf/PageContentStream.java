/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/

package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf;

import static java.lang.Float.compare;

import java.awt.Color;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;


public class PageContentStream extends PDPageContentStream {

    private static final String DASH_PATTERN = "d\n";

    private DashPattern dashPattern = DashPattern.SOLID_LINE;
    private PDFont font;
    private float fontSize;
    private float lineWidth;
    private Color nonStrokingColor;
    private Color strokingColor;

    public PageContentStream(PDDocument document, PDPage sourcePage) throws IOException {
        super(document, sourcePage);
    }

    public PageContentStream(PDDocument document, PDPage sourcePage, boolean appendContent, boolean compress) throws IOException {
        super(document, sourcePage, appendContent, compress);
    }

    public PageContentStream(PDDocument document, PDPage sourcePage, boolean appendContent, boolean compress, boolean resetContext) throws IOException {
        super(document, sourcePage, appendContent, compress, resetContext);
    }

    public void setDashPattern(DashPattern pattern) throws IOException {
        if (!dashPattern.equals(pattern)) {
            dashPattern = pattern;
            appendRawCommands(createDashPatternCommand(pattern.getDashArray(), pattern.getDashPhase()));
            appendRawCommands(" ");
            appendRawCommands(DASH_PATTERN);
        }
    }

    @Override
    public void setFont(PDFont font, float fontSize) throws IOException {
        if (!(compare(this.fontSize, fontSize) == 0) || !font.equals(this.font)) {
            this.fontSize = fontSize;
            this.font = font;
            super.setFont(font, fontSize);
        }
    }

    @Override
    public void setNonStrokingColor(Color color) throws IOException {
        if (color != null && !color.equals(nonStrokingColor)) {
            nonStrokingColor =  color;
            super.setNonStrokingColor(color);
        }
    }
    
    @Override
    public void setStrokingColor(Color color) throws IOException {
        if (color != null && !color.equals(strokingColor)) {
            strokingColor =  color;
            super.setStrokingColor(color);
        }
    }

    @Override
    public void setLineWidth(float lineWidth) throws IOException {
        if (!(compare(this.lineWidth, lineWidth) == 0)) {
            this.lineWidth = lineWidth;
            super.setLineWidth(lineWidth);
        }
    }

    private String createDashPatternCommand(float[] dashArray, int dashPhase) {
        return arrayToString(dashArray) + " " + dashPhase;
    }

    private String arrayToString(float [] array) {
        int lastIndex = array.length - 1;
        if (lastIndex == -1) {
            return "[]";
        }
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (int i = 0; i < lastIndex; i++) {
            builder.append(array[i]).append(" ");
        }

        builder.append(array[lastIndex]).append("]");
        return builder.toString();
    }

	public float getFontSize() {
		return fontSize;
	}

	public PDFont getFont() {
		return font;
	}
}
