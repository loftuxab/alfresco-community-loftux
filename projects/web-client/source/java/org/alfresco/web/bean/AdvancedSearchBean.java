/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/lgpl.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.web.bean;

import java.util.Collection;
import java.util.Date;

import javax.faces.event.ActionEvent;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.Path;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.util.Conversion;
import org.alfresco.web.app.Application;
import org.alfresco.web.bean.repository.Repository;

/**
 * @author Kevin Roast
 */
public class AdvancedSearchBean
{
   // ------------------------------------------------------------------------------
   // Bean property getters and setters 
   
   /**
    * @return Returns the navigation bean instance.
    */
   public NavigationBean getNavigator()
   {
      return this.navigator;
   }
   
   /**
    * @param navigator The NavigationBean to set.
    */
   public void setNavigator(NavigationBean navigator)
   {
      this.navigator = navigator;
   }
   
   /**
    * @return Returns the NodeService.
    */
   public NodeService getNodeService()
   {
      return this.nodeService;
   }

   /**
    * @param nodeService The NodeService to set.
    */
   public void setNodeService(NodeService nodeService)
   {
      this.nodeService = nodeService;
   }

   /**
    * @param namespaceService The NamespaceService to set.
    */
   public void setNamespaceService(NamespaceService namespaceService)
   {
      this.namespaceService = namespaceService;
   }
   
   /**
    * @return Returns the folder to search, null for all.
    */
   public String getLookin()
   {
      return this.lookin;
   }
   
   /**
    * @param lookin   The folder to search in or null for all.
    */
   public void setLookin(String lookIn)
   {
      this.lookin = lookIn;
   }
   
   /**
    * @return Returns the location.
    */
   public String getLocation()
   {
      return this.location;
   }
   
   /**
    * @param location The location to set.
    */
   public void setLocation(String location)
   {
      this.location = location;
   }
   
   /**
    * @return Returns the search mode.
    */
   public String getMode()
   {
      return this.mode;
   }
   
   /**
    * @param mode The search mode to set.
    */
   public void setMode(String mode)
   {
      this.mode = mode;
   }
   
   /**
    * @return Returns the text to search for.
    */
   public String getText()
   {
      return this.text;
   }
   
   /**
    * @param text The text to set.
    */
   public void setText(String text)
   {
      this.text = text;
   }
   
   /**
    * @return Returns the category.
    */
   public String getCategory()
   {
      return this.category;
   }
   
   /**
    * @param category The category to set.
    */
   public void setCategory(String category)
   {
      this.category = category;
   }
   
   /**
    * @return Returns true to search location children, false for just the specified location.
    */
   public boolean getLocationChildren()
   {
      return this.locationChildren;
   }
   
   /**
    * @param locationChildren    True to search location children, false for just the specified location.
    */
   public void setLocationChildren(boolean locationChildren)
   {
      this.locationChildren = locationChildren;
   }
   
   /**
    * @return Returns true to search category children, false for just the specified category.
    */
   public boolean getCategoryChildren()
   {
      return this.categoryChildren;
   }
   
   /**
    * @param categoryChildren    True to search category children, false for just the specified category.
    */
   public void setCategoryChildren(boolean categoryChildren)
   {
      this.categoryChildren = categoryChildren;
   }
   
   /**
    * @return Returns the createdDateFrom.
    */
   public Date getCreatedDateFrom()
   {
      return this.createdDateFrom;
   }

   /**
    * @param createdDateFrom The createdDateFrom to set.
    */
   public void setCreatedDateFrom(Date createdDate)
   {
      this.createdDateFrom = createdDate;
   }

   /**
    * @return Returns the description.
    */
   public String getDescription()
   {
      return this.description;
   }

   /**
    * @param description The description to set.
    */
   public void setDescription(String description)
   {
      this.description = description;
   }

   /**
    * @return Returns the modifiedDateFrom.
    */
   public Date getModifiedDateFrom()
   {
      return this.modifiedDateFrom;
   }

   /**
    * @param modifiedDateFrom The modifiedDateFrom to set.
    */
   public void setModifiedDateFrom(Date modifiedDate)
   {
      this.modifiedDateFrom = modifiedDate;
   }
   
