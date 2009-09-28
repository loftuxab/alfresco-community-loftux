/**
 * Copyright (C) 2005-2009 Alfresco Software Limited.
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
 * Folder path component.
 * 
 * @namespace Alfresco
 * @class Alfresco.FolderPath
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom;
   
   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML;
   
   /**
    * FolderPath constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.FolderPath} The new FolderPath instance
    * @constructor
    */
   Alfresco.FolderPath = function(htmlId)
   {
      Alfresco.FolderPath.superclass.constructor.call(this, "Alfresco.FolderPath", htmlId);
   
      /* Decoupled event listeners */
      YAHOO.Bubbling.on("folderDetailsAvailable", this.onFolderDetailsAvailable, this);
      
      return this;
   };
   
   YAHOO.extend(Alfresco.FolderPath, Alfresco.component.Base,
   {
      /**
       * Object container for initialization options
       *
       * @property options
       * @type object
       */
      options:
      {
         /**
          * Current siteId.
          * 
          * @property siteId
          * @type string
          */
         siteId: ""
      },
      
      /**
       * Event handler called when the "folderDetailsAvailable" event is received
       */
      onFolderDetailsAvailable: function FolderPath_onFolderDetailsAvailable(layer, args)
      {
         var folderData = args[1].folderDetails,
            pathHtml = "",
            rootLinkUrl = Alfresco.constants.URL_PAGECONTEXT + "site/" + this.options.siteId + "/documentlibrary",
            baseLinkUrl = rootLinkUrl + "{file}#path=",
            pathUrl = "/",
            folders = [];
         
         var path = folderData.location.path;
         // Document Library root node
         if (path == "/" + folderData.location.file)
         {
            // Root node link to contain current folder highlight parameter
            pathHtml += '<span class="path-link"><a href="' + YAHOO.lang.substitute(baseLinkUrl,
            {
               file: "?file=" + encodeURIComponent(folderData.fileName)
            });
            pathHtml += '">' + this.msg("path.documents") + '</a></span>';
         }
         else
         {
            pathHtml += '<span class="path-link"><a href="' + rootLinkUrl + '">' + this.msg("path.documents") + '</a></span>';
         }

         folders = path.substring(1, path.length).split("/");
         pathHtml += '<span class="separator"> &gt; </span>';
         
         for (var x = 0, y = folders.length; x < y; x++)
         {
            pathUrl += window.escape(folders[x]);
            
            pathHtml += '<span class="path-link ' + (y - x == 1 ? "self" : "folder") + '"><a href="' + YAHOO.lang.substitute(baseLinkUrl,
            {
               file: (y - x == 2) ? "?file=" + encodeURIComponent(folderData.fileName) : ""
            });
            pathHtml += pathUrl + '">' + $html(folders[x]) + '</a></span>';
            
            if (y - x > 1)
            {
               pathHtml += '<span class="separator"> &gt; </span>';
               pathUrl += "/";
            }
         }

         
         Dom.setStyle(this.id + "-defaultPath", "display", "none");
         Dom.get(this.id + "-path").innerHTML = pathHtml;
         
         var iconTypeHtml = YAHOO.lang.substitute('<img src="{iconContext}{icon}-48.png" width="48" height="48" /><span class="type">{type}</span>',
         {
            iconContext: Alfresco.constants.URL_CONTEXT + "components/documentlibrary/images/",
            icon: folderData.type,
            type: this.msg("type." + folderData.type)
         })
         Dom.get(this.id + "-iconType").innerHTML = iconTypeHtml;
      }
   });
})();