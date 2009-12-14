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
 * http://www.alfresco.com/legal/licensing
 */
package org.alfresco.web.uri;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.extensions.config.ConfigElement;
import org.alfresco.web.scripts.UriTemplate;

/**
 * Index of application URI templates.
 * 
 * Each template uses a simple form of the JAX-RS JSR-311 URI Template format - only basic variables
 * are specified in the URI template for matching.
 * 
 * Example config:
 * <pre>
 *    <uri-templates>
 *       <uri-template>/page/site/{site}/{page}</uri-template>
 *       <uri-template>/page/site/{site}</uri-template>
 *       <uri-template>/page/user/{userid}/{page}</uri-template>
 *       <uri-template>/page/user/{userid}</uri-template>
 *    </uri-templates>
 * </pre>
 * 
 * @author Kevin Roast
 */
public class UriTemplateListIndex
{
    private List<UriTemplate> uriTemplates;
    
    /**
     * Constructor
     * 
     * @param config     ConfigElement pointing to the <uri-templates> sections (see above)
     */
    public UriTemplateListIndex(ConfigElement config)
    {
        List<ConfigElement> uriElements = config.getChildren("uri-template");
        if (uriElements != null)
        {
            this.uriTemplates = new ArrayList<UriTemplate>(uriElements.size());
            
            for (ConfigElement uriElement : uriElements)
            {
                String template = uriElement.getValue();
                if (template == null || template.trim().length() == 0)
                {
                    throw new IllegalArgumentException("<uri-template> config element must contain a value.");
                }
                
                // build the object to represent the Uri Template
                UriTemplate uriTemplate = new UriTemplate(template);
                
                // store the Uri Template
                this.uriTemplates.add(uriTemplate);
            }
        }
        else
        {
            this.uriTemplates = Collections.<UriTemplate>emptyList();
        }
    }
    
    /**
     * Search the URI index to locale a match for the specified URI.
     * If found, return the args that represent the matched URI pattern tokens
     * and the values as per the supplied URI value.
     * 
     * @param uri  URI to match against the URI Templates in the index
     * 
     * @return Map of token args to values or null if no match was found.
     */
    public Map<String, String> findMatch(String uri)
    {
        for (UriTemplate template : this.uriTemplates)
        {
            Map<String, String> match = template.match(uri);
            if (match != null)
            {
                return match;
            }
        }
        
        // if we get here, no match was found
        return null;
    }
}
