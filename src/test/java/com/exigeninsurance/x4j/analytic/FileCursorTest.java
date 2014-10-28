package com.exigeninsurance.x4j.analytic;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.zip.GZIPOutputStream;

import org.apache.poi.util.Internal;
import org.junit.Before;
import org.junit.Test;

import com.exigeninsurance.x4j.analytic.api.Cursor;
import com.exigeninsurance.x4j.analytic.util.CursorMetadata;
import com.exigeninsurance.x4j.analytic.util.FileCursor;
import com.exigeninsurance.x4j.analytic.util.MockResultSet;
import com.exigeninsurance.x4j.analytic.util.ResultSetWrapper;

@Internal
public class FileCursorTest {

    private CursorMetadata metadata;
    private Object[] row;
    private ResultSet rs;

    @Before
    public void setUp() {
    	String cols[] = new String[2];
        
        cols[0] = "A";
        cols[1] = "B";
        
        metadata = new CursorMetadata(cols);
        row = new Object[] {"1", "2"};
        rs = MockResultSet.create(MockResultSet.cols("A","B"),MockResultSet.data( MockResultSet.row(row) ));
        
    }

    private void iterateThrough(Cursor cursor) {
        while (cursor.next()) {}
    }

    private void writeObjectsToFile(File file) throws IOException, SQLException {
        OutputStream fileOut = new GZIPOutputStream( new FileOutputStream(file) );
        try {
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            rs = MockResultSet.create(MockResultSet.cols("A","B"),MockResultSet.data( MockResultSet.row(row) ));
            try {
                objectOut.writeObject(metadata); 
                rs.next();
                metadata.writeRow(objectOut, new ResultSetWrapper(rs));
                rs.next();
                metadata.writeRow(objectOut, new ResultSetWrapper(rs));
            } finally {
                objectOut.close();
            }
        } finally {
            fileOut.close();
        }
    }

    @Test
    public void canRetrieveMetadata() throws IOException, SQLException {
        File file = new File("whatever");
        try {
            writeObjectsToFile(file);
            Cursor cursor = new FileCursor(file,2);
            try {
                CursorMetadata actual = cursor.getMetadata();
                assertThat(actual.getColumnCount(), equalTo(2));
                assertThat(actual.getColumnName(1), equalTo("A"));
                assertThat(actual.getColumnName(2), equalTo("B"));
            } finally {
                cursor.close();
            }
        } finally {
            assertTrue(file.delete());
        }
    }

    @Test
    public void nextOnNonEmptyCursor_shouldReturnTrue() throws IOException, SQLException {
        File file = new File("whatever");
        try {
            writeObjectsToFile(file);
            Cursor cursor = new FileCursor(file,2);
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
    public void testGetObject() throws IOException, SQLException {
        File file = new File("whatever");
        try {
            writeObjectsToFile(file);
            Cursor cursor = new FileCursor(file,2);
            try {
                cursor.next();
                assertEquals("1", cursor.getObject(1));
                assertEquals("2", cursor.getObject(2));
            } finally {
                cursor.close();
            }
        } finally {
            assertTrue(file.delete());
        }
    }

    @Test
    public void afterReseting_nextReturnsTrue() throws IOException, SQLException {
        File file = new File("whatever");
        try {
            writeObjectsToFile(file);
            Cursor cursor = new FileCursor(file,2);
            try {
                iterateThrough(cursor);
                cursor.reset();
                assertTrue(cursor.next());
            } finally {
                cursor.close();
            }
        } finally {
            assertTrue(file.delete());
        }
    }
}
