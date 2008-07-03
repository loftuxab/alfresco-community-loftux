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
      Alfresco.util.YUILoaderHelper.require(["button", "menu", "container", "datasource", "datatable", "json", "history"], this.onComponentsLoaded, this);
      
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
         actionsPopupTimeout: 500
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
            Event = YAHOO.util.Event,
            Element = YAHOO.util.Element,
            History = YAHOO.util.History;

         // Reference to self used by inline functions
         var me = this;
         
         // Decoupled event listeners
         YAHOO.Bubbling.on("pathChanged", this.onPathChanged, this);
         YAHOO.Bubbling.on("doclistRefresh", this.onDoclistRefresh, this);
         YAHOO.Bubbling.on("fileDeleted", this.onDoclistRefresh, this);
         YAHOO.Bubbling.on("folderCreated", this.onDoclistRefresh, this);
         YAHOO.Bubbling.on("folderDeleted", this.onDoclistRefresh, this);
         YAHOO.Bubbling.on("filterChanged", this.onFilterChanged, this);
      
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
         var uriDoclist = Alfresco.constants.PROXY_URI + "slingshot/doclib/doclist?";
         this.widgets.dataSource = new YAHOO.util.DataSource(uriDoclist);
         this.widgets.dataSource.responseType = YAHOO.util.DataSource.TYPE_JSON;
         this.widgets.dataSource.connXhrMode = "queueRequests";
         this.widgets.dataSource.responseSchema =
         {
             resultsList: "doclist.items",
             fields: ["index", "nodeRef", "type", "icon32", "name", "status", "lockedBy", "title", "description", "createdOn", "createdBy", "modifiedOn", "modifiedBy", "version", "contentUrl", "actionSet"]
         };
         
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
          * Status custom datacell formatter
          *
          * @method renderCellStatus
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         renderCellStatus = function DL_renderCellStatus(elCell, oRecord, oColumn, oData)
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

               if (".docx.xlsx.pptx".indexOf(extn) != -1)
               {
                  elCell.innerHTML = '<span class="demo-thumbnail"></span>';
               }
               else if (oRecord.getData("type") == "folder")
               {
                  var newPath = me.currentPath + "/" + oRecord.getData("name");
                  // TODO: *** Update the onclick to be logically-bound, not via HTML
                  elCell.innerHTML = '<a href="" onclick="YAHOO.Bubbling.fire(\'pathChanged\', {path: \'' + newPath.replace(/[']/g, "\\'") + '\'}); return false;"><span class="demo-folder"></span></a>';
               }
               else
               {
                  elCell.innerHTML = '<span class="demo-other"><img src="' + Alfresco.constants.URL_CONTEXT + oRecord.getData("icon32").substring(1) + '" alt="' + extn + '" /></span>';
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
                  elCell.innerHTML = '<a href="" onclick="YAHOO.Bubbling.fire(\'pathChanged\', {path: \'' + newPath.replace(/[']/g, "\\'") + '\'}); return false;"><span class="demo-folder-small"></span></a>';
               }
               else
               {
                  elCell.innerHTML = '<span class="demo-other-small"><img src="' + Alfresco.constants.URL_CONTEXT + oRecord.getData("icon32").substring(1) + '" alt="' + extn + '" /></span>';
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
               desc = '<h3 class="filename"><a href="" onclick="YAHOO.Bubbling.fire(\'pathChanged\', {path: \'' + newPath.replace(/[']/g, "\\'") + '\'}); return false;"><b>' + oRecord.getData("name") + '</b></a></h3>';

               if (me.options.detailedView)
               {
                  desc += '<div id="' + me.id + '-rename-' + oRecord.getId() + '" class="rename-file hidden">' + me._msg("actions.folder.rename") + '</div>';
                  if (oRecord.getData("description").length > 0)
                  {
                     desc += '<div class="detail"><span><b>Description:</b> ' + oRecord.getData("description") + '</span></div>';
                  }
               }
               else
               {
                  desc += '<div class="detail">' + Alfresco.util.formatDate(oRecord.getData("modifiedOn"), "dd mmmm yyyy") + '</div>';
               }
            }
            else
            {
               desc = '<h3 class="filename"><a target="content" href="' + Alfresco.constants.PROXY_URI + oRecord.getData("contentUrl") + '">' + oRecord.getData("name") + '</a></h3>';
               if (me.options.detailedView)
               {
                  desc += '<div id="' + me.id + '-rename-' + oRecord.getId() + '" class="rename-file hidden">' + me._msg("actions.document.rename") + '</div>';
                  desc += '<div class="detail"><span><b>Modified on:</b> ' + Alfresco.util.formatDate(oRecord.getData("modifiedOn")) + '</span>';
                  desc += '<span><b>Modified by:</b> ' + oRecord.getData("modifiedBy") + '</span>';
                  desc += '<span><b>Version:</b> ' + oRecord.getData("version") + '</span></div>';
                  desc += '<div class="detail"><span><b>Created on:</b> ' + Alfresco.util.formatDate(oRecord.getData("createdOn")) + '</span>';
                  desc += '<span><b>Created by:</b> ' + oRecord.getData("createdBy") + '</span>';
                  desc += '<div class="detail"><span><b>Description:</b> ' + oRecord.getData("description") + '</span></div>';
                  desc += '<div class="detail"><span><b>Comments:</b> 0</span></div>';
               }
               else
               {
                  desc += '<div class="detail">' + Alfresco.util.formatDate(oRecord.getData("modifiedOn"), "dd mmmm yyyy") + '</div>';
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
            key: "status", label: "Status", sortable: false, formatter: renderCellStatus, width: 16
         },
         {
            key: "icon32", label: "Preview", sortable: false, formatter: renderCellThumbnail, width: 80
         },
         {
            key: "name", label: "Description", sortable: false, formatter: renderCellDescription
         },
         {
            key: "actions", label: "Actions", sortable: false, formatter: renderCellActions, width: 160
         }];

         // Temporary blank "empty datatable" message
         YAHOO.widget.DataTable.MSG_EMPTY = "";

         // DataTable definition
         this.widgets.dataTable = new YAHOO.widget.DataTable(this.id + "-documents", columnDefinitions, this.widgets.dataSource,
         {
            renderLoopSize: 8,
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
            // Must return true to have the "Loading..." message replaced by the error message
            return true;
         }

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

         // Finally show the component body here to prevent UI artifacts on YUI button decoration
         Dom.setStyle(this.id + "-body", "visibility", "visible");
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
         p_obj.set("label", this._msg(this.options.detailedView ? "button.view.simple" : "button.view.detailed"));

         YAHOO.Bubbling.fire("doclistRefresh");
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

         var checks = YAHOO.util.Selector.query('input[type="checkbox"]', this.widgets.dataTable.getTbodyEl());
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
         var Dom = YAHOO.util.Dom;
         
         var target = oArgs.target;
         // elRename is the element id of the rename file link
         var elRename = Dom.get(this.id + "-rename-" + target.yuiRecordId);
         // elActions is the element id of the active table cell where we'll inject the actual links
         var elActions = Dom.get(this.id + "-actions-" + target.yuiRecordId);

         // Inject the correct action elements into the actionsId element
         if (elActions.firstChild === null)
         {
            // Retrieve the actionType - currently keyed off folder or file type.
            // TODO (M): Data webscript to inject action type into data record
            // TODO (S): Cache the types for each recordId?
            var actionSet = this.widgets.dataTable.getRecord(target).getData("actionSet");

            var clone = Dom.get(this.id + "-actionSet-" + actionSet).cloneNode(true);
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
         var Dom = YAHOO.util.Dom;

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
       */
      onActionShowMore: function DL_onActionShowMore(row, elMore)
      {
         var Dom = YAHOO.util.Dom;
         var Event = YAHOO.util.Event;
         var me = this;
         
         var elMoreActions = Dom.getNextSibling(elMore);
         Dom.removeClass(elMoreActions, "hidden");
         
         // Mouse over handler - clear any registered hide timer
         var onMouseOver = function DLSM_onMouseOver(e, obj)
         {
            // Clear any existing hide timer
            if (obj.hideTimerId)
            {
               clearTimeout(elMoreActions.hideTimerId);
               elMoreActions.hideTimerId = null;
            }
         }
         
         // Mouse out handler - register hide timer
         var onMouseOut = function DLSM_onMouseOut(e, obj)
         {
            var elTarget = Event.getTarget(e);
            var elTag = elTarget.nodeName.toLowerCase();
            var related = e.relatedTarget;

            // In some cases we should ignore this mouseout event
            if ((related != obj) && (related.prefix != 'xul') && (!Dom.isAncestor(obj, related)))
            {
               if (obj.hideTimerId)
               {
                  clearTimeout(obj.hideTimerId);
               }
               obj.hideTimerId = setTimeout(function()
               {
                  Dom.addClass(obj, "hidden");
                  Event.removeListener(obj, "mouseover");
                  Event.removeListener(obj, "mouseout");
               }, me.options.actionsPopupTimeout);
            }
            else
            {
               Alfresco.logger.debug("mouseout: ignored");
            }
         }
         
         Event.on(elMoreActions, "mouseover", onMouseOver, elMoreActions);
         Event.on(elMoreActions, "mouseout", onMouseOut, elMoreActions);
      },
      
      /**
       * Delete Asset.
       *
       * @method onActionDeleteAsset
       */
      onActionDeleteAsset: function DL_onActionDeleteAsset(row)
      {
         var me = this;
         var record = this.widgets.dataTable.getRecord(row);
         Alfresco.util.PopupManager.displayPrompt(
         {
            text: "Are you sure you want to delete '" + record.getData("name") + "'?",
            buttons: [
            {
               text: "Delete",
               handler: function DL_onActionDeleteAsset_delete()
               {
                  this.hide();
                  me._onActionDeleteAssetConfirm.call(me, record);
               },
               isDefault: true
            },
            {
               text: "Cancel",
               handler: function DL_onActionDeleteAsset_cancel()
               {
                  this.hide();
               }
            }]
         });
      },

      /**
       * Delete Asset confirmed.
       *
       * @method _onActionDeleteAssetConfirm
       * @private
       */
      _onActionDeleteAssetConfirm: function DL__onActionDeleteAssetConfirm(record)
      {
         var me = this;
         var fileType = record.getData("type");
         var obj =
         {
            successCallback:
            {
               fn: function DL__onActionDeleteAssetConfirm_success(data)
               {
                  // Fire the notification event
                  YAHOO.Bubbling.fire(fileType == "folder" ? "folderDeleted" : "fileDeleted",
                  {
                     path: data.config.object.filePath
                  });
                  
                  // Success confirmation message
                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: this._msg("message.delete.success", data.config.object.fileName)
                  });
               },
               scope: this
            },
            failureCallback:
            {
               fn: function DL__onActionDeleteAssetConfirm_failure(data)
               {
                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: this._msg("message.delete.failure", data.config.object.fileName)
                  });
               },
               scope: this
            }
         }
         var action = new Alfresco.module.DoclibActions();
         action.deleteFile(this.options.siteId, this.options.containerId, this.currentPath, record.getData("name"), obj);
      },

      /**
       * Edit Offline.
       *
       * @method onActionEditOffline
       */
      onActionEditOffline: function DL_onActionEditOffline(row)
      {
         var record = this.widgets.dataTable.getRecord(row);
         var obj =
         {
            successCallback:
            {
               fn: function DL_onActionEditOffline_success(data)
               {
                  // Fire the notification event
                  YAHOO.Bubbling.fire("doclistRefresh");
                  
                  // Success confirmation message
                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: this._msg("message.edit-offline.success", data.config.object.fileName)
                  });
               },
               scope: this
            },
            failureCallback:
            {
               fn: function DL_onActionEditOffline_failure(data)
               {
                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: this._msg("message.edit-offline.failure", data.config.object.fileName)
                  });
               },
               scope: this
            }
         }
         var action = new Alfresco.module.DoclibActions();
         action.editFileOffline(this.options.siteId, this.options.containerId, this.currentPath, record.getData("name"), obj);
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
       * @param layer {object} Event fired (unused)
       * @param args {array} Event parameters (unused)
       */
      onDoclistRefresh: function DL_onDoclistRefresh(layer, args)
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
            this._updateDocList.call(this, this.currentPath);
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
               window.location.reload();
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
          var params = "path=" + encodeURIComponent(path);
          params += "&site=" + encodeURIComponent(this.options.siteId);
          params += this.options.showFolders ? "" : "&type=documents";
          params += "&filter=" + encodeURIComponent(this.currentFilterId);
          return params;
       }
   };
})();
