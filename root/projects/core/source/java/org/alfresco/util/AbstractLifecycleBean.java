/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.alfresco.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;


/**
 * Abstract helper for assisting the bootstrap and termination of Alfresco Components
 *  
 * @author davidc
 */
public abstract class AbstractLifecycleBean implements ApplicationListener, ApplicationContextAware
{
    protected final static Log log = LogFactory.getLog(AbstractLifecycleBean.class);    
    private ApplicationContext applicationContext = null;
    
    
    /* (non-Javadoc)
     * @see org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
     */
    public void onApplicationEvent(ApplicationEvent event)
    {
        if (event instanceof ContextRefreshedEvent)
        {
            ContextRefreshedEvent refreshEvent = (ContextRefreshedEvent)event;
            ApplicationContext refreshContext = refreshEvent.getApplicationContext();
            if (refreshContext != null && refreshContext.equals(applicationContext))
            {
                if (log.isDebugEnabled())
                    log.debug("Bootstrapping component " + this.getClass().getName());
                onBootstrap(refreshEvent);
            }
        }
        else if (event instanceof ContextClosedEvent)
        {
            ContextClosedEvent closedEvent = (ContextClosedEvent)event;
            ApplicationContext closedContext = closedEvent.getApplicationContext();
            if (closedContext != null && closedContext.equals(applicationContext))
            {
                if (log.isDebugEnabled())
                    log.debug("Shutting down component " + this.getClass().getName());
                onShutdown(closedEvent);
            }
        }
    }

    
    /* (non-Javadoc)
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    public void setApplicationContext(ApplicationContext applicationContext)
        throws BeansException
    {
        this.applicationContext = applicationContext;
    }

    /**
     * Gets the Application Context
     * 
     * @return  application context
     */
    protected ApplicationContext getApplicationContext()
    {
        return this.applicationContext;
    }
    
    /**
     * Callback for initialising Component on first startup of Alfresco Server
     * 
     * @param event
     */
    protected abstract void onBootstrap(ApplicationEvent event);
    
    /**
     * Callback for terminating Component on shutdown of Alfresco Server
     * 
     * @param event
     */
    protected abstract void onShutdown(ApplicationEvent event);
    
}
