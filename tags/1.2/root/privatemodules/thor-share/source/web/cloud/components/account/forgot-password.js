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
 * ForgotPassword component.
 *
 * @namespace Alfresco
 * @class Alfresco.cloud.component.ForgotPassword
 */
(function()
{

   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      Selector = YAHOO.util.Selector;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML;

   /**
    * ForgotPassword constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.cloud.component.ForgotPassword} The new ForgotPassword instance
    * @constructor
    */
   Alfresco.cloud.component.ForgotPassword = function ForgotPassword_constructor(htmlId)
   {
      Alfresco.cloud.component.ForgotPassword.superclass.constructor.call(this, "Alfresco.cloud.component.ForgotPassword", htmlId, ["button"]);
      return this;
   };

   YAHOO.extend(Alfresco.cloud.component.ForgotPassword, Alfresco.component.Base,
   {
      /**
       * The username/email that the user typed into the textfield
       *
       * @property _username
       * @type string
       */
      _username: null,

      options:
      {
      },

      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function ForgotPassword_onReady()
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

         // Create yui buttons
         this.widgets.submitButton = Alfresco.util.createYUIButton(this, "submit", null, {
            type: "submit"
         });
         this.widgets.submitButton.addClass("alfresco-attention-yui-button");
         this.widgets.cancelButton = Alfresco.util.createYUIButton(this, "cancel", this.onCancelButtonClick);

         // Create the profile form
         var form = new Alfresco.forms.Form(this.id + "-form");
         form.addValidation(Dom.get(this.id + "-username"), Alfresco.forms.validation.mandatory, null, "keyup", this.msg("Alfresco.forms.validation.mandatory.message"));
         form.addValidation(Dom.get(this.id + "-username"), Alfresco.forms.validation.email, null, "blur", this.msg("Alfresco.forms.validation.email.message"));
         form.setSubmitElements(this.widgets.submitButton);
         form.setSubmitAsJSON(true);
         form.setAJAXSubmit(true, {
            successCallback: { fn: this.onForgotPasswordSuccess, scope: this },
            failureCallback: {fn: this.onForgotPasswordFailure, scope: this },
            failureMessage: this.msg("message.forgot-password.failure")
         });
         form.doBeforeFormSubmit = { fn: this.doBeforeForgotPasswordSubmit, scope: this };
         form.init();
      },

      /**
       * Called when the form is about to submit itself.
       *
       * @method doBeforeForgotPasswordSubmit
       */
      doBeforeForgotPasswordSubmit: function ForgotPassword_doBeforeForgotPasswordSubmit()
      {
         // Save the username/email that was submitted so we can display it in the confirmation
         this._username = Dom.get(this.id + "-username").value;

         // Save the password that was used, disable submit and display activating message
         this.widgets.feedbackMessage = Alfresco.util.PopupManager.displayMessage(
         {
            text: this.msg("message.forgot-password.waiting", this.name),
            spanClass: "wait",
            displayTime: 0
         });
      },

      /**
       * Called when the account has been activated.
       * Will login the user.
       *
       * @method onForgotPasswordSuccess
       */
      onForgotPasswordSuccess: function ForgotPassword_onForgotPasswordSuccess()
      {
         // Set success message
         Selector.query("p", this.id + "-confirmation-container", true).innerHTML = this.msg("text.forgot-password-success", $html(this._username));

         // Hide feedback message & form
         if (this.widgets.feedbackMessage)
         {
            this.widgets.feedbackMessage.hide();
         }
         this.widgets.overlay.hide();
      },

      /**
       * @method onHideOverlay
       */
      onHideOverlay: function ForgotPassword_onHideOverlay()
      {
         // Hide
         Dom.addClass(this.id + "-form-container", "hidden");
         Dom.removeClass(this.id + "-confirmation-container", "hidden");
         this.widgets.overlay.show();
      },

      /**
       * Called when the account activation failed.
       * Will hide the pending message and re enable the button;
       *
       * @method onForgotPasswordFailure
       */
      onForgotPasswordFailure: function ForgotPassword_onForgotPasswordFailure()
      {
         this.widgets.feedbackMessage.destroy();
      },


      /**
       * Called when the account activation failed.
       * Will hide the pending message and re enable the button;
       *
       * @method onCancelButtonClick
       */
      onCancelButtonClick: function ForgotPassword_onCancelButtonClick()
      {
         document.location.href = Alfresco.constants.URL_CONTEXT.match(/\/[^\/]+/g)[0];
      }
   });

})();
