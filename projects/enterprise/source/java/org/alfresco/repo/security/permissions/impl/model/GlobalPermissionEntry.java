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
package org.alfresco.repo.security.permissions.impl.model;

import org.alfresco.repo.security.permissions.PermissionEntry;
import org.alfresco.repo.security.permissions.PermissionReference;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.AccessStatus;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.dom4j.Attribute;
import org.dom4j.Element;

public class GlobalPermissionEntry implements XMLModelInitialisable, PermissionEntry
{
    private static final String AUTHORITY = "authority";
    
    private static final String PERMISSION = "permission";
    
    private String authority;
    
    private PermissionReference permissionReference;
    
    public GlobalPermissionEntry()
    {
        super();
        // TODO Auto-generated constructor stub
    }

    public void initialise(Element element, NamespacePrefixResolver nspr, PermissionModel permissionModel)
    {
        Attribute authorityAttribute = element.attribute(AUTHORITY);
        if(authorityAttribute != null)
        {
            authority = authorityAttribute.getStringValue();
        }
        Attribute permissionAttribute = element.attribute(PERMISSION);
        if(permissionAttribute != null)
        {
            permissionReference = permissionModel.getPermissionReference(null, permissionAttribute.getStringValue());
        }

    }
    
    public String getAuthority()
    {
        return authority;
    }
    
    public PermissionReference getPermissionReference()
    {
        return permissionReference;
    }

    public NodeRef getNodeRef()
    {
        return null;
    }

    public boolean isDenied()
    {
        return false;
    }

    public boolean isAllowed()
    {
        return true;
    }

    public AccessStatus getAccessStatus()
    {
        return AccessStatus.ALLOWED;
    }

}
