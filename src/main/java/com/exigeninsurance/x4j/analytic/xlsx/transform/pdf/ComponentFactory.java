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
    PdfCellNode createHeaderCell(XSSFSheet xssfSheet, XSSFCell cell, XLSXExpression expr);

    PdfCellNode createTableCell(XSSFSheet xssfSheet, XSSFCell cell, XLSXExpression expr);

    PdfCellNode createCell(XSSFSheet xssfSheet, XSSFCell cell, XLSXExpression expr);

    PdfCellNode createEmptyCell(XSSFSheet xssfSheet, XSSFCell cell);

    PdfContainer createTableRow(XSSFSheet sheet, Range verticalRange);

    PdfContainer createTableHeaderRow(XSSFSheet sheet, Range verticalRange);

    PdfContainer createVerticalRowWrapper(XSSFSheet sheet, Range verticalRange);

    PdfContainer createEmptyRow(XSSFSheet sheet, Range oneRowRange);

    PdfPictureNode createPictureNode(XSSFSheet sheet, Picture picture);

	PdfCellNode createWrappedCell(XSSFSheet sheet, XSSFCell cell, XLSXExpression expr);

	void makeCellWrappable(PdfCellNode cell);
}
