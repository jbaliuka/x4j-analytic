/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.exigeninsurance.x4j.analytic.api.ReportContext;
import com.exigeninsurance.x4j.analytic.xlsx.core.node.Node;
import com.exigeninsurance.x4j.analytic.xlsx.transform.Picture;
import com.exigeninsurance.x4j.analytic.xlsx.transform.WorkbookProcessor;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.header.PdfFooter;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.header.PdfHeader;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.header.rule.EvenHeaderFooterRule;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.header.rule.FirstHeaderFooterRule;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.header.rule.OddFooterRule;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.header.rule.OddHeaderRule;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLSXWorkbook;
import com.exigeninsurance.x4j.analytic.xlsx.utils.MacroNodeFactory;
import com.exigeninsurance.x4j.analytic.xlsx.utils.MacroNodeFactoryImpl;
import com.exigeninsurance.x4j.analytic.xlsx.utils.MacroParser;
import com.exigeninsurance.x4j.analytic.xlsx.utils.PictureParser;


public class PdfProcessor extends WorkbookProcessor {

	private final XLSXWorkbook workBook;
		
	private PdfContext context;
    private List<Integer> sheetBreaks = new ArrayList<Integer>();
    private ReportContext reportContext;

    public PdfProcessor(XLSXWorkbook workBook) {
		
		this.workBook = workBook;
	}

	public void processWorkbook(ReportContext reportContext) throws Exception {
        this.reportContext = reportContext;
        processSheets();
        appendHeadersAndFooters();
	}

    private void processSheets() throws Exception {
        for(int i = 0; i < workBook.getNumberOfSheets(); i++){
            processSheet(workBook.getSheetAt(i));
        }
    }

    private void processSheet(XSSFSheet sheet) throws Exception {
        preProcess(sheet);
        process(sheet);
        context.getMaxColumnWidths().clear();
        context.setMaxRowWidth(0);
    }

    private void preProcess(XSSFSheet sheet) throws Exception {
        parseSheetAndProcess(sheet, new PreProcessingComponentFactory(), new PreprocessingMacroFactory(sheet));
    }

    private void process(XSSFSheet sheet) throws Exception {
        parseSheetAndProcess(sheet, new ComponentFactoryImpl(), new MacroNodeFactoryImpl(sheet));
        flush();
        sheetBreaks.add(context.getTotalPageCount() - 1);
    }

    private void parseSheetAndProcess(XSSFSheet sheet, ComponentFactory componentFactory, MacroNodeFactory macroNodeFactory) throws Exception {
        PdfSheetParser parser = new PdfSheetParser( reportContext);
        
        parser.setMacroParser(new MacroParser(macroNodeFactory));
        parser.setComponentFactory(componentFactory);
        Node root = parser.parse(sheet);
        prepareContextForNewSheet(sheet, parser);
        new PdfLayoutTransformer(context, componentFactory).transform(root);
        root.process(context);
    }

    private void flush() throws Exception {
        if (!context.allItemsRendered()) {
            context.renderCurrentPage();
        }
        context.sheetOver();
    }

    private void prepareContextForNewSheet(XSSFSheet sheet, PdfSheetParser parser) throws Exception {
		if(context == null) {
			createContext(sheet);
            prepareNewSheet(sheet, parser);
		}
		else {
			prepareNewSheet(sheet, parser);
		}
	}

	private void createContext(XSSFSheet sheet) throws Exception {
		context = new PdfContext(null,sheet, reportContext);
		context.prepareForRendering(sheet);
		
		context.setDataProvider(getDataProvider());
		context.setTemplateProvider(getTemplateProvider());
		context.setFormatProvider(getFormatProvider());
		context.parseTableStyles(workBook);
		context.setStyles();
		context.setHeaderMap(extractHeaders(workBook));
		context.setFooterMap(extractFooters(workBook));
		context.setColumnWidths(workBook);
        context.setHeaderFooterPictureMap(parseHeaderFooterPictures(sheet.getWorkbook()));
		context.resetRenderingState();
	}

	private void prepareNewSheet(XSSFSheet sheet, PdfSheetParser parser) {
		context.setSheet(sheet);
		context.setMergedCells(parser.getMergedCells());
        context.setRepeatingItems(parser.getRepeatingItems());
        context.updatePageSetup(sheet);
	}

