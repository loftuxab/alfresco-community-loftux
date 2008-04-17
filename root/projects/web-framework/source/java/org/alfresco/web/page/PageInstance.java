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

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.web.page.PageRendererServlet.URLHelper;
import org.alfresco.web.scripts.Match;
import org.alfresco.web.scripts.Registry;
import org.alfresco.web.scripts.Store;
import org.alfresco.web.scripts.TemplateProcessor;
import org.alfresco.web.scripts.WebScript;
import org.alfresco.web.scripts.Description.RequiredAuthentication;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * Simple structure class representing the definition of a single page instance
 * 
 * @author Kevin Roast
 */
public class PageInstance
{
   /** unique ID of this page */
   private String pageId;
   
   /** the template instance for rendering this page */
   private String template;
   
   /** html title of the page */
   private String title;
   
   /** user defined description of the page */
   private String description;
   
   /** theme override for this page (if any) */
   private String theme;
   
   /** Authentication type required to view this page */
   private RequiredAuthentication authentication;
   
   /** the ordered map of components resolved for the regions on this page */
   private Map<String, PageComponent> components = new LinkedHashMap<String, PageComponent>();
   
   
   /**
    * Construction
    * 
    * @param store      Store to load the template from
    * @param path       Path to the template instance config XML
    */
   PageInstance(Store store, String path)
   {
      // read config for this page instance
      InputStream is = null;
      try
      {
         // parse page definition xml config file
         // TODO: convert to pull parser to optimize (see importer ViewParser)
         SAXReader reader = new SAXReader();
         is = store.getDocument(path);
         Document document = reader.read(is);
         
         Element rootElement = document.getRootElement();
         if (!rootElement.getName().equals("page"))
         {
            throw new AlfrescoRuntimeException(
                  "Expected 'page' root element in page definition config: " + path);
         }
         if (rootElement.element("id") == null)
         {
             throw new AlfrescoRuntimeException(
                  "Expected 'id' element on page element in page definition config: " + path);
         }
         this.pageId = rootElement.elementTextTrim("id");
         this.title = rootElement.elementTextTrim("title");
         this.description = rootElement.elementTextTrim("description");

         Element templateElement = rootElement.element("template-instance");
         if (templateElement == null && templateElement.getTextTrim() == null)
         {
            throw new AlfrescoRuntimeException(
                  "No 'template-instance' element found in page definition config: " + path);
         }
         this.template = templateElement.getTextTrim();
         
         Element authElement = rootElement.element("authentication");
         if (authElement != null)
         {
            try
            {
               this.authentication = RequiredAuthentication.valueOf(authElement.getTextTrim().toLowerCase());
            }
            catch (IllegalArgumentException enumErr)
            {
               throw new AlfrescoRuntimeException(
                     "Invalid component <authentication> element value: " + authElement.getTextTrim());
            }
         }
      }
      catch (IOException ioErr)
      {
         throw new AlfrescoRuntimeException("Failed to load page definition for page: " + path, ioErr);
      }
      catch (DocumentException docErr)
      {
         throw new AlfrescoRuntimeException("Failed to parse page definition for page: " + path, docErr);
      }
      finally
      {
         try
         {
            if (is != null) is.close();
         }
         catch (IOException e) {} // NOTE: ignoring close exception
      }
   }
   
   PageInstance(String pageId, String template)
   {
      this.pageId = pageId;
      this.template = template;
   }

   @Override
   public String toString()
   {
      StringBuilder buf = new StringBuilder(256);
      buf.append("PageId: ").append(pageId);
      buf.append(", Template: ").append(template);
      for (String id : components.keySet())
      {
         buf.append("\r\n   ").append(components.get(id).toString());
      }
      return buf.toString();
   }

   /**
    * @return the pageId
    */
   public String getPageId()
   {
      return this.pageId;
   }

   /**
    * @return the template
    */
   public String getTemplate()
   {
      return template;
   }

   /**
    * @param title the title to set
    */
   public void setTitle(String title)
   {
      this.title = title;
   }

   /**
    * @return the title
    */
   public String getTitle()
   {
      return title;
   }

   /**
    * @param description the description to set
    */
   public void setDescription(String description)
   {
      this.description = description;
   }

   /**
    * @return the description
    */
   public String getDescription()
   {
      return description;
   }

   /**
    * @param theme the theme to set
    */
   public void setTheme(String theme)
   {
      this.theme = theme;
   }

   /**
    * @return the theme
    */
   public String getTheme()
   {
      return theme;
   }

   /**
    * @param authentication the authentication to set
    */
   public void setAuthentication(RequiredAuthentication authentication)
   {
      this.authentication = authentication;
   }

   /**
    * @return the authentication
    */
   public RequiredAuthentication getAuthentication()
   {
      return authentication;
   }

   /**
    * @param component  the component to set (will overwrite old if same id)
    */
   public void setComponent(PageComponent component)
   {
      this.components.put(component.getId(), component);
   }

   /**
    * @return the component
    */
   public PageComponent getComponent(String id)
   {
      return this.components.get(id);
   }
   
   /**
    * @return An object capable of rendering the optional .head.ftl header templates for
    *         all components on the page when the Object.toString() method is called.
    */ 
   public Object getHeaderRenderer(
         final Registry registry, final TemplateProcessor processor, final URLHelper urlHelper)
   {
      return new Object()
      {
         @Override
         public String toString()
         {
            if (components.size() == 0) return "";
            
            Set<String> paths = new HashSet<String>(8);
            
            // template model - very simple as we only provide 'url.context' and 'theme'
            Map<String, Object> model = new HashMap<String, Object>(4, 1.0f);
            model.put("theme", theme);
            model.put("url", urlHelper);
            
            // for each page component, find the optional .head.ftl template associated with
            // its webscript - execute each template in turn and return the completed set
            StringWriter writer = new StringWriter(512);
            for (PageComponent component : components.values())
            {
               // calculate webscript url from the complete component url
               String url = component.getUrl();
               if (url.lastIndexOf('?') != -1)
               {
                  url = url.substring(0, url.lastIndexOf('?'));
               }
               Match match = registry.findWebScript("GET", url);
               if (match != null)
               {
                  WebScript webScript = match.getWebScript();
                  if (webScript != null)
                  {
                     // found a webscript, build the path to the .head.ftl template
                     String path = webScript.getDescription().getId() + ".head.ftl";
                     // ensure we render each path once only - no script/css duplicates
                     if (paths.contains(path) == false && processor.hasTemplate(path))
                     {
                        // add the 'htmlid' for the component so it is available to script writers
                        model.put(PageComponent.PROP_HTMLID, component.getId());
                        // render the head template for this component
                        processor.process(path, model, writer);
                     }
                     paths.add(path);
                  }
               }
            }
            return writer.toString();
         }
      };
   }
}
