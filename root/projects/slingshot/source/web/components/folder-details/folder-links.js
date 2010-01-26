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
 * Folder links component.
 * 
 * @namespace Alfresco
 * @class Alfresco.FolderLinks
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event;

   /**
    * Alfresco Slingshot aliases
    */
   var $combine = Alfresco.util.combinePaths;
   
   /**
    * FolderLinks constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.FolderLinks} The new FolderLinks instance
    * @constructor
    */
   Alfresco.FolderLinks = function(htmlId)
   {
      Alfresco.FolderLinks.superclass.constructor.call(this, htmlId);

      // Re-register with our own name
      this.name = "Alfresco.FolderLinks";
      Alfresco.util.ComponentManager.reregister(this);

      /* Decoupled event listeners */
      YAHOO.Bubbling.on("folderDetailsAvailable", this.onFolderDetailsAvailable, this);
      
      return this;
   };
   
   YAHOO.extend(Alfresco.FolderLinks, Alfresco.DocumentLinks,
   {
      /**
       * Event handler called when the "folderDetailsAvailable" event is received
       */
      onFolderDetailsAvailable: function FolderLinks_onFolderDetailsAvailable(layer, args)
      {
         var folderData = args[1].folderDetails;
         
         if (this.options.repositoryUrl)
         {
            // WebDAV URL
            this.populateLinkUI(
            {
               name: "webdav",
               url: $combine(this.options.repositoryUrl, folderData.webdavUrl)
            });
         }

         // This page
         this.populateLinkUI(
         {
            name: "page",
            url: window.location.href
         });
      }
   });
})();
