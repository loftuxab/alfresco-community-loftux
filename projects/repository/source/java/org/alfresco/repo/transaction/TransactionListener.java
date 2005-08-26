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
package org.alfresco.repo.transaction;

/**
 * Listener for Alfresco-specific transaction callbacks.
 *
 * @see org.alfresco.repo.transaction.AlfrescoTransactionSupport
 * 
 * @author Derek Hulley
 */
public interface TransactionListener
{
    /**
     * Allows the listener to flush any consuming resources.  This mechanism is
     * used primarily during long-lived transactions to ensure that system resources
     * are not used up.
     */
    void flush();
    
    /**
     * @param readOnly true if the transaction is read-only
     */
    void beforeCommit(boolean readOnly);
    
    /**
     * 
     */
    void beforeCompletion();
    
    /**
     *
     */
    void afterCommit();

    /**
     *
     */
    void afterRollback();
}
