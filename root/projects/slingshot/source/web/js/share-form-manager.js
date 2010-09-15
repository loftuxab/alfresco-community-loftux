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
 * ShareFormManager component.
 *
 * Defines which pages in Sare that shall be treated as ajax pages and be given a change to restore its previous
 * state after the form has been visited. 
 *
 * @namespace Alfresco.component
 * @class Alfresco.component.ShareFormManager
 */
(function()
{
   /**
    * ShareFormManager constructor.
    *
    * @param {String} el The HTML id of the parent element
    * @return {Alfresco.RulesHeader} The new RulesHeader instance
    * @constructor
    */
   Alfresco.component.ShareFormManager = function Location_constructor(el)
   {
      Alfresco.component.ShareFormManager.superclass.constructor.call(this, el);

      // Re-register with our own name
      this.name = "Alfresco.component.ShareFormManager";
      Alfresco.util.ComponentManager.reregister(this);

      // Instance variables
      this.options = YAHOO.lang.merge(this.options, Alfresco.component.ShareFormManager.superclass.options);

      return this;
   };

   YAHOO.extend(Alfresco.component.ShareFormManager, Alfresco.component.FormManager,
   {
      /**
       * Share pages that use ajax state ("#").
       *
       * @method pageUsesAjaxState
       * @param url
       * @return {boolean} True if the url is recognised as a page that uses ajax states (adds values after "#" on the url)
       * @override
       */
      pageUsesAjaxState: function FormManager_pageUsesAjaxState(url)
      {
         return (url.match(/documentlibrary([?]|$)/) ||
               url.match(/repository([?]|$)/) ||
               url.match(/my-workflows([?]|$)/) ||
               url.match(/my-tasks([?]|$)/));
      },

      /**
       * Override this method to make the user visit this url if no preferred urls was given for a form and
       * there was no page visited before the user came to the form page.
       *
       * @method getSiteDefaultUrl
       * @return {string} The url to make the user visit if no other alternatives have been found
       */
      getSiteDefaultUrl: function FormManager_getSiteDefaultUrl()
      {
         return Alfresco.util.uriTemplate("userdashboardpage", 
         {
            userid: Alfresco.constants.USERNAME
         });
      }

   });
})();
