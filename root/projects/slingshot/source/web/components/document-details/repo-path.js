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
 * Repository Document path component.
 * 
 * @namespace Alfresco
 * @class Alfresco.RepositoryDocumentPath
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
    * RepositoryDocumentPath constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.RepositoryDocumentPath} The new RepositoryDocumentPath instance
    * @constructor
    */
   Alfresco.RepositoryDocumentPath = function(htmlId)
   {
      return Alfresco.RepositoryDocumentPath.superclass.constructor.call(this, htmlId);
   };
   
   YAHOO.extend(Alfresco.RepositoryDocumentPath, Alfresco.DocumentPath,
   {
      /**
       * Event handler called when the "documentDetailsAvailable" event is received
       *
       * @override
       * @method: onDocumentDetailsAvailable
       */
      onDocumentDetailsAvailable: function RepositoryDocumentPath_onDocumentDetailsAvailable(layer, args)
      {
         var docData = args[1].documentDetails,
            pathHtml = "",
            rootLinkUrl = $combine(Alfresco.constants.URL_PAGECONTEXT, "repository"),
            baseLinkUrl = rootLinkUrl + "?{file}path=",
            pathUrl = "/";
         
         var path = docData.location.path;

         // Document Library root node
         if (path.length < 2)
         {
            pathHtml += '<span class="path-link"><a href="' + YAHOO.lang.substitute(baseLinkUrl,
            {
               file: "file=" + encodeURIComponent(docData.fileName) + "&"
            });
            pathHtml += '">' + this.msg("path.repository") + '</a></span>';
         }
         else
         {
            pathHtml += '<span class="path-link"><a href="' + encodeURI(rootLinkUrl) + '">' + this.msg("path.repository") + '</a></span>';
            
            var folders = path.substring(1, path.length).split("/");
            
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
         
         if (docData.custom.isWorkingCopy || docData.custom.hasWorkingCopy)
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
      }
   });
})();