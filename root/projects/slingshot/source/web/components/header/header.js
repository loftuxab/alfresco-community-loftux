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
         siteId: ""
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
       
      componentsLoaded: function()
      {
         YAHOO.util.Event.onContentReady(this.id, this.onReady, this, true);
      },
      
      onReady: function Header_onReady()
      {
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
         var searchAll = ! (YAHOO.util.Dom.get(this.id + "-searchtype").value == "site");

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
            url += "search?searchTerm=" + searchTerm;
            if (this.options.siteId.length > 0)
            {
               url += "&searchAll=" + searchAll;
               url += "&site=" + this.options.siteId;
            }
            window.location =  url;
         }
      }
   };
})();
