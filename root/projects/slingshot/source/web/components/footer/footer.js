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
      new Alfresco.util.YUILoaderHelper().load(["button", "menu", "containercore"], this.componentsLoaded, this);

      return this;
   };

   Alfresco.Footer.prototype =
   {
      componentsLoaded: function()
      {
         YAHOO.util.Event.onDOMReady(this.init, this, true);
      },

      init: function()
      {
      }
   };
})();
