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
      
      this.configDialog = null;
     
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);
      
      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require([], this.componentsLoaded, this);
      
      return this;
   }

  	Alfresco.RssFeed.prototype =
  	{  
      /**
       * Object container for initialization options
       *
       * @property options
       * @type object
       */
      options:
      {
         componentId: "",
         feedURL: "",
         limit: "all" // default is view all
      },  
      
      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       */
      setConfigOptions: function RssFeed_setConfigOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },      	   
		
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
		   var actionUrl = Alfresco.constants.URL_SERVICECONTEXT + "modules/feed/config/" + encodeURIComponent(this.options.componentId);
         
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
               },
               doSetupFormsValidation:
               {
                  fn: function RssFeed_doSetupForm_callback(form)
                  {
                     form.addValidation(this.configDialog.id + "-url", Alfresco.forms.validation.mandatory, null, "keyup");
                     form.setShowSubmitStateDynamically(true, false);
                     
                     var elem = YAHOO.util.Dom.get(this.configDialog.id + "-url");
                     if (elem)
                     {
                        elem.value = this.options.feedURL;
                     }
                     
                     var select = YAHOO.util.Dom.get(this.configDialog.id + "-limit");
                     var option, options = select.options;
                     for (var i=0; i < options.length; i++)
                     {
                        option = options[i];
                        if (option.value === this.options.limit)
                        {
                           option.selected = true;
                           break;
                        }
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