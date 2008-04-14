/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
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
package org.alfresco.web.site.config;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.alfresco.web.site.model.ModelObject;

/**
 * @author muzquiano
 */
public class RuntimeConfig
{
    protected RuntimeConfig(ModelObject object)
    {
        this.object = object;
        this.map = new HashMap();
    }

    public ModelObject getObject()
    {
        return this.object;
    }

    public int size()
    {
        return this.map.size();
    }

    public boolean isEmpty()
    {
        return this.map.isEmpty();
    }

    public boolean containsKey(String key)
    {
        return this.map.containsKey(key);
    }

    public boolean containsValue(String value)
    {
        return this.map.containsValue(value);
    }

    public Object get(String key)
    {
        return this.map.get(key);
    }

    public Object put(String key, Object value)
    {
        return this.map.put(key, value);
    }

    public Object remove(String key)
    {
        return this.map.remove(key);
    }

    public void putAll(Map map)
    {
        this.map.putAll(map);
    }

    public void clear()
    {
        this.map.clear();
    }

    public Set keySet()
    {
        return this.map.keySet();
    }

    public Collection values()
    {
        return this.map.values();

    }

    public boolean equals(Object o)
    {
        if (o == null)
            return false;
        if (o instanceof RuntimeConfig)
        {
            RuntimeConfig otherConfig = (RuntimeConfig) o;
            if (this.getObject() != null)
            {
                String id = this.getObject().getId();
                if (otherConfig.getObject() != null)
                {
                    if (id.equalsIgnoreCase(otherConfig.getObject().getId()))
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public int hashCode()
    {
        return this.map.hashCode();
    }

    protected Map map = null;
    protected ModelObject object = null;

    public void merge(RuntimeConfig config)
    {
        this.putAll(config.map);
    }
}
