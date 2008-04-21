/*
 *** Alfresco.DocListPackager
*/
(function()
{
   Alfresco.DocListPackager = function(htmlId)
   {
      this.name = "Alfresco.DocListPackager";
      this.id = htmlId;
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);
      
      /* Load YUI Components */
      new Alfresco.util.YUILoaderHelper().load(["event"], this.componentsLoaded, this);
      
      return this;
   }
   
   Alfresco.DocListPackager.prototype =
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
