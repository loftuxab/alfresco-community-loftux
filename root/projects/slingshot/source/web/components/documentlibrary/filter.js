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
      Alfresco.util.YUILoaderHelper.require([], this.componentsLoaded, this);
      
      return this;
   }
   
   Alfresco.DocListFilter.prototype =
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
