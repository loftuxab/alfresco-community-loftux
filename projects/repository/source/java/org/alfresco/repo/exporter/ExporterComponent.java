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
package org.alfresco.repo.exporter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.view.ExportStreamHandler;
import org.alfresco.service.cmr.view.Exporter;
import org.alfresco.service.cmr.view.ExporterException;
import org.alfresco.service.cmr.view.ExporterService;
import org.alfresco.service.cmr.view.ImporterException;
import org.alfresco.service.cmr.view.Location;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ParameterCheck;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;


/**
 * Default implementation of the Exporter Service.
 * 
 * @author David Caruana
 */
public class ExporterComponent
    implements ExporterService
{
    // Supporting services
    private NamespaceService namespaceService;
    private DictionaryService dictionaryService;
    private NodeService nodeService;
    private SearchService searchService;
    private ContentService contentService;

    /** The list of namespace URIs to exclude from the export */
    private String[] excludedURIs = new String[] { NamespaceService.SYSTEM_MODEL_1_0_URI, NamespaceService.REPOSITORY_VIEW_1_0_URI };
    
    
    /**
     * @param nodeService  the node service
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    /**
     * @param searchService  the service to perform path searches
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
    public void setNamespaceService(NamespaceService namespaceService)
    {
        this.namespaceService = namespaceService;
    }

    /**
     * @param excludedURIs  the URIs to exclude from the export
     */
    public void setExcludedURIs(String[] excludedURIs)
    {
        this.excludedURIs = excludedURIs;
    }
    
    
    /* (non-Javadoc)
     * @see org.alfresco.service.cmr.view.ExporterService#exportView(java.io.OutputStream, org.alfresco.service.cmr.view.Location, boolean, org.alfresco.service.cmr.view.Exporter)
     */
    public void exportView(OutputStream output, Location location, boolean exportChildren, Exporter progress)
    {
        ParameterCheck.mandatory("Output Stream", output);
        export(location, createXMLExporter(output), exportChildren, progress);
    }

    
    /* (non-Javadoc)
     * @see org.alfresco.service.cmr.view.ExporterService#exportView(java.io.OutputStream, org.alfresco.service.cmr.view.ExportStreamHandler, org.alfresco.service.cmr.view.Location, boolean, org.alfresco.service.cmr.view.Exporter)
     */
    public void exportView(OutputStream output, ExportStreamHandler streamHandler, Location location, boolean exportChildren, Exporter progress)
    {
        ParameterCheck.mandatory("Output Stream", output);
        ParameterCheck.mandatory("Content Handler", streamHandler);

        // Construct a URL Exporter (wrapped around an XML Exporter)
        Exporter xmlExporter = createXMLExporter(output);
        URLExporter urlExporter = new URLExporter(xmlExporter, streamHandler);
        
        // Export        
        export(location, urlExporter, exportChildren, progress);        
    }


    /**
     * Create an XML Exporter that exports repository information to the specified
     * output stream in xml format.
     * 
     * @param output  the output stream to write to
     * @return  the xml exporter
     */
    private Exporter createXMLExporter(OutputStream output)
    {
        // Construct an XML Output Stream Serializer
        OutputFormat format = new OutputFormat("xml", "UTF-8", true);
        format.setLineWidth(9999);
        XMLSerializer serializer = new XMLSerializer(output, format);

        // Construct an XML Exporter
        XMLExporter xmlExporter = new XMLExporter();
        xmlExporter.setNamespaceService(namespaceService);
        xmlExporter.setDictionaryService(dictionaryService);
        xmlExporter.setNodeService(nodeService);
        xmlExporter.setContentHandler(serializer);

        return xmlExporter;        
    }

    
    /**
     * Perform the Export
     * 
     * @param location  the location within the Repository to export
     * @param exporter  the exporter to perform the actual export
     * @param exportChildren  export children as well?
     * @param progress  exporter callback for tracking progress of export
     */
    private void export(Location location, Exporter exporter, boolean exportChildren, Exporter progress)
    {
        ParameterCheck.mandatory("Location", location);
        ParameterCheck.mandatory("Exporter", exporter);
        
        NodeRef nodeRef = getNodeRef(location);

        ChainedExporter chainedExporter = new ChainedExporter(new Exporter[] {exporter, progress});
        ExportNavigator navigator = new ExportNavigator(nodeRef, chainedExporter, exportChildren);
        navigator.walk();        
    }
    
    
    /**
     * Responsible for navigating the Repository from specified location and invoking
     * the provided exporter call-back for the actual export implementation.
     * 
     * @author David Caruana
     */
    private class ExportNavigator
    {
        private NodeRef nodeRef;
        private Exporter exporter;
        private boolean children;

        /**
         * Construct.
         * 
         * @param nodeRef  the starting point node reference
         * @param exporter  the export
         * @param children  export children as well?
         */
        private ExportNavigator(NodeRef nodeRef, Exporter exporter, boolean children)
        {
            this.nodeRef = nodeRef;
            this.exporter = exporter;
            this.children = children;
        }
        
        /**
         * Start navigation
         */
        private void walk()
        {
            exporter.start();
            walkStartNamespaces();

            List<ChildAssociationRef> childAssocs = nodeService.getChildAssocs(nodeRef);
            for (ChildAssociationRef childAssoc : childAssocs)
            {
                walkNode(childAssoc.getChildRef());
            }

            walkEndNamespaces();
            exporter.end();
        }
        
        
        /**
         * Call-backs for start of Namespace scope
         */
        private void walkStartNamespaces()
        {
            Collection<String> prefixes = namespaceService.getPrefixes();
            for (String prefix : prefixes)
            {
                if (prefix != null && prefix.length() > 0)
                {
                    String uri = namespaceService.getNamespaceURI(prefix);
                    if (!excludedURI(uri))
                    {
                        exporter.startNamespace(prefix, uri);
                    }
                }
            }
        }

        
        /**
         * Call-backs for end of Namespace scope
         */
        private void walkEndNamespaces()
        {
            Collection<String> prefixes = namespaceService.getPrefixes();
            for (String prefix : prefixes)
            {
                if (prefix != null && prefix.length() > 0)
                {
                    String uri = namespaceService.getNamespaceURI(prefix);
                    if (!excludedURI(uri))
                    {
                        exporter.endNamespace(prefix);
                    }
                }
            }
        }

        
        /**
         * Navigate a Node.
         * 
         * @param nodeRef  the node to navigate
         */
        private void walkNode(NodeRef nodeRef)
        {
            // Export node (but only if it's not excluded from export)
            QName type = nodeService.getType(nodeRef);
            if (excludedURI(type.getNamespaceURI()))
            {
                return;
            }
            exporter.startNode(nodeRef);

            // Export node aspects
            Set<QName> aspects = nodeService.getAspects(nodeRef);
            for (QName aspect : aspects)
            {
                if (!excludedURI(aspect.getNamespaceURI()))                
                {
                    exporter.startAspect(nodeRef, aspect);
                    exporter.endAspect(nodeRef, aspect);
                }
            }
            
            // Export node properties
            Map<QName, Serializable> properties = nodeService.getProperties(nodeRef);
            for (QName property : properties.keySet())
            {
                if (excludedURI(property.getNamespaceURI()))
                {
                    continue;
                }
                exporter.startProperty(nodeRef, property);
                
                // TODO: This should test for datatype.content
                if (dictionaryService.isSubClass(type, ContentModel.TYPE_CMOBJECT) && property.equals(ContentModel.PROP_CONTENT_URL))
                {
                    ContentReader reader = contentService.getReader(nodeRef);
                    if (reader.exists())
                    {
                        InputStream inputStream = reader.getContentInputStream();
                        try
                        {
                            exporter.content(nodeRef, property, inputStream);
                        }
                        finally
                        {
                            try
                            {
                                inputStream.close();
                            }
                            catch(IOException e)
                            {
                                throw new ExporterException("Failed to export node content for node " + nodeRef, e);
                            }
                        }
                    }
                }
                else
                {
                    exporter.value(nodeRef, property, properties.get(property));
                }
                
                exporter.endProperty(nodeRef, property);
            }
            
            // Export node children
            if (children)
            {
                List<ChildAssociationRef> childAssocs = nodeService.getChildAssocs(nodeRef);
                for (int i = 0; i < childAssocs.size(); i++)
                {
                    ChildAssociationRef childAssoc = childAssocs.get(i);
                    QName childAssocType = childAssoc.getTypeQName();
                    if (excludedURI(childAssocType.getNamespaceURI()))
                    {
                        continue;
                    }

                    if (i == 0 || childAssocs.get(i - 1).getTypeQName().equals(childAssocType) == false)
                    {
                        exporter.startAssoc(nodeRef, childAssocType);
                    }
                    
                    if (!excludedURI(childAssoc.getQName().getNamespaceURI()))
                    {
                        walkNode(childAssoc.getChildRef());
                    }
                    
                    if (i == childAssocs.size() - 1 || childAssocs.get(i + 1).getTypeQName().equals(childAssocType) == false)
                    {
                        exporter.endAssoc(nodeRef, childAssocType);
                    }
                }
            }
            
            // TODO: Export node associations

            // Signal end of node
            exporter.endNode(nodeRef);
        }

        
        /**
         * Is the specified URI an excluded URI?
         * 
         * @param uri  the URI to test
         * @return  true => it's excluded from the export
         */
        private boolean excludedURI(String uri)
        {
            for (String excludedURI : excludedURIs)
            {
                if (uri.equals(excludedURI))
                {
                    return true;
                }
            }
            return false;
        }

    }
    

    /**
     * Get the Node Ref from the specified Location
     * 
     * @param location  the location
     * @return  the node reference
     */
    private NodeRef getNodeRef(Location location)
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
    
    
}
