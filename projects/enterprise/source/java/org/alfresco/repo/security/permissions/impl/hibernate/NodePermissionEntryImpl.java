/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Alfresco Network License. You may obtain a
 * copy of the License at
 *
 *   http://www.alfrescosoftware.com/legal/
 *
 * Please view the license relevant to your network subscription.
 *
 * BY CLICKING THE "I UNDERSTAND AND ACCEPT" BOX, OR INSTALLING,  
 * READING OR USING ALFRESCO'S Network SOFTWARE (THE "SOFTWARE"),  
 * YOU ARE AGREEING ON BEHALF OF THE ENTITY LICENSING THE SOFTWARE    
 * ("COMPANY") THAT COMPANY WILL BE BOUND BY AND IS BECOMING A PARTY TO 
 * THIS ALFRESCO NETWORK AGREEMENT ("AGREEMENT") AND THAT YOU HAVE THE   
 * AUTHORITY TO BIND COMPANY. IF COMPANY DOES NOT AGREE TO ALL OF THE   
 * TERMS OF THIS AGREEMENT, DO NOT SELECT THE "I UNDERSTAND AND AGREE"   
 * BOX AND DO NOT INSTALL THE SOFTWARE OR VIEW THE SOURCE CODE. COMPANY   
 * HAS NOT BECOME A LICENSEE OF, AND IS NOT AUTHORIZED TO USE THE    
 * SOFTWARE UNLESS AND UNTIL IT HAS AGREED TO BE BOUND BY THESE LICENSE  
 * TERMS. THE "EFFECTIVE DATE" FOR THIS AGREEMENT SHALL BE THE DAY YOU  
 * CHECK THE "I UNDERSTAND AND ACCEPT" BOX.
 */
package org.alfresco.repo.security.permissions.impl.hibernate;

import java.util.HashSet;
import java.util.Set;

import org.alfresco.repo.domain.NodeKey;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;

/**
 * The hibernate persisted class for node permission entries.
 * 
 * @author andyh
 */
public class NodePermissionEntryImpl implements NodePermissionEntry
{
    /**
     * The key to find node permission entries
     */
    private NodeKey nodeKey;

    /**
     * Inherit permissions from the parent node?
     */
    private boolean inherits;

    /**
     * The set of permission entries.
     */
    private Set<PermissionEntry> permissionEntries = new HashSet<PermissionEntry>();

    public NodePermissionEntryImpl()
    {
        super();
    }

    public NodeKey getNodeKey()
    {
        return nodeKey;
    }

    public void setNodeKey(NodeKey nodeKey)
    {
        this.nodeKey = nodeKey;
    }

    public NodeRef getNodeRef()
    {
        return new NodeRef(new StoreRef(nodeKey.getProtocol(), nodeKey
                .getIdentifier()), nodeKey.getGuid());
    }

    public boolean getInherits()
    {
        return inherits;
    }

    public void setInherits(boolean inherits)
    {
        this.inherits = inherits;
    }

    public Set<PermissionEntry> getPermissionEntries()
    {
        return permissionEntries;
    }
    
    // Hibernate
    
    /* package */ void setPermissionEntries(Set<PermissionEntry> permissionEntries)
    {
        this.permissionEntries = permissionEntries;
    }

    // Hibernate pattern
    
    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof NodePermissionEntryImpl))
        {
            return false;
        }
        NodePermissionEntryImpl other = (NodePermissionEntryImpl) o;

        return this.nodeKey.equals(other.nodeKey)
                && (this.inherits == other.inherits)
                && (this.permissionEntries.equals(other.permissionEntries));
    }

    @Override
    public int hashCode()
    {
        return nodeKey.hashCode();
    }

}
