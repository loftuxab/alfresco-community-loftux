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
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      Element = YAHOO.util.Element;
   
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
      Alfresco.util.YUILoaderHelper.require(["button", "menu", "container", "datasource", "datatable", "json", "history"], this.onComponentsLoaded, this);
      
      // Decoupled event listeners
      YAHOO.Bubbling.on("pathChanged", this.onPathChanged, this);
      YAHOO.Bubbling.on("doclistRefresh", this.onDocListRefresh, this);
      YAHOO.Bubbling.on("highlightFile", this.onHighlightFile, this);
      YAHOO.Bubbling.on("fileCopied", this.onDocListRefresh, this);
      YAHOO.Bubbling.on("fileDeleted", this.onDocListRefresh, this);
      YAHOO.Bubbling.on("fileMoved", this.onDocListRefresh, this);
      YAHOO.Bubbling.on("folderCreated", this.onDocListRefresh, this);
      YAHOO.Bubbling.on("folderCopied", this.onDocListRefresh, this);
      YAHOO.Bubbling.on("folderDeleted", this.onDocListRefresh, this);
      YAHOO.Bubbling.on("folderMoved", this.onDocListRefresh, this);
      YAHOO.Bubbling.on("filterChanged", this.onFilterChanged, this);
      YAHOO.Bubbling.on("deactivateAllControls", this.onDeactivateAllControls, this);
   
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
          * ContainerId representing root container
          *
          * @property containerId
          * @type string
          * @default "documentLibrary"
          */
         containerId: "documentLibrary",

         /**
          * Initial path to show on load.
          * 
          * @property initialPath
          * @type string
          */
         initialPath: "",

         /**
          * Initial filter to show on load.
          * 
          * @property initialFilter
          * @type object
          */
         initialFilter: {},
         
         /**
          * Delay time value for "More Actions" popup, in milliseconds
          *
          * @property actionsPopupTimeout
          * @type int
          * @default 500
          */
         actionsPopupTimeout: 500,

         /**
          * FileName to highlight on initial DataTable render.
          * 
          * @property highlightFile
          * @type string
          */
         highlightFile: null
      },
      
      /**
       * Current path being browsed.
       * 
       * @property currentPath
       * @type string
       */
      currentPath: "",

      /**
       * Current filterId to filter document list.
       * 
       * @property currentFilterId
       * @type string
       */
      currentFilterId: "",

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
       * Objectliteral of selected states for visible files (indexed by nodeRef).
       * 
       * @property selectedFiles
       * @type object
       */
      selectedFiles: {},

      /**
       * Whether "More Actions" pop-up is currently visible.
       * 
       * @property showingMoreActions
       * @type boolean
       * @default false
       */
      showingMoreActions: false,

      /**
       * Flag to indicate this HistoryManager event was expected.
       * An unexpected event means the user has updated the URL hash manually.
       * 
       * @property expectedHistoryEvent
       * @type boolean
       * @default false
       */
      expectedHistoryEvent: false,

      /**
       * Deferred highlight row event when showing "More Actions".
       * 
       * @property deferHighlightRow
       * @type object
       * @default null
       */
      deferHighlightRow: null,

      /**
       * Deferred unhighlight row event when showing "More Actions".
       * 
       * @property deferUnhighlightRow
       * @type object
       * @default null
       */
      deferUnhighlightRow: null,

      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.DocListTree} returns 'this' for method chaining
       */
      setOptions: function DL_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },
      
      /**
       * Set messages for this component.
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {Alfresco.DocListTree} returns 'this' for method chaining
       */
      setMessages: function DL_setMessages(obj)
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
      onComponentsLoaded: function DL_onComponentsLoaded()
      {
         Event.onContentReady(this.id, this.onReady, this, true);
      },
   
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function DL_onReady()
      {
         // Reference to self used by inline functions
         var me = this;
         
         // YUI History
         var bookmarkedPath = YAHOO.util.History.getBookmarkedState("path") || "";
         while (bookmarkedPath != (bookmarkedPath = decodeURIComponent(bookmarkedPath)));
         
         this.currentPath = bookmarkedPath || this.options.initialPath || "";
         if ((this.currentPath.length > 0) && (this.currentPath[0] != "/"))
         {
            this.currentPath = "/" + this.currentPath;
         }

         // Register History Manager path update callback
         YAHOO.util.History.register("path", "", function(newPath)
         {
            if (this.expectedHistoryEvent)
            {
               // Clear the flag and update the DocList
               this.expectedHistoryEvent = false;
               this._updateDocList.call(this, (YAHOO.env.ua.gecko) ? decodeURIComponent(newPath) : newPath);
            }
            else
            {
               // Unexpected navigation - source event needs to be pathChanged event handler
               YAHOO.Bubbling.fire("pathChanged",
               {
                  path: newPath
               })
            }
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
            Alfresco.logger.debug("Alfresco.DocumentList: Couldn't initialize HistoryManager.", e.toString());
         }
         
         // Hide/Show Folders button
         Dom.get(this.id + "-showFolders-button").innerHTML = this._msg(this.options.showFolders ? "button.folders.hide" : "button.folders.show");
         this.widgets.showFolders = Alfresco.util.createYUIButton(this, "showFolders-button", this.onShowFolders);

         // Detailed/Simple List button
         Dom.get(this.id + "-detailedView-button").innerHTML = this._msg(this.options.detailedView ? "button.view.simple" : "button.view.detailed");
         this.widgets.detailedView =  Alfresco.util.createYUIButton(this, "detailedView-button", this.onDetailedView);

         // File Select menu button
         this.widgets.fileSelect = Alfresco.util.createYUIButton(this, "fileSelect-button", this.onFileSelect,
         {
            type: "menu", 
            menu: "fileSelect-menu"
         });

         // DataSource definition
         var uriDocList = Alfresco.constants.PROXY_URI + "slingshot/doclib/doclist/";
         this.widgets.dataSource = new YAHOO.util.DataSource(uriDocList);
         this.widgets.dataSource.responseType = YAHOO.util.DataSource.TYPE_JSON;
         this.widgets.dataSource.connXhrMode = "queueRequests";
         this.widgets.dataSource.responseSchema =
         {
            resultsList: "doclist.items",
            fields:
            [
               "index", "nodeRef", "type", "icon32", "fileName", "displayName", "status", "lockedBy", "lockedByUser", "title", "description",
               "createdOn", "createdBy", "createdByUser", "modifiedOn", "modifiedBy", "modifiedByUser", "version", "contentUrl", "actionSet"
            ]
         };
         
         /**
          * Custom field generator functions
          */

         /**
          * Generate "pathChanged" event onClick mark-up
          *
          * @method generatePathOnClick
          * @param path {string} New path to navigate to
          * @return {string} Mark-up for use in onClick attribute
          */
         var generatePathOnClick = function DL_generatePathOnClick(path)
         {
            return "YAHOO.Bubbling.fire('pathChanged', {path: '" + path.replace(/[']/g, "\\'") + "'}); return false;";
         }
         
         /**
          * Generate URL to thumbnail image
          *
          * @method generateThumbnailUrl
          * @param path {YAHOO.widget.Record} File record
          * @return {string} URL to thumbnail
          */
         var generateThumbnailUrl = function DL_generateThumbnailUrl(record)
         {
            var url = Alfresco.constants.PROXY_URI + "api/node/" + record.getData("nodeRef").replace(":/", "");
            url += "/content/thumbnails/doclib?qc=true&ph=true";
            return url;
         }

         /**
          * Generate URL to user profile page
          *
          * @method generateUserProfileUrl
          * @param userName {string} Username
          * @return {string} URL to profile page
          */
         var generateUserProfileUrl = function DL_generateUserProfileUrl(userName)
         {
            return Alfresco.util.uriTemplate("userpage",
            {
               userid: userName,
               pageid: "profile"
            });
         }
         
         /**
          * Generate URL to thumbnail image
          *
          * @method generateDocumentPreviewOnClick
          * @param path {YAHOO.widget.Record} File record
          * @return {string} a click handler to open the document preview component for the document
          */
         var generateDocumentPreviewOnClick = function DL_generateDocumentPreviewOnClick(record)
         {
            var nodeRef = record.getData("nodeRef");
            var fileName = record.getData("fileName").replace(/[']/g, "\\'");
            var icon32 = Alfresco.constants.URL_CONTEXT + record.getData("icon32");
            return "Alfresco.module.getDocumentPreviewInstance().show({nodeRef: '" + nodeRef + "', fileName: '" + fileName + "', icon32: '" + icon32 + "'});";
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
         var renderCellSelected = function DL_renderCellSelected(elCell, oRecord, oColumn, oData)
         {
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

            elCell.innerHTML = '<input id="checkbox-' + oRecord.getId() + '" type="checkbox" name="fileChecked" value="'+ oData + '"' + (me.selectedFiles[oData] ? ' checked="checked">' : '>');
         }
          
         /**
          * Status custom datacell formatter
          *
          * @method renderCellStatus
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         var renderCellStatus = function DL_renderCellStatus(elCell, oRecord, oColumn, oData)
         {
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

            var status = oRecord.getData("actionSet");
            var lockType = "";
            switch (status)
            {
               case "locked":
                  lockType = "locked";
                  break;
               
               case "workingCopyOwner":
                  lockType = "editing";
                  break;
                  
               case "lockOwner":
                  lockType = "lock-owner";
                  break;
            }
            if (lockType != "")
            {
               elCell.innerHTML = '<span class="locked-by"><img src="' + Alfresco.constants.URL_CONTEXT + 'components/documentlibrary/images/' + lockType + '.gif" alt="' + lockType + '" /></span>'
            }
            else
            {
               elCell.innerHTML = '';
            }
         }
          
         /**
          * Thumbnail custom datacell formatter
          *
          * @method renderCellThumbnail
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         var renderCellThumbnail = function DL_renderCellThumbnail(elCell, oRecord, oColumn, oData)
         {
            var name = oRecord.getData("fileName");
            var extn = name.substring(name.lastIndexOf("."));

            if (me.options.detailedView)
            {
               oColumn.width = 100;
               Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

               if (oRecord.getData("type") == "folder")
               {
                  elCell.innerHTML = '<a href="" onclick="' + generatePathOnClick(me.currentPath + "/" + name) + '"><span class="folder"></span></a>';
               }
               else
               {
                  elCell.innerHTML = '<span onclick="' + generateDocumentPreviewOnClick(oRecord) + '" class="thumbnail"><img src="' + generateThumbnailUrl(oRecord) + '" alt="' + extn + '" /></span>';
               }
            }
            else
            {
               oColumn.width = 40;
               Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

               if (oRecord.getData("type") == "folder")
               {
                  elCell.innerHTML = '<a href="" onclick="' + generatePathOnClick(me.currentPath + "/" + name) + '"><span class="folder-small"></span></a>';
               }
               else
               {
                  elCell.innerHTML = '<span onclick="' + generateDocumentPreviewOnClick(oRecord) + '" class="demo-other-small"><img src="' + Alfresco.constants.URL_CONTEXT + oRecord.getData("icon32").substring(1) + '" alt="' + extn + '" /></span>';
               }
            }
         }

         /**
          * Description/detail custom datacell formatter
          *
          * @method renderCellDescription
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         var renderCellDescription = function DL_renderCellDescription(elCell, oRecord, oColumn, oData)
         {
            var desc = "";
            if (oRecord.getData("type") == "folder")
            {
               /**
                * Folder type
                */
               desc = '<h3 class="filename"><a href="" onclick="' + generatePathOnClick(me.currentPath + "/" + oRecord.getData("fileName")) + '">';
               desc += '<b>' + oRecord.getData("displayName") + '</b></a></h3>';

               if (me.options.detailedView)
               {
                  /* Inline Rename
                  desc += '<div id="' + me.id + '-rename-' + oRecord.getId() + '" class="rename-file hidden">' + me._msg("actions.folder.rename") + '</div>';
                  */
                  desc += '<div class="detail"><span><b>' + me._msg("details.created-on") + '</b> ' + Alfresco.util.formatDate(oRecord.getData("createdOn")) + '</span>';
                  if (oRecord.getData("description").length > 0)
                  {
                     desc += '<div class="detail"><span><b>' + me._msg("details.description") + '</b> ' + oRecord.getData("description") + '</span></div>';
                  }
                  else
                  {
                     desc += '<div class="detail">&nbsp;</div>';
                  }
                  desc += '<div class="detail">&nbsp;</div>';
               }
               else
               {
                  desc += '<div class="detail">' + Alfresco.util.formatDate(oRecord.getData("createdOn"), "dd mmmm yyyy") + '</div>';
               }
            }
            else
            {
               /**
                * Document type
                */
               desc = '<h3 class="filename"><a target="content" href="' + Alfresco.constants.PROXY_URI + oRecord.getData("contentUrl") + '">' + oRecord.getData("displayName") + '</a></h3>';
               if (me.options.detailedView)
               {
                  /* Inline Rename
                  desc += '<div id="' + me.id + '-rename-' + oRecord.getId() + '" class="rename-file hidden">' + me._msg("actions.document.rename") + '</div>';
                  */
                  desc += '<div class="detail"><span><b>' + me._msg("details.modified-on") + '</b> ' + Alfresco.util.formatDate(oRecord.getData("modifiedOn")) + '</span>';
                  desc += '<span><b>' + me._msg("details.modified-by") + '</b> <a href="' + generateUserProfileUrl(oRecord.getData("modifiedByUser")) + '">' + oRecord.getData("modifiedBy") + '</a></span>';
                  desc += '<span><b>' + me._msg("details.version") + '</b> ' + oRecord.getData("version") + '</span></div>';
                  /* Created On field
                  desc += '<div class="detail"><span><b>' + me._msg("details.created-on") + '</b> ' + Alfresco.util.formatDate(oRecord.getData("createdOn")) + '</span>';
                  desc += '<span><b>' + me._msg("details.created-by") + '</b> ' + oRecord.getData("createdBy") + '</span>';
                  */
                  desc += '<div class="detail"><span><b>' + me._msg("details.description") + '</b> ' + oRecord.getData("description") + '</span></div>';
                  desc += '<div class="detail"><span><b>' + me._msg("details.comments") + '</b> 0</span></div>';
               }
               else
               {
                  desc += '<div class="detail">' + Alfresco.util.formatDate(oRecord.getData("modifiedOn"), "dd mmmm yyyy") + '</div>';
               }
            }
            elCell.innerHTML = desc;
         }

         /**
          * Actions custom datacell formatter
          *
          * @method renderCellActions
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         var renderCellActions = function DL_renderCellActions(elCell, oRecord, oColumn, oData)
         {
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");
            Dom.setStyle(elCell.parentNode, "border-left", "3px solid #fff");

            elCell.innerHTML = '<div id="' + me.id + '-actions-' + oRecord.getId() + '" class="hidden"></div>';
         }


         // DataTable column defintions
         var columnDefinitions = [
         {
            key: "nodeRef", label: "Select", sortable: false, formatter: renderCellSelected, width: 16
         },
         {
            key: "status", label: "Status", sortable: false, formatter: renderCellStatus, width: 16
         },
         {
            key: "thumbnail", label: "Preview", sortable: false, formatter: renderCellThumbnail, width: 100
         },
         {
            key: "fileName", label: "Description", sortable: false, formatter: renderCellDescription
         },
         {
            key: "actions", label: "Actions", sortable: false, formatter: renderCellActions, width: 160
         }];

         // Temporary "empty datatable" message
         YAHOO.widget.DataTable.MSG_EMPTY = this._msg("message.loading");

         // DataTable definition
         this.widgets.dataTable = new YAHOO.widget.DataTable(this.id + "-documents", columnDefinitions, this.widgets.dataSource,
         {
            renderLoopSize: 32,
            initialLoad: false
         });
         
         // Custom error messages
         this._setDefaultDataTableErrors();

         // Hook tableMsgShowEvent to clear out fixed-pixel width on <table> element (breaks resizer)
         this.widgets.dataTable.subscribe("tableMsgShowEvent", function(oArgs)
         {
            // NOTE: Scope needs to be DataTable
            this._elMsgTbody.parentNode.style.width = "";
         });
         
         // Override abstract function within DataTable to set custom error message
         this.widgets.dataTable.doBeforeLoadData = function DL_doBeforeLoadData(sRequest, oResponse, oPayload)
         {
            if (oResponse.error)
            {
               try
               {
                  var response = YAHOO.lang.JSON.parse(oResponse.responseText);
                  YAHOO.widget.DataTable.MSG_ERROR = response.message;
               }
               catch(e)
               {
                  me._setDefaultDataTableErrors();
               }
            }
            else if (oResponse.results)
            {
               this.renderLoopSize = oResponse.results.length >> (YAHOO.env.ua.gecko) ? 3 : 5;
            }
            // Must return true to have the "Loading..." message replaced by the error message
            return true;
         }

         // File checked handler
         this.widgets.dataTable.subscribe("checkboxClickEvent", function(e)
         { 
            var id = e.target.value; 
            this.selectedFiles[id] = e.target.checked;
            YAHOO.Bubbling.fire("selectedFilesChanged");
         }, this, true);
         
         // Rendering complete event handler
         this.widgets.dataTable.subscribe("initEvent", function()
         {
            if (this.options.highlightFile)
            {
               YAHOO.Bubbling.fire("highlightFile",
               {
                  fileName: this.options.highlightFile
               });
            }
         }, this, true);
         
         // Enable row highlighting
         this.widgets.dataTable.subscribe("rowMouseoverEvent", this.onEventHighlightRow, this, true);
         this.widgets.dataTable.subscribe("rowMouseoutEvent", this.onEventUnhighlightRow, this, true);
         
         // Fire pathChanged event for first-time population
         YAHOO.Bubbling.fire("pathChanged",
         {
            doclistInitialNav: true,
            path: this.currentPath
         });
         
         // Set the default view filter to be "path" and the owner to be "Alfresco.DocListTree"
         var filterObj = YAHOO.lang.merge(
         {
            filterId: "path",
            filterOwner: "Alfresco.DocListTree"
         }, this.options.initialFilter);

         YAHOO.Bubbling.fire("filterChanged", filterObj);

         // Hook action events
         var fnActionHandler = function DL_filterAction(layer, args)
         {
            var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "div");
            if (owner !== null)
            {
               var action = owner.className;
               var target = args[1].target;
               if (typeof me[action] == "function")
               {
                  me[action].call(me, target.offsetParent, owner);
                  args[1].stop = true;
               }
            }
      		 
            return true;
         }
         YAHOO.Bubbling.addDefaultAction("action-link", fnActionHandler);
         YAHOO.Bubbling.addDefaultAction("show-more", fnActionHandler);
         
         // DocLib Actions module
         this.modules.actions = new Alfresco.module.DoclibActions();

         // Finally show the component body here to prevent UI artifacts on YUI button decoration
         Dom.setStyle(this.id + "-body", "visibility", "visible");
      },
      
      /**
       * Public functions
       *
       * Functions designed to be called form external sources
       */

      /**
       * Public function to get array of selected files
       *
       * @method getSelectedFiles
       * @return {Array} Currently selected files
       */
      getSelectedFiles: function DL_getSelectedFiles()
      {
         var files = [];
         var recordSet = this.widgets.dataTable.getRecordSet();
         var record;
         
         for (var i = 0, j = recordSet.getLength(); i < j; i++)
         {
            record = recordSet.getRecord(i);
            if (this.selectedFiles[record.getData("nodeRef")])
            {
               files.push(record._oData);
            }
         }
         
         return files;
      },
      
      /**
       * Public function to select files by specified groups
       *
       * @method selectFiles
       * @param p_selectType {string} Can be one of the following:
       * <pre>
       * selectAll - all documents and folders
       * selectNone - deselect all
       * selectInvert - invert selection
       * selectDocuments - select all documents
       * selectFolders - select all folders
       * </pre>
       */
      selectFiles: function DL_selectFiles(p_selectType)
      {
         var recordSet = this.widgets.dataTable.getRecordSet();
         var record;
         var checks = YAHOO.util.Selector.query('input[type="checkbox"]', this.widgets.dataTable.getTbodyEl());
         var len = checks.length; 

         switch (p_selectType)
         {
            case "selectAll":
               for (var i = 0; i < len; i++)
               {
                  record = recordSet.getRecord(i);
                  this.selectedFiles[record.getData("nodeRef")] = checks[i].checked = true;
               }
               break;
            
            case "selectNone":
               for (var i = 0; i < len; i++)
               {
                  record = recordSet.getRecord(i);
                  this.selectedFiles[record.getData("nodeRef")] = checks[i].checked = false;
               }
               break;

            case "selectInvert":
               for (var i = 0; i < len; i++)
               {
                  record = recordSet.getRecord(i);
                  this.selectedFiles[record.getData("nodeRef")] = checks[i].checked = !checks[i].checked;
               }
               break;

            case "selectDocuments":
               for (var i = 0; i < len; i++)
               {
                  record = recordSet.getRecord(i);
                  this.selectedFiles[record.getData("nodeRef")] = checks[i].checked = record.getData("type") == "document";
               }
               break;

            case "selectFolders":
               for (var i = 0; i < len; i++)
               {
                  record = recordSet.getRecord(i);
                  this.selectedFiles[record.getData("nodeRef")] = checks[i].checked = record.getData("type") == "folder";
               }
               break;
         }
         
         YAHOO.Bubbling.fire("selectedFilesChanged");
      },

      /**
       * Public function to get URL to RSS feed for current view
       *
       * @method getRSSFeedUrl
       * @return {string} URL to RSS feed for current document list view
       */
      getRSSFeedUrl: function DL_getRSSFeedUrl()
      {
         var url = Alfresco.constants.PROXY_URI + "slingshot/doclib/doclist/";
         url += this._buildDocListRSSParams(this.currentPath);
         return url;
      },
      

      /**
       * YUI WIDGET EVENT HANDLERS
       * Handlers for standard events fired from YUI widgets, e.g. "click"
       */

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
         p_obj.set("label", this._msg(this.options.showFolders ? "button.folders.hide" : "button.folders.show"));

         YAHOO.Bubbling.fire("doclistRefresh");
         Event.preventDefault(e);
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
         p_obj.set("label", this._msg(this.options.detailedView ? "button.view.simple" : "button.view.detailed"));

         YAHOO.Bubbling.fire("doclistRefresh");
         Event.preventDefault(e);
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

         // Get the className of the clicked item
         var action = Alfresco.util.findEventClass(eventTarget);

         this.selectFiles(action);

         Event.preventDefault(domEvent);
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
         // Drop out if More Actions pop-up active
         if (this.showingMoreActions)
         {
            this.deferHighlightRow = oArgs;
            return;
         }
         
         var target = oArgs.target;
         // elRename is the element id of the rename file link
         var elRename = Dom.get(this.id + "-rename-" + target.yuiRecordId);
         // elActions is the element id of the active table cell where we'll inject the actual links
         var elActions = Dom.get(this.id + "-actions-" + target.yuiRecordId);

         // Inject the correct action elements into the actionsId element
         if (elActions.firstChild === null)
         {
            // Retrieve the actionSet for this asset
            var record = this.widgets.dataTable.getRecord(target);
            var actionSet = record.getData("actionSet");
            // Now clone the actionSet template node from the DOM
            var clone = Dom.get(this.id + "-actionSet-" + actionSet).cloneNode(true);
            // Token replacement
            clone.innerHTML = YAHOO.lang.substitute(unescape(clone.innerHTML),
            {
               downloadUrl: Alfresco.constants.PROXY_URI + record.getData("contentUrl") + "?a=true"
            });
            clone.id = elActions.id + "_a";
            Dom.addClass(clone, this.options.detailedView ? "detailed" : "simple");
            elActions.appendChild(clone);
         }
         // Show the actions
         Dom.removeClass(elRename, "hidden");
         Dom.removeClass(elActions, "hidden");
         
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
         // Drop out if More Actions pop-up active
         if (this.showingMoreActions)
         {
            this.deferUnhighlightRow = oArgs;
            return;
         }
         
         var target = oArgs.target;
         var renameId = this.id + "-rename-" + target.yuiRecordId;
         var actionsId = this.id + "-actions-" + target.yuiRecordId;

         // Just hide the action links
         Dom.addClass(renameId, "hidden");
         Dom.addClass(actionsId, "hidden");
         
         // Call through to get the row unhighlighted by YUI
         this.widgets.dataTable.onEventUnhighlightRow.call(this.widgets.dataTable, oArgs);
      },

      /**
       * BUBBLING LIBRARY EVENT HANDLERS FOR ACTIONS
       * Disconnected event handlers for action event notification
       */

      /**
       * Show more actions pop-up.
       *
       * @method onActionShowMore
       * @param row {object} DataTable row representing file to be actioned
       * @param elMore {element} DOM Element of "More Actions" link
       */
      onActionShowMore: function DL_onActionShowMore(row, elMore)
      {
         var me = this;
         
         var elMoreActions = Dom.getNextSibling(elMore);
         Dom.removeClass(elMoreActions, "hidden");
         me.showingMoreActions = true;
         
         // Mouse over handler
         var onMouseOver = function DLSM_onMouseOver(e, obj)
         {
            // Clear any existing hide timer
            if (obj.hideTimerId)
            {
               clearTimeout(elMoreActions.hideTimerId);
               elMoreActions.hideTimerId = null;
            }
         }
         
         // Mouse out handler
         var onMouseOut = function DLSM_onMouseOut(e, obj)
         {
            var elTarget = Event.getTarget(e);
            var elTag = elTarget.nodeName.toLowerCase();
            var related = e.relatedTarget;

            // In some cases we should ignore this mouseout event
            if ((related != obj) && (!Dom.isAncestor(obj, related)))
            {
               if (obj.hideTimerId)
               {
                  clearTimeout(obj.hideTimerId);
               }
               obj.hideTimerId = setTimeout(function()
               {
                  Event.removeListener(obj, "mouseover");
                  Event.removeListener(obj, "mouseout");
                  Dom.addClass(obj, "hidden");
                  me.showingMoreActions = false;
                  // Did we defer highlight or unhighlight events?
                  var high = me.deferHighlightRow;
                  var unhigh = me.deferUnhighlightRow;
                  if (unhigh)
                  {
                     if (!high || (high && high.target != unhigh.target))
                     {
                        me.onEventUnhighlightRow.call(me, unhigh);
                     }
                     me.deferUnhighlightRow = null;
                  }
                  if (high)
                  {
                     me.onEventHighlightRow.call(me, high);
                     me.deferHighlightRow = null;
                  }
               }, me.options.actionsPopupTimeout);
            }
         }
         
         Event.on(elMoreActions, "mouseover", onMouseOver, elMoreActions);
         Event.on(elMoreActions, "mouseout", onMouseOut, elMoreActions);
      },
      
      /**
       * Asset details.
       *
       * @method onActionDetails
       * @param row {object} DataTable row representing file to be actioned
       */
      onActionDetails: function DL_onActionDetails(row)
      {
         /**
          * TODO
          */
         return;
         
         var me = this;
         var record = this.widgets.dataTable.getRecord(row);

         var actionUrl = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "api/metadata/node/{nodeRef}",
         {
            nodeRef: record.getData("nodeRef").replace(":/", "")
         });
         
         var doSetupFormsValidation = function DLTB_oNF_doSetupFormsValidation(p_form)
         {
            // Validation
            // Name: mandatory value
            p_form.addValidation(this.id + "-details-name", Alfresco.forms.validation.mandatory, null, "keyup");
            // Name: valid filename
            p_form.addValidation(this.id + "-details-name", Alfresco.forms.validation.nodeName, null, "keyup");
            p_form.setShowSubmitStateDynamically(true, false);
         }
         
         if (!this.modules.details)
         {
            this.modules.details = new Alfresco.module.SimpleDialog(this.id + "-details").setOptions(
            {
               width: "30em",
               templateUrl: Alfresco.constants.URL_SERVICECONTEXT + "modules/documentlibrary/details",
               actionUrl: actionUrl,
               doSetupFormsValidation:
               {
                  fn: doSetupFormsValidation,
                  scope: this
               },
               firstFocus: this.id + "-details-name",
               onSuccess:
               {
                  fn: function DLTB_onActionDetails_callback(response)
                  {
                  },
                  scope: this
               }
            });
         }
         else
         {
            this.modules.details.setOptions(
            {
               actionUrl: actionUrl
            })
         }
         this.modules.details.show();
      },

      /**
       * Delete Asset.
       *
       * @method onActionDelete
       * @param row {object} DataTable row representing file to be actioned
       */
      onActionDelete: function DL_onActionDelete(row)
      {
         var me = this;
         var record = this.widgets.dataTable.getRecord(row);
         Alfresco.util.PopupManager.displayPrompt(
         {
            text: this._msg("message.confirm.delete", record.getData("fileName")),
            buttons: [
            {
               text: this._msg("button.delete"),
               handler: function DL_onActionDelete_delete()
               {
                  this.destroy();
                  me._onActionDeleteConfirm.call(me, record);
               },
               isDefault: true
            },
            {
               text: this._msg("button.cancel"),
               handler: function DL_onActionDelete_cancel()
               {
                  this.destroy();
               }
            }]
         });
      },

      /**
       * Delete Asset confirmed.
       *
       * @method _onActionDeleteConfirm
       * @param record {object} DataTable record representing file to be actioned
       * @private
       */
      _onActionDeleteConfirm: function DL__onActionDeleteConfirm(record)
      {
         var fileType = record.getData("type");
         var fileName = record.getData("fileName");
         var filePath = this.currentPath + "/" + fileName;
         var displayName = record.getData("displayName");
         
         this.modules.actions.genericAction(
         {
            success:
            {
               event:
               {
                  name: fileType == "folder" ? "folderDeleted" : "fileDeleted",
                  obj:
                  {
                     path: filePath
                  }
               },
               message: this._msg("message.delete.success", displayName)
            },
            failure:
            {
               message: this._msg("message.delete.failure", displayName)
            },
            webscript:
            {
               name: "file",
               method: Alfresco.util.Ajax.DELETE
            },
            params:
            {
               siteId: this.options.siteId,
               containerId: this.options.containerId,
               path: this.currentPath,
               file: fileName
            }
         });
      },

      /**
       * Edit Offline.
       *
       * @method onActionEditOffline
       * @param row {object} DataTable row representing file to be actioned
       */
      onActionEditOffline: function DL_onActionEditOffline(row)
      {
         var record = this.widgets.dataTable.getRecord(row);
         var fileName = record.getData("fileName");
         var displayName = record.getData("displayName");

         this.modules.actions.genericAction(
         {
            success:
            {
               event:
               {
                  name: "filterChanged",
                  obj:
                  {
                     filterId: "editingMe",
                     filterOwner: "Alfresco.DocListFilter"
                  }
               },
               message: this._msg("message.edit-offline.success", displayName),
               callback:
               {
                  fn: function DL_oAEO_success(data)
                  {
                     window.location = Alfresco.constants.PROXY_URI + data.json.results[0].downloadUrl;
                  }
               }
            },
            failure:
            {
               message: this._msg("message.edit-offline.failure", displayName)
            },
            webscript:
            {
               name: "checkout",
               method: Alfresco.util.Ajax.POST
            },
            params:
            {
               siteId: this.options.siteId,
               containerId: this.options.containerId,
               path: this.currentPath,
               file: fileName
            }
         });
      },

      /**
       * Upload new version.
       *
       * @method onActionUploadNewVersion
       * @param row {object} DataTable row representing file to be actioned
       */
      onActionUploadNewVersion: function DL_onActionUploadNewVersion(row)
      {
         var record = this.widgets.dataTable.getRecord(row);
         var fileName = record.getData("fileName");
         var nodeRef = record.getData("nodeRef");

         if (this.fileUpload === null)
         {
            this.fileUpload = Alfresco.module.getFileUploadInstance();
            // this.fileUpload = new Alfresco.module.FileUpload(this.id + "-fileUpload");
         }

         // Show uploader for multiple files
         var description = this._msg("label.filter-description", record.getData("displayName"));
         var extensions = "*" + fileName.substring(fileName.lastIndexOf("."));
         var singleUpdateConfig =
         {
            siteId: this.options.siteId,
            containerId: this.options.containerId,
            updateNodeRef: nodeRef,
            updateFilename: fileName,
            filter: [
            {
               description: description,
               extensions: extensions
            }],
            mode: this.fileUpload.MODE_SINGLE_UPDATE,
            onFileUploadComplete:
            {
               fn: this.onNewVersionUploadComplete,
               scope: this
            }
         }
         this.fileUpload.show(singleUpdateConfig);
         Event.preventDefault(e);
      },

      /**
       * Called from the uploader component after a the new version has been uploaded.
       *
       * @method onNewVersionUploadComplete
       * @param complete {object} Object literal containing details of successful and failed uploads
       */
      onNewVersionUploadComplete: function DL_onNewVersionUploadComplete(complete)
      {
         // Do something after the new version is uploaded
      },

      /**
       * Cancel editing.
       *
       * @method onActionCancelEditing
       * @param row {object} DataTable row representing file to be actioned
       */
      onActionCancelEditing: function DL_onActionCancelEditing(row)
      {
         var record = this.widgets.dataTable.getRecord(row);
         var fileName = record.getData("fileName");
         var displayName = record.getData("displayName");
         var nodeRef = record.getData("nodeRef");

         this.modules.actions.genericAction(
         {
            success:
            {
               event:
               {
                  name: "doclistRefresh"
               },
               message: this._msg("message.edit-cancel.success", displayName)
            },
            failure:
            {
               message: this._msg("message.edit-cancel.failure", displayName)
            },
            webscript:
            {
               name: "cancel-checkout",
               method: Alfresco.util.Ajax.POST
            },
            params:
            {
               nodeRef: nodeRef
            }
         });
      },
      
      /**
       * Copy single document or folder.
       *
       * @method onActionCopyTo
       * @param row {object} DataTable row representing file to be actioned
       */
      onActionCopyTo: function DL_onActionCopyTo(row)
      {
         var parentFolder = (this.currentPath[0] == "/") ? this.currentPath.substring(1) : this.currentPath;
         var file = this.widgets.dataTable.getRecord(row)._oData;
         
         if (!this.modules.copyTo)
         {
            this.modules.copyTo = new Alfresco.module.DoclibCopyTo(this.id + "-copyTo").setOptions(
            {
               siteId: this.options.siteId,
               containerId: this.options.containerId,
               path: this.currentPath,
               files: file
            });
         }
         else
         {
            this.modules.copyTo.setOptions(
            {
               path: this.currentPath,
               files: file
            })
         }
         this.modules.copyTo.showDialog();
      },

      /**
       * Move single document or folder.
       *
       * @method onActionMoveTo
       * @param row {object} DataTable row representing file to be actioned
       */
      onActionMoveTo: function DL_onActionMoveTo(row)
      {
         var parentFolder = (this.currentPath[0] == "/") ? this.currentPath.substring(1) : this.currentPath;
         var file = this.widgets.dataTable.getRecord(row)._oData;
         
         if (!this.modules.moveTo)
         {
            this.modules.moveTo = new Alfresco.module.DoclibMoveTo(this.id + "-moveTo").setOptions(
            {
               siteId: this.options.siteId,
               containerId: this.options.containerId,
               path: this.currentPath,
               files: file,
               width: "40em"
            });
         }
         else
         {
            this.modules.moveTo.setOptions(
            {
               path: this.currentPath,
               files: file
            })
         }
         this.modules.moveTo.showDialog();
      },


      /**
       * BUBBLING LIBRARY EVENT HANDLERS FOR PAGE EVENTS
       * Disconnected event handlers for inter-component event notification
       */

      /**
       * Path Changed event handler
       *
       * @method onPathChanged
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onPathChanged: function DL_onPathChanged(layer, args)
      {
         var obj = args[1];
         if (obj !== null)
         {
            // Should be a path in the arguments
            if (obj.path !== null)
            {
               var hashPath = YAHOO.util.History.getBookmarkedState("path");
               hashPath = (YAHOO.env.ua.gecko) ? decodeURIComponent(hashPath) : hashPath;
               
               if ((obj.doclistInitialNav) || (obj.path == hashPath))
               {
                  // HistoryManager won't fire for the initial navigation event, or if the path hasn't changed
                  this._updateDocList.call(this, obj.path);
               }
               else
               {
                  try
                  {
                     // Flag to indicate we're expecting the HistoryManager's event
                     this.expectedHistoryEvent = true;

                     // Update History Manager with new path. It will callback to update the doclist
                     YAHOO.util.History.navigate("path", (YAHOO.env.ua.gecko) ? encodeURIComponent(obj.path) : obj.path);
                  }
                  catch (e)
                  {
                     // Fallback for non-supported browsers, or hidden iframe loading delay
                     this._updateDocList.call(this, obj.path);
                  }
               }
            }
         }
      },
      
      /**
       * DocList Refresh Required event handler
       *
       * @method onDocListRefresh
       * @param layer {object} Event fired (unused)
       * @param args {array} Event parameters (unused)
       */
      onDocListRefresh: function DL_onDocListRefresh(layer, args)
      {
         this._updateDocList.call(this, this.currentPath);
      },

      /**
       * DocList View Filter changed event handler
       *
       * @method onFilterChanged
       * @param layer {object} Event fired (unused)
       * @param args {array} Event parameters (new filterId)
       */
      onFilterChanged: function DL_onFilterChanged(layer, args)
      {
         var obj = args[1];
         if ((obj !== null) && (obj.filterId !== null))
         {
            // Should be a filterId in the arguments
            this.currentFilterId = obj.filterId;
            // Ignore if it's the path, as we'll update on the pathChanged event
            if (obj.filterId != "path")
            {
               this._updateDocList.call(this, this.currentPath);
            }
         }
      },

      /**
       * Highlight file event handler
       * Used when a component (including the DocList itself on loading) wants to scroll to and hightlight a file
       *
       * @method onHighlightFile
       * @param layer {object} Event fired (unused)
       * @param args {array} Event parameters (filename to be highlighted)
       */
      onHighlightFile: function DL_onHighlightFile(layer, args)
      {
         var obj = args[1];
         if ((obj !== null) && (obj.fileName !== null))
         {
            var recordSet = this.widgets.dataTable.getRecordSet();
            var recordFound = null;
            for (var i = 0, j = recordSet.getLength(); i < j; i++)
            {
               if (recordSet.getRecord(i).getData("fileName") == obj.fileName)
               {
                  recordFound = recordSet.getRecord(i);
                  break;
               }
            }
            if (recordFound !== null)
            {
               // Scroll the record into view and highlight it
               var el = this.widgets.dataTable.getTrEl(recordFound);
               var yPos = Dom.getY(el);
               window.scrollTo(0, yPos);
               Alfresco.util.Anim.pulse(el);
               this.options.highlightFile = null;

               // Select the file
               Dom.get("checkbox-" + recordFound.getId()).checked = true;
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
      onDeactivateAllControls: function DL_onDeactivateAllControls(layer, args)
      {
         for (widget in this.widgets)
         {
            this.widgets[widget].set("disabled", true);
         }
      },

   
      /**
       * PRIVATE FUNCTIONS
       */

      /**
       * Gets a custom message
       *
       * @method _msg
       * @param messageId {string} The messageId to retrieve
       * @return {string} The custom message
       * @private
       */
      _msg: function DL__msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, "Alfresco.DocumentList", Array.prototype.slice.call(arguments).slice(1));
      },
      
      /**
       * Resets the YUI DataTable errors to our custom messages
       * NOTE: Scope could be YAHOO.widget.DataTable, so can't use "this"
       *
       * @method _setDefaultDataTableErrors
       */
      _setDefaultDataTableErrors: function DL__setDefaultDataTableErrors()
      {
         var msg = Alfresco.util.message;
         YAHOO.widget.DataTable.MSG_EMPTY = msg("message.empty", "Alfresco.DocumentList");
         YAHOO.widget.DataTable.MSG_ERROR = msg("message.error", "Alfresco.DocumentList");
      },
      
      /**
       * Updates document list by calling data webscript with current site and path
       *
       * @method _updateDocList
       * @param path {string} Path to navigate to
       */
      _updateDocList: function DL__updateDocList(path)
      {
         // Reset the custom error messages
         this._setDefaultDataTableErrors();
         
         function successHandler(sRequest, oResponse, oPayload)
         {
            this.currentPath = path;
            this.widgets.dataTable.onDataReturnInitializeTable.call(this.widgets.dataTable, sRequest, oResponse, oPayload);
         }
         
         function failureHandler(sRequest, oResponse)
         {
            if (oResponse.status == 401)
            {
               // Our session has likely timed-out, so refresh to offer the login page
               window.location.reload(true);
            }
            else
            {
               try
               {
                  var response = YAHOO.lang.JSON.parse(oResponse.responseText);
                  YAHOO.widget.DataTable.MSG_ERROR = response.message;
                  this.widgets.dataTable.showTableMessage(response.message, YAHOO.widget.DataTable.CLASS_ERROR);
                  if (oResponse.status == 404)
                  {
                     // Site or container not found - deactivate controls
                     YAHOO.Bubbling.fire("deactivateAllControls");
                  }
               }
               catch(e)
               {
                  this._setDefaultDataTableErrors();
               }
            }
         }
         
         this.widgets.dataSource.sendRequest(this._buildDocListParams(path),
         {
               success: successHandler,
               failure: failureHandler,
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
          var params = YAHOO.lang.substitute("{type}/site/{site}/{container}{path}",
          {
             type: this.options.showFolders ? "all" : "documents",
             site: encodeURIComponent(this.options.siteId),
             container: encodeURIComponent(this.options.containerId),
             path: encodeURI(path)
          });

          params += "?filter=" + encodeURIComponent(this.currentFilterId);
          return params;
       },
       
       /**
        * Build URI parameter string for doclist RSS data webscript
        *
        * @method _buildDocListRSSParams
        * @param path {string} Path to query
        */
        _buildDocListRSSParams: function DL__buildDocListRSSParams(path)
        {
           var params = this._buildDocListParams(path);
           params += "&format=rss";
           return params;
        }
   };
})();
