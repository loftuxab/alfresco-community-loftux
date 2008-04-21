/*
 *** Alfresco.DocListFilter
*/
(function()
{
   Alfresco.DocListFilter = function(htmlId)
   {
      this.name = "Alfresco.DocListFilter";
      this.id = htmlId;
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);
      
      /* Load YUI Components */
      new Alfresco.util.YUILoaderHelper().load(["event"], this.componentsLoaded, this);
      
      return this;
   }
   
   Alfresco.DocListFilter.prototype =
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
