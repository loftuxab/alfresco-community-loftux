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
 * SAMLSettings component.
 *
 * @namespace Alfresco
 * @class Alfresco.cloud.component.SAMLSettings
 */
(function()
{

   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML;

   /**
    * SAMLSettings constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.cloud.component.SAMLSettings} The new SAMLSettings instance
    * @constructor
    */
   Alfresco.cloud.component.SAMLSettings = function SAMLSettings_constructor(htmlId)
   {
      Alfresco.cloud.component.SAMLSettings.superclass.constructor.call(this, "Alfresco.cloud.component.SAMLSettings", htmlId, []);
      return this;
   };

   YAHOO.extend(Alfresco.cloud.component.SAMLSettings, Alfresco.component.Base,
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
          * Webscript to call for retrieving the SAML config settings.
          * Shall support json based input (for disabling using the given url)
          * and html multipart (for field & cerfitication updates using the the given url plus a "/multipart.html" suffix").
          *
          * @property maxItems
          * @type String
          */
         detailsWebscript: null
      },

      /**
       * Fired by YUI when parent element is available for scripting.
       * Initial History Manager event registration
       *
       * @method onReady
       */
      onReady: function CMU_onReady()
      {
         // Add listener to the form toggler
         Event.addListener(Dom.get(this.id + "-ssoEnabledButton"), "click", this.onSSOEnabledClicked, this, true);

         // Load SAML details
         this.loadDetails();
      },

      /**
       * Loads SAML settings
       *
       * @method loadDetails
       */
      loadDetails: function()
      {
         Alfresco.util.Ajax.jsonGet(
         {
            url: Alfresco.constants.PROXY_URI + this.options.detailsWebscript,
            successCallback:
            {
               fn: this.onLoadSuccess,
               scope: this
            },
            failureMessage: this.msg("load.message.failure")
         });
      },

      /**
       * Called when the SAML details have been successfully loaded
       *
       * @method onDetailsLoaded
       * @param response {Object} The response with SAML details
       */
      onLoadSuccess: function(response)
      {
         // Reset certificate state
         Dom.addClass(this.id + "-certificate-details-container", "hidden");
         Dom.addClass(this.id + "-certificate", "hidden");
         Dom.addClass(this.id + "-certificate-help-icon", "hidden");

         // The SAML details
         var details = response.json;

         if (details)
         {
            // Populate form values
            Dom.get(this.id + "-idpSsoURL").value = details.idpSsoURL || "";
            Dom.get(this.id + "-idpSloRequestURL").value = details.idpSloRequestURL || "";
            Dom.get(this.id + "-idpSloResponseURL").value = details.idpSloResponseURL || "";
            Dom.get(this.id + "-issuer").value = details.entityID;
            // Make sure share saml login url doesn't include a trailing slash
            var path = Alfresco.constants.URL_CONTEXT;
            path = path.match(/.*\/$/) ? path.substring(0, path.length - 1) : path;
            Dom.get(this.id + "-shareEntrypointUrl").value = window.location.protocol + "//" + window.location.host + path;

            // Set certificate state according to details
            if (details.certificate)
            {
               Dom.removeClass(this.id + "-certificate-details-container", "hidden");
               Dom.removeClass(this.id + "-certificate-status", "error");
               var status = details.certificate.status.toLowerCase();
               if (status == "expired")
               {
                  Dom.addClass(this.id + "-certificate-status", "error");
               }
               Dom.get(this.id + "-certificate-status").innerHTML = $html(this.msg("certificate.status.label", this.msg("certificate.status." + details.certificate.status.toLowerCase())));
               Dom.get(this.id + "-certificate-expires").innerHTML = $html(this.msg("certificate.expires.label", Alfresco.util.formatDate(Alfresco.util.fromISO8601(details.certificate.expiryDate.iso8601))));
            }
            else
            {
               Dom.removeClass(this.id + "-certificate", "hidden");
               Dom.removeClass(this.id + "-certificate-help-icon", "hidden");
            }

            if (details.ssoEnabled)
            {
               // Check the enabled button & display form
               Dom.get(this.id + "-ssoEnabledButton").checked = true;
               Dom.removeClass(this.id + "-form", "hidden");
            }
         }

         // Initialise the form if it hasn't been initialised already
         if (!this.widgets.form)
         {
            // Create a yui submit button
            this.widgets.saveButton = Alfresco.util.createYUIButton(this, "save-button", null,
            {
               type: "submit"
            });
            this.widgets.saveButton = Alfresco.util.createYUIButton(this, "reset-button", this.onResetClick, {});

            var form = new Alfresco.forms.Form(this.id + "-form");
            this.widgets.form = form;

            // Target form against the webscript supporting multipart uploads
            Dom.get(this.id + "-form").action = Alfresco.constants.PROXY_URI + this.options.detailsWebscript + "/multipart.html";

            // Make sure we listen to the change event so we can store details about the file (name & size) and use it in validations below
            _this = this;
            YAHOO.util.Event.addListener(this.id + "-certificate", "change", function()
            {
               if (this.files && this.files.length > 0)
               {
                  _this._fileSize = this.files[0].size;
               }
            });

            Alfresco.util.useAsButton(this.id + "-certificate-edit", function(e, obj)
            {
               if (!Dom.hasClass(this.id + "-certificate-edit", "certificate-edit-cancel"))
               {
                  // Go into edit mode and change icon to display edit can be cancelled
                  Dom.addClass(this.id + "-certificate-edit", "certificate-edit-cancel");
                  Dom.removeClass(this.id + "-certificate", "hidden");
                  Dom.removeClass(this.id + "-certificate-help-icon", "hidden");
               }
               else
               {
                  // We we're in edit mode, cancel it and change icon to display edit icon
                  Dom.removeClass(this.id + "-certificate-edit", "certificate-edit-cancel");
                  Dom.addClass(this.id + "-certificate", "hidden");
                  Dom.addClass(this.id + "-certificate-help-icon", "hidden");
               }
            }, {}, this);

            // Add validation
            form.addValidation(this.id + "-idpSsoURL", Alfresco.forms.validation.mandatory, null, "change", this.msg("Alfresco.forms.validation.mandatory.message"));
            form.addValidation(this.id + "-idpSloRequestURL", Alfresco.forms.validation.mandatory, null, "change", this.msg("Alfresco.forms.validation.mandatory.message"));
            form.addValidation(this.id + "-idpSloResponseURL", Alfresco.forms.validation.mandatory, null, "change", this.msg("Alfresco.forms.validation.mandatory.message"));
            form.addValidation(this.id + "-issuer", Alfresco.forms.validation.mandatory, null, "change", this.msg("Alfresco.forms.validation.mandatory.message"));
            form.addValidation(this.id + "-certificate", function HtmlUpload_validateSize(field, args)
            {
               if (!Alfresco.util.isVisible(_this.id + "-certificate"))
               {
                  // The certificate input field is not visible (editable) and is therefor not mandatory
                  return true;
               }
               return Alfresco.forms.validation.mandatory(field, args);
            }, null, "change", this.msg("Alfresco.forms.validation.mandatory.message"));

            // Internet Explorer does not support the "files" attribute for the input type file
            // so there is no point in adding validations
            if (YAHOO.env.ua.ie == 0)
            {
               // Make sure file isn't empty
               form.addValidation(this.id + "-certificate", function HtmlUpload_validateSize(field, args)
               {
                  if (!Alfresco.util.isVisible(_this.id + "-certificate"))
                  {
                     // The certificate input field is not visible (editable) and is therefor not mandatory
                     return true;
                  }
                  return !YAHOO.lang.isNumber(_this._fileSize) || _this._fileSize > 0;
               }, null, "change", this.msg("message.zeroByteFileSelected"));
            }

            // The ok button is the submit button, and it should be enabled when the form is ready
            form.setSubmitElements(this.widgets.saveButton);
            form.doBeforeFormSubmit = {
               fn: function()
               {
                  this.widgets.saveButton.set("disabled", true)
               },
               obj: null,
               scope: this
            };

            // Add field help
            this.addFieldHelp("idpSsoURL");
            this.addFieldHelp("idpSloRequestURL");
            this.addFieldHelp("idpSloResponseURL");
            this.addFieldHelp("certificate");
            this.addFieldHelp("issuer");
            this.addFieldHelp("shareEntrypointUrl");

            // Submit as an ajax submit (not leave the page), in json format
            form.setAJAXSubmit(true, {});

            // We're in a popup, so need the tabbing fix
            form.applyTabFix();
            form.init();
         }
      },

      /**
       * method addFieldHelp
       * @param field {String} The name of the field to add help for
       */
      addFieldHelp: function(field)
      {
         Alfresco.util.useAsButton(this.id + "-" + field + "-help-icon", function(e, field)
         {
            Alfresco.util.toggleHelpText(this.id + "-" + field + "-help");
         }, field, this);
      },

      /**
       * Called when the SAML details have been successfully saved
       *
       * @method onSaveSuccess
       * @param response {Object} The service response
       */
      onSaveSuccess: function(response)
      {
         this.widgets.saveButton.set("disabled", false);
         Alfresco.util.PopupManager.displayMessage(
         {
            text: this.msg("save.message.success")
         });

         this.loadDetails();
      },

      /**
       * Called when the SAML details couldn't be saved
       *
       * @method onSaveFailure
       * @param response {Object} The service response
       */
      onSaveFailure: function (response)
      {
         this.widgets.saveButton.set("disabled", false);
         Alfresco.util.PopupManager.displayPrompt(
         {
            title: this.msg("message.failure"),
            text: this.msg("save.message.failure")
         });
      },

      /**
       * Called when the SAML enalbed button is toggled.
       * Will display the settings form OR disabled SAML depending if it was checked or unchecked.
       *
       * @method onSSOEnabledClicked
       */
      onSSOEnabledClicked: function()
      {
         if (Dom.get(this.id + "-ssoEnabledButton").checked)
         {
            // Show form
            Dom.removeClass(this.id + "-form", "hidden");
         }
         else
         {
            // Hide form and disable SAML
            Dom.addClass(this.id + "-form", "hidden");
            Alfresco.util.Ajax.jsonPost(
            {
               url: Alfresco.constants.PROXY_URI + this.options.detailsWebscript,
               dataObj:
               {
                  ssoEnabled: false
               },
               successMessage: this.msg("disable.message.success"),
               failureMessage: this.msg("disable.message.failure")
            });
         }
      },

      /**
       * Called when the Reset button is clicked.
       * Will load the settings from the server and display them again
       *
       * @method onResetClick
       */
      onResetClick: function()
      {
         this.loadDetails();
      }

   });

})();
