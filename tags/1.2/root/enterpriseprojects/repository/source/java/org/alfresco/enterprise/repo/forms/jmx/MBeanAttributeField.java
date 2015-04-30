/*
 * Copyright (C) 2005-2011 Alfresco Software Limited.
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.MBeanAttributeInfo;

import org.alfresco.repo.forms.Field;
import org.alfresco.repo.forms.FieldDefinition;
import org.alfresco.repo.forms.PropertyFieldDefinition;
import org.alfresco.repo.forms.PropertyFieldDefinition.FieldConstraint;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;

public class MBeanAttributeField implements Field
{
    private PropertyFieldDefinition fieldDef;
    private String name;
    private Object value;

    public MBeanAttributeField(MBeanAttributeInfo attribute, Object value)
    {
        this.name = attribute.getName();
        
        if (value != null)
        {
            this.value = value.toString();
        }
        
        String type = attribute.getType();
        if (type.equals("java.lang.Boolean"))
        {
            this.fieldDef = new PropertyFieldDefinition(this.name, DataTypeDefinition.BOOLEAN.getLocalName());
        }
        else if (type.equals("java.util.Date"))
        {
            this.fieldDef = new PropertyFieldDefinition(this.name, DataTypeDefinition.DATETIME.getLocalName());
            this.value = value;
        }
        else
        {    
            this.fieldDef = new PropertyFieldDefinition(this.name, DataTypeDefinition.TEXT.getLocalName());
        }
        
        this.fieldDef.setLabel(this.name);
        this.fieldDef.setDataKeyName(this.name);
        this.fieldDef.setProtectedField(!attribute.isWritable());
        
        // process constraints for well known attributes
        processConstraints(attribute, this.fieldDef);
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
    
    /**
     * There are certain fields that require constraints which are added here, once we have
     * the ability to add these via configuration on the client side 
     * (http://issues.alfresco.com/jira/browse/ALF-7937) this method will be removed.
     * 
     * @param attribute The MBean attribute being created
     * @param fieldDef The field representing the attribute
     */
    private void processConstraints(MBeanAttributeInfo attribute, PropertyFieldDefinition fieldDef)
    {
        if (attribute.getName().equals("googledocs.username"))
        {
            // the Google username has to be a valid username so add a regex constraint
            Map<String, Object> params = new HashMap<String, Object>(2);
            params.put("expression", "(.+@.+\\.[a-zA-Z0-9]{2,6})");
            params.put("requiresMatch", true);
            FieldConstraint fieldConstraint = new FieldConstraint("REGEX", params);
            List<FieldConstraint> fieldConstraints = new ArrayList<FieldConstraint>(1);
            fieldConstraints.add(fieldConstraint);
            fieldDef.setConstraints(fieldConstraints);
        }
    }
}
