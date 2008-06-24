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
 * CreateFolder module for Document Library.
 * 
 * @namespace Alfresco.module
 * @class Alfresco.module.CreateFolder
 */
(function()
{
   Alfresco.module.CreateFolder = function(htmlId)
   {
      this.name = "Alfresco.module.CreateFolder";
      this.id = htmlId;
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "container", "connection", "json", "selector"], this.onComponentsLoaded, this);

      return this;
   };

   Alfresco.module.CreateFolder.prototype =
   {
      /**
       * REST API template.
       * 
       * @property REST_API
       * @type string
       */
      REST_API: Alfresco.constants.PROXY_URI + "slingshot/doclib/action/folder/{siteid}/{containerId}/{filepath}",
      
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
           * Current siteId.
           * 
           * @property siteId
           * @type string
           * @default ""
           */
          siteId: "",

          /**
           * ContainerID defining the root container.
           * 
           * @property containerId
           * @type string
           * @default "documentLibrary"
           */
          containerId: "documentLibrary",

          /**
           * Parent path for the new folder.
           * 
           * @property parentPath
           * @type string
           * @default ""
           */
          parentPath: "",

          /**
           * Object literal representing callback upon successful operation.
           *   fn: function, // The handler to call when the event fires.
           *   obj: object, // An object to pass back to the handler.
           *   scope: object // The object to use for the scope of the handler.
           * @property onSuccess
           * @type object
           * @default null
           */
          onSuccess:
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
      onComponentsLoaded: function DLCF_onComponentsLoaded()
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
      setOptions: function DL_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },
      
      /**
       * Main entrypoint to show the create folder dialog
       *
       * @method show
       */
      show: function DLCF_show()
      {
         if (this.dialog)
         {
            this._showDialog();
         }
         else
         {
            Alfresco.util.Ajax.request(
            {
               url: Alfresco.constants.URL_SERVICECONTEXT + "modules/documentlibrary/create-folder",
               dataObj:
               {
                  htmlid: this.id
               },
               successCallback:
               {
                  fn: this.onTemplateLoaded,
                  scope: this
               },
               failureMessage: "Could not load create folder dialog template",
               scope: this
            });
         }
         return this;
      },
      
      /**
       * Show the create folder dialog and set focus to the first text field
       *
       * @method showDialog
       * @private
       */
      _showDialog: function DLCF__showDialog()
      {
         // Construct the REST API
         var filePath = this.options.parentPath;
         if (filePath[0] = "/")
         {
            filePath = filePath.substring(1);
         }
         var action = YAHOO.lang.substitute(this.REST_API,
         {
            siteid: this.options.siteId,
            containerId: this.options.containerId,
            filepath: filePath
         });
         
         form = YAHOO.util.Dom.get(this.id + "-form");
         form.attributes.action.nodeValue = action;

         this.dialog.show();

         // Firefox insertion caret fix
         Alfresco.util.caretFix(form);

         YAHOO.util.Dom.get(this.id + "-name").focus();
      },
      
      /**
       * Event callback when dialog template has been loaded
       *
       * @method onTemplateLoaded
       * @param response {object} Server response from load template XHR request
       */
      onTemplateLoaded: function DLCF_onTemplateLoaded(response)
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

         // Validation
         // Name: mandatory value
         form.addValidation(this.id + "-name", Alfresco.forms.validation.mandatory, null, "keyup");
         // Name: valid filename
         form.addValidation(this.id + "-name", Alfresco.forms.validation.nodeName, null, "keyup");

         form.setShowSubmitStateDynamically(true);
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
      onCancel: function DLCF_onCancel(e, p_obj)
      {
         this.dialog.hide();

         // Firefox 2 still has the hidden caret issue unless teh form is redrawn from scratch.
         if (YAHOO.env.ua.gecko == 1.8)
         {
            this.dialog.destroy();
            this.dialog = null;
         }
      },

      /**
       * Successful folder creation event handler
       *
       * @method onSuccess
       * @param response {object} Server response object
       */
      onSuccess: function DLCF_onSuccess(response)
      {
         // Success, so clear out old values
         YAHOO.util.Dom.get(this.id + "-name").value = "";
         YAHOO.util.Dom.get(this.id + "-title").value = "";
         YAHOO.util.Dom.get(this.id + "-description").value = "";

         this.dialog.hide();

         if (!response)
         {
            Alfresco.util.PopupManager.displayMessage(
            {
               text: "Create folder operation failed."
            });
         }
         else
         {
            var folder = response.json.results[0];
            // Invoke the callback if one was supplied
            if (typeof this.options.onSuccess.fn == "function")
            {
               this.options.onSuccess.fn.call(this.options.onSuccess.scope, folder, this.options.onSuccess.obj);
            }
         }
      }

   };
})();

/* Dummy instance to load optional YUI components early */
new Alfresco.module.CreateFolder(null);