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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
 
/**
 * This class represents a single constraint-message configuration item.
 * 
 * @author Neil McErlean.
 */
public class ConstraintMessage
{
    private final String type;
    private String message;
    private String messageId;
    private static Log logger = LogFactory.getLog(ConstraintMessage.class);

    /**
     * Constructs a ConstraintMessage object with the specified type, message and
     * message-id. It is not expected that message and message-id will both be set.
     * Instead it is expected that either message or message-id will be set.
     * 
     * @param type the type of the constraint-message.
     * @param message the message of the constraint-message.
     * @param message-id the message-id of the constraint-message.
     */
    public ConstraintMessage(String type, String message, String messageId)
    {
    	if (type == null)
    	{
    		if (logger.isWarnEnabled())
    		{
	    		StringBuilder warning = new StringBuilder();
	    		warning.append("Constraint-message with type=null was specified.");
	    		logger.warn(warning.toString());
	    		type = "";
    		}
    	}
    	this.type = type;
    	
    	this.message = message;
    	this.messageId = messageId;
    }

    /**
     * Gets the type of this ConstraintMessage.
     * @return the type.
     */
    public String getType()
    {
        return type;
    }

    /**
     * Gets the message of this ConstraintMessage.
     * @return the message.
     */
    public String getMessage()
    {
        return message;
    }
    
    /* default */ void setMessage(String newValue)
    {
    	this.message = newValue;
    }

    /**
     * Gets the messageId of this ConstraintMessage.
     * @return the messageId.
     */
    public String getMessageId()
    {
        return messageId;
    }
    
    /* default */ void setMessageId(String newValue)
    {
    	this.messageId = newValue;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        result.append(type).append(":").append(message)
            .append("/").append(messageId);
        return result.toString();
    }
}