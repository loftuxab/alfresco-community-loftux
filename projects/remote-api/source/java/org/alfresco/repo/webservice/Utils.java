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
package org.alfresco.repo.webservice;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.alfresco.repo.webservice.types.NamedValue;
import org.alfresco.repo.webservice.types.ResultSet;
import org.alfresco.repo.webservice.types.ResultSetRow;
import org.alfresco.repo.webservice.types.ResultSetRowNode;
import org.alfresco.repo.webservice.types.Store;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.Path;
import org.alfresco.service.cmr.repository.StoreRef;

/**
 * Helper class used by the web services
 * 
 * @author gavinc
 */
public class Utils
{   
   private Utils()
   {
      // don't allow construction
   }
   
   /**
    * Converts the web service Store type to a StoreRef used by the repository
    * 
    * @param store The Store to convert
    * @return The converted StoreRef
    */
   public static StoreRef convertToStoreRef(Store store)
   {
      return new StoreRef(store.getScheme().getValue(), store.getAddress());
   }
   
   /**
    * Converts the repository ResultSet object into a web service ResultSet object
    * 
    * @param origResults The repository ResultSet
    * @param includeMetaData Whether to include meta data in the returned object
    * @param nodeService The node service to use to retrieve data from the repository
    * @return The web service ResultSet object
    */
   public static ResultSet convertToResultSet(org.alfresco.service.cmr.search.ResultSet origResults, 
         boolean includeMetaData, NodeService nodeService)
   {
      ResultSet results = new ResultSet();
      int size = origResults.length();
      
      // build up all the row data
      ResultSetRow[] rows = new ResultSetRow[size];
      for (int x = 0; x < size; x++)
      {
         org.alfresco.service.cmr.search.ResultSetRow origRow = origResults.getRow(x);
         NodeRef nodeRef = origRow.getNodeRef();
         ResultSetRowNode rowNode = new ResultSetRowNode(nodeRef.getId(), nodeService.getType(nodeRef).toString(), null);
         
         // get the data for the row and build up the columns structure
         Map<Path, Serializable> values = origRow.getValues();
         NamedValue[] columns = new NamedValue[values.size()];
         int col = 0;
         for (Path path : values.keySet())
         {
            columns[col] = new NamedValue(path.toString(), values.get(path).toString());
            col++;
         }
         
         ResultSetRow row = new ResultSetRow();
         row.setColumns(columns);
         row.setScore(origRow.getScore());
         row.setRowIndex(x);
         row.setNode(rowNode);
         
         // add the row to the overall results
         rows[x] = row;
      }
      
      // TODO: build up the meta data data structure if asked to
      
      // add the rows to the result set and set the size
      results.setRows(rows);
      results.setSize(size);
      
      return results;
   }
   
   /**
    * Converts the list of childs associations to a web service ResultSet object
    * 
    * @param kids The list of child associations
    * @param nodeService The node service to use to retrieve required data from the repository
    * @return The web service ResultSet object
    */
   public static ResultSet convertToResultSet(List<ChildAssociationRef> kids, NodeService nodeService)
   {
      ResultSet results = new ResultSet();
      int size = kids.size();
      
      // build up all the row data
      ResultSetRow[] rows = new ResultSetRow[size];
      for (int x = 0; x < size; x++)
      {
         ChildAssociationRef assoc = kids.get(x);
         NodeRef nodeRef = assoc.getChildRef();
         ResultSetRowNode rowNode = new ResultSetRowNode(nodeRef.getId(), nodeService.getType(nodeRef).toString(), null);
         ResultSetRow row = new ResultSetRow();
         row.setRowIndex(x);
         row.setNode(rowNode);
         
         // add the row to the overall results
         rows[x] = row;
      }
      
      // add the rows to the result set and set the size
      results.setRows(rows);
      results.setSize(size);
      
      return results; 
   }
}
