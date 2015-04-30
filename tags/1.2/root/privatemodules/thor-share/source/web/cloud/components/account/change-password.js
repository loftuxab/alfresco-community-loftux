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
 * Change Password component.
 * 
 * @namespace Alfresco
 * @class Alfresco.ChangePassword
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event;
      
   /**
    * ChangePassword constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.ChangePassword} The new UserProfile instance
    * @constructor
    */
   Alfresco.cloud.component.ChangePassword = function(htmlId)
   {
      Alfresco.cloud.component.ChangePassword.superclass.constructor.call(this, "Alfresco.ChangePassword", htmlId, ["button"]);
      return this;
   }
   
   YAHOO.extend(Alfresco.cloud.component.ChangePassword, Alfresco.component.Base,
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
          * Minimum length of a password
          * 
          * @property minPasswordLength
          * @type int
          * @default 0
          */
         minPasswordLength: 0,

         /**
          * Minimum number of upper case characters required in passwords
          *
          * @property minPasswordUpper
          * @type int
          * @default 0
          */
         minPasswordUpper: 0,

         /**
          * Minimum number of lower case characters required in passwords
          *
          * @property minPasswordLower
          * @type int
          * @default 0
          */
         minPasswordLower: 0,

         /**
          * Minimum number of numeric characters required in passwords
          *
          * @property minPasswordNumeric
          * @type int
          * @default 0
          */
         minPasswordNumeric: 0,

         /**
          * Minimum number of non-alphanumeric characters required in passwords
          *
          * @property minPasswordSymbols
          * @type int
          * @default 0
          */
         minPasswordSymbols: 0
      },
   
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function UP_onReady()
      {
         // Reference to self used by inline functions
         var me = this;
         
         // Buttons
         this.widgets.ok = Alfresco.util.createYUIButton(this, "button-ok", null,
            {
               type: "submit"
            });
         this.widgets.cancel = Alfresco.util.createYUIButton(this, "button-cancel", this.onCancel);
         
         // Form definition
         var form = new Alfresco.forms.Form(this.id + "-form");
         form.setSubmitElements(this.widgets.ok);
         form.setShowSubmitStateDynamically(true);
         form.setSubmitAsJSON(true);
         form.setAJAXSubmit(true,
         {
            successCallback:
            {
               fn: this.onSuccess,
               scope: this
            }
         });
         
         // Form field validation
         form.addValidation(this.id + "-oldpassword", Alfresco.forms.validation.mandatory, null, "keyup", this.msg("Alfresco.forms.validation.mandatory.message"));
         form.addValidation(this.id + "-newpassword1", Alfresco.forms.validation.mandatory, null, "keyup", this.msg("Alfresco.forms.validation.mandatory.message"));
         form.addValidation(this.id + "-newpassword1", Alfresco.forms.validation.length,
         {
            min: this.options.minPasswordLength,
            max: 255,
            crop: true
         }, "keyup", this.msg("Alfresco.forms.validation.length.message.min"));
         form.addValidation(this.id + "-newpassword1", Alfresco.forms.validation.passwordContent,
         {
            minUpper: this.options.minPasswordUpper,
            minLower: this.options.minPasswordLower,
            minNumeric: this.options.minPasswordNumeric,
            minSymbols: this.options.minPasswordSymbols
         }, "keyup", { html: this._pwContentHTMLMsg(), text: this._pwContentMsg() });
         form.addValidation(this.id + "-newpassword2", Alfresco.forms.validation.mandatory, null, "keyup", this.msg("Alfresco.forms.validation.mandatory.message"));
         form.addValidation(this.id + "-newpassword2", Alfresco.forms.validation.passwordMatch,
               {
                  el: this.id + "-newpassword1"
               }, "blur", Alfresco.util.message("Alfresco.forms.validation.passwordMatch.message"));
         
         // Add password strength meter
         this.widgets.passwordStrengthMeter = new Alfresco.widget.PasswordStrengthMeter(this.id + "-passwordStrengthMeter",
         {
            username: this.options.username,
            minLength: this.options.minPasswordLength,
            minUpper: this.options.minPasswordUpper,
            minLower: this.options.minPasswordLower,
            minNumeric: this.options.minPasswordNumeric,
            minSymbols: this.options.minPasswordSymbols
         });
         Event.addListener(this.id + "-newpassword1", "keyup", function(e){
            this.widgets.passwordStrengthMeter.setStrength(Dom.get(this.id + "-newpassword1").value);
         }, this, true);
         
         // Initialise the form
         form.init();
         
         // Finally show the main component body here to prevent UI artifacts on YUI button decoration
         Dom.setStyle(this.id + "-body", "display", "block");
      },

      /**
       * YUI WIDGET EVENT HANDLERS
       * Handlers for standard events fired from YUI widgets, e.g. "click"
       */
      
      /**
       * Save Changes form submit success handler
       *
       * @method onSuccess
       * @param response {object} Server response object
       */
      onSuccess: function UP_onSuccess(response)
      {
         if (response && response.json)
         {
            if (response.json.success)
            {
               // succesfully updated details - refresh back to the user profile main page
               Alfresco.util.PopupManager.displayMessage(
               {
                  text: Alfresco.util.message("message.success", this.name)
               });
               this.navigateToProfile();
            }
            else if (response.json.message)
            {
               Alfresco.util.PopupManager.displayPrompt(
               {
                  text: response.json.message
               });
            }
         }
         else
         {
            Alfresco.util.PopupManager.displayPrompt(
            {
               text: Alfresco.util.message("message.failure", this.name)
            });
         }
      },
      
      /**
       * Cancel Changes button click handler
       *
       * @method onCancel
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onCancel: function UP_onCancel(e, p_obj)
      {
         this.navigateToProfile();
      },
      
      /**
       * Perform URL navigation back to user profile main page
       * 
       * @method navigateToProfile
       */
      navigateToProfile: function UP_navigateToProfile()
      {
         var pageIndex = document.location.href.lastIndexOf('/');
         document.location.href = document.location.href.substring(0, pageIndex + 1) + "profile";
      },

      /**
       * Generate localized text message indicating what content is required in the password
       * 
       * @method _pwContentMsg
       * @private
       */
      _pwContentMsg: function UP__pwContentMsg()
      {
         var msg = YAHOO.lang.trim(this.msg("Alfresco.forms.validation.password-content.message")) + " " +
            (this.options.minPasswordUpper > 0 ? (this.msg("Alfresco.forms.validation.password-content.uppercase") + ", ") : "") +
            (this.options.minPasswordLower > 0 ? (this.msg("Alfresco.forms.validation.password-content.lowercase") + ", ") : "") +
            (this.options.minPasswordNumeric > 0 ? (this.msg("Alfresco.forms.validation.password-content.numeric") + ", ") : "") +
            (this.options.minPasswordSymbols > 0 ? (this.msg("Alfresco.forms.validation.password-content.symbols") + ", ") : "");
         if (msg.lastIndexOf(", ") > 0)
         {
            msg = msg.substring(0, msg.lastIndexOf(", ")); // Remove trailing comma and space
         }
         if (msg.lastIndexOf(", ") > 0)
         {
            msg = msg.substring(0, msg.lastIndexOf(", ")) + " " + this.msg("Alfresco.forms.validation.password-content.and") + 
               msg.substring(msg.lastIndexOf(", ") + 1); // Replace last comma with an 'and'
         }
         return msg;
      },

      /**
       * Generate localized HTML message indicating what content is required in the password
       * 
       * @method _pwContentHTMLMsg
       * @private
       */
      _pwContentHTMLMsg: function UP__pwContentHTMLMsg()
      {
         return "<p>" + this.msg("Alfresco.forms.validation.password-content.message") + "</p><ul>" +
            (this.options.minPasswordUpper > 0 ? ("<li>" + this.msg("Alfresco.forms.validation.password-content.uppercase") + "</li>") : "") +
            (this.options.minPasswordLower > 0 ? ("<li>" + this.msg("Alfresco.forms.validation.password-content.lowercase") + "</li>") : "") +
            (this.options.minPasswordNumeric > 0 ? ("<li>" + this.msg("Alfresco.forms.validation.password-content.numeric") + "</li>") : "") +
            (this.options.minPasswordSymbols > 0 ? ("<li>" + this.msg("Alfresco.forms.validation.password-content.symbols") + "</li>") : "") +
            "</ul>";
      }
   });
})();