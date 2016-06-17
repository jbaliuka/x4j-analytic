/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.html;

import java.awt.Color;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeStyleSheet;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCell;

import com.exigeninsurance.x4j.analytic.model.Link;
import com.exigeninsurance.x4j.analytic.model.Money;
import com.exigeninsurance.x4j.analytic.xlsx.core.expression.XLSXExpression;
import com.exigeninsurance.x4j.analytic.xlsx.core.node.CellNode;
import com.exigeninsurance.x4j.analytic.xlsx.transform.SST;
import com.exigeninsurance.x4j.analytic.xlsx.transform.TableStyle;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLSXStylesTable;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLXContext;
import com.exigeninsurance.x4j.analytic.xlsx.utils.ColorHelper;


class HTMLCellNode extends CellNode {
	
	private static final String ANCHOR_AND_LINK_OPEN = "<a href=\"";
	private static final String ANCHOR_CLOSE = "</a>";
	private static final String LINK_CLOSE = "\">";

	private XSSFCell cell;
    private long s;
	private boolean Sset;

	public HTMLCellNode(XSSFSheet sheet, XSSFCell cell, XLSXExpression expression) {
		super(sheet,cell,expression);	
		assert cell != null : "null cell" ;
        setCell(cell);
        CTCell ctCell = cell.getCTCell();
		s = ctCell.getS();
		Sset = ctCell.isSetS();
		
	}

	@Override
	public void process(XLXContext context) throws Exception {
		Object value = expression.evaluate(context);
        writeStyle(context);
        writeValue(context, value);

	}

    private void writeValue(XLXContext context, Object value) throws IOException {
        if (value != null) {
            if (value instanceof Money) {
                context.write(context.formatMoney((Money) value));
            }
            else if (value instanceof Number){
                Money money = new Money(null, BigDecimal.valueOf(((Number) value).doubleValue()));
                context.write(context.formatMoney(money));
            }
            else if (value instanceof Date) {
            	if (value instanceof Timestamp) {
            		context.write( context.dateTimeFormat((Date) value));	
            	} else {
            		context.write(context.defaultDateFormat((Date) value));
            	}
            } else if (value instanceof Link) {
                context.write(createHyperLink((Link) value));
            }
            else {
                context.write(value.toString());
            }
        }

        context.write("</td>");
    }

    private void writeStyle(XLXContext context) throws IOException {
        long style = Sset ? s : -1 ;
        String fill = getFill(context);

        String id = CellReference.convertNumToColString(getCell().getColumnIndex()) +
                 Long.toString(context.getCurrentRow() + 1 )  ;

        if(style > 0){
            context.write("<td "+ ( getAxis() != null ? "axis=\""
                    + getAxis() + "\"" : "")  +
                    " id=" + id + " bgcolor=" + fill + " class='c" + style + "'>");
        }else {
            context.write("<td " + ( getAxis() != null ? "axis=\""
                    + getAxis() + "\"" : "")  +
                    " id=" + id + " bgcolor=" + fill + ">");
        }
    }

    private String createHyperLink(Link link) {
		StringBuilder builder = new StringBuilder();
		builder.append(ANCHOR_AND_LINK_OPEN);
		builder.append(SST.forXML(link.getUrl()));
		builder.append(LINK_CLOSE);
		builder.append(link.getLabel());
		builder.append(ANCHOR_CLOSE);
		return builder.toString();
	}
	
	public String getFill(XLXContext context) {
		String fill = DEFAULT_FILL;
        TableStyle tableStyle = context.findTableStyle(context.getTableId());
        if (tableStyle != null) {
            fill = getFill(context.findStyle(tableStyle, this));
        }
		else {
            XSSFColor xssfColor = ColorHelper.getColorFromStylesSource(cell);
            if (xssfColor != null) {
                Color color = getColorHelper().getAwtColor(xssfColor);
                fill = ColorHelper.awtColorToHex(color);
            }
		}
		return fill;
	}


	public void setCell(XSSFCell cell) {
		this.cell = cell;
	}

	public XSSFCell getCell() {
		return cell;
	}
	

}
