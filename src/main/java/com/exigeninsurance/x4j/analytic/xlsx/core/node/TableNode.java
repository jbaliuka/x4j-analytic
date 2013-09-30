/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.core.node;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.model.MapInfo;
import org.apache.poi.xssf.model.Table;
import org.apache.poi.xssf.usermodel.XSSFRelation;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTAutoFilter;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTConnection;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTConnections;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDataBinding;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTMap;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTMapInfo;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSchema;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTable;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumn;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWebPr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTXmlColumnPr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STHtmlFmt;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STTableType;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STXmlDataType;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.TableDocument;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.exigeninsurance.x4j.analytic.util.IOUtils;
import com.exigeninsurance.x4j.analytic.xlsx.transform.SST;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLSXConnections;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLSXFactory;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLXContext;
import com.exigeninsurance.x4j.analytic.xlsx.utils.CellExpressionParser;


public class TableNode extends Node{

	private static final String XS = "http://www.w3.org/2001/XMLSchema";
	private static final String NAME = "tableStyleInfo name=";
	private Node header;
	private ForEachNode forEach;
	private Node totals;
	private Table table;
	private CTTable ctTable ;
	private MapInfo info ;

	private CTConnections connections;

	public TableNode(XSSFSheet sheet, Table table) {
		super(sheet);	
		this.table = table;
	}

	public void addHeader(Node header){
		this.header = header;
		getChildren().add(header);
	}

	public void addLoop(ForEachNode forEach){
		this.forEach = forEach;
		getChildren().add(forEach);
	}

	public void addTotals(Node totals){
		this.totals = totals;
	}

	@Override
	public void process(XLXContext context) throws Exception {
		for(CTTableColumn c : table.getCTTable().getTableColumns().getTableColumnArray()){
			Object name = CellExpressionParser.parseExpression(c.getName()).evaluate(context);
			if(name != null){
				c.setName(name.toString());
			}
		}

		if(header != null){
			context.setTableStartHeader();
			header.process(context);
			context.endTableHeader();
			generateXMLMap(context);
		}

		String tableStartRef  = startTable(context);
		long row = context.getCurrentRow();
		forEach.process(context);
		if(row == context.getCurrentRow()){
			context.nextRow();//insert empty row
		}
		if(totals != null){
			totals.process(context);
		}

		processTable(context, tableStartRef);
	}

	private void generateXMLMap(XLXContext context) {
		initXMLMap();
		CTConnection connection = buildConnection(context);
		CTMapInfo ctMapInfo = info.getCTMapInfo();		
		CTMap map = buildXMLMap(connection, ctMapInfo);		
		buildSchema(context,ctMapInfo, map);
	}

	private void buildSchema(XLXContext context, CTMapInfo ctMapInfo, CTMap map) {
		CTSchema schema = ctMapInfo.addNewSchema();
		schema.setID(map.getSchemaID());

		org.w3c.dom.Node node = schema.getDomNode();
		Document document = node.getOwnerDocument();
		Element schemaElement = document.createElementNS(XS,"xs:schema");		
		schemaElement.setAttribute("xmlns", "");
		schemaElement.setAttribute("xmlns:xs", XS);

		node.appendChild(schemaElement);
		Element rootElement = document.createElementNS(XS,"xs:element");
		rootElement.setAttribute("name", "root");
		schemaElement.appendChild(rootElement);
		Element complexType = document.createElementNS(XS,"xs:complexType");
		rootElement.appendChild(complexType);
		Element sequence = document.createElementNS(XS,"xs:sequence");
		sequence.setAttribute("minOccurs", "0");	

		complexType.appendChild(sequence);

		Element row = document.createElementNS(XS,"xs:element");			
		row.setAttribute("minOccurs", "0");
		row.setAttribute("maxOccurs", "unbounded");
		row.setAttribute("name", "row");
		row.setAttribute("form","unqualified");
		row.setAttribute("nillable", "true");

		sequence.appendChild(row);
		complexType = document.createElementNS(XS,"xs:complexType");
		row.appendChild(complexType);

		sequence = document.createElementNS(XS,"xs:sequence");
		sequence.setAttribute("minOccurs", "0");	

		complexType.appendChild(sequence);

		CTTable originalTable; 
		try{	
			originalTable = TableDocument.Factory.parse(table.getPackagePart().getInputStream()).getTable();
		}catch(Exception e){
			originalTable = table.getCTTable();
		}
		int i = 0;
		for(CTTableColumn col : ctTable.getTableColumns().getTableColumnArray()){

			CTXmlColumnPr ptr = col.addNewXmlColumnPr();
			ptr.setMapId(map.getID());
			
			String elementName = SST.toXMLEment(originalTable.getTableColumns().getTableColumnArray(i++).getName());
			
			ptr.setXmlDataType(STXmlDataType.Enum.forString(guessXMLType(elementName)));
			
			col.setUniqueName(col.getName());
			ptr.setXpath("/root/row/" + elementName);

			Element e = document.createElementNS(XS,"xs:element");
			e.setAttribute("name", elementName);
			e.setAttribute("type", "xs:" + guessXMLType(elementName));
			e.setAttribute("minOccurs", "0");
			e.setAttribute("maxOccurs", "1");
			e.setAttribute("nillable", "true");

			sequence.appendChild(e);
		}
	}

