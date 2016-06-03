package org.alfresco.module.vti.web;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletInputStream;

/**
 * Basic implementation of ServletInputStream  
 * 
 * @author alex.mukha
 * @since 4.2.3
 */
public class AlfrescoServletInputStream extends ServletInputStream
{
    private InputStream is;
    
    public AlfrescoServletInputStream(InputStream is)
    {
        this.is = is;
    }
    
    @Override
    public int read() throws IOException
    {
        return is.read();
    }
    
    @Override
    public synchronized void reset() throws IOException
    {
        if (is != null)
        {
            try
            {
                is.close();
            }
            catch (Exception e)
            {
            }
            is = null;
        }
    }
}
