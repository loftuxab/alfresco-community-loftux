/*
 * Copyright 2005-2015 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.officeservices.service;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletInputStream;

/**
 * Basic implementation of ServletInputStream  
 * 
 * @since 5.0.1
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
    public synchronized void close() throws IOException
    {
        if (is != null)
        {
            try
            {
                is.close();
            }
            catch (Exception e)
            {
            	; // ignore
            }
            is = null;
        }
    }

}
