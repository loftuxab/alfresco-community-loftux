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
 * Dashboard MyTasks component.
 * 
 * @namespace Alfresco.dashlet
 * @class Alfresco.dashlet.MyTasks
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
   var $html = Alfresco.util.encodeHTML;

   /**
    * Dashboard MyTasks constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.dashlet.MyTasks} The new component instance
    * @constructor
    */
   Alfresco.dashlet.MyTasks = function MyTasks_constructor(htmlId)
   {
      Alfresco.dashlet.MyTasks.superclass.constructor.call(this, "Alfresco.dashlet.MyTasks", htmlId, ["button", "container", "datasource", "datatable", "animation"]);

      return this;
   };

   YAHOO.extend(Alfresco.dashlet.MyTasks, Alfresco.component.Base,
   {
      /**
       * Fired by YUI when parent element is available for scripting
       * @method onReady
       */
      onReady: function MyTasks_onReady()
      {
         var me = this;
         
         // DataSource definition
         var properties = ["bpm_priority", "bpm_status", "bpm_dueDate", "bpm_description"];
         this.widgets.dataSource = new YAHOO.util.DataSource(Alfresco.constants.PROXY_URI + "api/task-instances?properties=" + properties.join(",") ,
         {
            responseType: YAHOO.util.DataSource.TYPE_JSON,
            responseSchema:
            {
               resultsList: "data",
               fields: ["id", "name", "state", "isPooled", "typeDefinitionTitle", "owner", "properties"]
            }
         });

         /**
          * Priority & pooled icons custom datacell formatter
          */
         var renderCellIcons = function MyTasks_onReady_renderCellIcons(elCell, oRecord, oColumn, oData)
         {
            Dom.setStyle(elCell, "width", oColumn.width + "px");
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");
            var priority = oRecord.getData("properties")["bpm_priority"],
               pooledTask = oRecord.getData("isPooled");
            var desc = '<img src="' + Alfresco.constants.URL_CONTEXT + '/components/images/priority-' + priority + '-16.png" title="' + me.msg("label.priority", me.msg("label.priority." + priority)) + '"/>';
            if (pooledTask)
            {
               desc += '<br/><img src="' + Alfresco.constants.URL_CONTEXT + '/components/images/pooled-task-16.png" title="' + me.msg("label.pooledTask") + '"/>';
            }
            elCell.innerHTML = desc;
         };
                  
         /**
          * Task info custom datacell formatter
          */
         var renderCellTaskInfo = function MyTasks_onReady_renderCellTaskInfo(elCell, oRecord, oColumn, oData)
         {
            var taskId = oRecord.getData("id"),
               title = oRecord.getData("properties")["bpm_description"],
               dueDateStr = oRecord.getData("properties")["bpm_dueDate"],
               dueDate = dueDateStr ? Alfresco.util.fromISO8601(dueDateStr) : null,
               today = new Date(),
               type = oRecord.getData("typeDefinitionTitle"),
               status = oRecord.getData("properties")["bpm_status"],
               assignee = oRecord.getData("owner");
            var titleDesc = '<h4><a href="task-details?taskId=' + encodeURIComponent(taskId) + '" class="theme-color-1" title="' + me.msg("link.viewTask") + '">' + title + '</a></h4>',
               dateDesc = dueDate ? '<h4><span class="' + (today > dueDate ? "task-delayed" : "") + '">' + Alfresco.util.formatDate(dueDate, "mediumDate") + '</span></h4>' : "",
               statusDesc = '<div>' + me.msg("label.taskSummary", type, status) + '</div>',
               unassignedDesc = '';
            if (!assignee || !assignee.userName)
            {
               unassignedDesc = '<span class="theme-bg-color-5 theme-color-5 unassigned-task">' + me.msg("label.unassignedTask") + '</span>';
            }
            elCell.innerHTML = titleDesc + dateDesc + statusDesc + unassignedDesc;
         };
         
         /**
          * Actions custom datacell formatter
          */
         var renderCellActions = function MyTasks_onReady_renderCellActions(elCell, oRecord, oColumn, oData)
         {
            Dom.setStyle(elCell, "width", oColumn.width + "px");
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");
            var taskId = oRecord.getData("id"),
               owner = oRecord.getData("owner");
            // todo also check against initiator once its in the REST api response.
            if (owner && owner.username == Alfresco.constants.USERNAME)
            {
               elCell.innerHTML = '<a href="edit-task?taskId=' + encodeURIComponent(taskId) + '" class="edit-task" title="' + me.msg("link.editTask") + '">&nbsp;</a>';
            }
         };

         // DataTable column definitions
         var columnDefinitions =
         [
            { key: "isPooled", sortable: false, formatter: renderCellIcons, width: 20 },
            { key: "typeDefinitionTitle", sortable: false, formatter: renderCellTaskInfo },
            { key: "name", sortable: false, formatter: renderCellActions, width: 20}
         ];

         // DataTable definition
         this.widgets.dataTable = new YAHOO.widget.DataTable(this.id + "-tasks", columnDefinitions, this.widgets.dataSource,
         {
            MSG_EMPTY: this.msg("label.noTasks")
         });

         // Enable row highlighting
         this.widgets.dataTable.subscribe("rowMouseoverEvent", this.widgets.dataTable.onEventHighlightRow);
         this.widgets.dataTable.subscribe("rowMouseoutEvent", this.widgets.dataTable.onEventUnhighlightRow);
      },

      /**
       * Fired by YUI when parent element is available for scripting
       * @method reloadTasks
       */
      reloadTasks: function MyTasks_reloadTasks()
      {
         var successHandler = function MyTasks_reloadTasks_successHandler(sRequest, oResponse, oPayload)
         {
            this.widgets.dataTable.onDataReturnInitializeTable.call(this.widgets.dataTable, sRequest, oResponse, oPayload);
         };

         this.widgets.dataSource.sendRequest("",
         {
            success: successHandler,
            scope: this
         });
      }

   });
})();
