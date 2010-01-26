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
 * Folder info component.
 * 
 * @namespace Alfresco
 * @class Alfresco.FolderInfo
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
    * FolderInfo constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.FolderInfo} The new FolderInfo instance
    * @constructor
    */
   Alfresco.FolderInfo = function(htmlId)
   {
      Alfresco.RepositoryFolderInfo.superclass.constructor.call(this, "Alfresco.FolderInfo", htmlId);
      
      /* Decoupled event listeners */
      YAHOO.Bubbling.on("folderDetailsAvailable", this.onFolderDetailsAvailable, this);
      
      return this;
   };
   
   YAHOO.extend(Alfresco.FolderInfo.prototype, Alfresco.component.Base,
   {
      /**
       * Event handler called when the "folderDetailsAvailable" event is received
       */
      onFolderDetailsAvailable: function FolderInfo_onFolderDetailsAvailable(layer, args)
      {
         var folderData = args[1].folderDetails,
            i, ii;
         
         // render tags values
         var tags = folderData.tags,
            tagsHtml = "";
         
         if (tags.length === 0)
         {
            tagsHtml = Alfresco.util.message("label.none", this.name);
         }
         else
         {
            for (i = 0, ii = tags.length; i < ii; i++)
            {
               tagsHtml += '<div class="tag"><img src="' + Alfresco.constants.URL_CONTEXT + 'components/images/tag-16.png" />';
               tagsHtml += $html(tags[i]) + '</div>';
            }
         }
         
         Dom.get(this.id + "-tags").innerHTML = tagsHtml;
         
         // render permissions values
         var noPerms = Alfresco.util.message("folder-info.role.None", this.name),
            managerPerms = noPerms,
            collaboratorPerms = noPerms,
            consumerPerms = noPerms,
            everyonePerms = noPerms;
         
         var rawPerms = folderData.permissions.roles;
         for (i = 0, ii = rawPerms.length; i < ii; i++)
         {
            var permParts = rawPerms[i].split(";");
            var group = permParts[1];
            if (group.indexOf("_SiteManager") != -1)
            {
               managerPerms = Alfresco.util.message("folder-info.role." + permParts[2], this.name);
            }
            else if (group.indexOf("_SiteCollaborator") != -1)
            {
               collaboratorPerms = Alfresco.util.message("folder-info.role." + permParts[2], this.name);
            }
            else if (group.indexOf("_SiteConsumer") != -1)
            {
               consumerPerms = Alfresco.util.message("folder-info.role." + permParts[2], this.name);
            }
            else if (group === "GROUP_EVERYONE")
            {
               everyonePerms = Alfresco.util.message("folder-info.role." + permParts[2], this.name);
            }
         }
         
         Dom.get(this.id + "-perms-managers").innerHTML = $html(managerPerms);
         Dom.get(this.id + "-perms-collaborators").innerHTML = $html(collaboratorPerms);
         Dom.get(this.id + "-perms-consumers").innerHTML = $html(consumerPerms);
         Dom.get(this.id + "-perms-everyone").innerHTML = $html(everyonePerms);
      }
   });
})();
