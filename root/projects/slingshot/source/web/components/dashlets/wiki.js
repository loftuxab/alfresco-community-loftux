/*
 *** Alfresco.WikiDashlet
 *
 * Aggregates events from all the sites the user belongs to.
 * For use on the user's dashboard.
 *
 */
(function()
{
   Alfresco.WikiDashlet = function(htmlId)
   {
      this.name = "Alfresco.WikiDashlet";
      this.id = htmlId;
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);
      
      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require([], this.componentsLoaded, this);
      
      this.parser = new Alfresco.WikiParser();
      
      return this;
   }

   Alfresco.WikiDashlet.prototype =
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
            * The gui id.
            *
            * @property guid
            * @type string
            */
         guid: "",

         /**
            * Current siteId.
            *
            * @property siteId
            * @type string
            */
         siteId: "",

         /**
            * The pages on this sites wiki.
            *
            * @property pages
            * @type Array
            */
         pages: []

      },

      /**
         * Set multiple initialization options at once.
         *
         * @method setOptions
         * @param obj {object} Object literal specifying a set of options
         * @return {Alfresco.WikiDashlet} returns 'this' for method chaining
         */
      setOptions: function DLTB_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },

      /**
		 * Allows the user to configure the feed for the dashlet.
		 *
		 * @property configDialog
	    * @type DOM node
		 */
      configDialog: null,
		
      /**
		 * Fired by YUILoaderHelper when required component script files have
		 * been loaded into the browser.
		 *
		 * @method onComponentsLoaded
	    */	
      componentsLoaded: function()
      {
         YAHOO.util.Event.onContentReady(this.id, this.init, this, true);
      },
      
      /**
	    * Fired by YUI when parent element is available for scripting.
	    * Initialises components, including YUI widgets.
	    *
	    * @method init
	    */ 
      init: function()
      {
         var configFeedLink = document.getElementById(this.id + "-wiki-link");
         YAHOO.util.Event.addListener(configFeedLink, "click", this.onConfigFeedClick, this, true);
         
         this.parser.URL = this._getAbsolutePath();
         var div = document.getElementById(this.id + "-scrollableList");
         div.innerHTML = this.parser.parse(div.innerHTML, this.options.pages);
      },
		
      /**
		 * Returns the absolute path (URL) to a wiki page, minus the title of the page.
		 *
		 * @method _getAbsolutePath
		 */
      _getAbsolutePath: function()
      {
         return Alfresco.constants.URL_CONTEXT + "page/site/" + this.options.siteId + "/wiki-page?title=";
      },
		
      onConfigFeedClick: function(e)
      {
         var actionUrl = Alfresco.constants.URL_SERVICECONTEXT + "modules/wiki/config/" + encodeURIComponent(this.options.guid);
         
         if (!this.configDialog)
         {
            this.configDialog = new Alfresco.module.SimpleDialog(this.id + "-configDialog").setOptions(
            {
               width: "50em",
               templateUrl: Alfresco.constants.URL_SERVICECONTEXT + "modules/wiki/config/" + this.options.siteId,
               actionUrl: actionUrl,
               onSuccess:
               {
                  fn: function WikiDashlet_onConfigFeed_callback(e)
                  {
                     var obj = YAHOO.lang.JSON.parse(e.serverResponse.responseText);
                     if (obj)
                     {
                        var content = obj["content"];
                        var div = document.getElementById(this.id + "-scrollableList");
                        div.innerHTML = this.parser.parse(content, this.options.pages);
                        
                        // Update the title
                        document.getElementById(this.id + "-title").innerHTML = obj["title"];
                     }
                  },
                  scope: this
               }
            });
         }
         else
         {
            this.configDialog.setOptions(
            {
               actionUrl: actionUrl
            });
         }
         
         this.configDialog.show();
      }
	
   };

})();