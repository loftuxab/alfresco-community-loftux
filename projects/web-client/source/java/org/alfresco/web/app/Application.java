package org.alfresco.web.app;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.portlet.PortletContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.alfresco.config.ConfigElement;
import org.alfresco.config.ConfigService;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.web.app.servlet.AuthenticationFilter;
import org.alfresco.web.bean.ErrorBean;
import org.alfresco.web.bean.repository.Node;
import org.alfresco.web.bean.repository.User;
import org.alfresco.web.config.ServerConfigElement;
import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.jsf.FacesContextUtils;

/**
 * Utilities class
 * 
 * @author gavinc
 */
public class Application
{
   public static final String BEAN_CONFIG_SERVICE = "configService";
   public static final String BEAN_DATA_DICTIONARY = "dataDictionary";
   public static final String BEAN_CURRENT_USER = "currentUser";
   
   private static String repoStoreName;
   private static String companyRootName;
   private static String companyRootDescription;
   private static String glossaryFolderName;
   private static String templatesFolderName;
   
   /**
    * Private constructor to prevent instantiation of this class 
    */
   private Application()
   {
   }
   
   /**
    * Determines whether the server is running in a portal
    * 
    * @param servletContext The servlet context
    * @return true if we are running inside a portal server
    */
   public static boolean inPortalServer(ServletContext servletContext)
   {
      boolean inPortal = true;
      
      ConfigService svc = (ConfigService)WebApplicationContextUtils.getRequiredWebApplicationContext(
            servletContext).getBean("configService");
      ServerConfigElement serverConfig = (ServerConfigElement)svc.getGlobalConfig().getConfigElement("server");
      
      if (serverConfig != null)
      {
         inPortal = serverConfig.isPortletMode();
      }
      
      return inPortal;
   }
   
   /**
    * Handles errors thrown from servlets
    * 
    * @param servletContext The servlet context
    * @param request The HTTP request
    * @param response The HTTP response
    * @param error The exception
    * @param logger The logger
    */
   public static void handleServletError(ServletContext servletContext, HttpServletRequest request,
         HttpServletResponse response, Throwable error, Logger logger, String returnPage)
      throws IOException, ServletException
   {
      // get the error bean from the session and set the error that occurred.
      HttpSession session = request.getSession();
      ErrorBean errorBean = (ErrorBean)session.getAttribute(ErrorBean.ERROR_BEAN_NAME);
      if (errorBean == null)
      {
         errorBean = new ErrorBean();
         session.setAttribute(ErrorBean.ERROR_BEAN_NAME, errorBean);
      }
      errorBean.setLastError(error);
      errorBean.setReturnPage(returnPage);

      // try and find the configured error page
      boolean errorShown = false;
      String errorPage = getErrorPage(servletContext);
      
      if (errorPage != null)
      {
         if (logger.isDebugEnabled())
            logger.debug("An error has occurred, redirecting to error page: " + errorPage);
      
         if (response.isCommitted() == false)
         {
            errorShown = true;
            response.sendRedirect(request.getContextPath() + errorPage);
         }
         else
         {
            if (logger.isDebugEnabled())
               logger.debug("Response is already committed, re-throwing error");
         }
      }
      else
      {
         if (logger.isDebugEnabled())
            logger.debug("No error page defined, re-throwing error");
      }
      
      // if we could show the error page for whatever reason, re-throw the error
      if (!errorShown)
      {
         if (error instanceof IOException)
         {
            throw (IOException)error;
         }
         else if (error instanceof ServletException)
         {
            throw (ServletException)error;
         }
         else
         {
            throw new ServletException(error);
         }
      }
   }
   
