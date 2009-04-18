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
    private static final String ATTR_LABEL_ID = "label-id";
    private static final String ATTR_LABEL = "label";
    private static final String ATTR_DESCRIPTION_ID = "description-id";
    private static final String ATTR_DESCRIPTION = "description";
    private static final String ATTR_HELP_TEXT_ID = "help-id";
	private static final String ATTR_HELP_TEXT = "help";
	private static final String ATTR_SET = "set";
    private static final String ATTR_READ_ONLY = "read-only";

	private static Log logger = LogFactory.getLog(FormField.class);
    
    private final String id;
    private final Map<String, String> attributes;
    private String template;
    private final List<ControlParam> controlParams = new ArrayList<ControlParam>();
    private final List<ConstraintHandlerDefinition> constraintDefns = new ArrayList<ConstraintHandlerDefinition>();
    
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
    void addConstraintDefinition(String type, String message, String messageId,
    		String validationHandler, String event)
    {
    	for (ConstraintHandlerDefinition constraint : this.constraintDefns)
    	{
    		if (constraint.getType().equals(type))
    		{
    			// The value for this constraint-message is being overridden.
    			constraint.setMessage(message);
    			constraint.setMessageId(messageId);
    			constraint.setValidationHandler(validationHandler);
    			constraint.setEvent(event);
    			return;
    		}
    	}
    	this.constraintDefns.add(new ConstraintHandlerDefinition(type, validationHandler, message, messageId, event));
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
    
    public String getDescription()
    {
        return attributes.get(ATTR_DESCRIPTION);
    }
    
    public String getDescriptionId()
    {
        return attributes.get(ATTR_DESCRIPTION_ID);
    }
    
    public boolean isReadOnly()
    {
        Object disabledValue = attributes.get(ATTR_READ_ONLY);
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
    
    public Map<String, ConstraintHandlerDefinition> getConstraintDefinitionMap()
    {
        Map<String, ConstraintHandlerDefinition> defns = new LinkedHashMap<String, ConstraintHandlerDefinition>(4);
        
        for (ConstraintHandlerDefinition defn : this.constraintDefns)
        {
            defns.put(defn.getType(), defn);
        }
        
        return Collections.unmodifiableMap(defns);
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
        for (ConstraintHandlerDefinition constraint : this.constraintDefns)
        {
        	result.addConstraintDefinition(constraint.getType(), constraint.getMessage(),
        			constraint.getMessageId(), constraint.getValidationHandler(), constraint.getEvent());
        }
        for (ConstraintHandlerDefinition constraint : otherField.constraintDefns)
        {
        	result.addConstraintDefinition(constraint.getType(), constraint.getMessage(),
        			constraint.getMessageId(), constraint.getValidationHandler(), constraint.getEvent());
        }

        return result;
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

        msg.append("Combining constraint-definitions")
            .append(constraintDefns)
            .append(" and ")
            .append(otherField.constraintDefns);
        logger.debug(msg.toString());
    }
}