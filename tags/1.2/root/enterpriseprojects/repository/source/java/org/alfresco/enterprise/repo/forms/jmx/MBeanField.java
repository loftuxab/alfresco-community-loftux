/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

package org.alfresco.enterprise.repo.forms.jmx;

import javax.management.MBeanAttributeInfo;

import org.alfresco.repo.forms.Field;
import org.alfresco.repo.forms.FieldDefinition;
import org.alfresco.repo.forms.PropertyFieldDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;

public class MBeanField implements Field
{
    private PropertyFieldDefinition fieldDef;
    private String name;
    private Object value;

    public MBeanField(MBeanAttributeInfo attribute, Object value)
    {
        this.name = attribute.getName();
        this.value = value.toString();
        
        String type = attribute.getType();
        if (type.equals("java.lang.Boolean"))
        {
            this.fieldDef = new PropertyFieldDefinition(this.name, DataTypeDefinition.BOOLEAN.getLocalName());
        }
        else
        {    
            this.fieldDef = new PropertyFieldDefinition(this.name, DataTypeDefinition.TEXT.getLocalName());
        }
        
        this.fieldDef.setLabel(this.name);
        this.fieldDef.setDataKeyName(this.name);
        this.fieldDef.setDescription(attribute.getDescription());
        this.fieldDef.setProtectedField(!attribute.isWritable());
    }
    
    @Override
    public FieldDefinition getFieldDefinition()
    {
        return this.fieldDef;
    }

    @Override
    public String getFieldName()
    {
        return this.name;
    }

    @Override
    public Object getValue()
    {
        return this.value;
    }
}
