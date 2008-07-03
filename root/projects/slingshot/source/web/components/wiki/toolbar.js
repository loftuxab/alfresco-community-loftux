/*
 *** Alfresco.Wiki
*/
(function()
{
	Alfresco.WikiToolbar = function(containerId)
   {
	   this.name = "Alfresco.WikiToolbar";
      this.id = containerId;

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "container", "connection"], this.componentsLoaded, this);

      return this;
   };

	Alfresco.WikiToolbar.prototype =
	{
	   /**
   	  * Sets the current site for this component.
   	  * 
   	  * @property siteId
   	  * @type string
   	  */
   	setSiteId: function(siteId)
   	{
   		this.siteId = siteId;
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
   	   this.panel = new YAHOO.widget.Panel(this.id + "-createpanel", { width:"320px", visible:false, constraintoviewport:true } );
	      this.panel.render();
	      
	      var saveButton = Alfresco.util.createYUIButton(this, "save-button", this.onSaveClick,
	      {
	      	type: "push"
	      });
	      
	      var createButton = Alfresco.util.createYUIButton(this, "create-button", this.onCreateClick,
	      {
	      	type: "push"
	      });
	      
	   },
	   
	   onCreateClick: function(e)
	   {
	      this.panel.show();
	   },
	   
	   onSaveClick: function(e)
	   {
	      var elem = YAHOO.util.Dom.get(this.id + "-title");
	      if (elem)
	      {
	         var title = elem.value;
	         if (title)
	         {
	            title = title.replace(/\s+/g, "_");
	            // Change the location bar
   	         window.location = Alfresco.constants.URL_CONTEXT + "page/wiki?site=" + this.siteId + "&title=" + title;
	         }
	         
	      } 
	   }
   };

})();   