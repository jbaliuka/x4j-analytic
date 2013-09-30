/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.geometry.Rectangle;


public class RectangleTest {

    @Test
    public void testRectangle() {
        Rectangle rect = new Rectangle(0, 0, 10, 20);
        assertEquals(10, rect.getWidth(), 0.1);
        assertEquals(20,rect.getHeight(), 0.1);
    }

    @Test
    public void testExpand() {
        Rectangle rect = new Rectangle(10, 10, 90, 90);
        Rectangle expandedRect = rect.expand(20, 20);

        assertEquals(110f, expandedRect.getWidth(), 0.1f);
        assertEquals(110f, expandedRect.getHeight(), 0.1f);

        assertEquals(0, expandedRect.getLowerLeft().getX(), 0.1);
    }
}
