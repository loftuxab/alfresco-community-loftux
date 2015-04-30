/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.cluster.messenger;

import org.alfresco.error.AlfrescoRuntimeException;

/**
 * Thrown when unable to send a message using a {@link Messenger}.
 * 
 * @author Matt Ward
 */
public class MessageSendingException extends AlfrescoRuntimeException
{
    private static final long serialVersionUID = 1L;

    public MessageSendingException(Throwable e)
    {
        super("system.cluster.err.cannot_send_msg", e);
    }
}
