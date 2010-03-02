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
 * LinksViewTemplate template.
 *
 * @namespace Alfresco
 * @class Alfresco.LinksViewTemplate
 */
(function()
{
   /**
    * LinksViewTemplate constructor.
    *
    * @return {Alfresco.LinksViewTemplate} The new LinksViewTemplate instance
    * @constructor
    */
   Alfresco.LinksViewTemplate = function LinksViewTemplate_constructor()
   {
      // Load YUI Components
      //Alfresco.util.YUILoaderHelper.require(["json"], this.onComponentsLoaded, this);

      return this;
   };

   Alfresco.LinksViewTemplate.prototype =
   {
      /**
       * Object container for storing YUI widget instances.
       *
       * @property widgets
       * @type object
       */
       widgets: {},

      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function LinksViewTemplate_onComponentsLoaded()
      {
         YAHOO.util.Event.onDOMReady(this.onReady, this, true);
      },

      /**
       * Fired by YUI when parent element is available for scripting.
       * Template initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function LinksViewTemplate_onReady()
      {

      }
   };

})();

// Instantiate the Discussions View Topics template
new Alfresco.LinksViewTemplate();