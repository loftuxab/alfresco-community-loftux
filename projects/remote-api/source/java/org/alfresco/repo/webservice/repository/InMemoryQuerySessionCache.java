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

import java.util.HashMap;

/**
 * Implementation of QuerySessionCache that stores all the QuerySession's in a HsahMap.
 * 
 * @author gavinc
 */
public class InMemoryQuerySessionCache implements QuerySessionCache
{
   /** timeout period, 5 minutes by default */
   private long timeout = 60*5*1000;
   private HashMap<String, QuerySession> cache = new HashMap<String, QuerySession>();
   
   /**
    * @see org.alfresco.repo.webservice.repository.QuerySessionCache#putQuerySession(org.alfresco.repo.webservice.repository.QuerySession)
    */
   public void putQuerySession(QuerySession querySession)
   {
      this.cache.put(querySession.getId(), querySession);
   }

   /**
    * @see org.alfresco.repo.webservice.repository.QuerySessionCache#getQuerySession(java.lang.String)
    */
   public QuerySession getQuerySession(String querySession)
   {
      return this.cache.get(querySession);
   }

   /**
    * @see org.alfresco.repo.webservice.repository.QuerySessionCache#removeQuerySession(java.lang.String)
    */
   public void removeQuerySession(String querySession)
   {
      this.cache.remove(querySession);
   }

   /**
    * @see org.alfresco.repo.webservice.repository.QuerySessionCache#purgeQuerySessions()
    */
   public void purgeQuerySessions()
   {
      // TODO: Go through the map and retrieve the last accessed time of each query session
      //       then remove all those that have not been accessed during the timeout period.
   }
}
