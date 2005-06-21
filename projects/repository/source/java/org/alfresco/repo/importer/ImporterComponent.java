package org.alfresco.repo.importer;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.ChildAssociationDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.datatype.ValueConverter;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ParameterCheck;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StringUtils;


/**
 * Default implementation of the Importer Service
 *  
 * @author David Caruana
 *
 */
public class ImporterComponent
    implements ImporterService
{
    // Default importer
    // TODO: Allow registration of plug-in parsers (by namespace)
    private Parser viewParser;

    // Supporting services
    private NamespaceService namespaceService;
    private DictionaryService dictionaryService;
    private NodeService nodeService;
    private ContentService contentService;

    
    /**
     * @param viewParser  the default parser
     */
    public void setViewParser(Parser viewParser)
    {
        this.viewParser = viewParser;
    }
    
    /**
     * @param nodeService  the node service
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    /**
     * @param contentService  the content service
     */
    public void setContentService(ContentService contentService)
    {
        this.contentService = contentService;
    }
    
    /**
     * @param dictionaryService  the dictionary service
     */
    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }
    
    /**
     * @param namespaceService  the namespace service
     */
    public void setNamespaceService(NamespaceService namespaceService)
    {
        this.namespaceService = namespaceService;
    }

    
    /* (non-Javadoc)
     * @see org.alfresco.repo.importer.ImporterService#importNodes(java.io.InputStream, org.alfresco.repo.importer.Location, java.util.Properties, org.alfresco.repo.importer.Progress)
     */
    public void importNodes(InputStream inputStream, Location location, Properties configuration, Progress progress)
    {
        ParameterCheck.mandatory("Input stream", inputStream);
        ParameterCheck.mandatory("Location", location);
        
        // Establish node to import within
        NodeRef nodeRef = location.getNodeRef();
        if (nodeRef == null)
        {
            // If a specific node has not been provided, default to the root
            nodeRef = nodeService.getRootNode(location.getStoreRef());
        }
        
        // Resolve to path within node, if one specified
        String path = location.getPath();
        if (path != null && path.length() >0)
        {
            // Create a valid path and search
            path = bindPlaceHolder(path, configuration);
            path = createValidPath(path);
            List<NodeRef> nodeRefs = nodeService.selectNodes(nodeRef, path, null, namespaceService, false);
            if (nodeRefs.size() == 0)
            {
                throw new ImporterException("Path " + path + " with node " + nodeRef + " does not exist - the path must resolve to a valid location");
            }
            if (nodeRefs.size() > 1)
            {
                throw new ImporterException("Path " + path + " with node " + nodeRef + " found too many locations - the path must resolve to one location");
            }
            nodeRef = nodeRefs.get(0);
        }
        
        // TODO: Check Node actually exists
        
        // Establish child association type to import under
        QName childAssocType = location.getChildAssocType();
        if (childAssocType == null)
        {
            // Determine if only one child association type exists
            QName nodeType = nodeService.getType(nodeRef);
            Set<QName> nodeAspects = nodeService.getAspects(nodeRef);
            TypeDefinition anonymousType = dictionaryService.getAnonymousType(nodeType, nodeAspects);
            Map<QName, ChildAssociationDefinition> childAssocDefs = anonymousType.getChildAssociations();
            if (childAssocDefs.size() > 1)
            {
                throw new ImporterException("Can not determine child association type to use - location " + nodeRef + " supports multiple child association types: " + childAssocDefs.toString());
            }
            childAssocType = childAssocDefs.keySet().iterator().next();
        }

        // Perform import
        Importer defaultImporter = new DefaultImporter(nodeRef, childAssocType, configuration, progress);
        viewParser.parse(inputStream, defaultImporter);
    }
    
    /**
     * Bind the specified value to the passed configuration values if it is a place holder
     * 
     * @param value  the value to bind
     * @param configuration  the configuration properties to bind to
     * @return  the bound value
     */
    private String bindPlaceHolder(String value, Properties configuration)
    {
        // TODO: replace with more efficient approach
        String boundValue = value;
        if (configuration != null)
        {
            for (Object key : configuration.keySet())
            {
                String stringKey = (String)key;
                boundValue = StringUtils.replace(boundValue, "${" + stringKey + "}", configuration.getProperty(stringKey));
            }
        }
        return boundValue;
    }
    
    /**
     * Create a valid path
     * 
     * @param path
     * @return
     */
    private String createValidPath(String path)
    {
        StringBuffer validPath = new StringBuffer(path.length());
        String[] segments = StringUtils.delimitedListToStringArray(path, "/");
        for (int i = 0; i < segments.length; i++)
        {
            if (segments[i] != null && segments[i].length() > 0)
            {
                validPath.append(QName.createValidLocalName(segments[i]));
            }
            if (i < (segments.length -1))
            {
                validPath.append("/");
            }
        }
        return validPath.toString();
    }
    
    /**
     * Default Importer strategy
     * 
     * @author David Caruana
     */
    private class DefaultImporter
        implements Importer
    {
        private NodeRef rootRef;
        private QName rootAssocType;
        private Properties configuration;
        private Progress progress;

        
        /**
         * Construct
         * 
         * @param rootRef
         * @param rootAssocType
         * @param configuration
         * @param progress
         */
        private DefaultImporter(NodeRef rootRef, QName rootAssocType, Properties configuration, Progress progress)
        {
            this.rootRef = rootRef;
            this.rootAssocType = rootAssocType;
            this.configuration = configuration;
            this.progress = progress;
        }
        
        /* (non-Javadoc)
         * @see org.alfresco.repo.importer.Importer#getRootRef()
         */
        public NodeRef getRootRef()
        {
            return rootRef;
        }

        /* (non-Javadoc)
         * @see org.alfresco.repo.importer.Importer#getRootAssocType()
         */
        public QName getRootAssocType()
        {
            return rootAssocType;
        }
        
        /* (non-Javadoc)
         * @see org.alfresco.repo.importer.Importer#importNode(org.alfresco.repo.importer.ImportNode)
         */
        public NodeRef importNode(ImportNode context)
        {
            TypeDefinition nodeType = context.getTypeDefinition();
            NodeRef parentRef = context.getParentContext().getParentRef();
            QName assocType = context.getParentContext().getAssocType();
            QName childQName = null;

            // Determine child name
            String childName = context.getChildName();
            if (childName != null)
            {
                childQName = QName.createQName(childName, namespaceService); 
            }
            else
            {
                Map<QName, String> typeProperties = context.getProperties(nodeType.getName());
                String name = typeProperties.get(ContentModel.PROP_NAME);
                if (name == null || name.length() == 0)
                {
                    throw new ImporterException("Cannot import node of type " + nodeType.getName() + " - it does not have a name");
                }
                
                name = bindPlaceHolder(name, configuration);
                String localName = QName.createValidLocalName(name);
                childQName = QName.createQName(assocType.getNamespaceURI(), localName);
            }
            
            // Build initial map of properties
            Map<QName, String> initialProperties = new HashMap<QName, String>();
            initialProperties.putAll(context.getProperties(nodeType.getName()));
            List<AspectDefinition> defaultAspects = nodeType.getDefaultAspects();
            for (AspectDefinition aspect: defaultAspects)
            {
                initialProperties.putAll(context.getProperties(aspect.getName()));
            }
            Map<QName, Serializable> boundInitialProperties = bindProperties(initialProperties);
            
            // Create initial node
            ChildAssociationRef assocRef = nodeService.createNode(parentRef, assocType, childQName, nodeType.getName(), boundInitialProperties);
            NodeRef nodeRef = assocRef.getChildRef();
            reportNodeCreated(assocRef);
            reportPropertySet(nodeRef, boundInitialProperties);
            
            // Apply aspects
            for (QName aspect : context.getNodeAspects())
            {
                if (nodeService.hasAspect(nodeRef, aspect) == false)
                {
                    Map<QName, String> aspectProperties = context.getProperties(aspect);
                    Map<QName, Serializable> boundAspectProperties = bindProperties(aspectProperties);
                    nodeService.addAspect(nodeRef, aspect, boundAspectProperties);
                    reportAspectAdded(nodeRef, aspect);
                    reportPropertySet(nodeRef, boundAspectProperties);
                }
            }

            // Import Content, if applicable
            if (dictionaryService.isSubClass(nodeType.getName(), ContentModel.TYPE_CONTENT))
            {
                importContent(nodeRef);
            }
            
            return nodeRef;
        }
        
        /**
         * Import Node Content
         * 
         * @param context
         */
        private void importContent(NodeRef nodeRef)
        {
            // Extract the source location of the content
            String contentUrl = (String)nodeService.getProperty(nodeRef, ContentModel.PROP_CONTENT_URL);
            if (contentUrl != null && contentUrl.length() > 0)
            {
                // Create a Resource around the source location
                ResourceLoader loader = new DefaultResourceLoader();
                Resource resource = loader.getResource(contentUrl);
                if (resource.exists() == false)
                {
                    throw new ImporterException("Content URL " + contentUrl + " does not exist.");
                }
        
                // Import the content
                try
                {
                    ContentWriter writer = contentService.getUpdatingWriter(nodeRef);
                    InputStream contentStream = resource.getInputStream();
                    writer.putContent(contentStream);
                }
                catch(IOException e)
                {
                    throw new ImporterException("Failed to import content " + contentUrl, e);
                }
                
                reportContentCreated(nodeRef, contentUrl);
            }
        }

        /**
         * Bind properties
         * 
         * @param properties
         * @return
         */
        private Map<QName, Serializable> bindProperties(Map<QName, String> properties)
        {
            Map<QName, Serializable> boundProperties = new HashMap<QName, Serializable>(properties.size());
            for (QName property : properties.keySet())
            {
                PropertyDefinition propDef = dictionaryService.getProperty(property);
                if (propDef == null)
                {
                    throw new ImporterException("Property " + property + " does not exist in the repository dictionary");
                }
                String value = bindPlaceHolder(properties.get(property), configuration);
                Serializable object = (Serializable)ValueConverter.convert(propDef.getPropertyType(), value);
                boundProperties.put(property, object);
            }
            return boundProperties;
        }

        /**
         * Helper to report node created progress
         * 
         * @param progress
         * @param childAssocRef
         */
        private void reportNodeCreated(ChildAssociationRef childAssocRef)
        {
            if (progress != null)
            {
                progress.nodeCreated(childAssocRef.getChildRef(), childAssocRef.getParentRef(), childAssocRef.getTypeQName(), childAssocRef.getQName());
            }
        }

        /**
         * Helper to report content created progress
         * 
         * @param progress
         * @param nodeRef
         * @param sourceUrl
         */
        private void reportContentCreated(NodeRef nodeRef, String sourceUrl)
        {
            if (progress != null)
            {
                progress.contentCreated(nodeRef, sourceUrl);
            }
        }
        
        /**
         * Helper to report aspect added progress
         *  
         * @param progress
         * @param nodeRef
         * @param aspect
         */
        private void reportAspectAdded(NodeRef nodeRef, QName aspect)
        {
            if (progress != null)
            {
                progress.aspectAdded(nodeRef, aspect);
            }        
        }

        /**
         * Helper to report property set progress
         * 
         * @param progress
         * @param nodeRef
         * @param properties
         */
        private void reportPropertySet(NodeRef nodeRef, Map<QName, Serializable> properties)
        {
            if (progress != null)
            {
                for (QName property : properties.keySet())
                {
                    progress.propertySet(nodeRef, property, properties.get(property));
                }
            }
        }
    }
    
}
