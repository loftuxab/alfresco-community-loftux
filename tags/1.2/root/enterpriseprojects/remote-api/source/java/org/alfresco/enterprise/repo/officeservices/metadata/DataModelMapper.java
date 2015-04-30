package org.alfresco.enterprise.repo.officeservices.metadata;

import java.util.Collection;
import java.util.Map;

import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

import com.xaldon.officeservices.datamodel.ContentTypeDefinition;
import com.xaldon.officeservices.datamodel.ContentTypeId;
import com.xaldon.officeservices.datamodel.FieldDefinition;
import com.xaldon.officeservices.datamodel.FieldValue;
import com.xaldon.officeservices.datamodel.Guid;

public interface DataModelMapper
{

    Guid getContentTypeGuid(QName name);

    Guid getPropertyGuid(QName name);
    
    ContentTypeId getContentTypeId(QName name);
    
    ContentTypeId getContentTypeId(NodeRef nodeRef);
    
    FieldDefinition convertToFieldDefinition(PropertyDefinition propDef);
    
    Collection<ContentTypeDefinition> getAllContentTypes(boolean includeFields, boolean onlyInstantiable);
    
    ContentTypeDefinition getContentTypeDefinition(ContentTypeId contentTypeId, boolean includeFields);
    
    QName getAlfrescoType(ContentTypeId contentTypeId);
    
    Map<QName, FieldDefinition> getPropertyMapping(NodeRef nodeRef);
    
    Collection<FieldValue> getFieldValues(NodeRef nodeRef);

}
