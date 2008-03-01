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
package org.alfresco.web.scripts;

import java.util.Set;

import org.springframework.beans.factory.InitializingBean;


/**
 * Set of Format Readers and Writers.
 * 
 * @author davidc
 */
public class FormatAdaptorSet implements InitializingBean
{
    private FormatRegistry registry;
    private Set<FormatReader<Object>> readers;
    private Set<FormatWriter<Object>> writers;

    /**
     * Sets the Format Registry
     * 
     * @param registry
     */
    public void setRegistry(FormatRegistry registry)
    {
        this.registry = registry;
    }
    
    /**
     * Sets the readers
     * 
     * @param readers
     */
    public void setReaders(Set<FormatReader<Object>> readers)
    {
        this.readers = readers;
    }

    /**
     * Sets the writers
     * 
     * @param writers
     */
    public void setWriters(Set<FormatWriter<Object>> writers)
    {
        this.writers = writers;
    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() throws Exception
    {
        if (readers != null)
        {
            for (FormatReader<Object> reader : readers)
            {
                registry.addReader(reader);
            }
        }
        if (writers != null)
        {
            for (FormatWriter<Object> writer : writers)
            {
                registry.addWriter(writer);
            }
        }
    }
    
}
