/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
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
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.web.site.servlet;

import java.util.StringTokenizer;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.extensions.surf.util.I18NUtil;

/**
 * @author kevinr
 * @author muzquiano
 */
public abstract class BaseServlet extends HttpServlet
{
    /**
     * Apply the headers required to disallow caching of the response in the browser
     */
    public static void setNoCacheHeaders(HttpServletResponse response)
    {
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
    }
    
   /**
    * Apply Client and Repository language locale based on the 'Accept-Language' request header
    */
   public static void setLanguageFromRequestHeader(HttpServletRequest req)
   {
      // set language locale from browser header
      String acceptLang = req.getHeader("Accept-Language");
      if (acceptLang != null && acceptLang.length() != 0)
      {
         StringTokenizer t = new StringTokenizer(acceptLang, ",; ");
         // get language and convert to java locale format
         String language = t.nextToken().replace('-', '_');
         I18NUtil.setLocale(I18NUtil.parseLocale(language));
      }
   }
}