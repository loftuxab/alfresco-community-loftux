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
 * Simple structure class representing a single component instance on a page.
 * 
 * @author Kevin Roast
 */
public class PageComponent
{
   /**
    * Enum representing the frame around the component:
    * 
    * NONE   - do not render any outer frame
    * DIV    - render a DIV with generated id - the id will be available in the context as "divId"
    * IFRAME - render an IFrame with the URL pointing to the component - must ref a "full page" webscript
    */
   public enum ComponentFrameType
   {
      NONE, DIV, IFRAME;
   }
   
   
   /** unique Id of the component - a combination of scope, regionId and sourceId */
   private String id;
   
   /** 
    * source Id for this component binding - related to the scope:
    *  scope=global - "global"
    *  scope=template - the source template type name
    *  scope=page - the source page id
    **/
   private String sourceId;
   
   /** the region ID for this component binding */
   private String regionId;
   
   /** the scope of this component binding - one of "global", "template" or "page" */
   private String scope;
   
   /** URL to the webscript UI component for this binding */
   private String url;
   
   private long lastModified;
   
   /** component chrome frame type */
   private ComponentFrameType frameType = ComponentFrameType.DIV;
   
   /** user defined properties for this component instance */
   private Map<String, String> properties = new HashMap<String, String>(4, 1.0f);
   
   
   /**
    * Construction.
    * 
    * @param store   Store to load component config from
    * @param path    Path to the component config
    */
   PageComponent(Store store, String path)
   {
      // read config for this page component
      InputStream is = null;
      try
      {
         this.lastModified = store.lastModified(path);
         
         // parse component instance xml config file
         // TODO: convert to pull parser to optimize (see importer ViewParser)
         SAXReader reader = new SAXReader();
         is = store.getDocument(path);
         Document document = reader.read(is);
         
         Element rootElement = document.getRootElement();
         if (!rootElement.getName().equals("component"))
         {
            throw new AlfrescoRuntimeException(
                  "Expected 'component' root element in component config: " + path);
         }
         
         if (rootElement.element("source-id") == null)
         {
             throw new AlfrescoRuntimeException(
                  "Expected 'source-id' element on root element in component config: " + path);
         }
         this.sourceId = rootElement.elementTextTrim("source-id");
         
         if (rootElement.element("region-id") == null)
         {
             throw new AlfrescoRuntimeException(
                  "Expected 'region-id' element on root element in component config: " + path);
         }
         this.regionId = rootElement.elementTextTrim("region-id");
         
         if (rootElement.element("scope") == null)
         {
             throw new AlfrescoRuntimeException(
                  "Expected 'scope' element on root element in component config: " + path);
         }
         this.scope = rootElement.elementTextTrim("scope");
         
         if (rootElement.element("url") == null)
         {
             throw new AlfrescoRuntimeException(
                  "Expected 'url' element on root element in component config: " + path);
         }
         this.url = rootElement.elementTextTrim("url");
         
         // read component chrome config
         if (rootElement.element("chrome") != null)
         {
            Element chromeElement = rootElement.element("chrome");
            if (chromeElement.elementTextTrim("frame") != null)
            {
               String frame = chromeElement.elementTextTrim("frame");
               try
               {
                  this.frameType = ComponentFrameType.valueOf(frame.toUpperCase());
               }
               catch (IllegalArgumentException e)
               {
                  throw new AlfrescoRuntimeException(
                        "Failed to parse component frame type, was expecting one of 'none', 'div' or 'iframe'.", e);
               }
            }
         }
         
         // store any additional custom component properties
         if (rootElement.element("properties") != null)
         {
            for (Element p : (List<Element>)rootElement.element("properties").elements())
            {
               this.properties.put(p.getName(), p.getTextTrim());
            }
         }
         
         // construct the component ID - this is used for lookup and reference of this component
         this.id = buildComponentId(this.scope, this.regionId, this.sourceId);
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

   /**
    * @return the unique id reference
    */
   public String getId()
   {
      return this.id;
   }
   
   /**
    * @return the region Id
    */
   public String getRegionId()
   {
      return this.regionId;
   }

   /**
    * @return the scope (one of "global", "template" or "page")
    */
   public String getScope()
   {
      return this.scope;
   }

   /**
    * @return the source Id - relative to the scope
    */
   public String getSourceId()
   {
      return this.sourceId;
   }

   /**
    * @return the ui component url
    */
   public String getUrl()
   {
      return this.url;
   }
   
   /**
    * @return the frame type for this component
    */
   public ComponentFrameType getFrameType()
   {
      return this.frameType;
   }
   
   /**
    * @return the last modified timestamp of the component config
    */
   public long getLastModified()
   {
      return this.lastModified;
   }

   @Override
   public String toString()
   {
      return "Component: " + getId() + " URL: " + getUrl() + " FrameType: " + getFrameType() +
             " Properties: " + getProperties().toString(); 
   }

   /**
    * @return the properties
    */
   public Map<String, String> getProperties()
   {
      return this.properties;
   }
   
   /**
    * Helper to return the unique ID for this component binding - based on scope, region-id and source-id
    * 
    * @return unique ID for this component binding
    */
   public static String buildComponentId(String scope, String regionId, String sourceId)
   {
      return scope + "." + regionId + (scope.equalsIgnoreCase("global") ? "" : ("." + sourceId));
   }
}
