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
import java.util.Set;

import org.alfresco.config.ConfigElement;
import org.alfresco.config.ConfigException;
import org.alfresco.config.element.ConfigElementAdapter;

public class ConstraintHandlersConfigElement extends ConfigElementAdapter
{
    private static final long serialVersionUID = 2266042608444782740L;
    public static final String CONFIG_ELEMENT_ID = "constraint-handlers";
    
    /*
     * LinkedHashMap is an extension of HashMap which maintains insertion order of the
     * entries. We are interested here in the insertion order of key/value pairs and
     * later in this class, we are traversing the keys with the assumption that they
     * will be iterated in insertion order.
     * Sun's javadoc is interesting here as it makes it clear that the <i>values</i> will have
     * their order maintained.
     * TODO The insertion order of keys is observed to be maintained in Apple's 1.5 VM,
     * but it's not clear that this is a contractual obligation w.r.t. the class API.
     */
    private Map<String, ItemDefinition> items = new LinkedHashMap<String, ItemDefinition>();

    /**
     * This constructor creates an instance with the default name.
     */
    public ConstraintHandlersConfigElement()
    {
        super(CONFIG_ELEMENT_ID);
    }

    /**
     * This constructor creates an instance with the specified name.
     * 
     * @param name the name for the ConfigElement.
     */
    public ConstraintHandlersConfigElement(String name)
    {
        super(name);
    }

    /**
     * @see org.alfresco.config.ConfigElement#getChildren()
     */
    @Override
    public List<ConfigElement> getChildren()
    {
        throw new ConfigException(
                "Reading the constraint-handlers config via the generic interfaces is not supported");
    }

    /**
     * @see org.alfresco.config.ConfigElement#combine(org.alfresco.config.ConfigElement)
     */
    @Override
    public ConfigElement combine(ConfigElement configElement)
    {
        // There is an assumption here that it is only like-with-like combinations
        // that are allowed. i.e. Only an instance of a ConstraintHandlersConfigElement
        // can be combined with this.
        ConstraintHandlersConfigElement otherCHCElement = (ConstraintHandlersConfigElement) configElement;

        ConstraintHandlersConfigElement result = new ConstraintHandlersConfigElement();

        for (String nextType : items.keySet())
        {
            String nextValidationHandler = getValidationHandlerFor(nextType);
            String nextMessage = getMessageFor(nextType);
            String nextMessageId = getMessageIdFor(nextType);
            result.addDataMapping(nextType, nextValidationHandler, nextMessage,
                    nextMessageId);
        }

        for (String nextType : otherCHCElement.items.keySet())
        {
            String nextValidationHandler = otherCHCElement
                    .getValidationHandlerFor(nextType);
            String nextMessage = otherCHCElement.getMessageFor(nextType);
            String nextMessageId = otherCHCElement.getMessageIdFor(nextType);
            result.addDataMapping(nextType, nextValidationHandler, nextMessage,
                    nextMessageId);
        }

        return result;
    }

    /* package */void addDataMapping(String type, String validationHandler,
            String message, String messageID)
    {
    	if (message == null)
    	{
    		message = "";
    	}
    	if (messageID == null)
    	{
    		messageID = "";
    	}
    	
        items.put(type, new ItemDefinition(type, validationHandler, message, messageID));
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return items.hashCode();
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object otherObj)
    {
        if (otherObj == null || !otherObj.getClass().equals(this.getClass()))
        {
            return false;
        }
        ConstraintHandlersConfigElement otherCHCE = (ConstraintHandlersConfigElement) otherObj;
        return this.items.equals(otherCHCE.items);
    }

    /**
     * This method returns the registered constraint types.
     * @return an unmodifiable List of the constraint types.
     */
    List<String> getConstraintTypes()
    {
        Set<String> result = items.keySet();
        // See the comment above on ordering in LinkedHashMaps' keys.
        List<String> listResult = new ArrayList<String>(result);
        return Collections.unmodifiableList(listResult);
    }

    /**
     * This method returns a String identifier for the validation-handler
     * associated with the specified constraint type.
     * 
     * @param type the constraint type.
     * @return a String identifier for the validation-handler.
     */
    String getValidationHandlerFor(String type)
    {
        return items.get(type).getValidationHandler();
    }

    /**
     * This method returns a message String  associated with the specified constraint
     * type.
     * 
     * @param type the constraint type.
     * @return the message String for the validation-handler.
     */
    String getMessageFor(String type)
    {
    	return items.get(type).getMessage();
    }

    /**
     * This method returns a message-id String  associated with the specified constraint
     * type.
     * 
     * @param type the constraint type.
     * @return the message-id String for the validation-handler.
     */
    String getMessageIdFor(String type)
    {
    	return items.get(type).getMessageId();
    }

    public List<String> getItemNames()
    {
    	return this.getConstraintTypes();
    }
    
    public Map<String, ItemDefinition> getItems()
    {
    	return Collections.unmodifiableMap(items);
    }
    
    public class ItemDefinition
    {
    	private final String type;
    	private final String validationHandler;
    	private final String message;
    	private final String messageId;
    	
    	public ItemDefinition(String type, String validationHandler, String msg, String msgId)
    	{
            this.type              = type              == null ? "" : type;
            this.validationHandler = validationHandler == null ? "" : validationHandler;
            this.message           = msg               == null ? "" : msg;
            this.messageId         = msgId             == null ? "" : msgId;
    	}
    	
		public String getType() {
			return type;
		}
		public String getValidationHandler() {
			return validationHandler;
		}
		public String getMessage() {
			return message;
		}
		public String getMessageId() {
			return messageId;
		}

        @Override
        public boolean equals(Object otherObj)
        {
            if (otherObj == this)
            {
                return true;
            }
            else if (otherObj == null || !otherObj.getClass().equals(this.getClass()))
            {
                return false;
            }
            ItemDefinition otherItem = (ItemDefinition)otherObj;
            return otherItem.type.equals(this.type) &&
                otherItem.validationHandler.equals(this.validationHandler) &&
                otherItem.message.equals(this.message) &&
                otherItem.messageId.equals(this.messageId);
        }

        @Override
        public int hashCode()
        {
            return type.hashCode()
                    + 3 * validationHandler.hashCode()
                    + 7 * message.hashCode()
                    + 13 * messageId.hashCode();
        }

        @Override
        public String toString()
        {
            StringBuilder result = new StringBuilder();
            result.append(type).append(", ")
                .append(validationHandler).append(", ")
                .append(message).append(", ")
                .append(messageId);
            return result.toString();
        }
    }
}
