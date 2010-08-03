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
      Event = YAHOO.util.Event,
      Selector = YAHOO.util.Selector;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML;

   Alfresco.Header = function(htmlId)
   {
      return Alfresco.Header.superclass.constructor.call(this, "Alfresco.Header", htmlId, ["button", "menu", "container"]);
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
          * Number of characters required for a search.
          *
          * @property minSearchTermLength
          * @type int
          * @default 1
          */
         minSearchTermLength: 1,
         
         /**
          * URI replacement tokens
          * 
          * @property tokens
          * @type object
          * @default {}
          */
         tokens: {}
      },
      
      /**
       * Default search text
       *
       * @property defaultSearchText
       * @type string
       */
      defaultSearchText: null,

      /**
       * Last status update time
       *
       * @property statusUpdateTime
       * @type Date
       */
      statusUpdateTime: null,
      
      /**
       * Fired by YUI when parent element is available for scripting.
       * Initial History Manager event registration
       *
       * @method onReady
       */
      onReady: function Header_onReady()
      {
         this.replaceUriTokens();
         this.configureSearch();
         this.configureMyStatus();
      },


      /**
       * About Share Handlers
       */

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
       * Header Items Handlers
       */

      /**
       * Token replacement for header item URLs
       *
       * @method replaceUriTokens
       */
      replaceUriTokens: function Header_replaceUriTokens()
      {
         var tokens = YAHOO.lang.merge(Alfresco.constants.URI_TEMPLATES, Alfresco.constants.HELP_PAGES, this.options.tokens),
            links = Selector.query("a", this.id),
            link;
         
         for (var i = 0, ii = links.length; i < ii; i++)
         {
            link = links[i];
            if (link.hasAttribute("templateUri"))
            {
               link.href = Alfresco.util.renderUriTemplate(link.attributes.templateUri.value, tokens);
            }
         }
      },


      /**
       * Search Handlers
       */
      
      /**
       * Configure search area
       *
       * @method configureSearch
       */
      configureSearch: function Header_configureSearch()
      {
         this.widgets.searchBox = Dom.get(this.id + "-searchText");
         this.defaultSearchText = this.msg("header.search.default");
         
         Event.addListener(this.widgets.searchBox, "focus", this.onSearchFocus, null, this);
         Event.addListener(this.widgets.searchBox, "blur", this.onSearchBlur, null, this);
         
         this.setDefaultSearchText();
         
         // Register the "enter" event on the search text field
         var me = this;
         
         this.widgets.searchEnterListener = new YAHOO.util.KeyListener(this.widgets.searchBox,
         {
            keys: YAHOO.util.KeyListener.KEY.ENTER
         }, 
         {
            fn: me.submitSearch,
            scope: this,
            correctScope: true
         }, "keydown").enable();

         this.widgets.searchMore = new YAHOO.widget.Button(this.id + "-search_more",
         {
            type: "menu",
            menu: this.id + "-searchmenu_more"
         });
      },
      
      /**
       * Update image class when search box has focus.
       *
       * @method onSearchFocus
       */
      onSearchFocus: function Header_onSearchFocus()
      {
         if (this.widgets.searchBox.value == this.defaultSearchText)
         {
            Dom.removeClass(this.widgets.searchBox, "faded");
            this.widgets.searchBox.value = "";
         }
         else
         {
            this.widgets.searchBox.select();
         }
      },
      
      /**
       * Set default search text when box loses focus and is empty.
       *
       * @method onSearchBlur
       */
      onSearchBlur: function Header_onSearchBlur()
      {
         var searchText = YAHOO.lang.trim(this.widgets.searchBox.value);
         if (searchText.length === 0)
         {
            /**
             * Since the blur event occurs before the KeyListener gets
             * the enter we give the enter listener a chance of testing
             * against "" instead of the help text.
             */
            YAHOO.lang.later(100, this, this.setDefaultSearchText, []);
         }
      }, 
      
      /**
       * Set default search text for search box.
       *
       * @method setDefaultSearchText
       */
      setDefaultSearchText: function Header_setDefaultSearchText()
      {
         Dom.addClass(this.widgets.searchBox, "faded");
         this.widgets.searchBox.value = this.defaultSearchText;
      },

      /**
       * Get current search text from search box.
       *
       * @method getSearchText
       */
      getSearchText: function Header_getSearchText()
      {
         return YAHOO.lang.trim(this.widgets.searchBox.value);
      },
      
      /**
       * Will trigger a search, via a page refresh to ensure the Back button works correctly
       *
       * @method submitSearch
       */
      submitSearch: function Header_submitSearch()
      {
         var searchText = this.getSearchText();
         if (searchText.replace(/\*/g, "").length < this.options.minSearchTermLength)
         {
            Alfresco.util.PopupManager.displayMessage(
            {
               text: this.msg("message.minimum-length", this.options.minSearchTermLength)
            });
         }
         else
         {
            // Redirect to the search page
            window.location = $siteURL("search?t=" + encodeURIComponent(searchText));
         }
      },
      
      
      /**
       * My Status handlers
       */
      
      /**
       * Configure My Status UI
       *
       * @method configureMyStatus
       */
      configureMyStatus: function Header_configureMyStatus()
      {
         this.widgets.statusBox = Dom.get(this.id + "-statusText");
         this.widgets.statusTime = Dom.get(this.id + "-statusTime");
         var statusISOTime = this.widgets.statusTime.attributes.title.value;
         if (statusISOTime !== "")
         {
            this.statusUpdateTime = Alfresco.util.fromISO8601(statusISOTime);
         }
         this.setStatusRelativeTime();

         // Register the "enter" event on the status text field
         var me = this;
         
         this.widgets.statusEnterListener = new YAHOO.util.KeyListener(this.widgets.statusBox,
         {
            keys: YAHOO.util.KeyListener.KEY.ENTER
         }, 
         {
            fn: me.submitStatus,
            scope: this,
            correctScope: true
         }, "keydown").enable();

         this.widgets.submitStatus = new YAHOO.widget.Button(this.id + "-submitStatus");
         this.widgets.submitStatus.on("click", this.submitStatus, this.widgets.submitStatus, this);
      },

      /**
       * Get current status text from textarea.
       *
       * @method getStatusText
       */
      getStatusText: function Header_getStatusText()
      {
         return YAHOO.lang.trim(this.widgets.statusBox.value);
      },

      /**
       * Updates relative status time display.
       *
       * @method setStatusRelativeTime
       */
      setStatusRelativeTime: function Header_setStatusRelativeTime()
      {
         var relativeTime = (this.statusUpdateTime === null) ? this.msg("status.never-updated") : Alfresco.util.relativeTime(this.statusUpdateTime);
         this.widgets.statusTime.innerHTML = this.msg("status.updated", relativeTime);
      },
      
      
      /**
       * Submit status handler
       *
       * @method submitStatus
       */
      submitStatus: function Header_submitStatus()
      {
         Alfresco.util.Ajax.jsonPost(
         {
            url: Alfresco.constants.PROXY_URI + "/slingshot/profile/userstatus",
            dataObj:
            {
               status: this.getStatusText()
            },
            successCallback:
            {
               fn: this.onStatusUpdated,
               scope: this
            },
            failureMessage: this.msg("message.status.failure")
         });
      },

      /**
       * Status submitted handler
       *
       * @method onStatusUpdated
       */
      onStatusUpdated: function Header_onStatusUpdated(response)
      {
         this.statusUpdateTime = Alfresco.util.fromISO8601(response.json.userStatusTime.iso8601);
         this.setStatusRelativeTime();
         Alfresco.util.PopupManager.displayMessage(this.msg("message.status.success"));
      }
   });
})();
