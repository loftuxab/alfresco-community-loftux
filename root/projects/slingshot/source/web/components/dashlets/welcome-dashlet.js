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
  	   setComponentId: function(id)
  	   {
  	      this.componentId = id;
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
		
		onRemoveSelect: function(e)
		{
		   // Make the call to have the component remove itself
		   Alfresco.util.Ajax.request({
				url: Alfresco.constants.URL_SERVICECONTEXT + "modules/remove-component",
				method: Alfresco.util.Ajax.POST,
				requestContentType: Alfresco.util.Ajax.JSON,
				dataObj: {
				   componentId: this.componentId
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
		
		onComponentRemoved: function(e)
		{
		   // Reload the dashboard
		   window.location.reload();  
		},
		
		onRemoveFailed: function(e)
		{
		   
		}
	
	};
	
})();