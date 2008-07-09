/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.extranet;

/**
 * The Class AbstractCompany.
 * 
 * @author muzquiano
 */
public abstract class AbstractCompany extends AbstractEntity
{
    public static String ENTITY_TYPE = "company";
    
    /**
     * Instantiates a new abstract company.
     * 
     * @param companyId the company id
     */
    public AbstractCompany(String companyId)
    {
        setCompanyId(companyId);        
    }

    /* (non-Javadoc)
     * @see org.alfresco.extranet.AbstractEntity#getEntityType()
     */
    public String getEntityType()
    {
        return AbstractCompany.ENTITY_TYPE;
    }
    
    /**
     * Gets the property names.
     * 
     * @return the property names
     */
    public static String[] getPropertyNames()
    {
        return new String[] { "companyId", "name", "description", "oid" }; 
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.extranet.AbstractEntity#getEntityId()
     */
    public String getEntityId()
    {
        return getCompanyId();
    }

    /**
     * Gets the company id.
     * 
     * @return the company id
     */
    public String getCompanyId()
    {
        return getStringProperty("companyId");
    }
    
    /**
     * Sets the company id.
     * 
     * @param companyId the new company id
     */
    public void setCompanyId(String companyId)
    {
        setProperty("companyId", companyId);
    }
    
    /**
     * Gets the name.
     * 
     * @return the name
     */
    public String getName()
    {
        return getStringProperty("name");
    }
    
    /**
     * Sets the name.
     * 
     * @param name the new name
     */
    public void setName(String name)
    {
        setProperty("name", name);
    }
        
    /**
     * Gets the description.
     * 
     * @return the description
     */
    public String getDescription()
    {
        return getStringProperty("description");
    }
    
    /**
     * Sets the description.
     * 
     * @param description the new description
     */
    public void setDescription(String description)
    {
        setProperty("description", description);
    }
    
    /**
     * Gets the oid.
     * 
     * @return the oid
     */
    public String getOid()
    {
        return getStringProperty("oid");
    }
    
    /**
     * Sets the oid.
     * 
     * @param oid the new oid
     */
    public void setOid(String oid)
    {
        setProperty("oid", oid);
    }
}
