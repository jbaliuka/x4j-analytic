/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx;

import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.xssf.usermodel.XSSFRelation;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDrawing;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTHeaderFooter;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTLegacyDrawing;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPageMargins;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPageSetup;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorksheet;

import com.exigeninsurance.x4j.analytic.api.ReportException;
import com.exigeninsurance.x4j.analytic.xlsx.core.expression.XLSXExpression;
import com.exigeninsurance.x4j.analytic.xlsx.core.node.Node;
import com.exigeninsurance.x4j.analytic.xlsx.transform.SST;
import com.exigeninsurance.x4j.analytic.xlsx.utils.CellExpressionParser;


public class XLSXSheetTail extends Node {
    public XLSXSheetTail(XSSFSheet sheet) {
        super(sheet);
    }

    @Override
    public void process(XLXContext context) throws Exception {
        context.write(tail(context));
    }

    private String tail(XLXContext context) {
        return margins(getSheet())
                + pageSetup(getSheet())
                + headerFooter(context, getSheet())
                + drawings(getSheet())
                + tableParts(context);
    }

    private String margins(XSSFSheet sheet) {
        String margins = "";
        CTPageMargins pageMargins = sheet.getCTWorksheet().getPageMargins();
        if (pageMargins != null) {
            margins += "<pageMargins";
            margins += " footer=\"" + pageMargins.getFooter() + "\"";
            margins += " header=\"" + pageMargins.getHeader() + "\"";
            margins += " bottom=\"" + pageMargins.getBottom() + "\"";
            margins += " top=\"" + pageMargins.getTop() + "\"";
            margins += " right=\"" + pageMargins.getRight() + "\"";
            margins += " left=\"" + pageMargins.getLeft() + "\"";
            margins += "/>";
        }
        return margins;
    }

    private String headerFooter(XLXContext context, XSSFSheet sheet) {
        CTHeaderFooter headerFooter = sheet.getCTWorksheet().getHeaderFooter();
        if (headerFooter != null) {
            return ("<headerFooter")
                    + attributes(headerFooter)
                    + headerFooter(context, headerFooter)
                    +"</headerFooter>";
        }
        return "";
    }

    private String attributes(CTHeaderFooter headerFooter) {
        String text = "";
        if (headerFooter.isSetDifferentFirst()) {
            text += " differentFirst=\"1\"";
        }
        if (headerFooter.isSetDifferentOddEven()) {
            text += " differentOddEven=\"1\"";
        }
        text += ">";
        return text;
    }

    private String headerFooter(XLXContext context, CTHeaderFooter headerFooter) {
        return oddHeader(context, headerFooter) + oddFooter(context, headerFooter)
                + evenHeader(context, headerFooter) + evenFooter(context, headerFooter)
                + firstHeader(context, headerFooter) + firstFooter(context, headerFooter);
    }

    private String firstFooter(XLXContext context, CTHeaderFooter headerFooter) {
        return headerFooter.isSetFirstFooter() ? formatHeader(context, "firstFooter", headerFooter.getFirstFooter()) : "";
    }

    private String firstHeader(XLXContext context, CTHeaderFooter headerFooter) {
        return headerFooter.isSetFirstHeader() ? formatHeader(context, "firstHeader", headerFooter.getFirstHeader()) : "";
    }

    private String evenFooter(XLXContext context, CTHeaderFooter headerFooter) {
        return headerFooter.isSetEvenFooter() ? formatHeader(context, "evenFooter", headerFooter.getEvenFooter()) : "";
    }

    private String evenHeader(XLXContext context, CTHeaderFooter headerFooter) {
        return headerFooter.isSetEvenHeader() ? formatHeader(context, "evenHooter", headerFooter.getEvenHeader()) : "";
    }

    private String oddFooter(XLXContext context, CTHeaderFooter headerFooter) {
        return headerFooter.isSetOddFooter() ? formatHeader(context, "oddFooter", headerFooter.getOddFooter()) : "";
    }

    private String oddHeader(XLXContext context, CTHeaderFooter headerFooter) {
        return headerFooter.isSetOddHeader() ? formatHeader(context, "oddHeader", headerFooter.getOddHeader()) : "";
    }

    private String formatHeader(XLXContext context, String tag, String headerFooter) {
        try {
            XLSXExpression parseExpression = CellExpressionParser.parseExpression(headerFooter);
            headerFooter = parseExpression.evaluate(context).toString();
        }
        catch (Exception e) {
            throw new ReportException("Error parsing header expressions", e);
        }
        return "<" + tag + ">" + SST.forXML(headerFooter) + "</" + tag + ">";
    }

    private String pageSetup(XSSFSheet sheet) {
        CTPageSetup pageSetup = sheet.getCTWorksheet().getPageSetup();
        if (pageSetup != null) {
            return "<pageSetup " + pageId(pageSetup)
                    +  " " + orientation(pageSetup)
                    + " " + fitToHeight(pageSetup) + " " + fitToWidth(pageSetup)
                    + " " + paperSize(pageSetup) + "/>";
        }
        return "";
    }

    private String pageId(CTPageSetup pageSetup) {
        return "r:id=\""
                + pageSetup.getId() + "\"";
    }

    private String orientation(CTPageSetup pageSetup) {
        return "orientation=\"" + pageSetup.getOrientation().toString() + "\"";
    }

    private String fitToHeight(CTPageSetup pageSetup) {
        return "fitToHeight=\"" + pageSetup.getFitToHeight() + "\"";
    }

    private String fitToWidth(CTPageSetup pageSetup) {
        return "fitToWidth=\"" + pageSetup.getFitToWidth() + "\"";
    }

    private String paperSize(CTPageSetup pageSetup) {
        return String.format("paperSize=\"" + pageSetup.getPaperSize() + "\"");
    }

    private String tableParts(XLXContext context) {
        StringBuilder tableParts = new StringBuilder();
        if(!context.getTables().isEmpty()){
            tableParts.append(String.format("<tableParts count=\"%d\">" ,context.getTables().size()));
            for (POIXMLDocumentPart next : getSheet().getRelations()) {
                tableParts.append( table(next) );
            }

            tableParts.append("</tableParts>");
        }
        return tableParts.toString();
    }

    private String table(POIXMLDocumentPart part) {
        return isTablePart(part) ? tablePart(part) : "";
    }

    private String tablePart(POIXMLDocumentPart part) {
        return "<tablePart r:id=\"" + part.getPackageRelationship().getId() + "\" />";
    }

    private boolean isTablePart(POIXMLDocumentPart next) {
        return next.getPackageRelationship().getRelationshipType().equals(XSSFRelation.TABLE.getRelation());
    }


    private String drawings(XSSFSheet sheet) {
        String drawings = "";
        CTWorksheet ctWorksheet = sheet.getCTWorksheet();
        CTDrawing drawing = ctWorksheet.getDrawing();
        CTLegacyDrawing legacyDrawing = ctWorksheet.getLegacyDrawing();
        CTLegacyDrawing legacyDrawingHF = ctWorksheet.getLegacyDrawingHF();
        if (drawing != null) {
            drawings += "<drawing r:id=\"" + drawing.getId() + "\" />";
        }
        if (legacyDrawing != null) {
            drawings += "<legacyDrawing r:id=\"" + legacyDrawing.getId() + "\" />";
        }
        if (legacyDrawingHF != null) {
            drawings += "<legacyDrawingHF r:id=\"" + legacyDrawingHF.getId() + "\" />";
        }
        return drawings;
    }
}
