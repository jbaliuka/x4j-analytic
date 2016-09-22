/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
 */


package com.exigeninsurance.x4j.analytic.xlsx.transform;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.openxml4j.opc.ZipPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.exigeninsurance.x4j.analytic.api.ReportContext;
import com.exigeninsurance.x4j.analytic.api.ReportDataProvider;
import com.exigeninsurance.x4j.analytic.api.TemplateResolver;
import com.exigeninsurance.x4j.analytic.api.Transform;
import com.exigeninsurance.x4j.analytic.util.IOUtils;
import com.exigeninsurance.x4j.analytic.xlsx.core.localization.FormatProvider;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLSXStylesTable;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLSXWorkbook;


abstract public class BaseTransform implements Transform {


	private static final Logger log = LoggerFactory.getLogger(BaseTransform.class);

	private ReportDataProvider dataProvider;	
	private TemplateResolver templateProvider;

	private FormatProvider formatProvider;

	public final void process(ReportContext reportContext, InputStream in, File saveTo) throws Exception {
		File template = createWorkbookFile();

		try {
			IOUtils.copy(in, template);
			XLSXWorkbook workbook  = new XLSXWorkbook(template);
			importStyles(reportContext,workbook);
			try {


				formatProvider = new FormatProvider(reportContext.getMetadata());
				doProcess(workbook,reportContext, saveTo);
			} 
			finally {				

				closeWorbook(workbook);
			}
		}
		finally {			
			IOUtils.delete(template);

		}
	}

	private void importStyles(ReportContext reportContext, XLSXWorkbook workbook) throws IOException {

		for(String styleTemplate: reportContext.getStyles()){

			InputStream is =  templateProvider.openTemplate(styleTemplate); 
			try {
				File file = IOUtils.createTempFile("style");
				try{
					IOUtils.copy(is, file);
					XLSXWorkbook stylesWorkBook = new XLSXWorkbook(file);
					try {
						XLSXStylesTable styles = (XLSXStylesTable) stylesWorkBook.getStylesSource();
						((XLSXStylesTable) workbook.getStylesSource()).importStyles(styles);
					}finally{
						closeWorbook(stylesWorkBook);
					}
				}finally{
					IOUtils.delete(file);
				}
			} finally {
				is.close();

			}

		}

	}

	protected void closeWorbook(XLSXWorkbook workbook) {
		if (workbook != null && workbook.getPackage() != null && ((ZipPackage)workbook.getPackage()).getZipArchive() != null ) {
			try{
				workbook.getPackage().revert();
			}catch(Exception e){
				if(log.isDebugEnabled()){
					log.debug(e.getMessage(), e);
				}
			}
		}
	}

	protected abstract void doProcess(XLSXWorkbook workbook,ReportContext reportContext, File saveTo) throws Exception;

	protected abstract File createWorkbookFile() throws Exception;



	public void setDataProvider(ReportDataProvider dataProvider) {
		this.dataProvider = dataProvider;
	}

	public void setTemplateProvider(TemplateResolver templateProvider) {
		this.templateProvider = templateProvider;
	}



	public ReportDataProvider getDataProvider() {
		return dataProvider;
	}

	public TemplateResolver getTemplateProvider() {
		return templateProvider;
	}



	public FormatProvider getFormatProvider() {
		return formatProvider;
	}

	public void setFormatProvider(FormatProvider formatProvider) {
		this.formatProvider = formatProvider;
	}
}
