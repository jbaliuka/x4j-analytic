/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import com.exigeninsurance.x4j.analytic.xlsx.core.expression.XLSXExpression;
import com.exigeninsurance.x4j.analytic.xlsx.transform.Picture;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.ChildProcessor;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.NoOpProcessor;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.cell.ColumnWidth;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.cell.Margins;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.cell.MeasuringProcessor;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.cell.TextLength;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.cell.WrappingCellWidthEstimator;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.row.MeasuringRowProcessor;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.geometry.Range;


public class PreProcessingComponentFactory implements ComponentFactory {

    private ComponentFactoryImpl factory = new ComponentFactoryImpl();

    @Override
    public PdfCellNode createHeaderCell(XSSFSheet xssfSheet, XSSFCell cell, XLSXExpression expr) {
        PdfCellNode wrappingCell = factory.createHeaderCell(xssfSheet, cell, expr);
		doNotProcess(wrappingCell);
		return wrappingCell;
    }

	@Override
	public PdfCellNode createWrappedCell(XSSFSheet sheet, XSSFCell cell, XLSXExpression expr) {
		PdfCellNode wrappingCell = factory.createWrappedCell(sheet, cell, expr);
		wrappingCell.setProcessor(new MeasuringProcessor(wrappingCell, new WrappingCellWidthEstimator(wrappingCell, new ColumnWidth(wrappingCell))));
		return wrappingCell;
	}

	@Override
	public void makeCellWrappable(PdfCellNode cell) {
		factory.makeCellWrappable(cell);
		cell.setProcessor(new MeasuringProcessor(cell, new WrappingCellWidthEstimator(cell, new ColumnWidth(cell))));
	}

	@Override
    public PdfCellNode createTableCell(XSSFSheet xssfSheet, XSSFCell cell, XLSXExpression expr) {
        PdfCellNode tableCell = factory.createTableCell(xssfSheet, cell, expr);
        tableCell.setProcessor(new MeasuringProcessor(tableCell, new Margins(new TextLength(tableCell))));
        return tableCell;
    }

    @Override
    public PdfCellNode createCell(XSSFSheet xssfSheet, XSSFCell cell, XLSXExpression expr) {
        PdfCellNode cellNode = factory.createCell(xssfSheet, cell, expr);
		doNotProcess(cellNode);
		return cellNode;
    }

    @Override
    public PdfCellNode createEmptyCell(XSSFSheet xssfSheet, XSSFCell cell) {
        PdfCellNode emptyCell = factory.createEmptyCell(xssfSheet, cell);
		doNotProcess(emptyCell);
		return emptyCell;
    }

    @Override
    public PdfContainer createTableRow(XSSFSheet sheet, Range verticalRange) {
        PdfContainer tableRow = factory.createTableRow(sheet, verticalRange);
        tableRow.setProcessor(new MeasuringRowProcessor(tableRow));
        return tableRow;
    }

    @Override
    public PdfContainer createTableHeaderRow(XSSFSheet sheet, Range verticalRange) {
        PdfContainer tableHeaderRow = factory.createTableHeaderRow(sheet, verticalRange);
        tableHeaderRow.setProcessor(new MeasuringRowProcessor(tableHeaderRow));
        return tableHeaderRow;
    }

    @Override
    public PdfContainer createVerticalRowWrapper(XSSFSheet sheet, Range verticalRange) {
        PdfContainer verticalRowWrapper = factory.createVerticalRowWrapper(sheet, verticalRange);
        verticalRowWrapper.setProcessor(new ChildProcessor(verticalRowWrapper));
        return verticalRowWrapper;
    }

    @Override
    public PdfContainer createEmptyRow(XSSFSheet sheet, Range oneRowRange) {
        PdfContainer emptyRow = factory.createEmptyRow(sheet, oneRowRange);
        emptyRow.setProcessor(new MeasuringRowProcessor(emptyRow));
        return emptyRow;
    }

    @Override
    public PdfPictureNode createPictureNode(XSSFSheet sheet, Picture picture) {
        PdfPictureNode pictureNode = factory.createPictureNode(sheet, picture);
        pictureNode.setProcessor(new NoOpProcessor());
        return pictureNode;
    }

	private void doNotProcess(PdfCellNode cell) {
		cell.setProcessor(new NoOpProcessor());
	}


}
