/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import com.exigeninsurance.x4j.analytic.xlsx.core.expression.ConstantExpression;
import com.exigeninsurance.x4j.analytic.xlsx.core.expression.XLSXExpression;
import com.exigeninsurance.x4j.analytic.xlsx.transform.Picture;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.Constant;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.NoOpNotifier;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.NoOpRenderer;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.NoStyle;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.PictureProcessor;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.TableHeaderStyle;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.TableStyle;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.cell.BasicCellHeigthEstimator;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.cell.BasicPdfCellProcessor;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.cell.ColumnWidth;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.cell.EmptyCellRenderer;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.cell.ExpandedColumn;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.cell.Margins;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.cell.PdfTableCellRenderer;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.cell.WrappingCellHeigthEstimator;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.cell.WrappingCellRenderer;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.cell.WrappingCellWidthEstimator;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.row.DefaultRowProcessor;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.row.DefaultRowRenderer;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.row.HeaderRowNotifier;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.row.HorizontalContainerHeigthEstimator;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.row.HorizontalContainerWidthEstimator;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.row.TableRowProcessor;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.row.VerticalContainerHeigthEstimator;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.row.VerticalContainerProcessor;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.row.VerticalContainerWidthEstimator;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.geometry.Range;


public class ComponentFactoryImpl implements ComponentFactory {

	public PdfCellNode createHeaderCell(XSSFSheet xssfSheet, XSSFCell cell, XLSXExpression expr) {
		return createWrappedCell(xssfSheet, cell, expr);
	}

	@Override
	public PdfCellNode createWrappedCell(XSSFSheet sheet, XSSFCell cell, XLSXExpression expr) {
		PdfCellNode cellNode = new PdfCellNode(sheet, cell, expr);
		cellNode.setProcessor(new BasicPdfCellProcessor(cellNode));
		cellNode.setStylingComponent(new TableHeaderStyle());
		cellNode.setNotifier(new NoOpNotifier());
		makeCellWrappable(cellNode);
		return cellNode;
	}

	@Override
	public void makeCellWrappable(PdfCellNode cellNode) {
		cellNode.setWidthEstimator(new Margins(new ExpandedColumn(cellNode, new WrappingCellWidthEstimator(cellNode, new ColumnWidth(cellNode)))));
		cellNode.setHeigthEstimator(new WrappingCellHeigthEstimator(cellNode));
		cellNode.setRenderer(new WrappingCellRenderer(cellNode));
	}

	public PdfCellNode createTableCell(XSSFSheet xssfSheet, XSSFCell cell, XLSXExpression expr) {
        return createCell(xssfSheet, cell, expr);
    }

    @Override
    public PdfCellNode createCell(XSSFSheet xssfSheet, XSSFCell cell, XLSXExpression expr) {
        PdfCellNode cellNode = new PdfCellNode(xssfSheet, cell, expr);
        cellNode.setProcessor(new BasicPdfCellProcessor(cellNode));
        cellNode.setHeigthEstimator(new BasicCellHeigthEstimator(cellNode));
        cellNode.setWidthEstimator(createCellWidthEstimator(cellNode));
        cellNode.setRenderer(new PdfTableCellRenderer(cellNode));
        cellNode.setStylingComponent(new TableStyle());
        cellNode.setNotifier(new NoOpNotifier());
        return cellNode;
    }

    private Margins createCellWidthEstimator(PdfCellNode cellNode) {
        return new Margins(new ExpandedColumn(cellNode, new ColumnWidth(cellNode)));
    }

    public PdfCellNode createEmptyCell(XSSFSheet xssfSheet, XSSFCell cell) {
        PdfCellNode cellNode = new PdfCellNode(xssfSheet, cell, new ConstantExpression(""));
        cellNode.setProcessor(new BasicPdfCellProcessor(cellNode));
        cellNode.setHeigthEstimator(new BasicCellHeigthEstimator(cellNode));
        cellNode.setWidthEstimator(createCellWidthEstimator(cellNode));
        cellNode.setRenderer(new EmptyCellRenderer(cellNode));
        cellNode.setStylingComponent(new NoStyle());
        cellNode.setNotifier(new NoOpNotifier());
        return cellNode;
    }

	public PdfContainer createEmptyRow(XSSFSheet sheet, Range verticalRange) {
		PdfContainer node = new PdfContainer(sheet, verticalRange);
		node.setHeigthEstimator(new Constant(sheet.getDefaultRowHeightInPoints()));
		node.setWidthEstimator(new Constant(0f));
		node.setProcessor(new DefaultRowProcessor(node));
		node.setRenderer(new DefaultRowRenderer(node));
		node.setNotifier(new NoOpNotifier());
		return node;
	}

    @Override
    public PdfPictureNode createPictureNode(XSSFSheet sheet, Picture picture) {
        PdfPictureNode node = new PdfPictureNode(sheet, picture);
        node.setProcessor(new PictureProcessor(node));
        return node;
    }

	public PdfContainer createTableRow(XSSFSheet sheet, Range verticalRange) {
		PdfContainer node = new PdfContainer(sheet, verticalRange);
		node.setHeigthEstimator(new HorizontalContainerHeigthEstimator(node));
		node.setWidthEstimator(new HorizontalContainerWidthEstimator(node));
		node.setProcessor(new TableRowProcessor(node));
		node.setRenderer(new DefaultRowRenderer(node));
		node.setNotifier(new NoOpNotifier());
		return node;
	}
	
	public PdfContainer createTableHeaderRow(XSSFSheet sheet, Range verticalRange) {
		PdfContainer node = new PdfContainer(sheet, verticalRange);
		node.setHeigthEstimator(new HorizontalContainerHeigthEstimator(node));
		node.setWidthEstimator(new HorizontalContainerWidthEstimator(node));
		node.setProcessor(new TableRowProcessor(node));
		node.setRenderer(new DefaultRowRenderer(node));
		node.setNotifier(new HeaderRowNotifier(node));
		return node;
	}
	
	public PdfContainer createVerticalRowWrapper(XSSFSheet sheet, Range verticalRange) {
		PdfContainer node = new PdfContainer(sheet, verticalRange);
		node.setHeigthEstimator(new VerticalContainerHeigthEstimator(node));
		node.setWidthEstimator(new VerticalContainerWidthEstimator(node));
		node.setProcessor(new VerticalContainerProcessor(node));
		node.setRenderer(new NoOpRenderer());
		node.setNotifier(new NoOpNotifier());
		return node;
	}
}
