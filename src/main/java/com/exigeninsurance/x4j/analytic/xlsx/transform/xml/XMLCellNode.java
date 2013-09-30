/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.xml;

import java.util.Date;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import com.exigeninsurance.x4j.analytic.model.Link;
import com.exigeninsurance.x4j.analytic.model.Money;
import com.exigeninsurance.x4j.analytic.xlsx.core.expression.XLSXExpression;
import com.exigeninsurance.x4j.analytic.xlsx.core.node.CellNode;
import com.exigeninsurance.x4j.analytic.xlsx.transform.SST;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLXContext;


public class XMLCellNode extends CellNode {



	private String element;



	public XMLCellNode(XSSFSheet sheet,XSSFCell cell,XLSXExpression expr) {
		super(sheet,cell,expr);		
	}

	public void setElement(String element) {
		this.element = element;

	}
	
	public String format(XLXContext context,Object value) {
		
		if (value instanceof Date) {
			return context.defaultDateFormat((Date)value);
		}
		if (value instanceof Money) {
			return ((Money) value).getValue().toString();
		}
		if (value instanceof Link) {
			return ((Link) value).getLabel();
		}

		return value.toString();
	}

	@Override
	public void process(XLXContext context) throws Exception {
  
		Object val = expression.evaluate(context);
		if(val != null){
			context.write("\t\t<");
			context.write(element);
			context.write(">");
			context.write(SST.forXML(format(context,val)));
			context.write("</");
			context.write(element);
			context.write(">\n");
		}


	}



}
