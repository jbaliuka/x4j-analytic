/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf;

import static com.exigeninsurance.x4j.analytic.util.MockResultSet.cols;
import static com.exigeninsurance.x4j.analytic.util.MockResultSet.data;
import static com.exigeninsurance.x4j.analytic.util.MockResultSet.row;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.exigeninsurance.x4j.analytic.api.ReportContext;
import com.exigeninsurance.x4j.analytic.model.Query;
import com.exigeninsurance.x4j.analytic.util.MockReportDataProvider;
import com.exigeninsurance.x4j.analytic.util.MockResultSet;
import com.exigeninsurance.x4j.analytic.util.ResultSetWrapper;
import com.exigeninsurance.x4j.analytic.xlsx.transform.TransformationTest;

public class XLSXWorkbookToPdfTransformTest extends TransformationTest{
	
	private static final String BASIC_LONG = "basiclong";
    private static final String TABLE_HEADER_PAGE_WRAPPING = "testTableHeaderWrapping";
    private static final String STRESS_TEST = "pdfstress";
    public static final String PDF_FORMATTING = "PDFFormatting";
    private HashMap<String,Object> parameters;

    private File file;
	private PDDocument document;

    public XLSXWorkbookToPdfTransformTest() {
        parameters = new HashMap<String, Object>();
    }

    @Before
	public void setup() throws IOException {
		super.setup();
	}
	
	@After
	public void cleanUp() throws IOException {
        if (file != null) {
            if (!testingFormatting()) {
		        file.delete();
            }
        }
		if (document != null) {
			document.close();
		}

        super.teardown();
	}
	
	@Test
	public void testImages() throws Exception {
	
		ResultSet rs = MockResultSet.create(cols("A", "B"),
				data(row(1, 3)), 100);

		String fileName = "image2";
		file = new File(fileName + ".pdf");
		XLSXWorkbookToPdfTransform transform = getTransform(rs);
		InputStream input = getExcelTemplate(fileName);

		transform.process(getReportContext(), input, file);

		document = PDDocument.load(file);
		List<?> pages = document.getDocumentCatalog().getAllPages();
		PDPage page = (PDPage) pages.get(0);
		assertTrue("no images found", !page.getResources().getXObjects().isEmpty());
	}

    @Test
    public void testStress() throws Exception {
        if(System.getProperty("PDFStress") != null){
            int rows = 3000000;
            ResultSet rs = MockResultSet.create(cols("A", "B", "C", "D", "E", "F"),
                    data(row("1", "2", "3", "4", "5", "6")), rows);

            String fileName = STRESS_TEST;
            file = new File(fileName + ".pdf");
            XLSXWorkbookToPdfTransform transform = getTransform(rs);
            InputStream input = getExcelTemplate(fileName);
            long start = System.currentTimeMillis();
            transform.process(getReportContext(), input, file);
            long end = System.currentTimeMillis();
            System.out.print("Stress test for PDF report with " + rows + " rows took : " + (end - start) / 1000 + " seconds");

        }
    }
	
	@Test
	public void testBasicProcessing() throws Exception {

		int lines = 5;
		ResultSet rs = MockResultSet.create(cols("A", "B", "C", "D"), data(row(
				"", 2, 333333333, 1)),
				lines);

		String fileName = BASIC_LONG;
		file = new File(fileName + ".pdf");
		XLSXWorkbookToPdfTransform transform = getTransform(rs);
        parameters.put("rows", ResultSetWrapper.wrap(rs));
		
		transform.process(getReportContext(), getExcelTemplate(fileName), file);
		
		document = PDDocument.load(file);

		PageContents contents = PageStripper.extractPageContents(document, 0);

        assertWordLineEquals(contents.getLine(0), "A", "B", "C", "D");

        assertEquals("incorrect amount of lines have been rendered", lines + 2, contents.getNumberOfLines());
	}

    @Test
    public void testTableHeaderPageWrapping() throws Exception {
        int lines = 100;
        ResultSet rs = MockResultSet.create(cols("A", "B", "C", "D"), data(row(
                "1", "2", "3", "4")),
                lines);

        String fileName = TABLE_HEADER_PAGE_WRAPPING;
        file = new File(fileName + ".pdf");
        XLSXWorkbookToPdfTransform transform = getTransform(rs);

        transform.process(getReportContext(), getExcelTemplate(fileName), file);

        document = PDDocument.load(file);

        String[] expectedFirstLine = {"A", "B", "C", "D"};

        PageContents firstPageContents = PageStripper.extractPageContents(document, 0);
        assertWordLineEquals(firstPageContents.getLine(0), expectedFirstLine);

        PageContents secondPageContents = PageStripper.extractPageContents(document, 1);
        assertWordLineEquals(secondPageContents.getLine(0), expectedFirstLine);
    }
	
