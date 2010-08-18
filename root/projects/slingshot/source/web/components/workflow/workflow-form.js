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
 * WorkflowForm component.
 *
 * The workflow details page form is actually a form display of the workflow's start task and data form the workflow itself.
 * To be able to display all this information the following approach is taken:
 *
 * 1. The page loads with a url containing the workflowId.
 * 2. Since we actually want to display the start task the data-loader compment has been bound into the page,
 *    instructed to load detailed workflow data, so we can get the start task instance id needed to request the form.
 * 3. A dynamically/ajax loaded form is brought in using the start task id which gives us a form with the
 *    "More Info", "Roles" and "Items" sections.
 * 4. Once this form is loaded the additional sections "Summary", "General", "Current Tasks" & "Workflow History"
 *    are inserted inside the form.
 *
 * @namespace Alfresco
 * @class Alfresco.WorkflowForm
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      Selector = YAHOO.util.Selector;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML;

   /**
    * WorkflowForm constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.WorkflowForm} The new WorkflowForm instance
    * @constructor
    */
   Alfresco.WorkflowForm = function WorkflowForm_constructor(htmlId)
   {

      Alfresco.WorkflowForm.superclass.constructor.call(this, "Alfresco.WorkflowForm", htmlId, ["button", "container", "datasource", "datatable"]);
      this.isReady = false;
      this.workflow = null;

      /* Decoupled event listeners */
      YAHOO.Bubbling.on("workflowDetailedData", this.onWorkflowDetailedData, this);

      return this;
   };

   YAHOO.extend(Alfresco.WorkflowForm, Alfresco.component.Base,
   {

      /**
       * Flag set after component is instantiated.
       *
       * @property isReady
       * @type {boolean}
       */
      isReady: false,

      /**
       * The workflow to display 
       *
       * @property workflow
       * @type {Object}
       */
      workflow: null,

      /**
       * Fired by YUI when parent element is available for scripting
       *
       * @method onReady
       */
      onReady: function WorkflowHistory_onReady()
      {
         // Display workflow history if data has been received
         this.isReady = true;
         this._loadWorkflowForm();
      },

      /**
       * Event handler called when the "onWorkflowDetailedData" event is received
       *
       * @method: onWorkflowDetailedData
       */
      onWorkflowDetailedData: function TDH_onWorkflowDetailedData(layer, args)
      {
         // Save workflow info
         this.workflow = args[1];
         this._loadWorkflowForm();
      },

      /**
       * @method _displayWorkflowForm
       * @private
       */
      _loadWorkflowForm: function WF__loadWorkflowForm()
      {
         if (this.isReady && this.workflow)
         {
            // First set the values in our prepared form sections
            Dom.get(this.id + "-title").innerHTML = $html(this.workflow.title);
            Dom.get(this.id + "-description").innerHTML = $html(this.workflow.description);
            var initiator = this.workflow.initiator;
            Dom.get(this.id + "-startedBy").innerHTML = Alfresco.util.userProfileLink(
                  initiator.userName, initiator.firstName + " " + initiator.lastName, null, !initiator.firstName);
            var dueDate = Alfresco.util.fromISO8601(this.workflow.dueDate);
            if (dueDate)
            {
               Dom.get(this.id + "-due").innerHTML = Alfresco.util.formatDate(dueDate);
            }
            var completedDate = Alfresco.util.fromISO8601(this.workflow.endDate);
            Dom.get(this.id + "-completed").innerHTML = $html(completedDate ? Alfresco.util.formatDate(completedDate) : this.msg("label.notCompleted"));
            var startDate = Alfresco.util.fromISO8601(this.workflow.startDate);
            if (startDate)
            {
               Dom.get(this.id + "-started").innerHTML = Alfresco.util.formatDate(completedDate);
            }
            var priorityMap = { "1": "high", "2": "medium", "3": "low" };
            Dom.get(this.id + "-priority").innerHTML = this.msg("priority." + priorityMap[this.workflow.priority + ""]);
            Dom.get(this.id + "-type").innerHTML = $html(this.workflow.title);
            Dom.get(this.id + "-status").innerHTML = $html(this.workflow.isActive ? this.msg("label.inProgress") : this.msg("label.completed"));

            // Load workflow's start task which "represents" the workflow
            Alfresco.util.Ajax.request(
            {
               url: Alfresco.constants.URL_SERVICECONTEXT + "components/form",
               dataObj:
               {
                  htmlid: this.id + "-WorkflowForm-" + Alfresco.util.generateDomId(),
                  itemKind: "task",
                  itemId: this.workflow.startTaskInstanceId,
                  mode: "view",
                  formUI: false
               },
               successCallback:
               {
                  fn: this.onWorkflowFormLoaded,
                  scope: this
               },
               failureMessage: this.msg("message.failure"),
               scope: this,
               execScripts: true
            });
         }
      },

      /**
       * Called when a workflow form has been loaded.
       * Will insert the form in the Dom.
       *
       * @method onWorkflowFormLoaded
       * @param response {Object}
       */
      onWorkflowFormLoaded: function WorkflowForm_onWorkflowFormLoaded(response)
      {
         // Insert the form html
         var formEl = Dom.get(this.id + "-body");
         formEl.innerHTML = response.serverResponse.responseText;

         // Insert the summary & general sections in the top of the form
         var formFieldsEl = Selector.query(".form-fields", this.id, true),
            workflowSummaryEl = Dom.get(this.id + "-summary-form-section"),
            generalSummaryEl = Dom.get(this.id + "-general-form-section");

         formFieldsEl.insertBefore(generalSummaryEl, Dom.getFirstChild(formFieldsEl));
         //formFieldsEl.insertBefore(workflowSummaryEl, generalSummaryEl);

         // Remove current tasks and display workflow history if component is ready
         var tasks = this.workflow.tasks, historyTasks = [], currentTasks = [];
         for (var i = 0, il = tasks.length; i < il; i++)
         {
            if (tasks[i].state == "COMPLETED")
            {
               historyTasks.push(tasks[i]);
            }
            else
            {
               currentTasks.push(tasks[i]);
            }
         }

         var sortByDate = function(dateStr1, dateStr2)
         {
            var date1 = Alfresco.util.fromISO8601(dateStr1),
               date2 = Alfresco.util.fromISO8601(dateStr2);
            if (date1 && date2)
            {
               return date1 < date2 ? 1 : -1;
            }
            else
            {
               return !date1 ? 1 : -1;
            }
         };

         // Sort tasks by completion date
         currentTasks.sort(function(task1, task2)
         {
            return sortByDate(task1.properties.bpm_dueDate, task2.properties.bpm_dueDate);
         });

         // Sort tasks by completion date
         historyTasks.sort(function(task1, task2)
         {
            return sortByDate(task1.properties.bpm_completionDate, task2.properties.bpm_completionDate);
         });

         var me = this;

         /**
          *
          */
         var renderCellType = function WorkflowHistory_onReady_renderCellType(elCell, oRecord, oColumn, oData)
         {
            elCell.innerHTML = '<a href="' + Alfresco.constants.PAGE_CONTEXT + 'task-details?taskId=' + oRecord.getData("id") + '" title="' + me.msg("link.title.task-details") + '">' + $html(oRecord.getData("title")) + '</a>';
         };

         /**
          *
          */
         var renderCellOwner = function WorkflowHistory_onReady_renderCellOwner(elCell, oRecord, oColumn, oData)
         {
            var owner = oRecord.getData("owner");
            if (owner.userName)
            {
               if (owner.firstName)
               {
                  var displayName = $html(me.msg("field.owner", owner.firstName, owner.lastName)),
                     link = '<a href="' + Alfresco.constants.PAGE_CONTEXT + 'user/' + owner.userName + '/profile" title="' + me.msg("link.title.user", displayName) + '">' + displayName + '</a>';
                  elCell.innerHTML = link;
               }
               else
               {
                  elCell.innerHTML = '<span title="' + me.msg("link.title.userDeleted", owner.userName) + '">' + $html(owner.userName) + '</span>';
               }
            }
         };

         /**
          *
          */
         var renderCellDateCompleted = function WorkflowHistory_onReady_renderCellDateCompleted(elCell, oRecord, oColumn, oData)
         {
            var completionDate = Alfresco.util.fromISO8601(oRecord.getData("properties")["bpm_completionDate"]);
            elCell.innerHTML = Alfresco.util.formatDate(completionDate);
         };

         /**
          *
          */
         var renderCellDueDate = function WorkflowHistory_onReady_renderCellDueDate(elCell, oRecord, oColumn, oData)
         {
            var completionDate = Alfresco.util.fromISO8601(oRecord.getData("properties")["bpm_dueDate"]);
            elCell.innerHTML = Alfresco.util.formatDate(completionDate);
         };

         /**
          *
          */
         var renderCellStatus = function WorkflowHistory_onReady_renderCellStatus(elCell, oRecord, oColumn, oData)
         {
            elCell.innerHTML = oRecord.getData("properties")["bpm_status"];
         };

         /**
          *
          */
         var renderCellOutcome = function WorkflowHistory_onReady_renderCellOutcome(elCell, oRecord, oColumn, oData)
         {
            elCell.innerHTML = $html(oRecord.getData("properties")["bpm_outcome"]);
         };

         /**
          *
          */
         var renderCellComment = function WorkflowHistory_onReady_renderCellComment(elCell, oRecord, oColumn, oData)
         {
            elCell.innerHTML = $html(oRecord.getData("properties")["bpm_comment"]);
         };

         /**
          *
          */
         var renderCellCurrentTasksActions = function WorkflowHistory_onReady_renderCellCurrentTasksActions(elCell, oRecord, oColumn, oData)
         {
            var task = oRecord.getData(),
               owner = task.owner ? task.owner : {};
            if (((task.isPooled && !owner.userName) ||
                  (owner.userName == Alfresco.constants.USERNAME) ||
                  (task.workflowInstance.initiator.userName == Alfresco.constants.USERNAME)))
            {
               elCell.innerHTML = '<a href="task-edit?taskId=' + task.id + '" class="edit-task" title="' + me.msg("link.title.task-edit") + '">' + me.msg("actions.edit") + '</a>';
            }
         };

         // Create header and data table elements
         var currentTasksContainerEl = Dom.get(this.id + "-currentTasks-form-section"),
            currentTasksTasksEl = Selector.query("div", currentTasksContainerEl, true);

         // DataTable column definitions for current tasks
         var currentTasksColumnDefinitions =
         [
            { key: "typeDefinitionTitle", label: this.msg("column.type"), formatter: renderCellType },
            { key: "owner", label: this.msg("column.assignedTo"), formatter: renderCellOwner },
            { key: "id", label: this.msg("column.dueDate"), formatter: renderCellDueDate },
            { key: "state", label: this.msg("column.status"), formatter: renderCellStatus },
            { key: "properties", label: this.msg("column.actions"), formatter: renderCellCurrentTasksActions }
         ];

         // Create current tasks data table filled with current tasks
         var currentTasksDS = new YAHOO.util.DataSource(currentTasks);
         currentTasksDS.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
         currentTasksDS.responseSchema =
         {
            fields: [ "title", "typeDefinitionTitle", "owner", "id", "state", "isPooled", "properties"]
         };
         this.widgets.dataTable = new YAHOO.widget.DataTable(currentTasksTasksEl, currentTasksColumnDefinitions, currentTasksDS,
         {
            MSG_EMPTY: this.msg("label.noTasks")
         });

         // DataTable column definitions workflow history
         var historyColumnDefinitions =
         [
            { key: "typeDefinitionTitle", label: this.msg("column.type"), formatter: renderCellType },
            { key: "owner", label: this.msg("column.userGroup"), formatter: renderCellOwner },
            { key: "id", label: this.msg("column.dateCompleted"), formatter: renderCellDateCompleted },
            { key: "state", label: this.msg("column.outcome"), formatter: renderCellOutcome },
            { key: "properties", label: this.msg("column.comment"), formatter: renderCellComment }
         ];

         // Create header and data table elements
         var historyContainerEl = Dom.get(this.id + "-workflowHistory-form-section"),
            historyTasksEl = Selector.query("div", historyContainerEl, true);

         // Create workflow history data table filled with history tasks
         var workflowHistoryDS = new YAHOO.util.DataSource(historyTasks);
         workflowHistoryDS.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
         workflowHistoryDS.responseSchema =
         {
            fields: [ "title", "typeDefinitionTitle", "owner", "id", "state", "properties"]
         };
         this.widgets.dataTable = new YAHOO.widget.DataTable(historyTasksEl, historyColumnDefinitions, workflowHistoryDS,
         {
            MSG_EMPTY: this.msg("label.noTasks")
         });

         // Display tables
         Selector.query(".form-fields", this.id, true).appendChild(currentTasksContainerEl);
         Selector.query(".form-fields", this.id, true).appendChild(historyContainerEl);
      }

   });

})();
