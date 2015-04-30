/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.cluster.messenger;

import java.io.Serializable;

import org.alfresco.util.Pair;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hazelcast.core.ITopic;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;

/**
 * Hazelcast-based implementation of the {@link Messenger} interface.
 * 
 * @see HazelcastMessengerFactory
 * @author Matt Ward
 */
public class HazelcastMessenger<T extends Serializable> implements Messenger<T>, MessageListener<Pair<String, T>>
{
    private ITopic<Pair<String, T>> topic;
    private MessageReceiver<T> receiverDelegate;
    private String address;
    private boolean allowSendToSelf;
    private static Log logger = LogFactory.getLog(HazelcastMessenger.class);
    
    /**
     * Create a messenger that will not respond to locally sent messages.
     * 
     * @param topic           ITopic to use as message transport.
     * @param address         Address to identify messenger source by, e.g. <IP>:<PORT> or <GUID>.
     */
    public HazelcastMessenger(ITopic<Pair<String, T>> topic, String address)
    {
        this(topic, address, false);
    }
    
    /**
     * @param topic           ITopic to use as message transport.
     * @param address         Address to identify messenger source by, e.g. <IP>:<PORT> or <GUID>.
     * @param allowSendToSelf If false then receiver will not be invoked for messages sent by the same messenger
     *                        (as identified by the address parameter).
     */
    public HazelcastMessenger(ITopic<Pair<String, T>> topic, String address, boolean allowSendToSelf)
    {
        this.topic = topic;
        this.address = address;
        this.allowSendToSelf = allowSendToSelf;
    }


    @Override
    public void send(T message)
    {
        if (logger.isTraceEnabled())
        {
            String digest = StringUtils.abbreviate(message.toString(), 100);
            logger.trace("Sending [topic: " + topic.getName() + ", source: " + address + "]: " + digest);
        }
        try
        {
            topic.publish(new Pair<String, T>(address, message));
        }
        catch (IllegalStateException e)
        {
            // Hazelcast is inactive
            String digest = StringUtils.abbreviate(message.toString(), 100);
            logger.warn("Cluster inactive, unable to send message: " + digest);
        }
    }

    @Override
    public void setReceiver(MessageReceiver<T> receiver)
    {
        // Install a delegate to ready to handle incoming messages.
        receiverDelegate = receiver;
        // Start receiving messages.
        topic.addMessageListener(this);
    }

    @Override
    public MessageReceiver<T> getReceiver()
    {
        return receiverDelegate;
    }

    @Override
    public void onMessage(Message<Pair<String, T>> message)
    {
        String sourceAddress = message.getMessageObject().getFirst();
        T payload = message.getMessageObject().getSecond();
        if (allowSendToSelf || !address.equals(sourceAddress))
        {
            if (logger.isTraceEnabled())
            {
                String digest = StringUtils.abbreviate(payload.toString(), 100);
                logger.trace("Received [destination: " + address +
                            ", source: " + sourceAddress + ", message: " + digest +
                            "] (delegating to receiver " + receiverDelegate + ")");
            }
            receiverDelegate.onReceive(payload);
        }
        else
        {
            if (logger.isTraceEnabled())
            {
                String digest = StringUtils.abbreviate(payload.toString(), 100);
                logger.trace("Ignoring message from self." +
                            " [topic: " + topic.getName() + ", address: " + address + ", message: " + digest + "]");
            }
        }
    }

    @Override
    public boolean isConnected()
    {
        return true;
    }
    
    protected ITopic<Pair<String, T>> getTopic()
    {
        return topic;
    }


    @Override
    public String getAddress()
    {
        return address;
    }


    @Override
    public String toString()
    {
        return "HazelcastMessenger[connected=" + isConnected() +
                    ", topic=" + getTopic() +
                    ", address=" + getAddress() + "]";
    }
}
