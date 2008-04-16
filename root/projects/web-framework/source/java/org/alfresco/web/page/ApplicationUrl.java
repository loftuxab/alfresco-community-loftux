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

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.web.scripts.Store;

/**
 * Application URL - matching and Page instance resolving.
 * 
 * @author Kevin Roast
 */
public class ApplicationUrl
{
   private Store store;
   private String pageId = null;

   public ApplicationUrl(Store store)
   {
      this.store = store;
   }

   public boolean match(String resource)
   {
      // TODO: match against the supplied resource url - is it one of our URLs?
      
      // TODO: resolve the page id to lookup from the resource url - attach behaviour to do this?
      //       for now we always "match" and there is a direct mapping between resource and page Id
      this.pageId = resource;
      
      return true;
   }

   /**
    * Resolve appropriate page definition based on page id.
    * 
    * @param pageId     The unique Id of the page instance to retrieve
    * 
    * @return PageInstance structure
    * 
    * @exception AlfrescoRuntimeException if unable to locate Page for specified page-id.
    */
   public PageInstance getPageInstance()
   {
      // TODO: pre-load all documents from a store? i.e. store.getAllDocuments()?
      //       the store should handle caching and remote refresh etc. as required...
      String pageDef = pageId + ".xml";
      
      if (this.store.hasDocument(pageDef))
      {
         return new PageInstance(store, pageDef);
      }
      else
      {
         throw new AlfrescoRuntimeException("Unable to locate page instance for resource: " + pageDef);
      }
   }

   @Override
   public String toString()
   {
      return pageId;
   }
}
