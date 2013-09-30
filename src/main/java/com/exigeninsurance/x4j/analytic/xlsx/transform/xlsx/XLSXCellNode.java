/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorkbookPr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STCellType;

import com.exigeninsurance.x4j.analytic.model.Link;
import com.exigeninsurance.x4j.analytic.model.Money;
import com.exigeninsurance.x4j.analytic.xlsx.core.expression.TotalsExpression;
import com.exigeninsurance.x4j.analytic.xlsx.core.expression.XLSXExpression;
import com.exigeninsurance.x4j.analytic.xlsx.core.node.CellNode;
import com.exigeninsurance.x4j.analytic.xlsx.transform.MergedRegion;
import com.exigeninsurance.x4j.analytic.xlsx.transform.SST;
import com.exigeninsurance.x4j.analytic.xlsx.utils.CellExpressionParser;
import com.exigeninsurance.x4j.analytic.xlsx.utils.WrappingUtil;


public final class XLSXCellNode extends CellNode {

	private static final byte[] F_END = "</f>".getBytes();
	private static final byte[] F = "<f>".getBytes();
	private static final byte[] C_R = "<c r=\"".getBytes();
	private static final byte[] C_TAIL = "</c>".getBytes();
	private static final byte[] S = " s=\"".getBytes();
	private static final byte[] T = " t=\"".getBytes();
	private static final byte[] V = "<v>".getBytes();
	private static final byte[] V_TAIL = "</v>".getBytes();	
	private static final byte QT = '\"';
	private static final byte GT = '>';
	private String formulaStringValue;
	private XLSXExpression formulaExpression;
	private long s = -1;
	private CTWorkbookPr workbookPr;
	private String colRef;
    private String absoluteRef;
	private XSSFComment comment ;
	private XLSXExpression commnetExpr;
	private boolean isTableNode;

	private String templateDate;
	private String templateDateTime;

	public XLSXCellNode(XSSFSheet sheet,XSSFCell cell,int index,XLSXExpression expression) {
		super(sheet,cell,expression);	
		assert cell != null : "null cell" ;

		if(cell.getCTCell().isSetF()){
			formulaStringValue = cell.getCTCell().getF().getStringValue();
		}
		colRef = CellReference.convertNumToColString(index);
        int rowRef = getCell().getRowIndex() + 1;
		absoluteRef = colRef + rowRef;

		if(cell.getCTCell().isSetS()){
			s = cell.getCTCell().getS();
		}
		workbookPr = getSheet().getWorkbook().getCTWorkbook().getWorkbookPr();
		comment = cell.getCellComment();
		if(comment != null){
			commnetExpr = CellExpressionParser.parseExpression(comment.getString().getString());
		}
	}

	@Override
	public void process(XLXContext context) throws Exception {
		Object value = expression.evaluate(context);
		if(value != null){
			writeRef(context);
			writeStyle(context, value);
			writeType(context, value);			
			writeFormula(context, value);			
			writeValue(context, value);
		}

		if(comment != null){
			Object cmt = commnetExpr.evaluate(context);		
			comment.setString(cmt == null ? "" :cmt.toString());
		}		

		if (isMerged(context)) {
			writeMergedRegion(context);
		}
	}

	private String createLinkFormula(Link link) {
		return new StringBuilder().append("HYPERLINK(\"")
				.append(link.getUrl())
				.append("\",\"")
				.append(link.getLabel())
				.append("\")").toString();
	}

	private void writeMergedRegion(XLXContext context) {
		MergedRegion region = context.getMergedRegion(getCell());
		context.getNewMergedCells().add(region.derive(context.getCurrentRow() + 1));
	}

	private boolean isMerged(XLXContext context) {
		return context.isCellMerged(absoluteRef);
	}

	private void writeRef(XLXContext context) throws IOException {
		context.write(C_R);
		context.write(colRef);
		context.write( Long.toString(context.getCurrentRow() + 1 )  );
		context.write(QT);
	}

	private void writeValue(XLXContext context, Object value)
			throws IOException {
		context.write(V);
		context.write(getV(context, value));
		context.write(V_TAIL);
		context.write(C_TAIL);
		if (isTableNode) {
			context.reportCellWidth(cell, formatValue(context, value));
		}
	}

