/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.header;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;

import com.exigeninsurance.x4j.analytic.api.ReportContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.header.rule.HeaderFooterApplicabilityRule;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLXContext;

public class PdfHeaderFooterTest {

	private XLXContext context;
	private Map<String,Object> parameters;

	private class MockHeaderFooter extends PdfHeaderFooter {
	
		public MockHeaderFooter(String contents, XLXContext context, HeaderFooterApplicabilityRule applicabilityRule) throws Exception {
			super(contents, context, applicabilityRule);
		}
		
		@Override
		public void draw(RenderingContext context) throws Exception {
			
		}

        @Override
        protected String getCode() {
            return "mock";
        }
    }

	@Before
	public void setUp() throws Exception {
		XSSFSheet sheet = new XSSFWorkbook().createSheet();
		parameters = new HashMap<String, Object>();
		ReportContext reportContext = new ReportContext(null);
		context = new XLXContext(null, sheet, reportContext, null);
	}

	@Test
	public void testSectionParse() throws Exception {
		String contents = "&Ltest";
		PdfHeaderFooter headerFooter = new MockHeaderFooter(contents, context, null);
		List<HeaderFooterSection> sections = headerFooter.getSections();
		assertEquals(1, sections.size());
		HeaderFooterSection section = sections.get(0);
		assertEquals("test", section.getContents());
	}
	
	@Test
	public void testMultipleSections() throws Exception {
		String contents = "&Ltest&Ctest2";
		PdfHeaderFooter headerFooter = new MockHeaderFooter(contents, context, null);
		List<HeaderFooterSection> sections = headerFooter.getSections();
		assertEquals(2, sections.size());
		HeaderFooterSection section = sections.get(0);
		assertEquals("test", section.getContents());
		section = sections.get(1);
		assertEquals("test2", section.getContents());
	}
	
	@Test
	public void testNoSectionFormatting() throws Exception {
		String contents = "test";
		PdfHeaderFooter headerFooter = new MockHeaderFooter(contents, context, null);
		List<HeaderFooterSection> sections = headerFooter.getSections();
		assertEquals(1, sections.size());
		HeaderFooterSection section = sections.get(0);
		assertEquals("test", section.getContents());
	}
	
	@Test
	public void testSectionsAndOtherInstructions() throws Exception {
		String contents = "&L&P of &N&C&Ptest";
		PdfHeaderFooter headerFooter = new MockHeaderFooter(contents, context, null);
		List<HeaderFooterSection> sections = headerFooter.getSections();
		assertEquals(2, sections.size());
		HeaderFooterSection section = sections.get(0);
		assertEquals("&P of &N", section.getContents());
		section = sections.get(1);
		assertEquals("&Ptest", section.getContents());
	}
	
	@Test
	public void testAmpersands() throws Exception {
		String contents = "&L&&P&C&&a";
		PdfHeaderFooter headerFooter = new MockHeaderFooter(contents, context, null);
		List<HeaderFooterSection> sections = headerFooter.getSections();
		assertEquals(2, sections.size());
		HeaderFooterSection section = sections.get(0);
		assertEquals("&&P", section.getContents());
		section = sections.get(1);
		assertEquals("&&a", section.getContents());
	}
	
	@Test
	public void testJexlExpressions() throws Exception {
		String contents = "&L${translation.test}";

		Map <String, String> translations = new HashMap<String, String>();
		translations.put("test", "test");
		context.getReportContext().getParameters().put("translation", translations);
		PdfHeaderFooter headerFooter = new MockHeaderFooter(contents, context, null);
		List<HeaderFooterSection> sections = headerFooter.getSections();
		assertEquals(1, sections.size());
		HeaderFooterSection section = sections.get(0);
		assertEquals("test", section.getContents());
	}
}
