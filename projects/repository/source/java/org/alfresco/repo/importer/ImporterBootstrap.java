/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.repo.importer;

import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Properties;

import javax.transaction.UserTransaction;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.view.ImporterException;
import org.alfresco.service.cmr.view.ImporterService;
import org.alfresco.service.cmr.view.Location;
import org.alfresco.service.cmr.view.ImporterProgress;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Bootstrap Repository store.
 * 
 * @author David Caruana
 */
public class ImporterBootstrap
{
    // View Properties (used in setBootstrapViews)
    public static final String VIEW_PATH_PROPERTY = "path";
    public static final String VIEW_CHILDASSOCTYPE_PROPERTY = "childAssocType";
    public static final String VIEW_LOCATION_VIEW = "location";
    
    // Logger
    private static final Log logger = LogFactory.getLog(ImporterBootstrap.class);

    // Dependencies
    private TransactionService transactionService;
    private NamespaceService namespaceService;
    private NodeService nodeService;
    private ImporterService importerService;
    private List<Properties> bootstrapViews;
    private StoreRef storeRef;
    private Properties configuration;
    

    /**
     * Sets the Transaction Service
     * 
     * @param userTransaction the transaction service
     */
    public void setTransactionService(TransactionService transactionService)
    {
        this.transactionService = transactionService;
    }
    
    /**
     * Sets the namespace service
     * 
     * @param namespaceService the namespace service
     */
    public void setNamespaceService(NamespaceService namespaceService)
    {
        this.namespaceService = namespaceService;
    }

    /**
     * Sets the node service
     * 
     * @param nodeService the node service
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    /**
     * Sets the importer service
     * 
     * @param importerService the importer service
     */
    public void setImporterService(ImporterService importerService)
    {
        this.importerService = importerService;
    }
        
    /**
     * Sets the bootstrap views
     * 
     * @param bootstrapViews
     */
    public void setBootstrapViews(List<Properties> bootstrapViews)
    {
        this.bootstrapViews = bootstrapViews;
    }
    
    /**
     * Sets the Store URL to bootstrap into
     * 
     * @param storeUrl
     */
    public void setStoreUrl(String storeUrl)
    {
        this.storeRef = new StoreRef(storeUrl);
    }
    
    /**
     * Sets the Configuration values for binding place holders
     * 
     * @param configuration
     */
    public void setConfiguration(Properties configuration)
    {
        this.configuration = configuration;
    }


    /**
     * Boostrap the Repository
     */
    public void bootstrap()
    {
        if (transactionService == null)
        {
            throw new ImporterException("Transaction Service must be provided");
        }
        if (namespaceService == null)
        {
            throw new ImporterException("Namespace Service must be provided");
        }
        if (nodeService == null)
        {
            throw new ImporterException("Node Service must be provided");
        }
        if (importerService == null)
        {
            throw new ImporterException("Importer Service must be provided");
        }
        if (storeRef == null)
        {
            throw new ImporterException("Store URL must be provided");
        }
        
        UserTransaction userTransaction = transactionService.getUserTransaction();

        try
        {
            userTransaction.begin();
        
            // check the repository exists, create if it doesn't
            if (nodeService.exists(storeRef))
            {
                if (logger.isDebugEnabled())
                    logger.debug("Store exists - bootstrap ignored: " + storeRef);
                
                userTransaction.rollback();
            }
            else
            {
                // create the store           
                storeRef = nodeService.createStore(storeRef.getProtocol(), storeRef.getIdentifier());
       
                if (logger.isDebugEnabled())
                    logger.debug("Created store: " + storeRef);
    
                // bootstrap the store contents
                for (Properties bootstrapView : bootstrapViews)
                {
                    // Create input stream onto view file
                    String view = bootstrapView.getProperty(VIEW_LOCATION_VIEW);
                    if (view == null || view.length() == 0)
                    {
                        throw new ImporterException("View file location must be provided");
                    }
                    InputStream viewStream = getClass().getClassLoader().getResourceAsStream(view);
                    if (viewStream == null)
                    {
                        throw new ImporterException("Could not find view file " + view);
                    }
                    
                    // Create import location
                    Location importLocation = new Location(storeRef);
                    String path = bootstrapView.getProperty(VIEW_PATH_PROPERTY);
                    if (path != null && path.length() > 0)
                    {
                        importLocation.setPath(path);
                    }
                    String childAssocType = bootstrapView.getProperty(VIEW_CHILDASSOCTYPE_PROPERTY);
                    if (childAssocType != null && childAssocType.length() > 0)
                    {
                        importLocation.setChildAssocType(QName.createQName(childAssocType, namespaceService));
                    }
        
                    // Now import...
                    importerService.importView(viewStream, importLocation, configuration, new BootstrapProgress());
                }
                
                userTransaction.commit();
            }
        }
        catch(Throwable e)
        {
            // rollback the transaction
            try { if (userTransaction != null) {userTransaction.rollback();} } catch (Exception ex) {}
            throw new AlfrescoRuntimeException("Bootstrap failed", e);
        }            
    }
    
    
    /**
     * Bootstrap Progress (debug logging)
     */
    private class BootstrapProgress implements ImporterProgress
    {
        /* (non-Javadoc)
         * @see org.alfresco.repo.importer.Progress#nodeCreated(org.alfresco.service.cmr.repository.NodeRef, org.alfresco.service.cmr.repository.NodeRef, org.alfresco.service.namespace.QName, org.alfresco.service.namespace.QName)
         */
        public void nodeCreated(NodeRef nodeRef, NodeRef parentRef, QName assocName, QName childName)
        {
            if (logger.isDebugEnabled())
                logger.debug("Created node " + nodeRef + " (child name: " + childName + ") within parent " + parentRef + " (association type: " + assocName + ")");
        }

        /* (non-Javadoc)
         * @see org.alfresco.repo.importer.Progress#contentCreated(org.alfresco.service.cmr.repository.NodeRef, java.lang.String)
         */
        public void contentCreated(NodeRef nodeRef, String sourceUrl)
        {
            if (logger.isDebugEnabled())
                logger.debug("Imported content from " + sourceUrl + " into node " + nodeRef);
        }

        /* (non-Javadoc)
         * @see org.alfresco.repo.importer.Progress#propertySet(org.alfresco.service.cmr.repository.NodeRef, org.alfresco.service.namespace.QName, java.io.Serializable)
         */
        public void propertySet(NodeRef nodeRef, QName property, Serializable value)
        {
            if (logger.isDebugEnabled())
                logger.debug("Property " + property + " set to value " + value + " on node " + nodeRef);
        }

        /* (non-Javadoc)
         * @see org.alfresco.repo.importer.Progress#aspectAdded(org.alfresco.service.cmr.repository.NodeRef, org.alfresco.service.namespace.QName)
         */
        public void aspectAdded(NodeRef nodeRef, QName aspect)
        {
            if (logger.isDebugEnabled())
                logger.debug("Added aspect " + aspect + " to node " + nodeRef);
        }

    }

    
}
