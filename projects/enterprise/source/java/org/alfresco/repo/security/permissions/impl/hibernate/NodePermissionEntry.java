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

import java.util.Set;

import org.alfresco.repo.domain.NodeKey;
import org.alfresco.service.cmr.repository.NodeRef;

/**
 * The interface to support persistence of node permission entries in hibernate
 * 
 * @author andyh
 */
public interface NodePermissionEntry
{
    /**
     * Get the node key.
     * 
     * @return
     */
    public NodeKey getNodeKey();

    /**
     * Set the node key.
     * 
     * @param key
     */
    public void setNodeKey(NodeKey key);
    
    /**
     * Get the node ref
     * 
     * @return
     */
    public NodeRef getNodeRef();
    
    /**
     * Get inheritance behaviour
     * @return
     */
    public boolean getInherits();
    
    /**
     * Set inheritance behaviour
     * @param inherits
     */
    public void setInherits(boolean inherits);
    
    /**
     * Get the permission entries set for the node
     * @return
     */
    public Set<PermissionEntry> getPermissionEntries();
    
}
