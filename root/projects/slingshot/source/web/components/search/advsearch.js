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
 * Advanced Search component.
 * 
 * @namespace Alfresco
 * @class Alfresco.AdvancedSearch
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
       Event = YAHOO.util.Event;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML;

   /**
    * Advanced Search constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.AdvancedSearch} The new AdvancedSearch instance
    * @constructor
    */
   Alfresco.AdvancedSearch = function(htmlId)
   {
      Alfresco.AdvancedSearch.superclass.constructor.call(this, "Alfresco.AdvancedSearch", htmlId, ["button", "container"]);
      
      return this;
   };
   
   YAHOO.extend(Alfresco.AdvancedSearch, Alfresco.component.Base,
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
          * Current siteId
          * 
          * @property siteId
          * @type string
          */
         siteId: "",
         
         /**
          * Search Form objects, for example:
          * {
          *    id: "advanced-search",
          *    type: "cm:content"
          * }
          * 
          * @property searchForms
          * @type Array
          */
         searchForms: []
      },
      
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function ADVSearch_onReady()
      {
         // Finally show the component body here to prevent UI artifacts on YUI button decoration
         Dom.setStyle(this.id + "-body", "visibility", "visible");
      },

      /**
       * DEFAULT ACTION EVENT HANDLERS
       * Handlers for standard events fired from YUI widgets, e.g. "click"
       */
      
      /**
       * Event callback when form template has been loaded
       *
       * @method onFormTemplateLoaded
       * @param response {object} Server response from load template XHR request
       */
      onFormTemplateLoaded: function ADVSearch_onFormTemplateLoaded(response)
      {
         // Inject the template from the XHR request into a new DIV element
         var containerDiv = Dom.get(this.id + "-form");
         containerDiv.innerHTML = response.serverResponse.responseText;
      }
   });
})();