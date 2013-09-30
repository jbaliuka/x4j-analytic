/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform;


public class XLSXFill {
	
	private ThemeColor fgColor;
	private ThemeColor bgColor;

    public XLSXFill() {

    }

    public XLSXFill(ThemeColor fgColor, ThemeColor bgColor) {
        this.fgColor = fgColor;
        this.bgColor = bgColor;
    }

    public ThemeColor getFgColor() {
		return fgColor;
	}
	public void setFgColor(ThemeColor fgColor) {
		this.fgColor = fgColor;
	}
	public ThemeColor getBgColor() {
		return bgColor;
	}
	public void setBgColor(ThemeColor bgColor) {
		this.bgColor = bgColor;
	}
}
