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

import java.io.Serializable;

/**
 * The interface against which permission references are persisted in hibernate.
 * 
 * @author andyh
 */
public interface PermissionReference extends Serializable
{
   /**
    * Get the URI for the type to which this permission applies.
    * 
    * @return
    */ 
    public String getTypeUri();
    
    /**
     * Set the URI for the type to which this permission applies.
     * 
     * @param typeUri
     */
    public void setTypeUri(String typeUri);
    
    /**
     * Get the local name of the type to which this permission applies.
     * 
     * @return
     */
    public String getTypeName();
    
    /**
     * Set the local name of the type to which this permission applies.
     * 
     * @param typeName
     */
    public void setTypeName(String typeName);
    
    /**
     * Get the name of the permission.
     * 
     * @return
     */
    public String getName();
    
    /**
     * Set the name of the permission.
     * 
     * @param name
     */
    public void setName(String name);
}
