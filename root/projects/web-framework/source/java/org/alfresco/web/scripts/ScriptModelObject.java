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

import org.alfresco.util.ParameterCheck;
import org.alfresco.web.framework.ModelObject;
import org.alfresco.web.site.RequestContext;
import org.mozilla.javascript.Scriptable;

/**
 * Provides a write-able model object wrapper to the script engine.
 * 
 * The properties of this object are writeable which means that the
 * developer has the option to use either the properties array or
 * explicit methods.
 * 
 * The following commands are equivalent:
 * 
 * myObject.properties.title = "abc";
 * myObject.properties["title"] = "abc";
 * myObject.setProperty("title", "abc");
 * 
 * Note: The index on the properties array is not supported.  Thus, a command
 * such as this:
 * 
 * myObject.properties[0] = "abc";
 * 
 * will no-op and do nothing.
 * 
 * @author muzquiano
 */
public final class ScriptModelObject extends ScriptBase
{
    // unmodifiable "system" properties
    private static final long serialVersionUID = -3378946227712939601L;
    private final ModelObject modelObject;
    
    /**
     * Instantiates a new script model object.
     * 
     * @param context the context
     * @param modelObject the model object
     */
    public ScriptModelObject(RequestContext context, ModelObject modelObject)
    {
        super(context);
        
        // store a reference to the model object
        this.modelObject = modelObject;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.AbstractScriptableObject#buildProperties()
     */
    protected ScriptableMap buildProperties()
    {
        if (this.properties == null)
        {
            // construct and add in all of our model object properties
            this.properties = new ScriptableMap<String, Serializable>(modelObject.getProperties())
            {
                // trap this method so that we can adjust the model object
                public void put(String name, Scriptable start, Object value)
                {
                    put(name, (Serializable)value);

                    // update the model object
                    modelObject.setProperty(name, (String) value);
                }

                // do not allow
                public void put(int index, Scriptable start, Object value)
                {
                }

                // trap this method so that we can adjust the model object
                public void delete(String name)
                {
                    remove(name);
                    
                    // update the model object
                    modelObject.removeProperty(name);
                }

                // do not allow
                public void delete(int index)
                {
                }
            };
        }
        
        return this.properties;
    }
    
    
    // --------------------------------------------------------------
    // JavaScript Properties
    //
    
    public String getId()
    {
        return modelObject.getId();
    }
    
    public String getTitle()
    {
        return modelObject.getTitle();
    }
    
    public void setTitle(Serializable value)
    {
        if(value != null)
        {
            getProperties().put("title", value);
        }
        else
        {
            getProperties().delete("title");
        }
    }
    
    public String getDescription()
    {
        return modelObject.getDescription();
    }
    
    public void setDescription(Serializable value)
    {
        if(value != null)
        {
            getProperties().put("description", value);
        }
        else
        {
            getProperties().delete("description");
        }
    }
    
    public long getTimestamp()
    {
        return modelObject.getModificationTime();
    }
    
    public String getPersisterId()
    {
        return modelObject.getPersisterId();
    }
    
    public String getStoragePath()
    {
        return modelObject.getStoragePath();
    }
    

    
    // --------------------------------------------------------------
    // JavaScript Functions
    //    
    
    
    /**
     * Save.
     */
    public void save()
    {
        // retrieve values from our properties array
        Iterator it = getProperties().keySet().iterator();
        while(it.hasNext())
        {
            String propertyName = (String) it.next();
            String propertyValue = (String) getProperties().get(propertyName);
            modelObject.setProperty(propertyName, propertyValue);
        }
        
        context.getModel().saveObject(modelObject);
    }

    /**
     * Removes the object
     */
    public void remove()
    {
        context.getModel().removeObject(modelObject);
    }
    
    public void delete()
    {
        remove();
    }

    /**
     * To xml.
     * 
     * @return the string
     */
    public String toXML()
    {
        return modelObject.toXML();
    }
    
    /**
     * Touch.
     */
    public void touch()
    {
        modelObject.touch();
        
        // this forces all of the properties to reload
        this.properties = null;
    }
    
    /**
     * Gets the boolean property.
     * 
     * @param propertyName the property name
     * 
     * @return the boolean property
     */
    public boolean getBooleanProperty(String propertyName)
    {
        ParameterCheck.mandatory("propertyName", propertyName);
        return modelObject.getBooleanProperty(propertyName);
    }

    /**
     * Gets the property.
     * 
     * @param propertyName the property name
     * 
     * @return the property
     */
    public String getProperty(String propertyName)
    {
        ParameterCheck.mandatory("propertyName", propertyName);
        return modelObject.getProperty(propertyName);
    }

    /**
     * Sets the property.
     * 
     * @param propertyName the property name
     * @param propertyValue the property value
     */
    public void setProperty(String propertyName, String propertyValue)
    {
        ParameterCheck.mandatory("propertyName", propertyName);
        ParameterCheck.mandatory("propertyValue", propertyValue);
        modelObject.setProperty(propertyName, propertyValue);
    }

    /**
     * Removes the property.
     * 
     * @param propertyName the property name
     */
    public void removeProperty(String propertyName)
    {
        ParameterCheck.mandatory("propertyName", propertyName);
        modelObject.removeProperty(propertyName);
    }    
}
