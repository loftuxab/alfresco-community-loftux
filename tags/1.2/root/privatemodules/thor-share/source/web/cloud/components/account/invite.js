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
 * Invite component.
 *
 * @namespace Alfresco
 * @class Alfresco.cloud.component.Invite
 */
(function()
{

   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      Selector = YAHOO.util.Selector;

   /**
    * Invite constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.cloud.component.Invite} The new Invite instance
    * @constructor
    */
   Alfresco.cloud.component.Invite = function Invite_constructor(htmlId)
   {
      Alfresco.cloud.component.Invite.superclass.constructor.call(this, "Alfresco.cloud.component.Invite", htmlId, ["button"]);
      return this;
   };

   YAHOO.extend(Alfresco.cloud.component.Invite, Alfresco.component.Base,
   {
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function Invite_onReady()
      {
         // Create a panel
         this.widgets.panel = Alfresco.util.createYUIPanel(this.id + "-body");

         // Create a form that enables the submit button when a proper email has been provided
         this.widgets.submitButton = Alfresco.util.createYUIButton(this, "submit", null, {
            type: "submit"
         });
         this.widgets.cancelButton = Alfresco.util.createYUIButton(this, "cancel", this.onCancelClick);


         var form = new Alfresco.forms.Form(this.id + "-form");
         this.widgets.emails = new Alfresco.widget.MultiSelectAutoComplete(this.id + '-inviteeEmail', {
            itemUrl: Alfresco.constants.PROXY_URI + "api/people?filter={query}",
            itemPath: "people",
            itemId: "userName",
            itemName: "userName",
            itemTemplate: Dom.get(this.id + "-inviteeTemplate").innerHTML,
            forbiddenItemIds: [ Alfresco.constants.USERNAME ],
            mandatory: true,
            form: form,
            formInputName: "inviteeEmails[]",
            delimiterKeyCodes: [YAHOO.util.KeyListener.KEY.ENTER, 44, 59], // 44 = comma, 50 = semicolon
            pasteDelimiterKeyCodes: [44, 59],
            minQueryLength: 3
         });
         this.widgets.emails.addValidation(Alfresco.forms.validation.email, {}, this.msg("Alfresco.forms.validation.email.message"));
         form.setSubmitElements(this.widgets.submitButton);
         form.setSubmitAsJSON(true);
         form.setAJAXSubmit(true,
         {
            successCallback: { fn: this.onInviteSuccess, scope: this },
            successMessage: this.msg("message.invite.success"),
            failureCallback: {fn: this.onInviteFailure, scope: this }
         });
         form.applyTabFix();

         this.widgets.panel.show();

         form.init();
         this.widgets.emails.focus();
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
       * @method onInviteSuccess
       */
      onInviteSuccess: function()
      {
         this.widgets.panel.destroy();
      },

      /**
       * Called when the invitation failed.
       * Will hide the pending message and re enable the button;
       *
       * @method onCreateProfileSuccess
       */
      onInviteFailure: function(response)
      {
         // Reenable the submit button
         this.widgets.submitButton.set("disabled", false);

         var msgKey = "message.invite.failure";
         if (response.serverResponse.status == 400)
         {
            msgKey += ".400";
         }
         Alfresco.util.PopupManager.displayPrompt(
         {
            title: this.msg("error.invite"),
            text: this.msg(msgKey)
         });
      }

   });

})();
