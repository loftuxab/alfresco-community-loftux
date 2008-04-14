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
package org.alfresco.web.scripts;

import java.util.HashMap;

import org.mozilla.javascript.Scriptable;

public class ScriptableMap<K, V> extends HashMap implements Scriptable
{
    public ScriptableMap()
    {
    }

    /**
     * @see org.mozilla.javascript.Scriptable#getClassName()
     */
    public String getClassName()
    {
        return "ScriptableMap";
    }

    /**
     * @see org.mozilla.javascript.Scriptable#get(java.lang.String,
     *      org.mozilla.javascript.Scriptable)
     */
    public Object get(String name, Scriptable start)
    {
        if ("length".equals(name))
        {
            return this.size();
        }
        else
        {
            return get(name);
        }
    }

    /**
     * @see org.mozilla.javascript.Scriptable#get(int,
     *      org.mozilla.javascript.Scriptable)
     */
    public Object get(int index, Scriptable start)
    {
        return null;
    }

    /**
     * @see org.mozilla.javascript.Scriptable#has(java.lang.String,
     *      org.mozilla.javascript.Scriptable)
     */
    public boolean has(String name, Scriptable start)
    {
        return containsKey(name);
    }

    /**
     * @see org.mozilla.javascript.Scriptable#has(int,
     *      org.mozilla.javascript.Scriptable)
     */
    public boolean has(int index, Scriptable start)
    {
        return false;
    }

    /**
     * @see org.mozilla.javascript.Scriptable#put(java.lang.String,
     *      org.mozilla.javascript.Scriptable, java.lang.Object)
     */
    public void put(String name, Scriptable start, Object value)
    {
        put(name, value);
    }

    /**
     * @see org.mozilla.javascript.Scriptable#put(int,
     *      org.mozilla.javascript.Scriptable, java.lang.Object)
     */
    public void put(int index, Scriptable start, Object value)
    {
    }

    /**
     * @see org.mozilla.javascript.Scriptable#delete(java.lang.String)
     */
    public void delete(String name)
    {
        remove(name);
    }

    /**
     * @see org.mozilla.javascript.Scriptable#delete(int)
     */
    public void delete(int index)
    {
    }

    /**
     * @see org.mozilla.javascript.Scriptable#getPrototype()
     */
    public Scriptable getPrototype()
    {
        return null;
    }

    /**
     * @see org.mozilla.javascript.Scriptable#setPrototype(org.mozilla.javascript.Scriptable)
     */
    public void setPrototype(Scriptable prototype)
    {
    }

    /**
     * @see org.mozilla.javascript.Scriptable#getParentScope()
     */
    public Scriptable getParentScope()
    {
        return null;
    }

    /**
     * @see org.mozilla.javascript.Scriptable#setParentScope(org.mozilla.javascript.Scriptable)
     */
    public void setParentScope(Scriptable parent)
    {
    }

    /**
     * @see org.mozilla.javascript.Scriptable#getIds()
     */
    public Object[] getIds()
    {
        return keySet().toArray();
    }

    /**
     * @see org.mozilla.javascript.Scriptable#getDefaultValue(java.lang.Class)
     */
    public Object getDefaultValue(Class hint)
    {
        return null;
    }

    /**
     * @see org.mozilla.javascript.Scriptable#hasInstance(org.mozilla.javascript.Scriptable)
     */
    public boolean hasInstance(Scriptable instance)
    {
        return instance instanceof ScriptableMap;
    }
}
