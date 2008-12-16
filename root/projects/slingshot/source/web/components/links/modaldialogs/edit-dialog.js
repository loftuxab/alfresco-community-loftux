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
 * LinksEditDialog class.
 *
 * @namespace Alfresco
 * @class Alfresco.LinksEditDialog
 */
(function()
{
   var Dom = YAHOO.util.Dom;

   Alfresco.LinksEditDialog = function(htmlId)
   {
      this.name = "Alfresco.LinksEditDialog";
      this.id = htmlId;
      this.modules = {};

      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "container", "connection", "json", "selector"], this.onComponentsLoaded, this);

      return this;
   };

   Alfresco.LinksEditDialog.prototype =
   {
      /**
       * Dialog instance.
       *
       * @property dialog
       * @type YUI.widget.Panel
       */
      dialog: null,

      /**
       * Form instance.
       *
       * @property form
       * @type Alfresco.forms.Form
       */
      form: null,

      /**
       * Object container for storing YUI widget instances.
       *
       * @property widgets
       * @type object
       */
      widgets:
      {
      },

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
          * Current containerId.
          *
          * @property containerId
          * @type string
          * @default ""
          */
         containerId:"",

         /**
          * URL which will return template body HTML
          *
          * @property templateUrl
          * @type string
          * @default null
          */
         templateUrl: null,

         /**
          * ID of form element to receive focus on show
          *
          * @property firstFocus
          * @type string
          * @default null
          */
         firstFocus: null,

         /**
          * Width for the dialog
          *
          * @property: width
          * @type: integer
          * @default: 30em
          */
         width: "40em",

         /**
          * Clear the form before showing it?
          *
          * @property: clearForm
          * @type: boolean
          * @default: false
          */
         clearForm: false,

         /**
          * Defines view. If true - edit dialog, otherwise - false
          *
          * @property: editMode
          * @type: boolean
          * @default: false
          */
         editMode: false,

         /**
          * The messge of current operation, for exapmle update/create
          *
          * @property: feedbackMessage
          * @type: string
          * @default: null
          */
         feedbackMessage: null

      },

      /**
       * Object container for storing module instances.
       *
       * @property modules
       * @type object
       */
      modules: null,

      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function LinksEditDialog_onComponentsLoaded()
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
      setOptions: function LinksEditDialog_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },

      /**
       * Inits the modal dialog.
       *
       * @method  init
       */
      init: function LinksEditDialog_init()
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
            failureMessage: this._msg("dialog.message.failure") + "'" + this.options.templateUrl + "'.",
            scope: this,
            execScripts: true
         });
      },

      /**
       * Main entrypoint to show the dialog
       *
       * @param data {object} the editable data
       * @method show
       */
      show: function LinksEditDialog_show(data)
      {
         if (this.dialog)
         {
            this._showDialog(data);
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
               failureMessage: this._msg("dialog.message.failure") + "'" + this.options.templateUrl + "'.",
               scope: this,
               execScripts: true
            });
         }
         return this;
      },         

      /**
       * Event callback when dialog template has been loaded
       *
       * @method onTemplateLoaded
       * @param response {object} Server response from load template XHR request
       */
      onTemplateLoaded: function LinksEditDialog_onTemplateLoaded(response)
      {
         // Inject the template from the XHR request into a new DIV element
         var containerDiv = document.createElement("div");
         containerDiv.innerHTML = response.serverResponse.responseText;

         // The panel is created from the HTML returned in the XHR request, not the container
         var dialogDiv = Dom.getFirstChild(containerDiv);
         while (dialogDiv && dialogDiv.tagName.toLowerCase() != "div")
         {
            dialogDiv = Dom.getNextSibling(dialogDiv);
         }

         // Create and render the YUI dialog
         this.dialog = new YAHOO.widget.Panel(dialogDiv,
         {
            modal: true,
            draggable: false,
            fixedcenter: true,
            close: false,
            visible: false,
            width: this.options.width
         });
         this.dialog.render(document.body);

		 this.dialog.subscribe("beforeShow",function(){this.element.style.display = "";});
		 this.dialog.subscribe("beforeHide",function(){this.element.style.display = "none";});          

		 // OK button
         this.widgets.okButton = Alfresco.util.createYUIButton(this, "ok-button", null, {type: "submit"});

         // Cancel button
         this.widgets.cancelButton = Alfresco.util.createYUIButton(this, "cancel-button", this.onCancel);

         // Form definition
         this.form = new Alfresco.forms.Form(this.id + "-form");

         this.form.addValidation(this.id + "-editdlg-title", Alfresco.forms.validation.mandatory, null, "keyup");

         this.form.addValidation(this.id + "-editdlg-url", Alfresco.forms.validation.mandatory, null, "keyup");

         this.modules.tagLibrary = new Alfresco.module.TagLibrary(this.id);
         this.modules.tagLibrary.setOptions({ siteId: this.options.siteId });
         this.modules.tagLibrary.initialize();

         // The ok button is the submit button, and it should be enabled when the form is ready
         this.form.setShowSubmitStateDynamically(true, false);
         this.form.setSubmitElements(this.widgets.okButton);
         this.form.setSubmitAsJSON(true);
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

         this.form.doBeforeFormSubmit =
         {
            fn: function(form, obj)
            {
               // update the tags set in the form
               this.modules.tagLibrary.updateForm(this.id + "-form", "tags");
               this.widgets.okButton.set("disabled", true);
               this.widgets.feedbackMessage = Alfresco.util.PopupManager.displayMessage(
               {
                  text: this.options.feedbackMessage,
                  spanClass: "wait",
                  displayTime: 0
               });
               this._hideDialog();
               this.widgets.feedbackMessage.show();
            },
            obj: null,
            scope: this
         }

         this.options.firstFocus = this.id + "-editdlg-title";

         // Initialise the form
         this.form.init();
      },

      /**
       * Cancel button event handler
       *
       * @method onCancel
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onCancel: function LinksEditDialog_onCancel(e, p_obj)
      {
         this._hideDialog();
      },

      /**
       * Failure event handler
       *
       * @method onFailure
       * @param response {object} Server response object
       */
      onFailure: function LinksEditDialog_onFailure(response)
      {
         this.widgets.feedbackMessage.destroy();
         this.form.updateSubmitElements();
         this._hideDialog();
         Alfresco.util.PopupManager.displayPrompt(
         {
            text: this._msg("dialog.message.opfailed"),
            close: false,
            buttons: [
               {
                  text: this._msg("dialog.promt.confirm"),
                  handler: function()
                  {
                     this.destroy();
                  },
                  isDefault: false
               }]
         });
      },

      /**
       * Successful data webscript call event handler
       *
       * @method onSuccess
       * @param response {object} Server response object
       */
      onSuccess: function LinksEditDialog_onSuccess(response)
      {
         this.widgets.feedbackMessage.destroy();
         this.form.updateSubmitElements();
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
                  text: this._msg("dialog.message.opfailed")
               });
            }
         }
         else
         {
            // Invoke the callback if one was supplied
            if (typeof this.options.onSuccess.fn == "function")
            {
               this.options.onSuccess.fn.call(this.options.onSuccess.scope, response, this.options.onSuccess.obj);
            }
            else
            {
               Alfresco.util.PopupManager.displayMessage(
               {
                  text: this._msg("dialog.message.opsucceeded")
               });
            }
         }
      },         

      /**
       * Fills editable fields.
       *
       * @param inputs {Array} the form fields.
       * @param data {object} the value of fields.
       * @method fillFormsInput
       */
      fillFormsInputs: function LinksEditDialog_fillFormsInputs(inputs, data)
      {
         //adjust inputs value
         for (var i = 0, j = inputs.length; i < j; i++)
         {
            switch (inputs[i].name)
            {
               case "title": inputs[i].value = data.title; break;
               case "description": inputs[i].value = data.description; break;
               case "url": inputs[i].value = data.url; break;
               case "isinternal": inputs[i].checked = eval(data.internal); break;
            }
         }
      },

       /**
       * PRIVATE FUNCTIONS
       */      

      /**
       * Show the dialog and set focus to the first text field
       *
       * @param data {object} the editable data
       * @method _showDialog
       * @private
       */
      _showDialog: function LinksEditDialog__showDialog(data)
      {   
          var actionUrl = "";
          if(this.options.editMode){
             actionUrl = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "api/links/site/{site}/{container}/{path}",
             {
                 site: this.options.siteId,
                 container : this.options.containerId,
                 path:data.name
             });
          }
          else
          {
             actionUrl = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "api/links/site/{site}/{container}/posts",
             {
                 site: this.options.siteId,
                 container : this.options.containerId
             });
          }

          var form = Dom.get(this.id + '-form');
          form.setAttribute("action", actionUrl);

          if (this.options.editMode)
          {
             this.form.setAjaxSubmitMethod(Alfresco.util.Ajax.PUT);
          }
          else
          {
             this.form.setAjaxSubmitMethod(Alfresco.util.Ajax.POST);
          }

         if (this.options.clearForm)
         {
            var inputs = YAHOO.util.Selector.query("input", form);
            inputs = inputs.concat(YAHOO.util.Selector.query("textarea", form));
            for (var i = 0, j = inputs.length; i < j; i++)
            {
               if ("defaultMessage" == inputs[i].name)continue;

               inputs[i].value = "";
            }

            if (this.options.editMode && (data))
            {
               this.fillFormsInputs(inputs, data);
            }

         }
         this.modules.tagLibrary._hidePopularTags(); 
         this.modules.tagLibrary.setTags((this.options.editMode && data.tags.length > 0) ? data.tags: []);

          
         this.dialog.show();

         // Fix Firefox caret issue
         Alfresco.util.caretFix(form);

         // We're in a popup, so need the tabbing fix
         this.form.applyTabFix();

         // Register the ESC key to close the dialog
         var escapeListener = new YAHOO.util.KeyListener(document,
         {
            keys: YAHOO.util.KeyListener.KEY.ESCAPE
         },
         {
            fn: function(id, keyEvent)
            {
               this._hideDialog();
            },
            scope: this,
            correctScope: true
         });
         escapeListener.enable();

         // Set focus if required
         if (this.options.firstFocus !== null)
         {
            Dom.get(this.options.firstFocus).focus();
         }

         if (this.options.editMode)
         {
            Dom.get(this.id + "-editdlg-header").innerHTML = this._msg("dialog.header.edit");
            this.options.feedbackMessage = this._msg("dialog.message.updating");
            this.widgets.okButton.set("disabled", false);
         }
         else
         {
            Dom.get(this.id + "-editdlg-header").innerHTML = this._msg("dialog.header.new");
            this.options.feedbackMessage = this._msg("dioalog.message.creating");
            this.widgets.okButton.set("disabled", true);
            Dom.get(this.id + "-editdlg-url-checkbox").checked = false;
         }
      },

      /**
       * Hide the dialog, removing the caret-fix patch
       *
       * @method _hideDialog
       * @private
       */
      _hideDialog: function LinksEditDialog__hideDialog()
      {
         var form = Dom.get(this.id + "-form");
         // Undo Firefox caret issue
         Alfresco.util.undoCaretFix(form);
         this.dialog.hide();
      },

      /**
       * Message
       */
      _msg:function LinksEditDialog_msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, this.name, Array.prototype.slice.call(arguments).slice(1));
      }
   };
})();