	@Test
	public void testGrouping() throws Exception {
        ResultSet rs = MockResultSet.create(cols("A", "B", "C"), data(
                row("A", "AA", "1"),
                row("A", "AA", "2"),
                row("A", "BB", "3"),
                row("A", "BB", "4"),
                row("B", "CC", "5"),
                row("B", "CC", "6"),
                row("B", "DD", "7"),
                row("B", "DD", "8")));

		String fileName = "testPdfGroups";
		file = new File(fileName + ".pdf");
		XLSXWorkbookToPdfTransform transform = getTransform(rs);

		parameters.put("rows", ResultSetWrapper.wrap(rs));
		ReportContext reportContext = createContext(parameters);
		
		transform.process(reportContext, getExcelTemplate(fileName), file);
		
		document = PDDocument.load(file);
		PageContents contents = PageStripper.extractPageContents(document, 0);
		
		List<Word> firstLine = contents.getLine(0);
		assertEquals(1, firstLine.size());
		Word firstElement = firstLine.get(0);
		assertEquals("A", firstElement.toString());
		
		List<Word> secondLine = contents.getLine(1);
		assertEquals(1, secondLine.size());
		Word secondElement = secondLine.get(0);
		assertEquals("AA", secondElement.toString());
		
		assertTrue(secondElement.getX() > firstElement.getX());
		
		List<Word> thirdLine = contents.getLine(2);
		assertEquals(1, thirdLine.size());
		Word thirdElement = thirdLine.get(0);
		assertEquals("1", thirdElement.toString());
		
		List<Word> fourthLine = contents.getLine(3);
		assertEquals(1, fourthLine.size());
		Word fourthElement = fourthLine.get(0);
		assertEquals("2", fourthElement.toString());
		
		assertTrue(fourthElement.getX() == thirdElement.getX());
		
	}
	
	@Test
	public void testTotals() throws Exception {
		ResultSet rs = MockResultSet.create(cols("A", "B", "C", "D"), data(
				row(1, 3, 2, 4),
				row(1, 2, 3, 4)));
		
		String fileName = "testTotals";
		file = new File(fileName + ".pdf");
		XLSXWorkbookToPdfTransform transform = getTransform(rs);
		
		transform.process(getReportContext(), getExcelTemplate(fileName), file);
		
		document = PDDocument.load(file);
		
		PageContents contents = PageStripper.extractPageContents(document, 0);
		
		assertEquals(4, contents.getNumberOfLines());

		List<Word> totalsLine = contents.getLine(3);
		
		assertEquals(3, totalsLine.size());

	}
	
	@Test
	public void testMergedCells() throws Exception {
        ResultSet rs = MockResultSet.create(cols("A", "B", "C", "D"), data(
                row("1", "2", "3", "4"),
                row("1", "2", "3", "4")));
		
		String fileName = "testPdfMergedCells";
		file = new File(fileName + ".pdf");
		XLSXWorkbookToPdfTransform transform = getTransform(rs);

		parameters.put("rows", ResultSetWrapper.wrap(rs));
		ReportContext reportContext = createContext(parameters);
		transform.process(reportContext , getExcelTemplate(fileName), file);
		
		document = PDDocument.load(file);
		
		PageContents contents = PageStripper.extractPageContents(document, 0);
		assertEquals(4, contents.getNumberOfLines());
		
		List<Word> firstMergedLine = contents.getLine(0);
		assertEquals(1, firstMergedLine.size());
		Word mergedCell = firstMergedLine.get(0);
		assertEquals("1", mergedCell.toString());
		
		List<Word> secondLine = contents.getLine(1);
		assertEquals(3, secondLine.size());
		Word leftChild = secondLine.get(0);
		Word centerChild = secondLine.get(1);
		Word rightChild = secondLine.get(2);
		
		assertEquals(mergedCell.getX(), centerChild.getX(), 0.1f);
		assertTrue(mergedCell.getX() > leftChild.getX() && mergedCell.getX() < rightChild.getX());
	}
	
