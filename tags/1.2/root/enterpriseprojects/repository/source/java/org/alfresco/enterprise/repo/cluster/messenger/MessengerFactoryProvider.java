/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.cluster.messenger;

/**
 * Static container for MessengerFactory. This allows code to obtain the correct
 * {@link MessengerFactory} implementation where dependency injection is not available.
 * 
 * @author Matt Ward
 */
public class MessengerFactoryProvider
{
    private static MessengerFactory instance;
    
    public void setInstance(MessengerFactory messengerFactory)
    {
        instance = messengerFactory;
    }
    
    public static MessengerFactory getInstance()
    {
        if (instance == null)
        {
            throw new IllegalStateException("MessengerFactory instance not configured yet.");
        }
        return instance;
    }
}
