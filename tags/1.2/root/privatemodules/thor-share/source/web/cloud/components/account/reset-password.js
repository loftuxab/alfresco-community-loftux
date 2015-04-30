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
 * ResetPassword component.
 *
 * @namespace Alfresco
 * @class Alfresco.cloud.component.ResetPassword
 */
(function()
{

   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      Selector = YAHOO.util.Selector;

   /**
    * ResetPassword constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.cloud.component.ResetPassword} The new ResetPassword instance
    * @constructor
    */
   Alfresco.cloud.component.ResetPassword = function ResetPassword_constructor(htmlId)
   {
      Alfresco.cloud.component.ResetPassword.superclass.constructor.call(this, "Alfresco.cloud.component.ResetPassword", htmlId, ["button"]);
      this._newPassword = null;
      return this;
   };

   YAHOO.extend(Alfresco.cloud.component.ResetPassword, Alfresco.component.Base,
   {
      /**
       * Stores the new password on form submit, will be used when automtically login in
       *
       * @type string
       */
      _newPassword: null,

      options:
      {
         /**
          * The users username used to automatically login after password reset.
          *
          * @property username
          * @type string
          */
         username: null,

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
         minPasswordSymbols: 0,

         /**
          * The current user's username
          *
          * @property currentUsername
          * @type string
          */
         currentUsername: null,

         /**
          * If the user is logged in as a another user than the user the reset is meant for this property will
          * be used to redirect him back to this page after being logged out.
          *
          * @property postLogoutRedirectUrl
          * @type string
          */
         postLogoutRedirectUrl: null
      },

      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function ResetPassword_onReady()
      {
         // Create yui overlay
         var overlay = Alfresco.util.createYUIOverlay(Dom.get(this.id),
         {
            fixedcenter: true,
            effect:
            {
               effect: YAHOO.widget.ContainerEffect.FADE,
               duration: 0.25
            }
         }, { render: false });
         Dom.removeClass(this.id + "-body", "hidden");
         overlay.render(document.body);
         overlay.center();
         overlay.show();
         overlay.hideEvent.subscribe(this.onHideOverlay, this, true);
         this.widgets.overlay = overlay;

         if (Dom.get(this.id + "-form"))
         {
            this._setupForm()
         }

         var logoutLink = Dom.get("logout-url");
         if (logoutLink)
         {
            this.renderLogoutLink(logoutLink);
         }
      },

      /**
       * Renders logout link 
       * 
       * @method renderLogoutLink
       * @param logoutLink {object} HTMLElement
       */
      renderLogoutLink: function(logoutLink)
      {
        this.logoutUrl = logoutLink.href;
        
        config = {
            type: "link",
            label: logoutLink.innerText || logoutLink.textContent,
            href: "#"
        };
        
        this.widgets.logoutLink = Alfresco.util.createYUIButton(this, "logout-link", this.doLogout, config, "logout-url");
        this.widgets.logoutLink.removeClass("yui-button");
      },

      /**
       * Performs logout action using POST request.
       *
       * @method doLogout
       * @param e {object} DomEvent
       */
      doLogout: function AccountCompletion__DoLogout(e)
      {
         Alfresco.util.Ajax.request(
         {
            url: this.logoutUrl,
            method: "POST"
         });      
      },

      /**
       * Sets up the reset password form
       *
       * @method _setupForm
       */
      _setupForm: function()
      {
         // Create yui buttons
         this.widgets.submitButton = Alfresco.util.createYUIButton(this, "submit", null, {
            type: "submit"
         });
         this.widgets.submitButton.addClass("alfresco-attention-yui-button");

         // Create the profile form
         var form = new Alfresco.forms.Form(this.id + "-form");
         form.addValidation(Dom.get(this.id + "-password"), Alfresco.forms.validation.mandatory, null, "keyup", this.paramMsg("Alfresco.forms.validation.mandatory.message"));
         form.addValidation(Dom.get(this.id + "-password"), Alfresco.forms.validation.length,
         {
            min: this.options.minPasswordLength
         }, "blur", this.paramMsg("Alfresco.forms.validation.length.message.min"));
         form.addValidation(Dom.get(this.id + "-password"), Alfresco.forms.validation.length,
         {
            min: this.options.minPasswordLength
         }, "keyup", this.paramMsg("Alfresco.forms.validation.length.message.min"));
         form.addValidation(this.id + "-password", Alfresco.forms.validation.passwordContent,
         {
            minUpper: this.options.minPasswordUpper,
            minLower: this.options.minPasswordLower,
            minNumeric: this.options.minPasswordNumeric,
            minSymbols: this.options.minPasswordSymbols
         }, "keyup", { html: this._pwContentHTMLMsg(), text: this._pwContentMsg() });
         form.addValidation(Dom.get(this.id + "-password2"), Alfresco.forms.validation.mandatory, null, "keyup", this.paramMsg("Alfresco.forms.validation.mandatory.message"));
         form.addValidation(Dom.get(this.id + "-password2"), Alfresco.forms.validation.passwordMatch,
         {
            el: this.id + "-password"
         }, "blur", this.paramMsg("Alfresco.forms.validation.passwordMatch.message"));
         form.setSubmitElements(this.widgets.submitButton);
         form.setSubmitAsJSON(true);
         form.setAJAXSubmit(true, {
            successCallback: { fn: this.onResetPasswordSuccess, scope: this },
            failureCallback: {fn: this.onResetPasswordFailure, scope: this },
            failureMessage: this.paramMsg("message.reset-password.failure")
         });
         form.doBeforeFormSubmit = { fn: this.doBeforeResetPasswordSubmit, scope: this };
         form.init();

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
         Event.addListener(this.id + "-password", "keyup", function(e){
            this.widgets.passwordStrengthMeter.setStrength(Dom.get(this.id + "-password").value);
         }, this, true);

      },

      /**
       * Called when the form is about to submit itself.
       *
       * @method doBeforeResetPasswordSubmit
       */
      doBeforeResetPasswordSubmit: function ResetPassword_doBeforeResetPasswordSubmit()
      {
         this._newPassword = Dom.get(this.id + "-password").value;

         // Save the password that was used, disable submit and display activating message
         this.widgets.feedbackMessage = Alfresco.util.PopupManager.displayMessage(
         {
            text: this.paramMsg("message.reset-password.waiting"),
            spanClass: "wait",
            displayTime: 0
         });
      },

      /**
       * Called when the account has been activated.
       * Will login the user.
       *
       * @method onResetPasswordSuccess
       */
      onResetPasswordSuccess: function ResetPassword_onResetPasswordSuccess()
      {
         // Hide feedback message
         if (this.widgets.feedbackMessage)
         {
            this.widgets.feedbackMessage.hide();
         }

         // Hide overlay
         this.widgets.overlay.hide();
      },

      /**
       * @method onHideOverlay
       */
      onHideOverlay: function ForgotPassword_onHideOverlay()
      {
         // Hide the form and display a success message
         Dom.addClass(this.id + "-form-container", "hidden");
         Dom.removeClass(this.id + "-confirmation-container", "hidden");
         this.widgets.overlay.show();

         // Automatically login user
         Alfresco.util.navigateTo(Alfresco.constants.URL_CONTEXT + "page/dologin", "POST",
         {
            username: this.options.username,
            password: this._newPassword
         });
      },

      /**
       * Called when the account activation failed.
       * Will hide the pending message and re enable the button;
       *
       * @method onResetPasswordFailure
       */
      onResetPasswordFailure: function ResetPassword_onResetPasswordFailure()
      {
         this.widgets.feedbackMessage.destroy();
      },

      /**
       * Provides 2 well known msg parameter values
       *
       * @method paramMsg
       * @return The i18n value for the msg key
       */
      paramMsg: function ResetPassword_paramMsg(key)
      {
         return this.msg(key, this.options.currentUsername, this.options.postLogoutRedirectUrl);
      },

      /**
       * Generate localized text message indicating what content is required in the password
       * 
       * @method _pwContentMsg
       * @private
       */
      _pwContentMsg: function ResetPassword__pwContentMsg()
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
      _pwContentHTMLMsg: function ResetPassword__pwContentHTMLMsg()
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
