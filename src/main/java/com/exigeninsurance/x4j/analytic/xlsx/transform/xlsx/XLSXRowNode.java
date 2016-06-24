/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import com.exigeninsurance.x4j.analytic.xlsx.core.node.Node;

import java.nio.charset.StandardCharsets;


public final class XLSXRowNode extends Node {

	private static final byte QT = '\"';

	private static final byte GT = '>';

	private static final byte[] ROW_HEAD = "<row r=\"".getBytes(StandardCharsets.UTF_8);

	private static final byte[] ROW_TAIL = "</row>".getBytes(StandardCharsets.UTF_8);

	private static final byte[] SPANS = " spans=\"".getBytes(StandardCharsets.UTF_8);

	private byte[] spans;

	private final XSSFRow row;

	public XLSXRowNode(XSSFSheet sheet, XSSFRow row) {
		super(sheet);		
		this.row = row; 
		int spanCnt =  row.getCTRow().getSpans() == null ? 0 : row.getCTRow().getSpans().size();
		if(spanCnt > 0){
			StringBuilder buffer = new StringBuilder();
			for(Object span : row.getCTRow().getSpans()){
				spanCnt--;
				buffer.append( span );
				if(spanCnt > 0){
					buffer.append(' ');
				}

			}
			spans = buffer.toString().getBytes(StandardCharsets.UTF_8);
		}
	}


	@Override
	public void process(XLXContext context) throws Exception {

		if(!getChildren().isEmpty()){
			
			context.write(ROW_HEAD);
			context.write(context.getCurrentRow() + 1);
			context.write(QT);


			if(spans != null){
				context.write(SPANS);				
				context.write(spans);
				context.write(QT);
			}

			context.write(GT);
			super.process(context);
			context.write(ROW_TAIL);
			context.nextRow(row.getRowNum());

		}
	}


	public XSSFRow getRow() {
		return row;
	}

}
