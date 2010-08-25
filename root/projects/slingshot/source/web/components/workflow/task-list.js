/**
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
 
/**
 * TaskList component.
 * 
 * @namespace Alfresco
 * @class Alfresco.component.TaskList
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML,
      $siteURL = Alfresco.util.siteURL;

   /**
    * DocumentList constructor.
    * 
    * @param htmlId {String} The HTML id of the parent element
    * @return {Alfresco.component.TaskList} The new DocumentList instance
    * @constructor
    */
   Alfresco.component.TaskList = function(htmlId)
   {
      Alfresco.component.TaskList.superclass.constructor.call(this, "Alfresco.component.TaskList", htmlId, ["button", "menu", "container", "datasource", "datatable", "paginator", "json", "history"]);

      // Initialise prototype properties
      this.currentPage = 1;
      this.totalRecords = 0;
      this.currentFilter =
      {
         filterId: "",
         filterData: ""
      };

      /**
       * Action links CSS style.
       */
      this.TASK_EDIT_CLASS = "task-edit-link";
      this.TASK_VIEW_CLASS = "task-view-link";
      this.WORKFLOW_VIEW_CLASS = "workflow-view-link";

      /**
       * Decoupled event listeners
       */
      YAHOO.Bubbling.on("changeFilter", this.onChangeFilter, this);
      YAHOO.Bubbling.on("filterChanged", this.onFilterChanged, this);
      return this;
   };

   /**
    * Extend from Alfresco.component.Base
    */
   YAHOO.extend(Alfresco.component.TaskList, Alfresco.component.Base);

   /**
    * Custom field generator functions
    */

   /**
    * Augment prototype with main class implementation, ensuring overwrite is enabled
    */
   YAHOO.lang.augmentObject(Alfresco.component.TaskList.prototype,
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
          * Initial page to show on load (otherwise taken from URL hash).
          * 
          * @property initialPage
          * @type int
          */
         initialPage: 1,

         /**
          * Number of items per page
          * 
          * @property pageSize
          * @type int
          */
         pageSize: 10,

         /**
          * Initial filter to show on load.
          * 
          * @property initialFilter
          * @type object
          */
         initialFilter: {},
         
         /**
          * Delay before showing "loading" message for slow data requests
          *
          * @property loadingMessageDelay
          * @type int
          * @default 1000
          */
         loadingMessageDelay: 1000,

         /**
          * Task types not to display
          *
          * @property hiddenTaskTypes
          * @type object
          * @default []
          */
         hiddenTaskTypes: []
      },

      /**
       * Current page being browsed.
       * 
       * @property currentPage
       * @type int
       * @default 1
       */
      currentPage: null,
      
      /**
       * Total number of records .
       * 
       * @property totalRecords
       * @type int
       * @default 0
       */
      totalRecords: null,

      /**
       * Current filter to filter document list.
       * 
       * @property currentFilter
       * @type object
       */
      currentFilter: null,

      /**
       * Fired by YUI when parent element is available for scripting.
       * Initial History Manager event registration
       *
       * @method onReady
       */
      onReady: function DL_onReady()
      {
         // Reference to self used by inline functions
         var me = this;

         // Set-up YUI History Managers
         this._setupHistoryManagers();

         // DataSource set-up and event registration
         this._setupDataSource();
         
         // DataTable set-up and event registration
         this._setupDataTable();

         // Hook action events
         var fnActionHandler = function DL_fnActionHandler(layer, args)
         {
            var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "div");
            if (owner !== null)
            {
               if (typeof me[owner.className] == "function")
               {
                  args[1].stop = true;
                  var asset = me.widgets.dataTable.getRecord(args[1].target.offsetParent).getData();
                  me[owner.className].call(me, asset, owner);
               }
            }
            return true;
         };
         YAHOO.Bubbling.addDefaultAction("action-link", fnActionHandler);

         // Hook filter change events
         var fnChangeFilterHandler = function DL_fnChangeFilterHandler(layer, args)
         {
            var owner = args[1].anchor;
            if (owner !== null)
            {
               var filter = owner.rel,
                  filters,
                  filterObj = {};
               if (filter && filter !== "")
               {
                  args[1].stop = true;
                  filters = filter.split("|");
                  filterObj =
                  {
                     filterOwner: window.unescape(filters[0] || ""),
                     filterId: window.unescape(filters[1] || ""),
                     filterData: window.unescape(filters[2] || ""),
                     filterDisplay: window.unescape(filters[3] || "")
                  };
                  Alfresco.logger.debug("DL_fnChangeFilterHandler", "changeFilter =>", filterObj);
                  YAHOO.Bubbling.fire("changeFilter", filterObj);
               }
            }
            return true;
         };
         YAHOO.Bubbling.addDefaultAction("filter-change", fnChangeFilterHandler);

         // Continue only when History Manager fires its onReady event
         YAHOO.util.History.onReady(this.onHistoryManagerReady, this, true);

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
            Alfresco.logger.error(this.name + ": Couldn't initialize HistoryManager.", e);
            this.onHistoryManagerReady();
         }
      },

      /**
       * DataTable Cell Renderers
       */

      /**
       * Returns priority & pooled icons custom datacell formatter
       *
       * @method fnRenderCellIcons
       */
      fnRenderCellIcons: function TL_fnRenderCellIcons()
      {
         var scope = this;

         /**
          * Priority & pooled icons custom datacell formatter
          *
          * @method TL_renderCellIcons
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         return function TL_renderCellIcons(elCell, oRecord, oColumn, oData)
         {
            Dom.setStyle(elCell, "width", oColumn.width + "px");
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");
            var priority = oRecord.getData("properties")["bpm_priority"],
               priorityMap = { "1": "high", "2": "medium", "3": "low" },
               priorityKey = priorityMap[priority + ""],
               pooledTask = oRecord.getData("isPooled");
            var desc = '<img src="' + Alfresco.constants.URL_CONTEXT + '/components/images/priority-' + priorityKey + '-16.png" title="' + scope.msg("label.priority", scope.msg("priority." + priorityKey)) + '"/>';
            if (pooledTask)
            {
               desc += '<br/><img src="' + Alfresco.constants.URL_CONTEXT + '/components/images/pooled-task-16.png" title="' + me.msg("label.pooledTask") + '"/>';
            }
            elCell.innerHTML = desc;
         };
      },

      /**
       * Returns task info custom datacell formatter
       *
       * @method fnRenderCellTaskInfo
       */
      fnRenderCellTaskInfo: function TL_fnRenderCellTaskInfo()
      {
         var scope = this;

         /**
          * Task info custom datacell formatter
          *
          * @method TL_renderCellTaskInfo
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         return function TL_renderCellTaskInfo(elCell, oRecord, oColumn, oData)
         {
            var taskId = oRecord.getData("id"),
               title = oRecord.getData("properties")["bpm_description"],
               dueDateStr = oRecord.getData("properties")["bpm_dueDate"],
               dueDate = dueDateStr ? Alfresco.util.fromISO8601(dueDateStr) : null,
               today = new Date(),
               type = oRecord.getData("title"),
               status = oRecord.getData("properties")["bpm_status"],
               assignee = oRecord.getData("owner");
            var titleDesc = '<h3><a href="task-details?taskId=' + taskId + '" class="theme-color-1" title="' + scope.msg("link.viewTask") + '">' + title + '</a></h3>',
               dateDesc = dueDate ? '<h4><span class="' + (today > dueDate ? "task-delayed" : "") + '">' + Alfresco.util.formatDate(dueDate, "mediumDate") + '</span></h4>' : "",
               statusDesc = '<div>' + scope.msg("label.taskSummary", type, status) + '</div>',
               unassignedDesc = '';
            if (!assignee || !assignee.userName)
            {
               unassignedDesc = '<span class="theme-bg-color-5 theme-color-5 unassigned-task">' + scope.msg("label.unassignedTask") + '</span>';
            }
            elCell.innerHTML = titleDesc + dateDesc + statusDesc + unassignedDesc;
         };
      },

      /**
       * Returns actions custom datacell formatter
       *
       * @method fnRenderCellActions
       */
      fnRenderCellActions: function TL_fnRenderCellActions()
      {
         var scope = this;

         /**
          * Actions custom datacell formatter
          *
          * @method TL_renderCellSelected
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         return function TL_renderCellActions(elCell, oRecord, oColumn, oData)
         {
            Dom.setStyle(elCell, "width", oColumn.width + "px");
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");
            var task = oRecord.getData(),
               prefix = oRecord.getData("id");            
            elCell.innerHTML = "<div class='" + scope.TASK_EDIT_CLASS +  "'><a id='edit-" + prefix + "'><span class='theme-color-1'>" + scope.msg("link.editTask") + "</a></span></div>" +
                  "<div class='" + scope.TASK_VIEW_CLASS + "'><a id='delete-" + prefix + "'><span class='theme-color-1'>" + scope.msg("link.viewTask") + "</a></span></div>" +
                  "<div class='" + scope.WORKFLOW_VIEW_CLASS + "'><a id='delete-" + prefix + "'><span class='theme-color-1'>" + scope.msg("link.viewWorkflow") + "</a></span></div>";

            var et = elCell.childNodes[0],
               vt = elCell.childNodes[1],
               vw = elCell.childNodes[2];

            // Edit permission?
            if (task.isEditable)
            {
               et.onclick = function Links_onEditLink()
               {
                  document.location.href = $siteURL("task-edit?taskId=" + oRecord.getData('id'));
               };
               et.onmouseover = function()
               {
                  Dom.addClass(this, scope.TASK_EDIT_CLASS + "-over");
               };
               et.onmouseout = function()
               {
                  Dom.removeClass(this, scope.TASK_EDIT_CLASS + "-over");
               };
            }
            else
            {
               Dom.addClass(ec, 'hidden');
            }

            vt.onclick = function Links_onEditLink()
            {
               document.location.href = $siteURL("task-details?taskId=" + oRecord.getData('id'));
            };
            vt.onmouseover = function()
            {
               Dom.addClass(this, scope.TASK_VIEW_CLASS + "-over");
            };
            vt.onmouseout = function()
            {
               Dom.removeClass(this, scope.TASK_VIEW_CLASS + "-over");
            };

            vw.onclick = function Links_onEditLink()
            {
               document.location.href = $siteURL("workflow-details?workflowId=" + oRecord.getData('workflowInstance').id + "&" + "taskId=" + oRecord.getData('id'));
            };
            vw.onmouseover = function()
            {
               Dom.addClass(this, scope.WORKFLOW_VIEW_CLASS + "-over");
            };
            vw.onmouseout = function()
            {
               Dom.removeClass(this, scope.WORKFLOW_VIEW_CLASS + "-over");
            };
         };
      },

      /**
       * History Manager set-up and event registration
       *
       * @method _setupHistoryManagers
       */
      _setupHistoryManagers: function DL__setupHistoryManagers()
      {
         /**
          * YUI History - filter
          */
         var bookmarkedFilter = YAHOO.util.History.getBookmarkedState("filter") || "";

         try
         {
            while (bookmarkedFilter != (bookmarkedFilter = decodeURIComponent(bookmarkedFilter))){}
         }
         catch (e)
         {
            // Catch "malformed URI sequence" exception
         }
         
         var fnDecodeBookmarkedFilter = function DL_fnDecodeBookmarkedFilter(strFilter)
         {
            var filterObj =
            {
               filterId: "",
               filterData: ""
            };
            if (strFilter.length > 0)
            {
               var filters = strFilter.split("|");
               filterObj.filterId = window.unescape(filters[0] || "");
               filterObj.filterData =  window.unescape(filters[1] || "");
               filterObj.filterOwner = Alfresco.util.FilterManager.getOwner(filterObj.filterId);
            }
            return filterObj;
         };
         
         this.options.initialFilter = fnDecodeBookmarkedFilter(bookmarkedFilter);

         // Register History Manager filter update callback
         YAHOO.util.History.register("filter", bookmarkedFilter, function DL_onHistoryManagerFilterChanged(newFilter)
         {
            Alfresco.logger.debug("HistoryManager: filter changed:" + newFilter);
            // Firefox fix
            if (YAHOO.env.ua.gecko > 0)
            {
               newFilter = window.unescape(newFilter);
               Alfresco.logger.debug("HistoryManager: filter (after Firefox fix):" + newFilter);
            }
            
            this._updateDocList.call(this,
            {
               filter: fnDecodeBookmarkedFilter(newFilter),
               page: this.currentPage
            });
         }, null, this);


         /**
          * YUI History - page
          */
         var handlePagination = function DL_handlePagination(state, me)
         {
            me.widgets.paginator.setState(state);
            YAHOO.util.History.navigate("page", String(state.page));
         };

         var bookmarkedPage = YAHOO.util.History.getBookmarkedState("page") || "0";
         while (bookmarkedPage != (bookmarkedPage = decodeURIComponent(bookmarkedPage))){}
         this.currentPage = parseInt(bookmarkedPage || this.options.initialPage, 10);

         // Register History Manager page update callback
         YAHOO.util.History.register("page", bookmarkedPage, function DL_onHistoryManagerPageChanged(newPage)
         {
            Alfresco.logger.debug("HistoryManager: page changed:" + newPage);
            // Update the DocList
            if (this.currentPage != newPage)
            {
               this._updateDocList.call(this,
               {
                  page: newPage
               });
            }
            else
            {
               Alfresco.logger.debug("...page changed event ignored.");
            }
         }, null, this);

         // YUI Paginator definition
         this.widgets.paginator = new YAHOO.widget.Paginator(
         {
            containers: [this.id + "-paginator"],
            rowsPerPage: this.options.pageSize,
            initialPage: this.currentPage,
            template: this.msg("pagination.template"),
            pageReportTemplate: this.msg("pagination.template.page-report"),
            previousPageLinkLabel: this.msg("pagination.previousPageLinkLabel"),
            nextPageLinkLabel: this.msg("pagination.nextPageLinkLabel")
         });

         this.widgets.paginator.subscribe("changeRequest", handlePagination, this);
      },
      
      /**
       * DataSource set-up and event registration
       *
       * @method _setupDataSource
       * @protected
       */
      _setupDataSource: function DL__setupDataSource()
      {
         var me = this;

         var properties = ["bpm_priority", "bpm_status", "bpm_dueDate", "bpm_description"];
         this.widgets.dataSource = new YAHOO.util.DataSource(Alfresco.constants.PROXY_URI +
               "api/task-instances?authority=" + encodeURIComponent(Alfresco.constants.USERNAME) +
               "&properties=" + properties.join(","),
         {
            responseType: YAHOO.util.DataSource.TYPE_JSON,
            responseSchema:
            {
               resultsList: "data",
               fields: ["id", "name", "state", "isPooled", "title", "owner", "properties", "isEditable"]
            },
            metaFields:
            {
               paging: "paging",
               recordOffset: "paging.skipCount",
               /*paginationRecordOffset: "paging.skipCount",*/
               totalRecords: "paging.totalItems"
            }
         });
      },
      
      /**
       * DataTable set-up and event registration
       *
       * @method _setupDataTable
       * @protected
       */
      _setupDataTable: function DL__setupDataTable()
      {
         var me = this;

         // DataTable column definitions
         var columnDefinitions =
         [
            { key: "isPooled", sortable: false, formatter: this.fnRenderCellIcons(), width: 40 },
            { key: "title", sortable: false, formatter: this.fnRenderCellTaskInfo() },
            { key: "name", sortable: false, formatter: this.fnRenderCellActions(), width: 200}
         ];

         // DataTable definition
         this.widgets.dataTable = new YAHOO.widget.DataTable(this.id + "-tasks", columnDefinitions, this.widgets.dataSource,
         {
            renderLoopSize: 16,
            initialLoad: false,
            dynamicData: true,
            MSG_EMPTY: this.msg("message.loading")
         });

         // Update totalRecords on the fly with value from server
         this.widgets.dataTable.handleDataReturnPayload = function DL_handleDataReturnPayload(oRequest, oResponse, oPayload)
         {
            /*
            me.totalRecords = oResponse.meta.totalRecords;
            return oResponse.meta;
            */
            // Save totalRecords for Paginator update later
            me.recordOffset = oResponse.meta.recordOffset;
            me.totalRecords = oResponse.meta.totalRecords;

            oPayload = oPayload || {};
            oPayload.recordOffset = oResponse.meta.recordOffset;
            oPayload.totalRecords = oResponse.meta.totalRecords;
            return oPayload;
         };

         // Custom error messages
         this._setDefaultDataTableErrors(this.widgets.dataTable);

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
                  me.widgets.dataTable.set("MSG_ERROR", response.message);
               }
               catch(e)
               {
                  me._setDefaultDataTableErrors(me.widgets.dataTable);
               }
            }

            // We don't get an renderEvent for an empty recordSet, but we'd like one anyway
            if (oResponse.results.length === 0)
            {
               this.fireEvent("renderEvent",
               {
                  type: "renderEvent"
               });
            }
            
            // Must return true to have the "Loading..." message replaced by the error message
            return true;
         };

         // Rendering complete event handler
         this.widgets.dataTable.subscribe("renderEvent", function()
         {
            Alfresco.logger.debug("DataTable renderEvent");
            
            // IE6 fix for long filename rendering issue
            if (YAHOO.env.ua.ie < 7)
            {
               var ie6fix = this.widgets.dataTable.getTableEl().parentNode;
               ie6fix.className = ie6fix.className;
            }
            
            // Update the paginator if it's been created
            if (this.widgets.paginator)
            {
               Alfresco.logger.debug("Setting paginator state: page=" + this.currentPage + ", totalRecords=" + this.totalRecords);

               this.widgets.paginator.setState(
               {
                  recordOffset: this.recordOffset,
                  totalRecords: this.totalRecords
               });
               this.widgets.paginator.render();
            }
            
         }, this, true);
         
         // Enable row highlighting
         this.widgets.dataTable.subscribe("rowMouseoverEvent", this.widgets.dataTable.onEventHighlightRow);
         this.widgets.dataTable.subscribe("rowMouseoutEvent", this.widgets.dataTable.onEventUnhighlightRow);

      },
      
      /**
       * Fired by YUI when History Manager is initialised and available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onHistoryManagerReady
       */
      onHistoryManagerReady: function DL_onHistoryManagerReady()
      {
         // Fire changeFilter event for first-time population
         Alfresco.logger.debug("DL_onHistoryManagerReady", "changeFilter =>", this.options.initialFilter);
         YAHOO.Bubbling.fire("changeFilter", YAHOO.lang.merge(
         {
            doclistFirstTimeNav: true
         }, this.options.initialFilter));
         
         // Finally show the component body here to prevent UI artifacts on YUI button decoration
         Dom.setStyle(this.id + "-body", "visibility", "visible");
      },

      /**
       * DocList View change filter request event handler
       *
       * @method onChangeFilter
       * @param layer {object} Event fired (unused)
       * @param args {array} Event parameters (new filterId)
       */
      onChangeFilter: function DL_onChangeFilter(layer, args)
      {
         var obj = args[1];
         if ((obj !== null) && (obj.filterId !== null))
         {
            // Should be a filter in the arguments
            var filter = Alfresco.util.cleanBubblingObject(obj),
               strFilter = window.escape(obj.filterId) + (typeof obj.filterData !== "undefined" ? "|" + window.escape(obj.filterData) : "");
            
            Alfresco.logger.debug("DL_onChangeFilter: ", filter);

            var objNav =
            {
               filter: strFilter
            };

            this.currentPage = 1;
            objNav.page = "1";

            // Initial navigation won't fire the History event
            if (obj.doclistFirstTimeNav)
            {
               this._updateDocList.call(this,
               {
                  filter: filter,
                  page: 1
               });
            }
            else
            {
               Alfresco.logger.debug("DL_onChangeFilter: objNav = ", objNav);
               YAHOO.util.History.multiNavigate(objNav);
            }
         }
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
            obj.filterOwner = obj.filterOwner || Alfresco.util.FilterManager.getOwner(obj.filterId);

            // Should be a filterId in the arguments
            this.currentFilter = Alfresco.util.cleanBubblingObject(obj);
            Alfresco.logger.debug("DL_onFilterChanged: ", this.currentFilter);
         }
      },

      /**
       * PRIVATE FUNCTIONS
       */

      /**
       * Resets the YUI DataTable errors to our custom messages
       * NOTE: Scope could be YAHOO.widget.DataTable, so can't use "this"
       *
       * @method _setDefaultDataTableErrors
       * @param dataTable {object} Instance of the DataTable
       */
      _setDefaultDataTableErrors: function DL__setDefaultDataTableErrors(dataTable)
      {
         var msg = Alfresco.util.message;
         dataTable.set("MSG_EMPTY", msg("message.empty", "Alfresco.component.TaskList"));
         dataTable.set("MSG_ERROR", msg("message.error", "Alfresco.component.TaskList"));
      },
      
      /**
       * Updates task list by calling data webscript
       *
       * @method _updateDocList
       * @param p_obj.filter {object} Optional filter to navigate with
       * @param p_obj.page {string} Optional page to navigate to (defaults to this.currentPage)
       */
      _updateDocList: function DL__updateDocList(p_obj)
      {
         p_obj = p_obj || {};
         Alfresco.logger.debug("DL__updateDocList: ", p_obj.filter, p_obj.page);
         var successFilter = YAHOO.lang.merge({}, p_obj.filter !== undefined ? p_obj.filter : this.currentFilter),
            successPage = p_obj.page !== undefined ? p_obj.page : this.currentPage,
            loadingMessage = null,
            timerShowLoadingMessage = null,
            me = this,
            params =
            {
               filter: successFilter,
               page: successPage
            };
         successFilter.doclistFirstTimeNav = false;
         
         // Clear the current document list if the data webscript is taking too long
         var fnShowLoadingMessage = function DL_fnShowLoadingMessage()
         {
            Alfresco.logger.debug("DL__uDL_fnShowLoadingMessage: slow data webscript detected.");
            // Check the timer still exists. This is to prevent IE firing the event after we cancelled it. Which is "useful".
            if (timerShowLoadingMessage)
            {
               loadingMessage = Alfresco.util.PopupManager.displayMessage(
               {
                  displayTime: 0,
                  text: '<span class="wait">' + $html(this.msg("message.loading")) + '</span>',
                  noEscape: true
               });
               
               if (YAHOO.env.ua.ie > 0)
               {
                  this.loadingMessageShowing = true;
               }
               else
               {
                  loadingMessage.showEvent.subscribe(function()
                  {
                     this.loadingMessageShowing = true;
                  }, this, true);
               }
            }
         };
         
         // Reset the custom error messages
         this._setDefaultDataTableErrors(this.widgets.dataTable);
         
         // Slow data webscript message
         this.loadingMessageShowing = false;
         timerShowLoadingMessage = YAHOO.lang.later(this.options.loadingMessageDelay, this, fnShowLoadingMessage);
         
         var destroyLoaderMessage = function DL__uDL_destroyLoaderMessage()
         {
            if (timerShowLoadingMessage)
            {
               // Stop the "slow loading" timed function
               timerShowLoadingMessage.cancel();
               timerShowLoadingMessage = null;
            }

            if (loadingMessage)
            {
               if (this.loadingMessageShowing)
               {
                  // Safe to destroy
                  loadingMessage.destroy();
                  loadingMessage = null;
               }
               else
               {
                  // Wait and try again later. Scope doesn't get set correctly with "this"
                  YAHOO.lang.later(100, me, destroyLoaderMessage);
               }
            }
         };
         
         var successHandler = function DL__uDL_successHandler(sRequest, oResponse, oPayload)
         {
            destroyLoaderMessage();
            // Updating the Doclist may change the file selection

            Alfresco.logger.debug("currentFilter was:", this.currentFilter, "now:", successFilter);
            Alfresco.logger.debug("currentPage was [" + this.currentPage + "] now [" + successPage + "]");
            this.currentFilter = successFilter;
            this.currentPage = successPage;
            YAHOO.Bubbling.fire("filterChanged", successFilter);
            this.widgets.dataTable.onDataReturnInitializeTable.call(this.widgets.dataTable, sRequest, oResponse, oPayload);
         };
         
         var failureHandler = function DL__uDL_failureHandler(sRequest, oResponse)
         {
            destroyLoaderMessage();

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
                  this.widgets.dataTable.set("MSG_ERROR", response.message);
                  this.widgets.dataTable.showTableMessage(response.message, YAHOO.widget.DataTable.CLASS_ERROR);
                  if (oResponse.status == 404)
                  {
                     // Site or container not found - deactivate controls
                     YAHOO.Bubbling.fire("deactivateAllControls");
                  }
               }
               catch(e)
               {
                  this._setDefaultDataTableErrors(this.widgets.dataTable);
               }
            }
         };
         
         // Update the DataSource
         var requestParams = this._buildDocListParams(params);
         Alfresco.logger.debug("DataSource requestParams: ", requestParams);
         this.widgets.dataSource.sendRequest(requestParams,
         {
            success: successHandler,
            failure: failureHandler,
            scope: this
         });
      },

      /**
       * Build URI parameter string for task-list JSON data webscript
       *
       * @method _buildDocListParams
       * @param p_obj.page {string} Page number
       * @param p_obj.pageSize {string} Number of items per page
       * @param p_obj.type {string} Filetype to filter: "all", "documents", "folders"
       * @param p_obj.site {string} Current site
       * @param p_obj.container {string} Current container
       * @param p_obj.filter {string} Current filter
       */
      _buildDocListParams: function DL__buildDocListParams(p_obj)
      {
         // Essential defaults
         var obj = 
         {
            filter: this.currentFilter
         };
         
         obj.page = this.widgets.paginator.getCurrentPage() || this.currentPage;
         obj.pageSize = this.widgets.paginator.getRowsPerPage();

         // Passed-in overrides
         if (typeof p_obj == "object")
         {
            obj = YAHOO.lang.merge(obj, p_obj);
         }

         // Build the URI stem
         var params = "";

         // Filter parameters
         params += "&filter=" + encodeURIComponent(obj.filter.filterId);
         if (obj.filter.filterData)
         {
            params += "&filterData=" + encodeURIComponent(obj.filter.filterData);             
         }
         
         // Paging parameters
         params += "&maxItems=" + obj.pageSize  + "&skipCount=" + obj.page;

         // No-cache
         params += "&noCache=" + new Date().getTime();

         return params;
      }
       
   }, true);
})();
