/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.cell;

import static org.apache.poi.ss.usermodel.CellStyle.VERTICAL_BOTTOM;
import static org.apache.poi.ss.usermodel.CellStyle.VERTICAL_CENTER;
import static org.apache.poi.ss.usermodel.CellStyle.VERTICAL_TOP;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.Before;
import org.junit.Test;

public class VerticalOffsetCalculatorTest {

    private VerticalOffsetCalculator calculator;

    @Before
    public void setUp() throws Exception {
        calculator = new VerticalOffsetCalculator();
    }

    @Test
    public void oneRowExactSize() {
        assertThat(calculator.calculate(VERTICAL_BOTTOM, 1, 10, 10, 5), equalTo(0f));
        assertThat(calculator.calculate(VERTICAL_CENTER, 1, 10, 10, 5), equalTo(0f));
        assertThat(calculator.calculate(VERTICAL_TOP, 1, 10, 10, 5), equalTo(0f));
    }

    @Test
    public void oneRowSmallerThanContainer() {
        assertThat(calculator.calculate(VERTICAL_BOTTOM, 1, 100, 10, 5), equalTo(0f));
        assertThat(calculator.calculate(VERTICAL_TOP, 1, 100, 10, 5), equalTo(90f));
        assertThat(calculator.calculate(VERTICAL_CENTER, 1, 100, 10, 5), equalTo(45f));
    }

    @Test
    public void twoRowsExactSize() {
        assertThat(calculator.calculate(VERTICAL_BOTTOM, 2, 25, 10, 5), equalTo(0f));
        assertThat(calculator.calculate(VERTICAL_CENTER, 2, 25, 10, 5), equalTo(0f));
        assertThat(calculator.calculate(VERTICAL_TOP, 2, 25, 10, 5), equalTo(0f));
    }

    @Test
    public void twoRows() {
        assertThat(calculator.calculate(VERTICAL_BOTTOM, 2, 100, 10, 5), equalTo(0f));
        assertThat(calculator.calculate(VERTICAL_TOP, 2, 100, 10, 5), equalTo(75f));
        assertThat(calculator.calculate(VERTICAL_CENTER, 2, 100, 10, 5), equalTo(37.5f));
    }
}
