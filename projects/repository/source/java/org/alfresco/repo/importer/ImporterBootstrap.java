package org.alfresco.repo.importer;

import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Properties;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
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
    private NamespaceService namespaceService;
    private ImporterService importerService;
    private List<Properties> bootstrapViews;
    private String storeId;
    private Properties configuration;
    

    /**
     * Sets the namespace service
     * 
     * @param namespaceService
     */
    public void setNamespaceService(NamespaceService namespaceService)
    {
        this.namespaceService = namespaceService;
    }
    
    /**
     * Sets the importer service
     * 
     * @param importerService
     */
    public void setImporterService(ImporterService importerService)
    {
        this.importerService = importerService;
    }

    /**
     * Sets the bootstrap views
     * @param bootstrapViews
     */
    public void setBootstrapViews(List<Properties> bootstrapViews)
    {
        this.bootstrapViews = bootstrapViews;
    }
    
    /**
     * Sets the Store ID to bootstrap into
     * 
     * @param storeId
     */
    public void setStoreId(String storeId)
    {
        this.storeId = storeId;
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
        if (namespaceService == null)
        {
            throw new ImporterException("Namespace Service must be provided");
        }
        if (importerService == null)
        {
            throw new ImporterException("Importer Service must be provided");
        }
        if (storeId == null || storeId.length() == 0)
        {
            throw new ImporterException("Store Id must be provided");
        }
        
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
            Location importLocation = new Location(new StoreRef(StoreRef.PROTOCOL_WORKSPACE, storeId));
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
            importerService.importNodes(viewStream, importLocation, configuration, new BootstrapProgress());
        }
    }
    
    
    /**
     * Bootstrap Progress (debug logging)
     */
    private class BootstrapProgress implements Progress
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
