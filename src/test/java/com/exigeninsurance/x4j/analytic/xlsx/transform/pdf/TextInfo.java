/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf;

import org.apache.pdfbox.pdmodel.font.PDFont;

/**
 * @author astanzys
 *
 */

public class TextInfo {
	
	private String string;
	private float x;
	private float y;
	private float fontSize;
	private float width;
	private float heigth;
	private PDFont font;
	
	public String getString() {
		return string;
	}
	public void setString(String string) {
		this.string = string;
	}
	public float getX() {
		return x;
	}
	public void setX(float x) {
		this.x = x;
	}
	public float getY() {
		return y;
	}
	public void setY(float y) {
		this.y = y;
	}
	public float getFontSize() {
		return fontSize;
	}
	public void setFontSize(float fontSize) {
		this.fontSize = fontSize;
	}
	public float getWidth() {
		return width;
	}
	public void setWidth(float width) {
		this.width = width;
	}
	public PDFont getFont() {
		return font;
	}
	public void setFont(PDFont font) {
		this.font = font;
	}
	public float getHeigth() {
		return heigth;
	}
	public void setHeigth(float heigth) {
		this.heigth = heigth;
	}
}
