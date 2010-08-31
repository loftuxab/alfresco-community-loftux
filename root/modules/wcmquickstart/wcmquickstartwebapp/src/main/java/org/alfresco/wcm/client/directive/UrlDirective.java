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
import java.net.URLEncoder;
import java.util.Map;

import org.alfresco.wcm.client.Asset;
import org.alfresco.wcm.client.Section;
import org.alfresco.wcm.client.util.UrlUtils;

import freemarker.core.Environment;
import freemarker.ext.beans.StringModel;
import freemarker.ext.servlet.HttpRequestHashModel;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * Freemarker directive to output the url of an asset or section.
 * Usage: 
 * <@makeurl section=xxx/> or 
 * <@makeurl asset=xxx force=fff/> where xxx is a variable which references an asset object.
 * The force parameter is optional. If set to 'short' it forces the code to use the short 
 * "asset/<id>/name.ext" style of URL. If set to 'long' a full, friendly URL is generated. 
 * If the force option is omitted then it leaves it to the logic in the UrlUtils class to
 * decide when a short or long URL is appropriate.
 * Alternatively:
 * <@makeurl asset=xxx rendition=rrr/> allows the URL of a rendition of the asset to be 
 * generated.    
 * @author Chris Lack
 */
public class UrlDirective implements TemplateDirectiveModel
{

    private UrlUtils urlUtils;

	@SuppressWarnings("unchecked")
    @Override
    public void execute(Environment env, 
    		            Map params, 
    		            TemplateModel[] loopVars,
            			TemplateDirectiveBody body) throws TemplateException, IOException
    {
		if (params.size() < 1 && params.size() > 2) throw new TemplateModelException("url directive expects one or two parameters");
					
		StringModel assetParam = (StringModel)params.get("asset");
		StringModel sectionParam = (StringModel)params.get("section");
		
		// Optional parameter for asset to get a rendition of it
		SimpleScalar renditionParam = (SimpleScalar)params.get("rendition");

		if ((assetParam == null || ! (assetParam.getWrappedObject() instanceof Asset))
				&& (sectionParam == null || ! (sectionParam.getWrappedObject() instanceof Section))) 
		{
			throw new TemplateModelException("url directive expects asset or section parameter");
		}
		
		SimpleScalar forceParam = (SimpleScalar)params.get("force");
		String force = null;
		if (forceParam != null)
		{
			force = forceParam.getAsString();
		}
				
		// Get the request url
		String requestUrl = ((HttpRequestHashModel)env.getDataModel().get("Request")).getRequest().getContextPath();

		// Build the url for the asset/section
		String url;		
		if (assetParam != null) 
		{
			if (renditionParam != null)
			{
				force = "short";
			}

			Asset asset = (Asset)assetParam.getWrappedObject();
			if ("short".equals(force))
			{
				url = requestUrl+urlUtils.getShortUrl(asset);
			}
			else if ("long".equals(force))
			{
				url = requestUrl+urlUtils.getLongUrl(asset);
			}
			else 
			{
				url = requestUrl+urlUtils.getUrl(asset);
			}
			
			if (renditionParam != null)
			{
				String rendition = renditionParam.getAsString();
				url += "?rendition="+URLEncoder.encode(rendition, "UTF-8");			
			}			
		}
		else 
		{
			Section section = (Section)sectionParam.getWrappedObject();
			url = requestUrl+urlUtils.getUrl(section);			
		}
		
		env.getOut().write(url);
    }
	
	public void setUrlUtils(UrlUtils urlUtils) {
		this.urlUtils = urlUtils;
	}
	
}
