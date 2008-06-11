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
package org.alfresco.web.framework;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A lightweight object that describes the context of the persisters
 * against which the current user is executing.
 * 
 * The object principally stores the user id which may eventually be used
 * by our store implementations to check for user rights against the
 * accessed object.
 * 
 * The object may also eventually store role and rights information regarding
 * who they are in the end application.
 * 
 * Presently, the object also describes which "preview store" they are
 * looking at in the event that they are previewing an AVM store.  The
 * store id is placed onto this object by the Web Framework and is
 * used by the RemoteStoreModelObjectPersister.
 * 
 * @author muzquiano
 */
public class ModelPersistenceContext
{
    public static String REPO_STOREID = "REPO_STOREID";
    
    protected String userId;
    protected Map<String, Object> values;
    
    /**
     * Instantiates a new persister context.
     * 
     * @param userId the user id
     */
    public ModelPersistenceContext(String userId)
    {
        this();
        this.userId = userId;
    }

    /**
     * Instantiates a new persister context
     *
     */
    public ModelPersistenceContext()
    {
        values = new HashMap<String, Object>(4, 1.0f);
    }
    
    /**
     * Gets the user id.
     * 
     * @return the user id
     */
    public String getUserId()
    {
        return this.userId;
    }
    
    /**
     * Returns the stored value with the given key
     * 
     * @param key the key
     * 
     * @return the value
     */
    public Object getValue(String key)
    {
        return values.get(key);
    }
    
    /**
     * Stores a value with the given key
     * 
     * @param key the key
     * @param value the value
     */
    public void putValue(String key, Object value)
    {
        this.values.put(key, value);
    }
    
    /**
     * Returns the set of keys
     * 
     * @return the set< string>
     */
    public Set<String> keys()
    {
        return this.values.keySet();
    }
    
    /**
     * Returns the collection of values
     * 
     * @return the collection< object>
     */
    public Collection<Object> values()
    {
        return this.values.values();
    }

    @Override
    public String toString()
    {
        return "PersisterContext-" + userId + "-" + values.toString();
    } 
    
}
