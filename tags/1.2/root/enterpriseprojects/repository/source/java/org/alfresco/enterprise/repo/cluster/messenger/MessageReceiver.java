/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.cluster.messenger;

import java.io.Serializable;

/**
 * Implement this interface and supply to a {@link Messenger} using
 * {@link Messenger#setReceiver(MessageReceiver)} in order to receive
 * messages from other {@link Messenger}s.
 * 
 * @author Matt Ward
 */
public interface MessageReceiver<T extends Serializable>
{
    void onReceive(T message);
}
