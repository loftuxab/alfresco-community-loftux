/*
 * Copyright 2005-2010 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.management;

import java.util.Collections;
import java.util.Map;

import javax.management.DynamicMBean;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.alfresco.util.OpenOfficeConnectionEvent;

/**
 * Exports {@link DynamicMBean} instances in response to {@link OpenOfficeConnectionEvent}s. The MBeans contain product
 * version information queried from the Open Office connection.
 * 
 * @author dward
 */
public class OpenOfficeMetadataExporter extends AbstractManagedResourceExporter<OpenOfficeConnectionEvent>
{
    /**
     * The Constructor.
     */
    public OpenOfficeMetadataExporter()
    {
        super(OpenOfficeConnectionEvent.class);
    }

    /*
     * (non-Javadoc)
     * @see
     * org.alfresco.enterprise.repo.management.AbstractManagedResourceExporter#getObjectsToExport(org.springframework
     * .context.ApplicationEvent)
     */
    @Override
    public Map<ObjectName, ?> getObjectsToExport(OpenOfficeConnectionEvent event) throws MalformedObjectNameException
    {
        return Collections.singletonMap(new ObjectName("Alfresco:Name=OpenOffice"), new PropertiesDynamicMBean(event
                .getMetaData()));
    }
}
