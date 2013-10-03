package com.exigeninsurance.x4j.analytic;

import static com.exigeninsurance.x4j.analytic.util.MockResultSet.cols;
import static com.exigeninsurance.x4j.analytic.util.MockResultSet.data;
import static com.exigeninsurance.x4j.analytic.util.MockResultSet.row;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;

import java.sql.ResultSet;

import org.junit.Before;
import org.junit.Test;

import com.exigeninsurance.x4j.analytic.api.Cursor;
import com.exigeninsurance.x4j.analytic.api.ReportContext;
import com.exigeninsurance.x4j.analytic.api.ReportDataProvider;
import com.exigeninsurance.x4j.analytic.model.Query;
import com.exigeninsurance.x4j.analytic.model.ReportMetadata;
import com.exigeninsurance.x4j.analytic.util.CursorManager;
import com.exigeninsurance.x4j.analytic.util.FileCursor;
import com.exigeninsurance.x4j.analytic.util.MockReportDataProvider;
import com.exigeninsurance.x4j.analytic.util.MockResultSet;

public class CursorManagerTest {

    private CursorManager lifecycle;
    private ReportContext reportContext;
    private ReportDataProvider dataProvider;
    private ResultSet resultSet;
    private Query query;

    @Before
    public void setup() {
        reportContext = new ReportContext(new ReportMetadata());
        
        resultSet = MockResultSet.create(cols("A", "B"),
                data(row(1, 2)), 1);
        dataProvider = new MockReportDataProvider(resultSet);

        lifecycle = new CursorManager(reportContext, dataProvider);
        query =  new Query();
    }

    @Test
    public void testCreateCursor() {
        try {
            Cursor cursor = lifecycle.createCursor(query);
            try {
                assertThat(cursor, instanceOf(FileCursor.class));
            } finally {
                cursor.close();
            }
        } finally {
            lifecycle.releaseManagedResources();
        }
    }
}
