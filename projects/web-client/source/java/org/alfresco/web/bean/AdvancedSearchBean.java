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

import javax.faces.event.ActionEvent;

import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.Path;
import org.alfresco.service.namespace.NamespaceService;
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
         
         // location path search
         if (this.lookin.equals(LOOKIN_OTHER) && this.location != null)
         {
            NodeRef ref = new NodeRef(Repository.getStoreRef(), this.location);
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
                     if (NamespaceService.ALFRESCO_URI.equals(elementRef.getQName().getNamespaceURI()))
                     {
                        elementString = '/' + NamespaceService.ALFRESCO_PREFIX + ':' + elementRef.getQName().getLocalName();
                     }
                  }
               }
               
               buf.append(elementString);
            }
            // append syntax to get all children of the path
            buf.append("//*");
            search.setLocation(buf.toString());
         }
         
         outcome = "browse";
      }
      
      return outcome;
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
}
