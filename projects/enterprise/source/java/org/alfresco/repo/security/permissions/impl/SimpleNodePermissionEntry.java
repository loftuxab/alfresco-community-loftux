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
package org.alfresco.repo.security.permissions.impl;

import java.io.Serializable;
import java.util.Set;

import org.alfresco.repo.security.permissions.PermissionEntry;
import org.alfresco.service.cmr.repository.NodeRef;

/**
 * A simple object representation of a node permission entry
 * 
 * @author andyh
 */
public class SimpleNodePermissionEntry extends AbstractNodePermissionEntry implements Serializable
{
    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 8157870444595023347L;

    /*
     * The node 
     */
    private NodeRef nodeRef;
    
    /*
     * Are permissions inherited?
     */
    private boolean inheritPermissions;
    
    /*
     * The set of permission entries.
     */
    private Set<? extends PermissionEntry> permissionEntries;
    
    
    public SimpleNodePermissionEntry(NodeRef nodeRef, boolean inheritPermissions, Set<? extends PermissionEntry> permissionEntries)
    {
        super();
        this.nodeRef = nodeRef;
        this.inheritPermissions = inheritPermissions;
        this.permissionEntries = permissionEntries;
    }

    public NodeRef getNodeRef()
    {
        return nodeRef;
    }

    public boolean inheritPermissions()
    {
        return inheritPermissions;
    }

    public Set<? extends PermissionEntry> getPermissionEntries()
    {
       return permissionEntries;
    }

}
