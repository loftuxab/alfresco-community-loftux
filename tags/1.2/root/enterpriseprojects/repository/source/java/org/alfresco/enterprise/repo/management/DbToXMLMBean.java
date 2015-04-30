/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.management;

import java.io.File;
import java.util.List;

import org.alfresco.util.schemacomp.MultiFileDumper;

/**
 * DbToXML management interface. Allows users to dump
 * the database schema structure to XML files using the
 * {@link org.alfresco.util.schemacomp.DbToXML} tool.
 * 
 * @author Matt Ward
 */
public interface DbToXMLMBean
{
    /**
     * Operation allowing the user to dump an XML representation of the database schema.
     * <p/>
     * Database objects are filtered by the default list of prefixes, {@link MultiFileDumper#DEFAULT_PREFIXES}.
     *  
     * @return Locations of the XML dump files.
     */
    List<File> dumpSchemaToXML();
    
    /**
     * Operation allowing the user to dump an XML representation of the database schema.
     * <p/>
     * A comma separated list of database object prefixes must be provided (e.g. "alf_, act_")
     * 
     * @param dbPrefixesAsCSV Comma-separated list of database object name prefixes to filter by.
     * @return Locations of the XML dump files.
     */
    List<File> dumpSchemaToXML(String dbPrefixesAsCSV);
}
