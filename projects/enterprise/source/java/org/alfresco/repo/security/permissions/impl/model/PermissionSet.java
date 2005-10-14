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

import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;
import org.dom4j.Attribute;
import org.dom4j.Element;

/**
 * Store and read the definition of a permission set
 * @author andyh
 */
public class PermissionSet implements XMLModelInitialisable
{
    private static final String TYPE = "type";
    private static final String PERMISSION_GROUP = "permissionGroup";
    private static final String PERMISSION = "permission";
    private static final String EXPOSE = "expose";
    private static final String EXPOSE_ALL = "all";
    //private static final String EXPOSE_SELECTED = "selected";
    
    
    private QName qname;
    
    private boolean exposeAll;
    
    private Set<PermissionGroup> permissionGroups = new HashSet<PermissionGroup>();
    
    private Set<Permission> permissions = new HashSet<Permission>();
    
    public PermissionSet()
    {
        super();
    }
    
    public void initialise(Element element, NamespacePrefixResolver nspr, PermissionModel permissionModel)
    {
        qname = QName.createQName(element.attributeValue(TYPE), nspr);
        
        Attribute exposeAttribute = element.attribute(EXPOSE);
        if(exposeAttribute != null)
        {
            exposeAll = exposeAttribute.getStringValue().equalsIgnoreCase(EXPOSE_ALL);
        }
        else
        {
            exposeAll = true;
        }
        
        for(Iterator pgit = element.elementIterator(PERMISSION_GROUP); pgit.hasNext(); /**/)
        {
            Element permissionGroupElement = (Element)pgit.next();
            PermissionGroup permissionGroup = new PermissionGroup(qname);
            permissionGroup.initialise(permissionGroupElement, nspr, permissionModel);
            permissionGroups.add(permissionGroup);
        }
        
        for(Iterator pit = element.elementIterator(PERMISSION); pit.hasNext(); /**/)
        {
            Element permissionElement = (Element)pit.next();
            Permission permission = new Permission(qname);
            permission.initialise(permissionElement, nspr, permissionModel);
            permissions.add(permission);
        }
        
    }

    public Set<PermissionGroup> getPermissionGroups()
    {
        return Collections.unmodifiableSet(permissionGroups);
    }

    public Set<Permission> getPermissions()
    {
        return Collections.unmodifiableSet(permissions);
    }

    public QName getQName()
    {
        return qname;
    }

    public boolean exposeAll()
    {
        return exposeAll;
    }
    
    

}
