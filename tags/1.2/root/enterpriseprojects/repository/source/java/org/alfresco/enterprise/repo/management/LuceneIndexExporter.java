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

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.alfresco.repo.search.impl.lucene.index.IndexEvent;
import org.alfresco.repo.search.impl.lucene.index.IndexMonitor;

/**
 * Exports {@link LuceneIndexMBean} instances in response to {@link IndexEvent}s.
 * 
 * @author dward
 */
public class LuceneIndexExporter extends AbstractManagedResourceExporter<IndexEvent>
{

    /**
     * The Constructor.
     */
    public LuceneIndexExporter()
    {
        super(IndexEvent.class);
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.repo.management.AbstractManagedResourceExporter#getObjectsToExport(org.springframework.context.
     * ApplicationEvent)
     */
    @Override
    public Map<ObjectName, ?> getObjectsToExport(IndexEvent event) throws MalformedObjectNameException
    {
        IndexMonitor indexMonitor = event.getIndexMonitor();
        return Collections.singletonMap(new ObjectName("Alfresco:Name=LuceneIndexes,Index="
                + indexMonitor.getRelativePath()), new LuceneIndex(indexMonitor));
    }
}
