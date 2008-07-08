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
 * SimpleDialog module.
 * 
 * @namespace Alfresco.module
 * @class Alfresco.module.SimpleDialog
 */
(function()
{
   Alfresco.module.SimpleDialog = function(htmlId)
   {
      this.name = "Alfresco.module.SimpleDialog";
      this.id = htmlId;
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "container", "connection", "json", "selector"], this.onComponentsLoaded, this);

      return this;
   };

   Alfresco.module.SimpleDialog.prototype =
   {
      /**
       * Dialog instance.
       * 
       * @property dialog
       * @type YUI.widget.Panel
       */
      dialog: null,

      /**
       * Object container for storing YUI widget instances.
       * 
       * @property widgets
       * @type object
       */
       widgets: {},

       /**
        * Object container for initialization options
        */
       options:
       {
          /**
           * URL which will return template body HTML
           *
           * @property templateUrl
           * @type string
           * @default null
           */
          templateUrl: null,

          /**
           * URL of the form action
           *
           * @property actionUrl
           * @type string
           * @default null
           */
          actionUrl: null,

          /**
           * Object literal representing callback upon successful operation.
           *   fn: function, // The handler to call when the event fires.
           *   obj: object, // An object to pass back to the handler.
           *   scope: object // The object to use for the scope of the handler.
           *
           * @property onSuccessCallback
           * @type object
           * @default null
           */
          onSuccessCallback:
          {
             fn: null,
             obj: null,
             scope: window
          },

          /**
           * Message to display on successful operation
           *
           * @property onSuccessMessage
           * @type string
           * @default ""
           */
          onSuccessMessage: "",
          
          /**
           * Object literal representing callback upon failed operation.
           *   fn: function, // The handler to call when the event fires.
           *   obj: object, // An object to pass back to the handler.
           *   scope: object // The object to use for the scope of the handler.
           *
           * @property onFailureCallback
           * @type object
           * @default null
           */
          onFailureCallback:
          {
             fn: null,
             obj: null,
             scope: window
          },

          /**
           * Message to display on failed operation
           *
           * @property onFailureMessage
           * @type string
           * @default ""
           */
          onFailureMessage: "",
          
          /**
           * Object literal representing function to set forms validation.
           *   fn: function, // The handler to call when the event fires.
           *   obj: object, // An object to pass back to the handler.
           *   scope: object // The object to use for the scope of the handler.
           *
           * @property doSetupFormsValidation
           * @type object
           * @default null
           */
          doSetupFormsValidation:
          {
             fn: null,
             obj: null,
             scope: window
          }
       },

      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function AmSD_onComponentsLoaded()
      {
         // Shortcut for dummy instance
         if (this.id === null)
         {
            return;
         }
      },
      
      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       */
      setOptions: function AmSD_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },
      
      /**
       * Main entrypoint to show the dialog
       *
       * @method show
       */
      show: function AmSD_show()
      {
         if (this.dialog)
         {
            this._showDialog();
         }
         else
         {
            Alfresco.util.Ajax.request(
            {
               url: this.options.templateUrl,
               dataObj:
               {
                  htmlid: this.id
               },
               successCallback:
               {
                  fn: this.onTemplateLoaded,
                  scope: this
               },
               failureMessage: "Could not load dialog template from '" + this.options.templateUrl + "'.",
               scope: this
            });
         }
         return this;
      },
      
      /**
       * Show the dialog and set focus to the first text field
       *
       * @method showDialog
       * @private
       */
      _showDialog: function AmSD__showDialog()
      {
         var form = YAHOO.util.Dom.get(this.id + "-form");
         
         if (this.options.actionUrl !== null)
         {
            form.attributes.action.nodeValue = this.options.actionUrl;
         }

         this.dialog.show();

         // Fix Firefox caret issue
         Alfresco.util.firefoxCaretFix(form);
         
         // Set focus if required
         if (this.options.firstFocus !== null)
         {
            YAHOO.util.Dom.get(this.options.firstFocus).focus();
         }
      },
      
      /**
       * Event callback when dialog template has been loaded
       *
       * @method onTemplateLoaded
       * @param response {object} Server response from load template XHR request
       */
      onTemplateLoaded: function AmSD_onTemplateLoaded(response)
      {
         // Inject the template from the XHR request into a new DIV element
         var containerDiv = document.createElement("div");
         containerDiv.innerHTML = response.serverResponse.responseText;

         // The panel is created from the HTML returned in the XHR request, not the container
         var dialogDiv = YAHOO.util.Dom.getFirstChild(containerDiv);

         // Create and render the YUI dialog
         this.dialog = new YAHOO.widget.Panel(dialogDiv,
         {
            modal: true,
            draggable: false,
            fixedcenter: true,
            close: true,
            visible: false
         });
         this.dialog.render(document.body);
         
         // OK button
         this.widgets.okButton = Alfresco.util.createYUIButton(this, "ok", null,
         {
            type: "submit"
         });

         // Cancel button
         this.widgets.cancelButton = Alfresco.util.createYUIButton(this, "cancel", this.onCancel);

         // Form definition
         var form = new Alfresco.forms.Form(this.id + "-form");
         form.setShowSubmitStateDynamically(true, false);
         form.setSubmitElements(this.widgets.okButton);
         form.setAJAXSubmit(true,
         {
            successCallback:
            {
               fn: this.onSuccess,
               scope: this
            }
         });
         form.setSubmitAsJSON(true);

         // Custom forms validation setup interest registered?
         var doSetupFormsValidation = this.options.doSetupFormsValidation;
         if (typeof doSetupFormsValidation.fn == "function")
         {
            doSetupFormsValidation.fn.call(doSetupFormsValidation.scope || this, form, doSetupFormsValidation.obj);
         }

         // Initialise the form
         form.init();

         this._showDialog();
      },

      /**
       * Cancel button event handler
       *
       * @method onCancel
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onCancel: function AmSD_onCancel(e, p_obj)
      {
        this.dialog.hide();
      },

      /**
       * Successful folder creation event handler
       *
       * @method onSuccess
       * @param response {object} Server response object
       */
      onSuccess: function AmSD_onSuccess(response)
      {
         this.dialog.hide();

         if (!response)
         {
            // Invoke the callback if one was supplied
            if (typeof this.options.onFailure.fn == "function")
            {
               this.options.onFailure.fn.call(this.options.onFailure.scope, this.options.onFailure.obj);
            }
            else
            {
               Alfresco.util.PopupManager.displayMessage(
               {
                  text: "Operation failed."
               });
            }
         }
         else
         {
            // Invoke the callback if one was supplied
            if (typeof this.options.onSuccess.fn == "function")
            {
               this.options.onSuccess.fn.call(this.options.onSuccess.scope, this.options.onSuccess.obj);
            }
            else
            {
               Alfresco.util.PopupManager.displayMessage(
               {
                  text: "Operation succeeded."
               });
            }
         }
      }

   };
})();

/* Dummy instance to load optional YUI components early */
new Alfresco.module.SimpleDialog(null);