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
 * Document and Folder path component.
 * 
 * @namespace Alfresco
 * @class Alfresco.component.Path
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
   var $html = Alfresco.util.encodeHTML,
      $combine = Alfresco.util.combinePaths;
   
   /**
    * Path constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.component.Path} The new Path instance
    * @constructor
    */
   Alfresco.component.Path = function(htmlId)
   {
      Alfresco.component.Path.superclass.constructor.call(this, "Alfresco.component.Path", htmlId);
   
      /* Decoupled event listeners */
      YAHOO.Bubbling.on("documentDetailsAvailable", this.onDocumentDetailsAvailable, this);
      YAHOO.Bubbling.on("folderDetailsAvailable", this.onFolderDetailsAvailable, this);
      YAHOO.Bubbling.on("nodeDetailsAvailable", this.onNodeDetailsAvailable, this);
      
      return this;
   };
   
   YAHOO.extend(Alfresco.component.Path, Alfresco.component.Base,
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
          * Root page to create links to.
          * 
          * @property rootPage
          * @type string
          * @default "documentlibrary"
          */
         rootPage: "documentlibrary",

         /**
          * Root label ID. The I18N property of the document library root container.
          * 
          * @property rootLabelId
          * @type string
          * @default "path.documents"
          */
         rootLabelId: "path.documents",

         /**
          * Flag indicating whether to show folder icon type.
          *
          * @property showIconType
          * @type boolean
          * @default: true
          */
         showIconType: true
      },
      
      /**
       * Event handler called when the "documentDetailsAvailable" event is received
       *
       * @method: onDocumentDetailsAvailable
       */
      onDocumentDetailsAvailable: function Path_onDocumentDetailsAvailable(layer, args)
      {
         var docData = args[1].documentDetails,
            pathHtml = "",
            rootLinkUrl = this.options.rootPage,
            baseLinkUrl = rootLinkUrl + "?{file}path=",
            pathUrl = "/",
            folders = [];

         var path = docData.location.path;

         // Document Library root node
         if (path.length < 2)
         {
            pathHtml += '<span class="path-link"><a href="' + YAHOO.lang.substitute(baseLinkUrl,
            {
               file: "file=" + encodeURIComponent(docData.fileName) + "&"
            });
            pathHtml += '">' + this.msg(this.options.rootLabelId) + '</a></span>';
         }
         else
         {
            pathHtml += '<span class="path-link"><a href="' + encodeURI(rootLinkUrl) + '">' + this.msg(this.options.rootLabelId) + '</a></span>';
            folders = path.substring(1, path.length).split("/");

            if (folders.length > 0)
            {
               pathHtml += '<span class="separator"> &gt; </span>';
            }
            
            for (var x = 0, y = folders.length; x < y; x++)
            {
               pathUrl += window.escape(folders[x]);
               
               pathHtml += '<span class="path-link folder"><a href="' + YAHOO.lang.substitute(baseLinkUrl,
               {
                  file: (y - x > 1) ? "" : "file=" + encodeURIComponent(docData.fileName) + "&"
               });
               pathHtml += encodeURIComponent(pathUrl) + '">' + $html(folders[x]) + '</a></span>';
               
               if (y - x > 1)
               {
                  pathHtml += '<span class="separator"> &gt; </span>';
                  pathUrl += "/";
               }
            }
         }
         
         Dom.setStyle(this.id + "-defaultPath", "display", "none");
         Dom.get(this.id + "-path").innerHTML = pathHtml;

         Dom.addClass(this.id + "-status", "hidden");
         
         if (docData.custom && (docData.custom.isWorkingCopy || docData.custom.hasWorkingCopy))
         {
            var bannerMsg, bannerStatus;
            
            // Locked / Working Copy handling
            if (docData.lockedByUser === Alfresco.constants.USERNAME)
            {
               bannerStatus = docData.actionSet === "lockOwner" ? "lock-owner" : "editing";
               bannerMsg = this.msg("banner." + bannerStatus);
            }
            else if (docData.lockedByUser && docData.lockedByUser !== "")
            {
               bannerStatus = "locked";
               bannerMsg = this.msg("banner.locked", '<a href="' + Alfresco.util.uriTemplate("userpage",
               {
                  userid: docData.lockedByUser,
                  pageid: "profile"
               }) + '" class="theme-color-1">' + $html(docData.lockedBy) + '</a>') + '</div>';
            }
            
            if (bannerMsg)
            {
               Dom.get(this.id + "-status").innerHTML = '<span class="' + $html(bannerStatus) + '">' + bannerMsg + '</span>';
               Dom.removeClass(this.id + "-status", "hidden");
            }
            
            YAHOO.Bubbling.fire("recalculatePreviewLayout");
         }
      },

      /**
       * Event handler called when the "folderDetailsAvailable" event is received
       *
       * @method: onFolderDetailsAvailable
       */
      onFolderDetailsAvailable: function Path_onFolderDetailsAvailable(layer, args)
      {
         var folderData = args[1].folderDetails,
            pathHtml = "",
            rootLinkUrl = this.options.rootPage,
            baseLinkUrl = rootLinkUrl + "?{file}path=",
            pathUrl = "/",
            folders = [];
         
         var path = folderData.location.path;
         
         // Document Library root node
         if (path == "/")
         {
            pathHtml += '<span class="path-link"><a href="' + YAHOO.lang.substitute(baseLinkUrl,
            {
               file: "file=" + encodeURIComponent(folderData.fileName) + "&"
            });
            pathHtml += '">' + this.msg(this.options.rootLabelId) + '</a></span>';
         }
         else
         {
            pathHtml += '<span class="path-link"><a href="' + encodeURI(rootLinkUrl) + '">' + this.msg(this.options.rootLabelId) + '</a></span>';
         }

         path = $combine(path, folderData.fileName);
         folders = path.substring(1, path.length).split("/");
         pathHtml += '<span class="separator"> &gt; </span>';
         
         for (var x = 0, y = folders.length; x < y; x++)
         {
            pathUrl += window.escape(folders[x]);
            
            pathHtml += '<span class="path-link ' + (y - x == 1 ? "self" : "folder") + '"><a href="' + YAHOO.lang.substitute(baseLinkUrl,
            {
               file: (y - x == 2) ? "file=" + encodeURIComponent(folderData.fileName) + "&" : ""
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

         if (this.options.showIconType && Dom.get(this.id + "-iconType"))
         {
            Dom.get(this.id + "-iconType").innerHTML = YAHOO.lang.substitute('<img src="{iconContext}{icon}-48.png" width="48" height="48" /><span class="type">{type}</span>',
            {
               iconContext: Alfresco.constants.URL_CONTEXT + "components/documentlibrary/images/",
               icon: folderData.type,
               type: this.msg("type." + folderData.type)
            });
         }
      },

      /**
       * Event handler called when the "nodeDetailsAvailable" event is received
       *
       * @method: onNodeDetailsAvailable
       */
      onNodeDetailsAvailable: function Path_onNodeDetailsAvailable(layer, args)
      {
         var nodeData = args[1].nodeDetails,
            newArgs = args;
         
         if (nodeData.isFolder)
         {
            newArgs[1].folderDetails = nodeData;
            this.onFolderDetailsAvailable(layer, newArgs);
         }
         else
         {
            newArgs[1].documentDetails = nodeData;
            this.onDocumentDetailsAvailable(layer, newArgs);
         }
      }
   });
})();