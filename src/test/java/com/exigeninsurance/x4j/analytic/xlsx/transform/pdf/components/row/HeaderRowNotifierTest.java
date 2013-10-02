/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.row;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfContainer;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfContext;

public class HeaderRowNotifierTest {

    @Mock private PdfContext context;
    @Mock private PdfContainer row;

    private HeaderRowNotifier notifier;

    @Before
    public void setup() {
        initMocks(this);
        notifier = new HeaderRowNotifier(row);
        when(row.getTableId()).thenReturn(1);
    }

    @Test
    public void testNotifyOnSameTable() {
        when(context.getTableId()).thenReturn(1);
        notifier.notify(context);
        verify(context).repeat(row);
    }

    @Test
    public void testNotifyOnDifferentTable() {
        when(context.getTableId()).thenReturn(2);
        notifier.notify(context);
        verify(context, never()).repeat(row);
    }
}
