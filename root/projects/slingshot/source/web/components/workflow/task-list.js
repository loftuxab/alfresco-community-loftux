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

      /**
       * Decoupled event listeners
       */
      YAHOO.Bubbling.on("filterChanged", this.onFilterChanged, this);

      return this;
   };

   /**
    * Extend from Alfresco.component.Base
    */
   YAHOO.extend(Alfresco.component.TaskList, Alfresco.component.Base);

   /**
    * Augment prototype with Common Workflow actions to reuse createFilterURLParameters
    */
   YAHOO.lang.augmentProto(Alfresco.component.TaskList, Alfresco.action.WorkflowActions);

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
          * Task types not to display
          *
          * @property hiddenTaskTypes
          * @type Array
          * @default []
          */
         hiddenTaskTypes: [],
         
         /**
          * Instruction show to resolve filter id & data to url parameters 
          *
          * @property filterParameters
          * @type Array
          * @default []
          */
         filterParameters: []
      },

      /**
       * Fired by YUI when parent element is available for scripting.
       * Initial History Manager event registration
       *
       * @method onReady
       */
      onReady: function DL_onReady()
      {
         var url = Alfresco.constants.PROXY_URI + "api/task-instances?authority=" + encodeURIComponent(Alfresco.constants.USERNAME) +
               "&properties=" + ["bpm_priority", "bpm_status", "bpm_dueDate", "bpm_description"].join(",") +
               "&exclude=" + this.options.hiddenTaskTypes.join(",");
         this.widgets.pagingDataTable = new Alfresco.util.DataTable(
         {
            dataTable:
            {
               container: this.id + "-tasks",
               columnDefinitions:
               [
                  { key: "id", sortable: false, formatter: this.proxy(this.renderCellIcons), width: 40 },
                  { key: "title", sortable: false, formatter: this.proxy(this.renderCellTaskInfo) },
                  { key: "name", sortable: false, formatter: this.proxy(this.renderCellActions), width: 200 }
               ],
               config:
               {
                  MSG_EMPTY: this.msg("message.noTasks")
               }
            },
            dataSource:
            {
               url: url,
               config:
               {
                  fields: ["id", "name", "state", "isPooled", "title", "owner", "properties", "isEditable", "workflowInstance"]
               },
               initialFilter:
               {
                  filterId: "all" 
               },
               filterResolver: this.proxy(function(filter)
               {
                  // Reuse method form WorkflowActions
                  return this.createFilterURLParameters(filter, this.options.filterParameters);
               })
            },
            paginator:
            {
               config:
               {
                  containers: [this.id + "-paginator"],
                  rowsPerPage: 50
               }
            }
         });
      },

      /**
       * Fired when the currently active filter has changed
       *
       * @method onFilterChanged
       * @param layer {string} the event source
       * @param args {object} arguments object
       */
      onFilterChanged: function BaseFilter_onFilterChanged(layer, args)
      {
         var filter = Alfresco.util.cleanBubblingObject(args[1]);
         Dom.get(this.id + "-filterTitle").innerHTML = $html(this.msg("filter." + filter.filterId + (filter.filterData ? "." + filter.filterData : ""), filter.filterData));
      },

      /**
       * DataTable Cell Renderers
       */

      /**
       * Priority & pooled icons custom datacell formatter
       *
       * @method TL_renderCellIcons
       * @param elCell {object}
       * @param oRecord {object}
       * @param oColumn {object}
       * @param oData {object|string}
       */
      renderCellIcons: function TL_renderCellIcons(elCell, oRecord, oColumn, oData)
      {
         var priority = oRecord.getData("properties")["bpm_priority"],
               priorityMap = { "1": "high", "2": "medium", "3": "low" },
               priorityKey = priorityMap[priority + ""],
               pooledTask = oRecord.getData("isPooled");
         var desc = '<img src="' + Alfresco.constants.URL_CONTEXT + '/components/images/priority-' + priorityKey + '-16.png" title="' + this.msg("label.priority", this.msg("priority." + priorityKey)) + '"/>';
         if (pooledTask)
         {
            desc += '<br/><img src="' + Alfresco.constants.URL_CONTEXT + '/components/images/pooled-task-16.png" title="' + this.msg("label.pooledTask") + '"/>';
         }
         elCell.innerHTML = desc;
      },

      /**
       * Task info custom datacell formatter
       *
       * @method TL_renderCellTaskInfo
       * @param elCell {object}
       * @param oRecord {object}
       * @param oColumn {object}
       * @param oData {object|string}
       */
      renderCellTaskInfo: function TL_renderCellTaskInfo(elCell, oRecord, oColumn, oData)
      {
         var taskId = oRecord.getData("id"),
               title = oRecord.getData("properties")["bpm_description"],
               dueDateStr = oRecord.getData("properties")["bpm_dueDate"],
               dueDate = dueDateStr ? Alfresco.util.fromISO8601(dueDateStr) : null,
               today = new Date(),
               type = oRecord.getData("title"),
               status = oRecord.getData("properties")["bpm_status"],
               assignee = oRecord.getData("owner");
         var titleDesc = '<h3><a href="task-edit?taskId=' + taskId + '" class="theme-color-1" title="' + this.msg("link.editTask") + '">' + title + '</a></h3>',
               dateDesc = dueDate ? '<h4><span class="' + (today > dueDate ? "task-delayed" : "") + '">' + Alfresco.util.formatDate(dueDate, "mediumDate") + '</span></h4>' : "",
               statusDesc = '<div>' + this.msg("label.taskSummary", type, status) + '</div>',
               unassignedDesc = '';
         if (!assignee || !assignee.userName)
         {
            unassignedDesc = '<span class="theme-bg-color-5 theme-color-5 unassigned-task">' + this.msg("label.unassignedTask") + '</span>';
         }
         elCell.innerHTML = titleDesc + dateDesc + statusDesc + unassignedDesc;
      },

      /**
       * Actions custom datacell formatter
       *
       * @method TL_renderCellSelected
       * @param elCell {object}
       * @param oRecord {object}
       * @param oColumn {object}
       * @param oData {object|string}
       */
      renderCellActions: function TL_renderCellActions(elCell, oRecord, oColumn, oData)
      {
         // Create actions using WorkflowAction
         this.createAction(elCell, this.msg("link.editTask"), "task-edit-link", $siteURL('task-edit?taskId=' + oRecord.getData('id')));
         this.createAction(elCell, this.msg("link.viewTask"), "task-view-link", $siteURL('task-details?taskId=' + oRecord.getData('id') + '&myTasksLinkBack=true'));
         this.createAction(elCell, this.msg("link.viewWorkflow"), "workflow-view-link", $siteURL('workflow-details?workflowId=' + oRecord.getData('workflowInstance').id + '&' + 'taskId=' + oRecord.getData('id') + '&myTasksLinkBack=true'));
      }

   }, true);
})();