   /**
    * @return Returns the createdDateTo.
    */
   public Date getCreatedDateTo()
   {
      return this.createdDateTo;
   }

   /**
    * @param createdDateTo The createdDateTo to set.
    */
   public void setCreatedDateTo(Date createdDateTo)
   {
      this.createdDateTo = createdDateTo;
   }

   /**
    * @return Returns the modifiedDateTo.
    */
   public Date getModifiedDateTo()
   {
      return this.modifiedDateTo;
   }

   /**
    * @param modifiedDateTo The modifiedDateTo to set.
    */
   public void setModifiedDateTo(Date modifiedDateTo)
   {
      this.modifiedDateTo = modifiedDateTo;
   }

   /**
    * @return Returns the title.
    */
   public String getTitle()
   {
      return this.title;
   }

   /**
    * @param title The title to set.
    */
   public void setTitle(String title)
   {
      this.title = title;
   }
   
   /**
    * @return Returns the author.
    */
   public String getAuthor()
   {
      return this.author;
   }

   /**
    * @param author The author to set.
    */
   public void setAuthor(String author)
   {
      this.author = author;
   }
   
   /**
    * @return Returns the modifiedDateChecked.
    */
   public boolean isModifiedDateChecked()
   {
      return this.modifiedDateChecked;
   }

   /**
    * @param modifiedDateChecked The modifiedDateChecked to set.
    */
   public void setModifiedDateChecked(boolean modifiedDateChecked)
   {
      this.modifiedDateChecked = modifiedDateChecked;
   }

   /**
    * @return Returns the createdDateChecked.
    */
   public boolean isCreatedDateChecked()
   {
      return this.createdDateChecked;
   }

   /**
    * @param createdDateChecked The createdDateChecked to set.
    */
   public void setCreatedDateChecked(boolean createdDateChecked)
   {
      this.createdDateChecked = createdDateChecked;
   }
   
   
   // ------------------------------------------------------------------------------
   // Action event handlers
   
   /**
    * Handler to clear the advanced search screen form details
    */
   public void reset(ActionEvent event)
   {
      this.text = "";
      this.mode = MODE_ALL;
      this.lookin = LOOKIN_ALL;
      this.location = null;
      this.category = null;
      this.title = null;
      this.description = null;
      this.author = null;
      this.createdDateFrom = null;
      this.modifiedDateFrom = null;
      this.createdDateChecked = false;
      this.modifiedDateChecked = false;
   }
   
   /**
    * Handler to perform a search based on the current criteria
    */
   public String search()
   {
      String outcome = null;
      
      if (this.text != null && this.text.length() != 0)
      {
         // construct the Search Context and set on the navigation bean
         // then simply navigating to the browse screen will cause it pickup the Search Context
         SearchContext search = new SearchContext();
         
         search.setText(this.text);
         
         if (this.mode.equals(MODE_ALL))
         {
            search.setMode(SearchContext.SEARCH_ALL);
         }
         else if (this.mode.equals(MODE_FILES_TEXT))
         {
            search.setMode(SearchContext.SEARCH_FILE_NAMES_CONTENTS);
         }
         else if (this.mode.equals(MODE_FILES))
         {
            search.setMode(SearchContext.SEARCH_FILE_NAMES);
         }
         else if (this.mode.equals(MODE_FOLDERS))
         {
            search.setMode(SearchContext.SEARCH_SPACE_NAMES);
         }
         this.navigator.setSearchContext(search);
         
         // additional attributes search
         if (this.description != null && this.description.length() != 0)
         {
            search.addAdditionalAttribute(ContentModel.PROP_DESCRIPTION, this.description + '*');
         }
         if (this.title != null && this.title.length() != 0)
         {
            search.addAdditionalAttribute(ContentModel.PROP_TITLE, this.title + '*');
         }
         if (this.author != null && this.author.length() != 0)
         {
            search.addAdditionalAttribute(ContentModel.PROP_CREATOR, this.author + '*');
         }
         if (this.createdDateChecked == true)
         {
            String strCreatedDate = Conversion.dateToXmlDate(this.createdDateFrom).substring(0, 10);
            String strCreatedDateTo = Conversion.dateToXmlDate(this.createdDateTo).substring(0, 10);
            search.addAdditionalAttribute(ContentModel.PROP_CREATED, "[" + strCreatedDate + " TO " + strCreatedDateTo + "]");
         }
         if (this.modifiedDateChecked == true)
         {
            String strModifiedDate = Conversion.dateToXmlDate(this.modifiedDateFrom).substring(0, 10);
            String strModifiedDateTo = Conversion.dateToXmlDate(this.modifiedDateTo).substring(0, 10);
            search.addAdditionalAttribute(ContentModel.PROP_MODIFIED, "[" + strModifiedDate + " TO " + strModifiedDateTo + "]");
         }
         
         // location path search
         if (this.lookin.equals(LOOKIN_OTHER) && this.location != null)
         {
            search.setLocation(getPathFromSpaceId(this.location, this.locationChildren));
         }
         
         // category path search
         if (this.category != null)
         {
            search.setCategories(new String[]{getPathFromSpaceId(this.category, this.categoryChildren)});
         }
         
         outcome = "browse";
      }
      
      return outcome;
   }
   
