package org.alfresco.enterprise.repo.officeservices.metadata;

import org.alfresco.service.namespace.QName;

public interface DataModelMappingConfiguration
{

    QName getRootDocumentType();

    QName getRootFolderType();
    
    boolean isTypeMapped(QName qname);
    
    boolean isAspectMapped(QName qname);

    boolean isPropertyMapped(QName qname);

    boolean isInstantiable(QName qname);

}
