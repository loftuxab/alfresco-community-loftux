/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.management;

import java.util.HashMap;
import java.util.Map;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.alfresco.repo.domain.schema.SchemaAvailableEvent;
import org.alfresco.repo.domain.schema.SchemaBootstrap;

/**
 * Creates and registers MBeans for SchemaBootstrap instances.
 * 
 * @author Matt Ward
 */
public class SchemaBootstrapExporter extends AbstractManagedResourceExporter<SchemaAvailableEvent>
{
    /**
     * Default constructor. Registers the event type used by this exporter.
     */
    public SchemaBootstrapExporter()
    {
        super(SchemaAvailableEvent.class);
    }

    
    @Override
    public Map<ObjectName, ?> getObjectsToExport(SchemaAvailableEvent event) throws MalformedObjectNameException
    {
        SchemaBootstrap schemaBootstrap = (SchemaBootstrap) event.getSource();
        Map<ObjectName, Object> exports = new HashMap<ObjectName, Object>(2);
        exports.put(new ObjectName(toolName("SchemaExport")), new DbToXML(schemaBootstrap));  
        exports.put(new ObjectName(toolName("SchemaValidator")), new DbSchemaValidator(schemaBootstrap));
        return exports;
    }
    
    private String toolName(String tool)
    {
        return "Alfresco:Name=DatabaseInformation,Tool=" + tool;
    }
}
