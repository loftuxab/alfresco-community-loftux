/*
 * Copyright 2005-2014 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.officeservices.metadata;

import java.util.Map;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

import com.alfresco.officeservices.docproc.DocumentProperty;

public interface ServerPropertiesProvider extends DataModelMapper
{

    Map<QName, DocumentProperty> getServerPropertiesMapping(NodeRef nodeRef);
    
    QName getDocumentTitlePropertyName();
    
    String getDocumentTitle(NodeRef nodeRef);
    
}
