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
 * Workflow WorkflowActions util
 *
 * @namespace Alfresco.action
 * @class Alfresco.action.WorkflowActions
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML,
      $combine = Alfresco.util.combinePaths,
      $siteURL = Alfresco.util.siteURL;

   /**
    * Alfresco.action.WorkflowActions implementation
    */
   Alfresco.action.WorkflowActions = {};
   Alfresco.action.WorkflowActions.prototype =
   {

      /**
       * Prompts the user if the workflow really should be cancelled
       *
       * @method _showDialog
       * @param workflowId {String} The workflow id
       * @param workflowTitle {String} THe workflow title
       * @private
       */
      cancelWorkflow: function WA_cancelWorkflow(workflowId, workflowTitle)
      {
         var me = this,
            wid = workflowId;
         Alfresco.util.PopupManager.displayPrompt(
         {
            title: this.msg("workflow.cancel.title"),
            text: this.msg("workflow.cancel.label", $html(workflowTitle)),
            noEscape: true,
            buttons: [
               {
                  text: Alfresco.util.message("button.yes", this.name),
                  handler: function WA_cancelWorkflow_yes()
                  {
                     this.destroy();
                     me._cancelWorkflow.call(me, wid);
                  }
               },
               {
                  text: Alfresco.util.message("button.no", this.name),
                  handler: function WA_cancelWorkflow_no()
                  {
                     this.destroy();
                  },
                  isDefault: true
               }]
         });
      },

      /**
       * Cancels the workflow
       *
       * @method _cancelWorkflow
       * @param workflowId {String} The workflow id
       * @private
       */
      _cancelWorkflow: function WA__cancelWorkflow(workflowId)
      {
         var me = this;
         var feedbackMessage = Alfresco.util.PopupManager.displayMessage(
         {
            text: this.msg("workflow.cancel.feedback"),
            spanClass: "wait",
            displayTime: 0
         });

         // user has confirmed, perform the actual delete
         Alfresco.util.Ajax.jsonDelete(
         {
            url: Alfresco.constants.PROXY_URI + "api/workflow-instances/" + workflowId,
            successCallback:
            {
               fn: function(response, workflowId)
               {
                  feedbackMessage.destroy();
                  if (response.json && response.json.success)
                  {
                     Alfresco.util.PopupManager.displayMessage(
                     {
                        text: this.msg("workflow.cancel.success", this.name)
                     });

                     // Tell other components that the site has been deleted
                     YAHOO.Bubbling.fire("workflowCancelled",
                     {
                        workflow:
                        {
                           id: workflowId
                        }
                     });
                  }
                  else
                  {
                     Alfresco.util.PopupManager.displayMessage(
                     {
                        text: Alfresco.util.message("workflow.cancel.failure", this.name)
                     });
                  }
               },
               obj: workflowId,
               scope: this
            },
            failureCallback:
            {
               fn: function(response)
               {
                  feedbackMessage.destroy();
                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: Alfresco.util.message("workflow.cancel.failure", this.name)
                  });
               },
               scope: this
            }
         });
      }

   };
})();
