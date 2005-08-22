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

import javax.xml.rpc.handler.MessageContext;

import org.alfresco.repo.webservice.axis.QueryConfigHandler;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.util.GUID;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Abstract implementation of a QuerySession providing support
 * for automatic id generation and last accessed handling
 * 
 * @author gavinc
 */
public abstract class AbstractQuerySession implements QuerySession
{
   private static Log logger = LogFactory.getLog(AbstractQuerySession.class);
   
   protected boolean hasMultipleBatches;
   protected boolean includeMetaData;
   protected boolean hasMoreResults = true;
   protected int batchSize;
   protected int position = 0;
   protected int totalRowCount;
   protected NodeService nodeService;
   
   private String id;
   private long lastAccessed;
      
   /**
    * Abstract constructor, sets up the batch size from the MessageContext, subclasses
    * must then setup  
    * 
    * @param context SOAP MessageContext
    * @param nodeService The NodeService instance to use
    * @param totalRowCount Total number of rows in the results
    * @param includeMetaData Whether to include meta data in the results
    */
   public AbstractQuerySession(MessageContext context, NodeService nodeService, 
         int totalRowCount, boolean includeMetaData)
   {
      this.id = GUID.generate();
      this.lastAccessed = new Date().getTime();
      this.totalRowCount = totalRowCount;
      this.includeMetaData = includeMetaData;
      this.nodeService = nodeService;
      
      Integer batchConfigSize = (Integer)context.getProperty(QueryConfigHandler.ALF_FETCH_SIZE);
      if (batchConfigSize != null)
      {
         this.batchSize = batchConfigSize.intValue();
      }
      else
      {
         this.batchSize = this.totalRowCount;
      }
      
      this.hasMultipleBatches = (this.totalRowCount > this.batchSize);
   }
   
   /**
    * @see org.alfresco.repo.webservice.repository.QuerySession#hasMoreResults()
    */
   public boolean hasMoreResults()
   {
      return this.hasMoreResults;
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
   
   /**
    * @see org.alfresco.repo.webservice.repository.QuerySession#getNextResultsBatch()
    */
   public QueryResult getNextResultsBatch()
   {
      if (logger.isDebugEnabled())
         logger.debug("Before getNextResultsBatch: " + toString());
      
      QueryResult queryResult = null;
      int currentBatchSize = this.batchSize;
      
      if (this.hasMultipleBatches == false)
      {
         currentBatchSize = this.totalRowCount;
      }
      
      if (this.position != -1)
      {
         // work out the size for the current batch (only necessary if we have more than one batch
         // and we have previously returned a batch)
         if (this.hasMultipleBatches && this.position > 0)
         {
            if ((this.position + this.batchSize) > this.totalRowCount)
            {
               // reduce the current batch size so we don't go past the end of the results
               currentBatchSize = this.totalRowCount - this.position;
            }
         }
         
         if (logger.isDebugEnabled())
            logger.debug("Current batch size = " + currentBatchSize);
         
         // build up all the row data
         queryResult = getRows(this.position, currentBatchSize + this.position);
         
         // move on the current position
         this.position += this.batchSize;
         if (this.position >= this.totalRowCount)
         {
            // signify that there are no more batches 
            this.position = -1;
            this.hasMoreResults = false;
         }
      }
      
      if (logger.isDebugEnabled())
         logger.debug("After getNextResultsBatch: " + toString());
         
      return queryResult;
   }
   
   /**
    * Retrieves a QueryResult object representing the rows between from and to 
    * 
    * @param from The row number to start at
    * @param to The last row to retrieve
    * @return QueryResult representing those rows
    */
   public abstract QueryResult getRows(int from, int to);
   
   /**
    * @see java.lang.Object#toString()
    */
   public String toString()
   {
      StringBuilder builder = new StringBuilder(super.toString());
      builder.append(" (id=").append(getId());
      builder.append(" totalRowCount=").append(this.totalRowCount);
      builder.append(" batchSize=").append(this.batchSize);
      builder.append(" position=").append(this.position);
      return builder.toString();
   }
}
