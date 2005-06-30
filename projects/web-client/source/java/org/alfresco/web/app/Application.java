/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/lgpl.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
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
import org.alfresco.web.app.servlet.AuthenticationFilter;
import org.alfresco.web.bean.ErrorBean;
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
   
   private static boolean inPortalServer = true;
   private static String repoStoreUrl;
   private static String rootPath;
   private static String companyRootId;
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
    * Sets whether this application is running inside a portal server
    * 
    * @param inPortal true to indicate the application is running as a portlet 
    */
   public static void setInPortalServer(boolean inPortal)
   {
      inPortalServer = inPortal;
   }
   
   /**
    * Determines whether the server is running in a portal
    * 
    * @return true if we are running inside a portal server
    */
   public static boolean inPortalServer()
   {
      return inPortalServer;
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
    * @return Returns the repository store URL (retrieved from config service)
    */
   public static String getRepositoryStoreUrl(ServletContext context)
   {
      return getRepositoryStoreUrl(WebApplicationContextUtils.getRequiredWebApplicationContext(context));
   }
   
   /**
    * @return Returns the repository store URL (retrieved from config service)
    */
   public static String getRepositoryStoreUrl(FacesContext context)
   {
      return getRepositoryStoreUrl(FacesContextUtils.getRequiredWebApplicationContext(context));
   }
   
   /**
    * @return Returns id of the company root 
    */
   public static String getCompanyRootId()
   {
      return companyRootId;
   }
   
   /**
    * Sets the company root id. This is setup by the ContextListener.
    * 
    * @param id The company root id
    */
   public static void setCompanyRootId(String id)
   {
      companyRootId = id;
   }
   
   /**
    * @return Returns the root path for the application (retrieved from config service)
    */
   public static String getRootPath(ServletContext context)
   {
      return getRootPath(WebApplicationContextUtils.getRequiredWebApplicationContext(context));
   }
   
   /**
    * @return Returns the root path for the application (retrieved from config service)
    */
   public static String getRootPath(FacesContext context)
   {
      return getRootPath(FacesContextUtils.getRequiredWebApplicationContext(context));
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
    * Returns the repository store URL (retrieved from config service)
    * 
    * @param context The spring context
    * @return The repository store URL to use
    */
   private static String getRepositoryStoreUrl(WebApplicationContext context)
   {
      if (repoStoreUrl == null)
      {
         ConfigService configService = (ConfigService)context.getBean(BEAN_CONFIG_SERVICE);
         ConfigElement repoConfig = configService.getGlobalConfig().getConfigElement("repository");
         ConfigElement storeUrlConfig = repoConfig.getChild("store-url");
         if (storeUrlConfig != null)
         {
             repoStoreUrl = storeUrlConfig.getValue();
         }
      }
      
      return repoStoreUrl;
   }
   
   /**
    * Returns the root path for the application (retrieved from config service)
    * 
    * @param context The spring context
    * @return The application root path
    */
   private static String getRootPath(WebApplicationContext context)
   {
      if (rootPath == null)
      {
         ConfigService configService = (ConfigService)context.getBean(BEAN_CONFIG_SERVICE);
         ConfigElement repoConfig = configService.getGlobalConfig().getConfigElement("repository");
         ConfigElement rootPathConfig = repoConfig.getChild("root-path");
         if (rootPathConfig != null)
         {
             rootPath = rootPathConfig.getValue();
         }
      }
      
      return rootPath;
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
