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
    public static final String PROP_GROUP_TYPE = "groupType";
    public static final String PROP_DESCRIPTION = "description";
    public static final String PROP_NAME = "name";
    public static final String PROP_GROUP_ID = "groupId";
    
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
    public String[] getPropertyNames()
    {
        return new String[] { PROP_GROUP_ID, PROP_NAME, PROP_DESCRIPTION, PROP_GROUP_TYPE }; 
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
        return getStringProperty(PROP_GROUP_ID);
    }
    
    /**
     * Sets the group id.
     * 
     * @param groupId the new group id
     */
    public void setGroupId(String groupId)
    {
        setProperty(PROP_GROUP_ID, groupId);
    }
    
    /**
     * Gets the name.
     * 
     * @return the name
     */
    public String getName()
    {
        return getStringProperty(PROP_NAME);
    }
    
    /**
     * Sets the name.
     * 
     * @param name the new name
     */
    public void setName(String name)
    {
        setProperty(PROP_NAME, name);
    }
        
    /**
     * Gets the description.
     * 
     * @return the description
     */
    public String getDescription()
    {
        return getStringProperty(PROP_DESCRIPTION);
    }
    
    /**
     * Sets the description.
     * 
     * @param description the new description
     */
    public void setDescription(String description)
    {
        setProperty(PROP_DESCRIPTION, description);
    }
    
    /**
     * Gets the group type.
     * 
     * @return the group type
     */
    public String getGroupType()
    {
        return getStringProperty(PROP_GROUP_TYPE);
    }
    
    /**
     * Sets the group type.
     * 
     * @param groupType the new group type
     */
    public void setGroupType(String groupType)
    {
        setProperty(PROP_GROUP_TYPE, groupType);
    }
}
