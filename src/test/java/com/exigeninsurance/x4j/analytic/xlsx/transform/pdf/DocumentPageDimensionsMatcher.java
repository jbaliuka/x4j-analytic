/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/

package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf;

import static java.lang.Float.compare;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.TypeSafeMatcher;

public class DocumentPageDimensionsMatcher extends TypeSafeMatcher<PDDocument> {

    private float expectedWidth;
    private float expectedHeight;

    private DocumentPageDimensionsMatcher(float expectedWidth, float expectedHeight) {
        this.expectedWidth = expectedWidth;
        this.expectedHeight = expectedHeight;
    }

    @Override
    public boolean matchesSafely(PDDocument document) {
        for (Object o : document.getDocumentCatalog().getAllPages()) {
            PDPage page = (PDPage) o;
            float width = page.getMediaBox().getWidth();
            float height = page.getMediaBox().getHeight();
            if (compare(expectedWidth, width) != 0 || compare(expectedHeight, height) != 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("Each document page has width of " + expectedWidth + " and height of " + expectedHeight);
    }

    @Factory
    public static DocumentPageDimensionsMatcher dimensionsForPages(float expectedWidth, float expectedHeight) {
        return new DocumentPageDimensionsMatcher(expectedWidth, expectedHeight);
    }
}
