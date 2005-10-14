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

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.alfresco.repo.security.permissions.NodePermissionEntry;
import org.alfresco.repo.security.permissions.PermissionEntry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.dom4j.Attribute;
import org.dom4j.Element;

/**
 * Support to read and store the definition of node permissions
 * @author andyh
 */
public class NodePermission implements NodePermissionEntry, XMLModelInitialisable
{
    // XML Constants
    
    private static final String NODE_REF = "nodeRef";
    
    private static final String NODE_PERMISSION = "nodePermission";
    
    private static final String INHERIT_FROM_PARENT = "inheritFromParent";
    
    // Instance variables
    
    // If null then it is the root.
    private NodeRef nodeRef;
    
    private Set<PermissionEntry> permissionEntries = new HashSet<PermissionEntry>();
    
    private boolean inheritPermissionsFromParent;
    
    public NodePermission()
    {
        super();
    }

    public NodeRef getNodeRef()
    {
       return nodeRef;
    }

    public boolean inheritPermissions()
    {
        return inheritPermissionsFromParent;
    }

    public Set<PermissionEntry> getPermissionEntries()
    {
       return Collections.unmodifiableSet(permissionEntries);
    }

    public void initialise(Element element, NamespacePrefixResolver nspr, PermissionModel permissionModel)
    {
       Attribute nodeRefAttribute = element.attribute(NODE_REF);
       if(nodeRefAttribute != null)
       {
           nodeRef = new NodeRef(nodeRefAttribute.getStringValue());
       }
       
       Attribute inheritFromParentAttribute = element.attribute(INHERIT_FROM_PARENT);
       if(inheritFromParentAttribute != null)
       {
           inheritPermissionsFromParent = Boolean.parseBoolean(inheritFromParentAttribute.getStringValue());
       }
       else
       {
           inheritPermissionsFromParent = true;
       }
       
       // Node Permissions Entry

       for (Iterator npit = element.elementIterator(NODE_PERMISSION); npit.hasNext(); /**/)
       {
           Element permissionEntryElement = (Element) npit.next();
           ModelPermissionEntry permissionEntry = new ModelPermissionEntry(nodeRef);
           permissionEntry.initialise(permissionEntryElement, nspr, permissionModel);
           permissionEntries.add(permissionEntry);
       }
        
    }

    
    
}