    private void appendHeadersAndFooters() throws Exception {
        List<XSSFSheet> sheets = getSheets(workBook);
        int startPage = 0;
        int endPage;
        for (int i = 0; i < sheets.size(); i++) {
            endPage = sheetBreaks.get(i);
            context.insertHeadersAndFooters(sheets.get(i), startPage, endPage);
            startPage = endPage + 1;
        }
    }

	
	

	public void saveDocument(File file) throws COSVisitorException,
		IOException {		
			context.saveDocument(file.getCanonicalPath());		
	}

	private List<XSSFSheet> getSheets(XSSFWorkbook book) {
		List<XSSFSheet> sheets = new ArrayList<XSSFSheet>();
		for (int i = 0; i < book.getNumberOfSheets(); i++) {
			sheets.add(book.getSheetAt(i));
		}
		return sheets;
	}
	
	private Map<XSSFSheet, List<PdfFooter>> extractFooters(XSSFWorkbook book) throws Exception {
		Map<XSSFSheet, List<PdfFooter>> headerMap = new HashMap<XSSFSheet, List<PdfFooter>>();
		for (int i = 0; i < book.getNumberOfSheets(); i++) {
			XSSFSheet sheet = book.getSheetAt(i);
			List<PdfFooter> footers = new ArrayList<PdfFooter>();
			if (sheet.getCTWorksheet().getHeaderFooter() != null) {
				String firstFooter = sheet.getCTWorksheet().getHeaderFooter().getFirstFooter();
				if (firstFooter != null) {
					footers.add(new PdfFooter(firstFooter, context, new FirstHeaderFooterRule()));
				}
				
				String evenFooter = sheet.getCTWorksheet().getHeaderFooter().getEvenFooter();
				if (evenFooter != null) {
					footers.add(new PdfFooter(evenFooter, context, new EvenHeaderFooterRule()));
				}
				String oddFooter = sheet.getCTWorksheet().getHeaderFooter().getOddFooter();
				if (oddFooter != null) {
					footers.add(new PdfFooter(oddFooter, context, new OddFooterRule()));
				}
			}
			headerMap.put(sheet, footers);
		}
		return headerMap;
	}

	private Map<XSSFSheet, List<PdfHeader>> extractHeaders(XSSFWorkbook book) throws Exception {
		Map<XSSFSheet, List<PdfHeader>> headerMap = new HashMap<XSSFSheet, List<PdfHeader>>();
		for (int i = 0; i < book.getNumberOfSheets(); i++) {
			XSSFSheet sheet = book.getSheetAt(i);
			List<PdfHeader> headers = new ArrayList<PdfHeader>();
			if (sheet.getCTWorksheet().getHeaderFooter() != null) {
			String firstHeader = sheet.getCTWorksheet().getHeaderFooter().getFirstHeader();
				if (firstHeader != null) {
					headers.add(new PdfHeader(firstHeader, context, new FirstHeaderFooterRule()));
				}
				String evenHeader = sheet.getCTWorksheet().getHeaderFooter().getEvenHeader();
				if (evenHeader != null) {
					headers.add(new PdfHeader(evenHeader, context, new EvenHeaderFooterRule()));
				}
				String oddHeader = sheet.getCTWorksheet().getHeaderFooter().getOddHeader();
				if (oddHeader != null) {
					headers.add(new PdfHeader(oddHeader, context, new OddHeaderRule()));
				}
			}
			headerMap.put(sheet, headers);
			
		}
		return headerMap;
	}

    private Map<XSSFSheet, Map<String, Picture>> parseHeaderFooterPictures(XSSFWorkbook book) throws Exception {
        Map<XSSFSheet, Map<String, Picture>> map = new HashMap<XSSFSheet, Map<String, Picture>>();
        for (int i = 0; i < book.getNumberOfSheets(); i++) {
            XSSFSheet sheet = book.getSheetAt(i);
            map.put(sheet, PictureParser.parseLegacyPicturesFromSheet(sheet));

        }
        return map;
    }

	public void close() throws IOException {
		if (context != null) {
			context.cleanUp();
		}
	}

    
}
