/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.cluster.messenger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.*;

/**
 * Helper class for testing Messenger related code.
 * 
 * @author Matt Ward
 */
public class MessengerTestHelper
{
    private String receivedMsg;
    private final static int SLEEP_MILLIS = 50;
    private static final int MAX_TRIES = 30;
    
    
    public MessengerTestHelper()
    {
        setReceivedMsg(null);
    }
    
    /**
     * Try to avoid intermitten test failures by trying multiple times. Hopefully the messge
     * will have been received after the very first sleep, but in a slow environment it may take longer.
     * This also allows the sleep time to be lower - rather than waiting for say 50 ms, we can try 10 times
     * at 5 ms - with a chance that we can return after the initial 5 ms.
     * 
     * @param expectedMsg
     * @throws InterruptedException
     */
    public void checkMessageReceivedWas(String expectedMsg)
    {
        int tries = 0;
        
        while (tries < MAX_TRIES)
        {
            try
            {
                Thread.sleep(SLEEP_MILLIS);
            }
            catch (InterruptedException e)
            {
                // Carry on
                e.printStackTrace();
            }
            if (getReceivedMsg() != null)
            {
                assertEquals(expectedMsg, getReceivedMsg());
                return;
            }
            tries++;
        }
        fail("No message received, tried " + tries +
             " times, sleeping " + SLEEP_MILLIS + "ms each time.");
    }
    
    /**
     * Assert that no message was received in the given period.
     */
    public void checkNoMessageReceived()
    {
        int tries = 0;
        
        while (tries < MAX_TRIES)
        {
            try
            {
                Thread.sleep(SLEEP_MILLIS);
            }
            catch (InterruptedException e)
            {
                // Carry on
                e.printStackTrace();
            }
            if (getReceivedMsg() != null)
            {
                fail("Message received but should NOT have been. Message was: " + getReceivedMsg());
            }
            tries++;
        }
    }

    /**
     * @return the receivedMsg
     */
    public String getReceivedMsg()
    {
        return this.receivedMsg;
    }

    /**
     * @param receivedMsg the receivedMsg to set
     */
    public void setReceivedMsg(String receivedMsg)
    {
        this.receivedMsg = receivedMsg;
    }
    
    
    public static class TestMessageReceiver implements MessageReceiver<String>
    {
        public MessengerTestHelper helper = new MessengerTestHelper();
        
        @Override
        public void onReceive(String message)
        {
            helper.setReceivedMsg(message);
        }   
    }
}
