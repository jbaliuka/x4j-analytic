
/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf;

import java.awt.Color;

import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.geometry.Line;


public class ColoredLine {

    private Line line;
    private Color color;

    public ColoredLine(Line line, Color color) {
        this.line = line;
        this.color = color;
    }

    public Line getLine() {
        return line;
    }

    public Color getColor() {
        return color;
    }
}
