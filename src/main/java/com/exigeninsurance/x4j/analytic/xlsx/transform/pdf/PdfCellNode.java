/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf;

import java.awt.Color;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import com.exigeninsurance.x4j.analytic.api.ReportException;
import com.exigeninsurance.x4j.analytic.model.Link;
import com.exigeninsurance.x4j.analytic.model.Money;
import com.exigeninsurance.x4j.analytic.xlsx.core.expression.XLSXExpression;
import com.exigeninsurance.x4j.analytic.xlsx.core.node.CellNode;
import com.exigeninsurance.x4j.analytic.xlsx.transform.MergedRegion;
import com.exigeninsurance.x4j.analytic.xlsx.transform.PdfStyle;
import com.exigeninsurance.x4j.analytic.xlsx.transform.TableStyle;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.Alignment;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.Estimator;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.Notifier;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.PDFHelper;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.Processor;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.Renderer;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.Style;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.geometry.Range;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.geometry.SimpleRange;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLXContext;
import com.exigeninsurance.x4j.analytic.xlsx.utils.ColorHelper;
import com.exigeninsurance.x4j.analytic.xlsx.utils.WrappingUtil;



public class PdfCellNode extends CellNode implements DrawablePdfElement, PdfGridElement {
	
	private static final String NON_BREAKING_SPACE = "\u00A0";
	
	private PdfContainer parent;

    private int excelWidth;
	private float rowHeigth;
	private Color fillColor;	
    private Alignment horizontalAlignment;
    private short verticalAlignment;
	
	private Estimator heigthEstimator;
	private Estimator widthEstimator;
	private Processor processor;
	private Renderer renderer;
	private Notifier notifier;
	private Style stylingComponent;

	private final String colRef;
    private String absoluteRef;
	private final Range verticalRange;
	
	

	public PdfCellNode(XSSFSheet sheet, XSSFCell cell, XLSXExpression expr) {
		super(sheet, cell, expr);
		excelWidth = sheet.getColumnWidth(cell.getColumnIndex());
        colRef = CellReference.convertNumToColString(cell.getColumnIndex());
		int rowIndex = getCell().getRowIndex();
        int rowRef = rowIndex + 1;
		absoluteRef = colRef + rowRef;
		verticalRange = new SimpleRange(rowIndex, rowIndex);
        horizontalAlignment = Alignment.LEFT;
        verticalAlignment = getCell().getCellStyle().getVerticalAlignment();
	}

	@Override
	public void process(XLXContext context) throws Exception {
		processor.process(context);
	}
	
	public void draw(RenderingContext context) throws Exception {
		renderer.render(context);
	}

	public float estimateWidth(RenderingContext context) {
		return widthEstimator.estimate(context);
	}

	public float estimateHeight(RenderingContext context) {
		return heigthEstimator.estimate(context);
	}

	public void notify(PdfContext context) {
		notifier.notify(context);
	}
	
	public int findFontSize() {
		return getCell().getCellStyle().getFont().getFontHeightInPoints();
	}
	
	public float getMaxFontHeight() {
		return findMaxFontHeight(getFont(), getCell().getCellStyle().getFont().getFontHeightInPoints());
	}
	
	private float findMaxFontHeight(PDFont font, short fontSize) {
		return PDFHelper.findMaxFontHeigth(font, fontSize);
	}
	
	public String formatValue(XLXContext context, Object value) {
		if (value instanceof Money) {
			return context.formatMoney((Money) value).replaceAll(NON_BREAKING_SPACE, " ");
		} else if (value instanceof Number) {
			Money money = new Money(null, BigDecimal.valueOf(((Number) value).doubleValue()));
			return context.formatMoney(money).replaceAll(NON_BREAKING_SPACE," ");
		} else if (value instanceof Date) {
			if (value instanceof Timestamp) {
				return context.dateTimeFormat((Date) value).replaceAll(NON_BREAKING_SPACE," ");
			}
			return context.defaultDateFormat((Date) value).replaceAll(NON_BREAKING_SPACE," ");
		} else if (value instanceof Link) {
			if(((Link) value).getLabel() == null){
				return "";
			}
			return ((Link) value).getLabel().replaceAll(NON_BREAKING_SPACE, " ");
		} else {
			return  value == null ? "" : value.toString();
		}
	}

