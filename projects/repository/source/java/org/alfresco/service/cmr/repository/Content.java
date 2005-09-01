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
package org.alfresco.service.cmr.repository;

import org.alfresco.service.transaction.TransactionService;


/**
 * A <tt>Content</tt> is a handle onto a specific content location.
 * 
 * @author Derek Hulley
 */
public interface Content
{    
    /**
     * Use this method to register any interest in events against underlying
     * content streams. 
     * {@link #getContentOutputStream() output stream}.
     * <p>
     * This method can only be used before the content stream has been retrieved.
     * <p>
     * When the stream has been closed, all listeners will be called
     * within a {@link #setTransactionService(TransactionService) transaction} -
     * to this end, a {@link TransactionService} must have been set as well.
     * 
     * @param listener a listener that will be called for output stream
     *      event notification
     *      
     * @see #setTransactionService(TransactionService)
     */
    public void addListener(ContentStreamListener listener);
    
    /**
     * Set the transaction provider that will be used when stream listeners are called.
     * 
     * @param transactionService a transaction provider
     */
    public void setTransactionService(TransactionService transactionService);
    
    /**
     * @return Returns a URL identifying the specific location of the content.
     *      The URL must identify, within the context of the originating content
     *      store, the exact location of the content.
     * @throws ContentIOException
     */
    public String getContentUrl() throws ContentIOException;
    
    /**
     * Gets content's mimetype.
     * 
     * @return Returns a standard mimetype for the content or null if the mimetype
     *      is unkown
     */
    public String getMimetype();
    
    /**
     * Sets the content's mimetype.
     * 
     * @param mimetype the standard mimetype for the content - may be null
     */
    public void setMimetype(String mimetype);
    
    /**
     * Gets the content's encoding.
     * 
     * @return Returns a valid Java encoding, typically a character encoding, or
     *      null if the encoding is unkown
     */
    public String getEncoding();
    
    /**
     * Sets the content's encoding.
     * 
     * @param encoding a valid Java encoding, typically a character encoding -
     *      may be null
     */
    public void setEncoding(String encoding);
}
