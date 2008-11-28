/*
 *** Alfresco.FormUI
*/
(function()
{
   Alfresco.FormUI = function(htmlId)
   {
      this.name = "Alfresco.FormUI";
      this.id = htmlId;
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);
      
      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "menu", "container"], this.componentsLoaded, this);

      return this;
   };

   Alfresco.FormUI.prototype =
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
