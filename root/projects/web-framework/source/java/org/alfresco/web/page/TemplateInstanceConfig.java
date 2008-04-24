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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.web.scripts.Store;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * Class representing the configuration for a template - the configuration of a template and
 * the template FTL represents a single template instance.
 * 
 * @author Kevin Roast
 */
public class TemplateInstanceConfig
{
   /** unique ID of this template instance */
   private String templateId;
   
   /** the template type (FTL+JS) used to render this template instance */
   private String templateType;
   
   /** custom properties defined for this template config*/
   private Map<String, String> properties = new HashMap<String, String>(8, 1.0f);
   
   
   /**
    * Construction
    * 
    * @param store      Store to load the template from
    * @param path       Path to the template instance config XML
    */
   TemplateInstanceConfig(Store store, String path)
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
         if (!rootElement.getName().equals("template-instance"))
         {
            throw new AlfrescoRuntimeException(
                  "Expected 'template-instance' root element in template instance config: " + path);
         }
         
         // template Id is implied by the unique path to it
         this.templateId = path;
         
         Element templateElement = rootElement.element("template-type");
         if (templateElement == null && templateElement.getTextTrim() == null)
         {
            throw new AlfrescoRuntimeException(
                  "No 'template-type' element found in template instance config: " + path);
         }
         this.templateType = templateElement.getTextTrim();
         
         // store any additional template config properties
         if (rootElement.element("properties") != null)
         {
            for (Element p : (List<Element>)rootElement.element("properties").elements())
            {
               this.properties.put(p.getName(), p.getTextTrim());
            }
         }
      }
      catch (IOException ioErr)
      {
         throw new AlfrescoRuntimeException("Failed to load template config for instance: " + path, ioErr);
      }
      catch (DocumentException docErr)
      {
         throw new AlfrescoRuntimeException("Failed to parse template config for instance: " + path, docErr);
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

   /**
    * @return the template type for this instance
    */
   public String getTemplateType()
   {
      return this.templateType;
   }
   
   /**
    * @return the map of name/value pair custom properties from the template instance config
    */
   public Map<String, String> getPropetries()
   {
      return this.properties;
   }
}