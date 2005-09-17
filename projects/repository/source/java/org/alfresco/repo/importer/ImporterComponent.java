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

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.transaction.AlfrescoTransactionSupport;
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
import org.alfresco.service.cmr.repository.datatype.DefaultTypeConverter;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.view.ImportStreamHandler;
import org.alfresco.service.cmr.view.ImporterException;
import org.alfresco.service.cmr.view.ImporterProgress;
import org.alfresco.service.cmr.view.ImporterService;
import org.alfresco.service.cmr.view.Location;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ParameterCheck;
import org.alfresco.util.debug.NodeStoreInspector;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StringUtils;


/**
 * Default implementation of the Importer Service
 *  
 * @author David Caruana
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
    private SearchService searchService;
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
     * @param searchService the service to perform path searches
     */
    public void setSearchService(SearchService searchService)
    {
        this.searchService = searchService;
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
    /**
     * @param namespaceService
     */
    public void setNamespaceService(NamespaceService namespaceService)
    {
        this.namespaceService = namespaceService;
    }

    /* (non-Javadoc)
     * @see org.alfresco.service.cmr.view.ImporterService#importView(java.io.InputStreamReader, org.alfresco.service.cmr.view.Location, java.util.Properties, org.alfresco.service.cmr.view.ImporterProgress)
     */
    public void importView(Reader viewReader, Location location, Properties configuration, ImporterProgress progress)
    {
        NodeRef nodeRef = getNodeRef(location, configuration);
        QName childAssocType = getChildAssocType(location, configuration);
        performImport(nodeRef, childAssocType, viewReader, new DefaultStreamHandler(), configuration, progress);       
    }

    /* (non-Javadoc)
     * @see org.alfresco.service.cmr.view.ImporterService#importView(java.io.InputStreamReader, org.alfresco.service.cmr.view.ImportStreamHandler, org.alfresco.service.cmr.view.Location, java.util.Properties, org.alfresco.service.cmr.view.ImporterProgress)
     */
    public void importView(Reader viewReader, ImportStreamHandler streamHandler, Location location, Properties configuration, ImporterProgress progress) throws ImporterException
    {
        NodeRef nodeRef = getNodeRef(location, configuration);
        QName childAssocType = getChildAssocType(location, configuration);
        performImport(nodeRef, childAssocType, viewReader, streamHandler, configuration, progress);

        // TODO: Remove
        System.out.println(NodeStoreInspector.dumpNodeStore(nodeService, nodeRef.getStoreRef()));
    }
    
    /**
     * Get Node Reference from Location
     *  
     * @param location the location to extract node reference from
     * @param configuration import configuration
     * @return node reference
     */
    private NodeRef getNodeRef(Location location, Properties configuration)
    {
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
            List<NodeRef> nodeRefs = searchService.selectNodes(nodeRef, path, null, namespaceService, false);
            if (nodeRefs.size() == 0)
            {
                throw new ImporterException("Path " + path + " within node " + nodeRef + " does not exist - the path must resolve to a valid location");
            }
            if (nodeRefs.size() > 1)
            {
                throw new ImporterException("Path " + path + " within node " + nodeRef + " found too many locations - the path must resolve to one location");
            }
            nodeRef = nodeRefs.get(0);
        }
    
        // TODO: Check Node actually exists
        
        return nodeRef;
    }
    
    /**
     * Get the child association type from location
     * 
     * @param location the location to extract child association type from
     * @param configuration import configuration
     * @return the child association type
     */
    private QName getChildAssocType(Location location, Properties configuration)
    {
        // Establish child association type to import under
        NodeRef nodeRef = getNodeRef(location, configuration);
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
        return childAssocType;
    }

    /**
     * Perform the actual import
     * 
     * @param nodeRef node reference to import under
     * @param childAssocType the child association type to import under
     * @param inputStream the input stream to import from
     * @param streamHandler the content property import stream handler
     * @param configuration import configuration
     * @param progress import progress
     */
    private void performImport(NodeRef nodeRef, QName childAssocType, Reader viewReader, ImportStreamHandler streamHandler, Properties configuration, ImporterProgress progress)
    {
        ParameterCheck.mandatory("Node Reference", nodeRef);
        ParameterCheck.mandatory("Child Assoc Type", childAssocType);
        ParameterCheck.mandatory("View Reader", viewReader);
        ParameterCheck.mandatory("Stream Handler", streamHandler);
        Importer defaultImporter = new DefaultImporter(nodeRef, childAssocType, configuration, streamHandler, progress);
        viewParser.parse(viewReader, defaultImporter);
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
        private ImporterProgress progress;
        private ImportStreamHandler streamHandler;

        // Flush threshold
        private int flushThreshold = 500;
        private int flushCount = 0;
        
        
        /**
         * Construct
         * 
         * @param rootRef
         * @param rootAssocType
         * @param configuration
         * @param progress
         */
        private DefaultImporter(NodeRef rootRef, QName rootAssocType, Properties configuration, ImportStreamHandler streamHandler, ImporterProgress progress)
        {
            this.rootRef = rootRef;
            this.rootAssocType = rootAssocType;
            this.configuration = configuration;
            this.progress = progress;
            this.streamHandler = streamHandler;
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
                childName = bindPlaceHolder(childName, configuration);
                childName = QName.createValidLocalName(childName);
                childQName = QName.createQName(childName, namespaceService); 
            }
            else
            {
                Map<QName, Serializable> typeProperties = context.getProperties(nodeType.getName());
                String name = (String)typeProperties.get(ContentModel.PROP_NAME);
                if (name == null || name.length() == 0)
                {
                    throw new ImporterException("Cannot import node of type " + nodeType.getName() + " - it does not have a name");
                }
                
                name = bindPlaceHolder(name, configuration);
                String localName = QName.createValidLocalName(name);
                childQName = QName.createQName(assocType.getNamespaceURI(), localName);
            }
            
            // Build initial map of properties
            Map<QName, Serializable> initialProperties = new HashMap<QName, Serializable>();
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
                    Map<QName, Serializable> aspectProperties = context.getProperties(aspect);
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
            
            // Do we need to flush?
            flushCount++;
            if (flushCount > flushThreshold)
            {
                AlfrescoTransactionSupport.flush();
                flushCount = 0;
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
                // since there really isn't any content associated with the node (yet), we should
                // remove the URL from the node.  It will be set after the content has been
                // uploaded
                nodeService.setProperty(nodeRef, ContentModel.PROP_CONTENT_URL, null);
        
                // import the content from the url
                InputStream contentStream = streamHandler.importStream(contentUrl);
                ContentWriter writer = contentService.getUpdatingWriter(nodeRef);
                writer.putContent(contentStream);
                reportContentCreated(nodeRef, contentUrl);
            }
        }

        /**
         * Bind properties
         * 
         * @param properties
         * @return
         */
        private Map<QName, Serializable> bindProperties(Map<QName, Serializable> properties)
        {
            Map<QName, Serializable> boundProperties = new HashMap<QName, Serializable>(properties.size());
            for (QName property : properties.keySet())
            {
                // get property definition
                PropertyDefinition propDef = dictionaryService.getProperty(property);
                if (propDef == null)
                {
                    throw new ImporterException("Property " + property + " does not exist in the repository dictionary");
                }
                
                // bind property value to configuration and convert to appropriate type
                Serializable value = properties.get(property);
                if (value instanceof Collection)
                {
                    List<Serializable> boundCollection = new ArrayList<Serializable>();
                    for (String collectionValue : (Collection<String>)value)
                    {
                        String strValue = bindPlaceHolder(collectionValue, configuration);
                        boundCollection.add(strValue);
                    }
                    value = (Serializable)DefaultTypeConverter.INSTANCE.convert(propDef.getDataType(), boundCollection);
                }
                else
                {
                    value = bindPlaceHolder((String)value, configuration);
                    value = (Serializable)DefaultTypeConverter.INSTANCE.convert(propDef.getDataType(), value);
                }
                boundProperties.put(property, value);
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


    /**
     * Default Import Stream Handler
     * 
     * @author David Caruana
     */
    private static class DefaultStreamHandler
        implements ImportStreamHandler
    {
        /* (non-Javadoc)
         * @see org.alfresco.service.cmr.view.ImportStreamHandler#importStream(java.lang.String)
         */
        public InputStream importStream(String url)
        {
            ResourceLoader loader = new DefaultResourceLoader();
            Resource resource = loader.getResource(url);
            if (resource.exists() == false)
            {
                throw new ImporterException("Content URL " + url + " does not exist.");
            }
            
            try
            {
                return resource.getInputStream();
            }
            catch(IOException e)
            {
                throw new ImporterException("Failed to retrieve input stream for content URL " + url);
            }
        }
    }


}
