/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 * 
 * As a special exception to the terms and conditions of version 2.0 of the GPL,
 * you may redistribute this Program in connection with Free/Libre and Open
 * Source Software ("FLOSS") applications as described in Alfresco's FLOSS
 * exception. You should have recieved a copy of the text describing the FLOSS
 * exception, and it is also available here:
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.web.framework.resource;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Wraps the metadata from a resource into a convenience object
 * 
 * @author muzquiano
 */
public class UnloadedResourceContentImpl implements ResourceContent
{
    public static Log logger = LogFactory
            .getLog(UnloadedResourceContentImpl.class);

    protected Resource resource = null;
    protected Throwable loaderException = null;
    protected long timestamp;

    public UnloadedResourceContentImpl(Resource resource)
    {
        this.resource = resource;
        this.timestamp = System.currentTimeMillis();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.framework.resource.ResourceContent#getResource()
     */
    public Resource getResource()
    {
        return this.resource;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.framework.resource.ResourceContent#getTimestamp()
     */
    public long getTimestamp()
    {
        return this.timestamp;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.framework.resource.ResourceContent#getId()
     */
    public String getId()
    {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.framework.resource.ResourceContent#getTypeId()
     */
    public String getTypeId()
    {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.framework.resource.ResourceContent#getProperty(java.lang.String)
     */
    public Object getProperty(String propertyName)
    {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.framework.resource.ResourceContent#getProperties()
     */
    public Map<String, Serializable> getProperties()
    {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.framework.resource.ResourceContent#isLoaded()
     */
    public boolean isLoaded()
    {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.framework.resource.ResourceContent#setLoaderException(java.lang.Throwable)
     */
    public void setLoaderException(Throwable loaderException)
    {
        this.loaderException = loaderException;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.framework.resource.ResourceContent#getLoaderException()
     */
    public Throwable getLoaderException()
    {
        return this.loaderException;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.framework.resource.ResourceContent#getJSON()
     */
    public String getJSON()
    {
        return null;
    }

}
