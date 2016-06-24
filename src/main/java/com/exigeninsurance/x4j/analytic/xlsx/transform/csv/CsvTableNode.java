/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.csv;

import org.apache.poi.xssf.model.Table;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import com.exigeninsurance.x4j.analytic.xlsx.core.node.TableNode;


public class CsvTableNode extends TableNode {

	public CsvTableNode(XSSFSheet sheet, Table table) {
		super(sheet, table);

	}

}
