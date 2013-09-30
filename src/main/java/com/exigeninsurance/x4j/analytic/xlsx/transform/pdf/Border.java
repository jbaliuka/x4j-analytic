/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf;

import java.awt.Color;

import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.geometry.Line;


public class Border {

    public static final float NARROW = 0.5f;
    public static final float MEDIUM = 1f;
    public static final float WIDE = 1.5f;

    private Color color;
    private Line line;
    private DashPattern pattern;
    private float width;

    public Border(Color color, Line line, DashPattern pattern, float width) {
        this.color = color;
        this.line = line;
        this.pattern = pattern;
        this.width = width;
    }

    public Color getColor() {
        return color;
    }

    public Line getLine() {
        return line;
    }

    public DashPattern getPattern() {
        return pattern;
    }

    public float getWidth() {
        return width;
    }

    @Override
    public String toString() {
        return color + " " + line + " " + pattern + " " + width;
    }
}
