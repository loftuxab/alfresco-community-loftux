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
 * The Class AbstractGroup.
 * 
 * @author muzquiano
 */
public abstract class AbstractGroup extends AbstractEntity
{
    public static String ENTITY_TYPE = "group";
    
    /**
     * Instantiates a new abstract group.
     * 
     * @param groupId the group id
     */
    public AbstractGroup(String groupId)
    {
        setGroupId(groupId);
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
        return new String[] { "groupId", "name", "description", "groupType" }; 
    }    
    
    /* (non-Javadoc)
     * @see org.alfresco.extranet.AbstractEntity#getEntityId()
     */
    public String getEntityId()
    {
        return getGroupId();
    }    

    /**
     * Gets the group id.
     * 
     * @return the group id
     */
    public String getGroupId()
    {
        return getStringProperty("groupId");
    }
    
    /**
     * Sets the group id.
     * 
     * @param groupId the new group id
     */
    public void setGroupId(String groupId)
    {
        setProperty("groupId", groupId);
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
     * Gets the group type.
     * 
     * @return the group type
     */
    public String getGroupType()
    {
        return getStringProperty("groupType");
    }
    
    /**
     * Sets the group type.
     * 
     * @param groupType the new group type
     */
    public void setGroupType(String groupType)
    {
        setProperty("groupType", groupType);
    }
}
