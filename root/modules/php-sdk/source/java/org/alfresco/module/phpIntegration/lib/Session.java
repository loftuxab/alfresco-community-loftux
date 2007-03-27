/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.module.phpIntegration.lib;

import java.util.List;

import javax.servlet.ServletContext;

import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.caucho.quercus.env.Env;

/**
 * @author Roy Wetherall
 */
public class Session
{
    private ServiceRegistry serviceRegistry;
    
    public Session(Env env)
    {
        ServletContext servletContext = env.getRequest().getSession().getServletContext();
        ApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        this.serviceRegistry = (ServiceRegistry)applicationContext.getBean("ServiceRegistry");
    }
    
    public Session(ServiceRegistry serviceRegistry)
    {
        this.serviceRegistry = serviceRegistry;
    }
    
    public ServiceRegistry getServiceRegistry()
    {
        return serviceRegistry;
    }
    
    public Store[] getStores()
    {
        // Get the node service
        NodeService nodeService = this.serviceRegistry.getNodeService();
        
        // Get the stores
        List<StoreRef> storeRefs = nodeService.getStores();
        
        // Buld the result array
        Store[] result = new Store[storeRefs.size()];
        int index = 0;
        for (StoreRef storeRef : storeRefs)
        {
            result[index] = new Store(this, storeRef);
            index ++;
        }
        
        return result;
    }
    
    /**
     * Execute a query
     * 
     * @param store
     * @param statement
     * @param language
     * @return
     */
    public Node[] query(Store store, String statement, String language)
    {
        Node[] result = null;
        
        // Get the search service
        SearchService searchService = this.getServiceRegistry().getSearchService();
        
        // Set the default search language
        if (language == null)
        {
            language = SearchService.LANGUAGE_LUCENE;
        }
        
        // Do the search
        ResultSet resultSet = searchService.query(store.getStoreRef(), language, statement);
        List<NodeRef> nodeRefs = resultSet.getNodeRefs();
        result = new Node[nodeRefs.size()];
        int iIndex = 0;
        for (NodeRef nodeRef : nodeRefs)
        {
            result[iIndex] = new Node(this, nodeRef);
            iIndex++;
        }        
        
        return result;
    }
}
