/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform;

import static com.exigeninsurance.x4j.analytic.util.MockResultSet.cols;
import static com.exigeninsurance.x4j.analytic.util.MockResultSet.data;
import static com.exigeninsurance.x4j.analytic.util.MockResultSet.row;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import junit.framework.TestCase;

import org.apache.poi.xssf.model.Table;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Assert;

import com.exigeninsurance.x4j.analytic.api.ReportContext;
import com.exigeninsurance.x4j.analytic.api.ReportDataProvider;
import com.exigeninsurance.x4j.analytic.model.Query;
import com.exigeninsurance.x4j.analytic.model.ReportMetadata;
import com.exigeninsurance.x4j.analytic.util.CursorManager;
import com.exigeninsurance.x4j.analytic.util.MockReportDataProvider;
import com.exigeninsurance.x4j.analytic.util.MockResultSet;
import com.exigeninsurance.x4j.analytic.xlsx.core.node.Node;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLSXSheetParser;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLXContext;

public class QueryProcessingTest extends TestCase {


    final ResultSet rs22 = MockResultSet.create(
            cols("A", "B"),
            data(row("1", "2"),
                    row("3", "4")
            )
    );

    final ResultSet rs24 = MockResultSet.create(
            cols("A", "B", "C", "D"),
            data(row("1", "2", "3", "4"),
                    row("5", "6", "7", "8"),
                    row("5", "6", "7", "8"),
                    row("5", "6", "7", "8")
            )
    );

    public void testMockRs() throws SQLException {

        try {
            Assert.assertTrue(rs22.next());
            Assert.assertEquals("1", rs22.getObject(1));
            Assert.assertEquals("2", rs22.getObject(2));
            Assert.assertEquals("1", rs22.getObject("A"));
            Assert.assertEquals("2", rs22.getObject("B"));

            Assert.assertTrue(rs22.next());
            Assert.assertEquals("3", rs22.getObject(1));
            Assert.assertEquals("4", rs22.getObject(2));
            Assert.assertEquals("3", rs22.getObject("A"));
            Assert.assertEquals("4", rs22.getObject("B"));

            Assert.assertFalse(rs22.next());
        } finally {
            rs22.close();
        }


    }


    public void testParseTable() throws Exception {

        InputStream is = getClass().getResourceAsStream("/testTables.xlsx");
        Assert.assertNotNull(is);
        try {

            XSSFWorkbook workBook = new XSSFWorkbook(is);
            XSSFSheet sheet = workBook.getSheetAt(0);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
           
            ReportMetadata metadata = new ReportMetadata();

            ArrayList<Query> list = new ArrayList<Query>();
            Query q1 = new Query();
            q1.setName("Table1");
            list.add(q1);

            Query q2 = new Query();
            q2.setName("Table4");
            list.add(q2);

            metadata.setQuery(list);

            ReportDataProvider dataProvider = new MockReportDataProvider(rs24);

            ReportContext reportContext = new ReportContext(metadata);
            CursorManager cursorManager = new CursorManager(reportContext, dataProvider);
            reportContext.setCursorManager(cursorManager);
            XLXContext context = new XLXContext(null, sheet, reportContext, stream);
            context.setDataProvider(dataProvider);

            SheetParser parser = new XLSXSheetParser(reportContext);
            Node root = parser.parse(sheet);

            try {
                root.process(context);

                Assert.assertEquals(2, parser.getTables().size());
                boolean table1 = false;
                boolean table4 = false;

                for (Table table : parser.getTables()) {

                    if (table.getCTTable().getName().equals("Table1")) {
                        table1 = true;
                        Assert.assertEquals("A1:B5", table.getCTTable().getRef());


                    } else if (table.getCTTable().getName().equals("Table4")) {
                        table4 = true;
                        Assert.assertEquals("B9:F13", table.getCTTable().getRef());
                    }

                }

                Assert.assertTrue(table1 && table4);
            } finally {
                cursorManager.releaseManagedResources();
            }


        } finally {
            is.close();
        }

    }

}
