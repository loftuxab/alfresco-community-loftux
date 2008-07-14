/*
 * Alfresco.TopicListFilters
 */
(function()
{
   Alfresco.TopicListFilters = function(htmlId)
   {
      this.name = "Alfresco.TopicListFilters";
      this.id = htmlId;
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);
      
      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require([], this.componentsLoaded, this);
      
      return this;
   }
   
   Alfresco.TopicListFilters.prototype =
   {      
      componentsLoaded: function TopicListFilters_componentsLoaded()
      {
         YAHOO.util.Event.onContentReady(this.id, this.onReady, this, true);
      },
   
      onReady: function TopicListFilters_onReady(htmlId, ownerId, param)
      {
         Alfresco.util.registerDefaultActionHandler(this.id, "filter-link", "li", this);
      },

      selectFilter: function TopicListFilters_selectFilter(htmlId, ownerId, param)
      {
         YAHOO.Bubbling.fire('onSetTopicListParams', {filter : param});
      }
   };
})();
