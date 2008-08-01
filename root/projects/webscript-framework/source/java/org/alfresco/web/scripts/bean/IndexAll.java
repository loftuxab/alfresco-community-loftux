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
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.web.scripts.bean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.web.scripts.DeclarativeWebScript;
import org.alfresco.web.scripts.Path;
import org.alfresco.web.scripts.PathImpl;
import org.alfresco.web.scripts.Status;
import org.alfresco.web.scripts.WebScript;
import org.alfresco.web.scripts.WebScriptRequest;


/**
 * Index of all Web Scripts
 * 
 * @author davidc
 */
public class IndexAll extends DeclarativeWebScript
{

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.DeclarativeWebScript#executeImpl(org.alfresco.web.scripts.WebScriptRequest, org.alfresco.web.scripts.WebScriptResponse)
     */
    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status)
    {
        String packageFilter = req.getParameter("package") != null ? req.getParameter("package") : "/";
        String urlFilter = req.getParameter("url") != null ? req.getParameter("url") : "/";
        String familyFilter = req.getParameter("family") != null ? req.getParameter("family") : "/";

        // filter web scripts
    	Collection<WebScript> scripts = getContainer().getRegistry().getWebScripts();
    	Collection<WebScript> filteredWebScripts = new ArrayList<WebScript>();
    	for (WebScript script : scripts)
    	{
    		if (includeWebScript(script, packageFilter, urlFilter, familyFilter))
    		{
    			filteredWebScripts.add(script);
    		}
    	}

    	// filter packages
        Path rootPackage = getContainer().getRegistry().getPackage("/");
    	Path filteredPackage = filterPath(null, rootPackage, packageFilter, urlFilter, familyFilter);

        // setup model
        Map<String, Object> model = new HashMap<String, Object>(7, 1.0f);
        model.put("description", req.getParameter("desc"));
        model.put("webscripts", filteredWebScripts);
        model.put("rootpackage", filteredPackage);
    	model.put("packageFilter", packageFilter);
    	model.put("urlFilter", urlFilter);
    	model.put("familyFilter", familyFilter);
    	return model;
    }
    
    /**
     * Filter Path by given filters
     * 
     * @param filteredParent  parent path
     * @param path  path to filter
     * @param packageFilter
     * @param urlFilter
     * @param familyFilter
     * @return
     */
    private PathImpl filterPath(PathImpl filteredParent, Path path, String packageFilter, String urlFilter, String familyFilter)
    {
    	PathImpl filteredPath = filteredParent == null ? new PathImpl(path.getPath()) : filteredParent.createChildPath(path.getName());
    	
    	// filter web scripts in package
    	for (WebScript script : path.getScripts())
    	{
    		if (includeWebScript(script, packageFilter, urlFilter, familyFilter))
    		{
    			filteredPath.addScript(script);
    		}
    	}

    	// process path children
    	for (Path child : path.getChildren())
    	{
    		filterPath(filteredPath, child, packageFilter, urlFilter, familyFilter);
    	}
    	
    	return filteredPath;
    }
    
    /**
     * Include Web Script given filters?
     * 
     * @param script
     * @param packageFilter
     * @param urlFilter
     * @param familyFilter
     * @return
     */
    private boolean includeWebScript(WebScript script, String packageFilter, String urlFilter, String familyFilter)
    {
    	// is it in the package
    	if (!script.getDescription().getPackage().toString().startsWith(packageFilter))
    	{
    		return false;
    	}
    	
		// is it in the family
    	if (script.getDescription().getFamily() == null && !familyFilter.equals("/"))
    	{
    		return false;
    	}
		if (script.getDescription().getFamily() != null && !script.getDescription().getFamily().startsWith(familyFilter))
		{
			return false;
		}
		
		// is it in the url
		String[] uris = script.getDescription().getURIs();
		for (String uri : uris)
		{
			if (!uri.startsWith(urlFilter))
			{
				return false;
			}
		}
		
		return true;
    }
}
