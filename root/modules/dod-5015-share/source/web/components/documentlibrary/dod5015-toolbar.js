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
 * DocumentList Toolbar component.
 * 
 * @namespace Alfresco
 * @class Alfresco.RecordsDocListToolbar
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
    * Preferences
    */
   var PREFERENCES_ROOT = "org.alfresco.share.documentList",
      PREF_HIDE_NAVBAR = PREFERENCES_ROOT + ".hideNavBar";
   
   /**
    * RecordsDocListToolbar constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.RecordsDocListToolbar} The new RecordsDocListToolbar instance
    * @constructor
    */
   Alfresco.RecordsDocListToolbar = function(htmlId)
   {
      return Alfresco.RecordsDocListToolbar.superclass.constructor.call(this, htmlId);
   };
   
   YAHOO.extend(Alfresco.RecordsDocListToolbar, Alfresco.DocListToolbar,
   {
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function DLTB_onReady()
      {
         // New Series button: user needs "create,series" access
         this.widgets.newSeries = Alfresco.util.createYUIButton(this, "newSeries-button", this.onNewContainer,
         {
            disabled: true,
            value: "create,new-series"
         });

         // New Category button: user needs "create,category" access
         this.widgets.newCategory = Alfresco.util.createYUIButton(this, "newCategory-button", this.onNewContainer,
         {
            disabled: true,
            value: "create,new-category"
         });

         // New Folder button: user needs "create,folder" access
         this.widgets.newFolder = Alfresco.util.createYUIButton(this, "newFolder-button", this.onNewContainer,
         {
            disabled: true,
            value: "create,new-folder"
         });
         
         // File Upload button: user needs "file" access
         this.widgets.fileUpload = Alfresco.util.createYUIButton(this, "fileUpload-button", this.onFileUpload,
         {
            disabled: true,
            value: "file"
         });

         // Selected Items menu button
         this.widgets.selectedItems = Alfresco.util.createYUIButton(this, "selectedItems-button", this.onSelectedItems,
         {
            type: "menu", 
            menu: "selectedItems-menu",
            disabled: true
         });
         // Clear the lazyLoad flag and fire init event to get menu rendered into the DOM
         this.widgets.selectedItems.getMenu().lazyLoad = false;
         this.widgets.selectedItems.getMenu().initEvent.fire();
         this.widgets.selectedItems.getMenu().render();

         // Customize button
         this.widgets.customize = Alfresco.util.createYUIButton(this, "customize-button", this.onCustomize);

         // Hide/Show NavBar button
         this.widgets.hideNavBar = Alfresco.util.createYUIButton(this, "hideNavBar-button", this.onHideNavBar);
         this.widgets.hideNavBar.set("label", this.msg(this.options.hideNavBar ? "button.navbar.show" : "button.navbar.hide"));
         Dom.setStyle(this.id + "-navBar", "display", this.options.hideNavBar ? "none" : "block");
         
         // Folder Up Navigation button
         this.widgets.folderUp =  Alfresco.util.createYUIButton(this, "folderUp-button", this.onFolderUp,
         {
            disabled: true
         });

         // Holds Folder Up Navigation button
         this.widgets.holdsFolderUp =  Alfresco.util.createYUIButton(this, "holdsFolderUp-button", this.onHoldsFolderUp,
         {
            disabled: true
         });

         // DocLib Actions module
         this.modules.actions = new Alfresco.module.DoclibActions();
         
         // Reference to Document List component
         this.modules.docList = Alfresco.util.ComponentManager.findFirst("Alfresco.DocumentList");

         // Preferences service
         this.services.preferences = new Alfresco.service.Preferences();

         // Finally show the component body here to prevent UI artifacts on YUI button decoration
         Dom.setStyle(this.id + "-body", "visibility", "visible");
      },
      

      /**
       * YUI WIDGET EVENT HANDLERS
       * Handlers for standard events fired from YUI widgets, e.g. "click"
       */

      /**
       * New Container button click handler
       *
       * Look at the event source to work out what type of container to create
       *
       * @method onNewContainer
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onNewContainer: function DLTB_onNewContainer(e, p_obj)
      {
         if (!this.modules.docList)
         {
            return;
         }

         var destination = this.modules.docList.doclistMetadata.parent,
            folderType = p_obj.get("name"),
            label = "label.new-" + p_obj.get("name").replace(":", "_"),
            msgTitle = this.msg(label + ".title"),
            msgHeader = this.msg(label + ".header");

         // Intercept before dialog show
         var doBeforeDialogShow = function DLTB_oNF_doBeforeDialogShow(p_form, p_dialog)
         {
            Dom.get(p_dialog.id + "-dialogTitle").innerHTML = msgTitle;
            Dom.get(p_dialog.id + "-dialogHeader").innerHTML = msgHeader;
         };
         
         var templateUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + "components/form?itemKind={itemKind}&itemId={itemId}&destination={destination}&mode={mode}&submitType={submitType}&showCancelButton=true",
         {
            itemKind: "type",
            itemId: folderType,
            destination: destination,
            mode: "create",
            submitType: "json"
         });

         // Using Forms Service, so always create new instance
         var createFolder = new Alfresco.module.SimpleDialog(this.id + "-createFolder");

         createFolder.setOptions(
         {
            width: "33em",
            templateUrl: templateUrl,
            actionUrl: null,
            doBeforeDialogShow:
            {
               fn: doBeforeDialogShow,
               scope: this
            },
            onSuccess:
            {
               fn: function DLTB_onNewFolder_callback(response)
               {
                  var folderName = response.config.dataObj["prop_cm_name"];
                  YAHOO.Bubbling.fire("folderCreated",
                  {
                     name: folderName,
                     parentPath: this.currentPath
                  });
                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: this.msg("message.new-folder.success", folderName)
                  });
               },
               scope: this
            }
         }).show();
      },

      /**
       * File Upload button click handler
       *
       * @method onFileUpload
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onFileUpload: function DLTB_onFileUpload(e, p_obj)
      {
         if (this.fileUpload === null)
         {
            this.fileUpload = Alfresco.getFileUploadInstance(); 
         }
         
         // Show uploader for multiple files
         var multiUploadConfig =
         {
            siteId: this.options.siteId,
            containerId: this.options.containerId,
            uploadDirectory: this.currentPath,
            filter: [],
            mode: this.fileUpload.MODE_MULTI_UPLOAD,
            thumbnails: "doclib",
            onFileUploadComplete:
            {
               fn: this.onFileUploadComplete,
               scope: this
            }
         };
         this.fileUpload.show(multiUploadConfig);
      },
      
      /**
       * File Upload complete event handler
       *
       * @method onFileUploadComplete
       * @param complete {object} Object literal containing details of successful and failed uploads
       */
      onFileUploadComplete: function DLTB_onFileUploadComplete(complete)
      {
         var success = complete.successful.length, activityData, file;
         if (success > 0)
         {
            if (success < this.options.groupActivitiesAt)
            {
               // Below cutoff for grouping Activities into one
               for (var i = 0; i < success; i++)
               {
                  file = complete.successful[i];
                  activityData =
                  {
                     fileName: file.fileName,
                     nodeRef: file.nodeRef
                  };
                  this.modules.actions.postActivity(this.options.siteId, "file-added", "document-details", activityData);
               }
            }
            else
            {
               // grouped into one message
               activityData =
               {
                  fileCount: success,
                  path: this.currentPath
               };
               this.modules.actions.postActivity(this.options.siteId, "files-added", "documentlibrary", activityData);
            }
         }
      },

      /**
       * Holds Folder Up Navigate button click handler
       *
       * @method onHoldsFolderUp
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onHoldsFolderUp: function DLTB_onHoldsFolderUp(e, p_obj)
      {
         YAHOO.Bubbling.fire("filterChanged",
         {
            filterOwner: "DocListFilePlan",
            filterId: "holds",
            filterData: ""
         });
         Event.preventDefault(e);
      },

      /**
       * Copy Multiple Assets.
       *
       * @method onActionCopyTo
       */
      onActionCopyTo: function DLTB_onActionCopyTo()
      {
         this._copyMoveFileTo("copy");
      },

      /**
       * File Multiple Assets.
       *
       * @method onActionFileTo
       */
      onActionFileTo: function DLTB_onActionFileTo()
      {
         this._copyMoveFileTo("file");
      },

      /**
       * Move Multiple Assets.
       *
       * @method onActionMoveTo
       */
      onActionMoveTo: function DLTB_onActionMoveTo()
      {
         this._copyMoveFileTo("move");
      },

      /**
       * Copy/Move/File To Multiple Assets implementation.
       *
       * @method _copyMoveFileTo
       * @param mode {String} Operation mode: copy|file|move
       * @private
       */
      _copyMoveFileTo: function DLTB__copyMoveFileTo(mode)
      {
         var allowedModes =
         {
            "copy": true,
            "file": true,
            "move": true
         };
         
         if (!mode in allowedModes)
         {
            return;
         }
         
         if (!this.modules.docList)
         {
            return;
         }

         var files = this.modules.docList.getSelectedFiles();
         
         if (!this.modules.copyMoveFileTo)
         {
            this.modules.copyMoveFileTo = new Alfresco.module.RecordsCopyMoveFileTo(this.id + "-copyMoveFileTo");
         }

         this.modules.copyMoveFileTo.setOptions(
         {
            mode: mode,
            siteId: this.options.siteId,
            containerId: this.options.containerId,
            path: this.currentPath,
            files: files
         });

         this.modules.copyMoveFileTo.showDialog();
      }
   });
})();