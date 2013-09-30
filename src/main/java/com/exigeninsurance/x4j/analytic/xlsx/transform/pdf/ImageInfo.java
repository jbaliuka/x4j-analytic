/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf;

import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;


public class ImageInfo {
    private PDXObjectImage image;
    private float x;
    private float y;
    private float width;
    private float height;

    public ImageInfo(PDXObjectImage image, float x, float y, float width, float height) {
        this.image = image;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public PDXObjectImage getImage() {
        return image;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
}
