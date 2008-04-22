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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * JSR-311 (Jax-RS) URI Index
 *   
 * @author davidc
 */
public class JaxRSUriIndex implements UriIndex
{
    // Logger
    private static final Log logger = LogFactory.getLog(JaxRSUriIndex.class);
    
    // map of web scripts by url
    private Map<IndexEntry, IndexEntry> index = new TreeMap<IndexEntry, IndexEntry>(COMPARATOR);
    
    
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
     * @see org.alfresco.web.scripts.UriIndex#findWebScript(java.lang.String, java.lang.String)
     */
    public Match findWebScript(String method, String uri)
    {
        IndexEntry pathMatch = null;
        Map<String, String> varsMatch = null;
        Match scriptMatch = null;
        String match = uri;
        String matchNoExt = ((uri.indexOf('.') != -1) ? uri.substring(0, uri.indexOf('.')) : uri);
        method = method.toUpperCase();

        // locate full match - on URI and METHOD
        for (IndexEntry entry : index.keySet())
        {
            String test = entry.getIncludeExtension() ? match : matchNoExt;
            Map<String, String> vars = entry.getTemplate().match(test);
            if (vars != null)
            {
                pathMatch = entry;
                varsMatch = vars;
                if (entry.getMethod().equals(method))
                {
                    scriptMatch = new Match(entry.getTemplate().getTemplate(), vars, entry.getStaticTemplate(), entry.getScript()); 
                    break;
                }
            }
        }
        
        // locate URI match
        if (scriptMatch == null && pathMatch != null)
        {
            scriptMatch = new Match(pathMatch.getTemplate().getTemplate(), varsMatch, pathMatch.getStaticTemplate());
        }
        
        return scriptMatch;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.UriIndex#registerUri(org.alfresco.web.scripts.WebScript, java.lang.String)
     */
    public void registerUri(WebScript script, String uri)
    {
        Description desc = script.getDescription();
        boolean extension = true;

        // trim uri parameters
        int queryArgIdx = uri.indexOf('?');
        if (queryArgIdx != -1)
        {
            uri = uri.substring(0, queryArgIdx);
        }
        
        // trim extension, only if script distinguishes response format via the extension
        if (desc.getFormatStyle() != Description.FormatStyle.argument)
        {
            int extIdx = uri.lastIndexOf(".");
            if (extIdx != -1)
            {
                uri = uri.substring(0, extIdx);
            }
            extension = false;
        }
        
        // index service ensuring no other service has already claimed the url
        IndexEntry entry = new IndexEntry(desc.getMethod(), new UriTemplate(uri), extension, script);
        if (index.containsKey(entry))
        {
            IndexEntry existingEntry = index.get(entry);
            WebScript existingService = existingEntry.getScript();
            if (!existingService.getDescription().getId().equals(desc.getId()))
            {
                String msg = "Web Script document " + desc.getDescPath() + " is attempting to define the url '" + entry + "' already defined by " + existingService.getDescription().getDescPath();
                throw new WebScriptException(msg);
            }
        }
        else
        {
            index.put(entry, entry);
            if (logger.isTraceEnabled())
                logger.trace("Indexed URI '" + uri + "' as '" + entry.getTemplate() + "'");
        }
    }

    /**
     * Comparator for ordering Uri Templates index entries according to JSR-311
     */
    static final Comparator<IndexEntry> COMPARATOR = new Comparator<IndexEntry>()
    {
        public int compare(IndexEntry o1, IndexEntry o2)
        {
            if (o1 == null && o2 == null)
            {
                return 0;
            }
            if (o1 == null)
            {
                return 1;
            }
            if (o2 == null)
            {
                return -1;
            }

            // primary key: order by number of static characters in URI template
            int i = o2.getTemplate().getStaticCharCount() - o1.getTemplate().getStaticCharCount();
            if (i != 0)
            {
                return i;
            }

            // secondary key: order by number of template vars
            i = o2.getTemplate().getVariableNames().length - o1.getTemplate().getVariableNames().length;
            if (i != 0)
            {
                return i;
            }

            // order by uri template regular expression
            i = o2.getTemplate().getRegex().pattern().compareTo(o1.getTemplate().getRegex().pattern());
            if (i != 0)
            {
                return i;
            }
            
            // implementation specific: order by http method
            return o2.method.compareTo(o1.method);
        }
    };

    /**
     * URI Index Entry
     * 
     * @author davidc
     */
    static class IndexEntry
    {
        private String method;
        private UriTemplate template;
        private WebScript script;
        private boolean includeExtension;
        private String staticTemplate;
        private final String key;

        /**
         * Construct
         * 
         * @param method   http method
         * @param template  uri template
         * @param includeExtension  include uri extension in index
         * @param script  associated web script
         */
        IndexEntry(String method, UriTemplate template, boolean includeExtension, WebScript script)
        {
            this.method = method.toUpperCase();
            this.template = template;
            this.includeExtension = includeExtension;
            this.script = script;
            this.key = template.getRegex() + ":" + this.method;
            int firstTokenIdx = template.getTemplate().indexOf('{');
            this.staticTemplate = (firstTokenIdx == -1) ? template.getTemplate() : template.getTemplate().substring(0, firstTokenIdx);
        }

        /**
         * @return  http method
         */
        public String getMethod()
        {
            return method;
        }

        /**
         * @return  uri template
         */
        public UriTemplate getTemplate()
        {
            return template;
        }
        
        /**
         * @return  static prefix of uri template
         */
        public String getStaticTemplate()
        {
            return staticTemplate;
        }

        /**
         * @return  includes uri extension in index
         */
        public boolean getIncludeExtension()
        {
            return includeExtension;
        }

        /**
         * @return  associated web script
         */
        public WebScript getScript()
        {
            return script;
        }
        
        @Override
        public final String toString()
        {
            return key;
        }
        
        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof IndexEntry))
            {
                return false;
            }
            return key.equals(((IndexEntry)obj).key);
        }

        @Override
        public int hashCode()
        {
            return key.hashCode();
        }
    }
}
