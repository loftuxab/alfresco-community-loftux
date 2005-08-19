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

import java.io.Serializable;
import java.util.Map;

import javax.xml.rpc.handler.MessageContext;

import org.alfresco.repo.webservice.types.NamedValue;
import org.alfresco.repo.webservice.types.ResultSetRowNode;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.Path;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author gavinc
 */
public class ResultSetQuerySession extends AbstractQuerySession
{
   private static Log logger = LogFactory.getLog(ResultSetQuerySession.class);
   
   private NodeService nodeService;
   private ResultSet results;
   private boolean includeMetaData;
   private boolean hasMultipleBatches;
   private boolean hasMoreResults = true;
   private int batchSize;
   private int position = 0;
   private int totalRowCount;
   
   /**
    * Constructs a ResultSetQuerySession
    * 
    * @param context Current MessageContext
    * @param nodeService NodeService instance used to lookup node information
    * @param results The ResultSet to cache
    * @param includeMetaData Whether the QueryResult objects returned should contain metadata
    */
   public ResultSetQuerySession(MessageContext context, NodeService nodeService,
         ResultSet results, boolean includeMetaData)
   {
      super();

      this.results = results;
      this.nodeService = nodeService;
      this.includeMetaData = includeMetaData;
      this.totalRowCount = this.results.length();
      
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
         
         org.alfresco.repo.webservice.types.ResultSet batchResults = new org.alfresco.repo.webservice.types.ResultSet();      
         org.alfresco.repo.webservice.types.ResultSetRow[] rows = new org.alfresco.repo.webservice.types.ResultSetRow[currentBatchSize];
         
         // build up all the row data
         int arrPos = 0;
         for (int x = this.position; x < (currentBatchSize + this.position); x++)
         {
            ResultSetRow origRow = this.results.getRow(x);
            NodeRef nodeRef = origRow.getNodeRef();
            ResultSetRowNode rowNode = new ResultSetRowNode(nodeRef.getId(), this.nodeService.getType(nodeRef).toString(), null);
            
            // get the data for the row and build up the columns structure
            Map<Path, Serializable> values = origRow.getValues();
            NamedValue[] columns = new NamedValue[values.size()];
            int col = 0;
            for (Path path : values.keySet())
            {
               String value = null;
               Serializable valueObj = values.get(path);
               if (valueObj != null)
               {
                  value = valueObj.toString();
               }
               columns[col] = new NamedValue(path.toString(), value);
               col++;
            }
            
            org.alfresco.repo.webservice.types.ResultSetRow row = new org.alfresco.repo.webservice.types.ResultSetRow();
            row.setColumns(columns);
            row.setScore(origRow.getScore());
            row.setRowIndex(x);
            row.setNode(rowNode);
            
            // add the row to the overall results
            rows[arrPos] = row;
            arrPos++;
         }
         
         // TODO: build up the meta data data structure if asked to
         
         // add the rows to the result set and set the total row count
         batchResults.setRows(rows);
         batchResults.setTotalRowCount(this.totalRowCount);
         
         queryResult = new QueryResult(getId(), batchResults);
         
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