	@Test
	public void testHeaders() throws Exception {
		ResultSet rs = MockResultSet.create(cols("A", "B", "C", "D"), data(
				row("1", "2", "3", "4")
				), 100);
		
		String fileName = "headerPostProcess";
		file = new File(fileName + ".pdf");
		XLSXWorkbookToPdfTransform transform = getTransform(rs);

		transform.process(getReportContext() , getExcelTemplate(fileName), file);
		
		document = PDDocument.load(file);
        int size = document.getDocumentCatalog().getAllPages().size();

        for (int p = 0; p < size / 2; p++) {
            PageContents pageContents = PageStripper.extractPageContents(document, p);
            if (p == 0) {
                assertTrue(pageContents.containsWord("first"));
            } else if ((p + 1) % 2 == 0) {
                assertTrue(pageContents.containsWord("even"));
            } else {
                assertTrue(pageContents.containsWord("odd"));
            }
            assertTrue(pageContents.containsWord((p + 1) + " of " + size));
        }

        for (int p = size / 2; p < size; p++) {
            PageContents pageContents = PageStripper.extractPageContents(document, p);
            assertTrue(pageContents.containsWord((p + 1) + " of " + size));
        }
	}

    @Test
    public void testHeaderPicture() throws Exception {
        ResultSet rs = MockResultSet.create(cols("ID", "B"), data(
                row("1", "2")
        ), 10);

        String fileName = "test_header_picture";
        file = new File(fileName + ".pdf");
        XLSXWorkbookToPdfTransform transform = getTransform(rs);

        transform.process(getReportContext() , getExcelTemplate(fileName), file);

        document = PDDocument.load(file);

        List<?> pages = document.getDocumentCatalog().getAllPages();

        for (Object obj : pages) {
            PDPage page = (PDPage) obj;
            assertFalse(page.getResources().getXObjects().isEmpty());
        }
    }

    @Test
    public void testRepeatingRows() throws Exception {
        ResultSet rs = MockResultSet.create(cols("A", "B"), data(
                row("1", "2")
        ), 200);

        String fileName = "testPdfRepeatingRows";
        file = new File(fileName + ".pdf");
        XLSXWorkbookToPdfTransform transform = getTransform(rs);

        ReportContext reportContext = getReportContext();
        reportContext.getParameters().put("rows", ResultSetWrapper.wrap(rs));
        transform.process(reportContext, getExcelTemplate(fileName), file);
        document = PDDocument.load(file);

        List pages = document.getDocumentCatalog().getAllPages();

        for (int i = 0; i < pages.size(); i++) {
            PageContents contents = PageStripper.extractPageContents(document, i);
            List<Word> firstLine = contents.getLine(0);
            assertEquals("A", firstLine.get(0).toString());
            assertEquals("B", firstLine.get(1).toString());
        }
    }

    @Test
    public void testFormatting() throws Exception {
        if (testingFormatting()) {
            ResultSet rs = MockResultSet.create(cols("A", "B"), data(
                    row("1", "2")
            ), 200);

            String fileName = "test_formatting";
            file = new File(fileName + ".pdf");
            XLSXWorkbookToPdfTransform transform = getTransform(rs);

            ReportContext reportContext = getReportContext();
            reportContext.getParameters().put("rows", rs);
            transform.process(reportContext, getExcelTemplate(fileName), file);
        }
    }

	@Test
	public void dynamicRows() throws Exception {

		ResultSet rs = MockResultSet.create(cols("A"), data(
				row("short"),
				row("wrapped one time"),
				row("small")));

		String fileName = "dynamicRows";
		file = new File(fileName + ".pdf");
		XLSXWorkbookToPdfTransform transform = getTransform(rs);
		parameters.put("rows", ResultSetWrapper.wrap(rs));

		transform.process(getReportContext(), getExcelTemplate(fileName), file);
	}

    private boolean testingFormatting() {
        return System.getProperty(PDF_FORMATTING) != null;
    }

	private InputStream getExcelTemplate(String fileName) {
		return getClass().getResourceAsStream("/" + fileName + ".xlsx");
	}

	private XLSXWorkbookToPdfTransform getTransform(ResultSet rs) {

		XLSXWorkbookToPdfTransform transform = new XLSXWorkbookToPdfTransform(
				null);
        dataProvider = new MockReportDataProvider(rs);
        transform.setDataProvider(dataProvider);
		
		return transform;
	}

	private ReportContext getReportContext() {
		ArrayList<Query> list = new ArrayList<Query>();
		Query q1 = new Query();
		q1.setName("Table1");
		list.add(q1);
		
		Query q2 = new Query();
		q2.setName("Table2");
		list.add(q2);
		
		Query q3 = new Query();
		q3.setName("Table3");
		list.add(q3);

		Query q4 = new Query();
		q4.setName("Table4");
		list.add(q4);
		
		metadata.setQuery(list);

        return createContext(parameters);
	}
	
	private void assertWordLineEquals(List<Word> line, String ... expected) {
		if (line.size() != expected.length) {
			throw new IllegalArgumentException("line sizes do not match");
		}
		
		for (int i = 0; i < expected.length; i++) {
			assertEquals(expected[i], line.get(i).toString());
		}
	}
}
