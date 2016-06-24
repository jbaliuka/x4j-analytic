/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.namespace.QName;

import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.POIXMLProperties.ExtendedProperties;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackagePartName;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.openxml4j.opc.PackagingURIHelper;
import org.apache.poi.openxml4j.opc.internal.ZipContentTypeManager;
import org.apache.poi.openxml4j.opc.internal.ZipHelper;
import org.apache.poi.openxml4j.opc.internal.marshallers.ZipPartMarshaller;
import org.apache.poi.xssf.model.CommentsTable;
import org.apache.poi.xssf.model.Table;
import org.apache.poi.xssf.usermodel.XSSFRelation;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.helpers.ColumnHelper;
import org.apache.xmlbeans.XmlOptions;
import org.openxmlformats.schemas.officeDocument.x2006.extendedProperties.CTProperties;
import org.openxmlformats.schemas.officeDocument.x2006.relationships.STRelationshipId;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCacheField;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDataField;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPivotCacheDefinition;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPivotTableDefinition;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheet;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTable;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumn;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorksheet;

import com.exigeninsurance.x4j.analytic.api.ReportContext;
import com.exigeninsurance.x4j.analytic.api.ReportException;
import com.exigeninsurance.x4j.analytic.util.IOUtils;
import com.exigeninsurance.x4j.analytic.xlsx.core.expression.XLSXExpression;
import com.exigeninsurance.x4j.analytic.xlsx.core.node.Node;
import com.exigeninsurance.x4j.analytic.xlsx.transform.PivotTable;
import com.exigeninsurance.x4j.analytic.xlsx.transform.PivotTableCache;
import com.exigeninsurance.x4j.analytic.xlsx.transform.SST;
import com.exigeninsurance.x4j.analytic.xlsx.transform.SheetParser;
import com.exigeninsurance.x4j.analytic.xlsx.transform.WorkbookProcessor;
import com.exigeninsurance.x4j.analytic.xlsx.utils.CellExpressionParser;
import com.exigeninsurance.x4j.analytic.xlsx.utils.MacroNodeFactoryImpl;
import com.exigeninsurance.x4j.analytic.xlsx.utils.MacroParser;
import com.exigeninsurance.x4j.analytic.xlsx.utils.WrappingUtil;
import com.exigeninsurance.x4j.analytic.xlsx.utils.XSSFSheetHelper;


final class XLSXProcessor extends WorkbookProcessor {

	private static final XmlOptions DEFAULT_XML_OPTIONS;

	static {
		DEFAULT_XML_OPTIONS = new XmlOptions();
		DEFAULT_XML_OPTIONS.setSaveOuter();
		DEFAULT_XML_OPTIONS.setUseDefaultNamespace();
		DEFAULT_XML_OPTIONS.setSaveAggressiveNamespaces();
	}

	private final XSSFWorkbook workBook;
	private final ZipOutputStream out;
	private final SST sst;
	
	
	
	public XLSXProcessor(XSSFWorkbook workBook, 
			ZipOutputStream out,
			SST sst){
		this.workBook = workBook;
		this.out = out;
		this.sst = sst;
	
		
	}

	public void processSheets(	ReportContext reportContext, List<String> savedParts, ZipContentTypeManager manager	)
			throws  Exception {

		for(int i = 0; i < workBook.getNumberOfSheets(); i++){
			XLSXSheet sheet = (XLSXSheet) nextSheet(reportContext, savedParts, i);
			CommentsTable comments = sheet.getCommentsTable(false);
			if(comments != null){
				String entryName = ZipHelper.getZipItemNameFromOPCName(comments.getPackagePart().getPartName().getName());
				savedParts.add(entryName);
				out.putNextEntry(new ZipEntry(entryName));
				comments.writeTo(out);
				out.closeEntry();
			}

			if (sheet.getPackagePart().hasRelationships()) {
				PackagePartName relationshipPartName = PackagingURIHelper
						.getRelationshipPartName(sheet.getPackagePart().getPartName());

				ZipPartMarshaller.marshallRelationshipPart(sheet.getPackagePart().getRelationships(),
						relationshipPartName, out);

				savedParts.add(relationshipPartName.getName());
                for (PackageRelationship next : sheet.getPackagePart().getRelationships()) {
                    PackagePartName name = PackagingURIHelper.createPartName(next.getTargetURI());
                    XSSFRelation type = XSSFRelation.getInstance(next.getRelationshipType());
                    if (type != null) {
                        manager.addContentType(name, type.getContentType());
                    }
                }
			}
		}
	}

