/*
 * Alfresco.TagComponent
 */
(function()
{
   Alfresco.TagComponent = function(htmlId)
   {
      this.name = "Alfresco.TagComponent";
      this.id = htmlId;
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);
      
      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require([], this.componentsLoaded, this);
      
      return this;
   }
   
   Alfresco.TagComponent.prototype =
   {
      componentsLoaded: function TagComponent_componentsLoaded()
      {
         YAHOO.util.Event.onContentReady(this.id, this.onReady, this, true);
      },
   
      onReady: function TagComponent_onReady()
      {
         //Alfresco.util.registerDefaultActionHandler(this.id, "tag-link", "li", this);
      },

      onTagSelection: function TagComponent_onTagSelection(htmlId, ownerId, param)
      {
         YAHOO.Bubbling.fire('onSetBlogPostListParams', {tag : param});
      }
   }
})();