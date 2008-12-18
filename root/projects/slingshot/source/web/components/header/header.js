/*
 *** Alfresco.Header
*/
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event;

   Alfresco.Header = function(htmlId)
   {
      this.name = "Alfresco.Header";
      this.id = htmlId;

      this.widgets = {};
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);
      
      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require([], this.onComponentsLoaded, this);

      // give the search component a change to tell the header that it has been loaded
      // (and thus no page reload is required for a new search)
      YAHOO.Bubbling.on("searchComponentExists", this.onSearchComponentExists, this);
      
      return this;
   };

   Alfresco.Header.prototype =
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
          * Current search type.
          * 
          * @property searchType
          * @type string
          * @default ""
          */
         searchType: ""
      },
      
      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.Header} returns 'this' for method chaining
       */
      setOptions: function Header_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },
      
      /**
       * Set messages for this component.
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {Alfresco.Header} returns 'this' for method chaining
       */
      setMessages: function Header_setMessages(obj)
      {
         Alfresco.util.addMessages(obj, this.name);
         return this;
      },
      
      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function Header_onComponentsLoaded()
      {
         Event.onContentReady(this.id, this.onReady, this, true);           
      },
      
      /**
       * Fired by YUI when parent element is available for scripting.
       * Initial History Manager event registration
       *
       * @method onReady
       */
      onReady: function Header_onReady()
      {
         Event.addListener(this.id + "-searchtext", "focus", this.focusSearchText, null, this);
         Event.addListener(this.id + "-searchtext", "blur", this.blurSearchText, null, this);
         Event.addListener(this.id + "-search-sbutton", "click", this.doSearch, null, this);
         
         this.defaultSearchText();
         
         // register the "enter" event on the search text field
         var zinput = Dom.get(this.id + "-searchtext");
         var me = this;
         new YAHOO.util.KeyListener(zinput, 
         {
            keys: 13
         }, 
         {
            fn: me.doSearch,
            scope: this,
            correctScope: true
         }, "keydown").enable();
                         
         var searchMenu = new YAHOO.widget.Menu(this.id + "-searchtogglemenu");
         searchMenu.render();
         searchMenu.owner = this;
         searchMenu.subscribe("show", searchMenu.focus);

         Event.addListener(this.id + "-search-tbutton", "click", this.openToggleSearchMenu, null, searchMenu);
         Dom.removeClass(this.id + "-searchtogglemenu", "hidden");

         var sitesMenu = new YAHOO.widget.Menu(this.id + "-sites-menu");
         sitesMenu.render();
         sitesMenu.subscribe("hide", this.onSitesMenuHide, this, true);
         this.widgets.sitesMenu = sitesMenu;

         var sitesButton = new YAHOO.widget.Button(this.id + "-sites",
         {
            type: "menu"
         });
         sitesButton.subscribe("click", this.onSitesMenuShow, this, true);
      },
      
      /**
       * Update image class when sarch box has focus.
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
         var searchVal = Dom.get(this.id + "-searchtext").value;
         if (searchVal.length == 0)
         {
            this.defaultSearchText();
         }
      }, 
      
      /**
       * Set default search text for search box.
       *
       * @method defaultSearchText
       */
      defaultSearchText: function Header_defaultSearchText()
      {
         Dom.get(this.id + "-searchtext").value = this._getToggleLabel(this.options.searchType);
         Dom.addClass(this.id + "-searchtext", "gray");
      },
      
      /**
       * Show the search options menu.
       *
       * @method openToggleSearchMenu
       */
      openToggleSearchMenu: function Header_openToggleSearchMenu()
      {
         this.show();
         var coord = Dom.getXY(this.owner.id + "-search-tbutton");
         coord[0] -= (Dom.get(this.owner.id + "-searchtogglemenu").offsetWidth - Dom.get(this.owner.id + "-search-tbutton").offsetWidth);
         coord[1] += Dom.get(this.owner.id + "-search-tbutton").offsetHeight;
         Dom.setXY(this.id, coord);       	
      },
      
      /**
       * Change the search type.
       *
       * @method doToggleSearchType
       * @param newVal {string} New search type from user input
       */
      doToggleSearchType: function Header_doToggleSearchType(newVal)
      {
         this.options.searchType = newVal;
         this.defaultSearchText();
      },
      
      /**
       * Called by the Search component to tell the header that
       * no page refresh is required for a new search
       *
       * @method onSearchComponentExists
       * @param layer {object} Unused
       * @param args {object} Unused
       */
      onSearchComponentExists: function Header_onSearchComponentExists(layer, args)
      {
         this.searchExists = true;
      },
      
      /**
       * Will trigger a search, via a page refresh to ensure the Back button works correctly
       *
       * @method doSearch
       */
      doSearch: function Header_doSearch()
      {
         var searchTerm = Dom.get(this.id + "-searchtext").value;
         if (searchTerm.length != 0)
         {
            var searchAll =  (this.options.searchType == "all");
            
            // redirect to the search page
            var url = Alfresco.constants.URL_CONTEXT + "page/";
            if (this.options.siteId.length != 0)
            {
               url += "site/" + this.options.siteId + "/";
            }
            url += "search?t=" + encodeURIComponent(searchTerm);
            if (this.options.siteId.length != 0)
            {
               url += "&a=" + searchAll;
            }
            window.location = url;
         }
      },
      
      /**
       * Returns the toggle label based on the passed-in search type
       *
       * @method _getToggleLabel
       * @param type {string} Search type
       * @return {string} i18n message corresponding to search type
       * @private
       */
	   _getToggleLabel: function Header__getToggleLabel(type)
      {
         if (type == 'all')
         {
            return this._msg("header.search.searchall");
         }
         return this._msg("header.search.searchsite", this.options.siteTitle);
      },

      /**
       * Show the sites drop-down menu
       *
       * @method onSitesMenuShow
       * @param e {object} User generated event
       */
      onSitesMenuShow: function Header_onSitesMenuShow(e)
      {
         // todo: Replace this positioning code when we use YUI 2.6.0 with position: "dynamic" and context
         // Position the menu under the link-menu-button wrapper span
         var coord = Dom.getXY(this.id + "-sites-linkMenuButton");
         coord[1] += Dom.get(this.id + "-sites-linkMenuButton").offsetHeight;
         Dom.setXY(this.widgets.sitesMenu.id, coord);
         this.widgets.sitesMenu.show();
         this.widgets.sitesMenu.focus();

         // Add a selector the link-menu-button wrapper so we can put a border around bothe the link and the button
         Dom.addClass(this.id + "-sites-linkMenuButton", "link-menu-button-menu-active");
      },

      /**
       * Hide the sites drop-down menu
       *
       * @method onSitesMenuHide
       * @param e {object} User generated event
       */
      onSitesMenuHide: function Header_onSitesMenuHide(e)
      {
         Dom.removeClass(this.id + "-sites-linkMenuButton", "link-menu-button-menu-active");
      },

      /**
       * Show the create site dialog
       *
       * @method showCreateSite
       */
      showCreateSite: function Header_showCreateSite()
      {
         Alfresco.module.getCreateSiteInstance().show();
      },

      /**
       * Gets a custom message
       *
       * @method _msg
       * @param messageId {string} The messageId to retrieve
       * @return {string} The custom message
       * @private
       */
      _msg: function Header__msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, this.name, Array.prototype.slice.call(arguments).slice(1));
      }      
   };
})();
