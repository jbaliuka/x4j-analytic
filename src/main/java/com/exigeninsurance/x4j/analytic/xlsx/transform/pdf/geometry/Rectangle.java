/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.geometry;


public class Rectangle {
    private final Point lowerLeft;
    private final Point lowerRight;
    private final Point topLeft;
    private final Point topRight;

    private final Line top;
    private final Line bottom;
    private final Line left;
    private final Line right;

    public Rectangle(float lowerX, float lowerY, float width, float height) {
        lowerLeft = new Point(lowerX, lowerY);
        lowerRight = new Point(lowerX + width, lowerY);
        topLeft = new Point(lowerX, lowerY + height);
        topRight = new Point(lowerX + width, + lowerY + height);
        top = new Line(topLeft, topRight);
        bottom = new Line(lowerLeft, lowerRight);
        left = new Line(lowerLeft, topLeft);
        right = new Line(lowerRight, topRight);
    }

    public Rectangle(Point lowerLeft, float width, float height) {
        this(lowerLeft.getX(), lowerLeft.getY(), width, height);
    }

    public Line getTop() {
        return top;
    }

    public Line getBottom() {
        return bottom;
    }

    public Line getLeft() {
        return left;
    }

    public Line getRight() {
        return right;
    }

    public Point getLowerLeft() {
        return lowerLeft;
    }

    public float getWidth() {
        return bottom.getLength();
    }

    public float getHeight() {
        return left.getLength();
    }

    public Rectangle expand(float dx, float dy) {
        float halfDx = dx / 2;
        float halfDy = dy / 2;
        Point newBase = new Point(lowerLeft.getX() - halfDx, lowerLeft.getY() - halfDy);
        return new Rectangle(newBase, getWidth() + dx, getHeight() + dy);
    }

    @Override
    public String toString() {
        return "(" + lowerLeft + ";" + topRight + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Rectangle rectangle = (Rectangle) o;

        if (!lowerLeft.equals(rectangle.lowerLeft)) {
            return false;
        }
        if (!lowerRight.equals(rectangle.lowerRight)) {
            return false;
        }
        if (!topLeft.equals(rectangle.topLeft)) {
            return false;
        }
        return topRight.equals(rectangle.topRight);

    }

    @Override
    public int hashCode() {
        int result = lowerLeft.hashCode();
        result = 31 * result + lowerRight.hashCode();
        result = 31 * result + topLeft.hashCode();
        result = 31 * result + topRight.hashCode();
        return result;
    }
}
