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
 * Create Users component.
 *
 * @namespace Alfresco
 * @class Alfresco.cloud.component.CreateUsers
 */
(function()
{

   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      Selector = YAHOO.util.Selector;

   /**
    * CreateUsers constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.cloud.component.CreateUsers} The new CreateUsers instance
    * @constructor
    */
   Alfresco.cloud.component.CreateUsers = function CreateUsers_constructor(htmlId)
   {
      Alfresco.cloud.component.CreateUsers.superclass.constructor.call(this, "Alfresco.cloud.component.CreateUsers", htmlId, ["button"]);
      this._invalidEmail = null;
      this._submittedEmails = [];
      return this;
   };

   YAHOO.extend(Alfresco.cloud.component.CreateUsers, Alfresco.component.Base,
   {
      /**
       * Keeps track of which email that was badly formatted
       *
       * @type {String}
       */
      _invalidEmail: null,

      /**
       * Keeps track of which emails that was submitted
       *
       * @type {String}
       */
      _submittedEmails: [],

      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function CreateUsers_onReady()
      {
         // Create a panel
         this.widgets.panel = Alfresco.util.createYUIPanel(this.id + "-body");

         // Create a form that enables the submit button when a proper email has been provided
         this.widgets.submitButton = Alfresco.util.createYUIButton(this, "submit", null, {
            type: "submit"
         });
         this.widgets.cancelButton = Alfresco.util.createYUIButton(this, "cancel", this.onCancelClick);

         var form = new Alfresco.forms.Form(this.id + "-form");
         form.addValidation(Dom.get(this.id + "-emails"), Alfresco.forms.validation.mandatory, null, "keyup", this.msg("Alfresco.forms.validation.mandatory.message"));
         form.addValidation(Dom.get(this.id + "-emails"), this.bind(this._emailsValidation), null, "blur", this.bind(this._emailValidationMessages));
         form.setSubmitElements(this.widgets.submitButton);
         form.setSubmitAsJSON(true);
         form.doBeforeAjaxRequest = {
            fn: function(config)
            {
               // Transform email string to array
               var emails = [],
                  emailStrings = config.dataObj.emails.split(","),
                  email;
               for (var i = 0, il = emailStrings.length; i < il; i++)
               {
                  email = YAHOO.lang.trim(emailStrings[i] || "");
                  if (email.length > 0)
                  {
                     emails.push(email);
                  }
               }
               this._submittedEmails = emails;
               config.dataObj.emails = emails;
               return true;
            },
            scope: this
         };

         form.setAJAXSubmit(true,
         {
            successCallback: { fn: this.onCreateUsersSuccess, scope: this },
            failureCallback: {fn: this.onCreateUsersFailure, scope: this },
            failureMessage: this.msg("message.create.failure")
         });
         form.applyTabFix();

         this.widgets.panel.show();

         form.init();
      },

      /**
       * Multiple email validation handler, tests that the given field's value is one or more valid
       * email addresses.
       *
       * @method email
       * @param field {object} The element representing the field the validation is for
       * @param args {object} Not used
       * @param event {object} The event that caused this handler to be called, maybe null
       * @param form {object} The forms runtime class instance the field is being managed by
       * @static
       */
      _emailsValidation: function CreateUsers__validateEmails(field, args, event, form)
      {
         var values = (field.value || "").split(","),
            value,
            valid = true;
         this._invalidEmail = null;
         for (var i = 0, il = values.length; i < il; i++)
         {
            value = YAHOO.lang.trim(values[i] || "");
            if (value.length > 0)
            {
               if (!Alfresco.forms.validation.email({ value: value }, args, event, form))
               {
                  // NOTE: Just adequate fix for THOR-1092: remove any tokens that may have been put in email address
                  this._invalidEmail = value.replace(/{.*}/g, "");
                  return false;
               }
            }
         }
         return true;
      },

      _emailValidationMessages: function()
      {
         return this._invalidEmail != null ? this.msg("validation.emails", this._invalidEmail) : "";
      },

      /**
       * Called when the user clicks on the cancel button.
       * Will close the dialog.
       *
       * @method onCancelClick
       */
      onCancelClick: function()
      {
         this.widgets.panel.destroy();
      },

      /**
       * Called when the users have been invited.
       * Will close the dialog.
       *
       * @method onCreateUsersSuccess
       */
      onCreateUsersSuccess: function(response)
      {
         var emailsNotCreated = [],
            invalidEmails = response.json.invalidEmails;
         for (var i = 0; i < invalidEmails.length; i++)
         {
            if (invalidEmails[i].failureReason == "INCORRECT_DOMAIN")
            {
               emailsNotCreated.push(invalidEmails[i].email);
            }
         }
         if (emailsNotCreated && emailsNotCreated.length > 0)
         {
            var msg;
            if (this._submittedEmails.length == emailsNotCreated.length)
            {
               // All emails were rejected
               msg = this.msg("message.create.failure.all");
            }
            else
            {
               // Some emails were accepted and some rejected
               Dom.get(this.id + "-emails").value = emailsNotCreated.join(", ");
               msg = this.msg("message.create.failure.some", this._submittedEmails.length - emailsNotCreated.length, emailsNotCreated.length);
            }
            // Inform user
            Alfresco.util.PopupManager.displayPrompt(
            {
               title: this.msg("message.create.title"),
               text: msg
            });

            // Re enable the submit button
            this.widgets.submitButton.set("disabled", false);
         }
         else
         {
            // All emails were accepted
            Alfresco.util.PopupManager.displayMessage(
            {
               text: this.msg("message.create.success")
            });
            this.widgets.panel.destroy();
         }
      },

      /**
       * Called when the invitation failed.
       * Will hide the pending message and re enable the button;
       *
       * @method onCreateProfileSuccess
       */
      onCreateUsersFailure: function(response)
      {
         // Re enable the submit button
         this.widgets.submitButton.set("disabled", false);
      }

   });

})();
