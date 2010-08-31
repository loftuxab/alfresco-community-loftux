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
package org.alfresco.wcm.client.directive;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.alfresco.web.awe.tag.AlfrescoTagUtil;
import org.alfresco.web.awe.tag.MarkedContent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import freemarker.core.Environment;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * Freemarker directive which indicates an editable area of the page
 * Usage: <@markContent id=nodeRef title=mytitle formId=form nestedMarker='true'/>
 * id - The mandatory id attribute specifies the NodeRef of the Alfresco node to be edited. 
 * title - The mandatory title attribute defines a descriptive title for the editable area 
 * being marked. The title used will be used in the quick edit drop down menu of editable 
 * items, as the title of form edit popup/dialog and the 'alt' text and tooltip text of the 
 * edit icon. 
 * formId - The optional formId attribute specifies which form will be used when the marked 
 * area is edited. See the Form Configuration section below for more details.
 * nestedMarker - The optional nestedMarker attribute defines whether the editable area is 
 * nested within another HTML tag that represents the content being edited. If set to "true" 
 * the whole parent element is highlighted when the area is selected in the quick edit drop 
 * down menu. If set to "false" only the edit icon is highlighted.
 *  
 * @author Gavin Cornwell
 * @author Chris Lack
 */
public class WebEditorMarkContentDirective extends AbstractTemplateDirective
{
    private static final Log logger = LogFactory.getLog(WebEditorMarkContentDirective.class);

	@SuppressWarnings("unchecked")
    @Override
    public void execute(Environment env, 
    		            Map params, 
    		            TemplateModel[] loopVars,
            			TemplateDirectiveBody body) throws TemplateException, IOException
    {
		SimpleScalar idParam = (SimpleScalar)params.get("id");
		SimpleScalar titleParam = (SimpleScalar)params.get("title");
		SimpleScalar formIdParam = (SimpleScalar)params.get("formId");
		SimpleScalar nestedMarkerParam = (SimpleScalar)params.get("nestedMarker");

		if (idParam == null || titleParam == null) 
		{
			throw new TemplateModelException("id and title parameters are mandatory for markContent directive");
		}

		String contentId = idParam.getAsString();
		//String safeId = URLEncoder.encode(contentId, "UTF-8");
		String contentTitle = titleParam.getAsString();
		String formId = null;
		boolean nestedMarker = false;
		if (formIdParam != null) formId = formIdParam.getAsString();
		if (nestedMarkerParam != null) nestedMarker = "true".equals(nestedMarkerParam.getAsString()); 

        if (isEditingEnabled(env))
        {
            try
            {
                Writer out = env.getOut();
                HttpServletRequest request = getRequest(env);
                
                // get the prefix URL to the AWE assets
                String urlPrefix = getWebEditorUrlPrefix(env);

                // generate a unique id for this marked content
                List<MarkedContent> markedContent = AlfrescoTagUtil.getMarkedContent(request);
                String markerIdPrefix = (String) request.getAttribute(AlfrescoTagUtil.KEY_MARKER_ID_PREFIX);
                String markerId = markerIdPrefix + "-" + (markedContent.size() + 1);

                // create marked content object and store
                MarkedContent content = new MarkedContent(markerId, contentId, contentTitle, formId, nestedMarker);
                markedContent.add(content);

                // render edit link for content
                out.write("<span class=\"alfresco-content-marker\" id=\"");
                out.write(markerId);
                out.write("\"><a href=\"");
                out.write(urlPrefix);
                out.write("/page/metadata?nodeRef=");
                out.write(contentId);
                //out.write("workspace://SpacesStore/d7aa3119-5ca9-4fb0-9f4d-b8b4febc4b7b"); //contentId);
                out.write("&js=off");
                out.write("&title=");
                out.write(URLEncoder.encode(contentTitle, "UTF-8"));

                String redirectUrl = calculateRedirectUrl(request);
                if (redirectUrl != null)
                {
                    out.write("&redirect=");
                    out.write(redirectUrl);
                }

                if (formId != null)
                {
                    out.write("&formId=");
                    out.write(formId);
                }

                out.write("\"><img src=\"");
                out.write(urlPrefix);
                out.write("/res/awe/images/edit.png\" alt=\"");
                out.write(encode(contentTitle));
                out.write("\" title=\"");
                out.write(encode(contentTitle));
                out.write("\"border=\"0\" /></a></span>\n");

                if (logger.isDebugEnabled())
                    logger.debug("Completed markContent rendering for: " + content);
            }
            catch (IOException ioe)
            {
                throw new TemplateModelException(ioe.toString());
            }
        }
        else if (logger.isDebugEnabled())
        {
            logger.debug("Skipping markContent rendering as editing is disabled");
        }
		
    }
	
    /**
     * Calculates the redirect url for form submission, this will
     * be the current request URL.
     * 
     * @return The redirect URL
     */
    private String calculateRedirectUrl(HttpServletRequest request)
    {
        // NOTE: This may become configurable in the future, for now
        //       this just returns the current page's URI

        String redirectUrl = null;
        try
        {
        	// Build the redirect URL up bit by bit to avoid getting /service/ included.
        	String fullUrl = request.getRequestURL().toString();
        	int firstSep = fullUrl.indexOf("/", 7);
            StringBuffer url = new StringBuffer();
            url.append(fullUrl.substring(0, firstSep));
            url.append(request.getContextPath());
            url.append(request.getPathInfo());            
            String queryString = request.getQueryString();
            if (queryString != null)
            {
                url.append("?").append(queryString);
            }

            redirectUrl = URLEncoder.encode(url.toString(), "UTF-8");
        }
        catch (UnsupportedEncodingException uee)
        {
            // just return null
        }

        return redirectUrl;
    }	
}

	