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
import java.util.Iterator;
import java.util.List;

import org.alfresco.config.ConfigElement;
import org.alfresco.config.ConfigException;
import org.alfresco.config.xml.elementreader.ConfigElementReader;
import org.alfresco.web.config.ClientConfigElement.CustomProperty;
import org.dom4j.Element;

/**
 * Custom element reader to parse config for client config values
 * 
 * @author Kevin Roast
 */
public class ClientElementReader implements ConfigElementReader
{
   public static final String ELEMENT_CLIENT = "client";
   public static final String ELEMENT_PAGESIZE = "page-size";
   public static final String ELEMENT_BROWSE = "browse";
   public static final String ELEMENT_FORUMS = "forums";
   public static final String ELEMENT_FORUM = "forum";
   public static final String ELEMENT_TOPIC = "topic";
   public static final String ELEMENT_LIST = "list";
   public static final String ELEMENT_DETAILS = "details";
   public static final String ELEMENT_ICONS = "icons";
   public static final String ELEMENT_BUBBLE = "bubble";
   public static final String ELEMENT_LINEAR = "linear";
   public static final String ELEMENT_DEFAULTVIEW = "default-view";
   public static final String ELEMENT_DEFAULTFORUMSVIEW = "default-forums-view";
   public static final String ELEMENT_DEFAULTFORUMVIEW = "default-forum-view";
   public static final String ELEMENT_DEFAULTTOPICVIEW = "default-topic-view";
   public static final String ELEMENT_DEFAULTTOPICSORTDIR = "default-topic-sort-direction";
   public static final String ELEMENT_RECENTSPACESITEMS = "recent-spaces-items";
   public static final String ELEMENT_LANGUAGES = "languages";
   public static final String ELEMENT_LANGUAGE = "language";
   public static final String ATTRIBUTE_LOCALE = "locale";
   public static final String ATTRIBUTE_NAME = "name";
   public static final String ELEMENT_HELPURL = "help-url";
   public static final String ELEMENT_SEARCHMINIMUM = "search-minimum";
   public static final String ELEMENT_HOMESPACEPERMISSION = "home-space-permission";
   public static final String ELEMENT_ADVANCEDSEARCH = "advanced-search";
   public static final String ELEMENT_CONTENTTYPES = "content-types";
   public static final String ELEMENT_TYPE = "type";
   public static final String ELEMENT_CUSTOMPROPS = "custom-properties";
   public static final String ELEMENT_METADATA = "meta-data";
   public static final String ATTRIBUTE_TYPE = "type";
   public static final String ATTRIBUTE_PROPERTY = "property";
   public static final String ATTRIBUTE_ASPECT = "aspect";
   
