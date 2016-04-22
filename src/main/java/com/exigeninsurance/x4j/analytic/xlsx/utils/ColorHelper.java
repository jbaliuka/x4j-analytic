/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
 */


package com.exigeninsurance.x4j.analytic.xlsx.utils;

import java.awt.Color;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.util.Internal;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeStyleSheet;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.exigeninsurance.x4j.analytic.xlsx.transform.ThemeColor;

@Internal
public class ColorHelper {


	private  Map<Integer, Integer> themeColors  = new HashMap<Integer, Integer>() ;

	public ColorHelper(CTOfficeStyleSheet office) {
		if(office != null){			
			int index = 0;
			NodeList nodes = office.getDomNode().getOwnerDocument().getElementsByTagNameNS("*", "sysClr");
			for (int i = 0; i < nodes.getLength(); ++i,index++) {
				Element e = (Element) nodes.item(i);
				String val = e.getAttribute("val");
				if("window".equals(val)){					
					themeColors.put(index,0x000000);
				}else {
					themeColors.put(index,0xFFFFFF);
				}
			}			
			nodes = office.getDomNode().getOwnerDocument().getElementsByTagNameNS("*", "srgbClr");
			for (int i = 0; i < nodes.getLength(); ++i,index++) {
				Element e = (Element) nodes.item(i);
				String val = e.getAttribute("val");
				themeColors.put(index, Integer.parseInt(val, 16));
			}
		}


	}





	public  Color getAwtColor(XSSFColor color) {
		if(color == null){
			return Color.BLACK;
		}
		if (color.getRgb() != null) {
			return new Color(ByteBuffer.wrap(color.getRgb()).getInt(), true);
		}
		else {
			return getAwtColor(color.getTheme(), color.getTint());
		}
	}

	public  Color getAwtColor(int theme, double tint) {
		Integer color = themeColors.get(theme);
		if(color == null){
			return null;
		}
		ByteBuffer b = ByteBuffer.allocate(4);
		b.putInt(color);
		byte[] rgbByte = b.array();
		byte[] rgbWithTint = getRgbWithTint(rgbByte, tint);
		return new Color(ByteBuffer.wrap(rgbWithTint).getInt(), false);
	}

	private static byte[] getRgbWithTint(byte[] rgb, double tint) {
		for(int i = 0; i < rgb.length; i++){
			rgb[i] = applyTint(rgb[i] & 0xFF, tint);
		}
		return rgb;
	}

	private static byte applyTint(int luminosity, double tint){
		if(tint > 0){
			return (byte)(luminosity * (1.0 - tint) + (255 - 255 * (1.0 - tint)));
		} else if (tint < 0){
			return (byte)(luminosity * (1 + tint));
		} else {
			return (byte)luminosity;
		}
	}

	public  String colorToHex(ThemeColor themeColor) {
		int theme = themeColor.getTheme();
		double tint = themeColor.getTint();
		Color color = getAwtColor(theme, tint);
		return awtColorToHex(color == null ? Color.BLUE : color);
	}

	public static String awtColorToHex(Color color) {
		int g = color.getGreen();
		int r = color.getRed();
		int b = color.getBlue();
		return "#" + Integer.toHexString(r) + Integer.toHexString(g) + Integer.toHexString(b);
	}

	public static XSSFColor getColorFromStylesSource(XSSFCell cell) {
		XSSFColor color = getCellColor(cell);
		return color != null ? color : getThemeOrStyleColor(cell);
	}

	private static XSSFColor getCellColor(XSSFCell cell) {
		return cell.getCellStyle().getFillForegroundXSSFColor();
	}

	private static XSSFColor getThemeOrStyleColor(XSSFCell cell) {
		long style = cell.getCTCell().getS();
		StylesTable stylesSource = cell.getSheet().getWorkbook().getStylesSource();
		int fillId = (int) stylesSource.getStyleAt((int) style).getStyleXf().getFillId();
		return stylesSource.getFillAt(fillId).getFillForegroundColor();
	}
}
