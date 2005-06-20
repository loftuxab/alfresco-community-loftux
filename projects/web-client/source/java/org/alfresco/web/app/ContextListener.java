package org.alfresco.web.app;

import java.util.List;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import javax.transaction.UserTransaction;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.importer.ImporterBootstrap;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.web.bean.repository.Repository;
import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * ServletContextListener implementation that initialises the application.
 * 
 * NOTE: This class must appear after the Spring context loader listener
 * 
 * @author gavinc
 */
public class ContextListener implements ServletContextListener, HttpSessionListener
{
   private static Logger logger = Logger.getLogger(ContextListener.class);
   
   /**
    * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
    */
   public void contextInitialized(ServletContextEvent event)
   {
      // make sure that the spaces store in the repository exists
      ServletContext servletContext = event.getServletContext();
      WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
      ServiceRegistry registry = (ServiceRegistry)ctx.getBean(ServiceRegistry.SERVICE_REGISTRY);
      NodeService nodeService = registry.getNodeService();
      SearchService searchService = registry.getSearchService();
      NamespaceService namespaceService = registry.getNamespaceService();

      String repoStoreName = Application.getRepositoryStoreName(servletContext);
      if (repoStoreName == null)
      {
         throw new Error("Repository store name has not been configured, is 'store-name' element missing?");
      }
      
      // check the repository exists, create if it doesn't
      UserTransaction tx = null;
      String companySpaceId = null;
      try
      {
         tx = registry.getUserTransaction();
         tx.begin();
         
         StoreRef storeRef = Repository.getStoreRef(servletContext);
         if (nodeService.exists(storeRef) == false)
         {
            storeRef = nodeService.createStore(StoreRef.PROTOCOL_WORKSPACE, repoStoreName);
            
            if (logger.isDebugEnabled())
               logger.debug("Created store with name: " + repoStoreName);
         }
         NodeRef rootNodeRef = nodeService.getRootNode(storeRef);
      
         // see if the company home space is present
         String companySpaceName = Application.getCompanyRootName(servletContext);
         String companyXPath = NamespaceService.ALFRESCO_PREFIX + ":" + QName.createValidLocalName(companySpaceName);
         List<ChildAssociationRef> nodes = nodeService.selectNodes(rootNodeRef, companyXPath, null, namespaceService, false);
         if (nodes.size() == 0)
         {
            // Construct binding values for import
            String companySpaceDescription = Application.getCompanyRootDescription(servletContext);
            String glossaryFolderName = Application.getGlossaryFolderName(servletContext);
            String templatesFolderName = Application.getTemplatesFolderName(servletContext);
            if (companySpaceName == null)
            {
               throw new Error("Company root name has not been configured, is 'company-root-name' element missing?");
            }
            if (glossaryFolderName == null)
            {
               throw new Error("Glossary folder name has not been configured, is 'glossary-folder-name' element missing?");
            }
            if (templatesFolderName == null)
            {
               throw new Error("Templates folder name has not been configured, is 'templates-folder-name' element missing?");
            }
            Properties configuration = new Properties();
            configuration.put("companySpaceName", companySpaceName);
            configuration.put("companySpaceDescription", companySpaceDescription);
            configuration.put("glossaryName", glossaryFolderName);
            configuration.put("templatesName", templatesFolderName);

            // Import bootstrap set of data
            ImporterBootstrap bootstrap = (ImporterBootstrap)ctx.getBean("importerBootstrap");
            bootstrap.setConfiguration(configuration);
            bootstrap.setStoreId(repoStoreName);
            bootstrap.bootstrap();

            // Find company root after import
            nodes = nodeService.selectNodes(rootNodeRef, companyXPath, null, namespaceService, false);
         }

         // Extract company space
         companySpaceId = nodes.get(0).getChildRef().getId();
         
         if (logger.isDebugEnabled())
             logger.debug("Found company space with id: " + companySpaceId);

         // commit the transaction
         tx.commit();
      }
      catch (Exception e)
      {
         // rollback the transaction
         try { if (tx != null) {tx.rollback();} } catch (Exception ex) {}
         throw new AlfrescoRuntimeException("Failed to initialise ", e);
      }
      
      // make sure the current user has the company space set as it's home space
      Application.getCurrentUser(servletContext).setHomeSpaceId(companySpaceId);
      
      if (logger.isDebugEnabled())
         logger.debug("Server is running in portal server: " + Application.inPortalServer(servletContext));
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
