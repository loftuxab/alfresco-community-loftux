(function()
{
   Alfresco.MySites = function(htmlId)
   {
      this.name = "Alfresco.MySites";
      this.id = htmlId;

      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "container"], this.componentsLoaded, this);

      return this;
   }

   Alfresco.MySites.prototype =
   {

      createSite: null,

      componentsLoaded: function()
      {
         YAHOO.util.Event.onContentReady(this.id, this.init, this, true);
      },

      init: function()
      {
         var createSiteButton = new YAHOO.widget.Button(this.id + "-createSite-button", {type: "button"});
         createSiteButton.subscribe("click", this.onCreateSiteButtonClick, this, true);
      },

      onCreateSiteButtonClick: function(event)
      {
         if(!this.createSite)
         {
            this.createSite = new Alfresco.module.CreateSite(this.id + "-createSite");
         }
         this.createSite.show();
      }

   };
})();
