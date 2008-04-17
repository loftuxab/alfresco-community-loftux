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


/**
 * A basic URL mapper that will map any URL directly to a page resource XML file with
 * the exact same path as given by the URL plus the default ".xml" file extension.
 * 
 * @author Kevin Roast
 */
public class DefaultPageUrlMapper implements UrlMapper
{
   /**
    * @see org.alfresco.web.page.UrlMapper#buildResourcePath(java.lang.String)
    */
   public String buildResourcePath(String url)
   {
      if (url == null || url.length() == 0)
      {
         throw new IllegalArgumentException("Url supplied to Url Mapper is mandatory.");
      }
      
      // clean up before generating the path
      url = trimUrl(url);
      
      return url + ".xml";
   }

   /**
    * @see org.alfresco.web.page.UrlMapper#match(java.lang.String)
    */
   public boolean match(String url)
   {
      // we map all urls directly to file paths - so can match anything
      return true;
   }
   
   /**
    * Trim trailing slash from specified url resource
    */
   public static String trimUrl(String url)
   {
      // strip trailing slashes
      if (url.charAt(url.length() - 1) == '/')
      {
         url = url.substring(0, url.length() - 2);
      }
      
      return url;
   }
}
