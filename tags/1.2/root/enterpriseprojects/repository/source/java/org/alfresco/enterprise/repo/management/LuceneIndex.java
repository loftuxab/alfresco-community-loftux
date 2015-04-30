/*
 * Copyright 2005-2010 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.management;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.management.openmbean.CompositeData;

import org.alfresco.enterprise.util.BeanMap;
import org.alfresco.repo.search.impl.lucene.index.IndexEvent;
import org.alfresco.repo.search.impl.lucene.index.IndexMonitor;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

/**
 * An implementation of the {@link LuceneIndexMBean} management interface that retrieves its information from an
 * {@link IndexMonitor}. As well as reporting the properties of an IndexMonitor, it listens for and counts occurrences
 * of significant events reported to it by the index monitor.
 * 
 * @author dward
 */
public final class LuceneIndex implements ApplicationListener, LuceneIndexMBean
{

    /** The index monitor. */
    private final IndexMonitor indexMonitor;

    /** The known entry status keys. */
    private final Set<String> statusKeys = new TreeSet<String>();

    /** The event counts. */
    private final Map<String, Integer> eventCounts = new TreeMap<String, Integer>();

    /**
     * The Constructor.
     * 
     * @param indexMonitor
     *            the index monitor
     */
    LuceneIndex(IndexMonitor indexMonitor)
    {
        this.indexMonitor = indexMonitor;
        indexMonitor.addApplicationListener(this);
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.enterprise.repo.management.LuceneIndexMBean#getActualSize()
     */
    public long getActualSize() throws IOException
    {
        return this.indexMonitor.getActualSize();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.enterprise.repo.management.LuceneIndexMBean#getUsedSize()
     */
    public long getUsedSize() throws IOException
    {
        return this.indexMonitor.getUsedSize();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.enterprise.repo.management.LuceneIndexMBean#getNumberOfDocuments()
     */
    public int getNumberOfDocuments() throws IOException
    {
        return this.indexMonitor.getNumberOfDocuments();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.enterprise.repo.management.LuceneIndexMBean#getNumberOfFields()
     */
    public int getNumberOfFields() throws IOException
    {
        return this.indexMonitor.getNumberOfFields();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.enterprise.repo.management.LuceneIndexMBean#getNumberOfIndexedFields()
     */
    public int getNumberOfIndexedFields() throws IOException
    {
        return this.indexMonitor.getNumberOfIndexedFields();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.enterprise.repo.management.LuceneIndexMBean#getEntryStatus()
     */
    @SuppressWarnings("unchecked")
    public CompositeData getEntryStatus()
    {
        Map<String, Integer> snapShot = this.indexMonitor.getStatusSnapshot();

        // Work out which known keys are not included in this snapshot
        Set<String> missingKeys = (Set<String>) ((TreeSet<String>) this.statusKeys).clone();
        missingKeys.removeAll(snapShot.keySet());

        // Add to the list of known keys
        this.statusKeys.addAll(snapShot.keySet());

        // Make sure the snapshot is 'cumulative' in that all keys previously reported are present
        for (String key : missingKeys)
        {
            snapShot.put(key, 0);
        }

        return snapShot.isEmpty() ? null : BeanMap.getCompositeData(snapShot);
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.enterprise.repo.management.LuceneIndexMBean#getEventCounts()
     */
    public CompositeData getEventCounts()
    {
        synchronized (this.eventCounts)
        {
            return this.eventCounts.isEmpty() ? null : BeanMap.getCompositeData(this.eventCounts);
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
     */
    public void onApplicationEvent(ApplicationEvent event)
    {
        if (event instanceof IndexEvent)
        {
            IndexEvent indexEvent = (IndexEvent) event;
            String description = indexEvent.getDescription();
            synchronized (this.eventCounts)
            {
                Integer oldCount = this.eventCounts.get(description);
                this.eventCounts.put(description, oldCount == null ? indexEvent.getCount() : indexEvent.getCount()
                        + oldCount);
            }
        }
    }
}
