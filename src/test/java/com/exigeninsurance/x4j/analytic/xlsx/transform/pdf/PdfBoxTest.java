/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.interactive.action.type.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;
import org.junit.Test;

public class PdfBoxTest {
	
	@Test
	public void testHyperlinks() throws IOException, COSVisitorException {
		String fileName = "testPdfHyperlinks.pdf";
		String text = "A link for Testing";
		PDDocument document = null;
		
		try {
			document = new PDDocument();
			PDPage page = new PDPage();
			List annotations = page.getAnnotations();
			
			PDPageContentStream stream = new PDPageContentStream(document, page);
			PDType1Font font = PDType1Font.TIMES_ROMAN;
			int fontSize = 11;
			stream.setFont(font, fontSize);
			float fontDescent = font.getFontDescriptor().getDescent() / 1000 * fontSize;
			
			try {
				
				float startX = 0f;
				float startY = 0f - fontDescent;
				float textWidth = font.getStringWidth(text) / 1000 * fontSize;
				
				float textHeight = (font.getFontDescriptor().getAscent() / 1000 * fontSize) - fontDescent;
				
				stream.beginText();
				stream.moveTextPositionByAmount(startX, startY);
				stream.drawString(text);
				stream.endText();
				
				PDAnnotationLink linkAnnotation = new PDAnnotationLink();
				PDRectangle rectangle = new PDRectangle();
				rectangle.setLowerLeftX(startX);
				rectangle.setLowerLeftY(startY);
				rectangle.setUpperRightX(startX + textWidth);
				rectangle.setUpperRightY(startY + textHeight + fontDescent);
				linkAnnotation.setRectangle(rectangle);
				PDBorderStyleDictionary bs = new PDBorderStyleDictionary();
				bs.setWidth(0);
				linkAnnotation.setBorderStyle(bs);
				
				PDActionURI action = new PDActionURI();
				action.setURI("http://www.google.lt");
				linkAnnotation.setAction(action);
				
				annotations.add(linkAnnotation);
				document.addPage(page);
				
			} finally {
				stream.close();
			}
			
			document.save(fileName);
			
			
		} finally {
			document.close();
		}
		
		File file = new File(fileName);
		try {
			
			document = PDDocument.load(file);
			PDPage page = (PDPage) document.getDocumentCatalog().getAllPages().get(0);
			List annotations = page.getAnnotations();
			
			assertEquals(1, annotations.size());
		} finally {
			if (document != null) {
				document.close();
			}
			assertTrue(file.delete());
		}
		
		
				
	}

}
