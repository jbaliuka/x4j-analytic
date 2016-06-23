/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.html;


import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import com.exigeninsurance.x4j.analytic.api.ReportContext;
import com.exigeninsurance.x4j.analytic.xlsx.core.node.Node;
import com.exigeninsurance.x4j.analytic.xlsx.transform.SheetParser;
import com.exigeninsurance.x4j.analytic.xlsx.transform.WorkbookProcessor;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLSXStylesTable;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLSXWorkbook;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLXContext;
import com.exigeninsurance.x4j.analytic.xlsx.utils.MacroNodeFactoryImpl;
import com.exigeninsurance.x4j.analytic.xlsx.utils.MacroParser;


public class HTMLProcessor extends WorkbookProcessor {

	private static final String TEXT_ALIGN_LEFT = "text-align:left;";
	private static final String TEXT_ALIGN_CENTER = "text-align:center;";
	private static final String TEXT_ALIGN_RIGHT = "text-align:right;";
	private static final String TEXT_DECORATION_UNDERLINE = "text-decoration:underline;";
	private static final String FONT_STYLE_ITALIC = "font-style:italic;";
	private static final String FONT_WEIGHT_BOLD = "font-weight:bold;";
	
    
	private final OutputStream out;
	private final XLSXWorkbook workBook;
	private final XLSXStylesTable styles;
	

	public HTMLProcessor(XLSXWorkbook workBook,  OutputStream out) {
		this.out = out;
		this.styles = (XLSXStylesTable) workBook.getStylesSource();	
		this.workBook = workBook;
	}

	public void processSheets(	ReportContext reportContext, List<String> savedParts	)
	throws  Exception {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<style>");

		int len =  styles._getXfsSize();
		for ( int i = 0; i < len ; i++ ){
			appendStyle(buffer, i);
		}

		buffer.append("</style>");

		out.write( "<html>\n<head>\n".getBytes(StandardCharsets.UTF_8) );
		out.write( buffer.toString().getBytes(StandardCharsets.UTF_8) );

		out.write("\n</head>\n<body>\n<table id=\"".getBytes(StandardCharsets.UTF_8));
		out.write( "workbook".getBytes(StandardCharsets.UTF_8) );
		out.write("\"><tr><td>\n".getBytes(StandardCharsets.UTF_8));

		for(int i = 0; i < workBook.getNumberOfSheets(); i++){
			nextSheet(reportContext, i);
		}
		out.write("</td></tr></table>\n</body>\n</html>".getBytes());
	}

	private void nextSheet(
            ReportContext reportContext, int index) throws  Exception {
		XSSFSheet sheet = workBook.getSheetAt(index);

		SheetParser parser = new HTMLSheetParser(sheet,reportContext);
		
        parser.setMacroParser(new MacroParser(sheet, new MacroNodeFactoryImpl(sheet)));
		Node root = parser.parse(sheet);

		XLXContext context = new XLXContext(null,sheet,reportContext,out);
		
		context.setDataProvider(getDataProvider());
		context.setTemplateProvider(getTemplateProvider());
		context.setFormatProvider(getFormatProvider());
		context.setStyles(styles);
		context.parseTableStyles(workBook);

		root.process(context);
		context.flush();
	}

	private void appendStyle(StringBuffer buffer, int i) {
		buffer.append(".c").append(i).append("{");
		XSSFCellStyle style  = styles.getStyleAt(i);
		appendFontStyle(buffer, style);
		appendAlign(buffer, style);
		buffer.append("}\n");
	}

	private void appendAlign(StringBuffer buffer, XSSFCellStyle style) {
		switch(style.getAlignment()){
		case CellStyle.ALIGN_GENERAL:
			break;
		case CellStyle.ALIGN_LEFT:
			buffer.append(TEXT_ALIGN_LEFT);
			break;
		case CellStyle.ALIGN_CENTER:
			buffer.append(TEXT_ALIGN_CENTER);
			break;
		case CellStyle.ALIGN_RIGHT:
			buffer.append(TEXT_ALIGN_RIGHT);
			break;
		}
	}

	private void appendFontStyle(StringBuffer buffer, XSSFCellStyle style) {
		XSSFFont font = style.getFont();
		buffer.append("font-size:").append(font.getFontHeightInPoints()).append("pt;");

		if(font.getBold() || font.getBoldweight() ==  Font.BOLDWEIGHT_BOLD){
			buffer.append( FONT_WEIGHT_BOLD );
		}
		if(font.getItalic()){
			buffer.append( FONT_STYLE_ITALIC );
		}
		if(font.getUnderline() != Font.U_NONE){
			buffer.append( TEXT_DECORATION_UNDERLINE );				 
		}
	}

	

}
