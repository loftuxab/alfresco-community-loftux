package com.activiti.web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.activiti.config.ConfigServiceFactory;

/**
 * ServletContextListener implementation that initialises the application 
 * 
 * @author gavinc
 */
public class ContextListener implements ServletContextListener
{
   /**
    * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
    */
   public void contextInitialized(ServletContextEvent event)
   {
      ConfigServiceFactory.getConfigService(event.getServletContext());
   }

   /**
    * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
    */
   public void contextDestroyed(ServletContextEvent event)
   {
      // nothing to do
   }
}
