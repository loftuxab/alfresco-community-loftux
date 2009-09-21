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
 * Document actions component.
 * 
 * @namespace Alfresco
 * @class Alfresco.DocumentActions
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      Element = YAHOO.util.Element;
   
   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML,
      $combine = Alfresco.util.combinePaths;
   
   /**
    * DocumentActions constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.DocumentActions} The new DocumentActions instance
    * @constructor
    */
   Alfresco.DocumentActions = function(htmlId)
   {
      Alfresco.DocumentActions.superclass.constructor.call(this, "Alfresco.DocumentActions", htmlId, ["button"]);
      
      /* Decoupled event listeners */
      YAHOO.Bubbling.on("documentDetailsAvailable", this.onDocumentDetailsAvailable, this);
      
      return this;
   };

   /**
    * Extend Alfresco.component.Base
    */
   YAHOO.extend(Alfresco.DocumentActions, Alfresco.component.Base);
   
   /**
    * Augment prototype with Actions module
    */
   YAHOO.lang.augmentProto(Alfresco.DocumentActions, Alfresco.doclib.Actions);

   /**
    * Augment prototype with main class implementation, ensuring overwrite is enabled
    */
   YAHOO.lang.augmentObject(Alfresco.DocumentActions.prototype,
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
         siteId: "",
         
         /**
          * ContainerId representing root container
          *
          * @property containerId
          * @type string
          * @default "documentLibrary"
          */
         containerId: "documentLibrary"
      },
      
      /**
       * The data for the document
       * 
       * @property assetData
       * @type object
       */
      assetData: null,
      
      /**
       * Metadata returned by doclist data webscript
       *
       * @property doclistMetadata
       * @type object
       * @default null
       */
      doclistMetadata: null,
      
      /**
       * Path of asset being viewed - used to scope some actions (e.g. copy to, move to)
       * 
       * @property currentPath
       * @type string
       */
      currentPath: null,

      /**
       * The urls to be used when creating links in the action cell
       *
       * @method getActionUrls
       * @return {object} Object literal containing URLs to be substituted in action placeholders
       */
      getActionUrls: function DocumentActions_getActionUrls()
      {
         var urlContextSite = Alfresco.constants.URL_PAGECONTEXT + "site/" + this.options.siteId,
            nodeRef = this.assetData.nodeRef;

         return (
         {
            downloadUrl: Alfresco.constants.PROXY_URI + this.assetData.contentUrl + "?a=true",
            editMetadataUrl: urlContextSite + "/edit-metadata?nodeRef=" + nodeRef
         });
      },
       
      /**
       * Event handler called when the "documentDetailsAvailable" event is received
       *
       * @method: onDocumentDetailsAvailable
       */
      onDocumentDetailsAvailable: function DocumentActions_onDocumentDetailsAvailable(layer, args)
      {
         var me = this;
         
         // Asset data passed-in through event arguments
         this.assetData = args[1].documentDetails;
         this.doclistMetadata = args[1].metadata;
         this.currentPath = this.assetData.location.path;
         
         var actionsContainer = Dom.get(this.id + "-actionSet-document");
         
         /**
          * Token replacement
          */
         actionsContainer.innerHTML = YAHOO.lang.substitute(window.unescape(actionsContainer.innerHTML), this.getActionUrls());
         
         /**
          * Hide actions which have been disallowed through permissions
          */
         if (this.assetData.permissions && this.assetData.permissions.userAccess)
         {
            var userAccess = this.assetData.permissions.userAccess,
               actions = YAHOO.util.Selector.query("div", actionsContainer),
               action, actionPermissions, i, ii, j, jj, actionAllowed;
            
            for (i = 0, ii = actions.length; i < ii; i++)
            {
               action = actions[i];
               actionAllowed = true;
               if (action.firstChild.rel !== "")
               {
                  actionPermissions = action.firstChild.rel.split(",");
                  for (j = 0, jj = actionPermissions.length; j < jj; j++)
                  {
                     if (!userAccess[actionPermissions[j]])
                     {
                        actionAllowed = false;
                        break;
                     }
                  }
               }
               Dom.setStyle(action, "display", actionAllowed ? "block" : "none");
            }
         }
         Dom.setStyle(actionsContainer, "visibility", "visible");
         
         // Hook action events
         var fnActionHandler = function DocumentActions_fnActionHandler(layer, args)
         {
            var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "div");
            if (owner !== null)
            {
               var action = owner.className;
               if (typeof me[action] == "function")
               {
                  args[1].stop = true;
                  me[action].call(me, me.assetData, owner);
               }
            }
            return true;
         };
         
         YAHOO.Bubbling.addDefaultAction("action-link", fnActionHandler);
         
         // DocLib Actions module
         this.modules.actions = new Alfresco.module.DoclibActions();
      },
      
      /**
       * Delete Asset confirmed.
       *
       * @override
       * @method _onActionDeleteConfirm
       * @param asset {object} Object literal representing file or folder to be actioned
       * @private
       */
      _onActionDeleteConfirm: function DocumentActions__onActionDeleteConfirm(asset)
      {
         var path = asset.location.path,
            fileName = asset.fileName,
            filePath = $combine(path, fileName),
            displayName = asset.displayName,
            callbackUrl = Alfresco.constants.URL_PAGECONTEXT + "site/" + this.options.siteId + "/documentlibrary",
            encodedPath = path.length > 1 ? "?path=" + window.escape(path) : "";
         
         this.modules.actions.genericAction(
         {
            success:
            {
               activity:
               {
                  siteId: this.options.siteId,
                  activityType: "file-deleted",
                  page: "documentlibrary",
                  activityData:
                  {
                     fileName: fileName,
                     path: path
                  }
               },
               callback:
               {
                  fn: function DocumentActions_oADC_success(data)
                  {
                     window.location = callbackUrl + encodedPath;
                  }
               }
            },
            failure:
            {
               message: this.msg("message.delete.failure", displayName)
            },
            webscript:
            {
               method: Alfresco.util.Ajax.DELETE,
               name: "file/site/{site}/{container}{path}/{file}",
               params:
               {
                  site: this.options.siteId,
                  container: this.options.containerId,
                  path: path,
                  file: fileName
               }
            }
         });
      }      
   }, true);
})();
