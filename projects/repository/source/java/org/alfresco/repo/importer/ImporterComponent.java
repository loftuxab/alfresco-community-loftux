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
import java.util.Set;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.transaction.AlfrescoTransactionSupport;
import org.alfresco.service.cmr.dictionary.ChildAssociationDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.datatype.DefaultTypeConverter;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.view.ImportStreamHandler;
import org.alfresco.service.cmr.view.ImporterBinding;
import org.alfresco.service.cmr.view.ImporterException;
import org.alfresco.service.cmr.view.ImporterProgress;
import org.alfresco.service.cmr.view.ImporterService;
import org.alfresco.service.cmr.view.Location;
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
 */
public class ImporterComponent
    implements ImporterService
{
    // default importer
    // TODO: Allow registration of plug-in parsers (by namespace)
    private Parser viewParser;

    // supporting services
    private NamespaceService namespaceService;
    private DictionaryService dictionaryService;
    private NodeService nodeService;
    private SearchService searchService;
    private ContentService contentService;

    // binding markers    
    private static final String START_BINDING_MARKER = "${";
    private static final String END_BINDING_MARKER = "}"; 
    
    
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
    public void importView(Reader viewReader, Location location, ImporterBinding binding, ImporterProgress progress)
    {
        NodeRef nodeRef = getNodeRef(location, binding);
        QName childAssocType = getChildAssocType(location, binding);
        performImport(nodeRef, childAssocType, viewReader, new DefaultStreamHandler(), binding, progress);       
    }

    /* (non-Javadoc)
     * @see org.alfresco.service.cmr.view.ImporterService#importView(java.io.InputStreamReader, org.alfresco.service.cmr.view.ImportStreamHandler, org.alfresco.service.cmr.view.Location, java.util.Properties, org.alfresco.service.cmr.view.ImporterProgress)
     */
    public void importView(Reader viewReader, ImportStreamHandler streamHandler, Location location, ImporterBinding binding, ImporterProgress progress) throws ImporterException
    {
        NodeRef nodeRef = getNodeRef(location, binding);
        QName childAssocType = getChildAssocType(location, binding);
        performImport(nodeRef, childAssocType, viewReader, streamHandler, binding, progress);
    }
    
    /**
     * Get Node Reference from Location
     *  
     * @param location the location to extract node reference from
     * @param binding import configuration
     * @return node reference
     */
    private NodeRef getNodeRef(Location location, ImporterBinding binding)
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
            path = bindPlaceHolder(path, binding);
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
     * @param binding import configuration
     * @return the child association type
     */
    private QName getChildAssocType(Location location, ImporterBinding binding)
    {
        // Establish child association type to import under
        NodeRef nodeRef = getNodeRef(location, binding);
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
     * @param binding import configuration
     * @param progress import progress
     */
    private void performImport(NodeRef nodeRef, QName childAssocType, Reader viewReader, ImportStreamHandler streamHandler, ImporterBinding binding, ImporterProgress progress)
    {
        ParameterCheck.mandatory("Node Reference", nodeRef);
        ParameterCheck.mandatory("Child Assoc Type", childAssocType);
        ParameterCheck.mandatory("View Reader", viewReader);
        ParameterCheck.mandatory("Stream Handler", streamHandler);
        Importer defaultImporter = new DefaultImporter(nodeRef, childAssocType, binding, streamHandler, progress);
        viewParser.parse(viewReader, defaultImporter);
    }
    
    /**
     * Bind the specified value to the passed configuration values if it is a place holder
     * 
     * @param value  the value to bind
     * @param binding  the configuration properties to bind to
     * @return  the bound value
     */
    private String bindPlaceHolder(String value, ImporterBinding binding)
    {
        if (binding != null)
        {
            int iStartBinding = value.indexOf(START_BINDING_MARKER);
            while (iStartBinding != -1)
            {
                int iEndBinding = value.indexOf(END_BINDING_MARKER, iStartBinding + START_BINDING_MARKER.length());
                if (iEndBinding == -1)
                {
                    throw new ImporterException("Cannot find end marker " + END_BINDING_MARKER + " within value " + value);
                }
                
                String key = value.substring(iStartBinding + START_BINDING_MARKER.length(), iEndBinding);
                String keyValue = binding.getValue(key);
                value = StringUtils.replace(value, START_BINDING_MARKER + key + END_BINDING_MARKER, keyValue == null ? "" : keyValue);
                iStartBinding = value.indexOf(START_BINDING_MARKER);
            }
        }
        return value;
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
                String[] qnameComponents = QName.splitPrefixedQName(segments[i]);
                QName segmentQName = QName.createQName(qnameComponents[0], QName.createValidLocalName(qnameComponents[1]), namespaceService); 
                validPath.append(segmentQName.toPrefixString());
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
        private ImporterBinding binding;
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
         * @param binding
         * @param progress
         */
        private DefaultImporter(NodeRef rootRef, QName rootAssocType, ImporterBinding binding, ImportStreamHandler streamHandler, ImporterProgress progress)
        {
            this.rootRef = rootRef;
            this.rootAssocType = rootAssocType;
            this.binding = binding;
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
                childName = bindPlaceHolder(childName, binding);
                String[] qnameComponents = QName.splitPrefixedQName(childName);
                childQName = QName.createQName(qnameComponents[0], QName.createValidLocalName(qnameComponents[1]), namespaceService); 
            }
            else
            {
                Map<QName, Serializable> typeProperties = context.getProperties(nodeType.getName());
                String name = (String)typeProperties.get(ContentModel.PROP_NAME);
                if (name == null || name.length() == 0)
                {
                    throw new ImporterException("Cannot import node of type " + nodeType.getName() + " - it does not have a name");
                }
                
                name = bindPlaceHolder(name, binding);
                String localName = QName.createValidLocalName(name);
                childQName = QName.createQName(assocType.getNamespaceURI(), localName);
            }
            
            // Build initial map of properties
            Map<QName, Serializable> properties = context.getProperties();
            Map<QName, Serializable> initialProperties = bindProperties(properties);
            
            // Create initial node
            ChildAssociationRef assocRef = nodeService.createNode(parentRef, assocType, childQName, nodeType.getName(), initialProperties);
            NodeRef nodeRef = assocRef.getChildRef();
            reportNodeCreated(assocRef);
            reportPropertySet(nodeRef, initialProperties);
            
            // Apply aspects
            for (QName aspect : context.getNodeAspects())
            {
                if (nodeService.hasAspect(nodeRef, aspect) == false)
                {
                    nodeService.addAspect(nodeRef, aspect, null);   // all properties previously added
                    reportAspectAdded(nodeRef, aspect);
                }
            }

            // import content, if applicable
            for (QName propertyQName : properties.keySet())
            {
                PropertyDefinition propertyDef = dictionaryService.getProperty(propertyQName);
                if (propertyDef == null)
                {
                    // not defined in the dictionary
                    continue;
                }
                QName propertyTypeQName = propertyDef.getDataType().getName();
                if (propertyTypeQName.equals(DataTypeDefinition.CONTENT))
                {
                    importContent(nodeRef, propertyQName);
                }
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
         * Import Node Content.
         * <p>
         * The content URL, if present, will be a local URL.  This import copies the content
         * from the local URL to a server-assigned location.
         * 
         * @param context
         * @param propertyQName the name of the content-type property
         */
        private void importContent(NodeRef nodeRef, QName propertyQName)
        {
            // Extract the source location of the content
            ContentData contentData = (ContentData)nodeService.getProperty(nodeRef, propertyQName);
            String contentUrl = contentData.getContentUrl();
            if (contentUrl != null && contentUrl.length() > 0)
            {
                // remove the 'fake' content URL - it causes failures
                contentData = new ContentData(
                        null,
                        contentData.getMimetype(),
                        0L,
                        contentData.getEncoding());
                nodeService.setProperty(nodeRef, propertyQName, contentData);
                
                // import the content from the url
                InputStream contentStream = streamHandler.importStream(contentUrl);
                ContentWriter writer = contentService.getWriter(nodeRef, ContentModel.PROP_CONTENT, true);
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
                        String strValue = bindPlaceHolder(collectionValue, binding);
                        boundCollection.add(strValue);
                    }
                    value = (Serializable)DefaultTypeConverter.INSTANCE.convert(propDef.getDataType(), boundCollection);
                }
                else
                {
                    value = bindPlaceHolder((String)value, binding);
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
