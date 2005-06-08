package org.alfresco.web.app;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import javax.transaction.UserTransaction;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.dictionary.NamespaceService;
import org.alfresco.repo.dictionary.impl.DictionaryBootstrap;
import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.ref.ChildAssocRef;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.QName;
import org.alfresco.repo.ref.StoreRef;
import org.alfresco.util.Conversion;
import org.alfresco.web.bean.repository.Node;
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
      WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(event.getServletContext());
      NodeService nodeService = (NodeService)ctx.getBean(Repository.NODE_SERVICE);
      //Searcher searchService = (Searcher)ctx.getBean(Repository.SEARCH_SERVICE);
         
      if (nodeService.exists(Repository.getStoreRef()) == false)
      {
         // create the store
         nodeService.createStore(StoreRef.PROTOCOL_WORKSPACE, Repository.REPOSITORY_STORE);
      }
      
      // TODO: Use a search
//      String actNs = NamespaceService.ALFRESCO_PREFIX;
//      String s = "PATH:\"/" + actNs + ":" + companySpaceName + "\"";
//      ResultSet results = searchService.query(Repository.getStoreRef(), "lucene", s, null, null);
//      if (results.length() == 0)
      
      // see if the company home space is present
      String companySpaceId = null;
      String companySpaceName = Application.COMPANY_SPACE_NAME;
      boolean companySpaceFound = false;
      List<ChildAssocRef> childRefs = nodeService.getChildAssocs(
            nodeService.getRootNode(Repository.getStoreRef()));
      for (ChildAssocRef ref: childRefs)
      {
         QName qname = ref.getQName();
         
         // create our Node representation
         Node node = new Node(ref.getChildRef(), nodeService);
         
         // look for Space aspect
         if (node.getName().equals(qname.getLocalName()))
         {
            companySpaceFound = true;
            companySpaceId = node.getId();
            
            if (logger.isDebugEnabled())
               logger.debug("Found existing company space with id: " + companySpaceId);
            
            break;
         }
      }
      
      // if we didn't find the company space, create it
      if (!companySpaceFound)
      {
         UserTransaction tx = null;
      
         try
         {
            tx = (UserTransaction)ctx.getBean(Repository.USER_TRANSACTION);
            tx.begin();
            
            // create the folder node
            ChildAssocRef assocRef = nodeService.createNode(
                  nodeService.getRootNode(Repository.getStoreRef()),
                  DictionaryBootstrap.CHILD_ASSOC_QNAME_CONTAINS,
                  QName.createQName(NamespaceService.ALFRESCO_URI, companySpaceName),
                  DictionaryBootstrap.TYPE_QNAME_FOLDER);
            NodeRef nodeRef = assocRef.getChildRef();
            companySpaceId = nodeRef.getId();

            // set the name property on the node
            nodeService.setProperty(nodeRef, DictionaryBootstrap.PROP_QNAME_NAME, companySpaceName);

            // apply the uifacets aspect - icon, title and description props
            Map<QName, Serializable> uiFacetsProps = new HashMap<QName, Serializable>(5);
            uiFacetsProps.put(DictionaryBootstrap.PROP_QNAME_ICON, "space-icon-default");
            uiFacetsProps.put(DictionaryBootstrap.PROP_QNAME_TITLE, companySpaceName);
            uiFacetsProps.put(DictionaryBootstrap.PROP_QNAME_DESCRIPTION, "The company root space");
            nodeService.addAspect(nodeRef, DictionaryBootstrap.ASPECT_QNAME_UIFACETS, uiFacetsProps);
            
            // apply the auditable aspect - created and modified date
            Map<QName, Serializable> auditProps = new HashMap<QName, Serializable>(5);
            Date now = new Date( Calendar.getInstance().getTimeInMillis() );
            auditProps.put(DictionaryBootstrap.PROP_QNAME_CREATED, now);
            auditProps.put(DictionaryBootstrap.PROP_QNAME_MODIFIED, now);
            nodeService.addAspect(nodeRef, DictionaryBootstrap.ASPECT_QNAME_AUDITABLE, auditProps);
            
            // commit the transaction
            tx.commit();
            
            if (logger.isDebugEnabled())
            logger.debug("Created company root space with id: " + companySpaceId);
         }
         catch (Exception e)
         {
            // rollback the transaction
            try { if (tx != null) {tx.rollback();} } catch (Exception ex) {}
            throw new AlfrescoRuntimeException("Failed to create company root space", e);
         }
      }
      
      // make sure the current user has the company space set as it's home space
      Application.getCurrentUser(event.getServletContext()).setHomeSpaceId(companySpaceId);
      
      if (logger.isDebugEnabled())
         logger.debug("Server is running in portal server: " + Application.inPortalServer(event.getServletContext()));
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
