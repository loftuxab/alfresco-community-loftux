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
package org.alfresco.web.scripts.servlet;

import java.io.IOException;
import java.util.List;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.web.scripts.SearchPath;
import org.alfresco.web.scripts.Store;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * Application URL - matching and Page instance resolving.
 * 
 * @author Kevin Roast
 */
public class ApplicationUrl
{
   private SearchPath searchPath;

   public ApplicationUrl(SearchPath searchPath)
   {
      this.searchPath = searchPath;
   }

   public boolean match(String resource)
   {
      // TODO: match against the supplied resource url - is it one of our URLs?
      return false;
   }

   /**
    * Resolve appropriate page definition from local store/repo store based on uri resource.
    * 
    * @param resource
    * 
    * @return PageInstance structure
    * 
    * @exception AlfrescoRuntimeException if unable to locate Page for specified resource.
    */
   public PageInstance getPageInstance(String resource)
   {
      // TODO: Return from repo with details of template (either where to get it or the template
      //       content itself) and the configuration of all ui components.
      //       Add this data to the PageInstance structure that represents this page.
      //       For now the SearchPath will lookup local page definitions in a basic folder structure.
      // TODO: Decompose the resource url to lookup in correct store etc.
      PageInstance page = null;
      String pageDef = resource + ".xml";
      for (Store store : searchPath.getStores())
      {
         if (store.hasDocument(pageDef))
         {
            // read config for this page instance
            try
            {
               // parse page definition xml config file
               // TODO: convert to pull parser to optimize (see importer ViewParser)
               SAXReader reader = new SAXReader();
               Document document = reader.read(store.getDocument(pageDef));

               Element rootElement = document.getRootElement();
               if (!rootElement.getName().equals("page"))
               {
                  throw new AlfrescoRuntimeException(
                        "Expected 'page' root element in page definition config: " + pageDef);
               }
               String title = rootElement.elementTextTrim("title");
               String description = rootElement.elementTextTrim("description");

               Element templateElement = rootElement.element("template");
               if (templateElement == null && templateElement.getTextTrim() == null)
               {
                  throw new AlfrescoRuntimeException(
                        "No 'template' element found in page definition config: " + pageDef);
               }
               String templateName = templateElement.getTextTrim();

               // create config object for this page and store template name for this page
               page = new PageInstance(templateName);
               page.Title = title;
               page.Description = description;

               // TODO: do we have default config? where does it come from?
               // copy in component mappings from default config definitions first
               //if (defaultPageDef != null)
               //{
               //   pageDef.Components.putAll(defaultPageDef.Components);
               //}

               // read the component defs for this page
               Element componentsElements = rootElement.element("components");
               if (componentsElements != null)
               {
                  for (Element ce : (List<Element>)componentsElements.elements("component"))
                  {
                     // read the mandatory component 'id' attribute
                     String id = ce.attributeValue("id");
                     if (id == null || id.length() == 0)
                     {
                        throw new AlfrescoRuntimeException(
                              "A 'component' element is missing mandatory 'id' attribute in page definition config: " + pageDef);
                     }

                     // next check for the 'disabled' boolean attribute - used to disable components
                     // that are specified in the default config - we don't need to read further
                     /*String disabled = ce.attributeValue("disabled");
                     if (disabled != null)
                     {
                        if (Boolean.parseBoolean(disabled) == true)
                        {
                           pageDef.Components.remove(id);
                           continue;
                        }
                     }*/

                     String url = ce.attributeValue("url");
                     if (url == null || url.length() == 0)
                     {
                        throw new AlfrescoRuntimeException(
                              "A 'component' element is missing mandatory 'url' attribute in page definition config: " + pageDef);
                     }

                     PageComponent component = new PageComponent(id, url);

                     // store any additional component properties
                     if (ce.element("properties") != null)
                     {
                        for (Element p : (List<Element>)ce.element("properties").elements())
                        {
                           component.Properties.put(p.attributeValue("name"), p.getTextTrim());
                        }
                     }

                     // add component mapping to the page definition
                     page.Components.put(component.Id, component);
                  }
               }
            }
            catch (IOException ioErr)
            {
               throw new AlfrescoRuntimeException("Failed to load page definition for page " + pageDef, ioErr);
            }
            catch (DocumentException docErr)
            {
               throw new AlfrescoRuntimeException("Failed to parse page definition for page " + pageDef, docErr);
            }

            break;
         }
      }
      if (page == null)
      {
         throw new AlfrescoRuntimeException("Unable to locate page instance for resource: " + resource);
      }
      return page;
   }
}
