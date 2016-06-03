package org.alfresco.module.vti.web;

import java.io.IOException;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.chemistry.opencmis.server.shared.ThresholdOutputStream;
import org.apache.chemistry.opencmis.server.shared.ThresholdOutputStreamFactory;
import org.springframework.util.FileCopyUtils;

/**
 * Buffered wrapper for request.  
 * 
 * @author alex.mukha
 * @since 4.2.3
 */
public class BufferedHttpServletRequest extends HttpServletRequestWrapper
{
    private ThresholdOutputStreamFactory streamFactory;
    private HttpServletRequest request;
    private ThresholdOutputStream bufferStream;
    private ServletInputStream contentStream;
    
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
    public ServletInputStream getInputStream() throws IOException
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
        contentStream = new AlfrescoServletInputStream(bufferStream.getInputStream());
        return contentStream;
    }
    
    public void close()
    {
        if (contentStream != null)
        {
            try
            {
                contentStream.reset();
            }
            catch (IOException ioe)
            {

            }
            contentStream = null;
        }
        
        if (bufferStream != null)
        {
            try
            {
                bufferStream.close();
            }
            catch (IOException ioe)
            {

            }
            bufferStream.destroy();
            bufferStream = null;
        }
    }
}
