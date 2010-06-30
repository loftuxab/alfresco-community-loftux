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
 * Global Header
 * 
 * @namespace Alfresco
 * @class Alfresco.Header
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

   Alfresco.Header = function(htmlId)
   {
      Alfresco.Header.superclass.constructor.call(this, "Alfresco.Header", htmlId);
      
      // Notifications that the favourite sites have been updated
      YAHOO.Bubbling.on("favouriteSiteAdded", this.onFavouriteSiteAdded, this);
      YAHOO.Bubbling.on("favouriteSiteRemoved", this.onFavouriteSiteRemoved, this);
      YAHOO.Bubbling.on("siteDeleted", this.onSiteDeleted, this);

      this.preferencesService = new Alfresco.service.Preferences();
      return this;
   };

   YAHOO.extend(Alfresco.Header, Alfresco.component.Base,
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
          * Current siteId.
          * 
          * @property siteId
          * @type string
          * @default ""
          */
         siteId: "",

         /**
          * Current site title.
          * 
          * @property siteTitle
          * @type string
          * @default ""
          */
         siteTitle: "",
         
         /**
          * Favourite sites
          * 
          * @property favouriteSites
          * @type object
          * @default {}
          */
         favouriteSites: {},

         /**
          * Number of characters required for a search.
          *
          * @property minSearchTermLength
          * @type int
          * @default 1
          */
         minSearchTermLength: 1
      },
      
      /**
       * Fired by YUI when parent element is available for scripting.
       * Initial History Manager event registration
       *
       * @method onReady
       */
      onReady: function Header_onReady()
      {
         /* Temporary fix for 1024x768 resolutions */
         if (window.screen.width < 1280)
         {
            Dom.setStyle(this.id + "-searchtext", "width", "10em");
         }
         
         Event.addListener(this.id + "-searchtext", "focus", this.focusSearchText, null, this);
         Event.addListener(this.id + "-searchtext", "blur", this.blurSearchText, null, this);
         
         this.defaultSearchText();
         
         // register the "enter" event on the search text field
         var zinput = Dom.get(this.id + "-searchtext"),
            me = this;
         
         this.widgets.enterListener = new YAHOO.util.KeyListener(zinput, 
         {
            keys: YAHOO.util.KeyListener.KEY.ENTER
         }, 
         {
            fn: me.doSearch,
            scope: this,
            correctScope: true
         }, "keydown").enable();
         
         // menu button for Advanced Search option (and future saved searches)
         this.widgets.searchButton = new YAHOO.widget.Button(this.id + "-search-tbutton",
         {
            type: "menu",
            menu: new YAHOO.widget.Menu(this.id + "-adv-search-menu"),
            menualignment: ["tr", "br"]
         });
         Dom.removeClass(this.id + "-adv-search-menu", "hidden");
         
         this.widgets.sitesMenu = new YAHOO.widget.Menu(this.id + "-sites-menu");
         this.widgets.sitesButton = new YAHOO.widget.Button(this.id + "-sites",
         {
            type: "menu",
            menu: this.widgets.sitesMenu
         });
         // Override align() function so menu can be aligned to "Sites" span, not button
         this.widgets.sitesMenu.align = function align()
         {
            this.cfg.config.context.value = [Dom.get(me.id + "-sites-linkMenuButton"), "tl", "bl"];
            return YAHOO.widget.Menu.superclass.align.apply(this, arguments);
         };
         // Listen for show and hide events on the menu so the Sites span can be styled
         this.widgets.sitesMenu.subscribe("show", function sitesMenu_onShow()
         {
            Dom.addClass(this.id + "-sites-linkMenuButton", "link-menu-button-menu-active");
         }, this, true);
         this.widgets.sitesMenu.subscribe("hide", function sitesMenu_onHide()
         {
            Dom.removeClass(this.id + "-sites-linkMenuButton", "link-menu-button-menu-active");
         }, this, true);
      },
      
      /**
       * Update image class when search box has focus.
       *
       * @method focusSearchText
       */
      focusSearchText: function Header_focusSearchText()
      {
         if (Dom.hasClass(this.id + "-searchtext", "gray"))
         {
            Dom.get(this.id + "-searchtext").value = "";
            Dom.removeClass(this.id + "-searchtext", "gray");
         }
         else
         {
            Dom.get(this.id + "-searchtext").select();
         }
      },
      
      /**
       * Set default search text when box loses focus and is empty.
       *
       * @method blurSearchText
       */
      blurSearchText: function Header_blurSearchText()
      {
         var searchVal = YAHOO.lang.trim(Dom.get(this.id + "-searchtext").value);
         if (searchVal.length === 0)
         {
            /**
             * Since the blur event occurs before the KeyListener gets
             * the enter we give the enter listener a chance of testing
             * against "" instead of the help text.
             */
            YAHOO.lang.later(100, this, this.defaultSearchText, []);
         }
      }, 
      
      /**
       * Set default search text for search box.
       *
       * @method defaultSearchText
       */
      defaultSearchText: function Header_defaultSearchText()
      {
         Dom.get(this.id + "-searchtext").value = this.msg("header.search.searchall");
         Dom.addClass(this.id + "-searchtext", "gray");
      },
      
      /**
       * Will trigger a search, via a page refresh to ensure the Back button works correctly
       *
       * @method doSearch
       */
      doSearch: function Header_doSearch()
      {
         var searchTerm = YAHOO.lang.trim(Dom.get(this.id + "-searchtext").value);
         if (searchTerm.replace(/\*/g, "").length < this.options.minSearchTermLength)
         {
            Alfresco.util.PopupManager.displayMessage(
            {
               text: this.msg("message.minimum-length", this.options.minSearchTermLength)
            });
         }
         else
         {
            // redirect to the search page
            var url = Alfresco.constants.URL_CONTEXT + "page";
            if (this.options.siteId.length !== 0)
            {
               url += "/site/" + this.options.siteId;
            }
            url += "/search?t=" + encodeURIComponent(searchTerm);
            window.location = url;
         }
      },

      /**
       * Show the Create Site dialog
       *
       * @method showCreateSite
       */
      showCreateSite: function Header_showCreateSite()
      {
         Alfresco.module.getCreateSiteInstance().show();
      },
      
      /**
       * Show the About Share dialog
       *
       * @method showAboutShare
       */
      showAboutShare: function Header_showAboutShare()
      {
         Alfresco.module.getAboutShareInstance().show();
      },
      
      /**
       * Favourite Site has been added
       *
       * @method onFavouriteSiteAdded
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onFavouriteSiteAdded: function Header_onFavouriteSiteAdded(layer, args)
      {
         var obj = args[1];
         if (obj && obj.shortName !== null)
         {
            this.options.favouriteSites[obj.shortName] = obj.title;
            this._renderFavouriteSites();
         }
      },

      /**
       * Favourite Site has been removed
       *
       * @method onFavouriteSiteAdded
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onFavouriteSiteRemoved: function Header_onFavouriteSiteRemoved(layer, args)
      {
         var obj = args[1];
         if (obj && obj.shortName !== null)
         {
            if (obj.shortName in this.options.favouriteSites)
            {
               delete this.options.favouriteSites[obj.shortName];
               this._renderFavouriteSites();
            }
         }
      },

      /**
       * Site has been deleted - maybe remove from favourites menu
       *
       * @method onSiteDeleted
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onSiteDeleted: function Header_onSiteDeleted(layer, args)
      {
         var obj = args[1];
         if (obj && obj.site !== null)
         {
            if (obj.site.shortName in this.options.favouriteSites)
            {
               delete this.options.favouriteSites[obj.site.shortName];
               this._renderFavouriteSites();
            }
         }
      },

      /**
       * Renders favourite sites into menu
       *
       * @method renderFavouriteSites
       * @private
       */
      _renderFavouriteSites: function Header__renderFavouriteSites()
      {
         var sites = [], site, sitesMenu = this.widgets.sitesMenu, sitesGroup, i, ii;
         
         // Create a sorted list of our current favourites
         for (site in this.options.favouriteSites)
         {
            if (this.options.favouriteSites.hasOwnProperty(site))
            {
               sites.push(site);
            }
         }
         sites.sort();

         sitesGroup = sitesMenu.getItemGroups()[0];
         for (i = 0, ii = sitesGroup.length; i < ii; i++)
         {
            sitesMenu.removeItem(0, 0, true);
         }
         
         Dom.setStyle(this.id + "-favouritesContainer", "display", sites.length > 0 ? "block" : "none");
         Dom.setStyle(this.id + "-favouriteSites", "display", site.length > 0 ? "block" : "none");

         for (i = 0, ii = sites.length; i < ii; i++)
         {
            sitesMenu.addItem(
            {
               text: $html(this.options.favouriteSites[sites[i]]),
               url: Alfresco.util.uriTemplate("sitedashboardpage",
               {
                  site: sites[i]
               })
            }, 0);
         }
         
         // Show/hide "Add to favourites" menu item if we're in a site
         if (this.options.siteId.length !== 0)
         {
            Dom.setStyle(this.id + "-addFavourite", "display", this.options.siteId in this.options.favouriteSites ? "none" : "block");
         }
         
         sitesMenu.render();
      },

      /**
       * Adds the current site as a favourite
       *
       * @method addAsFavourite
       */
      addAsFavourite: function Header_addAsFavourite()
      {
         var site =
         {
            shortName: this.options.siteId,
            title: this.options.siteTitle
         },
            me = this;

         var responseConfig =
         {
            failureCallback:
            {
               fn: function(event, obj)
               {
                  Alfresco.util.PopupManager.displayPrompt(
                  {
                     text: me.msg("message.siteFavourite.failure")
                  });
               },
               scope: this
            },
            successCallback:
            {
               fn: function(event, obj)
               {
                  YAHOO.Bubbling.fire("favouriteSiteAdded", obj.site);
               },
               scope: this,
               obj:
               {
                  site: site
               }
            }
         };

         this.preferencesService.set(Alfresco.service.Preferences.FAVOURITE_SITES + "." + site.shortName, true, responseConfig);
      }      
   });
})();
