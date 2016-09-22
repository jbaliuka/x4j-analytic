/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/

package com.exigeninsurance.x4j.analytic.api;

import com.exigeninsurance.x4j.analytic.util.CursorMetadata;


/**
 *  Simplified JDBC ResultSet
 *  
 * @author jbaliuka
 *
 */

public interface Cursor {

    void close();
    
    boolean isClosed();

    boolean next();

    CursorMetadata getMetadata();

    Object getObject(int i);

    void reset();
}
