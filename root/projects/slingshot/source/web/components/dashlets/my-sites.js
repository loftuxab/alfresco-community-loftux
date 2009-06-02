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
            this._addImapFavouriteHandling(i);
         }
         // initialize the mouse over listener
         Alfresco.util.rollover.registerHandlerFunctions(this.id, this.onSiteElementMouseEntered, this.onSiteElementMouseExited, this);
         // init mouse over
         Alfresco.util.rollover.registerListenersByClassName(this.id, 'detail-list-item', 'div');         
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
       * Adds an event handler that adds or removes the site as favourite site
       *
       * @method _addImapFavouriteHandling
       * @param siteIndex {int} The index of the site in the this.options.sites array
       * @private
       */
      _addImapFavouriteHandling: function MySites__addImapFavouriteHandling(siteIndex)
      {
         var me = this;
         var imapFavouriteSpan = Dom.get(this.id + "-imap-favourite-span-" + siteIndex);
         if (imapFavouriteSpan)
         {
            var imapFavouriteClickHandler = function (event, obj)
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
                        obj.thisComponent._addImapFavouriteHandling(obj.siteIndex);
                        obj.thisComponent._toggleImapFavourite(obj.siteIndex);
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
                        obj.thisComponent._addImapFavouriteHandling(obj.siteIndex);
                        var site = obj.thisComponent.options.sites[obj.siteIndex];
                        YAHOO.Bubbling.fire(site.isImapFavourite ? "favouriteSiteAdded" : "favouriteSiteRemoved", site);
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
               Event.removeListener(imapFavouriteSpan, "click", imapFavouriteClickHandler);
               me._toggleImapFavourite(siteIndex);
               me.preferencesService.set(
                  Alfresco.service.Preferences.IMAP_FAVOURITE_SITES + "." + me.options.sites[siteIndex].shortName,
                  me.options.sites[siteIndex].isImapFavourite,
                  responseConfig);
            };

            // Add listener to favourite icons
            Event.addListener(imapFavouriteSpan, "click", imapFavouriteClickHandler);
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
       * Helper method to change the gui and our local data model of sites
       * @method _toggleImapFavourite
       * @param siteIndex {integer} the index in our local data model
       */
      _toggleImapFavourite: function MySites__toggleImapFavourite(siteIndex)
      {
         var span = YAHOO.util.Dom.get(this.id + "-imap-favourite-span-" + siteIndex);
         this.options.sites[siteIndex].isImapFavourite = !this.options.sites[siteIndex].isImapFavourite;
         if (this.options.sites[siteIndex].isImapFavourite)
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
      },
      /** Called when the mouse enters into a list item. */
      onSiteElementMouseEntered: function SiteList_onCommentElementMouseEntered(layer, args)
      {
         var elem = args[1].target;
         Dom.addClass(elem, 'over');
      },
      
      /** Called whenever the mouse exits a list item. */
      onSiteElementMouseExited: function SiteList_onCommentElementMouseExited(layer, args)
      {
         var elem = args[1].target;
         Dom.removeClass(elem, 'over');
      }
   };
})();
Alfresco.util.rollover = {};

/**
 * Attaches mouseover/exit event listener to the passed element.
 * 
 * @method Alfresco.util.rollover._attachRolloverListener
 * @param elem the element to which to add the listeners
 * @param mouseOverEventName the bubble event name to fire for mouse enter events
 * @param mouseOutEventName the bubble event name to fire for mouse out events
 */
Alfresco.util.rollover._attachRolloverListener = function(elem, mouseOverEventName, mouseOutEventName)
{  
   var eventElem = elem, relTarg;
     
   var mouseOverHandler = function(e)
   {
      // find the correct target element and check whether we only moved between
      // subelements of the hovered element
      if (!e)
      {
         e = window.event;
      }
      relTarg = (e.relatedTarget !== undefined) ? e.relatedTarget : e.fromElement;
      while (relTarg && relTarg != eventElem && relTarg.nodeName != 'BODY')
      {
         relTarg = relTarg.parentNode;
      }
      if (relTarg == eventElem)
      {
         return;
      }
    
      // the mouse entered the element, fire an event to inform about it
      YAHOO.Bubbling.fire(mouseOverEventName,
      {
         event: e,
         target: eventElem
      });
   };
 
   var mouseOutHandler = function(e)
   {
      // find the correct target element and check whether we only moved between
      // subelements of the hovered element
      if (!e)
      {
         e = window.event;         
      }
      relTarg = (e.relatedTarget !== undefined) ? e.relatedTarget : e.toElement;
      while (relTarg !== null && relTarg != eventElem && relTarg.nodeName != 'BODY')
      {
         relTarg = relTarg.parentNode;
      }
      if (relTarg == eventElem)
      {
         return;
      }
     
      // the mouse exited the element, fire an event to inform about it
      YAHOO.Bubbling.fire(mouseOutEventName,
      {
         event: e,
         target: eventElem
      });
   };
 
   YAHOO.util.Event.addListener(elem, 'mouseover', mouseOverHandler);
   YAHOO.util.Event.addListener(elem, 'mouseout', mouseOutHandler);
};

/**
 * Register rollover listeners to elements identified by a class and tag name.
 * 
 * @param htmlId the id of the component for which the listeners get registered.
 *        This id is used to distinguish events from different components.
 * @param className the class name of elements to add the listener to
 * @param tagName the tag name of elements to add the listener to.
 */
Alfresco.util.rollover.registerListenersByClassName = function(htmlId, className, tagName)
{
   var mouseEnteredBubbleEventName = 'onRolloverMouseEntered-' + htmlId;
   var mouseExitedBubbleEventName = 'onRolloverMouseExited-' + htmlId;
   var elems = YAHOO.util.Dom.getElementsByClassName(className, tagName, htmlId);
   for (var x = 0; x < elems.length; x++)
   {
      Alfresco.util.rollover._attachRolloverListener(elems[x], mouseEnteredBubbleEventName, mouseExitedBubbleEventName);
   }
};


/**
 * Register handle functions that handle the mouse enter/exit events
 * 
 * @param htmlId the id of the component for which the listeners got registered
 * @param mouseEnteredFn the function to call for mouse entered events
 * @param mouseExitedFunction the function to call for mouse exited events
 * @param scope the object which is used as scope for the function execution
 */
Alfresco.util.rollover.registerHandlerFunctions = function(htmlId, mouseEnteredFn, mouseExitedFn, scope)
{
   // register bubble events
   var mouseEnteredBubbleEventName = 'onRolloverMouseEntered-' + htmlId;
   var mouseExitedBubbleEventName = 'onRolloverMouseExited-' + htmlId;
   YAHOO.Bubbling.on(mouseEnteredBubbleEventName, mouseEnteredFn, scope);
   YAHOO.Bubbling.on(mouseExitedBubbleEventName, mouseExitedFn, scope);
};