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
 * The interface against which permission entries are persisted
 * 
 * @author andyh
 */

public interface PermissionEntry
{
    /**
     * Get the identifier for this object.
     * 
     * @return
     */
    public long getId();
    
    /**
     * Get the containing node permission entry.
     * 
     * @return
     */
    public NodePermissionEntry getNodePermissionEntry();
    
    /**
     * Get the permission to which this entry applies.
     * 
     * @return
     */
    public PermissionReference getPermissionReference();
    
    /**
     * Get the recipient to which this entry applies.
     * 
     * @return
     */
    public Recipient getRecipient();
    
    /**
     * Is this permission allowed?
     * @return
     */
    public boolean isAllowed();
    
    /**
     * Set if this permission is allowed, otherwise it is denied.
     * 
     * @param allowed
     */
    public void setAllowed(boolean allowed);
    
    /**
     * Delete this permission entry - allows for deleting of the bidirectional relationship to the node permission entry.
     *
     */
    public void delete();
}