	private String guessXMLType(String elementName) {
		for(String pattern : Arrays.asList("amount","debit","credit","premium")){
			if(elementName.contains(pattern)){
				return  "double";
			}
		}

		return "string";
	}

	private CTMap buildXMLMap(CTConnection connection, CTMapInfo ctMapInfo) {
		ctMapInfo.setSelectionNamespaces("");

		CTMap map = ctMapInfo.addNewMap();
		map.setID(ctMapInfo.getMapArray().length);
		map.setSchemaID("Schema" + ctTable.getName());
		map.setName(ctTable.getName() + "Map");		
		map.setRootElement("root");
		map.setShowImportExportValidationErrors(false);
		map.setAutoFit(true);
		map.setAppend(false);
		map.setPreserveFormat(true);
		map.setPreserveSortAFLayout(true);

		CTDataBinding binding = map.addNewDataBinding();
		binding.setFileBinding(true);
		binding.setDataBindingLoadMode(1);
		binding.setConnectionID(connection.getId());
		return map;
	}

	private CTConnection buildConnection(XLXContext context) {
		CTConnection connection = connections.addNewConnection();		
		connection.setId(connections.getConnectionArray().length);
		connection.setName(ctTable.getName());
		connection.setType(4);
		connection.setRefreshedVersion((short) 0);
		connection.setBackground(true);
		CTWebPr webPtr = connection.addNewWebPr();
		webPtr.setXml(true);
		webPtr.setSourceData(true);
		webPtr.setHtmlFormat(STHtmlFmt.ALL);
		webPtr.setHtmlTables(true);
		webPtr.setUrl(context.getReportContext().getMetadata().getId());

		ctTable.setTableType(STTableType.Enum.forString("xml"));
		ctTable.setConnectionId(connection.getId());
		return connection;
	}

	private void initXMLMap() {
		ctTable = table.getCTTable();
        XSSFWorkbook workbook = getSheet().getWorkbook();
		info = workbook.getMapInfo();

		if(info == null){
			for(POIXMLDocumentPart p : workbook.getRelations()){
				if(p instanceof MapInfo) {
					info = (MapInfo) p;					
				}else if (p instanceof XLSXConnections){					
					connections = ((XLSXConnections) p).getCTConnections();
				}                
			}
		}

		if(info == null){
			info = (MapInfo) workbook.createRelationship(XSSFRelation.CUSTOM_XML_MAPPINGS, new XLSXFactory());
		}
		if(connections == null){
			connections = ((XLSXConnections) workbook.createRelationship(XLSXFactory.CONNECTIONS, new XLSXFactory())).getCTConnections();
		}
	}

	public String startTable(XLXContext context) {		
		int col = table.getStartCellReference().getCol();
		long row = context.getCurrentRow() - 1;
		return new CellReference((int)row,col).formatAsString();		
	}

	public void processTable(XLXContext context, String tableStartRef) {
		int col = table.getEndCellReference().getCol();
		long currentRow = context.getCurrentRow() - 1;		

		int totalsRowCount = (int) table.getCTTable().getTotalsRowCount();

		CTAutoFilter filter = table.getCTTable().getAutoFilter();
		if(filter != null){
            String autoFilterRef = new CellReference((int) (currentRow - totalsRowCount),col).formatAsString();
            table.getCTTable().getAutoFilter().setRef(tableStartRef + ":" + autoFilterRef);
		}

		String tableEndRef = new CellReference((int)currentRow,col).formatAsString();
		table.getCTTable().setRef(tableStartRef + ":" + tableEndRef);
        context.processTable(table);
	}

	public String extractTableStyleName() throws IOException {
		InputStream in = table.getPackagePart().getInputStream();
		String contents;
		try{
			contents = IOUtils.toString(in);
		}finally{
			in.close();
		}
		int start = contents.lastIndexOf(NAME);
		if (start == -1) {
			return null;
		}
		start += NAME.length() + 1;
		int end = contents.indexOf("\"", start);
		return contents.substring(start, end);
	}

	public Table getTable() {
		return table;
	}

	public Node getHeader() {
		return header;
	}

	public void setHeader(Node header) {
		this.header = header;
	}

	public ForEachNode getForEach() {
		return forEach;
	}

	public void setForEach(ForEachNode forEach) {
		this.forEach = forEach;
	}

	public Node getTotals() {
		return totals;
	}

	public void setTotals(Node totals) {
		this.totals = totals;
	}

	public void setTable(Table table) {
		this.table = table;
	}
}
