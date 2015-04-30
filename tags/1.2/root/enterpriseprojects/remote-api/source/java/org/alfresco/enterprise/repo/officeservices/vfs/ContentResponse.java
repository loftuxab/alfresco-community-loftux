/*
 * Copyright 2005-2014 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.officeservices.vfs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.alfresco.service.cmr.repository.ContentIOException;

public interface ContentResponse
{

    long getSize();

    void emitContent(OutputStream out) throws IOException, ContentIOException;

    InputStream getContentInputStream() throws IOException, ContentIOException;
    
    void releaseResources();

}
