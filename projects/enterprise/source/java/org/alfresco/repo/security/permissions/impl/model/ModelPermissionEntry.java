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
import org.alfresco.repo.security.permissions.impl.PermissionReferenceImpl;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.AccessStatus;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;
import org.dom4j.Attribute;
import org.dom4j.Element;

/**
 * Support to read and store the definion of a permission entry.
 * 
 * @author andyh
 */
public class ModelPermissionEntry implements PermissionEntry, XMLModelInitialisable
{
    // XML Constants
    
    private static final String PERMISSION_REFERENCE = "permissionReference";

    private static final String RECIPIENT = "recipient";

    private static final String ACCESS = "access";

    private static final String DENY = "deny";

    private static final String ALLOW = "allow";

    private static final String TYPE = "type";
    
    private static final String NAME = "name";

    // Instance variables
    
    private String recipient;

    private AccessStatus access;

    private PermissionReference permissionReference;

    private NodeRef nodeRef;

    public ModelPermissionEntry(NodeRef nodeRef)
    {
        super();
        this.nodeRef = nodeRef;
    }

    public PermissionReference getPermissionReference()
    {
        return permissionReference;
    }

    public String getAuthority()
    {
        return getRecipient();
    }

    public String getRecipient()
    {
        return recipient;
    }

    public NodeRef getNodeRef()
    {
        return nodeRef;
    }

    public boolean isDenied()
    {
        return access == AccessStatus.DENIED;
    }

    public boolean isAllowed()
    {
        return access == AccessStatus.ALLOWED;
    }

    public AccessStatus getAccessStatus()
    {
        return access;
    }

    public void initialise(Element element, NamespacePrefixResolver nspr, PermissionModel permissionModel)
    {
        Attribute recipientAttribute = element.attribute(RECIPIENT);
        if (recipientAttribute != null)
        {
            recipient = recipientAttribute.getStringValue();
        }
        else
        {
            recipient = null;
        }

        Attribute accessAttribute = element.attribute(ACCESS);
        if (accessAttribute != null)
        {
            if (accessAttribute.getStringValue().equalsIgnoreCase(ALLOW))
            {
                access = AccessStatus.ALLOWED;
            }
            else if (accessAttribute.getStringValue().equalsIgnoreCase(DENY))
            {
                access = AccessStatus.DENIED;
            }
            else
            {
                throw new PermissionModelException("The default permission must be deny or allow");
            }
        }
        else
        {
            access = AccessStatus.DENIED;
        }
        
        
        Element permissionReferenceElement = element.element(PERMISSION_REFERENCE);
        QName typeQName = QName.createQName(permissionReferenceElement.attributeValue(TYPE), nspr);
        String name = permissionReferenceElement.attributeValue(NAME);
        permissionReference = new PermissionReferenceImpl(typeQName, name);
    }
}
