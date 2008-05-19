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
      Alfresco.util.YUILoaderHelper.require(["button", "menu", "container"], this.componentsLoaded, this);

      return this;
   };

   Alfresco.Footer.prototype =
   {
      componentsLoaded: function()
      {
         YAHOO.util.Event.onContentReady(this.id, this.init, this, true);
      },

      init: function()
      {
      }
   };
})();
