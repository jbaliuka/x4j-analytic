/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.cell;

import static java.lang.Float.compare;

import java.awt.Color;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.TypeSafeMatcher;

import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.Border;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.DashPattern;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.geometry.Line;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.geometry.Point;

class BorderMatcher extends TypeSafeMatcher<Border> {


    private final Point from;
    private final Point to;
    private float width;

    private BorderMatcher(Point from, Point to, float width) {
        this.from = from;
        this.to = to;
        this.width = width;
    }

    @Override
    public boolean matchesSafely(Border item) {

        return lineMatches(item.getLine()) && widthMatches(item.getWidth());
    }

    private boolean widthMatches(float width) {
        return compare(width, this.width) == 0;
    }

    private boolean lineMatches(Line line) {
        Point start = line.getStart();
        Point end = line.getEnd();
        return (start.equals(from) && end.equals(to))
            || (start.equals(to) && end.equals(from));
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(new Border(Color.BLACK, new Line(from, to), DashPattern.SOLID_LINE, width).toString());
    }

    @Factory
    public static BorderMatcher border(Point from, Point to, float width) {
        return new BorderMatcher(from, to, width);
    }
}
