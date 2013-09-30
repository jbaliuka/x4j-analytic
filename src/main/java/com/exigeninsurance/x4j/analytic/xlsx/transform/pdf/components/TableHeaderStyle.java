/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components;

import com.exigeninsurance.x4j.analytic.xlsx.transform.PdfStyle;


public class TableHeaderStyle implements Style {

	public PdfStyle getStyle(com.exigeninsurance.x4j.analytic.xlsx.transform.TableStyle tableStyle) {
		return tableStyle.getHeaderRowStyle();
	}

}
