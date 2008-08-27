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
    * Dashboard MySites constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.MySites} The new component instance
    * @constructor
    */
   Alfresco.MySites = function MyS_constructor(htmlId)
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
       * @type {object} site object literal of the form:
       * {
       *    shortName: {string} the shortName of the site
       *    title: {string} the title of the site
       * }
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
      setOptions: function DL_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },

      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function()
      {
         YAHOO.util.Event.onContentReady(this.id, this.onReady, this, true);
      },

      /**
       * Fired by YUI when parent element is available for scripting
       * @method onReady
       */
      onReady: function()
      {
         // Listen on clicks for the create site link
         var createSiteLink = document.getElementById(this.id + "-createSite-button");
         YAHOO.util.Event.addListener(createSiteLink, "click", this.onCreateSiteLinkClick, this, true);

         // Listen on clicks for delete site icons
         var sites = this.options.sites;
         for (var i = 0; i < sites.length; i++)
         {
            var deleteSpan = document.getElementById(this.id + "-delete-span-" + i);
            if(deleteSpan)
            {
               YAHOO.util.Event.addListener(deleteSpan, "click",
                     function (event)
                     {
                        // Find the index of the site-delete link by looking at its id
                        var id = event.target.id;
                        var site = sites[new Number(id.substring(id.lastIndexOf("-") + 1))];

                        // Fin the site through the index and display the delete dialog for the site
                        Alfresco.module.getDeleteSiteInstance().show({site: site});
                     },
                     this, true);
            }
         }

      },

      /**
       * Fired by YUI Link when the "Create site" label is clicked
       * @method onCreateSiteLinkClick
       * @param event {domEvent} DOM event
       */
      onCreateSiteLinkClick: function(event)
      {
         // Create the CreateSite module if it doesnt exist
         if (this.createSite === null)
         {
            this.createSite = new Alfresco.module.CreateSite(this.id + "-createSite");
         }
         // and show it
         this.createSite.show();
      },

      /**
       * Fired any another component, DeleteSite, to let other components know
       * that a site has been deleted.
       *
       * @method onSiteDeleted
       * @param layer {object} Event fired (unused)
       * @param args {array} Event parameters (unused)
       */
      onSiteDeleted: function CS_onSiteDeleted(layer, args)
      {
         // Hide the site in this component
         var site = args[1].site;
         YAHOO.util.Dom.setStyle(this.id + "-site-div-" + site.shortName, "display", "none");
      }      

   };
})();
