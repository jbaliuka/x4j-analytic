/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/

package com.exigeninsurance.x4j.analytic.util;




public interface Cursor {

    public void close();
    
    public boolean isClosed();

    public boolean next();

    public CursorMetadata getMetadata();

    public Object getObject(int i);

    public void reset();
}
