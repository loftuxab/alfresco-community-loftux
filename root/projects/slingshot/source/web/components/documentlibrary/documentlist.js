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
       */
      options:
      {
         /**
          * Flag indicating whether folders are visible or not.
          * 
          * @property showFolders
          * @type boolean
          */
         showFolders: true,

         /**
          * Flag indicating whether the list shows a detailed view or a simple one.
          * 
          * @property showDetail
          * @type boolean
          */
         showDetail: true,

         /**
          * Current siteId.
          * 
          * @property siteId
          * @type string
          */
         siteId: "",

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
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       */
      setOptions: function DL_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
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
            Event = YAHOO.util.Event;

         // Reference to self used by inline functions
         var me = this;
         
         // Decoupled event listeners
         YAHOO.Bubbling.on("onDoclistPathChanged", this.onDoclistPathChanged, this);
         YAHOO.Bubbling.on("onDoclistRefresh", this.onDoclistRefresh, this);
      
         // YUI History
         var bookmarkedPath = YAHOO.util.History.getBookmarkedState("path");
         this.currentPath = bookmarkedPath || this.options.initialPath || "";
         if ((this.currentPath.length > 0) && (this.currentPath[0] != "/"))
         {
            this.currentPath = "/" + this.currentPath;
         }

         // Register History Manager path update callback
         YAHOO.util.History.register("path", "", function(newPath)
         {
            this._updateDocList.call(this, newPath);
         }, null, this);

         // Initialize the browser history management library
         try
         {
             YAHOO.util.History.initialize("yui-history-field", "yui-history-iframe");
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
         this.widgets.showFolders = Alfresco.util.createYUIButton(this, "showFolders-button", this.onShowFolders);

         // Detailed/Simple List button
         this.widgets.showDetail =  Alfresco.util.createYUIButton(this, "showDetail-button", this.onShowDetail);

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
             fields: ["nodeRef", "type", "icon32", "name", "status", "lockedBy", "title", "description", "createdOn", "createdBy", "version"]
         };
         
         // Custom error messages
         YAHOO.widget.DataTable.MSG_EMPTY = "No documents or folders found in Document Library.";
         
         this.widgets.dataSource.doBeforeParseData = function(oRequest, oFullResponse)
         {
            if (oFullResponse.doclist.error)
            {
               YAHOO.widget.DataTable.MSG_ERROR = oFullResponse.doclist.error;
            }
            return oFullResponse;
         }


         /**
          * DataTable Formatters
          *
          * Each cell has a custom formatter defined as a custom function. See YUI documentation for details.
          * These MUST be inline in order to have access to the Alfresco.DocumentList class.
          */

         /**
          * Thumbnail custom datacell formatter
          *
          * @method formatThumbnail
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         formatThumbnail = function DL_formatThumbnail(elCell, oRecord, oColumn, oData)
         {
            elCell.innerHTML = "<img src=\"" + Alfresco.constants.URL_CONTEXT + oRecord.getData("icon32").substring(1) + "\" />";
         };

         /**
          * Description/detail custom datacell formatter
          *
          * @method formatDescription
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         formatDescription = function DL_formatDescription(elCell, oRecord, oColumn, oData)
         {
            var desc = "";
            if (oRecord.getData("type") == "folder")
            {
               var newPath = me.currentPath + "/" + oRecord.getData("name");

               // TODO: *** Update the onclick to be logically-bound, not via HTML
               desc = "<p><a href=\"\" onclick=\"YAHOO.Bubbling.fire('onDoclistPathChanged', {path: '" + newPath.replace(/'/g, "\'") + "'}); return false;\"><b>" + oRecord.getData("name") + "</b></a></p>"
            }
            else
            {
               desc = "<h3>" + oRecord.getData("name") + "</h3>";
               desc += "<span><b>Created onClick:</b> " + oRecord.getData("createdOn") + "</span>";
               desc += "<span><b>Created by:</b> " + oRecord.getData("createdBy") + "</span>";
               desc += "<span><b>Version:</b> " + oRecord.getData("version") + "</span>";
               desc += "<br />";
               desc += "<span><b>Description:</b> " + oRecord.getData("description") + "</span>";
               desc += "<br />";
               desc += "<span><b>Comments:</b> 0</span>";
            }
            elCell.innerHTML = desc;
         };

         /**
          * Actions custom datacell formatter
          *
          * @method formatActions
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         formatActions = function DL_formatActions(elCell, oRecord, oColumn, oData)
         {
            elCell.innerHTML = "Actions here";
         };


         // DataTable column defintions
         var columnDefinitions = [
         {
            key: "icon32", label: "Preview", sortable: false, formatter: formatThumbnail, width: 128
         },
         {
            key: "name", label: "Description", sortable: false, formatter: formatDescription
         },
         {
            key: "actions", label: "Actions", sortable: false, formatter: formatActions, width: 128
         }];

         // DataTable definition
         // initialRequest made here, otherwise YUI will make an automatic one with null arguments
         this.widgets.dataTable = new YAHOO.widget.DataTable(this.id + "-documents", columnDefinitions, this.widgets.dataSource,
         {
            renderLoopSize: 5,
            initialRequest: "site=" + encodeURIComponent(this.options.siteId) + "&path=" + encodeURIComponent(this.currentPath)
         });

         // Fire disconnected event, but add "ignore" flag this time, due to initialRequest above
         YAHOO.Bubbling.fire("onDoclistPathChanged",
         {
            doclistInitialNav: true,
            path: this.currentPath
         });
         
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
      onNewFolder: function DL_onFileUpload(e, p_obj)
      {
         if (!this.modules.createFolder)
         {
            this.modules.createFolder = new Alfresco.module.CreateFolder(this.id + "-createFolder").setOptions(
            {
               siteId: this.options.siteId,
               componentId: "documentLibrary",
               path: this.currentPath,
               onSuccess:
               {
                  fn: function()
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
         // Use  like this for multi uploads
         var multiUploadConfig =
         {
            siteId: this.options.siteId,
            componentId: "documentLibrary",
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
        * @method onFileSelect
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
       * @method onShowDetail
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onShowDetail: function DL_onShowDetail(e, p_obj)
      {
         this.options.showDetail = !this.options.showDetail;
         p_obj.set("label", (this.options.showDetail ? "Detailed List" : "Simple List"));

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
      },


      /**
       * BUBBLING LIBRARY EVENT HANDLERS
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
                  YAHOO.util.History.navigate("path", obj.path);
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
        * Updates document list by calling data webscript with current site and path
        *
        * @method _updateDocList
        * @param path {string} Path to navigate to
        */
      _updateDocList: function DL__updateDocList(path)
      {
         Alfresco.logger.debug("DocList_updateDocList:", path);
         
         function successHandler(sRequest, oResponse, oPayload)
         {
            this.currentPath = path;
            this.widgets.dataTable.onDataReturnInitializeTable.call(this.widgets.dataTable, sRequest, oResponse, oPayload);
         }
         
         this.widgets.dataSource.sendRequest("site=" + encodeURIComponent(this.options.siteId) + "&path=" + encodeURIComponent(path) + (this.options.showFolders ? "" : "&type=documents"),
         {
               success: successHandler,
               failure: null,
               scope: this
         });
      }

   };
})();
