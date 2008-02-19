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
package org.alfresco.web.scripts;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;


/**
 * Uri index supporting simple URI templates where matching is performed
 * on static prefix of the URI.
 * 
 * e.g. /a/{b} is matched on /a
 * 
 * Note: this index was used until Alfresco v3.0
 * 
 * @author davidc
 */
public class PrefixTemplateUriIndex implements UriIndex
{
    // map of web scripts by url
    // NOTE: The map is sorted by url (descending order)
    private Map<String, IndexEntry> index = new TreeMap<String, IndexEntry>(Collections.reverseOrder());
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.UriIndex#clear()
     */
    public void clear()
    {
        index.clear();
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.UriIndex#getSize()
     */
    public int getSize()
    {
        return index.size();
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.UriIndex#registerUri(org.alfresco.web.scripts.WebScript, java.lang.String)
     */
    public void registerUri(WebScript script, String uri)
    {
        Description desc = script.getDescription();
        
        // establish static part of url template
        boolean wildcard = false;
        boolean extension = true;
        int queryArgIdx = uri.indexOf('?');
        if (queryArgIdx != -1)
        {
            uri = uri.substring(0, queryArgIdx);
        }
        int tokenIdx = uri.indexOf('{');
        if (tokenIdx != -1)
        {
            uri = uri.substring(0, tokenIdx);
            wildcard = true;
        }
        if (desc.getFormatStyle() != Description.FormatStyle.argument)
        {
            int extIdx = uri.lastIndexOf(".");
            if (extIdx != -1)
            {
                uri = uri.substring(0, extIdx);
            }
            extension = false;
        }
        
        // index service by static part of url (ensuring no other service has already claimed the url)
        String uriIdx = desc.getMethod() + ":" + uri;
        if (index.containsKey(uriIdx))
        {
            IndexEntry urlIndex = index.get(uriIdx);
            WebScript existingService = urlIndex.script;
            if (!existingService.getDescription().getId().equals(desc.getId()))
            {
                String msg = "Web Script document " + desc.getDescPath() + " is attempting to define the url '" + uriIdx + "' already defined by " + existingService.getDescription().getDescPath();
                throw new WebScriptException(msg);
            }
        }
        else
        {
            IndexEntry urlIndex = new IndexEntry(uri, wildcard, extension, script);
            index.put(uriIdx, urlIndex);
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.UriIndex#findWebScript(java.lang.String, java.lang.String)
     */
    public Match findWebScript(String method, String uri)
    {
        String matchedPath = null;
        Match scriptMatch = null;
        String match = method.toUpperCase() + ":" + uri;
        String matchNoExt = method.toUpperCase() + ":" + ((uri.indexOf('.') != -1) ? uri.substring(0, uri.indexOf('.')) : uri);
        
        // locate full match - on URI and METHOD
        for (Map.Entry<String, IndexEntry> entry : index.entrySet())
        {
            IndexEntry urlIndex = entry.getValue();
            String index = entry.getKey();
            String test = urlIndex.includeExtension ? match : matchNoExt; 
            if ((urlIndex.wildcardPath && test.startsWith(index)) || (!urlIndex.wildcardPath && test.equals(index)))
            {
                scriptMatch = new Match(urlIndex.path, null, urlIndex.path, urlIndex.script); 
                break;
            }
            else if ((urlIndex.wildcardPath && uri.startsWith(urlIndex.path)) || (!urlIndex.wildcardPath && uri.equals(urlIndex.path)))
            {
                matchedPath = urlIndex.path;
            }
        }
        
        // locate URI match
        if (scriptMatch == null && matchedPath != null)
        {
            scriptMatch = new Match(matchedPath, null, matchedPath);
        }
        
        return scriptMatch;
    }
    
    /**
     * Index Entry
     */
    private static class IndexEntry
    {
        private IndexEntry(String path, boolean wildcardPath, boolean includeExtension, WebScript script)
        {
            this.path = path;
            this.wildcardPath = wildcardPath;
            this.includeExtension = includeExtension;
            this.script = script;
        }
        
        private String path;
        private boolean wildcardPath;
        private boolean includeExtension;
        private WebScript script;
    }

}
