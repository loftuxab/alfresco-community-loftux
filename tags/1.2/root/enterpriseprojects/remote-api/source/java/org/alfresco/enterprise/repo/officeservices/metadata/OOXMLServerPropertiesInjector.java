/*
 * Copyright 2005-2014 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.officeservices.metadata;

import java.io.IOException;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.alfresco.service.cmr.dictionary.ClassDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.apache.log4j.Logger;
import org.apache.poi.hpsf.HPSFException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.xmlbeans.XmlException;
import org.xml.sax.SAXException;

import com.alfresco.officeservices.docproc.DocumentMetadataProcessor;
import com.alfresco.officeservices.docproc.DocumentProperty;
import com.alfresco.officeservices.docproc.ServerMetadata;
import com.xaldon.officeservices.datamodel.ContentTypeId;
import com.xaldon.officeservices.datamodel.Guid;

public class OOXMLServerPropertiesInjector implements OOXMLFileProcessor
{

    protected NodeService nodeService;
    
    protected DictionaryService dictionaryService;
    
    protected DataModelMappingConfiguration dataModelMappingConfiguration;
    
    protected ServerPropertiesProvider serverPropertiesProvider;
    
    protected Logger logger = Logger.getLogger(this.getClass());

    @Override
    public boolean appliesTo(NodeRef nodeRef)
    {
        if(nodeRef == null)
        {
            return false;
        }
        QName nodeTypeQname = nodeService.getType(nodeRef);
        return dataModelMappingConfiguration.isTypeMapped(nodeTypeQname);
    }

    @Override
    public boolean appliesTo(OPCPackage pkg, NodeRef nodeRef)
    {
        return this.appliesTo(nodeRef);
    }

    @Override
    public ContentFilterProcessingResult execute(OPCPackage pkg, NodeRef nodeRef)
    {
        if(nodeRef == null)
        {
            return ContentFilterProcessingResult.UNMODIFIED;
        }
        ServerMetadata serverMetadata = getServerMetadata(nodeRef);
        if(serverMetadata == null)
        {
            return ContentFilterProcessingResult.UNMODIFIED;
        }
        try
        {
            DocumentMetadataProcessor.embedMetadata(pkg, serverMetadata);
        }
        catch (HPSFException | IOException | OpenXML4JException | XmlException | ParserConfigurationException | SAXException e)
        {
            logger.error("Error embedding metadata",e);
        }
        return ContentFilterProcessingResult.MODIFIED;
    }

    protected ServerMetadata getServerMetadata(NodeRef nodeRef)
    {
        if(serverPropertiesProvider != null)
        {
            Map<QName, DocumentProperty> properties = serverPropertiesProvider.getServerPropertiesMapping(nodeRef);
            if(properties != null)
            {
                QName nodeTypeQname = nodeService.getType(nodeRef);
                ClassDefinition classDefinition = dictionaryService.getClass(nodeTypeQname);
                String contentTypeName = classDefinition.getTitle(dictionaryService);
                if( (contentTypeName == null) || contentTypeName.isEmpty())
                {
                    contentTypeName = nodeTypeQname.getLocalName();
                }
                ContentTypeId contentTypeId = ContentTypeId.DOCUMENT.getChild(Guid.parse(nodeRef.getId()));
                return new ServerMetadata(contentTypeName, contentTypeId.toString(), properties.values(), serverPropertiesProvider.getDocumentTitle(nodeRef));
            }
        }
        return null;
    }

    public NodeService getNodeService()
    {
        return nodeService;
    }

    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    public DictionaryService getDictionaryService()
    {
        return dictionaryService;
    }

    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }

    public DataModelMappingConfiguration getDataModelMappingConfiguration()
    {
        return dataModelMappingConfiguration;
    }

    public void setDataModelMappingConfiguration(DataModelMappingConfiguration dataModelMappingConfiguration)
    {
        this.dataModelMappingConfiguration = dataModelMappingConfiguration;
    }

    public ServerPropertiesProvider getServerPropertiesProvider()
    {
        return serverPropertiesProvider;
    }

    public void setServerPropertiesProvider(ServerPropertiesProvider serverPropertiesProvider)
    {
        this.serverPropertiesProvider = serverPropertiesProvider;
    }

}
