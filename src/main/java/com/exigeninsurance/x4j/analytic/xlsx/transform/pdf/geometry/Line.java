/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.geometry;


public class Line {

    private final Point start;
    private final Point end;
    private final float length;

    public Line(float startX, float startY, float endX, float endY) {
        this(new Point(startX, startY), new Point(endX, endY));
    }

    public Line(Point start, Point end) {
        this.start = start;
        this.end = end;
        length = (float) Math.sqrt(Math.pow(end.getX() - start.getX(), 2) + Math.pow(end.getY() - start.getY(), 2));
    }

    public float getLength() {
        return length;
    }

    public Point getStart() {
        return start;
    }

    public Point getEnd() {
        return end;
    }

    @Override
    public String toString() {
        return "(" + start + " - " + end + ")";
    }

    public Line extend(float by) {
        float half = by / 2;
        Point new1 = extend(start, end, half);
        Point new2 = extend(end, start, half);
        return new Line(new1, new2);
    }

    private Point extend(Point start, Point end, float extendBy) {
        float newLength = length + extendBy;
        float newX = start.getX()+ (end.getX() - start.getX()) / length * newLength;
        float newY = start.getY()+ (end.getY() - start.getY()) / length * newLength;
        return new Point(newX, newY);
    }
}