   /**
    * Retrieves the configured error page for the application
    * 
    * @param servletContext The servlet context
    * @return The configured error page or null if the configuration is missing
    */
   public static String getErrorPage(ServletContext servletContext)
   {
      return getErrorPage(WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext));
   }
   
   /**
    * Retrieves the configured error page for the application
    * 
    * @param portletContext The portlet context
    * @return
    */
   public static String getErrorPage(PortletContext portletContext)
   {
      return getErrorPage((WebApplicationContext)portletContext.getAttribute(
            WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE));
   }
   
   /**
    * Retrieves the configured login page for the application
    * 
    * @param servletContext The servlet context
    * @return The configured login page or null if the configuration is missing
    */
   public static String getLoginPage(ServletContext servletContext)
   {
      return getLoginPage(WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext));
   }
   
   /**
    * Retrieves the configured login page for the application
    * 
    * @param portletContext The portlet context
    * @return
    */
   public static String getLoginPage(PortletContext portletContext)
   {
      return getLoginPage((WebApplicationContext)portletContext.getAttribute(
            WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE));
   }
   
   /**
    * @return Returns the User object representing the currently logged in user
    */
   public static User getCurrentUser(ServletContext context)
   {
      return (User)context.getAttribute(AuthenticationFilter.AUTHENTICATION_USER);
   }
   
   /**
    * @return Returns the User object representing the currently logged in user
    */
   public static User getCurrentUser(FacesContext context)
   {
      return (User)context.getExternalContext().getSessionMap().get(AuthenticationFilter.AUTHENTICATION_USER);
   }
   
   /**
    * @return Returns the repository store name (retrieved from config service)
    */
   public static String getRepositoryStoreName(ServletContext context)
   {
      return getRepositoryStoreName(WebApplicationContextUtils.getRequiredWebApplicationContext(context));
   }
   
   /**
    * @return Returns the company root name (retrieved from config service)
    */
   public static String getRepositoryStoreName(FacesContext context)
   {
      return getRepositoryStoreName(FacesContextUtils.getRequiredWebApplicationContext(context));
   }
   
   /**
    * @return Returns the company root name (retrieved from config service)
    */
   public static String getCompanyRootName(ServletContext context)
   {
      return getCompanyRootName(WebApplicationContextUtils.getRequiredWebApplicationContext(context));
   }
   
   /**
    * @return Returns the company root name (retrieved from config service)
    */
   public static String getCompanyRootName(FacesContext context)
   {
      return getCompanyRootName(FacesContextUtils.getRequiredWebApplicationContext(context));
   }
   
   /**
    * @return Returns the company root description (retrieved from config service)
    */
   public static String getCompanyRootDescription(ServletContext context)
   {
      return getCompanyRootDescription(WebApplicationContextUtils.getRequiredWebApplicationContext(context));
   }
   
   /**
    * @return Returns the company root description (retrieved from config service)
    */
   public static String getCompanyRootDescription(FacesContext context)
   {
      return getCompanyRootDescription(FacesContextUtils.getRequiredWebApplicationContext(context));
   }
   
   /**
    * @return Returns the glossary folder name (retrieved from config service)
    */
   public static String getGlossaryFolderName(ServletContext context)
   {
      return getGlossaryFolderName(WebApplicationContextUtils.getRequiredWebApplicationContext(context));
   }
   
   /**
    * @return Returns the glossary folder name (retrieved from config service)
    */
   public static String getGlossaryFolderName(FacesContext context)
   {
      return getGlossaryFolderName(FacesContextUtils.getRequiredWebApplicationContext(context));
   }
   
   /**
    * @return Returns the templates folder name (retrieved from config service)
    */
   public static String getTemplatesFolderName(ServletContext context)
   {
      return getTemplatesFolderName(WebApplicationContextUtils.getRequiredWebApplicationContext(context));
   }
   
   /**
    * @return Returns the templates folder name (retrieved from config service)
    */
   public static String getTemplatesFolderName(FacesContext context)
   {
      return getTemplatesFolderName(FacesContextUtils.getRequiredWebApplicationContext(context));
   }
   
   /**
    * Returns the repository store name (retrieved from config service)
    * 
    * @param context The spring context
    * @return The company root name
    */
   private static String getRepositoryStoreName(WebApplicationContext context)
   {
      if (repoStoreName == null)
      {
         ConfigService configService = (ConfigService)context.getBean(BEAN_CONFIG_SERVICE);
         ConfigElement repoConfig = configService.getGlobalConfig().getConfigElement("repository");
         for (ConfigElement child : repoConfig.getChildren())
         {
            if (child.getName().equals("store-name"))
            {
               repoStoreName = child.getValue();
            }
         }
      }
      
      return repoStoreName;
   }
   
   /**
    * Returns the company root name (retrieved from config service)
    * 
    * @param context The spring context
    * @return The company root name
    */
   private static String getCompanyRootName(WebApplicationContext context)
   {
      if (companyRootName == null)
      {
         ConfigService configService = (ConfigService)context.getBean(BEAN_CONFIG_SERVICE);
         ConfigElement repoConfig = configService.getGlobalConfig().getConfigElement("repository");
         for (ConfigElement child : repoConfig.getChildren())
         {
            if (child.getName().equals("company-root-name"))
            {
               companyRootName = child.getValue();
            }
         }
      }
      
      return companyRootName;
   }
   
   /**
    * Returns the company root description (retrieved from config service)
    * 
    * @param context The spring context
    * @return The company root description
    */
   private static String getCompanyRootDescription(WebApplicationContext context)
   {
      if (companyRootDescription == null)
      {
         ConfigService configService = (ConfigService)context.getBean(BEAN_CONFIG_SERVICE);
         ConfigElement repoConfig = configService.getGlobalConfig().getConfigElement("repository");
         for (ConfigElement child : repoConfig.getChildren())
         {
            if (child.getName().equals("company-root-description"))
            {
               companyRootDescription = child.getValue();
            }
         }
      }
      
      return companyRootDescription;
   }
   
   /**
    * Returns the glossary folder name (retrieved from config service)
    * 
    * @param context The spring context
    * @return The glossary folder name
    */
   private static String getGlossaryFolderName(WebApplicationContext context)
   {
      if (glossaryFolderName == null)
      {
         ConfigService configService = (ConfigService)context.getBean(BEAN_CONFIG_SERVICE);
         ConfigElement repoConfig = configService.getGlobalConfig().getConfigElement("repository");
         for (ConfigElement child : repoConfig.getChildren())
         {
            if (child.getName().equals("glossary-folder-name"))
            {
               glossaryFolderName = child.getValue();
            }
         }
      }
      
      return glossaryFolderName;
   }
   
   /**
    * Returns the templates folder name (retrieved from config service)
    * 
    * @param context The spring context
    * @return The templates folder name
    */
   private static String getTemplatesFolderName(WebApplicationContext context)
   {
      if (templatesFolderName == null)
      {
         ConfigService configService = (ConfigService)context.getBean(BEAN_CONFIG_SERVICE);
         ConfigElement repoConfig = configService.getGlobalConfig().getConfigElement("repository");
         for (ConfigElement child : repoConfig.getChildren())
         {
            if (child.getName().equals("templates-folder-name"))
            {
               templatesFolderName = child.getValue();
            }
         }
      }
      
      return templatesFolderName;
   }
   
   /**
    * Retrieves the configured error page for the application
    * 
    * @param context The Spring contexr
    * @return The configured error page or null if the configuration is missing
    */
   private static String getErrorPage(WebApplicationContext context)
   {
      String errorPage = null;
      
      ConfigService svc = (ConfigService)context.getBean(BEAN_CONFIG_SERVICE);
      ServerConfigElement serverConfig = (ServerConfigElement)svc.getGlobalConfig().getConfigElement("server");
      
      if (serverConfig != null)
      {
         errorPage = serverConfig.getErrorPage();
      }
      
      return errorPage;
   }
   
   /**
    * Retrieves the configured login page for the application
    * 
    * @param context The Spring contexr
    * @return The configured login page or null if the configuration is missing
    */
   private static String getLoginPage(WebApplicationContext context)
   {
      String loginPage = null;
      
      ConfigService svc = (ConfigService)context.getBean(BEAN_CONFIG_SERVICE);
      ServerConfigElement serverConfig = (ServerConfigElement)svc.getGlobalConfig().getConfigElement("server");
      
      if (serverConfig != null)
      {
         loginPage = serverConfig.getLoginPage();
      }
      
      return loginPage;
   }
}
