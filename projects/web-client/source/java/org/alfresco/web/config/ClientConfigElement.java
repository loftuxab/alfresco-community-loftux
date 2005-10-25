/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.web.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.alfresco.config.ConfigElement;
import org.alfresco.config.element.ConfigElementAdapter;

/**
 * Custom config element that represents config values for the client
 * 
 * @author Kevin Roast
 */
public class ClientConfigElement extends ConfigElementAdapter
{
   public static final String CONFIG_ELEMENT_ID = "client";
   
   // defaults for any config values not supplied
   private int listPageSize = 10;
   private int detailsPageSize = 10;
   private int iconsPageSize = 9;
   private String defaultView = "list";
   private int recentSpacesItems = 6;
   private int searchMinimum = 3;
   private String helpUrl = null;
   private Map<String, String> localeMap = new HashMap<String, String>();
   private List<String> languages = new ArrayList<String>(8);
   private Map<String, String> processors = new HashMap<String, String>();
   
   /**
    * Default Constructor
    */
   public ClientConfigElement()
   {
      super(CONFIG_ELEMENT_ID);
   }
   
   /**
    * Constructor
    * 
    * @param name Name of the element this config element represents
    */
   public ClientConfigElement(String name)
   {
      super(name);
   }

   /**
    * @see org.alfresco.config.element.ConfigElementAdapter#combine(org.alfresco.config.ConfigElement)
    */
   public ConfigElement combine(ConfigElement configElement)
   {
      return null;
   }
   
   /**
    * @return Returns the defaultView.
    */
   public String getDefaultView()
   {
      return this.defaultView;
   }

   /**
    * @param defaultView The defaultView to set.
    */
   /*package*/ void setDefaultView(String defaultView)
   {
      this.defaultView = defaultView;
   }

   /**
    * @return Returns the detailsPageSize.
    */
   public int getDetailsPageSize()
   {
      return this.detailsPageSize;
   }

   /**
    * @param detailsPageSize The detailsPageSize to set.
    */
   /*package*/ void setDetailsPageSize(int detailsPageSize)
   {
      this.detailsPageSize = detailsPageSize;
   }

   /**
    * @return Returns the iconsPageSize.
    */
   public int getIconsPageSize()
   {
      return this.iconsPageSize;
   }

   /**
    * @param iconsPageSize The iconsPageSize to set.
    */
   /*package*/ void setIconsPageSize(int iconsPageSize)
   {
      this.iconsPageSize = iconsPageSize;
   }

   /**
    * @return Returns the listPageSize.
    */
   public int getListPageSize()
   {
      return this.listPageSize;
   }

   /**
    * @param listPageSize The listPageSize to set.
    */
   /*package*/ void setListPageSize(int listPageSize)
   {
      this.listPageSize = listPageSize;
   }

   /**
    * @return Returns the recentSpacesItems.
    */
   public int getRecentSpacesItems()
   {
      return this.recentSpacesItems;
   }

   /**
    * @param recentSpacesItems The recentSpacesItems to set.
    */
   /*package*/ void setRecentSpacesItems(int recentSpacesItems)
   {
      this.recentSpacesItems = recentSpacesItems;
   }
   
   /**
    * Add a language locale and display label to the list.
    * 
    * @param locale     Locale code
    * @param label      Display label
    */
   /*package*/ void addLanguage(String locale, String label)
   {
      this.localeMap.put(locale, label);
      this.languages.add(locale);
   }
   
   /**
    * @return List of supported language locale strings in config file order
    */
   public List<String> getLanguages()
   {
      return this.languages;
   }
   
   /**
    * @param locale     The locale string to lookup language label for
    * 
    * @return the language label for specified locale string, or null if not found
    */
   public String getLabelForLanguage(String locale)
   {
      return this.localeMap.get(locale);
   }
   
   /**
    * Add a template processor to the map.
    * 
    * @param name       Unique name of the template processor
    * @param processor  Fully qualified class name of the template processor
    */
   /*package*/ void addTemplateProcessor(String name, String processor)
   {
      this.processors.put(name, processor);
   }
   
   /**
    * Return the template processor with the specified name
    * 
    * @param name   Unique name of the template processor
    * 
    * @return Template processor or null if not found
    */
   public String getTemplateProcessor(String name)
   {
      return this.processors.get(name);
   }

   /**
    * @return Returns the help Url.
    */
   public String getHelpUrl()
   {
      return this.helpUrl;
   }

   /**
    * @param helpUrl The help Url to set.
    */
   /*package*/ void setHelpUrl(String helpUrl)
   {
      this.helpUrl = helpUrl;
   }

   /**
    * @return Returns the search minimum number of characters.
    */
   public int getSearchMinimum()
   {
      return this.searchMinimum;
   }

   /**
    * @param searchMinimum The searchMinimum to set.
    */
   /*package*/ void setSearchMinimum(int searchMinimum)
   {
      this.searchMinimum = searchMinimum;
   }
   
   
}
