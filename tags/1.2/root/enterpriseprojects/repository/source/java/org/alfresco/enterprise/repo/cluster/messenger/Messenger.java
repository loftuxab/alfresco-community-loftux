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
 * Provides facilities for peer-to-peer messaging within a cluster. This interface
 * is intended to act as a facade, allowing the actual implementation (e.g. Hazelcast)
 * to be decoupled as much as possible from the Alfresco code base.
 * <p>
 * Instances of this class are parameterised with the type of message payload
 * to send and receive.
 * 
 * @author Matt Ward
 */
public interface Messenger<T extends Serializable>
{
    void send(T message);
    
    void setReceiver(MessageReceiver<T> receiver);
    
    MessageReceiver<T> getReceiver();
    
    boolean isConnected();
    
    String getAddress();
}
