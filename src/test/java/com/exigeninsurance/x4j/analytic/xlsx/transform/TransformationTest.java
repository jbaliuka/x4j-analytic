/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.exigeninsurance.x4j.analytic.api.ReportContext;
import com.exigeninsurance.x4j.analytic.api.ReportDataProvider;
import com.exigeninsurance.x4j.analytic.model.Attribute;
import com.exigeninsurance.x4j.analytic.model.ReportMetadata;
import com.exigeninsurance.x4j.analytic.util.CursorManager;


public class TransformationTest {

    private static final String CURRENCY_FORMATS = "/currencyFormats.csv";
    private static final String DATE_FORMATS = "/dateFormats.csv";

    protected ReportMetadata metadata;
    protected ReportDataProvider dataProvider;
    private CursorManager cursorManager;

    protected void setup() throws IOException {
        String currencies = parseFormatsFromFile(CURRENCY_FORMATS);
        String dates = parseFormatsFromFile(DATE_FORMATS);
        List<Attribute> attributes = new ArrayList<Attribute>();
        Attribute currencyAttribute = new Attribute();
        currencyAttribute.setName("currency-formats");
        currencyAttribute.setValue(currencies);
        Attribute dateAttribute = new Attribute();
        dateAttribute.setName("date-formats");
        dateAttribute.setValue(dates);

        attributes.add(dateAttribute);
        attributes.add(currencyAttribute);

        metadata = new ReportMetadata();
        metadata.setAttribute(attributes);
    }

    private String parseFormatsFromFile(String fileName) throws IOException {
        StringBuilder builder = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(fileName)));
        try {
            String strLine = br.readLine();
            while (strLine != null) {
                builder.append(strLine);
                builder.append("\n");
                strLine = br.readLine();
            }

        } finally {
            br.close();
        }
        return builder.toString();
    }

    protected ReportContext createContext(Map<String, Object> parameters) {
        ReportContext reportContext = new ReportContext(metadata);
        reportContext.getParameters().putAll(parameters);
        cursorManager = new CursorManager(reportContext, dataProvider);
        reportContext.setCursorManager(cursorManager);
        return reportContext;
    }

    protected void teardown() {
        if (cursorManager != null) {
            cursorManager.releaseManagedResources();
        }

    }
}
