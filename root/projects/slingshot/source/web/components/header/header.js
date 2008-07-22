/*
 *** Alfresco.Header
*/
(function()
{
   Alfresco.Header = function(htmlId)
   {
      this.name = "Alfresco.Header";
      this.id = htmlId;
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);
      
      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require([], this.componentsLoaded, this);

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
          * @default null
          */
         siteId: "",
         searchType: ""
      },
      
      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       */
      setOptions: function DiscussionsTopic_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },
      
      /**
       * Set messages for this component.
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {Alfresco.Search} returns 'this' for method chaining
       */
      setMessages: function Search_setMessages(obj)
      {
         Alfresco.util.addMessages(obj, this.name);
         return this;
      },
      
      componentsLoaded: function()
      {
         YAHOO.util.Event.onContentReady(this.id, this.onReady, this, true);           
      },
      
      onReady: function Header_onReady()
      {
         YAHOO.util.Event.addListener(this.id+"-searchtext", "focus", this.focusSearchText, null, this);
         YAHOO.util.Event.addListener(this.id+"-searchtext", "blur", this.blurSearchText, null, this);
         

         
         this.defaultSearchText();
         
         // register the "enter" event on the search text field
         //var zinput = document.getElementById(this.id + "-searchtext");
         var zinput = YAHOO.util.Dom.get(this.id + "-searchtext");
         var me = this;
         new YAHOO.util.KeyListener(
            zinput, 
            {
               keys:13
            }, 
            {
               fn: function() {
                  me.doSearch()
               },
               scope:this,
               correctScope:true
            }, 
            "keydown" 
         ).enable();


		YAHOO.util.Event.onContentReady(me.id+"-searchtogglemenu", function () {
			var oMenu = new YAHOO.widget.Menu(me.id+"-searchtogglemenu");
			oMenu.render();
			oMenu.owner = me;
			oMenu.subscribe("show", oMenu.focus);
			
			YAHOO.util.Event.addListener(me.id+"-search-tbutton", "click", me.openToggleSearchMenu, null, oMenu);
		});
      },
      
      focusSearchText: function () {
		if(YAHOO.util.Dom.hasClass(this.id + "-searchtext", "gray")) {
			YAHOO.util.Dom.get(this.id + "-searchtext").value = "";
			YAHOO.util.Dom.removeClass(this.id + "-searchtext", "gray");
		}
		else {
			YAHOO.util.Dom.get(this.id + "-searchtext").select();
		}
      },
      
      blurSearchText: function () {
		var searchVal = YAHOO.util.Dom.get(this.id + "-searchtext").value;
		if(searchVal.length == 0) {
			this.defaultSearchText();
		}
      }, 
      
      defaultSearchText: function() {
			YAHOO.util.Dom.get(this.id + "-searchtext").value = this._getToggleLabel(this.options.searchType);
			YAHOO.util.Dom.addClass(this.id + "-searchtext", "gray");
      },
      
      openToggleSearchMenu: function() {
      	this.show();
      	var coord = YAHOO.util.Dom.getXY(this.owner.id+"-search-tbutton");
      	coord[0] -= (YAHOO.util.Dom.get(this.owner.id+"-searchtogglemenu").offsetWidth - YAHOO.util.Dom.get(this.owner.id+"-search-tbutton").offsetWidth);
      	coord[1] += YAHOO.util.Dom.get(this.owner.id+"-search-tbutton").offsetHeight;
	    YAHOO.util.Dom.setXY(this.id, coord);       	
      },
      
      doToggleSearchType: function(newVal) {
      	this.options.searchType = newVal;
      	this.defaultSearchText();
      },
      
      /**
       * Called by the Search component to tell the header that
       * no page refresh is required for a new search
       */
      onSearchComponentExists: function Header_onSearchComponentExists(layer, args)
      {
         this.searchExists = true;
      },
      
      /**
       * Will trigger a search, either through page refresh or a bubble event
       */
      doSearch: function()
      {
         var searchTerm = YAHOO.util.Dom.get(this.id + "-searchtext").value;
         var searchAll =  (this.options.searchType == "all");

         if (this.searchExists)
         {
            // fire a bubble event to issue a new search
            // we only need to supply the term and whether in-site should be searched
            var data = {
                searchTerm: searchTerm,
                searchAll: searchAll
            }
            YAHOO.Bubbling.fire("onSearch", data);
         }
         else
         {
            // redirect to the search page
            var url = Alfresco.constants.URL_CONTEXT + "page/";
            if (this.options.siteId.length > 0)
            {
               url += "site/" + this.options.siteId + "/";
            }
            url += "search?t=" + searchTerm;
            if (this.options.siteId.length > 0)
            {
               url += "&a=" + searchAll;
            }
            window.location =  url;
         }
      },

	  _getToggleLabel: function(type)
      {
         if (type == 'all')
         {
            return this._msg("header.search.searchall");
         }
         else
         {
            return this._msg("header.search.searchsite", this.options.siteTitle);
         }
      },
      
      /**
       * Gets a custom message
       *
       * @method _msg
       * @param messageId {string} The messageId to retrieve
       * @return {string} The custom message
       * @private
       */
      _msg: function Search__msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, this.name, Array.prototype.slice.call(arguments).slice(1));
      }      
   };
})();
