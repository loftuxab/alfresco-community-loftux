/*
 *** Alfresco.RssFeed
 *
 * Aggregates events from all the sites the user belongs to.
 * For use on the user's dashboard.
 *
 */
(function()
{
   Alfresco.RssFeed = function(htmlId)
   {
      this.name = "Alfresco.RssFeed";
      this.id = htmlId;
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);
      
      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require([], this.componentsLoaded, this);
      
      return this;
   }

  	Alfresco.RssFeed.prototype =
  	{
  	   guid: null,
  	   
  	   setGUID: function(guid)
  	   {
  	      this.guid = guid;
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
	      var configFeedLink = document.getElementById(this.id + "-configFeed-link");
         YAHOO.util.Event.addListener(configFeedLink, "click", this.onConfigFeedClick, this, true);
		},
		
		onConfigFeedClick: function(e)
		{
		   var actionUrl = Alfresco.constants.URL_SERVICECONTEXT + "modules/feed/config/" + encodeURIComponent(this.guid);
         
		   if (!this.configDialog)
         {
            this.configDialog = new Alfresco.module.SimpleDialog(this.id + "-configDialog").setOptions(
            {
               width: "50em",
               templateUrl: Alfresco.constants.URL_SERVICECONTEXT + "modules/feed/config",
               actionUrl: actionUrl,
               onSuccess:
               {
                  fn: function RssFeed_onConfigFeed_callback(response)
                  {
                     var txt = response.serverResponse.responseText;
                     var div = document.getElementById(this.id + "-scrollableList");
                     div.innerHTML = txt;
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