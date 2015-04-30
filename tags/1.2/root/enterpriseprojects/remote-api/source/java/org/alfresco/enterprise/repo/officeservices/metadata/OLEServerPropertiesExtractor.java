package org.alfresco.enterprise.repo.officeservices.metadata;

import java.io.IOException;
import java.text.ParseException;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.apache.log4j.Logger;
import org.apache.poi.hpsf.HPSFException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.springframework.beans.factory.InitializingBean;

import com.alfresco.officeservices.docproc.DocumentMetadataProcessor;
import com.alfresco.officeservices.docproc.DocumentProperty;
import com.xaldon.officeservices.datamodel.ContentTypeId;
import com.xaldon.officeservices.datamodel.FieldDefinition;

public class OLEServerPropertiesExtractor implements OLEFileProcessor, InitializingBean
{

    protected NodeService nodeService;

    protected DictionaryService dictionaryService;
    
    protected DataModelMappingConfiguration dataModelMappingConfiguration;
    
    protected ServerPropertiesProvider serverPropertiesProvider;
    
    protected Logger logger = Logger.getLogger(this.getClass());

    @Override
    public void afterPropertiesSet() throws Exception
    {
        PropertyCheck.mandatory(this, "nodeService", this.nodeService);
        PropertyCheck.mandatory(this, "dictionaryService", this.dictionaryService);
        PropertyCheck.mandatory(this, "dataModelMappingConfiguration", this.dataModelMappingConfiguration);
        PropertyCheck.mandatory(this, "serverPropertiesProvider", this.serverPropertiesProvider);
    }

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
    public boolean appliesTo(POIFSFileSystem oleFS, NodeRef nodeRef)
    {
        return this.appliesTo(nodeRef) && DocumentMetadataProcessor.hasServerProperties(oleFS);
    }

    @Override
    public ContentFilterProcessingResult execute(POIFSFileSystem oleFS, NodeRef nodeRef)
    {
        if(nodeRef == null)
        {
            return ContentFilterProcessingResult.UNMODIFIED;
        }
        boolean isFirstVersion = nodeService.getProperty(nodeRef, ContentModel.PROP_CONTENT) == null;
        Map<QName, DocumentProperty> propertyValuesBefore = null;
        if(!isFirstVersion)
        {
            propertyValuesBefore = getPropertiesMapping(nodeRef);
        }
        try
        {
            String contentTypeIdStr = DocumentMetadataProcessor.extractContentTypeId(oleFS);
            if(isFirstVersion && (contentTypeIdStr != null))
            {
                ContentTypeId contentTypeId = null;
                try
                {
                    contentTypeId = ContentTypeId.parse(contentTypeIdStr);
                }
                catch(ParseException pe)
                {
                    if(logger.isDebugEnabled())
                    {
                        logger.debug("Invalid ContentTypeId embedded in new Document",pe);
                    }
                }
                if(contentTypeId != null)
                {
                    QName newTypeName = serverPropertiesProvider.getAlfrescoType(contentTypeId);
                    if((newTypeName != null) && dataModelMappingConfiguration.isInstantiable(newTypeName))
                    {
                        nodeService.setType(nodeRef, newTypeName);
                    }
                }
            }
            Map<QName, FieldDefinition> propertyMapping = serverPropertiesProvider.getPropertyMapping(nodeRef);
            Map<String, Object> propertyValuesAfter = DocumentMetadataProcessor.extractServerProperties(oleFS);
            String titleAfter = DocumentMetadataProcessor.extractTitle(oleFS);
            ContentPostProcessor postProcessor = new OLEServerPropertiesExtractionPostProcessor(propertyMapping, propertyValuesBefore, propertyValuesAfter, titleAfter, nodeService, dictionaryService, serverPropertiesProvider);
            return new ContentFilterProcessingResult(false, postProcessor);
        }
        catch ( IOException | HPSFException e)
        {
            logger.error("Error extracting metadata",e);
            return ContentFilterProcessingResult.UNMODIFIED;
        }
    }

    protected Map<QName, DocumentProperty> getPropertiesMapping(NodeRef nodeRef)
    {
        if(serverPropertiesProvider != null)
        {
            return serverPropertiesProvider.getServerPropertiesMapping(nodeRef);
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
