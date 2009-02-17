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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
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
    private static final String ATTR_HELP_TEXT_ID = "help-text-id";
	private static final String ATTR_HELP_TEXT = "help-text";
	private static final String ATTR_SET = "set";
	private static final String ATTR_DISABLED = "disabled";
	private static final String ATTR_LABEL_ID = "label-id";
	private static final String ATTR_LABEL = "label";

	private static Log logger = LogFactory.getLog(FormField.class);
    
    private final String id;
    private final Map<String, String> attributes;
    private String template;
    private final List<ControlParam> controlParams = new ArrayList<ControlParam>();
    private final List<ConstraintMessage> constraintMessages = new ArrayList<ConstraintMessage>();
    
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
    void addControlParam(String name, String value)
    {
    	for (ControlParam cp : this.controlParams)
    	{
    		if (cp.getName().equals(name))
    		{
    			// The value for this control-param is being overridden.
    			cp.setValue(value);
    			return;
    		}
    	}
    	this.controlParams.add(new ControlParam(name, value));
    }
    void addConstraintMessage(String type, String message, String messageId)
    {
    	for (ConstraintMessage cm : this.constraintMessages)
    	{
    		if (cm.getType().equals(type))
    		{
    			// The value for this constraint-message is being overridden.
    			cm.setMessage(message);
    			cm.setMessageId(messageId);
    			return;
    		}
    	}
    	this.constraintMessages.add(new ConstraintMessage(type, message, messageId));
    }
    public Map<String, String> getAttributes()
    {
        return Collections.unmodifiableMap(attributes);
    }
    
    public String getId()
    {
        return this.id;
    }
    
    // The following are convenience accessor methods for certain known attributes.
    public String getLabel()
    {
    	return attributes.get(ATTR_LABEL);
    }
    public String getLabelId()
    {
    	return attributes.get(ATTR_LABEL_ID);
    }
    public boolean isDisabled()
    {
    	Object disabledValue = attributes.get(ATTR_DISABLED);
    	return disabledValue instanceof String
    	    && "true".equalsIgnoreCase((String)disabledValue);
    }
    public String getSet()
    {
    	return attributes.get(ATTR_SET);
    }
    public String getHelpText()
    {
    	return attributes.get(ATTR_HELP_TEXT);
    }
    public String getHelpTextId()
    {
    	return attributes.get(ATTR_HELP_TEXT_ID);
    }
    // End of convenience accessor methods.
    
    public String getTemplate()
    {
        return template;
    }
    public List<ControlParam> getControlParams()
    {
    	return Collections.unmodifiableList(this.controlParams);
    }
    public List<ConstraintMessage> getConstraintMessages()
    {
    	return Collections.unmodifiableList(this.constraintMessages);
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
        for (ControlParam cp : this.controlParams)
        {
        	result.addControlParam(cp.getName(), cp.getValue());
        }
        for (ControlParam cp : otherField.controlParams)
        {
        	result.addControlParam(cp.getName(), cp.getValue());
        }
        
        // Combine constraint-message data
        for (ConstraintMessage cp : this.constraintMessages)
        {
        	result.addConstraintMessage(cp.getType(), cp.getMessage(), cp.getMessageId());
        }
        for (ConstraintMessage cp : otherField.constraintMessages)
        {
        	result.addConstraintMessage(cp.getType(), cp.getMessage(), cp.getMessageId());
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
            this.controlParams.equals(otherFormField.controlParams) &&
            this.constraintMessages.equals(otherFormField.constraintMessages);
    }
    
    @Override
    public int hashCode()
    {
        int component1 = id.hashCode();
        int component2 = attributes.hashCode();
        int component3 = template == null ? 0 : template.hashCode();
        int component4 = controlParams == null ? 0 : controlParams.hashCode();
        int component5 = constraintMessages == null ? 0 : constraintMessages.hashCode();

        return component1
            + 3 * component2
            + 7 * component3
            + 11 * component4
            + 13 * component5;
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
            .append(controlParams)
            .append(" and ")
            .append(otherField.controlParams);
        logger.debug(msg.toString());
        msg = new StringBuilder();

        msg.append("Combining constraint-messages")
            .append(constraintMessages)
            .append(" and ")
            .append(otherField.constraintMessages);
        logger.debug(msg.toString());
    }
}