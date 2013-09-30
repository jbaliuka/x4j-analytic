
/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.header;

import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFSheet;

import com.exigeninsurance.x4j.analytic.xlsx.core.expression.XLSXExpression;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.DrawablePdfElement;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfContainer;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.header.rule.HeaderFooterApplicabilityRule;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLXContext;
import com.exigeninsurance.x4j.analytic.xlsx.utils.CellExpressionParser;
import com.exigeninsurance.x4j.analytic.xlsx.utils.SectionParser;


public abstract class PdfHeaderFooter implements DrawablePdfElement {

    protected String code;
	private PdfContainer parent;

	private List<HeaderFooterSection> sections;

	private HeaderFooterApplicabilityRule applicabilityRule;
	private final XLXContext context;
	private float heigth;
	
	public PdfHeaderFooter(String contents, XLXContext context, HeaderFooterApplicabilityRule applicabilityRule) throws Exception {
		this.context = context;
		contents = evaluateExpressions(contents);
        sections = SectionParser.parseSections(contents, getCode());
		this.applicabilityRule = applicabilityRule;
	}
	
	private String evaluateExpressions(String contents) throws Exception {
		XLSXExpression expr = CellExpressionParser.parseExpression(contents);
		return expr.evaluate(context).toString();
	}

	public abstract void draw(RenderingContext context) throws Exception;

    protected abstract String getCode();

	public float estimateWidth(RenderingContext context)  {
		return 0f;
	}

	public float estimateHeight(RenderingContext context)  {
		float max = 0f;
		for (DrawablePdfElement section : sections) {
			float sectionHeigth = section.estimateHeight(context);
			if (sectionHeigth > max) {
				max = sectionHeigth;
			}
		}
		heigth = max;
		return max;
	}

	public void notify(PdfContext context) {
		
	}
	
	public boolean isApplicable(int pageNumber, XSSFSheet sheet) {
		return applicabilityRule.isApplicable(pageNumber, sheet);
	}

	public HeaderFooterApplicabilityRule getApplicabilityRule() {
		return applicabilityRule;
	}

	public void setApplicabilityRule(HeaderFooterApplicabilityRule applicabilityRule) {
		this.applicabilityRule = applicabilityRule;
	}

	@Override
	public void setParent(PdfContainer element) {
		parent = element;
	}

	@Override
	public PdfContainer getParent() {
		return parent;
	}

	@Override
	public float getHeigth() {
		return heigth;
	}

    public List<HeaderFooterSection> getSections() {
        return sections;
    }

    public void process(PdfContext pdfContext) {
        for (HeaderFooterSection section : getSections()) {
            section.process(pdfContext);
        }
    }
}
