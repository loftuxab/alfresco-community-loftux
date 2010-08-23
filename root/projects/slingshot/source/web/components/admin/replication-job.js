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
 * Replication Job Form component.
 * 
 * @namespace Alfresco
 * @class Alfresco.component.ReplicationJob
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
    * ReplicationJob constructor.
    * 
    * @param htmlId {String} The HTML id of the parent element
    * @return {Alfresco.component.ReplicationJob} The new ReplicationJob instance
    * @constructor
    */
   Alfresco.component.ReplicationJob = function(htmlId)
   {
      Alfresco.component.ReplicationJob.superclass.constructor.call(this, "Alfresco.component.ReplicationJob", htmlId, ["button", "menu", "container", "json"]);

      // Initialise prototype properties
      
      // Decoupled event listeners

      return this;
   };

   /**
    * Extend from Alfresco.component.Base
    */
   YAHOO.extend(Alfresco.component.ReplicationJob, Alfresco.component.Base,
   {
      /**
       * Form Runtime instance
       *
       * @property form
       * @type Alfresco.forms.Form
       */
      form: null,

      /**
       * Form controls loaded deferred event utility instance
       *
       * @property controlsLoadedDeferred
       * @type Alfresco.util.Deferred
       */
      controlsLoadedDeferred: null,

      /**
       * Payload property extracted from object picker control.
       *
       * @property payload
       * @type Array
       */
      payload: null,

      /**
       * Transfer Target property extracted from object picker control.
       *
       * @property targetName
       * @type String
       */
      targetName: null,
      
      /**
       * Object container for initialization options
       *
       * @property options
       * @type object
       */
      options:
      {
         /**
          * Job Name - implies editing mode if set
          * 
          * @property jobName
          * @type string
          */
         jobName: "",

         /**
          * Payload property from an existing Job
          *
          * @property payload
          * @type Array
          */
         payload: [],
         
         /**
          * Target Name from an existing Job
          *
          * @property targetName
          * @type string
          */
         targetName: ""
      },

      /**
       * Evaluate create mode (rather than edit mode)
       *
       * @method isCreateMode
       * @return {Boolean} true for create mode
       */
      isCreateMode: function ReplicationJob_isCreateMode()
      {
         return (this.options.jobName == "");
      },

      /**
       * Fired by YUI when parent element is available for scripting.
       *
       * @method onReady
       */
      onReady: function ReplicationJob_onReady()
      {
         // Reference to self used by inline functions
         var me = this;

         // "Create Job" button needs to be "submit" type
         this.widgets.submitButton = Alfresco.util.createYUIButton(this, "form-submit", null,
         {
            type: "submit"
         });
         this.widgets.cancelButton = Alfresco.util.createYUIButton(this, "form-cancel", this.onCancel);

         /* Defer initializing Forms Runtime until all controls loaded */
         this.controlsLoadedDeferred = new Alfresco.util.Deferred(["onPayloadControl", "onTransferTargetControl"],
         {
            fn: this.onControlsLoaded,
            scope: this
         });
         
         this.createPayloadControl(this.id + "-payloadContainer");
         this.createTransferTargetControl(this.id + "-transferTargetContainer");
      },
      
      /**
       * Generate the Payload control
       *
       * @method createPayloadControl
       * @param p_id {String} DOM ID of container element in which to render the control
       */
      createPayloadControl: function ReplicationJob_createPayloadControl(p_id)
      {
         this.widgets.payload = new Alfresco.module.ControlWrapper(Alfresco.util.generateDomId()).setOptions(
         {
            name: "payload",
            type: "association",
            container: Dom.get(p_id),
            label: this.msg("label.source-items"),
            value: this.options.payload.join(","),
            controlParams:
            {
               displayMode: "list",
               multipleSelectMode: true
            },
            fnValueChanged:
            {
               fn: function ReplicationJob_createPayloadControl_fnValueChanged(obj)
               {
                  // Object Finder returns added and removed, but we need selected nodeRefs
                  this.payload = null;
                  if (obj.selectedItems.length > 0)
                  {
                     this.payload = obj.selectedItems;
                  }
               },
               scope: this
            }
         });
         this.widgets.payload.show(
         {
            fn: function ReplicationJob_createPayloadControl_callback()
            {
               this.controlsLoadedDeferred.fulfil("onPayloadControl");
            },
            scope: this
         });
      },

      /**
       * Generate the Transfer Target control
       *
       * @method createTransferTargetControl
       * @param p_id {String} DOM ID of container element in which to render the control
       */
      createTransferTargetControl: function ReplicationJob_createTransferTargetControl(p_id)
      {
         this.widgets.transferTarget = new Alfresco.module.ControlWrapper(Alfresco.util.generateDomId()).setOptions(
         {
            name: "targetName",
            type: "association",
            container: Dom.get(p_id),
            label: this.msg("label.transfer-to"),
            value: this.options.targetName,
            controlParams:
            {
               parentXPath: "/app:company_home/app:dictionary/app:transfers/app:transfer_groups/cm:default",
               valueType: "xpath;/app:company_home/app:dictionary/app:transfers/app:transfer_groups//cm:%VALUE%"
            },
            field:
            {
               endpointType: "trx:transferTarget",
               endpointMany: false
            },
            fnValueChanged:
            {
               fn: function ReplicationJob_createTransferTargetControl_fnValueChanged(obj)
               {
                  // Object Finder returns nodeRefs by default, but we need the name for Transfer Target
                  this.targetName = null;
                  if (obj.selectedItems.length === 1)
                  {
                     this.targetName = obj.selectedItemsMetaData[obj.selectedItems[0]].name;
                  }
               },
               scope: this
            }
         });
         this.widgets.transferTarget.show(
         {
            fn: function ReplicationJob__createTransferTargetControl_callback()
            {
               this.controlsLoadedDeferred.fulfil("onTransferTargetControl");
            },
            scope: this
         });
      },

      /**
       * Form controls loaded deferred event handler
       *
       * @method onControlsLoaded
       */
      onControlsLoaded: function ReplicationJob_onControlsLoaded()
      {
         // Form definition
         this.form = new Alfresco.forms.Form(this.id + "-form");
         
         // Validators
         this.form.addValidation(this.id + "-prop_name", Alfresco.forms.validation.mandatory, null, "blur");
         this.form.addValidation(this.id + "-prop_name", Alfresco.forms.validation.nodeName, null, "keyup");
         this.form.addValidation(this.id + "-prop_name", Alfresco.forms.validation.length,
         {
            max: 256,
            crop: true
         }, "keyup");
         this.form.addValidation(this.id + "-prop_description", Alfresco.forms.validation.length,
         {
            max: 512,
            crop: true
         }, "keyup");

         this.form.setSubmitElements(this.widgets.submitButton);
         this.form.setAJAXSubmit(true,
         {
            successCallback:
            {
               fn: this.onSuccess,
               scope: this
            },
            failureCallback:
            {
               fn: this.onFailure,
               scope: this
            }
         });
         this.form.setSubmitAsJSON(true);
         this.form.setAjaxSubmitMethod(this.isCreateMode() ? "POST" : "PUT");
         this.form.setShowSubmitStateDynamically(true, false);
         
         // Intercept data just before AJAX submission
         this.form.doBeforeAjaxRequest =
         {
            fn: this.doBeforeAjaxRequest,
            scope: this
         };

         // Initialise the form
         this.form.init();
      },

      /**
       * Interceptor just before Ajax request is sent
       *
       * @method doBeforeAjaxRequest
       * @param p_config {object} Object literal containing request config
       * @return {boolean} True to continue sending form, False to prevent it
       */
      doBeforeAjaxRequest: function ReplicationJob_doBeforeAjaxRequest(p_config)
      {
         // Clean-up the auto-generated form output before ajax submission
         p_config.dataObj.payload = this.payload || [];
         delete p_config.dataObj["payload_added"];
         delete p_config.dataObj["payload_removed"];
         if (this.targetName !== null)
         {
            p_config.dataObj.targetName = this.targetName;
         }
         delete p_config.dataObj["targetName_added"];
         delete p_config.dataObj["targetName_removed"];
         p_config.dataObj["enabled"] = (p_config.dataObj["enabled"] == "true");
         return true;
      },

      /**
       * Cancel button click handler.
       *
       * @method onCancel
       * @param type {string} Event type
       * @param args {object} Event arguments
       */
      onCancel: function ReplicationJob_onCancel(type, args)
      {
         this._navigateForward();
      },
      
      /**
       * Success handler
       *
       * @method onSuccess
       * @param response {object} The response from the ajax request
       */
      onSuccess: function ReplicationJob_onSuccess(response)
      {
         this._navigateForward(response.json.data.name);
      },
      
      /**
       * Failure handler
       *
       * @method onFailure
       * @param response {object} The response from the ajax request
       */
      onFailure: function ReplicationJob_onFailure(response)
      {
         Alfresco.util.PopupManager.displayPrompt(
         {
            title: this.msg("header." + this.isCreateMode() ? "create" : "edit"),
            text: (response.json && response.json.message ? response.json.message : this.msg("message.unknown-error"))
         });
      },
      
      /**
       * Navigate forward depending on the page outcome
       *
       * @method _navigateForward
       * @param jobName {string} Optional job name parameter to pass forward
       * @protected
       */
      _navigateForward: function ReplicationJob__navigateForward(jobName)
      {
         var url = Alfresco.util.uriTemplate("consoletoolpage",
         {
            pageid: "admin-console",
            toolid: "replication-jobs"
         });

         if (YAHOO.lang.isString(jobName))
         {
            url += "?jobName=" + encodeURIComponent(jobName);
         }
         else if (history.length > 1)
         {
            // Do we have a browser history? If so, go back a page
            history.go(-1);
         }

         window.location.href = url;
      }
   });
})();
