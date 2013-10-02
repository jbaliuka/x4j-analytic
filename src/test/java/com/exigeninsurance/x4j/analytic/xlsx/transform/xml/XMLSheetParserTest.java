/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.xml;

import static com.exigeninsurance.x4j.analytic.util.MockResultSet.cols;
import static com.exigeninsurance.x4j.analytic.util.MockResultSet.data;
import static com.exigeninsurance.x4j.analytic.util.MockResultSet.row;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.exigeninsurance.x4j.analytic.api.ReportContext;
import com.exigeninsurance.x4j.analytic.model.Query;
import com.exigeninsurance.x4j.analytic.util.MockReportDataProvider;
import com.exigeninsurance.x4j.analytic.util.MockResultSet;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.TransformTest;

public class XMLSheetParserTest extends TransformTest {

	@Before
	public void setup() throws IOException {
		super.setup();
	}

    @After
    public void teardown() {
        super.teardown();
    }

    @Test
    public void testProcess() throws Exception {

        ResultSet rs = MockResultSet.create(cols("A", "B", "C", "D"), data(row(
                "\"a\"", "3.21", "2", "1")));

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        XLSXWorkbookToXMLTransform transform = new XLSXWorkbookToXMLTransform(out);
        dataProvider = new MockReportDataProvider(rs);
        transform.setDataProvider(dataProvider);

        InputStream in = getClass().getResourceAsStream("/basiclong.xlsx");
        assertNotNull(in);

        try {
            ArrayList<Query> list = new ArrayList<Query>();

            Query q2 = new Query();
            q2.setName("Table4");
            list.add(q2);

            metadata.setQuery(list);

            HashMap<String, Object> parameters = new HashMap<String, Object>();
            ReportContext reportContext = createContext(parameters);

            transform.process(reportContext, in, null);

            String output = new String(out.toByteArray(), "UTF8");

            Assert.assertEquals("<root><row><a>&quot;a&quot;</a><b>3.21</b><c>2</c><d>1</d></row></root>", output.replaceAll("\\s", ""));

        } finally {
            in.close();
        }
    }


}
