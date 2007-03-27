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

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;

/**
 * @author Roy Wetherall
 */
public class Node
{
    private NodeService nodeService;
    private Session session;
    private NodeRef nodeRef;
    
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
    
    // TODO need to figure out how we sort this out in the general case
    public String getCm_name()
    {
        return (String)this.nodeService.getProperty(this.nodeRef, ContentModel.PROP_NAME);
    }
    
    public String __toString()
    {
        return this.nodeRef.toString();
    }
}
