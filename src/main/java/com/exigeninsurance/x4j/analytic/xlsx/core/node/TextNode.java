/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.core.node;

import org.apache.poi.xssf.usermodel.XSSFSheet;

import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLXContext;

public final class TextNode extends Node {

	private final String text;

	public TextNode(XSSFSheet sheet,String text) {
		super(sheet);
		this.text = text;
	}
	
	@Override
	public void process(XLXContext context) throws Exception {
	
		context.write(text);
		
	}

	public String getText() {		
		return text;
	}

}
