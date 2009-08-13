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
 * DocumentList Toolbar component.
 * 
 * @namespace Alfresco
 * @class Alfresco.DocListToolbar
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
    * DocListToolbar constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.DocListToolbar} The new DocListToolbar instance
    * @constructor
    */
   Alfresco.DocListToolbar = function(htmlId)
   {
      Alfresco.DocListToolbar.superclass.constructor.call(this, "Alfresco.DocListToolbar", htmlId, ["button", "menu", "container"]);
      
      // Initialise prototype properties
      this.selectedFiles = [];
      this.currentFilter = {};

      // Decoupled event listeners
      YAHOO.Bubbling.on("filterChanged", this.onFilterChanged, this);
      YAHOO.Bubbling.on("deactivateAllControls", this.onDeactivateAllControls, this);
      YAHOO.Bubbling.on("selectedFilesChanged", this.onSelectedFilesChanged, this);
      YAHOO.Bubbling.on("userAccess", this.onUserAccess, this);

      return this;
   };
   
   YAHOO.extend(Alfresco.DocListToolbar, Alfresco.component.Base,
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
         containerId: "documentLibrary",

         /**
          * Number of multi-file uploads before grouping the Activity Post
          *
          * @property groupActivitiesAt
          * @type int
          * @default 5
          */
         groupActivitiesAt: 5,
         
         /**
          * Flag indicating whether navigation bar is visible or not.
          * 
          * @property hideNavBar
          * @type boolean
          */
         hideNavBar: false
      },
      
      /**
       * Current path being browsed.
       * 
       * @property currentPath
       * @type string
       */
      currentPath: "",

      /**
       * Current filter to choose toolbar view and populate description.
       * 
       * @property currentFilter
       * @type string
       */
      currentFilter: null,

      /**
       * FileUpload module instance.
       * 
       * @property fileUpload
       * @type Alfresco.FileUpload
       */
      fileUpload: null,

      /**
       * Array of selected states for visible files.
       * 
       * @property selectedFiles
       * @type array
       */
      selectedFiles: null,

      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function DLTB_onReady()
      {
         // New Folder button: user needs "create" access
         this.widgets.newFolder = Alfresco.util.createYUIButton(this, "newFolder-button", this.onNewFolder,
         {
            disabled: true,
            value: "create"
         });
         
         // File Upload button: user needs  "create" access
         this.widgets.fileUpload = Alfresco.util.createYUIButton(this, "fileUpload-button", this.onFileUpload,
         {
            disabled: true,
            value: "create"
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
         
         // RSS Feed link button
         this.widgets.rssFeed = Alfresco.util.createYUIButton(this, "rssFeed-button", null, 
         {
            type: "link"
         });

         // Folder Up Navigation button
         this.widgets.folderUp =  Alfresco.util.createYUIButton(this, "folderUp-button", this.onFolderUp,
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
       * New Folder button click handler
       *
       * @method onNewFolder
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onNewFolder: function DLTB_onNewFolder(e, p_obj)
      {
         var actionUrl = Alfresco.constants.PROXY_URI + $combine("slingshot/doclib/action/folder/site", this.options.siteId, this.options.containerId, this.currentPath);

         var doSetupFormsValidation = function DLTB_oNF_doSetupFormsValidation(p_form)
         {
            // Validation
            p_form.addValidation(this.id + "-createFolder-name", Alfresco.forms.validation.mandatory, null, "blur");
            p_form.addValidation(this.id + "-createFolder-name", Alfresco.forms.validation.nodeName, null, "keyup");
            p_form.addValidation(this.id + "-createFolder-name", Alfresco.forms.validation.length,
            {
               max: 256,
               crop: true
            }, "keyup");
            p_form.addValidation(this.id + "-createFolder-title", Alfresco.forms.validation.length,
            {
               max: 256,
               crop: true
            }, "keyup");
            p_form.addValidation(this.id + "-createFolder-description", Alfresco.forms.validation.length,
            {
               max: 512,
               crop: true
            }, "keyup");
            p_form.setShowSubmitStateDynamically(true, false);
         };

         if (!this.modules.createFolder)
         {
            this.modules.createFolder = new Alfresco.module.SimpleDialog(this.id + "-createFolder").setOptions(
            {
               width: "30em",
               templateUrl: Alfresco.constants.URL_SERVICECONTEXT + "modules/documentlibrary/create-folder",
               actionUrl: actionUrl,
               doSetupFormsValidation:
               {
                  fn: doSetupFormsValidation,
                  scope: this
               },
               firstFocus: this.id + "-createFolder-name",
               onSuccess:
               {
                  fn: function DLTB_onNewFolder_callback(response)
                  {
                     var folder = response.json.results[0];
                     YAHOO.Bubbling.fire("folderCreated",
                     {
                        name: folder.name,
                        parentPath: folder.parentPath,
                        nodeRef: folder.nodeRef
                     });
                     Alfresco.util.PopupManager.displayMessage(
                     {
                        text: this.msg("message.new-folder.success", folder.name)
                     });
                  },
                  scope: this
               }
            });
         }
         else
         {
            this.modules.createFolder.setOptions(
            {
               actionUrl: actionUrl,
               clearForm: true
            });
         }
         this.modules.createFolder.show();
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
        * Selected Items button click handler
        *
        * @method onSelectedItems
        * @param sType {string} Event type, e.g. "click"
        * @param aArgs {array} Arguments array, [0] = DomEvent, [1] = EventTarget
        * @param p_obj {object} Object passed back from subscribe method
        */
      onSelectedItems: function DLTB_onSelectedItems(sType, aArgs, p_obj)
      {
         var domEvent = aArgs[0];
         var eventTarget = aArgs[1];
         
         // Get the function related to the clicked item
         var fn = Alfresco.util.findEventClass(eventTarget);
         if (fn && (typeof this[fn] == "function"))
         {
            this[fn].call(this);
         }
         Event.preventDefault(domEvent);
      },
      
      /**
       * Copy Multiple Assets.
       *
       * @method onActionCopyTo
       */
      onActionCopyTo: function DLTB_onActionCopyTo()
      {
         if (!this.modules.docList)
         {
            return;
         }

         var files = this.modules.docList.getSelectedFiles();
         
         if (!this.modules.copyTo)
         {
            this.modules.copyTo = new Alfresco.module.DoclibCopyTo(this.id + "-copyTo");
         }

         this.modules.copyTo.setOptions(
         {
            siteId: this.options.siteId,
            containerId: this.options.containerId,
            path: this.currentPath,
            files: files
         });

         this.modules.copyTo.showDialog();
      },

      /**
       * Move Multiple Assets.
       *
       * @method onActionMoveTo
       */
      onActionMoveTo: function DLTB_onActionMoveTo()
      {
         if (!this.modules.docList)
         {
            return;
         }

         var files = this.modules.docList.getSelectedFiles();
         
         if (!this.modules.moveTo)
         {
            this.modules.moveTo = new Alfresco.module.DoclibMoveTo(this.id + "-moveTo");
         }

         this.modules.moveTo.setOptions(
         {
            siteId: this.options.siteId,
            containerId: this.options.containerId,
            path: this.currentPath,
            files: files,
            width: "40em"
         });

         this.modules.moveTo.showDialog();
      },

      /**
       * Delete Multiple Assets.
       *
       * @method onActionDelete
       */
      onActionDelete: function DLTB_onActionDelete()
      {
         if (!this.modules.docList)
         {
            return;
         }

         var me = this,
            files = this.modules.docList.getSelectedFiles(),
            fileNames = [];
         
         for (var i = 0, j = files.length; i < j; i++)
         {
            fileNames.push("<span class=\"" + files[i].type + "\">" + files[i].displayName + "</span>");
         }
         
         var confirmTitle = this.msg("title.multiple-delete.confirm"),
            confirmMsg = this.msg("message.multiple-delete.confirm", files.length);
         confirmMsg += "<div class=\"toolbar-file-list\">" + fileNames.join("") + "</div>";

         Alfresco.util.PopupManager.displayPrompt(
         {
            title: confirmTitle,
            text: confirmMsg,
            noEscape: true,
            modal: true,
            buttons: [
            {
               text: this.msg("button.delete"),
               handler: function DLTB_onActionDelete_delete()
               {
                  this.destroy();
                  me._onActionDeleteConfirm.call(me, files);
               }
            },
            {
               text: this.msg("button.cancel"),
               handler: function DLTB_onActionDelete_cancel()
               {
                  this.destroy();
               },
               isDefault: true
            }]
         });
      },

      /**
       * Delete Multiple Assets confirmed.
       *
       * @method _onActionDeleteConfirm
       * @param files {array} Array containing files to be deleted
       * @private
       */
      _onActionDeleteConfirm: function DLTB__onActionDeleteConfirm(files)
      {
         var multipleFiles = [], i, ii;
         for (i = 0, ii = files.length; i < ii; i++)
         {
            multipleFiles.push(files[i].nodeRef);
         }
         
         // Success callback function
         var fnSuccess = function DLTB__oADC_success(data, files)
         {
            var result;
            var successCount = 0;

            // Did the operation succeed?
            if (!data.json.overallSuccess)
            {
               Alfresco.util.PopupManager.displayMessage(
               {
                  text: this.msg("message.multiple-delete.failure")
               });
               return;
            }

            YAHOO.Bubbling.fire("filesDeleted");

            for (i = 0, ii = data.json.totalResults; i < ii; i++)
            {
               result = data.json.results[i];
               
               if (result.success)
               {
                  successCount++;
                  
                  YAHOO.Bubbling.fire(result.type == "folder" ? "folderDeleted" : "fileDeleted",
                  {
                     multiple: true,
                     nodeRef: result.nodeRef
                  });
               }
            }

            // Activities
            var activityData;
            if (successCount > 0)
            {
               if (successCount < this.options.groupActivitiesAt)
               {
                  // Below cutoff for grouping Activities into one
                  for (i = 0; i < successCount; i++)
                  {
                     activityData =
                     {
                        fileName: data.json.results[i].id,
                        path: this.currentPath
                     };
                     this.modules.actions.postActivity(this.options.siteId, "file-deleted", "documentlibrary", activityData);
                  }
               }
               else
               {
                  // grouped into one message
                  activityData =
                  {
                     fileCount: successCount,
                     path: this.currentPath
                  };
                  this.modules.actions.postActivity(this.options.siteId, "files-deleted", "documentlibrary", activityData);
               }
            }

            Alfresco.util.PopupManager.displayMessage(
            {
               text: this.msg("message.multiple-delete.success", successCount)
            });
         };
         
         // Construct the data object for the genericAction call
         this.modules.actions.genericAction(
         {
            success:
            {
               callback:
               {
                  fn: fnSuccess,
                  scope: this,
                  obj: files
               }
            },
            failure:
            {
               message: this.msg("message.multiple-delete.failure")
            },
            webscript:
            {
               method: Alfresco.util.Ajax.DELETE,
               name: "files"
            },
            wait:
            {
               message: this.msg("message.multiple-delete.please-wait")
            },
            config:
            {
               requestContentType: Alfresco.util.Ajax.JSON,
               dataObj:
               {
                  nodeRefs: multipleFiles
               }
            }
         });
      },

      /**
       * Assign Multiple Assets to Workflow.
       *
       * @method onActionAssignWorkflow
       */
      onActionAssignWorkflow: function DLTB_onActionAssignWorkflow()
      {
         if (!this.modules.docList)
         {
            return;
         }

         var files = this.modules.docList.getSelectedFiles();
         
         if (!this.modules.workflow)
         {
            this.modules.workflow = new Alfresco.module.DoclibWorkflow(this.id + "-workflow").setOptions(
            {
               siteId: this.options.siteId,
               containerId: this.options.containerId,
               files: files
            });
         }
         else
         {
            this.modules.workflow.setOptions(
            {
               files: files
            });
         }
         this.modules.workflow.showDialog();
      },

      /**
       * Manage Permissions of Multiple Assets.
       *
       * @method onActionManagePermissions
       */
      onActionManagePermissions: function DLTB_onActionManagePermissions()
      {
         if (!this.modules.docList)
         {
            return;
         }

         var files = this.modules.docList.getSelectedFiles();
         
         if (!this.modules.permissions)
         {
            this.modules.permissions = new Alfresco.module.DoclibPermissions(this.id + "-workflow").setOptions(
            {
               siteId: this.options.siteId,
               containerId: this.options.containerId,
               files: files
            });
         }
         else
         {
            this.modules.permissions.setOptions(
            {
               files: files
            });
         }
         this.modules.permissions.showDialog();
      },

      /**
       * Deselect currectly selected assets.
       *
       * @method onActionDeselectAll
       */
      onActionDeselectAll: function DLTB_onActionDeselectAll()
      {
         if (this.modules.docList)
         {
            this.modules.docList.selectFiles("selectNone");
         }
      },

      /**
       * Customize button click handler
       *
       * @method onCustomize
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onCustomize: function DLTB_onCustomize(e, p_obj)
      {
         
      },

      /**
       * Show/Hide navigation bar button click handler
       *
       * @method onHideNavBar
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onHideNavBar: function DLTB_onHideNavBar(e, p_obj)
      {
         this.options.hideNavBar = !this.options.hideNavBar;
         p_obj.set("label", this.msg(this.options.hideNavBar ? "button.navbar.show" : "button.navbar.hide"));

         this.services.preferences.set(PREF_HIDE_NAVBAR, this.options.hideNavBar);

         Dom.setStyle(this.id + "-navBar", "display", this.options.hideNavBar ? "none" : "block");
         Event.preventDefault(e);
      },

      /**
       * Folder Up Navigate button click handler
       *
       * @method onFolderUp
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onFolderUp: function DLTB_onFolderUp(e, p_obj)
      {
         var newPath = this.currentPath.substring(0, this.currentPath.lastIndexOf("/")),
            filter = this.currentFilter;
         
         filter.filterData = newPath;

         YAHOO.Bubbling.fire("filterChanged", filter);
         Event.preventDefault(e);
      },

      /**
       * Path Changed handler
       *
       * @method pathChanged
       * @param path {string} New path
       */
      pathChanged: function DLTB_pathChanged(path)
      {
         this.currentPath = path;
         this._generateBreadcrumb();
         this._generateRSSFeedUrl();
         
         // Enable/disable the Folder Up button
         var paths = this.currentPath.split("/");
         // Check for root path special case
         if (this.currentPath === "/")
         {
            paths = ["/"];
         }
         this.widgets.folderUp.set("disabled", paths.length < 2);
      },
      

      /**
       * BUBBLING LIBRARY EVENT HANDLERS FOR PAGE EVENTS
       * Disconnected event handlers for inter-component event notification
       */

      /**
       * Filter Changed event handler
       *
       * @method onFilterChanged
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onFilterChanged: function DLTB_onFilterChanged(layer, args)
      {
         var obj = args[1];
         if (obj && (typeof obj.filterId !== "undefined"))
         {
            obj.filterOwner = obj.filterOwner || Alfresco.util.FilterManager.getOwner(obj.filterId);

            if (this.currentFilter.filterOwner != obj.filterOwner || this.currentFilter.filterId != obj.filterId)
            {
               var filterOwner = obj.filterOwner.split(".")[1],
                  ownerIdClass = filterOwner + "_" + obj.filterId;
               
               // Obtain array of DIVs we might want to hide
               var divs = YAHOO.util.Selector.query('div.hideable', Dom.get(this.id)), div;
               for (var i = 0, j = divs.length; i < j; i++)
               {
                  div = divs[i];
                  if (Dom.hasClass(div, filterOwner) || Dom.hasClass(div, ownerIdClass))
                  {
                     Dom.removeClass(div, "toolbar-hidden");
                  }
                  else
                  {
                     Dom.addClass(div, "toolbar-hidden");
                  }
               }
            }
            
            Alfresco.logger.debug("DLTB_onFilterChanged", "Old Filter", this.currentFilter);
            this.currentFilter = Alfresco.util.cleanBubblingObject(obj);
            Alfresco.logger.debug("DLTB_onFilterChanged", "New Filter", this.currentFilter);
            
            if (this.currentFilter.filterId == "path")
            {
               this.pathChanged(this.currentFilter.filterData);
            }
            else
            {
               this._generateDescription();
               this._generateRSSFeedUrl();
            }
         }
      },

      /**
       * Deactivate All Controls event handler
       *
       * @method onDeactivateAllControls
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onDeactivateAllControls: function DLTB_onDeactivateAllControls(layer, args)
      {
         var index, fnDisable = Alfresco.util.disableYUIButton;
         for (index in this.widgets)
         {
            if (this.widgets.hasOwnProperty(index))
            {
               fnDisable(this.widgets[index]);
            }
         }
      },

      /**
       * User Access event handler
       *
       * @method onUserAccess
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onUserAccess: function DLTB_onUserAccess(layer, args)
      {
         var obj = args[1];
         if ((obj !== null) && (obj.userAccess !== null))
         {
            var widget, widgetPermissions, index;
            for (index in this.widgets)
            {
               if (this.widgets.hasOwnProperty(index))
               {
                  widget = this.widgets[index];
                  if (widget.get("srcelement").className != "no-access-check")
                  {
                     widget.set("disabled", false);
                     if (typeof widget.get("value") == "string")
                     {
                        widgetPermissions = widget.get("value").split(",");
                        for (var i = 0, ii = widgetPermissions.length; i < ii; i++)
                        {
                           if (!obj.userAccess[widgetPermissions[i]])
                           {
                              widget.set("disabled", true);
                              break;
                           }
                        }
                     }
                  }
               }
            }
         }
      },

      /**
       * Selected Files Changed event handler.
       * Determines whether to enable or disable the multi-file action drop-down
       *
       * @method onSelectedFilesChanged
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onSelectedFilesChanged: function DLTB_onSelectedFilesChanged(layer, args)
      {
         if (this.modules.docList)
         {
            var files = this.modules.docList.getSelectedFiles(), fileTypes = [], file,
               userAccess = {}, fileAccess, index,
               menuItems = this.widgets.selectedItems.getMenu().getItems(), menuItem,
               actionPermissions, typesSupported, disabled,
               i, ii;
            
            // Check each file for user permissions
            for (i = 0, ii = files.length; i < ii; i++)
            {
               file = files[i];
               
               // Required user access level - logical AND of each file's permissions
               fileAccess = file.permissions.userAccess;
               for (index in fileAccess)
               {
                  if (fileAccess.hasOwnProperty(index))
                  {
                     userAccess[index] = (userAccess[index] === undefined ? fileAccess[index] : userAccess[index] && fileAccess[index]);
                  }
               }
               
               // Make a note of all selected file types Using a hybrid array/object so we can use both array.length and "x in object"
               if (!(file.type in fileTypes))
               {
                  fileTypes[file.type] = true;
                  fileTypes.push(file.type);
               }
            }

            // Now go through the menu items, setting the disabled flag appropriately
            for (index in menuItems)
            {
               if (menuItems.hasOwnProperty(index))
               {
                  menuItem = menuItems[index];
                  disabled = false;

                  if (menuItem.element.firstChild)
                  {
                     // Check permissions required
                     if (menuItem.element.firstChild.rel && menuItem.element.firstChild.rel !== "")
                     {
                        actionPermissions = menuItem.element.firstChild.rel.split(",");
                        for (i = 0, ii = actionPermissions.length; i < ii; i++)
                        {
                           if (!userAccess[actionPermissions[i]])
                           {
                              disabled = true;
                              break;
                           }
                        }
                     }

                     if (!disabled)
                     {
                        // Check filetypes supported
                        if (menuItem.element.firstChild.type && menuItem.element.firstChild.type !== "")
                        {
                           typesSupported = Alfresco.util.arrayToObject(menuItem.element.firstChild.type.split(","));

                           for (i = 0, ii = fileTypes.length; i < ii; i++)
                           {
                              if (!(fileTypes[i] in typesSupported))
                              {
                                 disabled = true;
                                 break;
                              }
                           }
                        }
                     }
                     menuItem.cfg.setProperty("disabled", disabled);
                  }
               }
            }
            this.widgets.selectedItems.set("disabled", (files.length === 0));
         }
      },
   
      /**
       * PRIVATE FUNCTIONS
       */

      /**
       * Generates the HTML mark-up for the breadcrumb from the currentPath
       *
       * @method _generateBreadcrumb
       * @private
       */
      _generateBreadcrumb: function DLTB__generateBreadcrumb()
      {
         var divBC = Dom.get(this.id + "-breadcrumb");
         if (divBC === null)
         {
            return;
         }
         divBC.innerHTML = "";
         
         var paths = this.currentPath.split("/");
         // Check for root path special case
         if (this.currentPath === "/")
         {
            paths = ["/"];
         }
         // Clone the array and re-use the root node name from the DocListTree
         var me = this,
            displayPaths = paths.concat();
         
         displayPaths[0] = Alfresco.util.message("node.root", "Alfresco.DocListTree");

         var fnBreadcrumbClick = function DLTB__gB_click(e, path)
         {
            var filter = me.currentFilter;
            filter.filterData = path;
            
            YAHOO.Bubbling.fire("filterChanged", filter);
            Event.stopEvent(e);
         };
         
         var eBreadcrumb = new Element(divBC);
         for (var i = 0, j = paths.length; i < j; ++i)
         {
            var eCrumb = new Element(document.createElement("span"));
            eCrumb.addClass("crumb");
            if (i === 0)
            {
               eCrumb.addClass("first");
            }
            else
            {
               eCrumb.addClass("folder");
            }

            // Last crumb shouldn't be rendered as a link
            if (j - i < 2)
            {
               if (j > 1)
               {
                  eCrumb.addClass("last");
               }
               eCrumb.set("innerHTML", displayPaths[i]);
            }
            else
            {
               var eLink = new Element(document.createElement("a"),
               {
                  href: "",
                  innerHTML: displayPaths[i]
               });
               var newPath = paths.slice(0, i+1).join("/");
               eLink.on("click", fnBreadcrumbClick, newPath);
               eCrumb.appendChild(eLink);
               eCrumb.appendChild(new Element(document.createElement("span"),
               {
                  innerHTML: "&gt;",
                  className: "separator"
               }));
            }
            eBreadcrumb.appendChild(eCrumb);
         }
      },

      /**
       * Generates the HTML mark-up for the description from the currentFilter
       *
       * @method _generateDescription
       * @private
       */
      _generateDescription: function DLTB__generateDescription()
      {
         var divDesc, eDivDesc, eDescMsg, eDescMore, filterDisplay;
         
         divDesc = Dom.get(this.id + "-description");
         if (divDesc === null)
         {
            return;
         }
         
         while (divDesc.hasChildNodes())
         {
            divDesc.removeChild(divDesc.lastChild);
         }
         
         // If filterDisplay is provided, then use that instead (e.g. for cases where filterData is a nodeRef)
         filterDisplay = typeof this.currentFilter.filterDisplay !== "undefined" ? this.currentFilter.filterDisplay : this.currentFilter.filterData;
         
         eDescMsg = new Element(document.createElement("div"),
         {
            innerHTML: this.msg("description." + this.currentFilter.filterId, filterDisplay)
         });
         eDescMsg.addClass("message");

         // Don't display a "more" message that contains the "{0}" placeholder unless filterData is populated
         if (this.msg("description." + this.currentFilter.filterId + ".more").indexOf("{0}") == -1 || filterDisplay !== "")
         {
            eDescMore = new Element(document.createElement("span"),
            {
               innerHTML: this.msg("description." + this.currentFilter.filterId + ".more", $html(filterDisplay))
            });
            eDescMore.addClass("more");

            eDescMsg.appendChild(eDescMore);
         }

         eDivDesc = new Element(divDesc);
         eDivDesc.appendChild(eDescMsg);
      },
      
      /**
       * Generates the HTML mark-up for the RSS feed link
       *
       * @method _generateRSSFeedUrl
       * @private
       */
      _generateRSSFeedUrl: function DLTB__generateRSSFeedUrl()
      {
         if (this.widgets.rssFeed && this.modules.docList)
         {
            var params = YAHOO.lang.substitute("{type}/site/{site}/{container}{path}",
            {
               type: this.modules.docList.options.showFolders ? "all" : "documents",
               site: encodeURIComponent(this.options.siteId),
               container: encodeURIComponent(this.options.containerId),
               path: encodeURI(this.currentPath)
            });

            params += "?filter=" + encodeURIComponent(this.currentFilter.filterId);
            if (this.currentFilter.filterData)
            {
               params += "&filterData=" + encodeURIComponent(this.currentFilter.filterData);             
            }
            params += "&format=rss";
            
            this.widgets.rssFeed.set("href", Alfresco.constants.URL_FEEDSERVICECONTEXT + "components/documentlibrary/feed/" + params);
         }
      }
   });
})();