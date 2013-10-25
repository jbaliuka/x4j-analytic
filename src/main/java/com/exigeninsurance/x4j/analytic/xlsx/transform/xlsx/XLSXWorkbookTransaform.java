/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.ZipPackage;
import org.apache.poi.openxml4j.opc.internal.ZipContentTypeManager;
import org.apache.poi.openxml4j.opc.internal.ZipHelper;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.exigeninsurance.x4j.analytic.api.ReportContext;
import com.exigeninsurance.x4j.analytic.util.IOUtils;
import com.exigeninsurance.x4j.analytic.xlsx.transform.BaseTransform;
import com.exigeninsurance.x4j.analytic.xlsx.transform.SST;
import com.exigeninsurance.x4j.analytic.xlsx.transform.UTF8OutputStream;


final public class XLSXWorkbookTransaform extends BaseTransform {

	

	private static final Logger log = LoggerFactory.getLogger(XLSXWorkbookTransaform.class);

	private static final String SST_HEAD = "<?xml version=\"1.0\" encoding=\"UTF-8\" "+
			" standalone=\"yes\" ?>\n" + 
			"<sst xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\" "+
			"count=\"@count@\" uniqueCount=\"@uniqueCount@\">\n  ";

	public void doProcess(XLSXWorkbook workbook,ReportContext reportContext, File saveTo) throws Exception{

		List<String> savedParts = new ArrayList<String>();
		process(reportContext, saveTo, savedParts, workbook);
	}

	protected File createWorkbookFile(ReportContext reportContext,
			InputStream in, File saveTo) throws Exception {
		return IOUtils.createTempFile("template");
	}

	private void process(ReportContext reportContext, File saveTo,
			List<String> savedParts, XLSXWorkbook workBook)
					throws
            Exception {
		
		ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(saveTo)));
		out.setLevel(Deflater.BEST_SPEED);

		try{

			File temtFile = IOUtils.createTempFile("stt");
			try{

				UTF8OutputStream sstOut = new UTF8OutputStream( new BufferedOutputStream( new FileOutputStream( temtFile )) );
				SST sst = null;

				try{

					writeEmptyHead(sstOut);

					sst = new SST(sstOut,workBook.getSharedStringSource());



					ZipContentTypeManager manager = new ZipContentTypeManager(null,null);

					

					XLSXProcessor processor = new XLSXProcessor(workBook,out,sst);
					processor.setDataProvider(getDataProvider());
					processor.setTemplateProvider(getTemplateProvider());					
					processor.setFormatProvider(getFormatProvider());
					processor.processSheets(reportContext, savedParts,manager );

                    for (PackagePart next : workBook.getPackage().getParts()) {
                        manager.addContentType(next.getPartName(), next.getContentType());
                    }


					XLSXMarshaler marshaler = new XLSXMarshaler((ZipPackage) workBook.getPackage(),workBook);
					marshaler.marshalParts(savedParts, out);
					manager.save(out);

					sstOut.writeUTF("</sst>");
					sstOut.flush();

				}catch(Exception e){
					log.error(e.getMessage(),e);
					throw e;
				}finally{

					sstOut.close();
				}


				updateHead(sst, temtFile);
				copyStrings(workBook, out, temtFile);


			}finally{
				IOUtils.delete(temtFile);
			}
		}finally{
			out.close();
		}
	}





	public void copyStrings(XSSFWorkbook workBook, ZipOutputStream out,
			File temtFile) throws Exception {

		InputStream strings = new BufferedInputStream( new FileInputStream(temtFile) );
		try{
			out.putNextEntry(new ZipEntry(getEntryName(workBook.getSharedStringSource().getPackagePart())));				
			IOUtils.copy(strings, out);
			out.closeEntry();
		}finally{
			strings.close();
		}
	}

	public void updateHead(SST sst, File temtFile)
			throws Exception {

		RandomAccessFile raf = new RandomAccessFile(temtFile, "rw");
		try{
			raf.seek(0);
			String head = SST_HEAD.replaceFirst("@count@",
					Long.toString(sst.getCount()));

			head = head.replaceFirst("@uniqueCount@",
					Long.toString(sst.getUnique()));

			raf.write(head.getBytes());
		}finally{
			raf.close();
		}
	}

	public void writeEmptyHead(OutputStream sstOut) throws IOException {

		for(int i = 0; i < SST_HEAD.length() + 20 ; i++){
			sstOut.write((byte)9);
		}

	}

	private String getEntryName(PackagePart packagePart) {
		return ZipHelper.getZipItemNameFromOPCName(packagePart.getPartName().getName());

	}






}
