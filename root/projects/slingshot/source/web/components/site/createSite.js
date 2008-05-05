/*
 *** Alfresco.CreateSite
*/
(function()
{
   Alfresco.CreateSite = function(htmlId)
   {
      this.name = "Alfresco.CreateSite";
      this.id = htmlId;

      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button"], this.componentsLoaded, this);

      return this;
   };

   Alfresco.CreateSite.prototype =
   {
      componentsLoaded: function()
      {
         YAHOO.util.Event.onContentReady(this.id, this.init, this, true);
      },

      init: function()
      {
         var Dom = YAHOO.util.Dom;

         var clButton = Dom.get(this.id + "-ok-button");
         var clearButton = new YAHOO.widget.Button(clButton, {type: "button"});
         clearButton.subscribe("click", this.onOkButtonClick, this, true);
      },

      onOkButtonClick: function(type, args)
      {
         Alfresco.util.Request.doJsonForm(this.id + "-createSite-form", null,
           {successMessage: "Site created", failureMessage: "Could not create site"}
         );
          
      }

   };
})();


