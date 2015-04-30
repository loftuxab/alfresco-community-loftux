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

import java.util.List;

import javax.management.MBeanOperationInfo;

import org.alfresco.repo.forms.Field;
import org.alfresco.repo.forms.FieldDefinition;
import org.alfresco.repo.forms.PropertyFieldDefinition;
import org.springframework.extensions.surf.util.I18NUtil;

/**
 * Field object that represents the operations of an MBean.
 *
 * @author Gavin Cornwell
 */
public class MBeanOperationsField implements Field
{
    private static final String OPERATIONS_LABEL_PREFIX = "mbean.operations.";
    
    private PropertyFieldDefinition fieldDef;
    private String name;
    private String value;
    
    private MBeanOperationInfo[] operations;
    private List<String> ignoredOperations;

    public MBeanOperationsField(MBeanOperationInfo[] operations, List<String> ignoredOperations)
    {
        this.operations = operations;
        this.ignoredOperations = ignoredOperations;
        
        this.name = JMXFormProcessor.OPERATIONS_FIELD_NAME;
        
        this.fieldDef = new PropertyFieldDefinition(this.name, this.name);
        this.fieldDef.setDataKeyName(this.name);
        this.fieldDef.setLabel(this.name);
    }
    
    /**
     * Builds the value for the field, this will be a comma separated list
     * of parameter less operations the MBean has.
     * 
     * @param operations Array of MBean operation info objects
     */
    protected void buildOperationsValue()
    {
        StringBuilder builder = new StringBuilder();
        
        for (MBeanOperationInfo operation : this.operations)
        {
            // add the operation if it has no parameters and it is not
            // in the list of ignored operations
            if (operation.getSignature().length == 0 &&
                (this.ignoredOperations == null || !this.ignoredOperations.contains(operation.getName())))
            {
                builder.append(buildOperationValue(operation)).append(",");
            }
        }
        
        // remove the last comma if necessary
        if (builder.length() > 0)
        {
            builder.deleteCharAt(builder.length()-1);
            this.value = builder.toString();
        }
        else
        {
            this.value = "";
        }
    }
    
    /**
     * Builds the value to represent the given MBean operation.
     * The string takes the form name|label. If a localised name
     * can not be found the operation name is used as the label.
     * 
     * @param operation The operation to build a value for
     * @return The name|label representation of the operation.
     */
    protected String buildOperationValue(MBeanOperationInfo operation)
    {
        String opName = operation.getName();

        // attempt to find the label for the operation
        String opLabel = I18NUtil.getMessage(OPERATIONS_LABEL_PREFIX + opName);
        if (opLabel == null)
        {
            opLabel = opName;
        }
        
        return opName + "|" + opLabel;
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
        if (this.value == null)
        {
            buildOperationsValue();
        }
        
        return this.value;
    }
}
