/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/lgpl.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.repo.integrity.db;

import java.util.List;

import org.alfresco.repo.domain.IntegrityEvent;
import org.alfresco.service.cmr.repository.NodeRef;

/**
 * Interface for providers of integrity-related persistence data.
 * 
 * @author Derek Hulley
 */
public interface IntegrityDaoService
{
    /**
     * Flush and clear the session cache to prevent memory issues.
     */
    public void flushAndClear();
    
    /**
     * @param txnId the ID of the transaction in which the event occured
     * @param eventType
     * @param primaryNodeRef
     * @return Returns a new persistent object
     */
    public IntegrityEvent newEvent(String txnId, String eventType, NodeRef primaryNodeRef);
    
    /**
     * Retrieves all events that arose in the given transaction.
     * <p>
     * This method forces pagination as the number of results returned can easily
     * be too large for long-running transactions.
     * 
     * @param txnId the transaction ID to search against
     * @param firstResult the first result
     * @param maxResults the maximum number of results to retrieve
     * @return Returns a list of all integrity events that have the given
     *      transaction ID.  The results are ordered by node reference
     *      and event type.
     */
    public List<IntegrityEvent> getEvents(String txnId, int firstResult, int maxResults);
    
    /**
     * Retrieves all nodes that should have their properties checked
     * 
     * @param txnId the transaction ID to search against
     * @return Returns all applicable node references (as strings)
     */
    public List<String> getNodesToCheckForProperties(final String txnId);
    
    /**
     * Deletes a specific event.
     * 
     * @param id the unique ID of the event
     */
    public void deleteEvent(Long id);
    
    /**
     * Removes all events associated with the given transaction ID.
     * 
     * @param txnId the transaction ID
     */
    public void deleteEvents(String txnId);
}
