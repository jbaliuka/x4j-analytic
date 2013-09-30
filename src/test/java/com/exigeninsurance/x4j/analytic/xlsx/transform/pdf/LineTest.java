/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf;

import static junit.framework.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;

import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.geometry.Line;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.geometry.Point;

public class LineTest {

    @Test
    public void testLineLength() {
        Point start = new Point(0, 0);
        Point end = new Point(3, 4);
        Line line = new Line(start, end);
        assertEquals(5f, line.getLength(), 0.01f);
    }

    @Test
    public void testExtend() {
        Point start = new Point(0, 0);
        Point end = new Point(4, 3);
        Line line = new Line(start, end);
        float by = 1.25f * 2; // extend to x = 5
        Line extended = line.extend(by);
        start = extended.getStart();
        end = extended.getEnd();
        float lowerX = Math.min(start.getX(), end.getX());
        float upperX = Math.max(start.getX(), end.getX());
        Assert.assertEquals(5f, upperX, 0.01f);
        Assert.assertEquals(-1f, lowerX, 0.01f);
    }

    @Test
    public void testHorizontalExtend() {
        Point start = new Point(0, 0);
        Point end = new Point(2, 0);
        Line line = new Line(start, end);
        float by = 2;
        Line extended = line.extend(by);
        start = extended.getStart();
        end = extended.getEnd();
        float leftX = Math.min(start.getX(), end.getX());
        float rightX = Math.max(start.getX(), end.getX());
        Assert.assertEquals(-1f, leftX, 0.01f);
        Assert.assertEquals(3, rightX, 0.01f);
        Assert.assertEquals(0f, start.getY(), 0.01f);
        Assert.assertEquals(0f, end.getY(), 0.01f);
    }

    @Test
    public void testVerticalExtend() {
        Point start = new Point(0, 0);
        Point end = new Point(0, 2);
        Line line = new Line(start, end);
        float by = 2;
        Line extended = line.extend(by);
        start = extended.getStart();
        end = extended.getEnd();
        float lowerY = Math.min(start.getY(), end.getY());
        float upperY = Math.max(start.getY(), end.getY());
        Assert.assertEquals(-1f, lowerY, 0.01f);
        Assert.assertEquals(3, upperY, 0.01f);
        Assert.assertEquals(0f, start.getX(), 0.01f);
        Assert.assertEquals(0f, end.getX(), 0.01f);
    }
}
