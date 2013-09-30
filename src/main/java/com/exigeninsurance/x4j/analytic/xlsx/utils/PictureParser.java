/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.utils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.util.Internal;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFPictureData;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFVMLDrawing;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTLegacyDrawing;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.exigeninsurance.x4j.analytic.xlsx.transform.Picture;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.PDFHelper;

@Internal
public class PictureParser {

    private XSSFPictureData pictureData;

    private Map<String,XSSFPictureData> legacyPictureMap;
    private Map<String,Picture> pics;

    public PictureParser() {

    }

    private PictureParser(XSSFPictureData pictureData) {
        this.pictureData = pictureData;
    }

    private static Picture parsePicture(XSSFPictureData pictureData) throws Exception {
        return (new PictureParser(pictureData)).parsePicture();
    }

    public static Map<String, Picture> parseLegacyPicturesFromSheet(XSSFSheet sheet) throws Exception {
        return (new PictureParser()).parseLegacyPictures(sheet);
    }

    private Picture parsePicture() throws Exception {
        String id = pictureData.getPackageRelationship().getId();

        InputStream in = pictureData.getPackageRelationship().getSource().getInputStream();
        Document document = null;
        try {
            document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
        }
        finally {
            in.close();
        }

        Node anchor = getAnchorNode(document, id);
        int fromRow = Integer.parseInt(getValue(anchor, "from", "row"));
        int fromCol = Integer.parseInt(getValue(anchor, "from", "col"));

        int toRow = Integer.parseInt(getValue(anchor, "to", "row"));
        int toCol = Integer.parseInt(getValue(anchor, "to", "col"));

        Node geom = getGeomNode(anchor);

        int height = Integer.parseInt(geom.getAttributes().getNamedItem("cy").getTextContent());
        int width = Integer.parseInt(geom.getAttributes().getNamedItem("cx").getTextContent());

        Picture picture = new Picture(pictureData, id, fromRow, fromCol, toRow, toCol);
        picture.setEmuHeight(height);
        picture.setEmuWidth(width);
        return picture;
    }

    private Node getGeomNode(Node anchor) throws XPathExpressionException {
        XPath xpath = XPathFactory.newInstance().newXPath();
        XPathExpression expr = xpath.compile("pic/spPr/xfrm/ext");
        return (Node) expr.evaluate(anchor, XPathConstants.NODE);
    }

    private Node getAnchorNode(Document doc, String id) throws XPathExpressionException {

        XPath xpath = XPathFactory.newInstance().newXPath();
        XPathExpression expr = xpath.compile("/wsDr//blip[@embed='" + id + "']");
        Node blip = (Node) expr.evaluate(doc, XPathConstants.NODE);
        XPath ancestor = XPathFactory.newInstance().newXPath();
        XPathExpression expr2 = ancestor.compile("ancestor::twoCellAnchor");
        return (Node) expr2.evaluate(blip, XPathConstants.NODE);

    }

    private String getValue(Node node, String element, String tagName) throws XPathExpressionException {

        XPath xpath = XPathFactory.newInstance().newXPath();

        XPathExpression expr = xpath.compile(element + "/" + tagName + "/text()");
        return (String) expr.evaluate(node, XPathConstants.STRING);
    }

    private Map<String, Picture> parseLegacyPictures(XSSFSheet sheet) throws Exception {
        pics = new HashMap<String, Picture>();
        POIXMLDocumentPart legacyDrawing = findLegacyDrawingPart(sheet);
        if (legacyDrawing != null) {
            legacyPictureMap = findLegacyPictureData(legacyDrawing);

            InputStream stream = legacyDrawing.getPackagePart().getInputStream();
            try {
                Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(stream);
                NodeList list = getShapes(doc);
                createPicturesFromShapes(list);
            } finally {
                stream.close();
            }

        }
        return pics;
    }

    private void createPicturesFromShapes(NodeList list) throws XPathExpressionException {
        for (int i = 0; i < list.getLength(); i++) {
            Node image = list.item(i);
            String sectionId = image.getAttributes().getNamedItem("id").getTextContent();
            Node imageData = (Node) XPathFactory.newInstance().newXPath().compile("imagedata").evaluate(image, XPathConstants.NODE);
            String id = imageData.getAttributes().getNamedItem("o:relid").getTextContent();
            String style = image.getAttributes().getNamedItem("style").getTextContent();
            Picture picture = new Picture(legacyPictureMap.get(id), id, 0, 0, 0, 0);
            parseAndSetMeasures(picture, style);
            pics.put(sectionId, picture);

        }
    }

    private void parseAndSetMeasures(Picture picture, String style) {
        String[] styleElements = style.split(";");
        for (String element : styleElements) {
            if (element.contains("width")) {
                picture.setEmuWidth(PDFHelper.pointsToEmu(parseMeasure(element)));
            }
            if (element.contains("height")) {
                picture.setEmuHeight(PDFHelper.pointsToEmu(parseMeasure(element)));
            }
        }
    }

    private float parseMeasure(String element) {
        return Float.parseFloat(element.substring(element.indexOf(":") + 1, element.lastIndexOf("pt")));
    }

    private Map<String, XSSFPictureData> findLegacyPictureData(POIXMLDocumentPart legacyDrawing) {
        Map<String, XSSFPictureData> pictureData = new HashMap<String, XSSFPictureData>();
        for (POIXMLDocumentPart part : legacyDrawing.getRelations()) {
            if (part instanceof XSSFPictureData) {
                pictureData.put(part.getPackageRelationship().getId(), (XSSFPictureData) part);
            }
        }
        return pictureData;
    }

    private NodeList getShapes(Document document) throws XPathExpressionException {
        XPathFactory xPathFactory = XPathFactory.newInstance();
        return (NodeList) xPathFactory.newXPath().compile("/xml//shape").evaluate(document, XPathConstants.NODESET);
    }

    private POIXMLDocumentPart findLegacyDrawingPart(XSSFSheet sheet) {
        CTLegacyDrawing legacyDrawingHF = sheet.getCTWorksheet().getLegacyDrawingHF();
        if (legacyDrawingHF != null) {
            String id = legacyDrawingHF.getId();
            for (POIXMLDocumentPart relation : sheet.getRelations()) {
                if (relation instanceof XSSFVMLDrawing && relation.getPackageRelationship().getId().equals(id)) {
                    return relation;
                }
            }
        }
        return null;
    }

    public static List<Picture> getSheetPictures(XSSFSheet sheet) throws Exception {
        ArrayList<Picture> pictures = new ArrayList<Picture>();
        for (POIXMLDocumentPart relation : sheet.getRelations()) {
            if (relation instanceof XSSFDrawing) {
                for (POIXMLDocumentPart img : relation.getRelations()) {
                    if (img instanceof XSSFPictureData) {
                        pictures.add(parsePicture((XSSFPictureData) img));
                    }
                }
            }
        }
        return pictures;
    }
}
