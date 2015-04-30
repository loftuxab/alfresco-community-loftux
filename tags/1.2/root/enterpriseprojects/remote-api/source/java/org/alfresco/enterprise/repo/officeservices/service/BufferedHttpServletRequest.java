/*
 * Copyright 2005-2015 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.officeservices.service;

import java.io.IOException;
import java.util.LinkedList;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.chemistry.opencmis.server.shared.ThresholdOutputStream;
import org.apache.chemistry.opencmis.server.shared.ThresholdOutputStreamFactory;
import org.springframework.util.FileCopyUtils;

/**
 * Buffered wrapper for request.  
 * 
 * @since 5.0.1
 */
public class BufferedHttpServletRequest extends HttpServletRequestWrapper
{

	private ThresholdOutputStreamFactory streamFactory;
    
	private HttpServletRequest request;
    
	private ThresholdOutputStream bufferStream;
    
	private LinkedList<ServletInputStream> providedInputStreams = new LinkedList<ServletInputStream>();
    
    public BufferedHttpServletRequest(HttpServletRequest request, ThresholdOutputStreamFactory streamFactory)
    {
        super(request);
        this.request = request;
        this.streamFactory = streamFactory;
    }

    private void bufferInputStream() throws IOException
    {
        ThresholdOutputStream bufferStream = streamFactory.newOutputStream();

        try
        {
            FileCopyUtils.copy(request.getInputStream(), bufferStream);
        }
        catch (IOException e)
        {
            bufferStream.destroy(); // remove temp file
            throw e;
        }
        this.bufferStream = bufferStream;
    }
    
    @Override
    public synchronized ServletInputStream getInputStream() throws IOException
    {
        if (bufferStream == null)
        {
            try
            {
                bufferInputStream();
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        ServletInputStream result = new AlfrescoServletInputStream(bufferStream.getInputStream());
        providedInputStreams.add(result);
        return result;
    }
    
    public synchronized void close()
    {
    	for(ServletInputStream sis : providedInputStreams)
    	{
            try
            {
                sis.reset();
            }
            catch (IOException ioe)
            {
                ; // ignore
            }
    	}
    	providedInputStreams.clear();
        if (bufferStream != null)
        {
            try
            {
                bufferStream.close();
            }
            catch (IOException ioe)
            {
                ; // ignore
            }
            bufferStream.destroy();
            bufferStream = null;
        }
    }

}
