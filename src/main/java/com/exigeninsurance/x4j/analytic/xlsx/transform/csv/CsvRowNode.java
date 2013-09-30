/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/



package com.exigeninsurance.x4j.analytic.xlsx.transform.csv;

import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFSheet;

import com.exigeninsurance.x4j.analytic.xlsx.core.node.Node;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLXContext;


public class CsvRowNode extends Node {
	
	

	public CsvRowNode(XSSFSheet sheet) {
		super(sheet);
		
	}

	@Override
	public void process(XLXContext context) throws Exception {
		
		List<Node> children = getChildren();
		int length = children.size();
		
		if (length > 0) {
			for (int i = 0; i < length - 1; i++) {
				children.get(i).process(context);
				context.write(",");
			}
			children.get(length - 1).process(context);
			context.write("\n");
		}
	}
}
