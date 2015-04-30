/*
 * Copyright 2005-2014 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.officeservices.metadata;

import java.io.IOException;
import java.io.PushbackInputStream;

import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.NodeRef;


public interface IOContentFilterRegistry
{

    ContentFilter getInputFilter(String mimeType, PushbackInputStream pis, NodeRef nodeRef) throws IOException;
    
    ContentFilter getOutputFilter(NodeRef nodeRef, ContentReader contentReader);

    void registerInputFilter(ContentFilter contentFilter);

    void registerOutputFilter(ContentFilter contentFilter);

    int getMaxInputPreviewByteCount();

}
