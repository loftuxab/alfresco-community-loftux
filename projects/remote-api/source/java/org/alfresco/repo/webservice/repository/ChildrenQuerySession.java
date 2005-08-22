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

import java.util.List;

import javax.xml.rpc.handler.MessageContext;

import org.alfresco.repo.webservice.types.ResultSetRow;
import org.alfresco.repo.webservice.types.ResultSetRowNode;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Implementation of a QuerySession that stores the results from a query for children
 * 
 * @author gavinc
 */
public class ChildrenQuerySession extends AbstractQuerySession
{
   private static Log logger = LogFactory.getLog(ChildrenQuerySession.class);
   private List<ChildAssociationRef> results;
   
   /**
    * Constructs a ChildrenQuerySession
    * 
    * @param context Current MessageContext
    * @param nodeService NodeService instance used to lookup node information
    * @param results The List to cache
    * @param includeMetaData Whether the QueryResult objects returned should contain metadata
    */
   public ChildrenQuerySession(MessageContext context, NodeService nodeService,
         List<ChildAssociationRef> results, boolean includeMetaData)
   {
      super(context, nodeService, results.size(), includeMetaData);
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
         
      int arrPos = 0;
      for (int x = from; x < to; x++)
      {
         ChildAssociationRef assoc = this.results.get(x);
         NodeRef childNodeRef = assoc.getChildRef();
         ResultSetRowNode rowNode = new ResultSetRowNode(childNodeRef.getId(), nodeService.getType(childNodeRef).toString(), null);
         ResultSetRow row = new ResultSetRow();
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
