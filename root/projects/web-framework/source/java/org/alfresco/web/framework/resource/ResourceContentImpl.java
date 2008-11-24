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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Wraps the metadata from a resource into a convenience object
 * 
 * @author muzquiano
 */
public class ResourceContentImpl implements ResourceContent
{
    public static Log logger = LogFactory.getLog(ResourceContentImpl.class);

    protected String id = null;
    protected String typeId = null;
    protected JSONObject json = null;
    protected Resource resource = null;
    protected Map<String, Serializable> properties = null;
    protected Throwable loaderException = null;
    protected long timestamp;

    public ResourceContentImpl(Resource resource, JSONObject json)
    {
        this.resource = resource;
        this.json = json;
        this.timestamp = System.currentTimeMillis();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.site.Content#getResource()
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
     * @see org.alfresco.web.site.Content#getId()
     */
    public String getId()
    {
        try
        {
            this.id = json.getString("id");
        }
        catch (JSONException je)
        {
            logger.debug("Unable to determine type for object id: " + this.id);
        }
        return this.id;

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.site.Content#getTypeId()
     */
    public String getTypeId()
    {
        try
        {
            this.typeId = json.getString("type");
        }
        catch (JSONException je)
        {
            logger.debug("Unable to determine type for object id: " + this.id);
        }
        return this.typeId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.site.Content#getProperty(java.lang.String)
     */
    public Object getProperty(String propertyName)
    {
        return getProperties().get(propertyName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.site.Content#getProperties()
     */
    public Map<String, Serializable> getProperties()
    {
        if (this.properties == null)
        {
            this.properties = new HashMap<String, Serializable>(48, 1.0f);

            Iterator it = json.keys();
            while (it.hasNext())
            {
                String key = (String) it.next();

                if (!"properties".equals(key))
                {
                    Object value = null;
                    try
                    {
                        value = json.get(key);
                    }
                    catch (JSONException je)
                    {
                        // all we can really do is log to debug
                        logger.debug("Unable to retrieve property '" + key
                                + "' from json properties for object id: "
                                + getId(), je);
                    }
                    if (value != null)
                    {
                        if (value instanceof org.json.JSONArray)
                        {
                        }
                        else
                        {
                            this.properties.put(key, (Serializable) value);
                        }
                    }
                }
            }

            // load in "metadata" (properties) stored keys
            // this pulls in Alfresco metadata properties
            // the keys here usually are of the form
            // {http://...}content
            try
            {
                JSONObject props = json.getJSONObject("properties");
                if (props != null)
                {
                    it = props.keys();
                    while (it.hasNext())
                    {
                        String key = (String) it.next();
                        Object value = props.get(key);
                        if (value != null)
                        {
                            this.properties.put(key, (Serializable) value);
                        }
                    }
                }
            }
            catch (JSONException je)
            {
                // all we can really do is log to debug
                logger.debug(
                        "Error while reading metadata from JSON object for object id: "
                                + getId(), je);
            }

        }

        return this.properties;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.framework.resource.ResourceContent#isLoaded()
     */
    public boolean isLoaded()
    {
        return true;
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
        return this.json.toString();
    }
}
