package org.alfresco.web.site;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HTMLUtil
{
    /**
     * Used to include rendition of a piece of content (xform-driven)
     * @param request
     * @param response
     * @param relativePath
     * @throws ServletException
     */
    public static void includeRendition(HttpServletRequest request,
            HttpServletResponse response, String renditionRelativePath,
            String originalRelativePath) throws ServletException
    {
        RequestContext context = RequestUtil.getRequestContext(request);

        // load the html
        String unprocessedHtml = ModelUtil.getFileStringContents(
                context, renditionRelativePath);

        // process the tags in the html
        // this executes and commits to the writer
        try
        {
            String content = FilterUtil.filterContent(context, request, response,
                    unprocessedHtml, originalRelativePath);
            response.getWriter().write(content);
        }
        catch (Exception ex)
        {
            throw new ServletException(ex);
        }
    }

    /**
     * Performs an HTML include and processes tags
     * @param request
     * @param response
     * @param renditionRelativePath
     * @throws ServletException
     */
    public static void includeHTML(HttpServletRequest request,
            HttpServletResponse response, String renditionRelativePath)
            throws ServletException
    {
        RequestContext context = RequestUtil.getRequestContext(request);

        // load the html
        String unprocessedHtml = ModelUtil.getFileStringContents(
                context, renditionRelativePath);

        try
        {
            // process the tags in the html
            // this executes and commits to the writer
            String content = FilterUtil.filterContent(context, request, response,
                    unprocessedHtml, renditionRelativePath);
            response.getWriter().write(content);
        }
        catch (Exception ex)
        {
            throw new ServletException(ex);
        }
    }
	
}