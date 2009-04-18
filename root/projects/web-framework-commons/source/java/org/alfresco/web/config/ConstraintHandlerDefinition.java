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

/**
 * This struct class represents the definition of a constraint-handler, as may be
 * used as a default, applied to a field or applied to a form.
 * 
 * @author Neil McErlean
 */
public class ConstraintHandlerDefinition
{
	private String type;
	private String validationHandler;
	private String message;
	private String messageId;
	private String event;
	
	public ConstraintHandlerDefinition(String type, String validationHandler, 
	                      String msg, String msgId, String event)
	{
        this.type              = type == null ? "" : type;
        this.validationHandler = validationHandler;
        this.message           = msg;
        this.messageId         = msgId;
        this.event             = event;
	}
	
	public String getType() 
	{
		return type;
	}
	
	public String getValidationHandler() 
	{
		return validationHandler;
	}
	
	public String getMessage() 
	{
		return message;
	}
	
	public String getMessageId() 
	{
		return messageId;
	}
	
	public String getEvent()
	{
	    return event;
	}

	void setValidationHandler(String validationHandler) {
		this.validationHandler = validationHandler;
	}

	void setMessage(String message) {
		this.message = message;
	}

	void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	void setEvent(String event) {
		this.event = event;
	}

    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        result.append(type).append(", ")
            .append(validationHandler).append(", ")
            .append(message).append(", ")
            .append(messageId).append(", ")
            .append(event);
        return result.toString();
    }
}