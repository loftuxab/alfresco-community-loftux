/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.web.site;

import java.util.Locale;
import java.util.Map;

import org.springframework.extensions.config.Config;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.surf.FrameworkUtil;
import org.springframework.extensions.surf.exception.PlatformRuntimeException;
import org.springframework.extensions.surf.mvc.AbstractWebFrameworkViewResolver;
import org.springframework.extensions.surf.mvc.PageView;
import org.springframework.extensions.surf.types.Page;
import org.springframework.extensions.surf.uri.UriTemplateListIndex;
import org.springframework.web.servlet.view.AbstractUrlBasedView;


/**
 * Slingshot specific implementation of PageViewResolver.
 * 
 * @author Kevin Roast
 */
public class SlingshotPageViewResolver extends AbstractWebFrameworkViewResolver
{
    /** URI Template index - slingshot page url mappings */
    private static UriTemplateListIndex uriTemplateIndex = null;
    
    
    /* (non-Javadoc)
     * @see org.springframework.web.servlet.view.UrlBasedViewResolver#canHandle(java.lang.String, java.util.Locale)
     */
    protected boolean canHandle(String viewName, Locale locale) 
    {
        // assume that the view name is a page name and check whether the page exists
        Page page = null;
        
        if (viewName.length() == 0 || (viewName.length() == 1 && viewName.charAt(0) == '/'))
        {
            // assume root page
            page = FrameworkUtil.getCurrentRequestContext().getRootPage();
        }
        else
        {
            String pageId = viewName;
            
            // Perform match against URI templates - to resolve application wide uri template
            // token values such as "site" i.e. /site/mysite/pageid/pageid
            // where the uri template might be /site/{site}/{pageid}
            Map<String, String> uriTokens = matchUriTemplate('/' + pageId);
            
            // if match our special "{pageid}" token - override pageId with the value
            if (uriTokens != null)
            {
                if (uriTokens.containsKey(SlingshotPageMapper.URI_PAGEID))
                {
                    pageId = uriTokens.get(SlingshotPageMapper.URI_PAGEID);
                }
            }
            page = getObjectService().getPage(pageId);
        }
        
        return (page != null);
    }
    
    /* (non-Javadoc)
     * @see org.springframework.web.servlet.view.UrlBasedViewResolver#buildView(java.lang.String)
     */
    protected AbstractUrlBasedView buildView(String viewName) throws Exception 
    {
        AbstractUrlBasedView view = null;

        // assume that that view name is a page id
        Page page = null;
        
        if (viewName.length() == 0 || (viewName.length() == 1 && viewName.charAt(0) == '/'))
        {
            // assume root page
            page = FrameworkUtil.getCurrentRequestContext().getRootPage();
        }
        else
        {
            String pageId = viewName;
            
            // Perform match against URI templates - to resolve application wide uri template
            // token values such as "site" i.e. /site/mysite/pageid/pageid
            // where the uri template might be /site/{site}/{pageid}
            Map<String, String> uriTokens = matchUriTemplate('/' + pageId);
            
            // if match our special "{pageid}" token - override pageId with the value
            if (uriTokens != null)
            {
                if (uriTokens.containsKey(SlingshotPageMapper.URI_PAGEID))
                {
                    pageId = uriTokens.get(SlingshotPageMapper.URI_PAGEID);
                }
            }
            page = getObjectService().getPage(pageId);
        }
        
        if (page != null)
        {
            view = new PageView(getServiceRegistry());            
            view.setUrl(viewName);
        }
        
        return view;
    }
    
    /**
     * Match the page Id against the available URI templates. If a match is found then return
     * the variables representing the tokens and the values extracted from the supplied page Id.
     * 
     * @param pageId    Page Id to match against
     * 
     * @return map of tokens to values or null if no match found 
     */
    private Map<String, String> matchUriTemplate(String pageId)
    {
        if (uriTemplateIndex == null)
        {
            Config config = this.getServiceRegistry().getConfigService().getConfig("UriTemplate");
            if (config == null)
            {
                throw new PlatformRuntimeException("Cannot find required config element 'UriTemplate'.");
            }
            ConfigElement uriConfig = config.getConfigElement("uri-templates");
            if (uriConfig == null)
            {
                throw new PlatformRuntimeException("Missing required config element 'uri-templates' under 'UriTemplate'.");
            }
            // NOTE: this update to a static field is not synchronized - it does not matter if
            //       multiple threads set it - the end result will be the same data
            uriTemplateIndex = new UriTemplateListIndex(uriConfig);
        }
        return uriTemplateIndex.findMatch(pageId);
    }
}
