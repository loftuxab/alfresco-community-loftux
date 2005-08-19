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
package org.alfresco.repo.webservice.repository;

/**
 * Interface definition for a QuerySession, implementations are expected to 
 * store the results of a query and maintain a pointer to the current set of
 * results as defined by the batch size passed as a SOAP header to the initiating
 * service call.
 * 
 * @author gavinc
 */
public interface QuerySession
{
   /**
    * Retrieves the id this query session can be identified as
    * 
    * @return Id of this query session
    */
   public String getId();
   
   /**
    * The timestamp of when the session was last touched
    * 
    * @return Timestamp of when the session was last touched
    */
   public long getLastAccessedTimestamp();
   
   /**
    * Updates the last accessed timestamp for this session to now
    */
   public void touch();
   
   /**
    * Determines whether this query session has any more results
    * 
    * @return true if there are more resutls, false otherwise
    */
   public boolean hasMoreResults();
   
   /**
    * Returns a QueryResult object representing the next batch of results.
    * QueryResult will contain a maximum of items as determined by the 
    * <code>fetchSize</code> element of the QueryConfiguration SOAP header.
    * 
    * @see org.alfresco.repo.webservice.repository.QuerySession#getId()
    * @return QueryResult containing the next batch of results or null if there
    * are no more results
    */
   public QueryResult getNextResultsBatch();
}