   /**
    * @see org.alfresco.config.xml.elementreader.ConfigElementReader#parse(org.dom4j.Element)
    */
   public ConfigElement parse(Element element)
   {
      ClientConfigElement configElement = null;
      
      if (element != null)
      {
         String name = element.getName();
         if (name.equals(ELEMENT_CLIENT) == false)
         {
            throw new ConfigException("ClientElementReader can only parse " +
                  ELEMENT_CLIENT + "elements, the element passed was '" + name + "'");
         }
         
         configElement = new ClientConfigElement();
         
         // get the page size sub-element
         Element pageSize = element.element(ELEMENT_PAGESIZE);
         if (pageSize != null)
         {
            // get the config for the browse view
            Element browseView = pageSize.element(ELEMENT_BROWSE);
            if (browseView != null)
            {
               Element viewPageSize = pageSize.element(ELEMENT_LIST);
               if (viewPageSize != null)
               {
                  configElement.setListPageSize(Integer.parseInt(viewPageSize.getTextTrim()));
               }
               Element detailsPageSize = pageSize.element(ELEMENT_DETAILS);
               if (detailsPageSize != null)
               {
                  configElement.setDetailsPageSize(Integer.parseInt(detailsPageSize.getTextTrim()));
               }
               Element iconsPageSize = pageSize.element(ELEMENT_ICONS);
               if (iconsPageSize != null)
               {
                  configElement.setIconsPageSize(Integer.parseInt(iconsPageSize.getTextTrim()));
               }
            }
            
            // get the config for the forums view
            Element forumsView = pageSize.element(ELEMENT_FORUMS);
            if (forumsView != null)
            {
               Element viewPageSize = pageSize.element(ELEMENT_LIST);
               if (viewPageSize != null)
               {
                  configElement.setForumsListPageSize(Integer.parseInt(viewPageSize.getTextTrim()));
               }
               Element detailsPageSize = pageSize.element(ELEMENT_DETAILS);
               if (detailsPageSize != null)
               {
                  configElement.setForumsDetailsPageSize(Integer.parseInt(detailsPageSize.getTextTrim()));
               }
               Element iconsPageSize = pageSize.element(ELEMENT_ICONS);
               if (iconsPageSize != null)
               {
                  configElement.setForumsIconsPageSize(Integer.parseInt(iconsPageSize.getTextTrim()));
               }
            }
            
            // get the config for the forum view
            Element forumView = pageSize.element(ELEMENT_FORUM);
            if (forumView != null)
            {
               Element detailsPageSize = pageSize.element(ELEMENT_DETAILS);
               if (detailsPageSize != null)
               {
                  configElement.setForumDetailsPageSize(Integer.parseInt(detailsPageSize.getTextTrim()));
               }
               Element bubblePageSize = pageSize.element(ELEMENT_BUBBLE);
               if (bubblePageSize != null)
               {
                  configElement.setForumBubblePageSize(Integer.parseInt(bubblePageSize.getTextTrim()));
               }
            }
            
            // get the config for the topic view
            Element topicView = pageSize.element(ELEMENT_TOPIC);
            if (topicView != null)
            {
               Element detailsPageSize = pageSize.element(ELEMENT_DETAILS);
               if (detailsPageSize != null)
               {
                  configElement.setTopicDetailsPageSize(Integer.parseInt(detailsPageSize.getTextTrim()));
               }
               Element bubblePageSize = pageSize.element(ELEMENT_BUBBLE);
               if (bubblePageSize != null)
               {
                  configElement.setTopicBubblePageSize(Integer.parseInt(bubblePageSize.getTextTrim()));
               }
            }
         }
         
         // get the languages sub-element
         Element languages = element.element(ELEMENT_LANGUAGES);
         if (languages != null)
         {
            Iterator<Element> langsItr = languages.elementIterator(ELEMENT_LANGUAGE);
            while (langsItr.hasNext())
            {
               Element language = langsItr.next();
               String localeCode = language.attributeValue(ATTRIBUTE_LOCALE);
               String label = language.getTextTrim();
               
               if (localeCode != null && localeCode.length() != 0 &&
                   label != null && label.length() != 0)
               {
                  // store the language code against the display label
                  configElement.addLanguage(localeCode, label);
               }
            }
         }
         
         // get the default view mode
         Element defaultView = element.element(ELEMENT_DEFAULTVIEW);
         if (defaultView != null)
         {
            configElement.setDefaultView(defaultView.getTextTrim());
         }
         
         // get the default forums view mode
         Element defaultForumsView = element.element(ELEMENT_DEFAULTFORUMSVIEW);
         if (defaultForumsView != null)
         {
            configElement.setDefaultForumsView(defaultForumsView.getTextTrim());
         }
         
         // get the default forum view mode
         Element defaultForumView = element.element(ELEMENT_DEFAULTFORUMVIEW);
         if (defaultForumView != null)
         {
            configElement.setDefaultForumView(defaultForumView.getTextTrim());
         }
         
         // get the default topic view mode
         Element defaultTopicView = element.element(ELEMENT_DEFAULTTOPICVIEW);
         if (defaultTopicView != null)
         {
            configElement.setDefaultTopicView(defaultTopicView.getTextTrim());
         }
         
         // get the default topic sort direction
         Element defaultTopicSortDir = element.element(ELEMENT_DEFAULTTOPICSORTDIR);
         if (defaultTopicSortDir != null)
         {
            configElement.setDefaultTopicSortDir(defaultTopicSortDir.getTextTrim());
         }
         
         // get the recent space max items
         Element recentSpaces = element.element(ELEMENT_RECENTSPACESITEMS);
         if (recentSpaces != null)
         {
            configElement.setRecentSpacesItems(Integer.parseInt(recentSpaces.getTextTrim()));
         }
         
         // get the Help url
         Element helpUrl = element.element(ELEMENT_HELPURL);
         if (helpUrl != null)
         {
            configElement.setHelpUrl(helpUrl.getTextTrim());
         }
         
         // get the minimum number of characters for valid search string
         Element searchMin = element.element(ELEMENT_SEARCHMINIMUM);
         if (searchMin != null)
         {
            configElement.setSearchMinimum(Integer.parseInt(searchMin.getTextTrim()));
         }
         
         // get the default permission for newly created users Home Spaces
         Element permission = element.element(ELEMENT_HOMESPACEPERMISSION);
         if (permission != null)
         {
            configElement.setHomeSpacePermission(permission.getTextTrim());
         }
         
         // get the Advanced Search config block
         Element advsearch = element.element(ELEMENT_ADVANCEDSEARCH);
         if (advsearch != null)
         {
            // get the list of content types
            Element contentTypes = advsearch.element(ELEMENT_CONTENTTYPES);
            Iterator<Element> typesItr = contentTypes.elementIterator(ELEMENT_TYPE);
            List<String> types = new ArrayList<String>(5);
            while (typesItr.hasNext())
            {
               Element contentType = typesItr.next();
               String type = contentType.attributeValue(ATTRIBUTE_NAME);
               if (type != null)
               {
                  types.add(type);
               }
            }
            configElement.setContentTypes(types);
            
            // get the list of custom properties to display
            Element customProps = advsearch.element(ELEMENT_CUSTOMPROPS);
            Iterator<Element> propsItr = customProps.elementIterator(ELEMENT_METADATA);
            List<CustomProperty> props = new ArrayList<CustomProperty>(5);
            while (propsItr.hasNext())
            {
               Element propElement = propsItr.next();
               String type = propElement.attributeValue(ATTRIBUTE_TYPE);
               String aspect = propElement.attributeValue(ATTRIBUTE_ASPECT);
               String prop = propElement.attributeValue(ATTRIBUTE_PROPERTY);
               props.add(new ClientConfigElement.CustomProperty(type, aspect, prop));
            }
            configElement.setCustomProperties(props);
         }
      }
      
      return configElement;
   }
}
