/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/

package com.exigeninsurance.x4j.analytic.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for parameterType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="parameterType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="STRING"/>
 *     &lt;enumeration value="DATE"/>
 *     &lt;enumeration value="TIME"/>
 *     &lt;enumeration value="DATE_TIME"/>
 *     &lt;enumeration value="TIMESTAMP"/>
 *     &lt;enumeration value="INTEGER"/>
 *     &lt;enumeration value="MONEY"/>
 *     &lt;enumeration value="FLOAT"/>
 *     &lt;enumeration value="BOOLEAN"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "parameterType")
@XmlEnum

public enum ParameterType{

    STRING,
    DATE,
    TIME,
    DATE_TIME,
    TIMESTAMP,
    INTEGER,
    MONEY,
    FLOAT,
    BOOLEAN,
    LOOKUP,
    MULTILOOKUP;

    public String value() {
        return name();
    }

    public static ParameterType fromValue(String v) {
        return valueOf(v);
    }

}
