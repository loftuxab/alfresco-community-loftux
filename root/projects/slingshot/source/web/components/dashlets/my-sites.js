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
   Alfresco.MySites = function MySites_constructor(htmlId)
   {
      this.name = "Alfresco.MySites";
      this.id = htmlId;

      // Register this component
      Alfresco.util.ComponentManager.register(this);

      // Load YUI Components
      Alfresco.util.YUILoaderHelper.require(["button", "container"], this.onComponentsLoaded, this);

      // Initialise prototype properties
      this.preferencesService = new Alfresco.service.Preferences();

      // Listen for events from other components
      YAHOO.Bubbling.on("siteDeleted", this.onSiteDeleted, this);

      return this;
   };

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
      setOptions: function MySites_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },

      /**
       * Set messages for this component.
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {Alfresco.MySites} returns 'this' for method chaining
       */
      setMessages: function MySites_setMessages(obj)
      {
         Alfresco.util.addMessages(obj, this.name);
         return this;
      },

      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function MySites_onComponentsLoaded()
      {
         Event.onContentReady(this.id, this.onReady, this, true);
      },

      /**
       * Fired by YUI when parent element is available for scripting
       * @method onReady
       */
      onReady: function MySites_onReady()
      {
         // Listen on clicks for the create site link
         Event.addListener(this.id + "-createSite-button", "click", this.onCreateSiteLinkClick, this, true);

         // Listen on clicks for delete site icons
         var sites = this.options.sites, i, j;
         for (i = 0, j = sites.length; i < j; i++)
         {
            if (sites[i].isSiteManager)
            {
               this._addDeleteHandling(i);               
            }
            this._addFavouriteHandling(i);
         }
      },

      /**
       * Adds an event handler for bringing up the delete site dialog for the specific site
       *
       * @method _addDeleteHandling
       * @param siteIndex {int} The index of the site in the this.options.sites array
       * @private
       */
      _addDeleteHandling: function MySites__addDeleteHandling(siteIndex)
      {
         var me = this;
         var deleteSpan = Dom.get(this.id + "-delete-span-" + siteIndex);
         if (deleteSpan)
         {
            Event.addListener(deleteSpan, "click", function (event, obj)
            {
               // Find the site through its index and display the delete dialog for the site
               Alfresco.module.getDeleteSiteInstance().show(
               {
                  site: me.options.sites[siteIndex]
               });
            });
         }
      },

      /**
       * Adds an event handler that adds or removes the site as favourite site
       *
       * @method _addFavouriteHandling
       * @param siteIndex {int} The index of the site in the this.options.sites array
       * @private
       */
      _addFavouriteHandling: function MySites__addFavouriteHandling(siteIndex)
      {
         var me = this;
         var favouriteSpan = Dom.get(this.id + "-favourite-span-" + siteIndex);
         if (favouriteSpan)
         {
            var favouriteClickHandler = function (event, obj)
            {
               /**
                * We assume that the change of favourite site will work and therefore change
                * the gui immediatly after the server call.
                * If it doesn't we revoke the gui changes and display an error message
                */
               var responseConfig =
               {
                  failureCallback:
                  {
                     fn: function(event, obj)
                     {
                        obj.thisComponent._addFavouriteHandling(obj.siteIndex);
                        obj.thisComponent._toggleFavourite(obj.siteIndex);
                        Alfresco.util.PopupManager.displayPrompt(
                        {
                           text: Alfresco.util.message("message.siteFavourite.failure", obj.thisComponent.name)
                        });
                     },
                     scope: this,
                     obj:
                     {
                         siteIndex: siteIndex,
                         thisComponent: me
                     }
                  },
                  successCallback:
                  {
                     fn: function(event, obj)
                     {
                        obj.thisComponent._addFavouriteHandling(obj.siteIndex);
                        var site = obj.thisComponent.options.sites[obj.siteIndex];
                        YAHOO.Bubbling.fire(site.isFavourite ? "favouriteSiteAdded" : "favouriteSiteRemoved", site);
                     },
                     scope: this,
                     obj:
                     {
                        siteIndex: siteIndex,
                        thisComponent: me
                     }
                  }
               };

               // Remove listener so we don't do double submits
               Event.removeListener(favouriteSpan, "click", favouriteClickHandler);
               me._toggleFavourite(siteIndex);
               me.preferencesService.set(
                  Alfresco.service.Preferences.FAVOURITE_SITES + "." + me.options.sites[siteIndex].shortName,
                  me.options.sites[siteIndex].isFavourite,
                  responseConfig);
            };

            // Add listener to favourite icons
            Event.addListener(favouriteSpan, "click", favouriteClickHandler);
         }
      },

      /**
       * Helper method to change the gui and our local data model of sites
       * @method _toggleFavourite
       * @param siteIndex {integer} the index in our local data model
       */
      _toggleFavourite: function MySites__toggleFavourite(siteIndex)
      {
         var span = YAHOO.util.Dom.get(this.id + "-favourite-span-" + siteIndex);
         this.options.sites[siteIndex].isFavourite = !this.options.sites[siteIndex].isFavourite;
         if (this.options.sites[siteIndex].isFavourite)
         {
            YAHOO.util.Dom.addClass(span, "enabled");
         }
         else
         {
            YAHOO.util.Dom.removeClass(span, "enabled");
         }
      },

      /**
       * Fired by YUI Link when the "Create site" label is clicked
       * @method onCreateSiteLinkClick
       * @param event {domEvent} DOM event
       */
      onCreateSiteLinkClick: function MySites_onCreateSiteLinkClick(event)
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
      onSiteDeleted: function MySites_onSiteDeleted(layer, args)
      {
         // Hide the site in this component
         var site = args[1].site;
         Dom.setStyle(this.id + "-site-div-" + site.shortName, "display", "none");
      }      
   };
})();