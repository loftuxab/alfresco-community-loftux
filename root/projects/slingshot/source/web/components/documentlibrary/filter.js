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
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      Element = YAHOO.util.Element;
   
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
      
      // Decoupled event listeners
      YAHOO.Bubbling.on("filterChanged", this.onFilterChanged, this);
      YAHOO.Bubbling.on("deactivateAllControls", this.onDeactivateAllControls, this);

      return this;
   }
   
   Alfresco.DocListFilter.prototype =
   {
      /**
       * Selected filter.
       * 
       * @property selectedFilter
       * @type {element}
       */
      selectedFilter: null,

      /**
       * Flag to indicate whether all controls are deactivated or not.
       * 
       * @property controlsDeactivated
       * @type {boolean}
       * @default false
       */
      controlsDeactivated: false,

      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function DLF_onComponentsLoaded()
      {
         Event.onContentReady(this.id, this.onReady, this, true);
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
            var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "span");
            if ((owner !== null) && !me.controlsDeactivated)
            {
               var filterId = owner.className;
               YAHOO.Bubbling.fire("filterChanged",
               {
                  filterId: filterId,
                  filterOwner: me.name
               });
               
               // If a function has been provided which corresponds to the filter name, then call it
               if (typeof me[filterId] == "function")
               {
                  me[filterId].call(me);
               }
            }
      		 
            return true;
         });
      },


      /**
       * BUBBLING LIBRARY EVENT HANDLERS FOR PAGE EVENTS
       * Disconnected event handlers for inter-component event notification
       */

      /**
       * Fired when the currently active filter has changed
       * @method onFilterChanged
       * @param layer {string} the event source
       * @param args {object} arguments object
       */
      onFilterChanged: function DLF_onFilterChanged(layer, args)
      {
         var obj = args[1];
         if ((obj !== null) && (obj.filterId !== null))
         {
            if (obj.filterOwner == this.name)
            {
               // Remove the old highlight, as it might no longer be correct
               if (this.selectedFilter !== null)
               {
                  Dom.removeClass(this.selectedFilter, "selected");
               }

               // Need to find the selectedFilter element, from the current filterId
               this.selectedFilter = YAHOO.util.Selector.query("." + obj.filterId, this.id + "-body")[0].parentNode;
               // This component now owns the active filter
               Dom.addClass(this.selectedFilter, "selected");
            }
            else
            {
               // Currently filtering by something other than this component
               if (this.selectedFilter !== null)
               {
                  Dom.removeClass(this.selectedFilter, "selected");
               }
            }
         }
      },
      
      /**
       * Deactivate All Controls event handler
       *
       * @method onDeactivateAllControls
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onDeactivateAllControls: function DLF_onDeactivateAllControls(layer, args)
      {
         this.controlsDeactivated = true;
         var filters = YAHOO.util.Selector.query("a.filter-link", this.id + "-body");
         for (var i = 0, j = filters.length; i < j; i++)
         {
            Dom.addClass(filters[i], "disabled");
         }
      }
      
   };
})();
