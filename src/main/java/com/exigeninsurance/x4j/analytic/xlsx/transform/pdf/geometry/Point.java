/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.geometry;
import static java.lang.Float.compare;
import static java.lang.Float.floatToIntBits;


public class Point {

    private final float x;
    private final float y;

    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    @Override
    public String toString() {
        return "(" + x + ";" + y + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Point point = (Point) o;

        if (compare(point.x, x) != 0) {
            return false;
        }
        return compare(point.y, y) == 0;

    }

    @Override
    public int hashCode() {
        int result = (x != +0.0f ? floatToIntBits(x) : 0);
        result = 31 * result + (y != +0.0f ? floatToIntBits(y) : 0);
        return result;
    }
}
