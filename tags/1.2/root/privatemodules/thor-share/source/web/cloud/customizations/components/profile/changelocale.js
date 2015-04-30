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
 * Change Locale component.
 * 
 * @namespace Alfresco
 * @class Alfresco.ChangeLocale
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event;
      
   /**
    * Preferences
    */
   var PREFERENCES_LOCALE = "locale";

   /**
    * ChangeLocale constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.ChangeLocale} The new UserProfile instance
    * @constructor
    */
   Alfresco.ChangeLocale = function ChangeLocale_constructor(htmlId)
   {
      Alfresco.ChangeLocale.superclass.constructor.call(this, "Alfresco.ChangeLocale", htmlId, ["button"]);
      return this;
   }
   
   YAHOO.extend(Alfresco.ChangeLocale, Alfresco.component.Base,
   {
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function ChangeLocale_onReady()
      {
         // Reference to self used by inline functions
         var me = this;
        
         // Buttons
         this.widgets.ok = Alfresco.util.createYUIButton(this, "button-ok", this.onSubmit);
         this.widgets.cancel = Alfresco.util.createYUIButton(this, "button-cancel", this.onCancel);
         
         // Form definition
         var form = new Alfresco.forms.Form(this.id + "-form");
         
         // Preferences service
         this.services.preferences = new Alfresco.service.Preferences();

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
       * Submit Changes button click handler
       *
       * @method onSubmitl
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onSubmit: function ChangeLocale_onSubmit(e, p_obj)
      {
         //Get the chosen language from the language selector
         var langSelect = document.getElementById(this.id + "-language");
         var locale = langSelect.options[langSelect.selectedIndex].value;

         //set the cookie expiration to 10 years from now.
         var expirationdate = new Date();
         expirationdate.setFullYear(expirationdate.getFullYear() + 10);
         document.cookie="alfLocale=" + locale + ";expires=" + expirationdate.toUTCString() + ";path=/";

         //set the user preferences to the new language
         this.services.preferences.set(PREFERENCES_LOCALE, locale);
               
         //Reload the page to change the language
         location.reload(true);
      },
      
      /**
       * Cancel Changes button click handler
       *
       * @method onCancel
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onCancel: function ChangeLocale_onCancel(e, p_obj)
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
      }
   });
})();