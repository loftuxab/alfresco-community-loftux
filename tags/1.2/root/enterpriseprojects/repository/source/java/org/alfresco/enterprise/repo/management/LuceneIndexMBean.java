/*
 * Copyright 2005-2010 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.management;

import java.io.IOException;

import javax.management.openmbean.CompositeData;

/**
 * A Management Interface exposing properties of an Alfresco Lucene Index for monitoring.
 * 
 * @author dward
 */
public interface LuceneIndexMBean
{
    /**
     * Gets the actual size of the index in bytes.
     * 
     * @return the actual size in bytes
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public long getActualSize() throws IOException;

    /**
     * Gets the size used on disk by the index directory. A large discrepancy from the value returned by
     * {@link #getActualSize()} may indicate that there are unused data files.
     * 
     * @return the size on disk in bytes
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public long getUsedSize() throws IOException;

    /**
     * Gets the number of documents in the index.
     * 
     * @return the number of documents
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public int getNumberOfDocuments() throws IOException;

    /**
     * Gets the number of fields known to the index.
     * 
     * @return the number of fields
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public int getNumberOfFields() throws IOException;

    /**
     * Gets the number of indexed fields.
     * 
     * @return the number of indexed fields
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public int getNumberOfIndexedFields() throws IOException;

    /**
     * Returns a composite object containing the current status of each entry in the index.
     * 
     * @return composite entry status
     */
    public CompositeData getEntryStatus();

    /**
     * Returns a composite object containing the counts of significant events that have occurred to the index since the
     * server was started.
     * 
     * @return composite event counts
     */
    public CompositeData getEventCounts();
}
