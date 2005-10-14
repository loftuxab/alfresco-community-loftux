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

import org.alfresco.util.EqualsHelper;

/**
 * Persisted permission entries
 * 
 * @author andyh
 */
public class PermissionEntryImpl implements PermissionEntry
{
    /**
     * The object id
     */
    private long id;
    
    /**
     * The container of this permissions
     */
    private NodePermissionEntry nodePermissionEntry;

    /**
     * The permission to which this applies
     * (non null - all is a special string)
     */
    private PermissionReference permissionReference;

    /**
     * The recipient to which this applies
     * (non null - all is a special string)
     */
    private Recipient recipient;

    /**
     * Is this permission allowed?
     */
    private boolean allowed;

    public PermissionEntryImpl()
    {
        super();
    }
    
    public long getId()
    {
        return id;
    }
    
    // Hibernate
    
    /* package */ void setId(long id)
    {
        this.id = id;
    }

    public NodePermissionEntry getNodePermissionEntry()
    {
        return nodePermissionEntry;
    }

    private void setNodePermissionEntry(NodePermissionEntry nodePermissionEntry)
    {
        this.nodePermissionEntry = nodePermissionEntry;
    }

    public PermissionReference getPermissionReference()
    {
        return permissionReference;
    }

    private void setPermissionReference(PermissionReference permissionReference)
    {
        this.permissionReference = permissionReference;
    }

    public Recipient getRecipient()
    {
        return recipient;
    }

    private void setRecipient(Recipient recipient)
    {
        this.recipient = recipient;
    }

    public boolean isAllowed()
    {
        return allowed;
    }

    public void setAllowed(boolean allowed)
    {
        this.allowed = allowed;
    }


    /**
     * Factory method to create an entry and wire it in to the contained nodePermissionEntry
     * 
     * @param nodePermissionEntry
     * @param permissionReference
     * @param recipient
     * @param allowed
     * @return
     */
    public static PermissionEntryImpl create(NodePermissionEntry nodePermissionEntry, PermissionReference permissionReference, Recipient recipient, boolean allowed)
    {
        PermissionEntryImpl permissionEntry = new PermissionEntryImpl();
        permissionEntry.setNodePermissionEntry(nodePermissionEntry);
        permissionEntry.setPermissionReference(permissionReference);
        permissionEntry.setRecipient(recipient);
        permissionEntry.setAllowed(allowed);
        nodePermissionEntry.getPermissionEntries().add(permissionEntry);
        return permissionEntry;
    }

    /**
     * Unwire 
     */
    public void delete()
    {
        nodePermissionEntry.getPermissionEntries().remove(this);
    }
    
    //
    // Hibernate object pattern
    //

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof PermissionEntryImpl))
        {
            return false;
        }
        PermissionEntryImpl other = (PermissionEntryImpl) o;
        return EqualsHelper.nullSafeEquals(this.nodePermissionEntry,
                other.nodePermissionEntry)
                && EqualsHelper.nullSafeEquals(this.permissionReference,
                        other.permissionReference)
                && EqualsHelper.nullSafeEquals(this.recipient, other.recipient)
                && (this.allowed == other.allowed);
    }

    @Override
    public int hashCode()
    {
        int hashCode = nodePermissionEntry.hashCode();
        if (permissionReference != null)
        {
            hashCode = hashCode * 37 + permissionReference.hashCode();
        }
        if (recipient != null)
        {
            hashCode = hashCode * 37 + recipient.hashCode();
        }
        hashCode = hashCode * 37 + (allowed ? 1 : 0);
        return hashCode;
    }

}
