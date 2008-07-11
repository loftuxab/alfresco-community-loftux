/*
 * Alfresco.BlogPostListTags
 */
(function()
{
   Alfresco.BlogPostListTags = function(htmlId)
   {
      this.name = "Alfresco.BlogPostListTags";
      this.id = htmlId;
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);
      
      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require([], this.componentsLoaded, this);
      
      return this;
   }
   
   Alfresco.BlogPostListTags.prototype =
   {
      componentsLoaded: function BlogPostListTags_componentsLoaded()
      {
         YAHOO.util.Event.onContentReady(this.id, this.onReady, this, true);
      },
   
      onReady: function BlogPostListTags_onReady()
      {
         Alfresco.util.registerDefaultActionHandler(this.id, "tag-link", "li", this);
      },

      onTagSelection: function BlogPostListTags_onTagSelection(htmlId, ownerId, param)
      {
         YAHOO.Bubbling.fire('onSetBlogPostListParams', {tag : param});
      }
   }
})();