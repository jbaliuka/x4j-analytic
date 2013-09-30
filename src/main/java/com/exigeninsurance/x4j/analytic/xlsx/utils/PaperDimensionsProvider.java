
/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.poi.util.Internal;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import com.exigeninsurance.x4j.analytic.api.ReportException;

@Internal
public class PaperDimensionsProvider {

    private Map<Integer, Dimension> paperSizes = new HashMap<Integer, Dimension>();

    public PaperDimensionsProvider() {
        InputStream stream = getClass().getResourceAsStream("/paperDimensions.properties");
        Properties properties = new Properties();
        try {
            try {
                properties.load(stream);
            }
             finally {
                stream.close();
            }
        } catch (IOException e) {
            throw new ReportException(e);
        }

        Set<String> keys = properties.stringPropertyNames();
        for (String key : keys) {
            String value = properties.getProperty(key).trim();
            String[] dimension = value.split(" ");
            paperSizes.put(Integer.valueOf(key),
                    new Dimension(Float.valueOf(dimension[0]), Float.valueOf(dimension[1])));
        }
    }

    public Dimension getPaperDimensions(XSSFSheet sheet) {
        Dimension d = paperSizes.get(getPaperSize(sheet));
        return isLandscape(sheet) ? invert(d) : d;
    }

    private int getPaperSize(XSSFSheet sheet) {
        return (int) sheet.getPrintSetup().getPaperSize();
    }

    private boolean isLandscape(XSSFSheet sheet) {
        return sheet.getPrintSetup().getLandscape();
    }

    private Dimension invert(Dimension d) {
        return new Dimension(d.getHeight(), d.getWidth());
    }
}
