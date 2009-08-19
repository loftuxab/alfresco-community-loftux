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
   
   Alfresco.RecordsDocListToolbar.containerMap =
   {
      "new-series": "dod:recordSeries",
      "new-category": "dod:recordCategory",
      "new-folder": "rma:recordFolder"
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
         // New Container button: user needs "create" and container-type access
         this.widgets.newContainer = Alfresco.util.createYUIButton(this, "newContainer-button", this.onNewContainer,
         {
            disabled: true,
            value: "new-series|new-category|new-folder"
         });
         this.widgets.newContainer.createAttribute("activePermission");
         this.widgets.newContainer.setAttributeConfig("activePermission",
         {
            method: this.setNewContainerPermissions,
            owner: this,
            silent: true,
            validator: YAHOO.lang.isString,
            value: ""
         });

         // File Upload button: user needs "file" access
         this.widgets.fileUpload = Alfresco.util.createYUIButton(this, "fileUpload-button", this.onFileUpload,
         {
            disabled: true,
            value: "file"
         });

         // Import button: user needs "import" access
         this.widgets.importButton = Alfresco.util.createYUIButton(this, "import-button", this.onImport,
         {
            disabled: true,
            value: "import"
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
       * Filter Changed event handler
       *
       * @method onFilterChanged
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onFilterChanged: function DLTB_onFilterChanged(layer, args)
      {
         Alfresco.RecordsDocListToolbar.superclass.onFilterChanged.apply(this, arguments);
         
         var holdsFolderEnabled = (this.currentFilter.filterId == "holds" && this.currentFilter.filterData !== "");
         this.widgets.holdsFolderUp.set("disabled", !holdsFolderEnabled);
      },
      

      /**
       * YUI WIDGET EVENT HANDLERS
       * Handlers for standard events fired from YUI widgets, e.g. "click"
       */

       /**
        * Required because this class has declared itself a button attribute owner
        *
        * @method fireBeforeChangeEvent
        * @return {boolean} true
        */
       fireBeforeChangeEvent: function DLTB_fireBeforeChangeEvent(e)
       {
          return true;
       },

      /**
       * Called when the value of the button's "permissions" attribute is set.
       *
       * @method setNewContainerPermissions
       * @param {String} p_sPermission String indicating the value for the button's "permission" attribute.
       */
      setNewContainerPermissions: function DLTB_setNewContainerPermissions(p_sValue)
      {
         this.widgets.newContainer.set("label", this.msg("button." + p_sValue));
         this.widgets.newContainer.set("name", Alfresco.RecordsDocListToolbar.containerMap[p_sValue]);
      },

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
            this.fileUpload = Alfresco.getRecordsFileUploadInstance();
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
       * Import button click handler
       *
       * @method onImport
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onImport: function DLTB_onImport(e, p_obj)
      {
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
      },

      /**
       * Cut Off action.
       *
       * @method onActionCutoff
       */
      onActionCutoff: function DLTB_onActionCutoff()
      {
         this._dod5015Action("message.cutoff", this.modules.docList.getSelectedFiles(), "cutoff");
      },

      /**
       * Destroy action.
       *
       * @method onActionDestroy
       */
      onActionDestroy: function DLTB_onActionDestroy()
      {
         this._dod5015Action("message.destroy", this.modules.docList.getSelectedFiles(), "destro");
      },

      /**
       * Freeze action.
       *
       * @method onActionFreeze
       */
      onActionFreeze: function DLTB_onActionFreeze()
      {
         var files = this.modules.docList.getSelectedFiles();

         Alfresco.util.PopupManager.getUserInput(
         {
            title: this.msg("message.freeze.reason.title", files.length),
            text: this.msg("message.freeze.reason.label"),
            okButtonText: this.msg("button.freeze.record"),
            callback:
            {
               fn: function DLTB_oAF_callback(value)
               {
                  this._dod5015Action("message.freeze", files, "freeze",
                  {
                     "reason": value
                  });
               },
               scope: this
            }
         });
      },

      /**
       * Retain action.
       *
       * @method onActionRetain
       */
      onActionRetain: function DLTB_onActionRetain()
      {
         this._dod5015Action("message.retain", this.modules.docList.getSelectedFiles(), "retain");
      },

      /**
       * Transfer action.
       *
       * @method onActionTransfer
       */
      onActionTransfer: function DLTB_onActionTransfer()
      {
         this._dod5015Action("message.transfer", this.modules.docList.getSelectedFiles(), "transfer");
      },

      /**
       * Transfer Confirmation action.
       *
       * @method onActionTransferComplete
       */
      onActionTransferComplete: function DLTB_onActionTransferComplete()
      {
         this._dod5015Action("message.transfer-complete", this.modules.docList.getSelectedFiles(), "transfer-complete");
      },

      /**
       * Unfreeze action.
       *
       * @method onActionUnfreeze
       */
      onActionUnfreeze: function DL_onActionUnfreeze()
      {
         this._dod5015Action("message.unfreeze", this.modules.docList.getSelectedFiles(), "unfreeze");
      },
      
      
      /**
       * DOD5015 action.
       *
       * @method _dod5015Action
       * @param i18n {string} Will be appended with ".success" or ".failure" depending on action outcome
       * @param files {array} Array containig file objects to be actioned
       * @param actionName {string} Name of repository action to run
       * @param actionParams {object} Optional object literal to pass parameters to the action
       * @param configOverride {object} Optional object literal to override default configuration parameters
       * @private
       */
      _dod5015Action: function DL__dod5015Action(i18n, files, actionName, actionParams, configOverride)
      {
         var dataObj =
         {
            name: actionName,
            nodeRefs: []
         };
         
         for (var i = 0, ii = files.length; i < ii; i++)
         {
            dataObj.nodeRefs.push(files[i].nodeRef);
         }
         
         if (YAHOO.lang.isObject(actionParams))
         {
            dataObj.params = actionParams;
         }
         
         var config =
         {
            success:
            {
               event:
               {
                  name: "metadataRefresh"
               },
               message: this.msg(i18n + ".success", files.length)
            },
            failure:
            {
               message: this.msg(i18n + ".failure", files.length)
            },
            webscript:
            {
               method: Alfresco.util.Ajax.POST,
               stem: Alfresco.constants.PROXY_URI + "api/rma/actions/",
               name: "ExecutionQueue"
            },
            config:
            {
               requestContentType: Alfresco.util.Ajax.JSON,
               dataObj: dataObj
            }
         };
         
         if (YAHOO.lang.isObject(configOverride))
         {
            config = YAHOO.lang.merge(config, configOverride);
         }

         this.modules.actions.genericAction(config);
      }      
   });
})();