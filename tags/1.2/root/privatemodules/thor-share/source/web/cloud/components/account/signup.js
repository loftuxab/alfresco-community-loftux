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
 * Signup component.
 *
 * @namespace Alfresco
 * @class Alfresco.cloud.component.Signup
 */
(function()
{
   /**
    * Signup constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.cloud.component.Signup} The new Signup instance
    * @constructor
    */
   Alfresco.cloud.component.Signup = function Signup_constructor(htmlId)
   {
      Alfresco.cloud.component.Signup.superclass.constructor.call(this, "Alfresco.cloud.component.Signup", htmlId, ["button"]);
      return this;
   };

   YAHOO.extend(Alfresco.cloud.component.Signup, Alfresco.component.Base,
   {
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function Signup_onReady()
      {
         // Create a form that enables the submit button when a proper email has been provided
         this.widgets.submitButton = Alfresco.util.createYUIButton(this, "submit", null, {
            type: "submit"
         });
         var form = new Alfresco.forms.Form(this.id + "-form");
         var emailEl = YAHOO.util.Dom.get(this.id + "-email");
         form.addValidation(emailEl, Alfresco.forms.validation.mandatory, null, "keyup", this.msg("validation-hint.mandatory"));
         form.addValidation(emailEl, Alfresco.forms.validation.email, {}, "blur", this.msg("validation-hint.email"));
         form.setSubmitElements(this.widgets.submitButton);
         form.setAsReusable(true);
         form.setAJAXSubmit(true, {
            successMessage: this.msg("signup.message.success"),
            failureMessage: this.msg("signup.message.failure")
         });
         form.setSubmitAsJSON(true);
         form.init();
      }

   });

})();
