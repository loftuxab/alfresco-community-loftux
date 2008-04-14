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
 * http://www.alfresco.com/legal/licensing
 */
package org.alfresco.web.scripts.servlet;

import org.alfresco.web.scripts.PresentationTemplateProcessor;
import org.springframework.context.ApplicationEvent;

import freemarker.cache.MruCacheStorage;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;

/**
 * Template Processor specific to the Page Renderer. Overrides the initial configuration and
 * provides a mechanism to add template loaders to the list derived from the Search Path.
 * 
 * Also sets the cache level to zero - we cannot cache included WebScript component results.
 * 
 * @author Kevin Roast
 */
public class PageRendererTemplateProcessor extends PresentationTemplateProcessor
{
    /**
     * Add a template loader to the list used when the config is initialised.
     * Must be called before the config is first initialised.
     * 
     * @param loader    TemplateLoader
     */
    public void addTemplateLoader(TemplateLoader loader)
    {
        loaders.add(loader);
    }
    
    /**
     * @return the current Configuration object for the template processor
     */
    public Configuration getConfig()
    {
        return templateConfig; 
    }
    
    /* (non-Javadoc)
     * @see org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
     */
    @Override
    public void onApplicationEvent(ApplicationEvent event)
    {
       // No Spring init required - we init the config by hand in the PageRenderer servlet init() method
    }
    
    @Override
    protected void initConfig()
    {
       super.initConfig();
       
       // This template processor is responsible for processing Page templates and also
       // for including the dynamic result output from UI WebScript components.
       // We cannot cache the WebScript results but we do know that any Page template will
       // be processed exactly twice in sequence before any WebScript components are resolved.
       // Therefore we use a cache size of 1.
       templateConfig.setCacheStorage(new MruCacheStorage(1, 0));
    }
}
