/*
 * Copyright (C) 2005-2007 Alfresco Software Limited.
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

import java.io.Serializable;
import java.util.Map;

import org.alfresco.web.site.Framework;
import org.alfresco.web.site.RequestContext;
import org.alfresco.web.site.model.ModelObject;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

/**
 * @author muzquiano
 */
public class ScriptBase implements Serializable
{
    protected RequestContext context;
    protected Map<String, Object> model;
    
    
    public ScriptBase()
    {
    }
    
    public ScriptBase(RequestContext context)
    {
        this.context = context;
    }

    public RequestContext getRequestContext()
    {
        return context;
    }

    public void setModel(Map<String, Object> model)
    {
        this.model = model;
    }

    public Map<String, Object> getModel()
    {
        return this.model;
    }

    // utilities

    public static ScriptableMap toScriptableMap(RequestContext context,
            ModelObject[] modelObjects)
    {
        ScriptableMap<String, Serializable> map = new ScriptableMap<String, Serializable>();

        for (int i = 0; i < modelObjects.length; i++)
        {
            ScriptModelObject scriptModelObject = toScriptModelObject(context, modelObjects[i]);
            String id = modelObjects[i].getId();
            map.put(id, scriptModelObject);
        }

        return map;
    }

    public static Scriptable toScriptableArray(Scriptable scope,
            String[] elements)
    {
        Object[] array = new Object[elements.length];
        for (int i = 0; i < elements.length; i++)
        {
            array[i] = elements[i];
        }

        return Context.getCurrentContext().newArray(scope, array);
    }

    public static ScriptModelObject toScriptModelObject(RequestContext context,
            ModelObject modelObject)
    {
        if (modelObject != null)
        {
            return new ScriptModelObject(context, modelObject);
        }
        return null;
    }

    public static Object[] toScriptModelObjectArray(RequestContext context,
            ModelObject[] modelObjects)
    {
        Object[] array = new Object[] {};
        if (modelObjects != null)
        {
            array = new Object[modelObjects.length];
            for (int i = 0; i < modelObjects.length; i++)
            {
                array[i] = toScriptModelObject(context, modelObjects[i]);
            }
        }
        return array;
    }

    // API

    public ScriptModelObject getObject(String id)
    {
        ModelObject modelObject = Framework.getModel().loadObject(context, id);
        if (modelObject != null)
            return new ScriptModelObject(context, modelObject);
        return null;
    }
}
