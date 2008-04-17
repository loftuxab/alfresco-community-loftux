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

import java.util.Map;

import org.alfresco.web.site.RequestContext;
import org.alfresco.web.site.model.Component;
import org.alfresco.web.site.model.ModelObject;
import org.alfresco.web.site.model.Template;

/**
 * @author muzquiano
 */
public class RuntimeConfigManager
{
    public static RuntimeConfig newConfiguration(RequestContext context)
    {
        RuntimeConfig config = new RuntimeConfig();
        return config;
    }
    
    public static RuntimeConfig loadConfiguration(RequestContext context,
            ModelObject obj)
    {
        RuntimeConfig config = new RuntimeConfig(obj);
        populateConfiguration(context, obj, config);
        return config;
    }

    public static void populateConfiguration(RequestContext context,
            ModelObject obj, RuntimeConfig config)
    {
        if (obj == null)
            return;

        if (obj instanceof Component)
            populateConfiguration(context, (Component) obj, config);
        if (obj instanceof Component)
            populateConfiguration(context, (Component) obj, config);
        if (obj instanceof Template)
            populateConfiguration(context, (Template) obj, config);
    }

    protected static void populateConfiguration(RequestContext context,
            Template template, RuntimeConfig config)
    {
        // populate with template settings
        Map templateSettings = template.getSettings();
        config.putAll(templateSettings);

        config.put("template-id", template.getId());
        config.put("template-type-id", template.getTemplateType());
    }

    protected static void populateConfiguration(RequestContext context,
            Component component, RuntimeConfig config)
    {
        // populate with component settings
        Map componentSettings = component.getSettings();
        config.putAll(componentSettings);

        config.put("component-id", component.getId());
        config.put("component-type-id", component.getComponentTypeId());
        config.put("component-region-id", component.getRegionId());
        config.put("component-source-id", component.getSourceId());
        config.put("component-scope-id", component.getScope());
    }

}
