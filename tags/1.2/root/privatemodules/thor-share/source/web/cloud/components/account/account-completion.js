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
 * AccountCompletion component.
 *
 * @namespace Alfresco
 * @class Alfresco.cloud.component.AccountCompletion
 */
(function()
{

   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      Selector = YAHOO.util.Selector;

   /**
    * AccountCompletion constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.cloud.component.AccountCompletion} The new AccountCompletion instance
    * @constructor
    */
   Alfresco.cloud.component.AccountCompletion = function AccountCompletion_constructor(htmlId)
   {
      Alfresco.cloud.component.AccountCompletion.superclass.constructor.call(this, "Alfresco.cloud.component.AccountCompletion", htmlId, ["button"]);
      return this;
   };

   YAHOO.extend(Alfresco.cloud.component.AccountCompletion, Alfresco.component.Base,
   {
      options:
      {
         /**
          * The users username used to match passwords against.
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
          * Url to redirect to.
          *
          * @property redirect
          * @type string
          */
         redirect: null,

         /**
          * iOS url to redirect if app is installed.
          *
          * @property startpageIOS
          * @type string
          */
         startpageIOS: null,

         /**
          * The current user's username
          *
          * @property currentUsername
          * @type string
          */
         currentUsername: null,

         /**
          * The name of the person that invited the user
          *
          * @property inviterName
          * @type string
          */
         inviterName: null,

         /**
          * The name of the site that the user was invited to
          *
          * @property inviteSiteTitle
          * @type string
          */
         inviteSiteTitle: null,

         /**
          * If the user is logged in as a another user than the user the reset is meant for this property will
          * be used to redirect him back to this page after being logged out.
          *
          * @property postLogoutRedirectUrl
          * @type string
          */
         postLogoutRedirectUrl: null,
         
         /**
          * URL that is redirected to instantly, when page is finished loading
          */
         instantRedirect: null
         
      },

      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function AccountCompletion_onReady()
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

         // If redirect, start it 1.5 sec after the notification has been displayed
         if (this.options.redirect)
         {
            overlay.showEvent.subscribe(function()
            {
               YAHOO.lang.later(1500, this, function()
               {
                  document.location.href = this.options.redirect;
               });
            }, this, true);
         }
         
         if(this.options.instantRedirect)
         {
            document.location = this.options.instantRedirect;                     
         }

         // Show overlay
         overlay.show();
         this.widgets.overlay = overlay;

         if (Dom.get(this.id + "-profileForm"))
         {
            this.setupProfileForm();
         }
         else if (Dom.get(this.id + "-loginWithActionForm"))
         {
            this.setupLoginWithActionForm();
         }
         else if (Dom.get(this.id + "-loginForm"))
         {
            this.setupLoginForm();
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
       * Sets up the login form 
       * 
       * @method setupLoginForm
       */
      setupLoginForm: function()
      {
         // The submit button 
         this.widgets.submitButton = Alfresco.util.createYUIButton(this, "submit", null, {
            type: "submit"
         });         
         this.widgets.submitButton.addClass("alfresco-attention-yui-button");
      },
      
      /**
       * Sets up the profile form
       * 
       * @method setupProfileForm
       */
      setupLoginWithActionForm: function()
      {
         // The submit button 
         this.widgets.submitButton = Alfresco.util.createYUIButton(this, "submit", null, {
            type: "submit"
         });         
         this.widgets.submitButton.addClass("alfresco-attention-yui-button");

         // Create the profile form
         var form = new Alfresco.forms.Form(this.id + "-loginWithActionForm");
         form.addValidation(Dom.get(this.id + "-username"), Alfresco.forms.validation.mandatory, null, "keyup", this.paramMsg("Alfresco.forms.validation.mandatory.message"));
         form.addValidation(Dom.get(this.id + "-password"), Alfresco.forms.validation.mandatory, null, "keyup", this.paramMsg("Alfresco.forms.validation.mandatory.message"));
         form.setSubmitElements(this.widgets.submitButton);
         form.setSubmitAsJSON(true);
         form.setAjaxNoReloadOnAuthFailure(true);
         form.setAJAXSubmit(true, {
            successCallback:
            {
               fn: this.onFormSuccess, scope: this
            },
            failureCallback:
            {
               fn: function AccountCompletion_onFormFailure(response)
               {
                  this.widgets.submitButton.set("disabled", false);
                  this.widgets.feedbackMessage.hide();

                  var key = "login-with-action.message.failure",
                     status = response.serverResponse.status;
                  if (this.paramMsg(key + "." + status) != key + "." + status)
                  {
                     key += "." + status
                  }
                  Alfresco.util.PopupManager.displayPrompt({
                     title: this.msg("login-with-action.title.failure"),
                     text: this.paramMsg(key)
                  });
               },
               scope: this
            }
         });
         form.doBeforeFormSubmit = { fn: function AccountCompletion_doBeforeProfileSubmit()
         {
            // Save the username password that was used, disable submit and display activating message
            Dom.get(this.id + "-login-username").value = Dom.get(this.id + "-username").value;
            Dom.get(this.id + "-login-password").value = Dom.get(this.id + "-password").value;
            this.widgets.feedbackMessage = Alfresco.util.PopupManager.displayMessage(
            {
               text: this.paramMsg("login-with-action.message.waiting"),
               spanClass: "wait",
               displayTime: 0
            });
         }, scope: this };
         form.init();
      },


      /**
       * Sets up the profile form
       * 
       * @method setupProfileForm
       */
      setupProfileForm: function()
      {
         // The submit button 
         this.widgets.submitButton = Alfresco.util.createYUIButton(this, "submit", null, {
            type: "submit"
         });         
         this.widgets.submitButton.addClass("alfresco-attention-yui-button");

         // Create the profile form
         var form = new Alfresco.forms.Form(this.id + "-profileForm");
         form.addValidation(Dom.get(this.id + "-firstName"), Alfresco.forms.validation.mandatory, null, "keyup", this.paramMsg("Alfresco.forms.validation.mandatory.message"));
         form.addValidation(Dom.get(this.id + "-lastName"), Alfresco.forms.validation.mandatory, null, "keyup", this.paramMsg("Alfresco.forms.validation.mandatory.message"));
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
         form.addValidation(Dom.get(this.id + "-terms"), Alfresco.forms.validation.mandatory, null, "keyup", this.paramMsg("validation-hint.terms"));
         form.setSubmitElements(this.widgets.submitButton);
         form.setSubmitAsJSON(true);
         form.setAJAXSubmit(true, {
            successCallback:
            {
               fn: this.onFormSuccess, scope: this
            },
            failureCallback:
            {
               fn: function AccountCompletion_onFormFailure(response)
               {
                  this.widgets.submitButton.set("disabled", false);
                  this.widgets.feedbackMessage.hide();

                  var key = "profile.message.failure",
                     status = response.serverResponse.status;
                  if (this.paramMsg(key + "." + status) != key + "." + status)
                  {
                     key += "." + status;
                  }
                  Alfresco.util.PopupManager.displayPrompt({
                     title: this.msg("label.error"),
                     text: this.paramMsg(key)
                  });
               },
               scope: this
            }
         });
         form.doBeforeFormSubmit = { fn: function AccountCompletion_doBeforeProfileSubmit()
         {
            // Save the password that was used, disable submit and display activating message
            Dom.get(this.id + "-login-password").value = Dom.get(this.id + "-password").value;
            this.widgets.feedbackMessage = Alfresco.util.PopupManager.displayMessage(
            {
               text: this.paramMsg("profile.message.waiting"),
               spanClass: "wait",
               displayTime: 0
            });
         }, scope: this };
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
       * Called when the account has been activated.
       * Will login the user.
       *
       * @method onCreateProfileSuccess
       */
      onFormSuccess: function AccountCompletion_onFormSuccess()
      {
         // Display ios app tip if available and on iOS
         var iosNotification = Selector.query(".ios-notification", this.widgets.overlay.element, true);
         if (iosNotification && YAHOO.env.ua.ios > 0 && this.options.startpageIOS)
         {
            // We are on iOS display notification
            this.widgets.appIOSLaunchButton = Alfresco.util.createYUIButton(this, "app-ios-launch", function()
            {
               document.location = this.options.startpageIOS;
            });
            this.widgets.appIOSLaunchButton.addClass("alfresco-attention-yui-button");

            this._showAppNotification(iosNotification);

            return;
         }

         // Submit hidden login form
         Dom.get(this.id + "-login").submit();
      },

      /**
       * Will fade away the overlay, hide current form & about text, show app notification and fade in overlay again.
       *
       * @method _showAppNotification
       * @param notificationEl {HTMLElement} The element containing the app notification tip
       */
      _showAppNotification: function AccountCompletion__showAppNotification(notificationEl)
      {
         // Hide feedback message
         this.widgets.feedbackMessage.hide();

         // Hide notification
         var el = notificationEl;
         //this.widgets.overlay.hideEvent.subscribe(function()
         this.widgets.overlay.subscribe("hide", function()
         {
            YAHOO.lang.later(100, this, function()
            {
               Dom.addClass(Selector.query(".account-info", this.widgets.overlay.element, true), "hidden");
               Dom.addClass(Selector.query(".account-form", this.widgets.overlay.element, true), "hidden");
               Dom.removeClass(el, "hidden");
               this.widgets.overlay.show();
            });
         }, this, true);
         this.widgets.overlay.hide();
      },

      /**
       * Provides well known msg parameter values
       *
       * @method paramMsg
       * @return The i18n value for the msg key
       */
      paramMsg: function AccountCompletion_paramMsg(key)
      {
         return this.msg(key, this.options.currentUsername, this.options.inviterName, this.options.inviteSiteTitle, this.options.postLogoutRedirectUrl);
      },

      /**
       * Generate localized text message indicating what content is required in the password
       * 
       * @method _pwContentMsg
       * @private
       */
      _pwContentMsg: function AccountCompletion__pwContentMsg()
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
      _pwContentHTMLMsg: function AccountCompletion__pwContentHTMLMsg()
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
