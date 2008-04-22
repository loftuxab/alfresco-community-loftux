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
package org.alfresco.web.page;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.config.ConfigElement;
import org.alfresco.web.scripts.UriTemplate;

/**
 * Index of application URI templates. One or more URI templates can map to a single entry url.
 * Each template uses a simple form of the JAX-RS JSR-311 URI Template format - only basic variables
 * are specified in the URI template for matching.
 * 
 * Example config:
 * <pre>
 *    <!-- simple mapping from a friendly url template to a similarly named page -->
 *    <uri-mapping>
 *       <uri-template>/site/{site}/dashboard</uri-template>
 *       <url-entry>/page/sites/{site}/dashboard</url-entry>
 *    </uri-mapping>
 *    <!-- multiple friendly url templates can map to a single page -->
 *    <uri-mapping>
 *       <uri-template>/user/{user}</uri-template>
 *       <uri-template>/user/{user}/mydashboard</uri-template>
 *       <url-entry>/page/users/{user}/dashboard</url-entry>
 *    </uri-mapping>
 *    <!-- reusable user tool page - note the arbitrary url path suffix mapped to an argument -->
 *    <uri-mapping>
 *       <uri-template>/user/{user}/wiki/{path}</uri-template>
 *       <url-entry>/page/users/tools/wiki?user={user}&article={path}</url-entry>
 *    </uri-mapping>
 * </pre>
 * 
 * @author Kevin Roast
 */
public class UriTemplateIndex
{
   private Map<UriTemplate, String> mappings;
   
   /**
    * Constructor
    * 
    * @param config     ConfigElement pointing to the <uri-mapping> sections (see above)
    */
   public UriTemplateIndex(ConfigElement config)
   {
      List<ConfigElement> mappingElements = config.getChildren("uri-mapping");
      if (mappingElements != null)
      {
         this.mappings = new LinkedHashMap<UriTemplate, String>(mappingElements.size());
         
         for (ConfigElement mappingElement : mappingElements)
         {
            String entry = mappingElement.getChildValue("url-entry");
            if (entry == null || entry.trim().length() == 0)
            {
               throw new IllegalArgumentException("<uri-mapping> config element must contain <url-entry> element value.");
            }
            
            List<ConfigElement> templateElements = mappingElement.getChildren("uri-template");
            if (templateElements.size() == 0)
            {
               throw new IllegalArgumentException("<uri-mapping> config element must contain <uri-template> element(s).");
            }
            for (ConfigElement templateElement : templateElements)
            {
               String template = templateElement.getValue();
               if (template == null || template.trim().length() == 0)
               {
                  throw new IllegalArgumentException("<uri-template> config element must contain a value.");
               }
               
               // build the object to represent the Uri Template
               UriTemplate uriTemplate = new UriTemplate(template);
               
               // store the mapping between the Uri Template and the url entry pattern
               this.mappings.put(uriTemplate, entry);
            }
         }
      }
      else
      {
         this.mappings = Collections.<UriTemplate, String>emptyMap();
      }
   }
   
   /**
    * Search the URI index to locate a match for the specified uri pattern
    * 
    * @param uri  URI to match against the URI Templates in the index
    * 
    * @return URI match with tokens replaced as per the URI Template pattern 
    */
   public String findMatch(String uri)
   {
      for (UriTemplate template : this.mappings.keySet())
      {
         Map<String, String> match = template.match(uri);
         if (match != null)
         {
            // found a uri template match
            // so replace the tokens in the matched page with those from the uri
            return UriUtils.replaceUriTokens(this.mappings.get(template), match);
         }
      }
      
      // if we get here, no match was found
      return null;
   }
}
