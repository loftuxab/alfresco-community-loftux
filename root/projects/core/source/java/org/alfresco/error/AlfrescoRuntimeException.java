/*
 * Copyright (C) 2005 Alfresco, Inc.
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
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.error;

import org.alfresco.i18n.I18NUtil;

/**
 * I18n'ed runtime exception thrown by Alfresco code.
 * 
 * @author gavinc
 */
public class AlfrescoRuntimeException extends RuntimeException
{
    /**
     * Serial version UUID
     */
    private static final long serialVersionUID = 3834594313622859827L;

    /**
     * Helper factory method making use of variable argument numbers
     */
    public static AlfrescoRuntimeException create(String msgId, Object ...objects)
    {
        return new AlfrescoRuntimeException(msgId, objects);
    }

    /**
     * Helper factory method making use of variable argument numbers
     */
    public static AlfrescoRuntimeException create(Throwable cause, String msgId, Object ...objects)
    {
        return new AlfrescoRuntimeException(msgId, objects, cause);
    }
    
    /**
     * Constructor
     * 
     * @param msgId     the message id
     */
    public AlfrescoRuntimeException(String msgId)
    {
        super(resolveMessage(msgId, null));
    }
    
    /**
     * Constructor
     * 
     * @param msgId         the message id
     * @param msgParams     the message parameters
     */
    public AlfrescoRuntimeException(String msgId, Object[] msgParams)
    {
        super(resolveMessage(msgId, msgParams));
    }

    /**
     * Constructor
     * 
     * @param msgId     the message id
     * @param cause     the exception cause
     */
    public AlfrescoRuntimeException(String msgId, Throwable cause)
    {
        super(resolveMessage(msgId, null), cause);
    }
    
    /**
     * Constructor
     * 
     * @param msgId         the message id
     * @param msgParams     the message parameters
     * @param cause         the exception cause
     */
    public AlfrescoRuntimeException(String msgId, Object[] msgParams, Throwable cause)
    {
        super(resolveMessage(msgId, msgParams), cause);
    }
    
    /**
     * Resolves the message id to the localised string.
     * <p>
     * If a localised message can not be found then the message Id is
     * returned.
     * 
     * @param messageId     the message Id
     * @param params        message parameters
     * @return              the localised message (or the message id if none found)
     */
    private static String resolveMessage(String messageId, Object[] params)
    {
        String message = I18NUtil.getMessage(messageId, params);
        if (message == null)
        {
            // If a localised string cannot be found then return the messageId
            message = messageId;
        }
        return message;
    }
}
