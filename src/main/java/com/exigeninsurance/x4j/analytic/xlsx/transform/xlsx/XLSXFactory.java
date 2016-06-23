/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx;

import java.lang.reflect.Constructor;

import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.POIXMLException;
import org.apache.poi.POIXMLFactory;
import org.apache.poi.POIXMLRelation;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFRelation;

import com.exigeninsurance.x4j.analytic.xlsx.transform.PivotTable;
import com.exigeninsurance.x4j.analytic.xlsx.transform.PivotTableCache;


final public class XLSXFactory extends POIXMLFactory{
	
	
	
	public static final POIXMLRelation THEME = new POIXMLRelation(
			"application/vnd.openxmlformats-officedocument.theme+xml",
			"http://schemas.openxmlformats.org/officeDocument/2006/relationships/theme",
			"/xl/theme1.xml",
			XLSXTheme.class
	){};
	
	public static final POIXMLRelation PIVOT = new POIXMLRelation(
			"application/vnd.openxmlformats-officedocument.spreadsheetml.pivotTable+xml",
			"http://schemas.openxmlformats.org/officeDocument/2006/relationships/pivotTable",
			"/xl/pivotTables/pivotTable#.xml",
			PivotTable.class
	){};
	
	public static final POIXMLRelation PIVOT_CACHE = new POIXMLRelation(
			"application/vnd.openxmlformats-officedocument.spreadsheetml.pivotCacheDefinition+xml",
			"http://schemas.openxmlformats.org/officeDocument/2006/relationships/pivotCacheDefinition",
			"/xl/pivotCache/pivotCacheDefinition#.xml",
			PivotTableCache.class
	){};
	
	public static final POIXMLRelation CONNECTIONS = new POIXMLRelation(
			"application/vnd.openxmlformats-officedocument.spreadsheetml.connections+xml",
			"http://schemas.openxmlformats.org/officeDocument/2006/relationships/connections",
			"/xl/connections.xml",
			XLSXConnections.class
	){};
	
	public static final POIXMLRelation WORKSHEET = new POIXMLRelation(
			"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml",
			"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet",
			"/xl/worksheets/sheet#.xml",
			XLSXSheet.class
	){};
	
	private POIXMLRelation getDescriptor(PackageRelationship rel){
		
		if(THEME.getRelation().equals(rel.getRelationshipType())){
			return THEME;
		}else if(PIVOT.getRelation().equals(rel.getRelationshipType())){
			return PIVOT;
		}else if (PIVOT_CACHE.getRelation().equals(rel.getRelationshipType())){
			return PIVOT_CACHE;
		}else if (CONNECTIONS.getRelation().equals(rel.getRelationshipType())){
			return CONNECTIONS;
		}else if (WORKSHEET.getRelation().equals(rel.getRelationshipType())){
			return WORKSHEET;
		}
		return XSSFRelation.getInstance(rel.getRelationshipType());
		
	}

	@Override
	public POIXMLDocumentPart createDocumentPart(PackageRelationship rel, PackagePart part){
		
        POIXMLRelation descriptor = getDescriptor(rel);
        
        if(descriptor == null || getClass(descriptor) == null){         
            return new POIXMLDocumentPart(part, rel);
        }

        try {
            Class<? extends POIXMLDocumentPart> cls = getClass(descriptor);
            Constructor<? extends POIXMLDocumentPart> constructor = cls.getDeclaredConstructor(PackagePart.class, PackageRelationship.class);
            constructor.setAccessible(true);
            return constructor.newInstance(part, rel);
        } catch (Exception e){
            throw new POIXMLException(e);
        }
    }

	private Class<? extends POIXMLDocumentPart> getClass(
			POIXMLRelation descriptor) {
		Class<? extends POIXMLDocumentPart> cls = descriptor.getRelationClass();
		if(cls == StylesTable.class){
			return XLSXStylesTable.class;
		}else {
			return cls;
		}
		
	}

    public POIXMLDocumentPart newDocumentPart(POIXMLRelation descriptor){
        try {
            Class<? extends POIXMLDocumentPart> cls = getClass(descriptor);
            Constructor<? extends POIXMLDocumentPart> constructor = cls.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception e){
            throw new POIXMLException(e);
        }
    }


}