   /**
    * Generate a search XPATH pointing to the specified node Id, optionally return an XPATH
    * that includes the child nodes.
    *  
    * @param id         Of the node to generate path too
    * @param children   Whether to include children of the node
    * 
    * @return the path
    */
   private String getPathFromSpaceId(String id, boolean children)
   {
      NodeRef ref = new NodeRef(Repository.getStoreRef(), id);
      Path path = this.nodeService.getPath(ref);
      StringBuilder buf = new StringBuilder(64);
      for (int i=0; i<path.size(); i++)
      {
         String elementString = "";
         Path.Element element = path.get(i);
         if (element instanceof Path.ChildAssocElement)
         {
            ChildAssociationRef elementRef = ((Path.ChildAssocElement)element).getRef();
            if (elementRef.getParentRef() != null)
            {
               Collection prefixes = this.namespaceService.getPrefixes(elementRef.getQName().getNamespaceURI());
               if (prefixes.size() >0)
               {
                  elementString = '/' + (String)prefixes.iterator().next() + ':' + elementRef.getQName().getLocalName();
               }
            }
         }
         
         buf.append(elementString);
      }
      if (children == true)
      {
         // append syntax to get all children of the path
         buf.append("//*");
      }
      else
      {
         // append syntax to just represent the path, not the children
         buf.append("/*");
      }
      
      return buf.toString();
   }
   
   
   // ------------------------------------------------------------------------------
   // Private data 
   
   private static final String MODE_ALL = "all";
   private static final String MODE_FILES_TEXT = "files_text";
   private static final String MODE_FILES = "files";
   private static final String MODE_FOLDERS = "folders";
   
   private static final String LOOKIN_ALL = "all";
   private static final String LOOKIN_OTHER = "other";
   
   /** The NodeService to be used by the bean */
   private NodeService nodeService;
   
   /** The NamespaceService to be used by the bean */
   private NamespaceService namespaceService;
   
   /** The NavigationBean reference */
   private NavigationBean navigator;
   
   /** the text to search for */
   private String text = "";
   
   /** search mode */
   private String mode = MODE_ALL;
   
   /** folder lookin to look in */
   private String lookin = LOOKIN_ALL;
   
   /** Space Selector location */
   private String location = null;
   
   /** categories to search */
   private String category = null;
   
   /** title attribute to search */
   private String title = null;
   
   /** description attribute to search */
   private String description = null;
   
   /** created attribute to search from */
   private Date createdDateFrom = null;
   
   /** created attribute to search to */
   private Date createdDateTo = null;
   
   /** modified attribute to search from */
   private Date modifiedDateFrom = null;
   
   /** modified attribute to search to */
   private Date modifiedDateTo = null;
   
   /** true to search location children as well as location */
   private boolean locationChildren = true;
   
   /** true to search category children as well as category */
   private boolean categoryChildren = true;
   
   /** author (creator) attribute to search */
   private String author = null;
   
   private boolean modifiedDateChecked = false;
   private boolean createdDateChecked = false;
}
