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
 * Folder actions component.
 * 
 * @namespace Alfresco
 * @class Alfresco.FolderActions
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
   var $html = Alfresco.util.encodeHTML;
   
   /**
    * FolderActions constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.FolderActions} The new FolderActions instance
    * @constructor
    */
   Alfresco.FolderActions = function(htmlId)
   {
      this.name = "Alfresco.FolderActions";
      this.id = htmlId;
      
      // initialise prototype properties
      this.widgets = {};
      this.modules = {};
      this.folderData = null;
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button"], this.onComponentsLoaded, this);
   
      /* Decoupled event listeners */
      YAHOO.Bubbling.on("folderDetailsAvailable", this.onFolderDetailsAvailable, this);
      YAHOO.Bubbling.on("fileRenamed", this.onDetailsEdited, this);
      YAHOO.Bubbling.on("folderRenamed", this.onDetailsEdited, this);
      
      return this;
   };
   
   Alfresco.FolderActions.prototype =
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
       * Object container for storing YUI widget instances.
       * 
       * @property widgets
       * @type object
       */
       widgets: {},
       
      /**
       * Object container for storing YUI widget instances.
       * 
       * @property widgets
       * @type object
       */
       modules: {},
       
       /**
        * The data for the folder
        * 
        * @property folderData
        * @type object
        */
       folderData: null,
       
      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.Search} returns 'this' for method chaining
       */
      setOptions: function SiteMembers_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },
      
      /**
       * Set messages for this component.
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {Alfresco.Search} returns 'this' for method chaining
       */
      setMessages: function FolderActions_setMessages(obj)
      {
         Alfresco.util.addMessages(obj, this.name);
         return this;
      },
      
      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function FolderActions_onComponentsLoaded()
      {
         // do nothing
      },
      
      /**
       * Event handler called when the "folderDetailsAvailable" event is received
       */
      onFolderDetailsAvailable: function FolderActions_onFolderDetailsAvailable(layer, args)
      {
         // reference to self
         var me = this;
         
         // remember the data for the folder
         this.folderData = args[1];
         
         // update the href for the edit metadata link
         var metadataUrl = Alfresco.constants.URL_PAGECONTEXT + "site/" + this.options.siteId + 
            "/edit-metadata?nodeRef=" + this.folderData.nodeRef;
         try
         {
            Dom.get(this.id + "-edit-metadata-action").href = metadataUrl;
         }
         catch (e)
         {
            // Edit metadata action missing from config
         }
         
         // Disable actions not accessible to current user
         var actionsContainer = Dom.get(this.id + "-actionSet-folder");
         if (this.folderData.permissions && this.folderData.permissions.userAccess)
         {
            var userAccess = this.folderData.permissions.userAccess;
            var actions = YAHOO.util.Selector.query("div", actionsContainer);
            var action, actionPermissions, i, ii, j, jj, actionAllowed;
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
         var fnActionHandler = function FolderActions_fnActionHandler(layer, args)
         {
            var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "div");
            if (owner !== null)
            {
               var action = owner.className;
               var target = args[1].target;
               if (typeof me[action] == "function")
               {
                  args[1].stop = true;
                  me[action].call(me, target.offsetParent, owner);
               }
            }
      		 
            return true;
         };
         
         YAHOO.Bubbling.addDefaultAction("action-link", fnActionHandler);
         
         // DocLib Actions module
         this.modules.actions = new Alfresco.module.DoclibActions();
      },

      /**
       * Fired by Metadata edit dialog when new details have been successfully saved.
       * Prompts a reload of the page, as we can't be sure exactly what metadata may have been editing by the system.
       *
       * @method onReady
       */
      onDetailsEdited: function FolderActions_onDetailsEdited(layer, args)
      {
         // Reload the page, rather than trying to replace edited metadata in-place
         window.location.reload(true);
      },
      
      /**
       * Copy single folder.
       *
       * @method onActionCopyTo
       * @param obj {object} Not used
       */
      onActionCopyTo: function FolderActions_onActionCopyTo(obj)
      {
         if (!this.modules.copyTo)
         {
            this.modules.copyTo = new Alfresco.module.DoclibCopyTo(this.id + "-copyTo").setOptions(
            {
               siteId: this.options.siteId,
               containerId: this.options.containerId,
               path: this.folderData.location.path,
               files: this.folderData
            });
         }
         else
         {
            this.modules.copyTo.setOptions(
            {
               path: this.folderData.location.path,
               files: this.folderData
            });
         }

         // show the dialog         
         this.modules.copyTo.showDialog();
      },
      
      /**
       * Move single folder.
       *
       * @method onActionMoveTo
       * @param obj {object} Not used
       */
      onActionMoveTo: function FolderActions_onActionMoveTo(obj)
      {
         if (!this.modules.moveTo)
         {
            this.modules.moveTo = new Alfresco.module.DoclibMoveTo(this.id + "-moveTo").setOptions(
            {
               siteId: this.options.siteId,
               containerId: this.options.containerId,
               path: this.folderData.location.path,
               files: this.folderData
            });
         }
         else
         {
            this.modules.moveTo.setOptions(
            {
               path: this.folderData.location.path,
               files: this.folderData
            });
         }
         
         // show the dialog
         this.modules.moveTo.showDialog();
      },
      
      /**
       * Delete Asset.
       *
       * @method onActionDelete
       * @param obj {object} Not used
       */
      onActionDelete: function FolderActions_onActionDelete(obj)
      {
         var me = this;

         Alfresco.util.PopupManager.displayPrompt(
         {
            text: this._msg("folder-actions.delete.confirm", this.folderData.fileName),
            buttons: [
            {
               text: this._msg("folder-actions.button.delete"),
               handler: function FolderActions_onActionDelete_delete()
               {
                  this.destroy();
                  me._onActionDeleteConfirm.call(me, this.folderData);
               }
            },
            {
               text: this._msg("button.cancel"),
               handler: function FolderActions_onActionDelete_cancel()
               {
                  this.destroy();
               },
               isDefault: true
            }]
         });
      },

      /**
       * Delete Asset confirmed.
       *
       * @method _onActionDeleteConfirm
       * @param obj {object} Not used
       * @private
       */
      _onActionDeleteConfirm: function FolderActions__onActionDeleteConfirm(record)
      {
         var fileType = this.folderData.type,
            fileName = this.folderData.fileName,
            filePath = this.folderData.location.path + "/" + fileName,
            displayName = this.folderData.displayName;
         
         var callbackUrl = Alfresco.constants.URL_PAGECONTEXT + "site/" + this.options.siteId + "/documentlibrary#path=";
         var encodedPath = (YAHOO.env.ua.gecko > 0) ? encodeURIComponent(this.folderData.location.path) : this.folderData.location.path;
         
         this.modules.actions.genericAction(
         {
            success:
            {
               callback:
               {
                  fn: function FolderActions_oADC_success(data)
                  {
                     window.location = callbackUrl + encodedPath;
                  }
               }
            },
            failure:
            {
               message: this._msg("folder-actions.delete.failure", displayName)
            },
            webscript:
            {
               name: "file/site/{site}/{container}{path}/{file}",
               method: Alfresco.util.Ajax.DELETE
            },
            params:
            {
               site: this.options.siteId,
               container: this.options.containerId,
               path: this.folderData.location.path,
               file: fileName
            }
         });
      },

      /**
       * Set permissions on a single document or folder.
       *
       * @method onActionManagePermissions
       * @param row {object} DataTable row representing file to be actioned
       */
      onActionManagePermissions: function FolderActions_onActionManagePermissions(row)
      {
         if (!this.modules.permissions)
         {
            this.modules.permissions = new Alfresco.module.DoclibPermissions(this.id + "-permissions").setOptions(
            {
               siteId: this.options.siteId,
               containerId: this.options.containerId,
               path: this.folderData.location.path,
               files: this.folderData
            });
         }
         else
         {
            this.modules.permissions.setOptions(
            {
               path: this.folderData.location.path,
               files: this.folderData
            });
         }
         this.modules.permissions.showDialog();
      },
      
      /**
       * Gets a custom message
       *
       * @method _msg
       * @param messageId {string} The messageId to retrieve
       * @return {string} The custom message
       * @private
       */
      _msg: function FolderActions__msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, "Alfresco.FolderActions", Array.prototype.slice.call(arguments).slice(1));
      }
   };
})();
