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
 * Dashboard MySites component.
 * 
 * @namespace Alfresco
 * @class Alfresco.MySites
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event;

   /**
    * Dashboard MySites constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.MySites} The new component instance
    * @constructor
    */
   Alfresco.MySites = function MS_constructor(htmlId)
   {
      this.name = "Alfresco.MySites";
      this.id = htmlId;

      // Register this component
      Alfresco.util.ComponentManager.register(this);

      // Load YUI Components
      Alfresco.util.YUILoaderHelper.require(["button", "container"], this.onComponentsLoaded, this);

      YAHOO.Bubbling.on("siteDeleted", this.onSiteDeleted, this);

      return this;
   }

   Alfresco.MySites.prototype =
   {
      /**
       * CreateSite module instance.
       * 
       * @property createSite
       * @type Alfresco.module.CreateSite
       */
      createSite: null,

      /**
       * Object container for initialization options
       *
       * @property options
       * @type {object} object literal
       */
      options:
      {
         /**
          * An array with shortNames in the same order as they are listed in the html template
          *
          * @property sites
          * @type Array
          */
         sites: []
      },


      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.DocumentList} returns 'this' for method chaining
       */
      setOptions: function MS_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },

      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function MS_onComponentsLoaded()
      {
         Event.onContentReady(this.id, this.onReady, this, true);
      },

      /**
       * Fired by YUI when parent element is available for scripting
       * @method onReady
       */
      onReady: function MS_onReady()
      {
         // Listen on clicks for the create site link
         Event.addListener(this.id + "-createSite-button", "click", this.onCreateSiteLinkClick, this, true);

         // Listen on clicks for delete site icons
         var sites = this.options.sites, i, j, deleteSpan;
         for (i = 0, j = sites.length; i < j; i++)
         {
            deleteSpan = Dom.get(this.id + "-delete-span-" + i);
            if (deleteSpan)
            {
               Event.addListener(deleteSpan, "click", function (event, obj)
               {
                  // Find the site through its index and display the delete dialog for the site
                  Alfresco.module.getDeleteSiteInstance().show(
                  {
                     site: sites[obj.selectedSiteIndex]
                  });
               },
               {
                  selectedSiteIndex: i,
                  thisComponent: this
               });
            }
         }
      },

      /**
       * Fired by YUI Link when the "Create site" label is clicked
       * @method onCreateSiteLinkClick
       * @param event {domEvent} DOM event
       */
      onCreateSiteLinkClick: function MS_onCreateSiteLinkClick(event)
      {
         Alfresco.module.getCreateSiteInstance().show();         
      },

      /**
       * Fired by another component, DeleteSite, to let other components know
       * that a site has been deleted.
       *
       * @method onSiteDeleted
       * @param layer {object} Event fired (unused)
       * @param args {array} Event parameters (unused)
       */
      onSiteDeleted: function MS_onSiteDeleted(layer, args)
      {
         // Hide the site in this component
         var site = args[1].site;
         Dom.setStyle(this.id + "-site-div-" + site.shortName, "display", "none");
      }      
   };
})();