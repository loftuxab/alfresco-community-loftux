/*
 * Alfresco.BlogPostListFilters
 */
(function()
{
   Alfresco.BlogPostListFilters = function(htmlId)
   {
      this.name = "Alfresco.BlogPostListFilters";
      this.id = htmlId;
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);
      
      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require([], this.componentsLoaded, this);
      
      return this;
   }
   
   Alfresco.BlogPostListFilters.prototype =
   {      
      componentsLoaded: function BlogPostListFilters_componentsLoaded()
      {
         YAHOO.util.Event.onContentReady(this.id, this.onReady, this, true);
      },
   
      onReady: function BlogPostListFilters_onReady(htmlId, ownerId, param)
      {
         Alfresco.util.registerDefaultActionHandler(this.id, "filter-link", "li", this);
      },

      selectFilter: function BlogPostListFilters_selectFilter(htmlId, ownerId, param)
      {
         YAHOO.Bubbling.fire('onSetBlogPostListParams', {filter : param});
      }
   };
})();