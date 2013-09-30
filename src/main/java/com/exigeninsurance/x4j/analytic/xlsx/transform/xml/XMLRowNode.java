/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.xml;

import org.apache.poi.xssf.usermodel.XSSFSheet;

import com.exigeninsurance.x4j.analytic.xlsx.core.node.Node;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLXContext;


public class XMLRowNode extends Node {

	public XMLRowNode(XSSFSheet sheet) {
		super(sheet);
	}

	
	@Override
	public void process(XLXContext context) throws Exception {
		context.write("\t<row>\n");
		super.process(context);
		context.write("\t</row>\n");
	}
	
}
