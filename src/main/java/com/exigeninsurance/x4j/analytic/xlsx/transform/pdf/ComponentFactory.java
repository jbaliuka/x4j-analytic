/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import com.exigeninsurance.x4j.analytic.xlsx.core.expression.XLSXExpression;
import com.exigeninsurance.x4j.analytic.xlsx.transform.Picture;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.geometry.Range;


public interface ComponentFactory {
    public PdfCellNode createHeaderCell(XSSFSheet xssfSheet, XSSFCell cell, XLSXExpression expr);

    public PdfCellNode createTableCell(XSSFSheet xssfSheet, XSSFCell cell, XLSXExpression expr);

    public PdfCellNode createCell(XSSFSheet xssfSheet, XSSFCell cell, XLSXExpression expr);

    public PdfCellNode createEmptyCell(XSSFSheet xssfSheet, XSSFCell cell);

    public PdfContainer createTableRow(XSSFSheet sheet, Range verticalRange);

    public PdfContainer createTableHeaderRow(XSSFSheet sheet, Range verticalRange);

    public PdfContainer createVerticalRowWrapper(XSSFSheet sheet, Range verticalRange);

    public PdfContainer createEmptyRow(XSSFSheet sheet, Range oneRowRange);

    public PdfPictureNode createPictureNode(XSSFSheet sheet, Picture picture);

	public PdfCellNode createWrappedCell(XSSFSheet sheet, XSSFCell cell, XLSXExpression expr);

	public void makeCellWrappable(PdfCellNode cell);
}
