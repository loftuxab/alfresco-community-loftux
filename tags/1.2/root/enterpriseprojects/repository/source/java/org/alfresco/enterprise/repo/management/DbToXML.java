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

import org.alfresco.repo.domain.schema.SchemaBootstrap;
import org.springframework.util.StringUtils;

/**
 * Implementation of DbToXMLMBean management interface. This allows users to dump
 * the database schema structure to XML files using the {@link org.alfresco.util.schemacomp.DbToXML} tool.
 * 
 * @author Matt Ward
 */
public class DbToXML implements DbToXMLMBean
{
    private SchemaBootstrap schemaBootstrap;
    
    public DbToXML(SchemaBootstrap schemaBootstrap)
    {
        this.schemaBootstrap = schemaBootstrap;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<File> dumpSchemaToXML()
    {
        return schemaBootstrap.dumpSchema();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<File> dumpSchemaToXML(String dbPrefixesAsCSV)
    {
        String[] dbPrefixes = StringUtils.commaDelimitedListToStringArray(dbPrefixesAsCSV);
        for (int i = 0; i < dbPrefixes.length; i++)
        {
            // Trim any leading/trailing whitespace from each supplied prefix.
            dbPrefixes[i] = StringUtils.trimWhitespace(dbPrefixes[i]);
        }
        return schemaBootstrap.dumpSchema(dbPrefixes);
    }
}
