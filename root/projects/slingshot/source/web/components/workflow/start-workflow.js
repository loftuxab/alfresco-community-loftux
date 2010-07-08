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
      Selector = YAHOO.util.Selector,
      Event = YAHOO.util.Event;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML;

   /**
    * StartWorkflow constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.StartWorkflow} The new StartWorkflow instance
    * @constructor
    */
   Alfresco.StartWorkflow = function StartWorkflow_constructor(htmlId)
   {
      Alfresco.StartWorkflow.superclass.constructor.call(this, "Alfresco.StartWorkflow", htmlId, ["button"]);
      return this;
   };

   YAHOO.extend(Alfresco.StartWorkflow, Alfresco.component.Base,
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
          * The workflow items
          *
          * @property workFlowItems
          * @type Array of Alfresco.util.NodeRef
          */
         workFlowItems: null,

         /**
          * Current siteId.
          *
          * @property siteId
          * @type string
          */
         siteId: ""
      },

      /**
       * Fired by YUI when parent element is available for scripting.
       * Template initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function StartWorkflow_onReady()
      {
         this.widgets.workflowSelectEl = Dom.get(this.id + "-workflowDefinitions");
         Event.addListener(this.widgets.workflowSelectEl, "change", this.onWorkflowSelectChange, null, this);
         this.onWorkflowSelectChange();
      },

      /**
       * Called when a workflow definition has been selected
       *
       * @method onWorkflowSelectChange
       */
      onWorkflowSelectChange: function StartWorkflow_onWorkflowSelectChange()
      {
        var i = this.widgets.workflowSelectEl.selectedIndex;
         if (i >= 0) {
            Alfresco.util.Ajax.request(
            {
               url: Alfresco.constants.URL_SERVICECONTEXT + "components/form",
               dataObj:
               {
                  htmlid: this.id + "-startWorkflowForm-" + Alfresco.util.generateDomId(),
                  itemKind: "type",
                  itemId: this.widgets.workflowSelectEl.options[i].value,
                  mode: "edit",
                  submitType: "json",
                  //formId: "doclib-simple-metadata",
                  showCaption: true,
                  formUI: true,
                  showCancelButton: true
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
         Dom.get(this.id + "-workflowFormContainer").innerHTML = response.serverResponse.responseText;
      }

   });
})();
