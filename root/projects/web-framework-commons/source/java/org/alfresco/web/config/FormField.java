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
 * FLOSS exception.  You should have received a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.web.config;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.alfresco.config.ConfigException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class represents a &lt;field&gt; element within the &lt;appearance&gt; tag in
 * the config xml.
 * 
 * @author Neil McErlean.
 */
public class FormField
{
    private static Log logger = LogFactory.getLog(FormField.class);
    
    private final String id;
    private final Map<String, String> attributes;
    private String template;
    private final Map<String, String> controlParams = new LinkedHashMap<String, String>();
    private String constraintType;
    private String constraintMessage;
    private String constraintMessageId;
    
    /**
     * 
     * @param id the id of the field. This cannot be null.
     * @param attributes
     * @throws ConfigException if id is null.
     */
    public FormField(String id, Map<String, String> attributes)
    {
        if (id == null)
        {
            String msg = "Illegal null field id";
            if (logger.isWarnEnabled())
            {
                logger.warn(msg);
            }
            throw new ConfigException(msg);
        }
        this.id = id;
        if (attributes == null)
        {
            attributes = Collections.emptyMap();
        }
        this.attributes = attributes;
    }

    void setTemplate(String template)
    {
        this.template = template;
    }
    void setConstraintType(String constraintType)
    {
        this.constraintType = constraintType;
    }
    void setConstraintMessage(String constraintMessage)
    {
        this.constraintMessage = constraintMessage;
    }
    void setConstraintMessageId(String constraintMessageId)
    {
        this.constraintMessageId = constraintMessageId;
    }
    void addControlParam(String name, String value)
    {
        this.controlParams.put(name, value);
    }
    public Map<String, String> getAttributes()
    {
        return Collections.unmodifiableMap(attributes);
    }
    public String getId()
    {
        return this.id;
    }
    public String getTemplate()
    {
        return template;
    }
    public Map<String, String> getControlParams()
    {
        return Collections.unmodifiableMap(controlParams);
    }
    public String getConstraintType()
    {
        return constraintType;
    }
    public String getConstraintMessage()
    {
        return constraintMessage;
    }
    public String getConstraintMessageId()
    {
        return constraintMessageId;
    }
    
    public FormField combine(FormField otherField)
    {
        if (logger.isDebugEnabled())
        {
            StringBuilder msg = new StringBuilder();
            msg.append("Combining instances of ").append(this);
            logger.debug(msg.toString());
        }
        
        // It doesn't make sense to combine two fields with different IDs.
        if (!this.id.equals(otherField.id) && logger.isWarnEnabled())
        {
            StringBuilder msg = new StringBuilder();
            msg.append("Illegal attempt to combine two FormFields with different IDs: ")
                .append(this.id)
                .append(", ")
                .append(otherField.id);
            logger.warn(msg.toString());
            return this;
        }

        logDebugInformation(otherField);
        
        // Combine the xml attributes of the <field> tag.
        Map<String, String> combinedAttributes = new LinkedHashMap<String, String>();
        combinedAttributes.putAll(this.attributes);
        combinedAttributes.putAll(otherField.attributes);

        
        FormField result = new FormField(this.id, combinedAttributes);
        
        // Combine templates
        result.setTemplate(this.template);
        if (otherField.template != null)
        {
            result.setTemplate(otherField.template);
        }

        // Combine control-params
        for (String nextCPName : this.getControlParams().keySet())
        {
            // If any control-param appears in the otherField, we should use that one.
            // We are choosing to put name-values from the other object in this loop
            // as we want to maintain the ordering of control params.
            if (otherField.controlParams.containsKey(nextCPName))
            {
                result.addControlParam(nextCPName, otherField.controlParams.get(nextCPName));
            }
            else
            {
                result.addControlParam(nextCPName, this.controlParams.get(nextCPName));
            }
        }
        // Any additional control-params are appended to the map.
        for (String nextCPName : otherField.getControlParams().keySet())
        {
            if (!this.controlParams.containsKey(nextCPName))
            {
                result.addControlParam(nextCPName, otherField.controlParams.get(nextCPName));
            }
        }
        
        // Combine constraint-message data
        result.setConstraintType(this.constraintType);
        if (otherField.constraintType != null)
        {
            result.setConstraintType(otherField.constraintType);
        }
        result.setConstraintMessage(this.constraintMessage);
        if (otherField.constraintMessage != null)
        {
            result.setConstraintMessage(otherField.constraintMessage);
        }
        result.setConstraintMessageId(this.constraintMessageId);
        if (otherField.constraintMessageId != null)
        {
            result.setConstraintMessageId(otherField.constraintMessageId);
        }

        return result;
    }

    @Override
    public boolean equals(Object otherObj)
    {
        if (otherObj == null || !otherObj.getClass().equals(this.getClass()))
        {
            return false;
        }
        FormField otherFormField = (FormField)otherObj;
        
        return this.id.equals(otherFormField.id) &&
            this.attributes.equals(otherFormField.attributes) &&
            this.template == null ? otherFormField.template == null : this.template.equals(otherFormField.template) &&
            this.controlParams == null ? otherFormField.controlParams == null : this.controlParams.equals(otherFormField.controlParams) &&
            this.constraintType == null ? otherFormField.constraintType == null : this.constraintType.equals(otherFormField.constraintType) &&
            this.constraintMessage == null ? otherFormField.constraintMessage == null : this.constraintMessage.equals(otherFormField.constraintMessage) &&
            this.constraintMessageId == null ? otherFormField.constraintMessageId == null : this.constraintMessageId.equals(otherFormField.constraintMessageId);
    }
    
    @Override
    public int hashCode()
    {
        int component1 = id.hashCode();
        int component2 = attributes.hashCode();
        int component3 = template == null ? 0 : template.hashCode();
        int component4 = controlParams == null ? 0 : controlParams.hashCode();
        int component5 = constraintType == null ? 0 : constraintType.hashCode();
        int component6 = constraintMessage == null ? 0 : constraintMessage.hashCode();
        int component7 = constraintMessageId == null ? 0 : constraintMessageId.hashCode();

        return component1
            + 3 * component2
            + 7 * component3
            + 11 * component4
            + 13 * component5
            + 17 * component6
            + 19 * component7;
    }
    
    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        result.append("FormField:")
            .append(this.id);
        return result.toString();
    }

    private void logDebugInformation(FormField otherField)
    {
        if (!logger.isDebugEnabled())
        {
            return;
        }

        StringBuilder msg = new StringBuilder();
        msg.append("Combining xml attributes ")
            .append(attributes.keySet())
            .append(" and ")
            .append(otherField.attributes.keySet());
        logger.debug(msg.toString());

        msg = new StringBuilder();
        msg.append("Combining templates ")
            .append(template)
            .append(" and ")
            .append(otherField.template);
        logger.debug(msg.toString());

        msg = new StringBuilder();
        msg.append("Combining control-params ")
            .append(controlParams.keySet())
            .append(" and ")
            .append(otherField.controlParams.keySet());
        logger.debug(msg.toString());
        msg = new StringBuilder();

        msg.append("Combining constraint type,message,message-id '")
            .append(constraintType).append(",")
            .append(constraintMessage).append(",")
            .append(constraintMessageId).append(",")
            .append("' and '")
            .append(otherField.constraintType).append(",")
            .append(otherField.constraintMessage).append(",")
            .append(otherField.constraintMessageId).append("'");
        logger.debug(msg.toString());
    }
}