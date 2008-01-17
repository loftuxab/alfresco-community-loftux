/*
 * Copyright (C) 2005-2007 Alfresco Software Limited.
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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;

/**
 * @author Roy Wetherall
 */
public class NodeFactory
{
    private List<QName> typeOrder = new ArrayList<QName>(5);
    private Map<QName, Class<? extends Node>> typeMap = new HashMap<QName, Class<? extends Node>>(5);
    
    private DictionaryService dictionaryService;
    private NodeService nodeService;
    
    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }
    
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
    
    public void addNodeType(QName type, Class<? extends Node> clazz)
    {
        // Maintain the correct lookup order
        int insertIndex = 0;
        for (QName registeredQName : this.typeOrder)
        {
            if (dictionaryService.isSubClass(type, registeredQName) == true)
            {
                break;
            }
            insertIndex++;
        }
        
        // Store the details of the type
        this.typeOrder.add(insertIndex, type);
        this.typeMap.put(type, clazz);
    }
    
    public Node createNode(Session session, Store store, String id, String type)
    {
        Node node = null;
        
        // Get the type of the node
        QName typeQName = QName.createQName(type);
        
        // See if we can find a match in the list of registered types
        for (QName registeredQName : this.typeOrder)
        {
            if (dictionaryService.isSubClass(typeQName, registeredQName) == true)
            {
                try
                {
                    // Create the node and break
                    Constructor<? extends Node> constructor = this.typeMap.get(registeredQName).getDeclaredConstructor(Session.class, Store.class, String.class, String.class);
                    node = constructor.newInstance(session, store, id, type);
                }
                // Ignore any exceptions raised and go on to create a node
                catch (IllegalAccessException exception) {}
                catch (InvocationTargetException exception) {}
                catch (InstantiationException exception) {}
                catch (NoSuchMethodException exception) {}
                
                // Break
                break;
            }
        }
        
        // Create a normal node if non specified
        if (node == null)
        {
            node = new Node(session, store, id, type);
        }
                
        return node;
        
    }
    
    public Node createNode(Session session, NodeRef nodeRef)
    {
        Node node = null;
        
        // Get the type of the node
        QName type = nodeService.getType(nodeRef);
        
        // See if we can find a match in the list of registered types
        for (Map.Entry<QName, Class<? extends Node>> entry : this.typeMap.entrySet())
        {
            if (dictionaryService.isSubClass(type, entry.getKey()) == true)
            {
                try
                {
                    // Create the node and break
                    Constructor<? extends Node> constructor = entry.getValue().getDeclaredConstructor(Session.class, NodeRef.class);
                    node = constructor.newInstance(session, nodeRef);
                }
                // Ignore any exceptions raised and go on to create a node
                catch (IllegalAccessException exception) {}
                catch (InvocationTargetException exception) {}
                catch (InstantiationException exception) {}
                catch (NoSuchMethodException exception) {}
                
                // Break
                break;
            }
        }
        
        // Create a normal node if non specified
        if (node == null)
        {
            node = new Node(session, nodeRef);
        }
                
        return node;
    }
}
