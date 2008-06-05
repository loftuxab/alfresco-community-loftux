/*

YAHOO.util.Dom.get("template.documentlist.documentlibrary-body").clientWidth

*/


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
 * DocumentList component.
 * 
 * @namespace Alfresco
 * @class Alfresco.DocumentList
 */
(function()
{
   /**
    * DocumentList constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.DocumentList} The new DocumentList instance
    * @constructor
    */
   Alfresco.DocumentList = function(htmlId)
   {
      this.name = "Alfresco.DocumentList";
      this.id = htmlId;
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "menu", "container", "datasource", "datatable", "history"], this.onComponentsLoaded, this);
      
      return this;
   }
   
   Alfresco.DocumentList.prototype =
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
          * Flag indicating whether folders are visible or not.
          * 
          * @property showFolders
          * @type boolean
          */
         showFolders: false,

         /**
          * Flag indicating whether the list shows a detailed view or a simple one.
          * 
          * @property detailedView
          * @type boolean
          */
         detailedView: true,

         /**
          * Current siteId.
          * 
          * @property siteId
          * @type string
          */
         siteId: "",

         /**
          * ComponentId representing root container
          *
          * @property componentId
          * @type string
          * @default "documentLibrary"
          */
         componentId: "documentLibrary",

         /**
          * Initial path to show on load.
          * 
          * @property initialPath
          * @type string
          */
         initialPath: ""
      },
      
      /**
       * Current path being browsed.
       * 
       * @property currentPath
       * @type string
       */
      currentPath: "",

      /**
       * FileUpload module instance.
       * 
       * @property fileUpload
       * @type Alfresco.module.FileUpload
       */
      fileUpload: null,

      /**
       * Object container for storing YUI widget instances.
       * 
       * @property widgets
       * @type object
       */
       widgets: {},

       /**
        * Object container for storing module instances.
        * 
        * @property modules
        * @type object
        */
        modules: {},

        /**
         * Object container for storing action markup elements.
         * 
         * @property actions
         * @type object
         */
         actions: {},

        /**
         * Array of selected states for visible files.
         * 
         * @property selectedFiles
         * @type array
         */
         selectedFiles: [],

      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       */
      setOptions: function DL_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },
      
      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function DL_onComponentsLoaded()
      {
         YAHOO.util.Event.onContentReady(this.id, this.onReady, this, true);
      },
   
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function DL_onReady()
      {
         var Dom = YAHOO.util.Dom,
            Event = YAHOO.util.Event
            Element = YAHOO.util.Element
            History = YAHOO.util.History;

         // Reference to self used by inline functions
         var me = this;
         
         // Decoupled event listeners
         YAHOO.Bubbling.on("onDoclistPathChanged", this.onDoclistPathChanged, this);
         YAHOO.Bubbling.on("onDoclistRefresh", this.onDoclistRefresh, this);
      
         // YUI History
         var bookmarkedPath = History.getBookmarkedState("path") || "";
         while (bookmarkedPath != (bookmarkedPath = decodeURIComponent(bookmarkedPath)));
         
         this.currentPath = bookmarkedPath || this.options.initialPath || "";
         if ((this.currentPath.length > 0) && (this.currentPath[0] != "/"))
         {
            this.currentPath = "/" + this.currentPath;
         }

         // Register History Manager path update callback
         History.register("path", "", function(newPath)
         {
            this._updateDocList.call(this, (YAHOO.env.ua.gecko) ? decodeURIComponent(newPath) : newPath);
         }, null, this);

         // Initialize the browser history management library
         try
         {
             History.initialize("yui-history-field", "yui-history-iframe");
         }
         catch(e)
         {
             /*
              * The only exception that gets thrown here is when the browser is
              * not supported (Opera, or not A-grade)
              */
            Alfresco.logger.debug("DocList_onReady: Couldn't initialize HistoryManager.", e.toString());
         }
         
         // New Folder button
         this.widgets.newFolder = Alfresco.util.createYUIButton(this, "newFolder-button", this.onNewFolder);
         
         // File Upload button
         this.widgets.fileUpload = Alfresco.util.createYUIButton(this, "fileUpload-button", this.onFileUpload);

         // Selected Items menu button
         this.widgets.selectedItems = Alfresco.util.createYUIButton(this, "selectedItems-button", this.onSelectedItems,
         {
            type: "menu", 
            menu: "selectedItems-menu"
         });

         // Hide/Show Folders button
         Dom.get(this.id + "-showFolders-button").innerHTML = this.options.showFolders ? "Hide Folders" : "Show Folders";
         this.widgets.showFolders = Alfresco.util.createYUIButton(this, "showFolders-button", this.onShowFolders);

         // Detailed/Simple List button
         this.widgets.detailedView =  Alfresco.util.createYUIButton(this, "detailedView-button", this.onDetailedView);

         // Folder Up Navigation button
         this.widgets.folderUp =  Alfresco.util.createYUIButton(this, "folderUp-button", this.onFolderUp);
         
         // File Select menu button
         this.widgets.fileSelect = Alfresco.util.createYUIButton(this, "fileSelect-button", this.onFileSelect,
         {
            type: "menu", 
            menu: "fileSelect-menu"
         });

         // DataSource definition
         var uriDoclist = Alfresco.constants.PROXY_URI + "slingshot/doclib/doclist?";
         this.widgets.dataSource = new YAHOO.util.DataSource(uriDoclist);
         this.widgets.dataSource.responseType = YAHOO.util.DataSource.TYPE_JSON;
         this.widgets.dataSource.connXhrMode = "queueRequests";
         this.widgets.dataSource.responseSchema =
         {
             resultsList: "doclist.items",
             fields: ["index", "nodeRef", "type", "icon32", "name", "status", "lockedBy", "title", "description", "createdOn", "createdBy", "modifiedOn", "modifiedBy", "version", "contentUrl"]
         };
         
         // Custom error messages
         this._setDataTableErrors();
         
         this.widgets.dataSource.doBeforeParseData = function DL_doBeforeParseData(oRequest, oFullResponse)
         {
            if (oFullResponse.doclist.error)
            {
               YAHOO.widget.DataTable.MSG_ERROR = oFullResponse.doclist.error;
            }
            return oFullResponse;
         }

         /**
          * DataTable Cell Renderers
          *
          * Each cell has a custom renderer defined as a custom function. See YUI documentation for details.
          * These MUST be inline in order to have access to the Alfresco.DocumentList class (via the "me" variable).
          */

          /**
           * Selector custom datacell formatter
           *
           * @method renderCellSelected
           * @param elCell {object}
           * @param oRecord {object}
           * @param oColumn {object}
           * @param oData {object|string}
           */
          renderCellSelected = function DL_renderCellSelected(elCell, oRecord, oColumn, oData)
          {
             Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

             elCell.innerHTML = '<input type="checkbox" name="fileChecked" value="'+ oData + '"' + (me.selectedFiles[oData] ? ' checked="checked">' : '>');
          },
          
         /**
          * Thumbnail custom datacell formatter
          *
          * @method renderCellThumbnail
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         renderCellThumbnail = function DL_renderCellThumbnail(elCell, oRecord, oColumn, oData)
         {
            var name = oRecord.getData("name");
            var extn = name.substring(name.lastIndexOf("."));

            if (me.options.detailedView)
            {
               oColumn.width = 80;
               Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

               if (".doc.docx.xls.xlsx.ppt.pptx".indexOf(extn) != -1)
               {
                  elCell.innerHTML = '<span class="demo-thumbnail"></span>';
               }
               else if (oRecord.getData("type") == "folder")
               {
                  var newPath = me.currentPath + "/" + oRecord.getData("name");
                  // TODO: *** Update the onclick to be logically-bound, not via HTML
                  elCell.innerHTML = '<a href="" onclick="YAHOO.Bubbling.fire(\'onDoclistPathChanged\', {path: \'' + newPath.replace(/[']/g, "\\'") + '\'}); return false;"><span class="demo-folder"></span></a>';
               }
               else
               {
                  elCell.innerHTML = '<span class="demo-other"><img src="' + Alfresco.constants.URL_CONTEXT + oRecord.getData("icon32").substring(1) + '" /></span>';
               }
            }
            else
            {
               oColumn.width = 40;
               Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

               if (oRecord.getData("type") == "folder")
               {
                  var newPath = me.currentPath + "/" + oRecord.getData("name");
                  // TODO: *** Update the onclick to be logically-bound, not via HTML
                  elCell.innerHTML = '<a href="" onclick="YAHOO.Bubbling.fire(\'onDoclistPathChanged\', {path: \'' + newPath.replace(/[']/g, "\\'") + '\'}); return false;"><span class="demo-folder-small"></span></a>';
               }
               else
               {
                  elCell.innerHTML = '<span class="demo-other-small"><img src="' + Alfresco.constants.URL_CONTEXT + oRecord.getData("icon32").substring(1) + '" /></span>';
               }
            }
         };

         /**
          * Description/detail custom datacell formatter
          *
          * @method renderCellDescription
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         renderCellDescription = function DL_renderCellDescription(elCell, oRecord, oColumn, oData)
         {
            var desc = "";
            if (oRecord.getData("type") == "folder")
            {
               var newPath = me.currentPath + "/" + oRecord.getData("name");

               // TODO: *** Update the onclick to be logically-bound, not via HTML
               desc = '<h3><a href="" onclick="YAHOO.Bubbling.fire(\'onDoclistPathChanged\', {path: \'' + newPath.replace(/[']/g, "\\'") + '\'}); return false;"><b>' + oRecord.getData("name") + '</b></a></h3>';
               if (me.options.detailedView)
               {
                  if (oRecord.getData("description").length > 0)
                  {
                     desc += '<div class="detail"><span><b>Description:</b> ' + oRecord.getData("description") + '</span></div>';
                  }
               }
            }
            else
            {
               desc = '<h3><a target="content" href="' + Alfresco.constants.PROXY_URI + oRecord.getData("contentUrl") + '">' + oRecord.getData("name") + '</a></h3>';
               if (me.options.detailedView)
               {
                  desc += '<div class="detail"><span><b>Created on:</b> ' + Alfresco.util.formatDate(oRecord.getData("createdOn")) + '</span>';
                  desc += '<span><b>Created by:</b> ' + oRecord.getData("createdBy") + '</span>';
                  desc += '<span><b>Version:</b> ' + oRecord.getData("version") + '</span></div>';
                  desc += '<div class="detail"><span><b>Description:</b> ' + oRecord.getData("description") + '</span></div>';
                  desc += '<div class="detail"><span><b>Comments:</b> 0</span></div>';
               }
               else
               {
                  desc += '<span>' + Alfresco.util.formatDate(oRecord.getData("modifiedOn"), "dd mmmm yyyy") + '</span>';
               }
            }
            elCell.innerHTML = desc;
         };

         /**
          * Actions custom datacell formatter
          *
          * @method renderCellActions
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         renderCellActions = function DL_renderCellActions(elCell, oRecord, oColumn, oData)
         {
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");
            Dom.setStyle(elCell.parentNode, "border-left", "3px solid #fff");

            elCell.innerHTML = '<div id="' + me.id + '-actions-' + oRecord.getId() + '" class="hidden"></div>';
         };


         // DataTable column defintions
         var columnDefinitions = [
         {
            key: "index", label: "Select", sortable: false, formatter: renderCellSelected, width: 20
         },
         {
            key: "icon32", label: "Preview", sortable: false, formatter: renderCellThumbnail, width: 80
         },
         {
            key: "name", label: "Description", sortable: false, formatter: renderCellDescription
         },
         {
            key: "actions", label: "Actions", sortable: false, formatter: renderCellActions, width: 120
         }];

         // DataTable definition
         // initialRequest made here, otherwise YUI will make an automatic one with null arguments
         this.widgets.dataTable = new YAHOO.widget.DataTable(this.id + "-documents", columnDefinitions, this.widgets.dataSource,
         {
            renderLoopSize: 8,
            initialRequest: this._buildDocListParams(this.currentPath)
         });
         
         // File checked handler
         this.widgets.dataTable.subscribe("checkboxClickEvent", function(e)
         { 
            var id = parseInt(e.target.value, 10); 
            this.checked[id] = e.target.checked;
         }, this, true);
         
         // Enable row highlighting
         this.widgets.dataTable.subscribe("rowMouseoverEvent", this.onEventHighlightRow, this, true);
         this.widgets.dataTable.subscribe("rowMouseoutEvent", this.onEventUnhighlightRow, this, true);
         
         // Fire disconnected event, but add "ignore" flag this time, due to initialRequest above
         YAHOO.Bubbling.fire("onDoclistPathChanged",
         {
            doclistInitialNav: true,
            path: this.currentPath
         });

         // Hook action events
         YAHOO.Bubbling.addDefaultAction("action-link", function DL_filterAction(layer, args)
         {
            var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "span");
            if (owner !== null)
            {
               var action = owner.className;
               var target = args[1].target;
               if (typeof me[action] == "function")
               {
                  me[action].call(me, target.offsetParent);
                  args[1].stop = true;
               }
            }
      		 
            return true;
         });

         // Gather actions
         this.actions["document"] = YAHOO.util.Dom.get(this.id + "-actions-document");
         this.actions["folder"] = YAHOO.util.Dom.get(this.id + "-actions-folder");
         
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
      onNewFolder: function DL_onNewFolder(e, p_obj)
      {
         if (!this.modules.createFolder)
         {
            this.modules.createFolder = new Alfresco.module.CreateFolder(this.id + "-createFolder").setOptions(
            {
               siteId: this.options.siteId,
               componentId: this.options.componentId,
               parentPath: this.currentPath,
               onSuccess:
               {
                  fn: function DL_onNewFolder_callback()
                  {
                     YAHOO.Bubbling.fire("onDoclistRefresh");
                  },
                  scope: this
               }
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
      onFileUpload: function DL_onFileUpload(e, p_obj)
      {
         if (this.fileUpload === null)
         {
            this.fileUpload = new Alfresco.module.FileUpload(this.id + "-fileUpload");
         }
         
         // Multi-file upload configuration
         var multiUploadConfig =
         {
            siteId: this.options.siteId,
            componentId: this.options.componentId,
            path: this.currentPath,
            title: "Upload file(s)",
            filter: [],
            multiSelect: true,
            noOfVisibleRows: 5,
            versionInput: false
         }
         this.fileUpload.show(multiUploadConfig);
         YAHOO.util.Event.preventDefault(e);
      },
      
       /**
        * Selected Items button click handler
        *
        * @method onSelectedItems
        * @param sType {string} Event type, e.g. "click"
        * @param aArgs {array} Arguments array, [0] = DomEvent, [1] = EventTarget
        * @param p_obj {object} Object passed back from subscribe method
        */
      onSelectedItems: function DL_onSelectedItems(sType, aArgs, p_obj)
      {
         var domEvent = aArgs[0]
         var eventTarget = aArgs[1];
      },

      /**
       * Show/Hide folders button click handler
       *
       * @method onShowFolders
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onShowFolders: function DL_onShowFolders(e, p_obj)
      {
         this.options.showFolders = !this.options.showFolders;
         p_obj.set("label", (this.options.showFolders ? "Hide Folders" : "Show Folders"));

         YAHOO.Bubbling.fire("onDoclistRefresh");
         YAHOO.util.Event.preventDefault(e);
      },
      
      /**
       * Show/Hide detailed list button click handler
       *
       * @method onDetailedView
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onDetailedView: function DL_onDetailedView(e, p_obj)
      {
         this.options.detailedView = !this.options.detailedView;
         p_obj.set("label", (this.options.detailedView ? "Simple List" : "Detailed List"));

         YAHOO.Bubbling.fire("onDoclistRefresh");
         YAHOO.util.Event.preventDefault(e);
      },
      
      /**
       * Folder Up Navigate button click handler
       *
       * @method onFolderUp
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onFolderUp: function DL_onFolderUp(e, p_obj)
      {
         var newPath = this.currentPath.substring(0, this.currentPath.lastIndexOf("/"));
         YAHOO.Bubbling.fire("onDoclistPathChanged",
         {
            path: newPath
         });
         YAHOO.util.Event.preventDefault(e);
      },
      
       /**
        * Multi-file select button click handler
        *
        * @method onFileSelect
        * @param sType {string} Event type, e.g. "click"
        * @param aArgs {array} Arguments array, [0] = DomEvent, [1] = EventTarget
        * @param p_obj {object} Object passed back from subscribe method
        */
      onFileSelect: function DL_onFileSelect(sType, aArgs, p_obj)
      {
         var domEvent = aArgs[0]
         var eventTarget = aArgs[1];

         var checks = YAHOO.util.Selector.query('input[type="checkbox"]', this.widgets.dataTable.id);
         var len = checks.length; 

         switch (eventTarget.value)
         {
            case "all":
               for (var i = 0; i < len; ++i)
               {
                  this.selectedFiles[i] = checks[i].checked = true;
               }
               break;
            
            case "none":
               for (var i = 0; i < len; ++i)
               {
                  this.selectedFiles[i] = checks[i].checked = false;
               }
               break;

            case "invert":
               for (var i = 0; i < len; ++i)
               {
                  this.selectedFiles[i] = checks[i].checked = !checks[i].checked;
               }
               break;

            case "documents":
               for (var i = 0; i < len; ++i)
               {
                  this.selectedFiles[i] = checks[i].checked = this.widgets.dataTable.getRecord(i).getData("type") == "document";
               }
               break;

            case "folders":
               for (var i = 0; i < len; ++i)
               {
                  this.selectedFiles[i] = checks[i].checked = this.widgets.dataTable.getRecord(i).getData("type") == "folder";
               }
               break;
         }
      },

      /**
       * Custom event handler to highlight row.
       *
       * @method onEventHighlightRow
       * @param oArgs.event {HTMLEvent} Event object.
       * @param oArgs.target {HTMLElement} Target element.
       */
      onEventHighlightRow: function DL_onEventHighlightRow(oArgs)
      {
         var target = oArgs.target;
         // activeId is the element id of the active table cell where we'll inject the actual links
         var activeId = this.id + "-actions-" + target.yuiRecordId;
         
         // Inject the correct action elements into the activeId element
         var elActive = YAHOO.util.Dom.get(activeId);
         if (elActive.firstChild === null)
         {
            // Retrieve the actionType - currently keyed off folder or file type.
            // TODO (M): Data webscript to inject action type into data record
            // TODO (S): Cache the types for each recordId?
            var actionType = this.widgets.dataTable.getRecord(target).getData("type");

            var elActions = YAHOO.util.Dom.get(this.id + "-actions-" + actionType);
            var clone = elActions.cloneNode(true);
            clone.id = activeId + "_a";
            elActive.appendChild(clone);
         }
         // Show the actions
         YAHOO.util.Dom.removeClass(elActive, "hidden");
         
         // Call through to get the row highlighted by YUI
         this.widgets.dataTable.onEventHighlightRow.call(this.widgets.dataTable, oArgs);
      },

      /**
       * Custom event handler to unhighlight row.
       *
       * @method onEventUnhighlightRow
       * @param oArgs.event {HTMLEvent} Event object.
       * @param oArgs.target {HTMLElement} Target element.
       */
      onEventUnhighlightRow: function DL_onEventUnhighlightRow(oArgs)
      {
         var target = oArgs.target;
         var actionsId = this.id + "-actions-" + target.yuiRecordId;

         // Just hide the action links
         YAHOO.util.Dom.addClass(actionsId, "hidden");
         
         // Call through to get the row unhighlighted by YUI
         this.widgets.dataTable.onEventUnhighlightRow.call(this.widgets.dataTable, oArgs);
      },


      /**
       * BUBBLING LIBRARY EVENT HANDLERS FOR ACTIONS
       * Disconnected event handlers for action event notification
       */

      /**
       * Delete Asset.
       *
       * @method onDeleteAsset
       */
      onDeleteAsset: function DL_onDeleteAsset(row)
      {
         var me = this;
         var record = this.widgets.dataTable.getRecord(row);
         Alfresco.util.PopupManager.displayPrompt(
         {
            text: "Are you sure you want to delete '" + record.getData("name") + "'?",
            buttons: [
            {
               text: "Delete",
               handler: function DL_onDeleteAsset_delete()
               {
                  this.hide();
                  me._onDeleteAssetConfirm.call(me, record);
               },
               isDefault: true
            },
            {
               text: "Cancel",
               handler: function DL_onDeleteAsset_cancel()
               {
                  this.hide();
               }
            }]
         });
      },

      /**
       * Delete Asset confirmed.
       *
       * @method _onDeleteAssetConfirm
       * @private
       */
      _onDeleteAssetConfirm: function DL__onDeleteAssetConfirm(record)
      {
         var me = this;
         var obj =
         {
            successCallback:
            {
               fn: function DL__onDeleteAssetConfirm_success()
               {
                  // Fire the notification events
                  if (record.getData("type") == "folder")
                  {
                     YAHOO.Bubbling.fire("onDoclistFolderDeleted");
                  }
                  YAHOO.Bubbling.fire("onDoclistRefresh");
                  
                  // Success confirmation message
                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: record.getData("name") + " was deleted."
                  });
               },
               scope: this
            },
            failureCallback:
            {
               fn: function DL__onDeleteAssetConfirm_failure()
               {
                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: "Failed to delete '" + record.getData("name") + "'."
                  });
               },
               scope: this
            }
         }
         var action = new Alfresco.module.DoclibActions();
         action.deleteFile(this.options.siteId, this.options.componentId, this.currentPath, record.getData("name"), obj);
      },


      /**
       * BUBBLING LIBRARY EVENT HANDLERS FOR PAGE EVENTS
       * Disconnected event handlers for inter-component event notification
       */

      /**
       * Path Changed event handler
       *
       * @method onDoclistPathChanged
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onDoclistPathChanged: function DL_onDoclistPathChanged(layer, args)
      {
         var obj = args[1];
         if (obj !== null)
         {
            // Was this our "first navigation" event?
            if (!!obj.doclistInitialNav)
            {
               return;
            }
            
            // Should be a path in the arguments
            if (obj.path !== null)
            {
               try
               {
                  // Update History Manager with new path. It will callback to update the doclist
                  YAHOO.util.History.navigate("path", (YAHOO.env.ua.gecko) ? encodeURIComponent(obj.path) : obj.path);
               }
               catch (e)
               {
                  // Fallback for non-supported browsers, or hidden iframe loading delay
                  this._updateDocList.call(this, this.currentPath);
               }
            }
         }
      },
      
      /**
       * DocList Refresh Required event handler
       *
       * @method onDoclistRefresh
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onDoclistRefresh: function DL_onDoclistRefresh(layer, args)
      {
         this._updateDocList.call(this, this.currentPath);
      },

   
      /**
       * PRIVATE FUNCTIONS
       */

        /**
         * Resets the YUI DataTable errors to our custom messages
         *
         * @method _setDataTableErrors
         */
       _setDataTableErrors: function DL__setdataTableErrors()
       {
          YAHOO.widget.DataTable.MSG_EMPTY = "No documents or folders found in Document Library.";
       },
      
       /**
        * Updates document list by calling data webscript with current site and path
        *
        * @method _updateDocList
        * @param path {string} Path to navigate to
        */
      _updateDocList: function DL__updateDocList(path)
      {
         Alfresco.logger.debug("DocList_updateDocList:", path);
         
         // Reset the custom error messages
         this._setDataTableErrors();
         
         function successHandler(sRequest, oResponse, oPayload)
         {
            this.currentPath = path;
            this.widgets.dataTable.onDataReturnInitializeTable.call(this.widgets.dataTable, sRequest, oResponse, oPayload);
         }
         
         this.widgets.dataSource.sendRequest(this._buildDocListParams(path),
         {
               success: successHandler,
               failure: null,
               scope: this
         });
      },

      /**
       * Build URI parameter string for doclist JSON data webscript
       *
       * @method _buildDocListParams
       * @param path {string} Path to query
       */
       _buildDocListParams: function DL__buildDocListParams(path)
       {
          var params = "path=" + encodeURIComponent(path);
          params += "&site=" + encodeURIComponent(this.options.siteId);
          params += this.options.showFolders ? "" : "&type=documents";
          return params;
       }
   };
})();
