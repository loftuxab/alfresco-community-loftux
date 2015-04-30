/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.management;

import java.util.Collections;
import java.util.Map;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.content.ContentStoreCreatedEvent;

/**
 * Exports {@link ContentStoreMBean} instances in response to {@link ContentStoreCreatedEvent}s.
 * 
 * @author dward
 */
public class ContentStoreExporter extends AbstractManagedResourceExporter<ContentStoreCreatedEvent>
{

    /**
     * The Constructor.
     */
    public ContentStoreExporter()
    {
        super(ContentStoreCreatedEvent.class);
    }

    @Override
    public Map<ObjectName, ?> getObjectsToExport(ContentStoreCreatedEvent event)
            throws MalformedObjectNameException, NullPointerException
    {
        org.alfresco.repo.content.ContentStore store = event.getContentStore();
        try
        {
            StringBuilder objName = new StringBuilder();
            objName.append("Alfresco")
                   .append(":Name=ContentStore")
                   .append(",Type=").append(store.getClass().getName())
                   .append(",Root=").append(store.getRootLocation().replace(':', '|'));
            for(String key:event.getExtendedEventParams().keySet())
            {
            	objName.append("," + key + "=" + event.getExtendedEventParams().get(key));
            }
            return Collections.singletonMap(new ObjectName(objName.toString()), new ContentStore(event));
        }
        catch (Throwable e)
        {
            throw new AlfrescoRuntimeException("Failed to retrieve objects to export content store: " + event, e);
        }

    }
}