	private String formatValue(XLXContext context, Object value) {
		if (value instanceof Money) {
			return context.formatMoney((Money) value);
		} else if (value instanceof Number) {
			Money money = new Money(null, new BigDecimal(((Number) value).doubleValue()));
			return context.formatMoney(money);
		} else if (value instanceof Date) {
			if (value instanceof Timestamp) {
				return formatDateTime(context, (Date) value);
			}
			return formatDate(context, (Date) value);
		} else if (value instanceof Link) {
			if(((Link) value).getLabel() == null){
				return "";
			}
			return ((Link) value).getLabel();
		} else {
			return  value == null ? "" : value.toString();
		}
	}

	private String formatDateTime(XLXContext context, Date value) {
		if (templateDateTime == null) {
			templateDateTime = context.dateTimeFormat(value);
		}
		return templateDateTime;
	}

	private String formatDate(XLXContext context, Date value) {
		if (templateDate == null) {
			templateDate = context.defaultDateFormat(value);
		}
		return templateDate;
	}

	private void writeType(XLXContext context, Object value) throws IOException {
		context.write(T);
		context.write(getT(context,value));
		context.write(QT);		
		context.write(GT);
	}

	private void writeStyle(XLXContext context, Object value)
			throws IOException {
		long  style = context.createStyle(value, this);

		if(style >= 0){
			context.write(S);
			context.write(style);
			context.write(QT);
		}
	}

	private void writeFormula(XLXContext context, Object value) throws Exception {
		if (value instanceof Link) {
			formulaStringValue = createLinkFormula((Link) value);
		}

		if(formulaStringValue != null){
			if(formulaExpression == null){
                if (expression instanceof TotalsExpression) {
                    setAxis(((TotalsExpression) expression).getTemplateCellNode().getAxis());
                    XLSXExpression axisExpr = CellExpressionParser.parseExpression(getAxis());
                    String axisValue = WrappingUtil.excelWrap(WrappingUtil.wrap((String) axisExpr.evaluate(context)));
                    formulaStringValue = formulaStringValue.replace(getAxis(),axisValue);
                }
                formulaExpression = CellExpressionParser.parseExpression(formulaStringValue);
				formulaStringValue = (String) formulaExpression.evaluate(context);
			}

			context.write(F);
			context.write(SST.forXML(formulaStringValue.trim()));
			context.write(F_END);
		}
	}

	private String getT(XLXContext context,Object value) {
		if(value == null){
			return STCellType.N.toString();

		} else if (value instanceof Link) {
			return "str";
		}else if(value instanceof Date){
			return STCellType.N.toString();
		}else if (value instanceof Money){
			return STCellType.N.toString();
		}else if (value instanceof Number){
			return STCellType.N.toString();
		}else if (value instanceof XLSXErrorValue){
			return STCellType.E.toString();
		}
		return context.getSst() == null ? STCellType.INLINE_STR.toString() :  STCellType.S.toString();
	}

	private Object getV(XLXContext context,Object value) throws IOException {
		if(value == XLSXErrorValue.INSATNCE){
			return "#VALUE!";
		}

		if (value instanceof Money){
			return ((Money)value).getValue().doubleValue();
		}

		else if (value instanceof Number){
			return value.toString();
		}else if (value instanceof Date){
			boolean date1904 =  workbookPr != null && workbookPr.getDate1904();
			return String.valueOf(DateUtil.getExcelDate((Date) value, date1904 ));
		} else if (value instanceof Link) {
			return ((Link) value).getLabel();
		}

		if(context.getSst() == null || cell.getCellType() == XSSFCell.CELL_TYPE_FORMULA){
			return SST.forXML(value.toString().trim());
		}else {
			return context.getSst().add(value.toString().trim());
		}
	}

	public void setTableNode(boolean tableNode) {
		isTableNode = tableNode;
	}

	public long getStyle() {
		return s;
	}

	public boolean hasStyleSet() {
		return getStyle() != -1;
	}
}
