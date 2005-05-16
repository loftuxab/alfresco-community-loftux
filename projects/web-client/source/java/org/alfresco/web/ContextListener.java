package org.alfresco.web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.ref.StoreRef;
import org.alfresco.web.bean.repository.Repository;

/**
 * ServletContextListener implementation that initialises the application.
 * 
 * NOTE: This class must appear after the Spring context loader listener
 * 
 * @author gavinc
 */
public class ContextListener implements ServletContextListener, HttpSessionListener
{
   private static boolean inPortlet = true;
   private static final String SERVER_TYPE = "org.alfresco.SERVER_TYPE";
   private static Logger logger = Logger.getLogger(ContextListener.class);
   
   public static boolean inPortletServer()
   {
      return inPortlet;
   }
   
   /**
    * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
    */
   public void contextInitialized(ServletContextEvent event)
   {
      // make sure that the spaces store in the repository exists
      WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(event.getServletContext());
      NodeService nodeService = (NodeService)ctx.getBean("indexingNodeService");
         
      if (nodeService.exists(Repository.getStoreRef()) == false)
      {
         // create the store
         nodeService.createStore(StoreRef.PROTOCOL_WORKSPACE, Repository.REPOSITORY_STORE);
      }
      
      // determine what type of server we are using, servlet or portlet, default to portlet
      String serverType = event.getServletContext().getInitParameter(SERVER_TYPE);
      if (serverType != null && serverType.equalsIgnoreCase("servlet"))
      {
         inPortlet = false;
         
         if (logger.isDebugEnabled())
            logger.debug("Running in servlet mode");
      }
   }

   /**
    * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
    */
   public void contextDestroyed(ServletContextEvent event)
   {
      // nothing to do
   }

   public void sessionCreated(HttpSessionEvent event)
   {
      logger.info("HTTP session created: " + event.getSession().getId());
   }

   public void sessionDestroyed(HttpSessionEvent event)
   {
      logger.info("HTTP session destroyed: " + event.getSession().getId());
   }
}
