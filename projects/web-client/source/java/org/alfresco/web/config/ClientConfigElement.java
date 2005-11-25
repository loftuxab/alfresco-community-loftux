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
   private String defaultView = "icons";
   
   private int forumsListPageSize = 20;
   private int forumsDetailsPageSize = 20;
   private int forumsIconsPageSize = 20;
   private String defaultForumsView = "list";

   private int forumDetailsPageSize = 20;
   private int forumBubblePageSize = 20;
   private String defaultForumView = "details";

   private int topicDetailsPageSize = 20;
   private int topicBubblePageSize = 20;
   private String defaultTopicView = "details";
   private String defaultTopicSortDir = "ascending";
   
   private int recentSpacesItems = 6;
   private int searchMinimum = 3;
   private String helpUrl = null;
   private Map<String, String> localeMap = new HashMap<String, String>();
   private List<String> languages = new ArrayList<String>(8);
   private String homeSpacePermission = null;
   private List<String> contentTypes = null;
   private List<CustomProperty> customProps = null;
   
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
    * @return Returns the defaultForumsView.
    */
   public String getDefaultForumsView()
   {
      return this.defaultForumsView;
   }

   /**
    * @param defaultForumsView The defaultForumsView to set.
    */
   /*package*/ void setDefaultForumsView(String defaultForumsView)
   {
      this.defaultForumsView = defaultForumsView;
   }

   /**
    * @return Returns the forumsDetailsPageSize.
    */
   public int getForumsDetailsPageSize()
   {
      return this.forumsDetailsPageSize;
   }

   /**
    * @param forumsDetailsPageSize The forumsDetailsPageSize to set.
    */
   /*package*/ void setForumsDetailsPageSize(int forumsDetailsPageSize)
   {
      this.forumsDetailsPageSize = forumsDetailsPageSize;
   }

   /**
    * @return Returns the forumsIconsPageSize.
    */
   public int getForumsIconsPageSize()
   {
      return this.forumsIconsPageSize;
   }

   /**
    * @param forumsIconsPageSize The forumsIconsPageSize to set.
    */
   /*package*/ void setForumsIconsPageSize(int forumsIconsPageSize)
   {
      this.forumsIconsPageSize = forumsIconsPageSize;
   }

   /**
    * @return Returns the forumsListPageSize.
    */
   public int getForumsListPageSize()
   {
      return this.forumsListPageSize;
   }

   /**
    * @param forumsListPageSize The forumsListPageSize to set.
    */
   /*package*/ void setForumsListPageSize(int forumsListPageSize)
   {
      this.forumsListPageSize = forumsListPageSize;
   }

   /**
    * @return Returns the defaultForumView.
    */
   public String getDefaultForumView()
   {
      return this.defaultForumView;
   }

   /**
    * @param defaultForumView The defaultForumView to set.
    */
   /*package*/ void setDefaultForumView(String defaultForumView)
   {
      this.defaultForumView = defaultForumView;
   }

   /**
    * @return Returns the forumDetailsPageSize.
    */
   public int getForumDetailsPageSize()
   {
      return this.forumDetailsPageSize;
   }

   /**
    * @param forumDetailsPageSize The forumDetailsPageSize to set.
    */
   /*package*/ void setForumDetailsPageSize(int forumDetailsPageSize)
   {
      this.forumDetailsPageSize = forumDetailsPageSize;
   }

   /**
    * @return Returns the forumBubblePageSize.
    */
   public int getForumBubblePageSize()
   {
      return this.forumBubblePageSize;
   }

   /**
    * @param forumBubblePageSize The forumBubblePageSize to set.
    */
   /*package*/ void setForumBubblePageSize(int forumBubblePageSize)
   {
      this.forumBubblePageSize = forumBubblePageSize;
   }

   /**
    * @return Returns the defaultTopicView.
    */
   public String getDefaultTopicView()
   {
      return this.defaultTopicView;
   }

   /**
    * @param defaultTopicView The defaultTopicView to set.
    */
   /*package*/ void setDefaultTopicView(String defaultTopicView)
   {
      this.defaultTopicView = defaultTopicView;
   }

   /**
    * @return Returns the topicDetailsPageSize.
    */
   public int getTopicDetailsPageSize()
   {
      return this.topicDetailsPageSize;
   }

   /**
    * @param topicDetailsPageSize The topicDetailsPageSize to set.
    */
   /*package*/ void setTopicDetailsPageSize(int topicDetailsPageSize)
   {
      this.topicDetailsPageSize = topicDetailsPageSize;
   }

   /**
    * @return Returns the topicBubblePageSize.
    */
   public int getTopicBubblePageSize()
   {
      return this.topicBubblePageSize;
   }

   /**
    * @param topicBubblePageSize The topicBubblePageSize to set.
    */
   /*package*/ void setTopicBubblePageSize(int topicBubblePageSize)
   {
      this.topicBubblePageSize = topicBubblePageSize;
   }
   
   /**
    * Returns the default sort direction for the topic view
    * 
    * @return descending or ascending
    */
   public String getDefaultTopicSortDir()
   {
      return this.defaultTopicSortDir;
   }

   /**
    * Sets the default sort direction for the topic view 
    * 
    * @param defaultTopicSortDir either descending or ascending
    */
   /*package*/ void setDefaultTopicSortDir(String defaultTopicSortDir)
   {
      this.defaultTopicSortDir = defaultTopicSortDir;
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

   /**
    * @return Returns the default Home Space permissions.
    */
   public String getHomeSpacePermission()
   {
      return this.homeSpacePermission;
   }

   /**
    * @param homeSpacePermission The default Home Space permission to set.
    */
   /*package*/ void setHomeSpacePermission(String homeSpacePermission)
   {
      this.homeSpacePermission = homeSpacePermission;
   }

   /**
    * @return Returns the contentTypes.
    */
   public List<String> getContentTypes()
   {
      return this.contentTypes;
   }

   /**
    * @param contentTypes The contentTypes to set.
    */
   /*package*/ void setContentTypes(List<String> contentTypes)
   {
      this.contentTypes = contentTypes;
   }
   
   /**
    * @return Returns the customProps.
    */
   public List<CustomProperty> getCustomProperties()
   {
      return this.customProps;
   }

   /**
    * @param customProps The customProps to set.
    */
   /*package*/ void setCustomProperties(List<CustomProperty> customProps)
   {
      this.customProps = customProps;
   }
   
   
   public static class CustomProperty
   {
      CustomProperty(String type, String aspect, String property)
      {
         Type = type;
         Aspect = aspect;
         Property = property;
      }
      
      public String Type;
      public String Aspect;
      public String Property;
   }
}
