/*
 * Copyright (C) 2005-2007 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.web.scripts.atom;

import java.io.IOException;
import java.io.OutputStream;

import org.alfresco.web.scripts.FormatWriter;
import org.alfresco.web.scripts.WebScriptException;
import org.apache.abdera.model.Base;
import org.apache.abdera.writer.Writer;


/**
 * Convert mimetypes to Atom classes.
 * 
 * @author davidc
 */
public class AtomWriter implements FormatWriter<Base>
{
    // dependencies
    protected AbderaService abderaService;
    
    private Class<? extends Base> clazz;
    private String mimetype;
    private String writerName = AbderaService.DEFAULT_WRITER;
    

    /**
     * Sets the Abdera Service
     * 
     * @param abderaService
     */
    public void setAbderaService(AbderaService abderaService)
    {
        this.abderaService = abderaService; 
    }

    /**
     * Sets the Class to convert to
     * 
     * @param clazz
     */
    public void setClass(Class<? extends Base> clazz)
    {
        this.clazz = clazz;
    }
    
    /**
     * Sets the mimetype to convert from
     * 
     * @param mimetype
     */
    public void setMimetype(String mimetype)
    {
        this.mimetype = mimetype;
    }
    
    /**
     * Sets the Atom Writer
     * 
     * @param writerName
     */
    public void setWriter(String writerName)
    {
        this.writerName = writerName;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.FormatWriter#getSourceClass()
     */
    public Class<? extends Base> getSourceClass()
    {
        return clazz;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.FormatWriter#getDestinationMimetype()
     */
    public String getDestinationMimetype()
    {
        return mimetype;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.FormatWriter#write(java.lang.Object, java.io.Writer)
     */
    public void write(Base object, java.io.Writer output)
    {
        Writer writer = abderaService.getWriter(writerName);
        try
        {
            object.writeTo(writer, output);
        }
        catch (IOException e)
        {
            throw new WebScriptException("Failed to write atom object to mimetype '" + mimetype + "'", e);
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.FormatWriter#write(java.lang.Object, java.io.OutputStream)
     */
    public void write(Base object, OutputStream output)
    {
        Writer writer = abderaService.getWriter(writerName);
        try
        {
            object.writeTo(writer, output);
        }
        catch (IOException e)
        {
            throw new WebScriptException("Failed to write atom object to mimetype '" + mimetype + "'", e);
        }
    }

}
