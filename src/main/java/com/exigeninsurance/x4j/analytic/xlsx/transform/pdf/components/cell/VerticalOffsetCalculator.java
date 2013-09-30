/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.cell;

import org.apache.poi.ss.usermodel.CellStyle;


public class VerticalOffsetCalculator {
    public float calculate(short alignment, float rowCount, float totalHeight, float rowHeight,  float margin) {
        float itemHeight = rowHeight * rowCount + margin * (rowCount - 1);
        switch (alignment) {
            case (CellStyle.VERTICAL_CENTER):
                return totalHeight / 2 - itemHeight / 2 ;
            case (CellStyle.VERTICAL_TOP):
                return totalHeight - itemHeight;

            default:
                return 0f;

        }
    }
}
