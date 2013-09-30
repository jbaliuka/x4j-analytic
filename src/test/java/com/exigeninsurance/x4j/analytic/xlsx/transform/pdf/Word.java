/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf;
import java.util.ArrayList;
import java.util.List;

public class Word {
	private List <TextInfo> elements = new ArrayList<TextInfo>();
	private float width;
	private float x;
	private float y;
	private float heigth;
	
	public Word(TextInfo element) {
		addElement(element);
		x = element.getX();
		y = element.getY();
	}
	
	public Word() {
		
	}
	
	public void addElement(TextInfo element) {
		if (elements.isEmpty()) {
			x = element.getX();
			y = element.getY();
		}
		elements.add(element);
		width += element.getWidth();
		if (element.getHeigth() > heigth) {
			heigth = element.getHeigth();
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (TextInfo element : elements) {
			sb.append(element.getString());
		}
		return sb.toString();
	}
	 
	public List<TextInfo> getElements() {
		return elements;
	}
	public void setElements(List<TextInfo> elements) {
		this.elements = elements;
	}
	public float getWidth() {
		return width;
	}
	public void setWidth(float width) {
		this.width = width;
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
	public float getHeigth() {
		return heigth;
	}
	public void setHeigth(float heigth) {
		this.heigth = heigth;
	}
	 
	 
}
