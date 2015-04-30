/*
 * Copyright 2005-2010 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.management;

import org.alfresco.repo.content.ContentStoreCreatedEvent;
import org.alfresco.repo.content.filestore.FileContentStore;

/**
 * An implementation of the {@link ContentStoreMBean} management interface for {@link FileContentStore}s.
 * 
 * @author dward
 * @since 3.1
 */
public class ContentStore implements ContentStoreMBean
{
    /** the source store to query */
    private final org.alfresco.repo.content.ContentStore store;

    /**
     * The Constructor.
     * 
     * @param fileEvent
     *            the file event
     */
    public ContentStore(ContentStoreCreatedEvent fileEvent)
    {
        this.store = fileEvent.getContentStore();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isWriteSupported()
    {
        return store.isWriteSupported();
    }

    /**
     * @see org.alfresco.repo.content.ContentStore#getSpaceTotal()
     */
    @Override
    public long getSpaceTotal()
    {
        return store.getSpaceTotal();
    }

    /**
     * @see org.alfresco.repo.content.ContentStore#getSpaceFree()
     */
    @Override
    public long getSpaceFree()
    {
        return store.getSpaceFree();
    }
}
