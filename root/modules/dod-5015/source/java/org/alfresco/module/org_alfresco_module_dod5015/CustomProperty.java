/*
 * Copyright (C) 2005-2009 Alfresco Software Limited.
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
 * FLOSS exception.  You should have received a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.module.org_alfresco_module_dod5015;

import org.alfresco.repo.dictionary.M2Property;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.namespace.QName;

/**
 * This simple data class is a convenient struct for custom property metadata.
 * 
 * @author Neil McErlean
 */
public final class CustomProperty
{

    private String defaultValue;
    private String description;
    private boolean mandatory;
    private boolean multiValued;
    private String name;
    private boolean protected_;
    private String title;
    private QName type;

    public CustomProperty(String name)
    {
        this.name = name;
    }
    
    public static CustomProperty createInstance(M2Property m2Property, ServiceRegistry serviceRegistry)
    {
        CustomProperty result = new CustomProperty(m2Property.getName());
        result.setDefaultValue(m2Property.getDefaultValue());
        result.setDescription(m2Property.getDescription());
        result.setMandatory(m2Property.isMandatory());
        result.setProtected(m2Property.isProtected());
        result.setTitle(m2Property.getTitle());
        QName typeQName = QName.createQName(m2Property.getType(), serviceRegistry.getNamespaceService());
        result.setType(typeQName);
        
        return result;
    }
    
    public String getDefaultValue()
    {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue)
    {
        this.defaultValue = defaultValue;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public boolean isMandatory()
    {
        return mandatory;
    }

    public void setMandatory(boolean mandatory)
    {
        this.mandatory = mandatory;
    }

    public boolean isMultiValued()
    {
        return multiValued;
    }

    public void setMultiValued(boolean multiValued)
    {
        this.multiValued = multiValued;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public boolean isProtected()
    {
        return protected_;
    }

    public void setProtected(boolean protected_)
    {
        this.protected_ = protected_;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public QName getType()
    {
        return type;
    }

    public void setType(QName type)
    {
        this.type = type;
    }
    
    @Override
    public String toString()
    {
    	return this.getName();
    }
}
