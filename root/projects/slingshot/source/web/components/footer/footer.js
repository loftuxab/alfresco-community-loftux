/*
 *** Alfresco.Footer
*/
(function()
{
   Alfresco.Footer = function(htmlId)
   {
      this.name = "Alfresco.Footer";
      this.id = htmlId;
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);
      
      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "menu", "container"], this.onComponentsLoaded, this);

      return this;
   };

   Alfresco.Footer.prototype =
   {
      onComponentsLoaded: function()
      {
         YAHOO.util.Event.onContentReady(this.id, this.init, this, true);
      },

      init: function()
      {
      }
   };
})();
