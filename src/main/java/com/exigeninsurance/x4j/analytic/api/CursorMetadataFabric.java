package com.exigeninsurance.x4j.analytic.api;

import com.exigeninsurance.x4j.analytic.util.CursorMetadata;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by kkolesnichenko on 5/24/2016.
 */
public interface CursorMetadataFabric{

    CursorMetadata createFromResultSet(ResultSet rs) throws SQLException;
}