	private void savePivots(XSSFSheet sheet, XLXContext context, List<String> savedParts) throws Exception {
		for (POIXMLDocumentPart p : sheet.getRelations()) {
			if (p.getPackageRelationship().getRelationshipType().equals(XLSXFactory.PIVOT.getRelation())) {
				savePivotTable(context, savedParts, p);
			}
		}
	}

	private void savePivotTable(XLXContext context, List<String> savedParts,
			POIXMLDocumentPart p) throws Exception {

		PivotTable pivotTable = (PivotTable) p;
		CTPivotTableDefinition ctPivotTable = pivotTable.getCtPivotTable();

		translateCaption(context, ctPivotTable);

		if (ctPivotTable.getDataFields() != null) {
			for(CTDataField field : ctPivotTable.getDataFields().getDataFieldArray()){
				translatePivotField(context, field);
			}
		}

		String pivotEntryName = getEntryName(pivotTable.getPackagePart());
		savedParts.add(pivotEntryName);
		out.putNextEntry(new ZipEntry(pivotEntryName));
		pivotTable.writeTo(out);
		out.closeEntry();

		for (POIXMLDocumentPart pc : pivotTable.getRelations()) {
			savePivotCache(context, savedParts, pc);
		}
	}

	private void translatePivotField(XLXContext context, CTDataField field)
			throws Exception {

		Object fieldName = CellExpressionParser.parseExpression(field.getName()).evaluate(context);
		if(fieldName != null){
			field.setName(WrappingUtil.wrapFormula(fieldName.toString(), context));
		}
	}

	private void translateCaption(XLXContext context,
			CTPivotTableDefinition ctPivotTable) throws Exception {

		Object caption = CellExpressionParser.parseExpression(ctPivotTable.getDataCaption()).evaluate(context);
		if(caption != null){
			ctPivotTable.setDataCaption(caption.toString());
		}
	}

	private void savePivotCache(XLXContext context, List<String> savedParts,
			POIXMLDocumentPart pc) throws Exception {
		if ( pc.getPackageRelationship().getRelationshipType().equals(XLSXFactory.PIVOT_CACHE.getRelation())){

			PivotTableCache cache = (PivotTableCache) pc;
			CTPivotCacheDefinition ctPivotTableCache = cache.getCtPivotTable();

			for(CTCacheField field : ctPivotTableCache.getCacheFields().getCacheFieldArray()){
				Object fieldName = CellExpressionParser.parseExpression(field.getName()).evaluate(context);
				if(fieldName != null){
					field.setName(WrappingUtil.wrapTableColumnName(fieldName.toString()));
				}
				if(field.getFormula() != null){
					field.setFormula(WrappingUtil.wrapFormula(field.getFormula(), context));
				}
			}

			String casheEntryName = getEntryName(cache.getPackagePart());
			savedParts.add(casheEntryName);
			out.putNextEntry(new ZipEntry(casheEntryName));
			cache.writeTo(out);
			out.closeEntry();
		}
	}

    private XSSFSheet nextSheet(
			ReportContext reportContext,
			List<String> savedParts,
			int index
			)
					throws  Exception {

		XSSFSheet sheet = workBook.getSheetAt(index);
		String entryName = getEntryName(sheet.getPackagePart());
		savedParts.add(entryName);
		out.putNextEntry(new ZipEntry(entryName));

		SheetParser parser = new XLSXSheetParser(reportContext);
		
        parser.setMacroParser(new MacroParser(new MacroNodeFactoryImpl(sheet)));
		Node root = parser.parse(sheet);
		File sheetData = IOUtils.createTempFile("sheetData");
		try {
			OutputStream sheetOut = new FileOutputStream(sheetData);
			try {
				XLXContext context = new XLXContext(sst,sheet,reportContext,sheetOut);
			
				context.setDataProvider(getDataProvider());
				context.setTemplateProvider(getTemplateProvider());
				context.setFormatProvider(getFormatProvider());
				context.setStyles();
				context.setMergedCells(parser.getMergedCells());
				context.setRepeatingRows(XSSFSheetHelper.getRepeatingRows(sheet.getWorkbook(), sheet.getWorkbook().getSheetIndex(sheet.getSheetName())));
				translateSheetName(sheet, context);

				root.process(context);

				setColWidths(sheet, context);
				context.flush();

				writeHead(sheet);
				IOUtils.copy(sheetData, out);
				out.closeEntry();

				savePivots(sheet, context, savedParts);
				saveTables(savedParts, context, parser);

			} finally {
				sheetOut.close();
			}

		} finally {
			IOUtils.delete(sheetData);
		}
		return sheet;
	}

