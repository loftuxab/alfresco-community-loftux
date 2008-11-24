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
 * exception. You should have received a copy of the text describing the FLOSS
 * exception, and it is also available here:
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.web.framework.resource;

import java.util.Iterator;

import org.alfresco.web.framework.exception.ResourceMetadataException;
import org.alfresco.web.site.RequestContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Abstract implementation of a resource resolver
 * 
 * @author muzquiano
 */
public abstract class AbstractAlfrescoResourceResolver extends
        AbstractResourceResolver
{
    public AbstractAlfrescoResourceResolver(Resource resource)
    {
        super(resource);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.framework.resource.ResourceResolver#getMetadata(org.alfresco.web.site.RequestContext)
     */
    public String getMetadata(RequestContext context)
            throws ResourceMetadataException
    {
        String metadata = this.getRawMetadata(context);

        // output
        String result = null;

        // convert to JSON to work with it
        JSONObject source = null;
        try
        {
            source = new JSONObject(metadata);

            JSONObject dest = new JSONObject();
            serialize(source, dest, true);

            result = dest.toString();
        }
        catch (JSONException jsonEx)
        {
            throw new ResourceMetadataException(
                    "Unable to parse JSON for resource: " + this.resource.getId(),
                    jsonEx);
        }

        return result;
    }

    protected void copy(JSONObject source, JSONObject dest, String name)
            throws JSONException
    {
        copy(source, dest, name, name);
    }

    protected void copy(JSONObject source, JSONObject dest, String sourceName,
            String destName) throws JSONException
    {
        if (source.has(sourceName))
        {
            Object obj = source.get(sourceName);
            if (obj != null)
            {
                dest.put(destName, obj);
            }
        }
    }

    protected void serialize(JSONObject source, JSONObject dest,
            boolean includeChildren) throws JSONException
    {
        // core properties
        copy(source, dest, "nodeRef", "id");
        copy(source, dest, "name", "title");
        copy(source, dest, "type");
        copy(source, dest, "size");
        copy(source, dest, "url");

        // construct the path
        String path = source.get("displayPath") + "/" + source.get("name");
        dest.put("path", path);

        // booleans
        copy(source, dest, "isContainer");
        copy(source, dest, "isDocument", "isItem");
        copy(source, dest, "isLocked");
        copy(source, dest, "isCategory");

        // file specific properties
        copy(source, dest, "mimetype");

        // toss in properties
        if (source.has("properties") && (source.get("properties") != null))
        {
            JSONObject destProps = new JSONObject();
            dest.put("properties", destProps);

            JSONObject sourceProps = source.getJSONObject("properties");
            Iterator keys = sourceProps.keys();
            while (keys.hasNext())
            {
                String key = (String) keys.next();
                String value = (String) sourceProps.get(key);
                destProps.put(key, value);
            }

            // copy in system properties
            dest.put(
                    "modified",
                    sourceProps.get("{http://www.alfresco.org/model/content/1.0}modified"));
            dest.put(
                    "modifier",
                    sourceProps.get("{http://www.alfresco.org/model/content/1.0}modifier"));
            dest.put(
                    "created",
                    sourceProps.get("{http://www.alfresco.org/model/content/1.0}created"));
            dest.put(
                    "creator",
                    sourceProps.get("{http://www.alfresco.org/model/content/1.0}creator"));

            // description
            if (sourceProps.has("{http://www.alfresco.org/model/content/1.0}description"))
            {
                dest.put(
                        "description",
                        sourceProps.get("{http://www.alfresco.org/model/content/1.0}description"));
            }
        }

        // toss in children
        if (source.has("children") && source.get("children") != null)
        {
            JSONArray destChildren = new JSONArray();
            dest.put("children", destChildren);

            JSONArray sourceChildren = source.getJSONArray("children");
            for (int i = 0; i < sourceChildren.length(); i++)
            {
                JSONObject sourceChild = sourceChildren.getJSONObject(i);

                JSONObject destChild = new JSONObject();
                serialize(sourceChild, destChild, false);

                destChildren.put(destChild);
            }
        }
    }
}
