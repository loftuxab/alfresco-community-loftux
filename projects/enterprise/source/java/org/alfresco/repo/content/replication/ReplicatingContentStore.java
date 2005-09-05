/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.repo.content.replication;

import java.util.List;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.content.ContentStore;
import org.alfresco.service.cmr.repository.ContentIOException;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentStreamListener;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.transaction.TransactionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A content store implementation that is able to replicate content between
 * stores that support the same protocols.
 * <p>
 * A store that can service content URLs of type <b>file://</b> only would not
 * be able to replicate to a store that only supports URLs of type <b>db://</b>. 
 * 
 * @author Derek Hulley
 */
public class ReplicatingContentStore implements ContentStore
{
    private static Log logger = LogFactory.getLog(ReplicatingContentStore.class);
    
    private TransactionService transactionService;
    private List<ContentStore> stores;

    /**
     * Required to ensure that content listeners are executed in a transaction
     * 
     * @param transactionService
     */
    public void setTransactionService(TransactionService transactionService)
    {
        this.transactionService = transactionService;
    }

    /**
     * Set the stores that this store must replicate to.  The first
     * store will be used as the default store for reading content and
     * should therefore be configured to be the primary and fastest
     * store.
     * <p>
     * All stores in the list must support the same content URL protocol.
     * 
     * @param stores a list of stores to replicate to
     */
    public void setStores(List<ContentStore> stores)
    {
        if (this.stores != null)
        {
            throw new AlfrescoRuntimeException("Resetting of the store list is not allowed");
        }
        this.stores = stores;
    }

    /**
     * Forwards the call directly to the first store in the list of stores.
     */
    public ContentReader getReader(String contentUrl) throws ContentIOException
    {
        if (stores == null || stores.size() == 0)
        {
            throw new AlfrescoRuntimeException("ReplicatingContentStore not initialised");
        }
        return stores.get(0).getReader(contentUrl);
    }

    /**
     * Forwards the call directly to the first store in the list of stores.  The writer
     * has a listener attached that will ensure that, upon stream closure, the content
     * is replicated to the remaining stores.
     */
    public ContentWriter getWriter(ContentReader existingContentReader, String newContentUrl) throws ContentIOException
    {
        if (stores == null || stores.size() == 0)
        {
            throw new AlfrescoRuntimeException("ReplicatingContentStore not initialised");
        }
        // get the writer
        ContentWriter writer = stores.get(0).getWriter(existingContentReader, newContentUrl);
        if (logger.isDebugEnabled())
        {
            logger.debug("Attaching replicating listener to local writer: \n" +
                    "   primary store: " + stores.get(0) + "\n" +
                    "   writer: " + writer);
        }
        // attach the listener
        ReplicatingWriteListener listener = new ReplicatingWriteListener(stores, writer);
        writer.addListener(listener);
        writer.setTransactionService(transactionService);   // mandatory when listeners are added
        
        // done
        return writer;
    }

    /**
     * Propagates the delete to all stores.
     * 
     * @return Returns true always
     */
    public boolean delete(String contentUrl) throws ContentIOException
    {
        if (stores == null || stores.size() == 0)
        {
            throw new AlfrescoRuntimeException("ReplicatingContentStore not initialised");
        }
        // call each store - these should be protecting content for a while so the content
        // will still be available for a short while; enough to allow transactions to
        // complete
        for (ContentStore store : stores)
        {
            store.delete(contentUrl);
        }
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("Propagated content delete: " + contentUrl);
        }
        return true;
    }

    /**
     * @return Returns the results as given by the primary store
     */
    public List<String> listUrls() throws ContentIOException
    {
        if (stores == null || stores.size() == 0)
        {
            throw new AlfrescoRuntimeException("ReplicatingContentStore not initialised");
        }
        // we could choose to get this from any store, but the primary one is by contract
        // the one chosen by the configuration to be the default for reads
        return stores.get(0).listUrls();
    }

    /**
     * Replicates the content upon stream closure.
     * <p>
     * No transaction boundaries have been declared as the
     * {@link ContentWriter#addListener(ContentStreamListener)} method indicates that
     * all listeners will be called within a transaction.
     * 
     * @author Derek Hulley
     */
    public static class ReplicatingWriteListener implements ContentStreamListener
    {
        private List<ContentStore> stores;
        private ContentWriter writer;
        
        public ReplicatingWriteListener(List<ContentStore> stores, ContentWriter writer)
        {
            this.stores = stores;
            this.writer = writer;
        }
        
        public void contentStreamClosed() throws ContentIOException
        {
            try
            {
                boolean first = true;
                for (ContentStore store : stores)
                {
                    if (first)
                    {
                        // the first store was alfready written to as it would have supplied the writer
                        first = false;
                        continue;
                    }
                    else
                    {
                        // replicate the content to the store - we know the URL that we want to write to
                        ContentReader reader = writer.getReader();
                        String contentUrl = reader.getContentUrl();
                        // in order to replicate, we have to specify the URL that we are going to write to
                        ContentWriter replicatedWriter = store.getWriter(null, contentUrl);
                        // write it
                        replicatedWriter.putContent(reader);
                        
                        if (logger.isDebugEnabled())
                        {
                            logger.debug("Replicated content to store: \n" +
                                    "   url: " + contentUrl + "\n" +
                                    "   to store: " + store);
                        }
                    }
                }
            }
            catch (Throwable e)
            {
                throw new ContentIOException("Content replication failed", e);
            }
        }
    }
}
