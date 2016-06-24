/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/

package com.exigeninsurance.x4j.analytic.xlsx.transform.xml;

import java.io.OutputStream;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFSheet;

import com.exigeninsurance.x4j.analytic.api.ReportContext;
import com.exigeninsurance.x4j.analytic.xlsx.core.node.Node;
import com.exigeninsurance.x4j.analytic.xlsx.transform.SheetParser;
import com.exigeninsurance.x4j.analytic.xlsx.transform.WorkbookProcessor;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLSXWorkbook;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLXContext;
import com.exigeninsurance.x4j.analytic.xlsx.utils.MacroNodeFactoryImpl;
import com.exigeninsurance.x4j.analytic.xlsx.utils.MacroParser;



public class XMLProcessor extends WorkbookProcessor{

	
	private final OutputStream out;
	private final XLSXWorkbook workBook;	

	public XMLProcessor(XLSXWorkbook workBook, OutputStream out) {
		this.out = out;
		
		this.workBook = workBook;
	}

	public void processSheets(	ReportContext reportContext, List<String> savedParts	)
			throws  Exception {
		for(int i = 0; i < workBook.getNumberOfSheets(); i++){
			if(nextSheet(reportContext, i)){
				break;
			}
		}
	}

	private boolean nextSheet(
            ReportContext reportContext,
            int index) throws  Exception {
		XSSFSheet sheet = workBook.getSheetAt(index);

		SheetParser parser = new XMLSheetParser(sheet,reportContext);
		
        parser.setMacroParser(new MacroParser(new MacroNodeFactoryImpl(sheet)));
		Node root = tableNode(parser.parse(sheet));

		if(root != null){
			XLXContext context = new XLXContext(null,sheet,reportContext,out);
			context.setFormatProvider(getFormatProvider());
			context.setStyles();
			context.setDataProvider(getDataProvider());
			context.setTemplateProvider(getTemplateProvider());
			root.process(context);
			context.flush();
			return true;
		}else {
			return false;
		}
	}


	private Node tableNode(Node root) {
		if(root instanceof XMLTableNode){
			return root;
		}else {
		   for(Node n: root.getChildren()){
			   Node next = tableNode(n);
			   if(next instanceof XMLTableNode){
			       return next;
			   }
		   }
		}
		return null;
	}

	
}
