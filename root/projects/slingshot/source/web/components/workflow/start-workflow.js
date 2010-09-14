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
 * StartWorkflow component.
 *
 * @namespace Alfresco
 * @class Alfresco.StartWorkflow
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event;

   /**
    * StartWorkflow constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.StartWorkflow} The new StartWorkflow instance
    * @constructor
    */
   Alfresco.StartWorkflow = function StartWorkflow_constructor(htmlId)
   {
      Alfresco.StartWorkflow.superclass.constructor.call(this, htmlId, ["button"]);

      // Re-register with our own name
      this.name = "Alfresco.StartWorkflow";
      this.selectedItems = "";
      this.destination = "";
      this.workflowTypes = [];
      Alfresco.util.ComponentManager.reregister(this);

      YAHOO.Bubbling.on("objectFinderReady", this.onObjectFinderReady, this);
      YAHOO.Bubbling.on("formContentReady", this.onFormContentReady, this);

      return this;
   };

   YAHOO.extend(Alfresco.StartWorkflow, Alfresco.FormManager,
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
          * The nodeRefs, separated by commas, to display in the workflow forms packageItems control.
          *
          * @property selectedItems
          * @type string
          */
         selectedItems: "",

         /**
          * A nodeRef that represents the context of the workflow
          *
          * @property destination
          * @type string
          */
         destination: "",

         /**
          * The workflow types that can be started
          *
          * @property workflowDefinitions
          * @type Array of
          *    {
          *       name: {String} The workflow name (unique)
          *       title: {String} The title of the workflow
          *       description {String} The description of the workflow
          *    }
          */
         workflowDefinitions: []
      },

      /**
       * Fired by YUI when parent element is available for scripting.
       * Template initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function StartWorkflow_onReady()
      {
         // Listen for workflow choices
         this.widgets.workflowDefinitionMenuButton = new YAHOO.widget.Button(this.id + "-workflow-definition-button",
         {
            type: "menu",
            menu: this.id + "-workflow-definition-menu"
         });
         this.widgets.workflowDefinitionMenuButton.set("label", this.msg("label.selectWorkflowDefinition"));
         this.widgets.workflowDefinitionMenuButton.set("title", this.msg("title.selectWorkflowDefinition"));
         this.widgets.workflowDefinitionMenuButton.getMenu().subscribe("click", this.onWorkflowSelectChange, null, this);

         return Alfresco.StartWorkflow.superclass.onReady.call(this);
      },

      /**
       * Will populate the form packageItem's objectFinder with selectedItems when its ready
       *
       * @method onObjectFinderReady
       * @param layer {object} Event fired (unused)
       * @param args {array} Event parameters
       */
      onObjectFinderReady: function StartWorkflow_onObjectFinderReady(layer, args)
      {
         var objectFinder = args[1].eventGroup;
         if (objectFinder.options.field == "assoc_packageItems" && objectFinder.eventGroup.indexOf(this.id) == 0)
         {
            objectFinder.selectItems(this.options.selectedItems);
         }
      },

      /**
       * Called when a workflow definition has been selected
       *
       * @method onWorkflowSelectChange
       */
      onWorkflowSelectChange: function StartWorkflow_onWorkflowSelectChange(p_sType, p_aArgs)
      {
         var i = p_aArgs[1].index;
         if (i >= 0)
         {
            // Update label of workflow menu button
            var workflowDefinition = this.options.workflowDefinitions[i];
            this.widgets.workflowDefinitionMenuButton.set("label", workflowDefinition.title);
            this.widgets.workflowDefinitionMenuButton.set("title", workflowDefinition.description);

            // Load the form for the specific workflow
            Alfresco.util.Ajax.request(
            {
               url: Alfresco.constants.URL_SERVICECONTEXT + "components/form",
               dataObj:
               {
                  htmlid: this.id + "-startWorkflowForm-" + Alfresco.util.generateDomId(),
                  itemKind: "workflow",
                  itemId: workflowDefinition.name,
                  mode: "create",
                  submitType: "json",
                  showCaption: true,
                  formUI: true,
                  showCancelButton: true,
                  destination: this.options.destination
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
      onWorkflowFormLoaded: function StartWorkflow_onWorkflowFormLoaded(response)
      {
         var formEl = Dom.get(this.id + "-workflowFormContainer");
         Dom.addClass(formEl, "hidden");
         formEl.innerHTML = response.serverResponse.responseText;
      },

      /**
       * Event handler called when the "formContentReady" event is received
       */
      onFormContentReady: function FormManager_onFormContentReady(layer, args)
      {
         var formEl = Dom.get(this.id + "-workflowFormContainer");
         Dom.removeClass(formEl, "hidden");                  
      },

      /**
       * Decides to which page we shall navigate after form submission or cancellation
       *
       * @method navigateForward
       * @override
       */
      navigateForward: function FormManager_navigateForward()
      {
         // Did the user come from
         if (document.referrer.match(/document-details([?]|$)/))
         {
            /**
             * We can't use history.go(-1) since it won't refresh the page which means
             * the user wouldn't see the new workflow in the workflow list
             */
            document.location.href = document.referrer;
         }
         else
         {
            Alfresco.StartWorkflow.superclass.navigateForward.call(this);
         }
      }

   });

})();
