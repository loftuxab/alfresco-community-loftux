/*
 * Alfresco.WelcomeDashlet
 *
 * Registers a event handler on the 'Remove Me' button to have the component remove itself.
 *
 */
(function()
{
   Alfresco.WelcomeDashlet = function(htmlId)
   {
      this.name = "Alfresco.WelcomeDashlet";
      this.id = htmlId;
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);
      
      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["grids"], this.componentsLoaded, this);
      
      return this;
   }

  	Alfresco.WelcomeDashlet.prototype =
  	{	
  	   
      /**
       * Object container for initialization options
       *
       * @property options
       * @type Object
       */
      options:
      {
         componentId: "",
         dashboardURL: "",
      },
        	   
      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       */
      setOptions: function WelcomeDashlet_setOptions(obj)
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
	      var removeButton = Alfresco.util.createYUIButton(this, "remove-button", this.onRemoveSelect,
   		{
   			type: "push"
   		});  
		},

		/*
	 	 * Fired when the "Thanks, now remove this" button is clicked.
	 	 * Kicks off an AJAX call to remove the component from the dashboard.
	 	 * 
	 	 * @param e {Object} DomEvent
	 	 * @method  onRemoveSelect
	 	 */		
		onRemoveSelect: function(e)
		{
		   // Make the call to have the component remove itself
		   Alfresco.util.Ajax.request({
				url: Alfresco.constants.URL_SERVICECONTEXT + "modules/remove-component",
				method: Alfresco.util.Ajax.POST,
				requestContentType: Alfresco.util.Ajax.JSON,
				dataObj: {
				   componentId: this.options.componentId,
				   dashboardURL: this.options.dashboardURL
				},
				successCallback:
				{
					fn: this.onComponentRemoved,
					scope: this
				},
				failureCallback: 
				{
				   fn: this.onRemoveFailed,
				   scope: this
				}
			});
		},
		
		/**
		 * Event handler that gets fired when the component is  
		 * successfully removed from the dashboard. Reloads the current page.
		 *
		 * @param e {Object} DomEvent
		 * @method onComponentRemoved
		 */
		onComponentRemoved: function(e)
		{
		   // Reload the dashboard
		   window.location.reload();  
		},
		
		/**
		 * Event handler that gets fired when removing the component 
		 * from the dashboard fails. Currently doesn't do anything.
		 *
		 * @param e {Object} DomEvent
		 * @method onRemoveFailed
		 */ 
		onRemoveFailed: function(e)
		{
		   // Do nothing at the moment   
		}
	
	};
	
})();