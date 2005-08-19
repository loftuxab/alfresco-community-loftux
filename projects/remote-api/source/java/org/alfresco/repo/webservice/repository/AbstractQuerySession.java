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

import java.util.Date;

import org.alfresco.util.GUID;

/**
 * Abstract implementation of a QuerySession providing support
 * for automatic id generation and last accessed handling
 * 
 * @author gavinc
 */
public abstract class AbstractQuerySession implements QuerySession
{
   private String id;
   private long lastAccessed;
      
   public AbstractQuerySession()
   {
      this.id = GUID.generate();
      this.lastAccessed = new Date().getTime();
   }
   
   /**
    * @see org.alfresco.repo.webservice.repository.QuerySession#getId()
    */
   public String getId()
   {
      return this.id;
   }

   /**
    * @see org.alfresco.repo.webservice.repository.QuerySession#getLastAccessedTimestamp()
    */
   public long getLastAccessedTimestamp()
   {
      return this.lastAccessed;
   }

   /**
    * @see org.alfresco.repo.webservice.repository.QuerySession#touch()
    */
   public void touch()
   {
      this.lastAccessed = new Date().getTime();
   }
}
