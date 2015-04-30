/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.cluster.messenger;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Field;

import org.alfresco.util.Pair;
import org.apache.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.hazelcast.core.ITopic;
import com.hazelcast.core.Message;

/**
 * Tests for the HazelcastMessenger class.
 * 
 * @author Matt Ward
 */
@RunWith(MockitoJUnitRunner.class)
public class HazelcastMessengerTest
{
    private @Mock ITopic<Pair<String, String>> topic;
    private @Mock Log logger;
    private HazelcastMessenger<String> messenger;
    private String receivedMsg;
    
    @Before
    public void setUp() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
    {
        messenger = new HazelcastMessenger<String>(topic, "address");
        receivedMsg = null;
        Field loggerField = HazelcastMessenger.class.getDeclaredField("logger");
        loggerField.setAccessible(true);
        loggerField.set(messenger, logger);
    }
    
    @Test
    public void canSendMessage()
    {
        messenger.send("Test string");
        verify(topic).publish(new Pair<String, String>("address", "Test string"));
    }
    
    @Test
    public void canReceiveMessage()
    {
        messenger.setReceiver(new MessageReceiver<String>()
        {
            @Override
            public void onReceive(String message)
            {
                receivedMsg = new String(message);
            }
        });
        
        // Hazelcast will call the onMessage method...
        messenger.onMessage(new Message<Pair<String, String>>(
                    "topicName",
                    new Pair<String, String>("different_address", "Hazelcast is sending a message.")));
        
        // setReceiver() should have resulted in a listener being registered with the topic.
        verify(topic).addMessageListener(messenger);
        
        assertEquals("Hazelcast is sending a message.", receivedMsg);
    }
    
    @Test
    public void canIgnoreMessageFromSelf()
    {
        messenger.setReceiver(new MessageReceiver<String>()
        {
            @Override
            public void onReceive(String message)
            {
                receivedMsg = new String(message);
            }
        });
        
        // Hazelcast will call the onMessage method...
        messenger.onMessage(new Message<Pair<String, String>>(
                    "topicName",
                    new Pair<String, String>("address", "Hazelcast is sending a message.")));
        
        verify(topic).addMessageListener(messenger);
        
        // But the message is ignored
        assertEquals(null, receivedMsg);
    }
    
    @Test
    public void canReceiveMessageFromSelf()
    {
        // This time, allow local messaging
        messenger = new HazelcastMessenger<String>(topic, "address", true);
        
        messenger.setReceiver(new MessageReceiver<String>()
        {
            @Override
            public void onReceive(String message)
            {
                receivedMsg = new String(message);
            }
        });
        
        // Hazelcast will call the onMessage method...
        messenger.onMessage(new Message<Pair<String, String>>(
                    "topicName",
                    new Pair<String, String>("address", "Hazelcast is sending a message.")));
        
        // setReceiver() should have resulted in a listener being registered with the topic.
        verify(topic).addMessageListener(messenger);
        
        assertEquals("Hazelcast is sending a message.", receivedMsg);
    }
    
    @Test
    public void canActAsNullMessengerWhenHazelcastInactive()
    {
        doThrow(new IllegalStateException()).
            when(topic).
            publish(new Pair<String, String>("address", "Test string"));

        messenger.send("Test string");
        
        verify(logger).warn(anyString());
    }
}
