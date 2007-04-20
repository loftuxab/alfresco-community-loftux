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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.datatype.DefaultTypeConverter;
import org.alfresco.service.namespace.QName;

import com.caucho.quercus.env.Value;

/**
 * @author Roy Wetherall
 */
public class Node implements ScriptObject
{
    private static final String SCRIPT_OBJECT_NAME = "Node";
    
    private NodeService nodeService;
    private Session session;
    private NodeRef nodeRef;
    
    private boolean arePropertiesDirty = false;
    private Map<String, String> properties;
    
    public String getScriptObjectName()
    {
        return SCRIPT_OBJECT_NAME;
    }
    
    public Node(Session session, NodeRef nodeRef)
    {
        this.session = session;
        this.nodeRef = nodeRef;
        this.nodeService = session.getServiceRegistry().getNodeService();
    }
    
    public Node(Session session, Store store, String id)
    {
        this.session = session;
        this.nodeRef = new NodeRef(store.getStoreRef(), id);
    }
    
    // TODO this should look in the session cache and reuse the node (do we need to do that when running in the VM?)
    public static Node createNode(Session session, Store store, String id)
    {
        return new Node(session, store, id);
    }
    
    public NodeRef getNodeRef()
    {
        return nodeRef;
    }
    
    /**
     * Get the nodes store
     * 
     * @return  the Store
     */
    public Store getStore()
    {
        return new Store(this.session, this.nodeRef.getStoreRef());
    }
    
    /**
     * Gets the id of the node
     * 
     * @return  the id of the node
     */
    public String getId()
    {
        return this.nodeRef.getId();
    }
    
    /** 
     * Gets the type of the node
     * 
     * @return  the node type
     */
    public String getType()
    {
        return this.nodeService.getType(this.nodeRef).toString();
    }
    
    /**
     * Get the map of property names and values
     * 
     * @return  a map of property names and values
     */
    public Map<String, String> getProperties()
    {
        return getPropertiesImpl();
    }
    
    /**
     * Sets the property values for the node
     * 
     * @param properties    a map of property names and values
     */
    public void setProperties(Map<String, String> properties)
    {
        this.properties = properties;
        this.arePropertiesDirty = true;
    }        
    
    /**
     * Dynamic implementation of the properties
     * 
     * @param name
     * @return
     */
    public Value __getField(Value name)
    {
        System.out.println("__getField: " + name.toString());

        return name;
        
    }

    public void __setField(String name, String value)
    {
        System.out.println("__set: " + name.toString());
    }
    
    public String __toString()
    {
        return this.nodeRef.toString();
    }
    
    private Map<String, String> getPropertiesImpl()
    {
        if (this.properties == null)
        {
            Map<QName, Serializable> properties = this.nodeService.getProperties(this.nodeRef);
            this.properties = new HashMap<String, String>(properties.size());
            for (Map.Entry<QName, Serializable> entry : properties.entrySet())
            {
                String value = DefaultTypeConverter.INSTANCE.convert(String.class, entry.getValue());
                this.properties.put(entry.getKey().toString(), value);
            }
            this.arePropertiesDirty = false;
        }
        return this.properties;
    }
    
}
