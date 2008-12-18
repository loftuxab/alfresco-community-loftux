/**
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
 * http://www.alfresco.com/legal/licensing
 */
 
/**
 * BlogViewPosts template.
 * 
 * @namespace Alfresco
 * @class Alfresco.BlogViewPosts
 */
(function()
{
   /**
    * BlogViewPosts constructor.
    * 
    * @return {Alfresco.BlogViewPosts} The new BlogViewPosts instance
    * @constructor
    */
   Alfresco.BlogViewPosts = function BlogViewPosts_constructor()
   {
      Alfresco.BlogViewPosts.superclass.constructor.call(this);
      return this;
   };
   
   YAHOO.extend(Alfresco.BlogViewPosts, Alfresco.widget.Resizer,
   {
      /**
       * DOM ID of left-hand container DIV
       *
       * @property divLeft
       * @type string
       * @default "divLeft"
       */
      divLeft: "divPostListFilters",

      /**
       * DOM ID of right-hand container DIV
       *
       * @property divRight
       * @type string
       * @default "divRight"
       */
      divRight: "divPostListPosts"
   });
   
})();

// Instantiate the Document Library template
new Alfresco.BlogViewPosts();