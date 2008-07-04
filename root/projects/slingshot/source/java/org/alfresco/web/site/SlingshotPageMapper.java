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
package org.alfresco.web.site;

import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.alfresco.config.Config;
import org.alfresco.config.ConfigElement;
import org.alfresco.connector.AlfrescoAuthenticator;
import org.alfresco.connector.ConnectorSession;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.web.framework.model.Page;
import org.alfresco.web.framework.model.Theme;
import org.alfresco.web.scripts.ProcessorModelHelper;
import org.alfresco.web.scripts.URLHelper;
import org.alfresco.web.site.exception.ContentLoaderException;
import org.alfresco.web.site.exception.PageMapperException;
import org.alfresco.web.uri.UriTemplateListIndex;

/**
 * This is a Page Mapper class which serves to interpret URLs at dispatch
 * time for the Slingshot project.
 * 
 * Requests are received in the style defined for Slingshot.
 * 
 * Requests arrive in the form:
 * 
 * 		/page
 * 		/page/<pageId>
 * 		/page/<pageId>?<objArgument>=<objectId>
 * 		/page/<pageId>?<objArgument>=<objectId>&other arguments
 * 
 * Other forms may exists, they are matched against the configured uri templates.
 * See web-framework-config-application.xml for available uri templates.
 * 
 * The <pageId> identifier could be the id of the page object.
 * It could also be a relative path to the page object:
 * 
 * 		For example:  /page/collaboration/dashboard
 * 
 * Everything from the original request is available downstream to
 * all rendering components and templates.
 * 
 * @author muzquiano
 * @author kevinr
 */
public class SlingshotPageMapper extends AbstractPageMapper
{
    private static final String URI_PAGEID = "pageid";
    
    /** URI Template index - page url mappings */
    private static final UriTemplateListIndex uriTemplateIndex;
    
    
    /**
     * Empty constructor - for instantiation via reflection 
     */
    public SlingshotPageMapper()
    {
        super();
    }
    
    /**
     * Process a page request.
     */
    public void execute(RequestContext context, ServletRequest request)
    	throws PageMapperException
    {
    	if (request instanceof HttpServletRequest == false)
    	{
    		throw new PageMapperException("The slingshot page mapper must be given an HttpServletRequest to execute.");
    	}
    	
    	// The request URI string.  This comes in as something like:
    	//    /slingshot/page/collaboration/user-profile
        // Strip off the webapp name (if any - may be ROOT i.e. "/")
        HttpServletRequest req = ((HttpServletRequest)request);
    	String requestURI = req.getRequestURI().substring(req.getContextPath().length());
        
    	// Extract page Id from the rest of the URI
    	String pageId = null;
        
	    // strip servlet name and set remaining path as currently executing URI
        Map<String, String> uriTokens = null;
        int pathIndex = requestURI.indexOf('/', 1);
        if (pathIndex != -1 && requestURI.length() > (pathIndex + 1))
        {
            pageId = requestURI.substring(pathIndex + 1);
            context.setUri(pageId);
            
            // Perform match against URI templates - to resolve application wide uri template
            // token values such as "site" i.e. /site/mysite/pageid/pageid
            // where the uri template might be /site/{site}/{pageid}
            uriTokens = matchUriTemplate(requestURI.substring(pathIndex));
            
            // if match our special "{pageid}" token - override pageId with the value
            if (uriTokens != null)
            {
                if (uriTokens.containsKey(URI_PAGEID))
                {
                    pageId = uriTokens.get(URI_PAGEID);
                }
            }
        }
        
        // build a URLHelper object one time - it is immutable and can be reused
        URLHelper urlHelper = new URLHelper(req, uriTokens);
        context.setValue(ProcessorModelHelper.MODEL_URL, urlHelper);
    	
    	// Did we receive an "object" request parameter
    	String objectId = (String)request.getParameter("doc");
    	if (objectId != null && objectId.length() == 0)
    	{
    		objectId = null;
    	}
    	
        /**
         * Extract the page type id and determine which page to bind
         * into the request context.
         * 
         * This checks to see if a theme is defined and whether said theme
         * describes an override for this page type.
         * 
         * Otherwise, a check is made to see if a system default page has
         * been specified for this page type.
         * 
         * Finally, if nothing can be determined, a generic page is
         * bound into the request context.
         */
        String pageTypeId = (String) request.getParameter("pt");
        if (pageTypeId != null && pageTypeId.length() != 0)
        {
            // page type overrides anything else on the uri
            pageId = null;
            
            // Consider the theme
            String themeId = (String) context.getThemeId();
            Theme theme = context.getModel().getTheme(themeId);
            if (theme != null)
            {
                pageId = theme.getPageId(pageTypeId);
            }
            
            // Consider whether a system default has been set up
            if (pageId == null)
            {
                pageId = getPageId(context, pageTypeId);
            }
            
            // Worst case, pick a generic page
            if (pageId == null)
            {
                pageId = getPageId(context, WebFrameworkConstants.GENERIC_PAGE_TYPE_DEFAULT_PAGE_ID);
            }
        }
        
    	// Place everything into the request context
    	if (pageId != null)
    	{
    		// We have a page Id.  We must resolve it to a page.
            Page page = context.getModel().getPage(pageId);
            if (page != null)
            {
                context.setPage(page);
            }
        }
        
        /**
         * At present, the Slingshot project doesn't do much with formats.
         * 
         * Note that if we didn't set it, it would still automatically
         * pick up the default format.
         */
        context.setFormatId(FrameworkHelper.getConfig().getDefaultFormatId());
    	
        /**
         * If we received a "currently viewed object", then lets set it
         * onto the request as well.
         */
    	if (objectId != null)
    	{
        	Content content = null;
        	try
        	{
        		content = loadContent(context, objectId);
	        	if (content != null)
	        	{
	        		context.setCurrentObject(content);
	        	}
        	}
    		catch (ContentLoaderException cle)
    		{
    			throw new PageMapperException("Page Mapper was unable to load content for object id: " + objectId);
    		}    		
    	}
    	
        // get the connector "session" to this endpoint (for this user)        
        ConnectorSession connectorSession =
            FrameworkHelper.getConnectorSession(context, AlfrescoUserFactory.ALFRESCO_ENDPOINT_ID);
        if (connectorSession != null)
        {
            // retrieve the alfTicket - special case for Flash apps that do not share the user session
            String ticket = (String)connectorSession.getParameter(AlfrescoAuthenticator.CS_PARAM_ALF_TICKET);
            context.setValue(AlfrescoAuthenticator.CS_PARAM_ALF_TICKET, ticket);
        }
    }
    
    /**
     * Match the page Id against the available URI templates. If a match is found then return
     * the variables representing the tokens and the values extracted from the supplied page Id.
     * 
     * @param pageId    Page Id to match against
     * 
     * @return map of tokens to values or null if no match found 
     */
    private static Map<String, String> matchUriTemplate(String pageId)
    {
        return uriTemplateIndex.findMatch(pageId);
    }
    
    static
    {
        Config config = FrameworkHelper.getConfigService().getConfig("UriTemplate");
        if (config == null)
        {
            throw new AlfrescoRuntimeException("Cannot find required config element 'UriTemplate'.");
        }
        ConfigElement uriConfig = config.getConfigElement("uri-templates");
        if (uriConfig == null)
        {
            throw new AlfrescoRuntimeException("Missing required config element 'uri-templates' under 'UriTemplate'.");
        }
        uriTemplateIndex = new UriTemplateListIndex(uriConfig);
    }
}
