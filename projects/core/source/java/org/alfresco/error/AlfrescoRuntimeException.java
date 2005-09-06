/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
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
