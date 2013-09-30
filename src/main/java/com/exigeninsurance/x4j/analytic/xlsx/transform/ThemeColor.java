/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform;


public class ThemeColor {

	private int theme;
	private double tint;

    public ThemeColor() {

    }

    public ThemeColor(int theme, double tint) {
        this.theme = theme;
        this.tint = tint;
    }

	public int getTheme() {
		return theme;
	}
	public void setTheme(int theme) {
		this.theme = theme;
	}
	public double getTint() {
		return tint;
	}
	public void setTint(double tint) {
		this.tint = tint;
	}
}
