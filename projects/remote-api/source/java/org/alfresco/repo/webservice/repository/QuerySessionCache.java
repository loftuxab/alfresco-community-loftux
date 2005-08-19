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
 * Interface definition for a query session cache mechanism, implementations
 * must be able to store and retrieve query session objects across web service
 * calls. They must also provide a cleanup process to remove query
 * sessions that have not been accessed for a period of time. 
 * 
 * @author gavinc
 */
public interface QuerySessionCache
{
   /**
    * Puts the given QuerySession into the cache, if it already existed in the 
    * cache the old one will be replaced.
    * 
    * @param querySession The QuerySession to add
    */
   public void putQuerySession(QuerySession querySession);
   
   /**
    * Retrieves the QuerySession with the given id.
    * <p>QuerySession objects may not be retrievable if a timeout occurs i.e.
    * if the QuerySession is not accessed after a set amount of time (determined
    * by the implementation) it will be removed from the cache.</p>
    * 
    * @param querySession Id of the QuerySesssion to retrieve
    * @return QuerySession object or null if it didn't exist in the cache 
    */
   public QuerySession getQuerySession(String querySession);
   
   /**
    * Removes the QuerySession with the given id from the cache.
    * 
    * @param querySession Id of the QuerySession to remove
    */
   public void removeQuerySession(String querySession);
   
   /**
    * Runs a process to remove all QuerySession's that have not been accessed within 
    * the timeout period (as defined by the implementation).
    */
   public void purgeQuerySessions();
}
