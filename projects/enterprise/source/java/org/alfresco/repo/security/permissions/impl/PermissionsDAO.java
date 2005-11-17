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

import org.alfresco.repo.security.permissions.NodePermissionEntry;
import org.alfresco.repo.security.permissions.PermissionEntry;
import org.alfresco.repo.security.permissions.PermissionReference;
import org.alfresco.service.cmr.repository.NodeRef;

/**
 * The API for accessing persisted Alfresco permissions.
 * 
 * @author andyh
 */
public interface PermissionsDAO
{

    /**
     * Get the permissions that have been set on a given node.
     * 
     * @param nodeRef
     * @return
     */
    public NodePermissionEntry getPermissions(NodeRef nodeRef);

    /**
     * Delete all the permissions on a given node.
     * The node permission and all the permission entries it contains will be deleted.
     * 
     * @param nodeRef
     */
    public void deletePermissions(NodeRef nodeRef);

    /**
     * Delete all the permissions on a given node.
     * The node permission and all the permission entries it contains will be deleted.
     * 
     * @param nodePermissionEntry
     */
    public void deletePermissions(NodePermissionEntry nodePermissionEntry);

    
    /**
     * Delete as single permission entry.
     * This deleted one permission on the node. It does not affect the persistence of any other permissions.
     * 
     * @param permissionEntry
     */
    public void deletePermissions(PermissionEntry permissionEntry);

    /**
     * 
     * Delete as single permission entry, if a match is found.
     * This deleted one permission on the node. It does not affect the persistence of any other permissions.
     * 
     * @param nodeRef
     * @param authority
     * @param perm
     * @param allow
     */
    public void deletePermissions(NodeRef nodeRef, String authority, PermissionReference perm,  boolean allow);

    /**
     * Set a permission on a node.
     * If the node has no permissions set then a default node permission (allowing inheritance) will be created to
     * contain the permission entry.
     * 
     * @param nodeRef
     * @param authority
     * @param perm
     * @param allow
     */
    public void setPermission(NodeRef nodeRef, String authority, PermissionReference perm, boolean allow);

    /**
     * Create a persisted permission entry given and other representation of a permission entry.
     * 
     * @param permissionEntry
     */
    public void setPermission(PermissionEntry permissionEntry);

    /**
     * Create a persisted node permission entry given a template object from which to copy.
     * 
     * @param nodePermissionEntry
     */
    public void setPermission(NodePermissionEntry nodePermissionEntry);

    /**
     * Set the inheritance behaviour for permissions on a given node.
     * 
     * @param nodeRef
     * @param inheritParentPermissions
     */
    public void setInheritParentPermissions(NodeRef nodeRef, boolean inheritParentPermissions);

    /**
     * Clear all the permissions set for a given authentication
     * 
     * @param nodeRef
     * @param authority
     */
    public void clearPermission(NodeRef nodeRef, String authority);
    
    /**
     * Remove all permissions for the specvified authority
     * @param authority
     */
    public void deleteAllPermissionsForAuthority(String authority);

}
