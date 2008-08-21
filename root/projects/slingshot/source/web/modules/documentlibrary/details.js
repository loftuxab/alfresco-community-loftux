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
 * Document Library "Details" module for Document Library.
 * 
 * @namespace Alfresco.module
 * @class Alfresco.module.DoclibDetails
 */
(function()
{
   /**
   * YUI Library aliases
   */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      Element = YAHOO.util.Element;

   Alfresco.module.DoclibDetails = function(htmlId)
   {
      // Mandatory properties
      this.name = "Alfresco.module.DoclibDetails";
      this.id = htmlId;

      // Initialise prototype properties
      this.widgets = {};
      this.modules = {};

      // Load YUI Components
      Alfresco.util.YUILoaderHelper.require(["button", "container", "connection", "json"], this.onComponentsLoaded, this);

      return this;
   };
   
   Alfresco.module.DoclibDetails.prototype =
   {
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
          */
         siteId: "",

         /**
          * ContainerId representing root container
          *
          * @property containerId
          * @type string
          * @default "documentLibrary"
          */
         containerId: "documentLibrary",

         /**
          * Current path being browsed.
          * 
          * @property currentPath
          * @type string
          */
         currentPath: "",

         /**
          * Object literal representing item to edit details for.
          * 
          * @property file
          * @type object
          */
         file: null,

         /**
          * Width for the dialog
          *
          * @property: width
          * @type: integer
          * @default: 40em
          */
         width: "40em"
      },
      
      /**
       * Object container for storing YUI widget instances.
       * 
       * @property widgets
       * @type object
       */
      widgets: null,

      /**
       * Object container for storing module instances.
       * 
       * @property modules
       * @type object
       */
      modules: null,

      /**
       * Container element for template in DOM.
       * 
       * @property containerDiv
       * @type DOMElement
       */
      containerDiv: null,

      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.module.DoclibDetails} returns 'this' for method chaining
       */
      setOptions: function DLD_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },

      /**
       * Set messages for this component.
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {Alfresco.module.DoclibDetails} returns 'this' for method chaining
       */
      setMessages: function DLD_setMessages(obj)
      {
         Alfresco.util.addMessages(obj, this.name);
         return this;
      },

      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function DLD_onComponentsLoaded()
      {
      },

      /**
       * Main entry point
       * @method showDialog
       */
      showDialog: function DLD_showDialog()
      {
         // DocLib Actions module
         if (!this.modules.actions)
         {
            this.modules.actions = new Alfresco.module.DoclibActions();
         }
         
         if (!this.containerDiv)
         {
            // Load the UI template from the server
            Alfresco.util.Ajax.request(
            {
               url: Alfresco.constants.URL_SERVICECONTEXT + "modules/documentlibrary/details",
               dataObj:
               {
                  htmlid: this.id
               },
               successCallback:
               {
                  fn: this.onTemplateLoaded,
                  scope: this
               },
               failureMessage: "Could not load Document Library Details template",
               execScripts: true
            });
         }
         else
         {
            // Show the dialog
            this._showDialog();
         }
      },

      /**
       * Event callback when dialog template has been loaded
       *
       * @method onTemplateLoaded
       * @param response {object} Server response from load template XHR request
       */
      onTemplateLoaded: function DLD_onTemplateLoaded(response)
      {
         // Inject the template from the XHR request into a new DIV element
         this.containerDiv = document.createElement("div");
         this.containerDiv.setAttribute("style", "display:none");
         this.containerDiv.innerHTML = response.serverResponse.responseText;

         // The panel is created from the HTML returned in the XHR request, not the container
         var dialogDiv = Dom.getFirstChild(this.containerDiv);
         while (dialogDiv && dialogDiv.tagName.toLowerCase() != "div")
         {
            dialogDiv = Dom.getNextSibling(dialogDiv);
         }
         
         // Create and render the YUI dialog
         this.widgets.dialog = new YAHOO.widget.Panel(dialogDiv,
         {
            modal: true,
            draggable: false,
            fixedcenter: true,
            close: true,
            visible: false,
            width: this.options.width
         });
         this.widgets.dialog.render(document.body);
         
         // OK button
         this.widgets.okButton = Alfresco.util.createYUIButton(this, "ok", null,
         {
            type: "submit"
         });

         // Cancel button
         this.widgets.cancelButton = Alfresco.util.createYUIButton(this, "cancel", this.onCancel);

         // Form definition
         this.modules.form = new Alfresco.forms.Form(this.id + "-form");

         // Validation
         // Name: mandatory value
         this.modules.form.addValidation(this.id + "-name", Alfresco.forms.validation.mandatory, null, "keyup");
         // Name: valid filename
         this.modules.form.addValidation(this.id + "-name", Alfresco.forms.validation.nodeName, null, "keyup");
         this.modules.form.setShowSubmitStateDynamically(true, false);

         // OK button submits the form
         this.modules.form.setSubmitElements(this.widgets.okButton);

         // JSON submit type
         this.modules.form.setAJAXSubmit(true,
         {
            successCallback:
            {
               fn: this.onSuccess,
               scope: this
            },
            failureMessage: this._msg("message.details.failure")
         });
         this.modules.form.setSubmitAsJSON(true);

         // Show the dialog
         this._showDialog();
      },

      /**
       * Details form submit success handler
       *
       * @method onSuccess
       * @param response {object} Server response object
       */
      onSuccess: function DLD_onSuccess(response)
      {
         // Reload the node's metadata
         Alfresco.util.Ajax.request(
         {
            url: Alfresco.constants.PROXY_URI + "slingshot/doclib/doclist/all/node/" + this.options.file.nodeRef.replace(":/", "") + "?filter=node",
            successCallback:
            {
               fn: this.onMetadataSuccess,
               scope: this
            },
            failureCallback:
            {
               fn: this.onMetadataFailed,
               scope: this
            }
         });
         
      },

      /**
       * Metadata refresh success handler
       *
       * @method onMetadataSuccess
       * @param response {object} Server response object
       */
      onMetadataSuccess: function DLD_onMetadataSuccess(response)
      {
         var file = response.json.items[0];

         // Fire "renamed" event
         YAHOO.Bubbling.fire(file.type == "folder" ? "folderRenamed" : "fileRenamed",
         {
            file: file
         });

         // Fire "tagRefresh" event
         YAHOO.Bubbling.fire("tagRefresh");

         // Display success message
         Alfresco.util.PopupManager.displayMessage(
         {
            text: this._msg("message.details.success")
         });
         
         this._hideDialog();
      },

      /**
       * Metadata refresh failure handler
       *
       * @method onMetadataFailed
       * @param response {object} Server response object
       */
      onMetadataFailed: function DLD_onMetadataFailed(response)
      {
         // Display success message anyway
         Alfresco.util.PopupManager.displayMessage(
         {
            text: this._msg("message.details.success")
         });
         
         // Fire doclistRefresh event
         YAHOO.Bubbling.fire("doclistRefresh");
         
         this._hideDialog();
      },
      

      /**
       * YUI WIDGET EVENT HANDLERS
       * Handlers for standard events fired from YUI widgets, e.g. "click"
       */

      /**
       * Dialog Cancel button event handler
       *
       * @method onCancel
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onCancel: function DLD_onCancel(e, p_obj)
      {
         this._hideDialog();
      },


      /**
       * PRIVATE FUNCTIONS
       */

      /**
       * Internal show dialog function
       * @method _showDialog
       */
      _showDialog: function DLD__showDialog()
      {
         var file = this.options.file;

         // Grab the form element
         var formElement = Dom.get(this.id + "-form");
         
         // Set-up the form action
         var actionUrl = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "api/metadata/node/{nodeRef}",
         {
            nodeRef: file.nodeRef.replace(":/", "")
         });
         formElement.attributes.action.nodeValue = actionUrl;
         
         // Dialog title
         var titleDiv = Dom.get(this.id + "-title");
         var fileSpan = '<span class="light">' + file.displayName + '</span>';
         titleDiv.innerHTML = this._msg("title", fileSpan)

         // Item details
         Dom.get(this.id + "-name").value = file.fileName ? file.fileName : "";
         Dom.get(this.id + "-title").value = file.title ? file.title : "";
         Dom.get(this.id + "-description").value = file.description ? file.description : "";
         Dom.get(this.id + "-tags").value = file.tags.join(" ");
         if (file.type == "document")
         {
            var option = YAHOO.util.Selector.query("option[value=\"" + file.mimetype + "\"]", this.id + "-mimetype")[0];
            if (option)
            {
               Dom.get(this.id + "-mimetype").selectedIndex = option.index;
               Dom.get(this.id + "-mimetype").name = "mimetype";
            }
            Dom.removeClass(this.id + "-mimetype-field", "hidden");
         }
         else
         {
            Dom.addClass(this.id + "-mimetype-field", "hidden");
            Dom.get(this.id + "-mimetype").name = "-";
         }

         // Initialise the form
         this.modules.form.init();

         // Show the dialog
         this.widgets.dialog.show();

         // Fix Firefox caret issue
         Alfresco.util.caretFix(this.id + "-form");

         // We're in a popup, so need the tabbing fix
         this.modules.form.applyTabFix();
         
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

         // Set focus to fileName input
         Dom.get(this.id + "-name").focus();
      },

      /**
       * Hide the dialog, removing the caret-fix patch
       *
       * @method _hideDialog
       * @private
       */
      _hideDialog: function DLD__hideDialog()
      {
         // Grab the form element
         var formElement = Dom.get(this.id + "-form");

         // Undo Firefox caret issue
         Alfresco.util.undoCaretFix(formElement);
         this.widgets.dialog.hide();
      },

      /**
       * Gets a custom message
       *
       * @method _msg
       * @param messageId {string} The messageId to retrieve
       * @return {string} The custom message
       * @private
       */
       _msg: function DLD__msg(messageId)
       {
          return Alfresco.util.message.call(this, messageId, this.name, Array.prototype.slice.call(arguments).slice(1));
       }
   };
})();

/* Dummy instance to load optional YUI components early */
new Alfresco.module.DoclibDetails(null);