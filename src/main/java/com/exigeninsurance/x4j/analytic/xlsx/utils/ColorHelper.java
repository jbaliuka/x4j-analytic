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

import com.exigeninsurance.x4j.analytic.xlsx.transform.ThemeColor;

@Internal
public class ColorHelper {

    private static final Map<Integer, Integer> THEME_COLORS;

    static {
        THEME_COLORS = new HashMap<Integer, Integer>();
        THEME_COLORS.put(0, 0xFFFFFF);
        THEME_COLORS.put(1, 0x000000);
        THEME_COLORS.put(2, 0xEEECE1);
        THEME_COLORS.put(3, 0x1F497D);
        THEME_COLORS.put(4, 0x4F81BD);
        THEME_COLORS.put(5, 0xC0504D);
        THEME_COLORS.put(6, 0x9BBB59);
        THEME_COLORS.put(7, 0x8064A2);
        THEME_COLORS.put(8, 0x4BACC6);
        THEME_COLORS.put(9, 0xF79646);
    }

    public static Color getAwtColor(XSSFColor color) {
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

    public static Color getAwtColor(int theme, double tint) {
        ByteBuffer b = ByteBuffer.allocate(4);
        b.putInt(THEME_COLORS.get(theme));
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

    public static String colorToHex(ThemeColor themeColor) {
        int theme = themeColor.getTheme();
        double tint = themeColor.getTint();
        Color color = getAwtColor(theme, tint);
        return awtColorToHex(color);
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
