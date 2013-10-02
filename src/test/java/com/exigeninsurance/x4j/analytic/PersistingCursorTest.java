package com.exigeninsurance.x4j.analytic;

import static com.exigeninsurance.x4j.analytic.util.MockResultSet.cols;
import static com.exigeninsurance.x4j.analytic.util.MockResultSet.create;
import static com.exigeninsurance.x4j.analytic.util.MockResultSet.data;
import static com.exigeninsurance.x4j.analytic.util.MockResultSet.row;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.File;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.exigeninsurance.x4j.analytic.util.Cursor;
import com.exigeninsurance.x4j.analytic.util.MockResultSet;
import com.exigeninsurance.x4j.analytic.util.PersistingCursor;

public class PersistingCursorTest {

    @Mock MockResultSet mockResultSet;
    @Mock ResultSetMetaData mockMetadata;
    private Object[] row;
    private ResultSet oneRowRs;
    private ResultSet emptyRs;

    @Before
    public void setUp() throws SQLException {
        initMocks(this);
        row = new Object[]{1, 3};
        when(mockResultSet.getMetaData()).thenReturn(mockMetadata);

        oneRowRs = create(cols("A", "B"), data(row(row)));
        emptyRs = create(cols("A", "B"), data());
    }

    private void iterateThrough(Cursor cursor) {
        while (cursor.next()) {}
    }

    @Test
    public void nextOnOneRowCursor_shouldReturnTrue() {
        File file = new File("file");
        try {
            Cursor cursor = new PersistingCursor(file, oneRowRs);
            try {
                assertTrue(cursor.next());
            } finally {
                cursor.close();
            }
        } finally {
            assertTrue(file.delete());
        }
    }

    @Test
    public void verifyMetadataReturnsCorrectColumnCount() {
        File file = new File("file");
        try {
            Cursor cursor = new PersistingCursor(file, oneRowRs);
            try {
                assertThat(cursor.getMetadata().getColumnCount(), equalTo(2));
            } finally {
                cursor.close();
            }
        } finally {
            assertTrue(file.delete());
        }
    }

    @Test
    public void verifyMetadataReturnsCorrectColumnNames() {
        File file = new File("file");
        try {
            Cursor cursor = new PersistingCursor(file, oneRowRs);
            try {
                assertThat(cursor.getMetadata().getColumnName(1), equalTo("A"));
                assertThat(cursor.getMetadata().getColumnName(2), equalTo("B"));
            } finally {
                cursor.close();
            }
        } finally {
            assertTrue(file.delete());
        }
    }

    @Test
    public void nextOnEmptyResultSet_shouldReturnFalse() {
        File file = new File("file");
        try {
            Cursor cursor = new PersistingCursor(file, emptyRs);
            try {
                assertFalse(cursor.next());
            } finally {
                cursor.close();
            }
        } finally {
            assertTrue(file.delete());
        }
    }

    @Test
    public void nextAfterLastRow_shouldReturnFalse() {
        File file = new File("file");
        try {
            Cursor cursor = new PersistingCursor(file, oneRowRs);
            try {
                iterateThrough(cursor);
                assertFalse(cursor.next());
            } finally {
                cursor.close();
            }
        } finally {
            assertTrue(file.delete());
        }
    }

    @Test
    public void verifyGetObjectReturnsCorrectValues() {
        File file = new File("file");
        try {
            Cursor cursor = new PersistingCursor(file, oneRowRs);
            try {
                cursor.next();
                assertThat(cursor.getObject(1), equalTo(row[0]));
                assertThat(cursor.getObject(2), equalTo(row[1]));
            } finally {
                cursor.close();
            }
        } finally {
            assertTrue(file.delete());
        }
    }

   
}
