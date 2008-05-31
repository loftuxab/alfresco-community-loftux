/**
 * Copyright (C) 2005-2008 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing
 */
 
/**
 * DocListFilter component.
 * 
 * @namespace Alfresco
 * @class Alfresco.DocListFilter
 */
(function()
{
   /**
    * DocListFilter constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.DocListFilter} The new DocListFilter instance
    * @constructor
    */
   Alfresco.DocListFilter = function(htmlId)
   {
      this.name = "Alfresco.DocListFilter";
      this.id = htmlId;
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);
      
      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require([], this.onComponentsLoaded, this);
      
      return this;
   }
   
   Alfresco.DocListFilter.prototype =
   {
      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function DLF_onComponentsLoaded()
      {
         YAHOO.util.Event.onContentReady(this.id, this.onReady, this, true);
      },
   
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function DLF_onReady()
      {
         var me = this;
         
         YAHOO.Bubbling.addDefaultAction("filter-link", function DLF_filterAction(layer, args)
         {
            var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "li");
            if (owner !== null)
            {
               var action = owner.className;
               if (typeof me[action] == "function")
               {
                  me[action].call(me);
               }
            }
      		 
            return true;
         });
      },
      
      /**
       * Handler for Recently Modified filter.
       *
       * @method recentlyModified
       */
      recentlyModified: function()
      {
      },

      /**
       * Handler for Recently Added filter.
       *
       * @method recentlyAdded
       */
      recentlyAdded: function()
      {
      },

      /**
       * Handler for I Am Editing filter.
       *
       * @method iAmEditing
       */
      iAmEditing: function()
      {
      },

      /**
       * Handler for Others Are Editing filter.
       *
       * @method othersAreEditing
       */
      othersAreEditing: function()
      {
      }
   };
})();
