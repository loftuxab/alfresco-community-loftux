/*
 * Copyright (C) 2005-2007 Alfresco Software Limited.
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

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.extensions.surf.util.I18NUtil;

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
     * Utility to convert a general Throwable to a RuntimeException.  No conversion is done if the
     * throwable is already a <tt>RuntimeException</tt>.
     * 
     * @see #create(Throwable, String, Object...)
     */
    public static RuntimeException makeRuntimeException(Throwable e, String msgId, Object ...objects)
    {
        if (e instanceof RuntimeException)
        {
            return (RuntimeException) e;
        }
        // Convert it
        return AlfrescoRuntimeException.create(e, msgId, objects);
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
        return buildErrorLogNumber(message);
    }
    
    /**
     * Generate an error log number - based on MMDDXXXX - where M is month,
     * D is day and X is an atomic integer count.
     * 
     * @param message       Message to prepend the error log number to 
     * 
     * @return message with error log number prefix
     */
    private static String buildErrorLogNumber(String message)
    {
        // ensure message is not null
        if (message == null)
        {
            message= "";
        }
        
        Date today = new Date();
        StringBuilder buf = new StringBuilder(message.length() + 10);
        padInt(buf, today.getMonth(), 2);
        padInt(buf, today.getDate(), 2);
        padInt(buf, errorCounter.getAndIncrement(), 4);
        buf.append(' ');
        buf.append(message);
        return buf.toString();
    }
    
    /**
     * Helper to zero pad a number to specified length 
     */
    private static void padInt(StringBuilder buffer, int value, int length)
    {
        String strValue = Integer.toString(value);
        for (int i = length - strValue.length(); i > 0; i--)
        {
            buffer.append('0');
        }
        buffer.append(strValue);
    }
    
    private static AtomicInteger errorCounter = new AtomicInteger();
}
