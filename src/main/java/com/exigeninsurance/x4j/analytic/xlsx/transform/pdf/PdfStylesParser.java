/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.exigeninsurance.x4j.analytic.api.ReportException;
import com.exigeninsurance.x4j.analytic.xlsx.transform.PdfStyle;
import com.exigeninsurance.x4j.analytic.xlsx.transform.PdfStylesTable;
import com.exigeninsurance.x4j.analytic.xlsx.transform.TableStyle;
import com.exigeninsurance.x4j.analytic.xlsx.transform.ThemeColor;
import com.exigeninsurance.x4j.analytic.xlsx.transform.XLSXFill;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.PDFHelper;


public class PdfStylesParser {

	public static final String BACKGROUND = "bg";
	public static final String FOREGROUND = "fg";
	public static final String DUPLICATE_SUFFIX = "copy";

	private static final String FIRST_COLUMN_STRIPE = "firstColumnStripe";
	private static final String FIRST_ROW_STRIPE = "firstRowStripe";
	private static final String LAST_COLUMN = "lastColumn";
	private static final String FIRST_COLUMN = "firstColumn";
	private static final String TOTAL_ROW = "totalRow";
	private static final String HEADER_ROW = "headerRow";
	private static final String WHOLE_TABLE = "wholeTable";
	private static final String DXF_ID = "dxfId";
	private static final String FILL = "fill";
	private static final String PATTERN_FILL = "patternFill";
	private static final String DEFAULT_TABLE_STYLE = "defaultTableStyle";
	private static final String THEME = "theme";
	private static final String TINT = "tint";
	private static final String NAME = "name";
	private static final String PIVOT = "pivot";
	private static final String TYPE = "type";

    private XPathFactory xPathFactory;
    private Element root;
	private List<Node> dxfs = new ArrayList<Node>();
	private List<Node> tableStyles = new ArrayList<Node>();
	private final List<PdfStyle> pdfStyles = new ArrayList<PdfStyle>();
	private final Map<String,TableStyle> pdfTableStyles = new HashMap<String,TableStyle>();
	
	public PdfStylesParser(InputStream stream) {
		try{
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();

            Document document = builder.parse(stream);
			root = document.getDocumentElement();
		} catch (Exception e) {
			throw new ReportException(e);
		}

        xPathFactory = XPathFactory.newInstance();
	}
	
	public PdfStylesTable produceStylesTable() {
		PdfStylesTable table = new PdfStylesTable();
		parseDxfs();
		parseTableStyles();
		producePdfStyles();
		produceTableStyles();
		table.setTableStyles(pdfTableStyles);
		table.setDefaultTableStyle(parseDefaultTableStyle());
		return table;
	}
	
	public TableStyle produceTableStyle(Node tableStyleNode) {
		TableStyle tableStyle = new TableStyle();
		tableStyle.setName(extractAttribute(NAME, tableStyleNode));
		tableStyle.setPivot(Boolean.parseBoolean(extractAttribute(PIVOT, tableStyleNode)));
		setPdfStyles(tableStyle, tableStyleNode);
		return tableStyle;	
	}
	
	public String parseDefaultTableStyle() {
		Node tableStyles = fetchNode("//tableStyles", root);
		return extractAttribute(DEFAULT_TABLE_STYLE, tableStyles);
	}
	
	public List<Node> parseDxfs() {
        dxfs = fetchItems("/styleSheet/dxfs/dxf");
        return dxfs;
    }

    public List<Node> parseTableStyles() {
		tableStyles = fetchItems("//tableStyle");
		return tableStyles;
	}

    public PdfStyle produceStyle(Node dxf) {
        PdfStyle style = new PdfStyle();
        XLSXFill fill;
        Node fillNode = getNode(FILL, dxf);
        if (fillNode == null) {
            fill = PDFHelper.WHITE_FILL;
        }
        else {
            fill = new XLSXFill();
            fill.setBgColor(produceColor(BACKGROUND, fillNode));
            fill.setFgColor(produceColor(FOREGROUND, fillNode));
        }
        style.setFill(fill);
        return style;
    }
	
