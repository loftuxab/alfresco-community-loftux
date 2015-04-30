/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.cluster.messenger;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A do-nothing implementation of the {@link Messenger} interface.
 * 
 * @author Matt Ward
 */
public class NullMessenger<T extends Serializable> implements Messenger<T>
{
    private static final Log logger = LogFactory.getLog(NullMessenger.class);
    private MessageReceiver<T> receiverDelegate;
    
    @Override
    public void send(T message)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Throwing away message: " + message);
        }
    }

    @Override
    public void setReceiver(MessageReceiver<T> receiver)
    {
        // Why are we keeping a receiver that is not used? If upgrading
        // a NullMessenger to a working Messenger, then we need to install the
        // correct receiver - which can't be done if it has been discarded.
        // (see ClusteredObjectProxyFactory)
        receiverDelegate = receiver;
    }

    @Override
    public MessageReceiver<T> getReceiver()
    {
        return receiverDelegate;
    }

    @Override
    public boolean isConnected()
    {
        return false;
    }

    @Override
    public String getAddress()
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("getAddress() always returns loopback address: 127.0.0.1");
        }
        return "127.0.0.1";
    }
}
