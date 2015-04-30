/*
 * Copyright 2005-2014 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.officeservices.metadata;

import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.poi.openxml4j.opc.OPCPackage;

public interface OOXMLFileProcessor
{

    boolean appliesTo(NodeRef nodeRef);

    boolean appliesTo(OPCPackage pkg, NodeRef nodeRef);
    
    ContentFilterProcessingResult execute(OPCPackage pkg, NodeRef nodeRef);

}
