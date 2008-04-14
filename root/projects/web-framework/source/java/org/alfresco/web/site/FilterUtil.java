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
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.web.site.parser.ITagletHandler;
import org.alfresco.web.site.parser.tags.PageTokenizer;
import org.alfresco.web.site.parser.tags.TagletParser;

/**
 * @author muzquiano
 */
public class FilterUtil
{
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
    public static String filterContent(HttpServletRequest request,
            HttpServletResponse response, String data, String relativePath)
    {
        RequestContext context = RequestUtil.getRequestContext(request);

        FilterContext cxt = new FilterContext(request, response,
                request.getSession().getServletContext());
        request.setAttribute("FILTER_CONTEXT", cxt);

        // store content identification onto the filter
        cxt.setValue(FilterContext.CONTENT_ITEM_ID, relativePath);

        // create a new parser that processes links
        TagletParser parser = getTagletParser(context);

        // create a new handler -- this one executes tags on-the-fly
        ITagletHandler th = new PageTokenizer();

        String result = executeParser(parser, th, cxt, data);
        return result;
    }

    public static String executeParser(TagletParser parser, ITagletHandler th,
            FilterContext cxt, String data)
    {
        InputStream in = new ByteArrayInputStream(data.getBytes());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        parser.parseTaglets(cxt, th, in, baos);
        return new String(baos.toByteArray());
    }

    private static TagletParser defaultTagletParser;

    public static TagletParser getTagletParser(RequestContext context)
    {
        if (defaultTagletParser != null)
            return defaultTagletParser;

        // tld documents to load
        // TODO: Make this extensible
        Map<String, String> tldMap = new HashMap<String, String>();
        tldMap.put("ui", "/WEB-INF/tlds/ui.tld");
        tldMap.put("adw", "/WEB-INF/tlds/adw.tld");

        // tld url mappings
        // TODO: Make this extensible
        Map<String, String> tldUrlMap = new HashMap<String, String>();
        tldUrlMap.put("ui", "http://www.alfresco.org/taglib/adw/ui");
        tldUrlMap.put("adw", "http://www.alfresco.org/taglib/adw");

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
                String xml = context.getModelManager().getDocumentString(
                        context, relativePath);
                parser.importNamespace(prefix, tldUrl, xml);

                System.out.println("Successfully imported namespace: " + prefix);
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
