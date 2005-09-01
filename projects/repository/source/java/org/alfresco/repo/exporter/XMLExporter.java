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

import java.io.InputStream;
import java.io.Serializable;

import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.Path;
import org.alfresco.service.cmr.repository.datatype.ValueConverter;
import org.alfresco.service.cmr.view.Exporter;
import org.alfresco.service.cmr.view.ExporterException;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;


/**
 * Exporter that exports Repository information to XML (Alfresco Repository View Schema)
 * 
 * @author David Caruana
 */
/*package*/ class XMLExporter
    implements Exporter
{
    private final static String VIEW_LOCALNAME = "view";
    private final static QName VIEW_QNAME = QName.createQName(NamespaceService.REPOSITORY_VIEW_PREFIX, VIEW_LOCALNAME);

    private final static String CHILDNAME_LOCALNAME = "childName";
    private final static QName CHILDNAME_QNAME = QName.createQName(NamespaceService.REPOSITORY_VIEW_PREFIX, CHILDNAME_LOCALNAME);
    
    private NamespaceService namespaceService;
    private DictionaryService dictionaryService;
    private NodeService nodeService;
    private ContentHandler contentHandler;
    

    /**
     * Set the namspace service
     * 
     * @param namespaceService  the namespace service
     */
    public void setNamespaceService(NamespaceService namespaceService)
    {
        this.namespaceService = namespaceService;
    }

    /**
     * Set the dictionary service
     * 
     * @param dictionaryService  the dictionary service
     */
    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }

    /**
     * Set the node service
     * 
     * @param nodeService  the node service
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    /**
     * Set the content handler
     * 
     * @param contentHandler  the content handler call-back
     */
    public void setContentHandler(ContentHandler contentHandler)
    {
        this.contentHandler = contentHandler;
    }
    
    
    /* (non-Javadoc)
     * @see org.alfresco.service.cmr.view.Exporter#start()
     */
    public void start()
    {
        try
        {
            contentHandler.startDocument();
            contentHandler.startPrefixMapping(NamespaceService.REPOSITORY_VIEW_PREFIX, NamespaceService.REPOSITORY_VIEW_1_0_URI);
            contentHandler.startElement(NamespaceService.REPOSITORY_VIEW_PREFIX, VIEW_LOCALNAME, VIEW_QNAME.toPrefixString(), null);
        }
        catch (SAXException e)
        {
            throw new ExporterException("Failed to process export start event", e);
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.service.cmr.view.Exporter#startNamespace(java.lang.String, java.lang.String)
     */
    public void startNamespace(String prefix, String uri)
    {
        try
        {
            contentHandler.startPrefixMapping(prefix, uri);
        }
        catch (SAXException e)
        {
            throw new ExporterException("Failed to process start namespace event - prefix " + prefix + " uri " + uri, e);
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.service.cmr.view.Exporter#endNamespace(java.lang.String)
     */
    public void endNamespace(String prefix)
    {
        try
        {
            contentHandler.endPrefixMapping(prefix);
        }
        catch (SAXException e)
        {
            throw new ExporterException("Failed to process end namespace event - prefix " + prefix, e);
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.service.cmr.view.Exporter#startNode(org.alfresco.service.cmr.repository.NodeRef)
     */
    public void startNode(NodeRef nodeRef)
    {
        try
        {
            Path path = nodeService.getPath(nodeRef);
            String childName = path.last().getElementString();
            QName childQName = QName.createQName(childName);
            String prefix = namespaceService.getPrefixes(childQName.getNamespaceURI()).iterator().next();
            QName prefixedQName = QName.createQName(prefix, childQName.getLocalName(), namespaceService);
            
            AttributesImpl attrs = new AttributesImpl(); 
            attrs.addAttribute(NamespaceService.REPOSITORY_VIEW_1_0_URI, CHILDNAME_LOCALNAME, CHILDNAME_QNAME.toPrefixString(), null, prefixedQName.toPrefixString());
            
            QName type = nodeService.getType(nodeRef);
            contentHandler.startElement(type.getNamespaceURI(), type.getLocalName(), type.toPrefixString(), attrs);
        }
        catch (SAXException e)
        {
            throw new ExporterException("Failed to process start node event - node ref " + nodeRef.toString(), e);
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.service.cmr.view.Exporter#endNode(org.alfresco.service.cmr.repository.NodeRef)
     */
    public void endNode(NodeRef nodeRef)
    {
        try
        {
            QName type = nodeService.getType(nodeRef);
            contentHandler.endElement(type.getNamespaceURI(), type.getLocalName(), type.toPrefixString());
        }
        catch (SAXException e)
        {
            throw new ExporterException("Failed to process end node event - node ref " + nodeRef.toString(), e);
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.service.cmr.view.Exporter#startAspect(org.alfresco.service.cmr.repository.NodeRef, org.alfresco.service.namespace.QName)
     */
    public void startAspect(NodeRef nodeRef, QName aspect)
    {
        try
        {
            contentHandler.startElement(aspect.getNamespaceURI(), aspect.getLocalName(), aspect.toPrefixString(), null);
        }
        catch (SAXException e)
        {
            throw new ExporterException("Failed to process start aspect event - node ref " + nodeRef.toString() + "; aspect " + aspect.toPrefixString(), e);
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.service.cmr.view.Exporter#endAspect(org.alfresco.service.cmr.repository.NodeRef, org.alfresco.service.namespace.QName)
     */
    public void endAspect(NodeRef nodeRef, QName aspect)
    {
        try
        {
            contentHandler.endElement(aspect.getNamespaceURI(), aspect.getLocalName(), aspect.toPrefixString());
        }
        catch (SAXException e)
        {
            throw new ExporterException("Failed to process end aspect event - node ref " + nodeRef.toString() + "; aspect " + aspect.toPrefixString(), e);
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.service.cmr.view.Exporter#startProperty(org.alfresco.service.cmr.repository.NodeRef, org.alfresco.service.namespace.QName)
     */
    public void startProperty(NodeRef nodeRef, QName property)
    {
        try
        {
            contentHandler.startElement(property.getNamespaceURI(), property.getLocalName(), property.toPrefixString(), null);
        }
        catch (SAXException e)
        {
            throw new ExporterException("Failed to process start property event - nodeRef " + nodeRef + "; property " + property.toPrefixString(), e);
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.service.cmr.view.Exporter#endProperty(org.alfresco.service.cmr.repository.NodeRef, org.alfresco.service.namespace.QName)
     */
    public void endProperty(NodeRef nodeRef, QName property)
    {
        try
        {
            contentHandler.endElement(property.getNamespaceURI(), property.getLocalName(), property.toPrefixString());
        }
        catch (SAXException e)
        {
            throw new ExporterException("Failed to process end property event - nodeRef " + nodeRef + "; property " + property.toPrefixString(), e);
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.service.cmr.view.Exporter#value(org.alfresco.service.cmr.repository.NodeRef, org.alfresco.service.namespace.QName, java.io.Serializable)
     */
    public void value(NodeRef nodeRef, QName property, Serializable value)
    {
        if (value != null)
        {
            String strValue = null;
            
            try
            {
                PropertyDefinition propDef = dictionaryService.getProperty(property);
                Object objValue = ValueConverter.convert(propDef.getDataType(), value);
                strValue = objValue.toString();
            }
            catch(UnsupportedOperationException e)
            {
                // TODO: Either abort or log warning according to configuration
                strValue = value.toString();
            }
            
            try
            {
                contentHandler.characters(strValue.toCharArray(), 0, strValue.length());
            }
            catch (SAXException e)
            {
                throw new ExporterException("Failed to process value - property " + property.toPrefixString(), e);
            }
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.service.cmr.view.Exporter#content(org.alfresco.service.cmr.repository.NodeRef, org.alfresco.service.namespace.QName, java.io.InputStream)
     */
    public void content(NodeRef nodeRef, QName property, InputStream content)
    {
        // TODO: Base64 encode content and send out via Content Handler
    }

    /* (non-Javadoc)
     * @see org.alfresco.service.cmr.view.Exporter#startAssoc(org.alfresco.service.cmr.repository.NodeRef, org.alfresco.service.namespace.QName)
     */
    public void startAssoc(NodeRef nodeRef, QName assoc)
    {
        try
        {
            contentHandler.startElement(assoc.getNamespaceURI(), assoc.getLocalName(), assoc.toPrefixString(), null);
        }
        catch (SAXException e)
        {
            throw new ExporterException("Failed to process start assoc event - nodeRef " + nodeRef + "; association " + assoc.toPrefixString(), e);
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.service.cmr.view.Exporter#endAssoc(org.alfresco.service.cmr.repository.NodeRef, org.alfresco.service.namespace.QName)
     */
    public void endAssoc(NodeRef nodeRef, QName assoc)
    {
        try
        {
            contentHandler.endElement(assoc.getNamespaceURI(), assoc.getLocalName(), assoc.toPrefixString());
        }
        catch (SAXException e)
        {
            throw new ExporterException("Failed to process end assoc event - nodeRef " + nodeRef + "; association " + assoc.toPrefixString(), e);
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.service.cmr.view.Exporter#end()
     */
    public void end()
    {
        try
        {
            contentHandler.endElement(NamespaceService.REPOSITORY_VIEW_PREFIX, VIEW_LOCALNAME, VIEW_QNAME.toPrefixString());
            contentHandler.endPrefixMapping(NamespaceService.REPOSITORY_VIEW_PREFIX);
            contentHandler.endDocument();
        }
        catch (SAXException e)
        {
            throw new ExporterException("Failed to process end export event", e);
        }
    }

}
