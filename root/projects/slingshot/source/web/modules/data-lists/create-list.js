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
 * CreateList module
 *
 * A dialog for creating lists
 *
 * @namespace Alfresco.module
 * @class Alfresco.module.CreateList
 */
(function()
{
   
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      KeyListener = YAHOO.util.KeyListener;
   
   /**
    * CreateList constructor.
    *
    * @param htmlId {string} A unique id for this component
    * @return {Alfresco.CreateList} The new DocumentList instance
    * @constructor
    */
   Alfresco.module.CreateList = function(containerId)
   {
      var instance = Alfresco.util.ComponentManager.get(this.id);
      if (instance !== null)
      {
         throw new Error("An instance of Alfresco.module.CreateList already exists.");
      }

      Alfresco.module.CreateList.superclass.constructor.call(this, "Alfresco.module.CreateList", containerId, ["button", "container", "connection", "selector", "json"]);

      return this;
   };

   YAHOO.extend(Alfresco.module.CreateList, Alfresco.component.Base,
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
        * Current siteId.
        * 
        * @property siteId
        * @type string
        * @default null
        */
         siteId: ""
      },
      /**
       * Shows the CreateList dialog to the user.
       *
       * @method show
       */
      show: function CS_show()
      {
         if (this.widgets.panel)
         {
            /**
             * The panel gui has been showed before and its gui has already
             * been loaded and created
             */
            this._showPanel();
         }
         else
         {
            /**
             * Load the gui from the server and let the templateLoaded() method
             * handle the rest.
             */
            Alfresco.util.Ajax.request(
            {
               url: Alfresco.constants.URL_SERVICECONTEXT + "modules/data-lists/create-list",
               dataObj:
               {
                  htmlid: this.id
               },
               successCallback:
               {
                  fn: this.onTemplateLoaded,
                  scope: this
               },
               execScripts: true,
               failureMessage: "Could not load create list template"
            });
         }
      },

      /**
       * Called when the CreateList html template has been returned from the server.
       * Creates the YUI gui objects such as buttons and a panel and shows it.
       *
       * @method onTemplateLoaded
       * @param response {object} a Alfresco.util.Ajax.request response object 
       */
      onTemplateLoaded: function CS_onTemplateLoaded(response)
      {
         // Inject the template from the XHR request into a new DIV element
         var containerDiv = document.createElement("div");
         containerDiv.innerHTML = response.serverResponse.responseText;

         // The panel is created from the HTML returned in the XHR request, not the container
         var panelDiv = Dom.getFirstChild(containerDiv);

         this.widgets.panel = Alfresco.util.createYUIPanel(panelDiv);

         // Create the cancel button
         this.widgets.cancelButton = Alfresco.util.createYUIButton(this, "cancel-button", this.onCancelButtonClick);

         // Create the ok button, the forms runtime will handle when its clicked
         this.widgets.okButton = Alfresco.util.createYUIButton(this, "ok-button", null,
         {
            type: "submit"
         });
         
         // List access form controls
         this.widgets.isDefault = Dom.get(this.id + "-datalist-default");

         // Configure the forms runtime
         var CreateListForm = new Alfresco.forms.Form(this.id + "-form");

         // name is mandatory
         CreateListForm.addValidation(this.id + "-name", Alfresco.forms.validation.mandatory, null, "blur");
         // and can NOT contain whitespace characters
         CreateListForm.addValidation(this.id + "-name", Alfresco.forms.validation.regexMatch,
         {
            pattern: /^[ ]*[0-9a-zA-Zs]+[ ]*$/
         }, "keyup");

         // The ok button is the submit button, and it should be enabled when the form is ready
         CreateListForm.setShowSubmitStateDynamically(true, false);
         CreateListForm.setSubmitElements(this.widgets.okButton);
         CreateListForm.doBeforeFormSubmit =
         {
            fn: function()
            {
               var formEl = Dom.get(this.id + "-form");
               formEl.attributes.action.nodeValue = Alfresco.constants.URL_SERVICECONTEXT + "modules/data-lists/create-list"; 
               
               this.widgets.okButton.set("disabled", true);
               this.widgets.cancelButton.set("disabled", true);
               
               // List access
               var listDefault = false;
               if (this.widgets.isDefault.checked)
               {
                  listDefault = true;
               }
               
               this.widgets.panel.hide();
               this.widgets.feedbackMessage = Alfresco.util.PopupManager.displayMessage(
               {
                  text: Alfresco.util.message("message.creating", this.name),
                  spanClass: "wait",
                  displayTime: 0
               });
            },
            obj: null,
            scope: this
         };

         // Submit as an ajax submit (not leave the page), in json format
         CreateListForm.setAJAXSubmit(true,
         {
            successCallback:
            {
               fn: this.onCreateListSuccess,
               scope: this               
            },
            failureCallback:
            {
               fn: this.onCreateListFailure,
               scope: this
            }
         });
         CreateListForm.setSubmitAsJSON(true);
         // We're in a popup, so need the tabbing fix
         CreateListForm.applyTabFix();
         CreateListForm.init();

         // Show the panel
         this._showPanel();
      },

      /**
       * Called when user clicks on the cancel button.
       * Closes the CreateList panel.
       *
       * @method onCancelButtonClick
       * @param type
       * @param args
       */
      onCancelButtonClick: function CS_onCancelButtonClick(type, args)
      {
         this.widgets.panel.hide();
      },

      /**
       * Called when a list has been succesfully created on the server.
       * Redirects the user to the new list.
       *
       * @method onCreateListSuccess
       * @param response
       */
      onCreateListSuccess: function CS_onCreateListSuccess(response)
      {
         if (response.json !== undefined && response.json.success)
         {
            // redirect user to newly created datalist
            document.location.href = Alfresco.constants.URL_CONTEXT + "page/site/"+this.options.siteId+"/data-lists?list="+response.json.listId;
         }
         else
         {
            this._adjustGUIAfterFailure(response);
         }
      },

      /**
       * Called when a list failed to be created.
       *
       * @method onCreateListFailure
       * @param response
       */
      onCreateListFailure: function CS_onCreateListFailure(response)
      {
         this._adjustGUIAfterFailure(response);
      },

      /**
       * Helper method that restores the gui and displays an error message.
       *
       * @method _adjustGUIAfterFailure
       * @param response
       */
      _adjustGUIAfterFailure: function CS__adjustGUIAfterFailure(response)
      {
         this.widgets.feedbackMessage.destroy();
         this.widgets.okButton.set("disabled", false);
         this.widgets.cancelButton.set("disabled", false);
         this.widgets.panel.show();
         var text = Alfresco.util.message("message.failure", this.name);
         if (response.json.message)
         {
            var tmp = Alfresco.util.message(response.json.message, this.name);
            text = tmp ? tmp : text;
         }
         Alfresco.util.PopupManager.displayPrompt(
         {
            title: Alfresco.util.message("message.failure", this.name), 
            text: text
         });
      },

      /**
       * Prepares the gui and shows the panel.
       *
       * @method _showPanel
       * @private
       */
      _showPanel: function CS__showPanel()
      {
         // Show the upload panel
         this.widgets.panel.show();

         // Firefox insertion caret fix
         Alfresco.util.caretFix(this.id + "-form");

         // Register the ESC key to close the dialog
         var escapeListener = new KeyListener(document,
         {
            keys: KeyListener.KEY.ESCAPE
         },
         {
            fn: function(id, keyEvent)
            {
               this.onCancelButtonClick();
            },
            scope: this,
            correctScope: true
         });
         escapeListener.enable();

         // Set the focus on the first field
         Dom.get(this.id + "-datalists").focus();
      }
   });
})();

Alfresco.module.getCreateListInstance = function()
{
   var instanceId = "alfresco-CreateList-instance";
   return Alfresco.util.ComponentManager.get(instanceId) || new Alfresco.module.CreateList(instanceId);
};