    public void determineAndSetAlignment(PdfContext context, Object value) {
        horizontalAlignment = context.determineAlignment(value, cell);
    }

	public List <String> splitCell(String value, float width, float margin) {
		return WrappingUtil.pdfWrap(value, this, width - margin);
	}
	
	 public boolean isWrapped() {
	        return cell.getCellStyle().getWrapText();
	 }
	
	public float findTextLength(String value) {
		
		short fontHeightInPoints = getCell().getCellStyle().getFont().getFontHeightInPoints();

		float length;
		try {
			length = getFont().getStringWidth(value) / 1000 * fontHeightInPoints;
		} catch (IOException e) {
			throw new ReportException(e);
		}

		return length;
	}

    public Color getColor(TableStyle tableStyle) {
        if (tableStyle != null) {
            Color tableColor = getTableColor(tableStyle);
            return tableColor != null ? tableColor : Color.white;
        }
        else {
            XSSFColor xssfColor = ColorHelper.getColorFromStylesSource(cell);
            return xssfColor != null ? getColorHelper().getAwtColor(xssfColor) : Color.white;
        }
    }

    private Color getTableColor(TableStyle tableStyle) {
        if (tableStyle != null) {
            PdfStyle style = stylingComponent.getStyle(tableStyle);
            if (style != null) {
                int theme = style.getFill().getBgColor().getTheme();
                double tint = style.getFill().getBgColor().getTint();
                return getColorHelper().getAwtColor(theme, tint);
            }
        }
        return null;
    }
	
	public boolean isMerged(XLXContext context) {
		return context.isCellMerged(absoluteRef);
	}
	
	public MergedRegion getMergedRegion(XLXContext context) {
		return context.getMergedRegion(getCell());
	}

	public void setHeigthEstimator(Estimator heigthEstimator) {
		this.heigthEstimator = heigthEstimator;
	}

	public void setWidthEstimator(Estimator widthEstimator) {
		this.widthEstimator = widthEstimator;
	}

	public void setProcessor(Processor processor) {
		this.processor = processor;
	}

	public void setRenderer(Renderer renderer) {
		this.renderer = renderer;
	}

	public int getCellWidthInExcelPoints() {
		return excelWidth;
	}

	public void setRowHeigth(float rowHeigth) {
		this.rowHeigth = rowHeigth;
	}

	public Color getFillColor() {
		return fillColor;
	}

	public void setFillColor(Color fillColor) {
		this.fillColor = fillColor;
	}

	public void setStylingComponent(Style stylingComponent) {
		this.stylingComponent = stylingComponent;
	}

	public void setNotifier(Notifier notifier) {
		this.notifier = notifier;
	}

	@Override
	public void setParent(PdfContainer element) {
		parent = element;
		
	}

	@Override
	public PdfContainer getParent() {
		return parent;
	}

	@Override
	public float getHeigth() {
		return parent.getHeigth();
	}

	@Override
	public Range getVerticalRange() {
		return verticalRange;
	}

    public Alignment getHorizontalAlignment() {
        return horizontalAlignment;
    }

    public short getVerticalAlignment() {
        return verticalAlignment;
    }

    public PDFont getFont() {
        return PDFHelper.findFont(getCell().getCellStyle().getFont());
    }

    public String getColRef() {
        return colRef;
    }

    public Estimator getWidthEstimator() {
        return widthEstimator;
    }

	public byte getUnderline() {
		return cell.getCellStyle().getFont().getUnderline();
	}

	public Estimator getHeigthEstimator() {
		return heigthEstimator;
	}

	public Processor getProcessor() {
		return processor;
	}

	public Renderer getRenderer() {
		return renderer;
	}

	public Style getStylingComponent() {
		return stylingComponent;
	}

	public float getRowHeight() {
		return rowHeigth;
	}

	public Color getTextColor() {
		XSSFColor color = getCell().getCellStyle().getFont().getXSSFColor();		
		return color == null ? Color.BLACK : getColorHelper().getAwtColor(color);
	}

	public String getAbsoluteRef() {
		return absoluteRef;
	}

	
}