	private ThemeColor produceColor(String prefix, Node node) {
		ThemeColor color = new ThemeColor();
		Node patternFillNode = getNode(PATTERN_FILL, node);
		if (patternFillNode == null) {
			return PDFHelper.WHITE_COLOR;
		}
		NodeList children = patternFillNode.getChildNodes();
		String colorName = prefix + "Color";
		
		for (int i = 0; i < children.getLength(); i++) {
			Node current = children.item(i);
			if (current.getNodeName().equals(colorName)) {
				NamedNodeMap attributes = current.getAttributes();
				Node themeNode = attributes.getNamedItem(THEME);
				Node tintNode = attributes.getNamedItem(TINT);
				
				if (themeNode != null) {
					color.setTheme(Integer.parseInt(themeNode.getNodeValue()));
				}
				if (tintNode != null) {
					color.setTint(Double.parseDouble(tintNode.getNodeValue()));
				}
			}
		}
		return color;
	}
	
	private String extractAttribute(String attributeName, Node node) {
		NamedNodeMap attributes = node.getAttributes();
		Node attributeNode = attributes.getNamedItem(attributeName);
		return attributeNode == null ? null : attributeNode.getNodeValue();
	}
	
	private Node getTableStyleElement(String typeName, Node parent) {
		Node node = null;
		NodeList children = parent.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node current = children.item(i);
			if (current instanceof Element) {
				String type = extractAttribute(TYPE, current);
				if (type.equals(typeName)) {
					return current;
				}
			}
		}
		return node;
	}
	
	private void setPdfStyles(TableStyle tableStyle, Node tableStyleNode) {
		tableStyle.setWholeTableStyle(getPdfStyle(WHOLE_TABLE, tableStyleNode));
		tableStyle.setHeaderRowStyle(getPdfStyle(HEADER_ROW, tableStyleNode));
		tableStyle.setTotalRowStyle(getPdfStyle(TOTAL_ROW, tableStyleNode));
		tableStyle.setFirstColumnStyle(getPdfStyle(FIRST_COLUMN, tableStyleNode));
		tableStyle.setLastColumnStyle(getPdfStyle(LAST_COLUMN, tableStyleNode));
		tableStyle.setFirstRowStripeStyle(getPdfStyle(FIRST_ROW_STRIPE, tableStyleNode));
		tableStyle.setFirstColumnStripeStyle(getPdfStyle(FIRST_COLUMN_STRIPE, tableStyleNode));
	}
	
	private PdfStyle getPdfStyle(String typeName, Node tableStyleNode) {
		Node styleNode = getTableStyleElement(typeName, tableStyleNode);
		if (styleNode != null) {
			int dxfId = Integer.parseInt(extractAttribute(DXF_ID, styleNode));
			return pdfStyles.get(dxfId);
		}
		else {
			return null;
		}
	}

    public void producePdfStyles() {
        for (Node dxf : dxfs) {
            pdfStyles.add(produceStyle(dxf));
        }
    }
	
	private void produceTableStyles() {
		for (Node tableStyle : tableStyles) {
			TableStyle style = produceTableStyle(tableStyle);
			pdfTableStyles.put(style.getName(), style);
		}
	}

    private List<Node> createNodeCollection(NodeList nodeList) {
        List <Node> collection = new ArrayList<Node>();
        int length = nodeList.getLength();
        for (int i = 0; i < length; i++) {
            collection.add(nodeList.item(i));
        }
        return collection;
    }

    private List<Node> fetchItems(String xpath) {
        XPath xPath = xPathFactory.newXPath();
        XPathExpression expression;
        NodeList nodeList;
        try {
            expression = xPath.compile(xpath);
            nodeList = (NodeList) expression.evaluate(root, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            throw new IllegalStateException("Failed to evaluate xpath: " + xpath, e);
        }

        return createNodeCollection(nodeList);
    }

    private Node fetchNode(String xpath, Node root) {
        XPath xPath = xPathFactory.newXPath();
        XPathExpression expression;
        Node node;
        try {
            expression = xPath.compile(xpath);
            node = (Node) expression.evaluate(root, XPathConstants.NODE);
        } catch (XPathExpressionException e) {
            throw new IllegalStateException("Failed to evaluate xpath: " + xpath, e);
        }

        return node;
    }

    private Node getNode(String name, Node startingNode) {
        NodeList nodes = startingNode.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node instanceof Element) {
                if (node.getNodeName().equals(name)) {
                    return node;
                }
            }
        }
        return null;
    }

    public List<PdfStyle> getPdfStyles() {
        return pdfStyles;
    }
}
