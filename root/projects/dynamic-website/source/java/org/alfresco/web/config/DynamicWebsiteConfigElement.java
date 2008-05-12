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
package org.alfresco.web.config;

import java.util.HashMap;
import java.util.List;

import org.alfresco.config.ConfigElement;
import org.alfresco.config.element.ConfigElementAdapter;
import org.dom4j.Element;

/**
 * @author muzquiano
 */
public class DynamicWebsiteConfigElement extends ConfigElementAdapter implements DynamicWebsiteConfigProperties
{
	public static final String CONFIG_ELEMENT_ID = "dynamic-website";

	protected HashMap<String, InContextElementDescriptor> incontexts = null;
	
	protected boolean inContextEnabled = true;
	protected String dynamicWebsiteServletUri = "/dynamic-website";
	
	/**
	 * Default Constructor
	 */
	public DynamicWebsiteConfigElement()
	{
		super(CONFIG_ELEMENT_ID);
		
		incontexts = new HashMap<String, InContextElementDescriptor>();
		
		inContextEnabled = true;
	}
	
    /* (non-Javadoc)
     * @see org.alfresco.config.element.GenericConfigElement#combine(org.alfresco.config.ConfigElement)
     */
    public ConfigElement combine(ConfigElement element)
    {
    	DynamicWebsiteConfigElement configElement = (DynamicWebsiteConfigElement) element;
    	
    	// new combined element    	
    	DynamicWebsiteConfigElement combinedElement = new DynamicWebsiteConfigElement();
        
    	// copy in our things
        combinedElement.incontexts.putAll(this.incontexts);
    	
        // override with things from the merging object
        combinedElement.incontexts.putAll(configElement.incontexts);
        
        // other properties
        combinedElement.inContextEnabled = this.inContextEnabled;
        if(configElement.inContextEnabled)
        {
        	combinedElement.inContextEnabled = configElement.inContextEnabled;
        }
        combinedElement.dynamicWebsiteServletUri = this.dynamicWebsiteServletUri;
        if(configElement.dynamicWebsiteServletUri != null)
        {
        	combinedElement.dynamicWebsiteServletUri = configElement.dynamicWebsiteServletUri;
        }

        return combinedElement;
    }
    
////////////////////////////
    
    public boolean isInContextEnabled()
    {
    	return this.inContextEnabled;    	
    }
    
    public String[] getInContextElementIds()
    {
    	return toStringArray(this.incontexts.keySet().toArray());   	
    }
    
    public InContextElementDescriptor getInContextElementDescriptor(String id)
    {
    	return (InContextElementDescriptor) this.incontexts.get(id);
    }
    
    public String getDynamicWebsiteServletUri()
    {
    	return this.dynamicWebsiteServletUri;
    }
    

////////////////////////////

    public static class Descriptor
    {
    	private static final String ID = "id";
    	
    	HashMap<String, Object> map;
    	
    	Descriptor(Element el)
    	{
    		List elements = el.elements();
    		for(int i = 0; i < elements.size(); i++)
    		{
    			Element element = (Element) elements.get(i);
    			put(element);
    		}
    	}
    	
		public void put(Element el)
		{
			if(this.map == null)
			{
				this.map = new HashMap<String, Object>();
			}
			
			String key = el.getName();
			Object value = (Object) el.getTextTrim();
			if(value != null)
			{
				this.map.put(key, value);
			}
		}
		
		public Object get(String key)
		{
			if(this.map == null)
			{
				this.map = new HashMap<String, Object>();
			}
			
			return (Object) this.map.get(key);
		}	
		
		public String getId() 
		{
			return (String) get(ID);
		}		
		
		public Object getProperty(String key)
		{
			return get(key);
		}
		
		public String getStringProperty(String key)
		{
			return (String) get(key);
		}
    }
    
    public static class InContextElementDescriptor extends Descriptor
    {
    	private static final String NAME = "name";
    	private static final String DESCRIPTION = "description";
    	private static final String TYPE = "type";
    	
    	private String defaultEnabled;
    	private String defaultState;
		
		InContextElementDescriptor(Element el)
    	{
			super(el);
			
			Element defaultsElement = el.element("defaults");
			if(defaultsElement != null)
			{
				defaultEnabled = defaultsElement.elementTextTrim("enabled");
				defaultState = defaultsElement.elementTextTrim("state");
			}
    	}

		public String getName() 
		{
			return getStringProperty(NAME);
		}
		public String getDescription() 
		{
			return getStringProperty(DESCRIPTION);
		}
		
		public String getType()
		{
			return getStringProperty(TYPE);
		}
		
		public String getDefaultEnabled()
		{
			return defaultEnabled;
		}
		
		public String getDefaultState()
		{
			return defaultState;
		}
    }    
    
    protected static DynamicWebsiteConfigElement newInstance(Element elem)
    {
    	DynamicWebsiteConfigElement configElement = new DynamicWebsiteConfigElement();
   
    	// incontexts
    	List list = elem.elements("incontext-element");
    	for(int i = 0; i < list.size(); i++)
    	{
    		Element el = (Element) list.get(i);
    		InContextElementDescriptor descriptor = new InContextElementDescriptor(el);
    		configElement.incontexts.put(descriptor.getId(), descriptor);
    	}
    	
    	// in context enabled
    	String _inContextEnabled = (String) elem.elementTextTrim("incontext-enabled");
    	if(_inContextEnabled != null)
    	{
    		configElement.inContextEnabled = ("true".equalsIgnoreCase(_inContextEnabled));
    	}
    	
    	return configElement;
    }
    
    protected static String[] toStringArray(Object[] array)
    {
    	if(array == null)
    	{
    		return null;
    	}
    	String[] newArray = new String[array.length];
    	for(int i = 0; i < array.length; i++)
    	{
    		newArray[i] = (String) array[i];
    	}
    	return newArray;
    }    
}
