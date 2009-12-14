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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.element.ConfigElementAdapter;
import org.dom4j.Element;

/**
 * @author muzquiano
 */
public class WebStudioConfigElement extends ConfigElementAdapter implements
        WebStudioConfigProperties
{
    public static final String CONFIG_ELEMENT_ID = "web-studio";

    protected HashMap<String, ApplicationDescriptor> applications = null;
    protected HashMap<String, AppletDescriptor> applets = null;

    protected boolean developerMode = false;

    /**
     * Default Constructor
     */
    public WebStudioConfigElement()
    {
        super(CONFIG_ELEMENT_ID);

        this.applications = new HashMap<String, ApplicationDescriptor>(16, 1.0f);
        this.applets = new HashMap<String, AppletDescriptor>(16, 1.0f);

        this.developerMode = false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.extensions.config.element.GenericConfigElement#combine(org.alfresco.config.ConfigElement)
     */
    public ConfigElement combine(ConfigElement element)
    {
        WebStudioConfigElement configElement = (WebStudioConfigElement) element;

        // new combined element
        WebStudioConfigElement combinedElement = new WebStudioConfigElement();

        // copy in our things
        combinedElement.applets.putAll(this.applets);
        combinedElement.applications.putAll(this.applications);

        // override with things from the merging object
        combinedElement.applets.putAll(configElement.applets);
        combinedElement.applications.putAll(configElement.applications);

        combinedElement.developerMode = this.developerMode;
        if (configElement.developerMode)
        {
            combinedElement.developerMode = configElement.developerMode;
        }

        return combinedElement;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.config.WebStudioConfigProperties#isDeveloperMode()
     */
    public boolean isDeveloperMode()
    {
        return this.developerMode;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.config.WebStudioConfigProperties#setDeveloperMode(boolean)
     */
    public void setDeveloperMode(boolean developerMode)
    {
        this.developerMode = developerMode;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.config.WebStudioConfigProperties#getApplicationIds()
     */
    public String[] getApplicationIds()
    {
        return this.applications.keySet().toArray(
                new String[this.applications.size()]);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.config.WebStudioConfigProperties#getApplication(java.lang.String)
     */
    public ApplicationDescriptor getApplication(String id)
    {
        return (ApplicationDescriptor) this.applications.get(id);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.config.WebStudioConfigProperties#getAppletIds()
     */
    public String[] getAppletIds()
    {
        return this.applets.keySet().toArray(new String[this.applets.size()]);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.config.WebStudioConfigProperties#getApplet(java.lang.String)
     */
    public AppletDescriptor getApplet(String id)
    {
        return (AppletDescriptor) this.applets.get(id);
    }

    // //////////////////////////

    public static class Descriptor
    {
        private static final String ID = "id";

        HashMap<String, Object> map;

        Descriptor(Element el)
        {
            List elements = el.elements();
            for (int i = 0; i < elements.size(); i++)
            {
                Element element = (Element) elements.get(i);
                put(element);
            }
        }

        public void put(Element el)
        {
            if (this.map == null)
            {
                this.map = new HashMap<String, Object>();
            }

            String key = el.getName();
            Object value = (Object) el.getTextTrim();
            if (value != null)
            {
                this.map.put(key, value);
            }
        }

        public Object get(String key)
        {
            if (this.map == null)
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

        public String getStringPropertyEx(String key)
        {
            String value = getStringProperty(key);
            if (value != null)
            {
                if (value.indexOf("${id}") > -1)
                {
                    value = value.replace("${id}", getId());
                }
            }
            return value;
        }
    }

    public static class ApplicationDescriptor extends Descriptor
    {
        private static final String TITLE = "title";
        private static final String DESCRIPTION = "description";
        private static final String IMAGEURL = "image-url";
        private static final String BOOTSTRAP_CLASSNAME = "bootstrap-classname";
        private static final String BOOTSTRAP_LOCATION = "bootstrap-location";

        private List<String> appletIncludes;

        ApplicationDescriptor(Element el)
        {
            super(el);

            this.appletIncludes = new ArrayList<String>();

            // get all of the applets
            List includes = el.elements("include-applet");
            for (int z = 0; z < includes.size(); z++)
            {
                Element includeElement = (Element) includes.get(z);
                String appletId = includeElement.attributeValue("id");

                this.appletIncludes.add(appletId);
            }
        }

        public String getTitle()
        {
            return getStringProperty(TITLE);
        }

        public String getDescription()
        {
            return getStringProperty(DESCRIPTION);
        }
        
        public String getImageUrl()
        {
            return getStringProperty(IMAGEURL);
        }

        public String getBootstrapClassName()
        {
            return getStringPropertyEx(BOOTSTRAP_CLASSNAME);
        }

        public String getBootstrapLocation()
        {
            return getStringPropertyEx(BOOTSTRAP_LOCATION);
        }

        public List<String> getAppletIncludes()
        {
            return this.appletIncludes;
        }
    }

    public static class AppletDescriptor extends Descriptor
    {
        private static final String TITLE = "title";
        private static final String DESCRIPTION = "description";
        private static final String BOOTSTRAP_CLASSNAME = "bootstrap-classname";
        private static final String BOOTSTRAP_LOCATION = "bootstrap-location";

        AppletDescriptor(Element el)
        {
            super(el);
        }

        public String getTitle()
        {
            return getStringProperty(TITLE);
        }

        public String getDescription()
        {
            return getStringProperty(DESCRIPTION);
        }

        public String getBootstrapClassName()
        {
            return getStringPropertyEx(BOOTSTRAP_CLASSNAME);
        }

        public String getBootstrapLocation()
        {
            return getStringPropertyEx(BOOTSTRAP_LOCATION);
        }
    }

    protected static WebStudioConfigElement newInstance(Element elem)
    {
        WebStudioConfigElement configElement = new WebStudioConfigElement();

        // developer mode
        String _developerMode = (String) elem.elementTextTrim("developer-mode");
        if (_developerMode != null)
        {
            configElement.developerMode = ("true"
                    .equalsIgnoreCase(_developerMode));
        }

        // applications
        Element applications = elem.element("applications");
        if (applications != null)
        {
            List list = applications.elements("application");
            for (int i = 0; i < list.size(); i++)
            {
                Element el = (Element) list.get(i);

                ApplicationDescriptor applicationDescriptor = new ApplicationDescriptor(
                        el);
                configElement.applications.put(applicationDescriptor.getId(),
                        applicationDescriptor);
            }
        }

        // applets
        Element applets = elem.element("applets");
        if (applets != null)
        {
            List list = applets.elements("applet");
            for (int i = 0; i < list.size(); i++)
            {
                Element el = (Element) list.get(i);

                AppletDescriptor appletDescriptor = new AppletDescriptor(el);
                configElement.applets.put(appletDescriptor.getId(),
                        appletDescriptor);
            }
        }

        return configElement;
    }
}