	private void writeHead(XSSFSheet sheet) throws IOException {
		String str = toString(sheet);
		int index = str.indexOf("<cols/>");
		if(index > 0){
			out.write((str.substring(0, index) + "<sheetData>").getBytes(StandardCharsets.UTF_8));
		}else {
			out.write(str.substring(0, str.indexOf("<row")).getBytes(StandardCharsets.UTF_8));
		}
	}

	private String toString(XSSFSheet sheet) {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();

		XmlOptions xmlOptions = new XmlOptions(DEFAULT_XML_OPTIONS);
		xmlOptions.setSaveSyntheticDocumentElement(new QName(CTWorksheet.type.getName().getNamespaceURI(), "worksheet"));
		Map<String, String> map = new HashMap<String, String>();
		map.put(STRelationshipId.type.getName().getNamespaceURI(), "r");
		xmlOptions.setSaveSuggestedPrefixes(map);
		try {
			sheet.getCTWorksheet().save(bout, xmlOptions);
		} catch (IOException e) {
			throw new ReportException(e);
		}
		return new String(bout.toByteArray(), StandardCharsets.UTF_8);
	}

	private void setColWidths(XSSFSheet sheet, XLXContext context) {
		Map<Integer,Double> columnWidths = context.getColumnWidths();
		ColumnHelper helper = new ColumnHelper(sheet.getCTWorksheet());
		for (Entry<Integer, Double> entry : columnWidths.entrySet()) {
			double width = entry.getValue();
			if(width != -1){
				helper.setColBestFit(entry.getKey(), true);
				helper.setCustomWidth(entry.getKey(), true);
				helper.setColWidth(entry.getKey(), width);
			}
		}
	}

	private void translateSheetName(XSSFSheet sheet, XLXContext context)
			throws Exception {

		for( CTSheet next : sheet.getWorkbook().getCTWorkbook().getSheets().getSheetArray()){
			if(next.getName().equals(sheet.getSheetName())){
				String rawName = next.getName();
				XLSXExpression exp = CellExpressionParser.parseExpression(next.getName());
				String translatedName = (String) exp.evaluate(context);
				next.setName(translatedName);
				translateSheetName(sheet, rawName, translatedName);
				break;
			}
		}
	}

	private void translateSheetName(XSSFSheet sheet, String rawName,
			String translatedName) {

		ExtendedProperties extendedProperties = sheet.getWorkbook().getProperties().getExtendedProperties();
		CTProperties props = extendedProperties.getUnderlyingProperties();
		String[] sheetNames = props.getTitlesOfParts().getVector().getLpstrArray();
		int i = 0;
		for( String sheetName : sheetNames ){
			if(rawName.equals(sheetName)){
				sheetNames[i] = translatedName;
				break;
			}
			i++;
		}
	}

	private void saveTables(List<String> savedParts, XLXContext context, SheetParser parser) throws Exception {
        saveTables(savedParts, context);
        markUnprocessedTablesAsSaved(savedParts, parser);
    }

    private void saveTables(List<String> savedParts, XLXContext context) throws IOException {
        for (Table table : context.getTables()) {
            String entryName = getEntryName(table.getPackagePart());
            wrapColumns(table);
            savedParts.add(entryName);
            out.putNextEntry(new ZipEntry(entryName));
            table.writeTo(out);
            out.closeEntry();
        }
    }

    private void wrapColumns(Table table) {
        CTTable ctTable = table.getCTTable();
        for (CTTableColumn column : ctTable.getTableColumns().getTableColumnArray()) {
            column.setName(WrappingUtil.wrapTableColumnName(column.getName()));
        }
    }

    private void markUnprocessedTablesAsSaved(List<String> savedParts, SheetParser parser) {
        for (Table table : parser.getTables()) {
            String entryName = getEntryName(table.getPackagePart());
            if (!savedParts.contains(entryName)) {
                savedParts.add(entryName);
            }
        }
    }

    private String getEntryName(PackagePart packagePart) {
		return ZipHelper.getZipItemNameFromOPCName(packagePart.getPartName().getName());
	}

	
	
}
