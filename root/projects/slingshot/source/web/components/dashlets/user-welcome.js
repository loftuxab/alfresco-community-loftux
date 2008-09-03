/*
 * Alfresco.UserWelcome
 *
 * Registers a event handler on the 'Remove Me' button to have the component remove itself.
 *
 */
(function()
{
   Alfresco.UserWelcome = function(htmlId)
   {
      this.name = "Alfresco.UserWelcome";
      this.id = htmlId;
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);
      
      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["grids"], this.componentsLoaded, this);
      
      return this;
   }

   Alfresco.UserWelcome.prototype =
   {
  	   
      /**
       * CreateSite module instance.
       *
       * @property createSite
       * @type Alfresco.module.CreateSite
       */
      createSite: null,

      /**
		 * Fired by YUILoaderHelper when required component script files have
		 * been loaded into the browser.
		 *
		 * @method onComponentsLoaded
	    */	
      componentsLoaded: function()
      {
         YAHOO.util.Event.onContentReady(this.id, this.onReady, this, true);
      },
      
      /**
	    * Fired by YUI when parent element is available for scripting.
	    * Initialises components, including YUI widgets.
	    *
	    * @method onReady
	    */ 
      onReady: function()
      {
         // Listen on clicks for the create site link
         var createSiteLink = document.getElementById(this.id + "-createSite-button");
         YAHOO.util.Event.addListener(createSiteLink, "click", this.onCreateSiteLinkClick, this, true);
      },

      /**
       * Fired by YUI Link when the "Create site" label is clicked
       * @method onCreateSiteLinkClick
       * @param event {domEvent} DOM event
       */
      onCreateSiteLinkClick: function(event)
      {
         // Create the CreateSite module if it doesnt exist
         if (this.createSite === null)
         {
            this.createSite = Alfresco.module.getCreateSiteInstance();
         }
         // and show it
         this.createSite.show();
      }

   };

})();