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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.alfresco.web.framework.render.RenderContext;

/**
 * Describes a Form that can be bound for the currently rendering
 * component.
 * 
 * @author muzquiano
 */
public final class ScriptForm extends ScriptBase
{
	protected Map<String, FormBinding> bindings;
	protected RenderContext renderContext;
		
    /**
     * Instantiates a new script form.
     * 
     * @param context the request context
     */
    public ScriptForm(RenderContext context)
    {
    	super(context);
    	
    	this.renderContext = context;    	
    	this.bindings = new HashMap<String, FormBinding>(16, 1.0f);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.ScriptBase#buildProperties()
     */
    protected ScriptableMap buildProperties()
    {
    	return null;
    }
    
    // --------------------------------------------------------------
    // JavaScript Properties
    
    /**
     * Binds an element to this form.
     * 
     * @param title the title
     * @param value the value
     * 
     * @return the object
     */
    public void bind(String title, Object value)
    {
    	bind(title, value, null);
    }

    /**
     * Binds an element to this form.
     * 
     * @param title the title
     * @param value the value
     * @param nullValue the null value
     * 
     * @return the object
     */
    public FormBinding bind(String id, Object value, Object nullValue)
    {
    	if(value == null)
    	{
    		value = nullValue;
    	}
    	
    	FormBinding binding = new FormBinding(id, value);
    	bindings.put(id, binding);
    	
    	return binding;
    }
    
    /**
     * Gets an element form binding.
     * 
     * @param id the id
     * 
     * @return the binding
     */
    public FormBinding getBinding(String id)
    {
    	return (FormBinding) bindings.get(id);
    }
    
    /**
     * Gets the bindings.
     * 
     * @return the bindings
     */
    public Object[] getBindings()
    {
        Object[] array = new Object[bindings.size()];
        
        int i = 0;
        Iterator it = bindings.keySet().iterator();
        while(it.hasNext())
        {
        	String key = (String) it.next();
        	FormBinding binding = (FormBinding) bindings.get(key);
        	array[i] = binding;
        	i++;
        }

        return array;
    }
    
    /**
     * Gets the ids of all element form bindings.
     * 
     * @return the binding ids
     */
    public String[] getBindingIds()
    {
    	return bindings.keySet().toArray(new String[bindings.keySet().size()]);
    }    

    public class FormBinding implements Serializable
    {
	    private String id;   	
	    private Object value;
    	
    	/**
	     * Instantiates a new form binding.
	     * 
	     * @param id the id
	     */
	    public FormBinding(String id)
    	{
    		this.id = id;
    	}
    	
    	/**
	     * Instantiates a new form binding.
	     * 
	     * @param id the id
	     * @param value the value
	     */
	    public FormBinding(String id, Object value)
    	{
    		this(id);

    		this.value = value;    		
    	}
    	
    	/**
	     * Sets the value.
	     * 
	     * @param value the new value
	     */
	    public void setValue(Object value)
    	{
    		this.value = value;
    	}
	    
	    public Object getValue()
	    {
	    	return this.value;
	    }
	    
	    public String getId()
	    {
	    	return ScriptForm.prefix(renderContext, id);
	    }	    
    }
    
    protected static String getPrefix(RenderContext context)
    {
    	return "form_" + context.getObject().getId() + "___";
    }
    
	protected static String prefix(RenderContext context, String id)
	{
		return getPrefix(context) + id;
	}
	
	protected static String unprefix(String prefixedId)
	{
		int x = prefixedId.indexOf("___");
		if(x > -1)
		{
			return prefixedId.substring(x+3);
		}
		return null;
	}    
}
