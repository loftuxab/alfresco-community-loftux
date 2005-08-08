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

import java.rmi.RemoteException;

import org.alfresco.repo.webservice.types.NamedValue;
import org.alfresco.repo.webservice.types.Query;
import org.alfresco.repo.webservice.types.ResultSet;
import org.alfresco.repo.webservice.types.ResultSetMetaData;
import org.alfresco.repo.webservice.types.ResultSetRow;
import org.alfresco.repo.webservice.types.Store;
import org.alfresco.repo.webservice.types.StoreEnum;
import org.alfresco.repo.webservice.types.ValueDefinition;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.util.GUID;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Web service implementation of the RepositoryService.
 * The WSDL for this service can be accessed from http://localhost:8080/web-client/remote-api/RepositoryService?wsdl
 * 
 * @author gavinc
 */
public class RepositoryWebService implements RepositoryServiceSoapPort
{
   private static Log logger = LogFactory.getLog(RepositoryWebService.class);
   
   private NodeService nodeService;
   
   /**
    * Sets the instance of the NodeService to be used
    * 
    * @param nodeService The NodeService
    */
   public void setNodeService(NodeService nodeService)
   {
      this.nodeService = nodeService;
   }

   /**
    * @see org.alfresco.repo.webservice.repository.RepositoryServiceSoapPort#getStores()
    */
   public Store[] getStores() throws RemoteException, RepositoryFault
   {
      // TODO: Return the proper list of stores
      
      Store store1 = new Store(StoreEnum.workspace, "SpacesStore");
      Store store2 = new Store(StoreEnum.version, "LightweightVersionStore");
      
      return new Store[] {store1, store2};
   }

   /**
    * @see org.alfresco.repo.webservice.repository.RepositoryServiceSoapPort#query(org.alfresco.repo.webservice.types.Store, org.alfresco.repo.webservice.types.Query, boolean)
    */
   public QueryResult query(Store store, Query query, boolean includeMetaData) throws RemoteException, RepositoryFault
   {
      logger.info("query received is " + query.getStatement());
      logger.info("query language is " + query.getLanguage());
      
      QueryResult queryResult = new QueryResult();
      
      queryResult.setQuerySession(GUID.generate());
      
      ResultSetRow row1 = new ResultSetRow(1, new NamedValue[] {new NamedValue("name", "Gav")}, new Float(1.0), null);
      ResultSetRow row2 = new ResultSetRow(2, new NamedValue[] {new NamedValue("name", "Dave")}, new Float(1.0), null);
      ResultSetRow[] rows = new ResultSetRow[] {row1, row2};
      
      ValueDefinition valueDef = new ValueDefinition();
      valueDef.setDataType("string");
      valueDef.setDescription("desc");
      valueDef.setName("name");
      valueDef.setTitle("title");
      
      ResultSetMetaData metaData = new ResultSetMetaData();
      metaData.setValueDef(new ValueDefinition[] {valueDef});
      
      ResultSet resultSet = new ResultSet();
      resultSet.setSize(2);
      resultSet.setMetaData(metaData);
      resultSet.setRow(rows);
      
      queryResult.setResultSet(resultSet);
      
      return queryResult;
   }
}
