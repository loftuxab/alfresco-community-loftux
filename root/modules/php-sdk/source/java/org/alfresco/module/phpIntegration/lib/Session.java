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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.apache.log4j.Logger;

/**
 * @author Roy Wetherall
 */
public class Session implements ScriptObject
{
    private static Logger logger = Logger.getLogger(Session.class);
    
    private static final String SCRIPT_OBJECT_NAME = "Session";
    
    private ServiceRegistry serviceRegistry;
    
    private NamespaceMap namespaceMap;
    
    private Map<String, Node> nodeMap;
    
    public Session(ServiceRegistry serviceRegistry)
    {
        this.serviceRegistry = serviceRegistry;
        this.namespaceMap = new NamespaceMap(this);
        this.nodeMap = new HashMap<String, Node>(10);
    }
    
    public String getScriptObjectName()
    {
        return SCRIPT_OBJECT_NAME;
    }
    
    public ServiceRegistry getServiceRegistry()
    {
        return serviceRegistry;
    }
    
    public String getTicket()
    {
        return this.serviceRegistry.getAuthenticationService().getCurrentTicket();
    }
    
    public NamespaceMap getNamespaceMap()
    {
        return this.namespaceMap;
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
    
    
    // TODO figure out how to use the attributes to set the default values ...
    public Store getStore(String address, String scheme)
    {
        Store store = null;
        
        // Set the default value
        if (scheme == null)
        {
            scheme = StoreRef.PROTOCOL_WORKSPACE;
        }
        
        // Check for the existance of the store
        StoreRef storeRef = new StoreRef(scheme, address);
        if (this.serviceRegistry.getNodeService().exists(storeRef) == true)
        {
            store = new Store(this, storeRef);
        }
        
        return store;
    }
    
    public Store getStoreFromString(String value)
    {
        Store store = null;
        StoreRef storeRef = new StoreRef(value);
        
        // Check for the existance of the store
        if (this.serviceRegistry.getNodeService().exists(storeRef) == true)
        {
            store = new Store(this, storeRef);
        }
        
        return store;
    }
    
    public Node getNode(Store store, String id)
    {
        NodeRef nodeRef = new NodeRef(store.getStoreRef(), id);
        Node node = this.nodeMap.get(nodeRef.toString());
        
        if (node == null)
        {        
            // Check for the existance of the node        
            if (this.serviceRegistry.getNodeService().exists(nodeRef) == true)
            {
                node = new Node(this, nodeRef);
            }
        }
        
        return node;
    }
    
    public Node getNodeFromString(String nodeString)
    {
        Node node = this.nodeMap.get(nodeString); 
        
        if (node == null)
        {        
            // Check for the existance of the node
            NodeRef nodeRef = new NodeRef(nodeString);
            if (this.serviceRegistry.getNodeService().exists(nodeRef) == true)
            {
                node = new Node(this, nodeRef);
            }
        }
        
        return node;
    }
    
    /**
     * Adds a node to the session cache
     * 
     * @param node  the node to add to the session cache
     */
    /*package*/ void addNode(Node node)
    {
        // Log a warning if the node is already in the session
        if (this.nodeMap.containsKey(node.toString()) == true)
        {
            if (logger.isInfoEnabled() == true)
            {
                logger.info("A duplicate node is being added to the session. (" + node.toString() + ")");
            }
        }
        
        // Add the node
        this.nodeMap.put(node.toString(), node);
    }
    
    /**
     * Remove the node from the session cache.  This be called with care as it could mean modificaiton made to the node are lost.
     * 
     * @param node  the node to remove from the session cache
     */
    /*package*/ void removeNode(Node node)
    {
        // Remove the node
        this.nodeMap.remove(node.toString());
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
    
    public void save()
    {
        if (logger.isDebugEnabled() == true)
        {
            logger.debug("Saving session");
        }
        
        // Prepare for the save
        for (Node node : this.nodeMap.values())
        {
            // Prepare each node for saving
            node.prepareSave();
        }
        
        // Check each node and see whether the node needs to be saved
        for (Node node : this.nodeMap.values())
        {
            // Do the save processing on each node
            node.onSave();
        }
    }
    
    public void clean()
    {
        // Clear the node map to clean the session
        this.nodeMap.clear();
    }
}
