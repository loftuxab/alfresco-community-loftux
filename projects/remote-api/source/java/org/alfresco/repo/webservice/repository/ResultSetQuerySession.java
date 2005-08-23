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
 * Implementation of a QuerySession that stores the results from a repository RsultSet
 * 
 * @author gavinc
 */
public class ResultSetQuerySession extends AbstractQuerySession
{
   private static Log logger = LogFactory.getLog(ResultSetQuerySession.class);
   private ResultSet results;
   
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
      super(context, nodeService, results.length(), includeMetaData);
      this.results = results;
   }

   /**
    * @see org.alfresco.repo.webservice.repository.AbstractQuerySession#getRows(int, int)
    */
   @Override
   public QueryResult getRows(int from, int to) 
   {
      org.alfresco.repo.webservice.types.ResultSet batchResults = new org.alfresco.repo.webservice.types.ResultSet();      
      org.alfresco.repo.webservice.types.ResultSetRow[] rows = new org.alfresco.repo.webservice.types.ResultSetRow[to-from];

      // TODO: make sure the rows array is not null when there are no results
      
      int arrPos = 0;
      for (int x = from; x < to; x++)
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
      
      return new QueryResult(getId(), batchResults);
   }
}
