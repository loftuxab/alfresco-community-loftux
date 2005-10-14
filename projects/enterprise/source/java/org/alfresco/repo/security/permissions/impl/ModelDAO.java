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

import java.util.Set;

import org.alfresco.repo.security.permissions.PermissionEntry;
import org.alfresco.repo.security.permissions.PermissionReference;
import org.alfresco.repo.security.permissions.impl.model.RequiredPermission;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

/**
 * The API for the alfresco permission model.
 * 
 * @author Andy Hind
 */
public interface ModelDAO
{

    /**
     * Get the permissions that can be set for the given type.
     * 
     * @param type - the type in the data dictionary.
     * @return
     */
    public Set<PermissionReference> getAllPermissions(QName type);

    /**
     * Get the permissions that can be set for the given node. 
     * This is determined by the node type.
     * 
     * @param nodeRef
     * @return
     */
    public Set<PermissionReference> getAllPermissions(NodeRef nodeRef);
    
    /**
     *Get the permissions that are exposed to be set for the given type.
     * 
     * @param type - the type in the data dictionary.
     * @return
     */
    public Set<PermissionReference> getExposedPermissions(QName type);

    /**
     * Get the permissions that are exposed to be set for the given node. 
     * This is determined by the node type.
     * 
     * @param nodeRef
     * @return
     */
    public Set<PermissionReference> getExposedPermissions(NodeRef nodeRef);

    /**
     * Get all the permissions that grant this permission.
     * 
     * @param perm
     * @return
     */
    public Set<PermissionReference> getGrantingPermissions(PermissionReference perm);

    /**
     * Get the permissions that must also be present on the node for the required permission to apply.
     *  
     * @param required
     * @param qName
     * @param aspectQNames
     * @param on
     * @return
     */
    public Set<PermissionReference> getRequiredPermissions(PermissionReference required, QName qName, Set<QName> aspectQNames, RequiredPermission.On on);

    /**
     * Get the permissions which are granted by the supplied permission.
     * 
     * @param permissionReference
     * @return
     */
    public Set<PermissionReference> getGranteePermissions(PermissionReference permissionReference);

    /**
     * Is this permission refernece to a permission and not a permissoinSet?
     * 
     * @param required
     * @return
     */
    public boolean checkPermission(PermissionReference required);

    /**
     * Does the permission reference have a unique name?
     * 
     * @param permissionReference
     * @return
     */
    public boolean isUnique(PermissionReference permissionReference);

    /**
     * Find a permission by name in the type context.
     * If the context is null and the permission name is unique it will be found.
     * 
     * @param qname
     * @param permissionName
     * @return
     */
    public PermissionReference getPermissionReference(QName qname, String permissionName);
    
    /**
     * Get the global permissions for the model.
     * Permissions that apply to all nodes and take precedence over node specific permissions.
     * 
     * @return
     */
    public Set<? extends PermissionEntry> getGlobalPermissionEntries();

}
