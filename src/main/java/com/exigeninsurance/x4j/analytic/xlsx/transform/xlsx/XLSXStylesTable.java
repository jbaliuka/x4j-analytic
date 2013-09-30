/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx;

import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellBorder;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellFill;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeStyleSheet;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCellStyle;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCellStyles;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDxf;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFill;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFont;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableStyle;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableStyleElement;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableStyles;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTXf;


final public class XLSXStylesTable extends StylesTable {

	private static final String TABLE_HEADER = "tableHeader";
    private static final String HYPERLINK = "hyperlinks";
	private XLSXTheme theme;
	
	
	public XLSXStylesTable(PackagePart part, PackageRelationship rel) throws IOException {
		super(part, rel);
	}


	public CTDxf getDxf(int idx) {
		
		return super.getDxf(idx);
	}

	public int putDxf(CTDxf dxf) {
		
		return super.putDxf(dxf);
	}
	
	@Override
	public void readFrom(InputStream is) throws IOException {		
		super.readFrom(is);
	} 

	public void setDefaultTableStyle(String style){
		getCTStylesheet().getTableStyles().setDefaultTableStyle(style);	

	}

	public String getDefaultTableStyle(){		
		return getCTStylesheet().getTableStyles().getDefaultTableStyle();
	}

	public void setDefaultPivotStyle(String style){
		getCTStylesheet().getTableStyles().setDefaultPivotStyle(style);	

	}

	public String getDefaultPivotStyle(){		
		return getCTStylesheet().getTableStyles().getDefaultPivotStyle();
	}

 
	public int getStyle(String name){
		CTCellStyles cellStyles = getCTStylesheet().getCellStyles();

		for( CTCellStyle item :  cellStyles.getCellStyleArray()){
			if(item.getName().equalsIgnoreCase(name)){
				long id =  item.getXfId();
				for(int i = 0; i < _getXfsSize(); i++){
					if(id == getCellXfAt(i).getXfId()){
						return i;
					}
				}
			}			
		}
		return -1;
	}

	public int getTableHeaderStyle(){		
		return getStyle(TABLE_HEADER);
	}

    public long getHyperlinkStyle() {
        return getStyle(HYPERLINK);
    }

	public void importStyles(XLSXStylesTable styles) {

		importTableStyles(styles);
		importCellStyles(styles);


	}

	private void importCellStyles(XLSXStylesTable styles) {
		
		CTCellStyles cellStyles = styles.getCTStylesheet().getCellStyles();
		CTCellStyles thisCellStyles = getCTStylesheet().getCellStyles();
		int index = (int) (thisCellStyles.getCount());

		thisCellStyles.setCount(index + cellStyles.getCount());

		CTCellStyle[] union = new CTCellStyle[(int) thisCellStyles.getCount()];
		System.arraycopy(thisCellStyles.getCellStyleArray(), 0, union, 0, index);


		for( CTCellStyle item :  cellStyles.getCellStyleArray()){
			union[index] = copy(styles, item);
			index++;
		}
		thisCellStyles.setCellStyleArray(union);

	}

	
	private CTCellStyle copy(XLSXStylesTable styles, CTCellStyle item) {

		item = (CTCellStyle) item.copy();
		copyStyle(styles, item);
		
		return item;
	}

	private CTXf copyStyle(XLSXStylesTable styles, CTCellStyle item) {
		
		int idx = (int) item.getXfId();
		CTXf cellXf = styles.getCellStyleXfAt(idx);
		int newIdx = putCellStyleXf(cellXf) - 1  /*POI BUG*/;		
		item.setXfId(newIdx);
		
		int fontIdx = (int) cellXf.getFontId();
		XSSFFont font = new XSSFFont((CTFont) styles.getFontAt(fontIdx).getCTFont().copy());
		fontIdx = putFont(font);
		cellXf.setFontId(fontIdx);
		
		
		int borderIdx = (int) cellXf.getBorderId();
		XSSFCellBorder border = new XSSFCellBorder(styles.getBorderAt(borderIdx).getCTBorder());
		borderIdx = putBorder(border);
		cellXf.setBorderId(borderIdx);
		
		
		int fillIdx = (int) cellXf.getFillId();
		XSSFCellFill fill = new XSSFCellFill( (CTFill) styles.getFillAt(fillIdx).getCTFill().copy());
		fillIdx = putFill(fill);
		cellXf.setFillId(fillIdx);
		
		int formatIdx = (int) cellXf.getNumFmtId();
		
		if(formatIdx > 0){
			String format = styles.getNumberFormatAt(formatIdx);
			formatIdx = putNumberFormat(format);
			cellXf.setNumFmtId(formatIdx);
		}
		
		for(int i = 0; i < styles._getXfsSize(); i++){
			if(idx == styles.getCellXfAt(i).getXfId()){
				CTXf xfs = (CTXf) styles.getCellXfAt(i).copy();
				putCellXf(xfs);
				xfs.setXfId(newIdx);				
				xfs.setFontId(fontIdx);
				xfs.setBorderId(borderIdx);
				xfs.setFillId(fillIdx);
				xfs.setNumFmtId(formatIdx);
			}
		}
		
		return cellXf;
	}

	

	private void importTableStyles(XLSXStylesTable styles) {

		CTTableStyles tableStyles = styles.getCTStylesheet().getTableStyles();
		CTTableStyles thisTableStyles = getCTStylesheet().getTableStyles();
		thisTableStyles.setDefaultTableStyle(tableStyles.getDefaultTableStyle());
		thisTableStyles.setDefaultPivotStyle(tableStyles.getDefaultPivotStyle());		

		int index = (int) (thisTableStyles.getCount());

		thisTableStyles.setCount(thisTableStyles.getCount() + tableStyles.getCount() );
		CTTableStyle[] union = new CTTableStyle[(int) thisTableStyles.getCount()];
		System.arraycopy(thisTableStyles.getTableStyleArray(), 0, union, 0, index);

		for ( CTTableStyle item : tableStyles.getTableStyleArray() ){			
			union[index] = copyTableElements(styles, item);
			index++;
		}

		thisTableStyles.setTableStyleArray(union);
	}

	private CTTableStyle copyTableElements(XLSXStylesTable styles, CTTableStyle item) {
		item = (CTTableStyle) item.copy();
		for(CTTableStyleElement element : item.getTableStyleElementArray()){
			int dxfId = (int) element.getDxfId();
			CTDxf dxf = styles.getDxf(dxfId);
			dxfId = putDxf(dxf);
			element.setDxfId(dxfId - 1/* POI BUG */);

		}
		return item;
	}


	public void setTheme(XLSXTheme theme) {
		this.theme = theme;
	}


	public CTOfficeStyleSheet getStyleSheet() {
		return theme.getTheme();		
	}
}
