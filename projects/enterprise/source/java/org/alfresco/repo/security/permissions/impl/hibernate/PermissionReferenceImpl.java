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


/**
 * The persisted class for permission references.
 * 
 * @author andyh
 */
public class PermissionReferenceImpl implements PermissionReference
{   
    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = -6352566900815035461L;

    private String typeUri;
    
    private String typeName;
    
    private String name;

    public PermissionReferenceImpl()
    {
        super();
    }
    
    public String getTypeUri()
    {
        return typeUri;
    }

    public void setTypeUri(String typeUri)
    {
       this.typeUri = typeUri;
    }
    
    public String getTypeName()
    {
        return typeName;
    }

    public void setTypeName(String typeName)
    {
       this.typeName = typeName;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    // Hibernate pattern
    
    @Override
    public boolean equals(Object o)
    {
        if(this == o)
        {
            return true;
        }
        if(!(o instanceof PermissionReference))
        {
            return false;
        }
        PermissionReference other = (PermissionReference)o;
        return this.getTypeUri().equals(other.getTypeUri()) && this.getTypeName().equals(other.getTypeName()) && this.getName().equals(other.getName()); 
    }

    @Override
    public int hashCode()
    {
        return ((typeUri.hashCode() * 37) + typeName.hashCode() ) * 37 + name.hashCode();
    }
    
    

}
