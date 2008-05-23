/*
 *** Alfresco.Header
*/
(function()
{
   Alfresco.Header = function(htmlId)
   {
      this.name = "Alfresco.Header";
      this.id = htmlId;
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);
      
      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require([], this.componentsLoaded, this);

      return this;
   };

   Alfresco.Header.prototype =
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
