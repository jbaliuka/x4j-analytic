/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.cell;

import static org.mockito.Matchers.anyFloat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingParameter;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.Renderer;

public class WrappingCellRendererTest extends CellRendererTestFixture {

    @Before
    public void setup() {
        super.init();

        when(context.getX()).thenReturn(10f);
        when(node.splitCell(anyString(), anyFloat(), anyFloat())).thenAnswer(new Answer<Object>() {
            @Override
            public List<String> answer(InvocationOnMock invocation) throws Throwable {
                String argument = (String) invocation.getArguments()[0];
                if (argument.equals(ONE_ROW)) {
                    return new ArrayList<String>(Arrays.asList(argument));
                } else if (argument.equals(TWO_ROWS)) {
                    return new ArrayList<String>(Arrays.asList("first", "second"));
                } else {
                    return new ArrayList<String>();
                }
            }
        });
    }

    @Test
    public void afterRenderingPointerIsMoved() throws IOException {
		render();
		verify(context).movePointerBy(25f, 0f);
    }

	@Test
    public void renderEmptyString() throws IOException {
		render();
		verify(context, never()).drawText(anyString(),any(Color.class), anyFloat(), anyFloat());
    }

    @Test
    public void renderOneRow() throws IOException {
		render(ONE_ROW);
		verify(context).drawText(ONE_ROW,null, 10f, 0);
    }

	@Test
    public void renderTwoRows() throws IOException  {
		render(TWO_ROWS);
		verify(context).drawText("second",null, 10f, 0);
        verify(context).drawText("first",null,10f, 15f);
    }

    @Override
    protected Renderer createRenderer() {
        return new WrappingCellRenderer(node);
    }

	@Override
	protected Set<RenderingParameter> getDefaultParameters() {
		HashSet<RenderingParameter> params = new HashSet<RenderingParameter>();
		params.add(new RenderingParameter(RenderingParameter.ROW_HEIGHT, 10f));
		return params;
	}
}
