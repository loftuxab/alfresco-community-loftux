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
package org.alfresco.web.scripts;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

import org.alfresco.web.framework.ModelObject;
import org.alfresco.web.site.RequestContext;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

/**
 * A helper class with static functions for working with Scriptable maps,
 * arrays and ScriptModelObjects.
 * 
 * @author muzquiano
 */
public final class ScriptHelper implements Serializable
{
    /**
     * Creates a Scriptable Map for a given array of model objects
     * 
     * @param context the context
     * @param modelObjects the model objects
     * 
     * @return the scriptable map
     */
    public static ScriptableMap toScriptableMap(RequestContext context,
            ModelObject[] modelObjects)
    {
        ScriptableMap<String, Serializable> map = new ScriptableMap<String, Serializable>(modelObjects.length);
        
        for (int i = 0; i < modelObjects.length; i++)
        {
            ScriptModelObject scriptModelObject = toScriptModelObject(context, modelObjects[i]);
            String id = modelObjects[i].getId();
            map.put(id, scriptModelObject);
        }
        
        return map;
    }

    /**
     * Creates a Scriptable Map for a given map of model objects
     * 
     * @param context the context
     * @param objects a map of model objects (keyed by object id)
     * 
     * @return the scriptable map
     */
    public static ScriptableMap toScriptableMap(RequestContext context,
            Map<String, ModelObject> objects)
    {
        ScriptableMap<String, Serializable> map = new ScriptableMap<String, Serializable>(objects.size());
        
        // convert to map of script model objects
        Iterator it = objects.keySet().iterator();
        while(it.hasNext())
        {
            String id = (String) it.next();
            ModelObject modelObject = (ModelObject) objects.get(id);
            
            ScriptModelObject scriptModelObject = toScriptModelObject(context, modelObject);
            map.put(id, scriptModelObject);            
        }
        
        return map;
    }
    
    /**
     * Converts an existing map to a Scriptable map
     * 
     * @param map the map
     * 
     * @return the scriptable map
     */
    public static ScriptableMap toScriptableMap(Map<String, Serializable> map)
    {
        return new ScriptableMap<String, Serializable>(map);
    }

    /**
     * Converts a given array to a Scriptable array that can be traversed
     * by the script and Freemarker engines
     * 
     * @param scope the scope
     * @param elements the elements
     * 
     * @return the scriptable
     */
    public static Scriptable toScriptableArray(Scriptable scope, String[] elements)
    {
        Object[] array = new Object[elements.length];
        for (int i = 0; i < elements.length; i++)
        {
            array[i] = elements[i];
        }

        return Context.getCurrentContext().newArray(scope, array);
    }

    /**
     * Wraps a ModelObject with a script wrapper to produce a ScriptModelObject
     * that can be used by the script and Freemarker engines.
     * 
     * @param context the context
     * @param modelObject the model object
     * 
     * @return the script model object
     */
    public static ScriptModelObject toScriptModelObject(RequestContext context,
            ModelObject modelObject)
    {
        if (modelObject != null)
        {
            return new ScriptModelObject(context, modelObject);
        }
        return null;
    }

    /**
     * Converts an array of ModelObjects to an array of ScriptModelObjects
     * which can be used by the script and Freemarker engines.
     * 
     * @param context the context
     * @param modelObjects the model objects
     * 
     * @return the object[]
     */
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

    /**
     * Converts a map of model objects to an array of ScriptModelObjects
     * which can be used by the script and Freemarker engines.
     * 
     * @param context the context
     * @param objects the model objects
     * 
     * @return the object[]
     */
    public static Object[] toScriptModelObjectArray(RequestContext context,
            Map<String, ModelObject> objects)
    {
        // convert to array
        Object[] array = objects.values().toArray(new Object[objects.size()]);
        
        // walk through array and wrap everything as a script model object
        for (int i = 0; i < array.length; i++)
        {
            array[i] = toScriptModelObject(context, (ModelObject)array[i]);
        }
        
        return array;
    }
    
    
    /**
     * Retrieves a model object from the underlying store and hands it back
     * wrapped as a ScriptModelObject.  If the model object cannot be found,
     * null will be returned.
     * 
     * @param id the id
     * 
     * @return the script model object
     */
    public static ScriptModelObject getObject(RequestContext context, String objectTypeId, String objectId)
    {
        ScriptModelObject scriptModelObject = null;
        
        ModelObject modelObject = context.getModel().getObject(objectTypeId, objectId);
        if (modelObject != null)
        {
            scriptModelObject = new ScriptModelObject(context, modelObject);
        }
        
        return scriptModelObject;
    }        
}
