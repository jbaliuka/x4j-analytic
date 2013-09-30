
/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/

package com.exigeninsurance.x4j.analytic.xlsx.core.node;

import org.apache.poi.xssf.usermodel.XSSFSheet;

import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLXContext;


final public class EmptyNode extends Node {

	public EmptyNode(XSSFSheet sheet) {
		super(sheet);	
	}
	
	@Override
	public void process(XLXContext context) throws Exception {
		
		context.setCurrentRow(context.getCurrentRow() + 1);
		
	}

}
