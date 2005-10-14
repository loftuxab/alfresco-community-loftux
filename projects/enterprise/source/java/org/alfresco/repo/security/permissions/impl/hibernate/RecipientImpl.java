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

import java.util.HashSet;
import java.util.Set;

/**
 * The persisted class for recipients.
 * 
 * @author andyh
 */
public class RecipientImpl implements Recipient
{
    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = -5582068692208928127L;

    private String recipient;
    
    private Set<String> externalKeys = new HashSet<String>();

    public RecipientImpl()
    {
        super();
    }
    
    public String getRecipient()
    {
        return recipient;
    }

    public void setRecipient(String recipient)
    {
       this.recipient = recipient;
    }

    public Set<String> getExternalKeys()
    {
        return externalKeys;
    }

    // Hibernate
    /* package */ void setExternalKeys(Set<String> externalKeys)
    {
        this.externalKeys = externalKeys;
    }
    
    // Hibernate pattern
    
    @Override
    public boolean equals(Object o)
    {
        if(this == o)
        {
            return true;
        }
        if(!(o instanceof Recipient))
        {
            return false;
        }
        Recipient other = (Recipient)o;
        return this.getRecipient().equals(other.getRecipient());
    }

    @Override
    public int hashCode()
    {
        return getRecipient().hashCode();
    }
    
    
}
