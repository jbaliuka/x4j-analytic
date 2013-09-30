/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.TypeSafeMatcher;

public class DocumentPageCountMatcher extends TypeSafeMatcher<PDDocument> {

    private int expectedPages;

    private DocumentPageCountMatcher(int expectedPages) {
        this.expectedPages = expectedPages;
    }

    @Override
    public boolean matchesSafely(PDDocument document) {
        return document.getDocumentCatalog().getAllPages().size() == expectedPages;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("page contains " + expectedPages + " pages");
    }

    @Factory
    public static DocumentPageCountMatcher pageCount(int expectedPages) {
        return new DocumentPageCountMatcher(expectedPages);
    }
}
