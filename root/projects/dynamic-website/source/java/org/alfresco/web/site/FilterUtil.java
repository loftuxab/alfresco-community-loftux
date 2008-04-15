/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
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
package org.alfresco.web.site;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.tools.FakeHttpServletResponse;
import org.alfresco.tools.WrappedHttpServletRequest;
import org.alfresco.web.site.parser.ITagletHandler;
import org.alfresco.web.site.parser.tags.PageTokenizer;
import org.alfresco.web.site.parser.tags.TagletParser;

/**
 * @author muzquiano
 */
public class FilterUtil
{
    /**
     * Standalone method that processes tags on the given content. This hands
     * back the processed result as a string.
     * 
     * @param data
     * @param relativePath
     * @return
     * @throws Exception
     */
    public static String filterContent(RequestContext context,
            HttpServletRequest request, HttpServletResponse response,
            String content, String relativePath) throws Exception
    {        
        WrappedHttpServletRequest req = new WrappedHttpServletRequest(request);
        FakeHttpServletResponse resp = new FakeHttpServletResponse();
        
        // Create the filter context
        FilterContext filterContext = new FilterContext(req, resp,
                request.getSession().getServletContext());
        filterContext.setRequestContext(context);

        // process the filter
        executeFilter(filterContext, content, relativePath);

        return resp.getContentAsString();
    }

    /**
     * Processes filtering on the given html data. This will "remember" the
     * current content item (described by relative path). Some tags may use this
     * as a default content path/id if no id is provided.
     * 
     * @param request
     * @param response
     * @param data
     * @param relativePath
     * @return
     */
    protected static void executeFilter(FilterContext filterContext,
            String data, String relativePath) 
            throws Exception
    {
        // store content identification onto the filter
        filterContext.setValue(FilterContext.CONTENT_ITEM_ID, relativePath);

        // create a new parser
        RequestContext requestContext = filterContext.getRequestContext();
        TagletParser parser = getTagletParser(requestContext);

        // create a new handler -- this one executes tags on-the-fly
        ITagletHandler th = new PageTokenizer();

        // execute the tags in the document
        executeParser(parser, th, filterContext, data);
    }

    public static void executeParser(TagletParser parser, ITagletHandler th,
            FilterContext filterContext, String data) throws Exception
    {
        InputStream is = new ByteArrayInputStream(data.getBytes());
        parser.parseTaglets(filterContext, th, is);
    }

    private static TagletParser defaultTagletParser;

    public static TagletParser getTagletParser(RequestContext context)
    {
        if (defaultTagletParser != null)
            return defaultTagletParser;

        // tld documents to load
        // and tld url mappings
        Map<String, String> tldMap = new HashMap<String, String>();
        Map<String, String> tldUrlMap = new HashMap<String, String>();

        // walk the configuration
        String tldIds[] = Framework.getConfig().getTagLibraryIds();
        for (int i = 0; i < tldIds.length; i++)
        {
            String uri = Framework.getConfig().getTagLibraryUri(tldIds[i]);
            String namespace = Framework.getConfig().getTagLibraryNamespace(
                    tldIds[i]);

            // the id is the prefix
            String prefix = tldIds[i];
            tldMap.put(prefix, uri);
            tldUrlMap.put(prefix, namespace);

            // tldMap.put("alf", "/WEB-INF/tlds/alf.tld");
            // tldUrlMap.put("alf", "http://www.alfresco.org/taglib/alf");
        }

        TagletParser parser = getTagletParser(context, tldMap, tldUrlMap);
        if (parser != null)
            defaultTagletParser = parser;
        return parser;
    }

    public static TagletParser getTagletParser(RequestContext context,
            Map<String, String> tldMap, Map<String, String> tldUrlMap)
    {
        TagletParser parser = null;
        try
        {
            parser = new TagletParser();

            Iterator it = tldMap.keySet().iterator();
            while (it.hasNext())
            {
                String prefix = (String) it.next();
                String relativePath = (String) tldMap.get(prefix);
                String tldUrl = (String) tldUrlMap.get(prefix);

                // load the xml
                String xml = ModelUtil.getFileStringContents(context,
                        relativePath);

                // import the tag library
                parser.importNamespace(prefix, tldUrl, xml);
            }
        }
        catch (Exception ex)
        {
            // TODO: Handle
            ex.printStackTrace();
        }
        return parser;
    }

}
