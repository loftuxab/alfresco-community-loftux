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

import java.io.Serializable;
import java.util.StringTokenizer;

import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.web.bean.repository.Repository;
import org.alfresco.web.ui.common.Utils;

/**
 * Holds the context required to build a search query and can return the populated query.
 * 
 * @author Kevin Roast
 */
public final class SearchContext implements Serializable
{
   /** Search mode constants */
   public final static int SEARCH_ALL = 0;
   public final static int SEARCH_FILE_NAMES_CONTENTS = 1;
   public final static int SEARCH_FILE_NAMES = 2;
   public final static int SEARCH_SPACE_NAMES = 3;
   
   /** the search text string */
   private String text = "";
   
   /** mode for the search */
   private int mode = SearchContext.SEARCH_ALL;
   
   /** folder node location for the search */
   private String location = null;
   
   /** categories to add to the search */
   private String[] categories = new String[0];

   
   /**
    * Build the search query string based on the current search context members.
    * 
    * @return prepared search query string
    */
   public String buildQuery()
   {
      // TODO: change this to a StringBuilder
      String query;
      
      // the QName for the well known "name" attribute
      String nameAttr = Repository.escapeQName(QName.createQName(NamespaceService.ALFRESCO_URI, "name"));
      
      // match against content text
      String text = this.text.trim();
      String safeText = Utils.remove(text, "\"");
      String fullTextQuery;
      String nameAttrQuery;
      if (text.equals("*") == true)
      {
         // special case to handle search against EVERYTHING
         fullTextQuery = " ISNODE:*";
         nameAttrQuery = " ISNODE:*";
      }
      else if (text.indexOf(' ') == -1)
      {
         // simple single word text search
         fullTextQuery = " TEXT:" + safeText + '*';
         nameAttrQuery = " +@" + nameAttr + ":" + safeText + '*';
      }
      else
      {
         // multiple word search
         if (text.charAt(0) == '"' && text.charAt(text.length() - 1) == '"')
         {
            // as quoted phrase
            fullTextQuery = " TEXT:\"" + safeText + '"';
            nameAttrQuery = " +@" + nameAttr + ":\"" + safeText + "\"";
         }
         else
         {
            // as individual search terms
            StringTokenizer t = new StringTokenizer(safeText, " ");
            StringBuilder fullTextBuf = new StringBuilder(64);
            StringBuilder nameAttrBuf = new StringBuilder(64);
            fullTextBuf.append('(');
            nameAttrBuf.append('(');
            while (t.hasMoreTokens())
            {
               String term = t.nextToken();
               
               fullTextBuf.append("TEXT:").append(term).append('*');
               nameAttrBuf.append("@").append(nameAttr).append(":").append(term).append('*');
               if (t.hasMoreTokens())
               {
                  fullTextBuf.append(" OR ");
                  nameAttrBuf.append(" OR ");
               }
            }
            fullTextBuf.append(')');
            nameAttrBuf.append(')');
            fullTextQuery = fullTextBuf.toString();
            nameAttrQuery = nameAttrBuf.toString();
         }
      }
      
      // match a specific PATH
      StringBuilder pathQuery = null;
      if (location != null || (categories != null && categories.length !=0))
      {
         pathQuery = new StringBuilder(128);
         if (location != null)
         {
            pathQuery.append(" +PATH:\"").append(location).append("\" ");
         }
         if (categories != null && categories.length != 0)
         {
            for (int i=0; i<categories.length; i++)
            {
               if (pathQuery.length() != 0)
               {
                  pathQuery.append("OR");
               }
               pathQuery.append(" +PATH:\"").append(categories[i]).append("\" "); 
            }
         }
      }
      
      // match against CONTENT type
      String fileTypeQuery = " +TYPE:\"{" + NamespaceService.ALFRESCO_URI + "}content\" ";
      
      // match against FOLDER type
      String folderTypeQuery = " +TYPE:\"{" + NamespaceService.ALFRESCO_URI + "}folder\" ";
      
      switch (mode)
      {
         case SearchContext.SEARCH_ALL:
            query = '(' + nameAttrQuery + ')' + " OR " + fullTextQuery;
            break;
         
         case SearchContext.SEARCH_FILE_NAMES:
            query = fileTypeQuery + " AND " + nameAttrQuery;
            break;
         
         case SearchContext.SEARCH_FILE_NAMES_CONTENTS:
            query = '(' + fileTypeQuery + " AND " + nameAttrQuery + ") OR " + fullTextQuery;
            break;
         
         case SearchContext.SEARCH_SPACE_NAMES:
            query = folderTypeQuery + " AND " + nameAttrQuery;
            break;
         
         default:
            throw new IllegalStateException("Unknown search mode specified: " + mode);
      }
      
      // match entire query against specified Space path
      if (pathQuery != null)
      {
         query = pathQuery + " AND (" + query + ')';
      }
      
      return query;
   }
   
   /**
    * @return Returns the categories to use for the search
    */
   public String[] getCategories()
   {
      return this.categories;
   }
   
   /**
    * @param categories    The categories to set.
    */
   public void setCategories(String[] categories)
   {
      if (categories != null)
      {
         this.categories = categories;
      }
   }
   
   /**
    * @return Returns the node to search from or null for all.
    */
   public String getLocation()
   {
      return this.location;
   }
   
   /**
    * @param location      The node to search from or null for all..
    */
   public void setLocation(String location)
   {
      this.location = location;
   }
   
   /**
    * @return Returns the mode to use during the search (see constants)
    */
   public int getMode()
   {
      return this.mode;
   }
   
   /**
    * @param mode The mode to use during the search (see constants)
    */
   public void setMode(int mode)
   {
      this.mode = mode;
   }
   
   /**
    * @return Returns the search text string.
    */
   public String getText()
   {
      return this.text;
   }
   
   /**
    * @param text       The search text string.
    */
   public void setText(String text)
   {
      this.text = text;
   }
}
