/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.enterprise.repo.cache;

import java.util.HashSet;
import java.util.Set;

import org.alfresco.enterprise.repo.cluster.messenger.MessageReceiver;
import org.alfresco.enterprise.repo.cluster.messenger.MessageSendingException;
import org.alfresco.enterprise.repo.cluster.messenger.Messenger;
import org.alfresco.enterprise.repo.cluster.messenger.MessengerFactory;
import org.alfresco.util.cache.AsynchronouslyRefreshedCacheRegistry;
import org.alfresco.util.cache.RefreshableCacheEvent;
import org.alfresco.util.cache.RefreshableCacheListener;
import org.alfresco.util.transaction.TransactionSupportUtil;
import org.alfresco.repo.transaction.TransactionListener;
import org.alfresco.util.PropertyCheck;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;

/**
 * This class listens to asynchronous cache events and broadcasts them in a cluster.
 * It receives the messages and delivers them to the appropriate cache by cache id  
 * 
 * @author Andy
 * @since 4.2
 */
public class AsynchronouslyRefreshedCacheMessageBroker implements InitializingBean, RefreshableCacheListener, BeanNameAware, MessageReceiver<RefreshableCacheEvent>, TransactionListener
{
    private static final String RESOURCE_KEY_TXN_DATA = "AsynchronouslyRefreshedCacheMessageBroker.TxnData";
    
    private static Log logger = LogFactory.getLog(AsynchronouslyRefreshedCacheMessageBroker.class);
    
    /** Creates communication channels within a cluster */
    private MessengerFactory messengerFactory; 

    /** Provides communications within a cluster */
    private Messenger<RefreshableCacheEvent> messenger;
    
    // State
    
    AsynchronouslyRefreshedCacheRegistry registry;
    String cacheId;
    
    private String resourceKeyTxnData;
    
    /**
     * @param registry the registry to set
     */
    public void setRegistry(AsynchronouslyRefreshedCacheRegistry registry)
    {
        this.registry = registry;
    }

    /**
     * @param messengerFactory the messengerFactory to set
     */
    public void setMessengerFactory(MessengerFactory messengerFactory)
    {
        this.messengerFactory = messengerFactory;
    }

    @Override
    public void setBeanName(String name)
    {
        cacheId = name;
    }

    @Override
    public String getCacheId()
    {
        return cacheId;
    }
    
    @Override
    public void afterPropertiesSet() throws Exception
    {
        PropertyCheck.mandatory(this, "registry", registry);
        PropertyCheck.mandatory(this, "messengerFactory", messengerFactory);
        messenger = messengerFactory.createMessenger(getClass().getName());
        messenger.setReceiver(this); 
        registry.register(this);
        
        resourceKeyTxnData = RESOURCE_KEY_TXN_DATA + "." + cacheId;
    }

    @Override
    public void onReceive(RefreshableCacheEvent event)
    {
        if (event == null)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Received cluster event - Ignoring: " + event);
            }
            return;
        }

        if (logger.isTraceEnabled())
        {
            logger.trace("RECEIVE: Broadcasting event: " + event);
        }

        // deliver by id only 
        registry.broadcastEvent(event, false);
    }

    @Override
    public void onRefreshableCacheEvent(RefreshableCacheEvent refreshableCacheEvent)
    {
        if (TransactionSupportUtil.getTransactionId() != null)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Preparing cache message in TX: " + refreshableCacheEvent);
            }
            TransactionData txData = getTransactionData();
            txData.refreshableCacheEvents.add(refreshableCacheEvent);
        }
        else
        {
            broadcast(refreshableCacheEvent);
        }
    }

    /**
     * @param refreshableCacheEvent
     */
    private void broadcast(RefreshableCacheEvent refreshableCacheEvent)
    {
        // Broadcast these events across the cluster
        if (messenger.isConnected())
        {
            try
            {
                if (logger.isTraceEnabled())
                {
                    logger.trace("SEND: Broadcasting event: " + refreshableCacheEvent);
                }
                messenger.send(refreshableCacheEvent);
            }
            catch (MessageSendingException e)
            { 
                logger.error("Error sending cluster event: " + refreshableCacheEvent, e);
            }
        }
    }
    
    /**
     * To be used in a transaction only.
     */
    private TransactionData getTransactionData()
    {
        TransactionData data = (TransactionData) TransactionSupportUtil.getResource(resourceKeyTxnData);
        if (data == null)
        {
            data = new TransactionData();

            // ensure that we get the transaction callbacks as we have bound the unique
            // transactional caches to a common manager
            TransactionSupportUtil.bindListener(this, 0);
            TransactionSupportUtil.bindResource(resourceKeyTxnData, data);
        }
        return data;
    }
    
    private static class TransactionData
    {
        Set<RefreshableCacheEvent> refreshableCacheEvents = new HashSet<RefreshableCacheEvent>(4);
    }

    @Override
    public void flush()
    {
        // Nothing
    }

    @Override
    public void beforeCommit(boolean readOnly)
    {
        // Nothing
    }

    @Override
    public void beforeCompletion()
    {
        // Nothing
    }

    @Override
    public void afterCommit()
    {
        TransactionData txData = getTransactionData();
        for (RefreshableCacheEvent refreshableCacheEvent : txData.refreshableCacheEvents)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Broadcasting cache message after commit: " + refreshableCacheEvent);
            }
            broadcast(refreshableCacheEvent);
        }
    }

    @Override
    public void afterRollback()
    {
        // Nothing
    }
}
