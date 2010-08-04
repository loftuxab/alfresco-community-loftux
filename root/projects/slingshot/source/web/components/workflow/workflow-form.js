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
 * @namespace Alfresco
 * @class Alfresco.WorkflowForm
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event;

   /**
    * WorkflowForm constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.WorkflowForm} The new WorkflowForm instance
    * @constructor
    */
   Alfresco.WorkflowForm = function WorkflowForm_constructor(htmlId)
   {

      Alfresco.WorkflowForm.superclass.constructor.call(this, "Alfresco.WorkflowForm", htmlId, ["button"]);

      /* Decoupled event listeners */
      YAHOO.Bubbling.on("workflowDetailedData", this.onWorkflowDetailsData, this);

      return this;
   };

   YAHOO.extend(Alfresco.WorkflowForm, Alfresco.component.Base,
   {
      /**
       * Event handler called when the "onWorkflowDetailedData" event is received
       *
       * @method: onWorkflowDetailsData
       */
      onWorkflowDetailsData: function TDH_onWorkflowDetailsData(layer, args)
      {
         // Load workflow's start task which "represents" the workflow
         Alfresco.util.Ajax.request(
         {
            url: Alfresco.constants.URL_SERVICECONTEXT + "components/form",
            dataObj:
            {
               htmlid: this.id + "-WorkflowForm-" + Alfresco.util.generateDomId(),
               itemKind: "task",
               itemId: args[1].startTaskInstanceId,
               mode: "view",
               formUI: true
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
         var formEl = Dom.get(this.id + "-body");
         formEl.innerHTML = response.serverResponse.responseText;
         Alfresco.util.YUILoaderHelper.loadComponents();
      }

   });

})();
