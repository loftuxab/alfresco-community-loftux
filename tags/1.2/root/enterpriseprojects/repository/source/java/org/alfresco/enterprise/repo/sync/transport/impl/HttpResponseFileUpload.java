/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.sync.transport.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.alfresco.service.cmr.remoteconnector.RemoteConnectorResponse;
import org.alfresco.service.cmr.remoteconnector.RemoteConnectorService;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.httpclient.Header;

/**
 * An implementation of {@link FileUpload} which works on a {@link RemoteConnectorService} 
 *  Response object ({@link RemoteConnectorResponse}). This is a little unusual, but 
 *  needed because we use form multipart in both directions, not just upload
 *  
 * @author Nick Burch
 * @since TODO
 */
public class HttpResponseFileUpload extends FileUpload
{
    public HttpResponseFileUpload() 
    {
        super();
    }

    /**
     * Processes an <a href="http://www.ietf.org/rfc/rfc1867.txt">RFC 1867</a>
     * compliant <code>multipart/form-data</code> stream.
     *
     * @param response The response request to be parsed.
     *
     * @return A list of <code>FileItem</code> instances parsed from the
     *         request, in the order that they were transmitted.
     *
     * @throws FileUploadException if there are problems reading/parsing
     *                             the request or storing files.
     */
    @SuppressWarnings("unchecked")
    public List<FileItem> parseRequest(RemoteConnectorResponse response)
                throws FileUploadException {
        return parseRequest(new ResponseRequestContext(response));
    }


    /**
     * Processes an <a href="http://www.ietf.org/rfc/rfc1867.txt">RFC 1867</a>
     * compliant <code>multipart/form-data</code> stream.
     *
     * @param response The response request to be parsed.
     *
     * @return An iterator to instances of <code>FileItemStream</code>
     *         parsed from the request, in the order that they were
     *         transmitted.
     *
     * @throws FileUploadException if there are problems reading/parsing
     *                             the request or storing files.
     * @throws IOException An I/O error occurred. This may be a network
     *   error while communicating with the client or a problem while
     *   storing the uploaded content.
     */
    public FileItemIterator getItemIterator(RemoteConnectorResponse response)
             throws FileUploadException, IOException {
        return super.getItemIterator(new ResponseRequestContext(response));
    }
    
    /**
     * A {@link RequestContext} wrapper around a {@link RemoteConnectorResponse}
     */
    private static class ResponseRequestContext implements RequestContext
    {
        private RemoteConnectorResponse response;
        private ResponseRequestContext(RemoteConnectorResponse response)
        {
            this.response = response;
        }
        
        @Override
        public String getCharacterEncoding()
        {
            return CloudSyncMemberNodeTransportImpl.getContentTypeEncoding(response.getContentType(), true);
        }
        @Override
        public String getContentType()
        {
            return response.getRawContentType();
        }
        @Override
        public int getContentLength()
        {
            // We may well not know... but -1 is ok!
            for (Header hdr : response.getResponseHeaders())
            {
                if (hdr.getName().equalsIgnoreCase("Content-Length"))
                {
                    return Integer.parseInt(hdr.getValue());
                }
            }
            return -1;
        }
        @Override
        public InputStream getInputStream() throws IOException
        {
            return response.getResponseBodyAsStream();
        }
    }
}
