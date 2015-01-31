/*
 * Copyright (C) 2005-2015 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
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
