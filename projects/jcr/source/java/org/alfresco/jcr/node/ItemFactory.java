/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.jcr.node;

import java.util.List;

import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;

import org.alfresco.jcr.session.SessionContext;
import org.alfresco.jcr.session.SessionContextProxyFactory;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.search.SearchService;


/**
 * Factory for creating JCR Items (Nodes, Properties) from Alfresco equivalents 
 * 
 * @author David Caruana
 *
 */
public class ItemFactory
{

    /**
     * Create a Node from a Node Reference
     * 
     * @param context  session context
     * @param nodeRef  the node reference
     * @return  the JCR Node
     */
    public static Node createNode(SessionContext context, NodeRef nodeRef)
    {
        NodeImpl nodeImpl = new NodeImpl(context, nodeRef);
        Node node = (Node)SessionContextProxyFactory.create(nodeImpl, Node.class, context);
        return node;
    }
    
    
    /**
     * Create an Item from a JCR Path
     * 
     * @param context  session context
     * @param from  starting node for path
     * @param path  the path
     * @return  the Item (Node or Property)
     * @throws PathNotFoundException
     */
    public static Item createItem(SessionContext context, NodeRef from, String path)
        throws PathNotFoundException
    {
        Item item = null;
        
        NodeRef nodeRef = getNodeRef(context, from, path);
        if (nodeRef != null)
        {
            item = createNode(context, nodeRef);
        }
        else
        {
            // TODO: create property
        }
        
        if (item == null)
        {
            throw new PathNotFoundException("Path " + path + " not found.");
        }

        return item;
    }


    /**
     * Determine if Item exists
     * 
     * @param context  session context
     * @param from  starting node for path
     * @param path  the path
     * @return  true => exists, false => no it doesn't
     */
    public static boolean itemExists(SessionContext context, NodeRef from, String path)
    {
        boolean exists = false;

        NodeRef nodeRef = getNodeRef(context, from, path);
        if (nodeRef != null)
        {
            exists = true;
        }
        else
        {
            // TODO: Look for property
        }
        
        return exists;
    }

    
    /**
     * Gets the Node Reference for the node at the specified path
     * 
     * @param context  session context
     * @param from  the starting node for the path
     * @param path  the path
     * @return  the node reference (or null if not found)
     */
    private static NodeRef getNodeRef(SessionContext context, NodeRef from, String path)
    {
        NodeRef nodeRef = null;
        
        // TODO: Support JCR Path
        // TODO: Catch malformed path and return false (per Specification)
        SearchService search = context.getServiceRegistry().getSearchService(); 
        List<NodeRef> nodeRefs = search.selectNodes(from, path, null, context.getNamespaceResolver(), false);
        if (nodeRefs != null && nodeRefs.size() > 0)
        {
            nodeRef = nodeRefs.get(0);
        }
            
        return nodeRef;
    }
    
}
