/*
 *** Alfresco.DocListTree
*/
(function()
{
   Alfresco.DocListTree = function(htmlId)
   {
      this.name = "Alfresco.DocListTree";
      this.id = htmlId;
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);
      
      /* Load YUI Components */
      new Alfresco.util.YUILoaderHelper().load(["event"], this.componentsLoaded, this);
      
      return this;
   }
   
   Alfresco.DocListTree.prototype =
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
