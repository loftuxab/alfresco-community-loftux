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

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.service.cmr.repository.ContentIOException;
import org.alfresco.service.cmr.repository.ContentReader;

public class RepositoryContentResponse implements ContentResponse
{

    protected ContentReader contentReader;
    
    public RepositoryContentResponse(ContentReader contentReader)
    {
        if(contentReader == null)
        {
            throw new AlfrescoRuntimeException("RepositoryContentResponse cannot be created without a ContentReader");
        }
        this.contentReader = contentReader;
    }
    
    @Override
    public long getSize()
    {
        return contentReader.getSize();
    }

    @Override
    public void emitContent(OutputStream out) throws IOException, ContentIOException
    {
        contentReader.getContent(out);
    }

    @Override
    public InputStream getContentInputStream() throws ContentIOException
    {
        return contentReader.getContentInputStream();
    }

    @Override
    public void releaseResources()
    {
    }

}
