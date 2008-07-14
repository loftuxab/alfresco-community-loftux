/*
 * Alfresco.TopicListTags
 */
(function()
{
   Alfresco.TopicListTags = function(htmlId)
   {
      this.name = "Alfresco.TopicListTags";
      this.id = htmlId;
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);
      
      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require([], this.componentsLoaded, this);
      
      return this;
   }
   
   Alfresco.TopicListTags.prototype =
   {
      componentsLoaded: function TopicListTags_componentsLoaded()
      {
         YAHOO.util.Event.onContentReady(this.id, this.onReady, this, true);
      },
   
      onReady: function TopicListTags_onReady()
      {
         Alfresco.util.registerDefaultActionHandler(this.id, "tag-link-li", "li", this);
      },

      onTagSelection: function TopicListTags_onTagSelection(htmlId, ownerId, param)
      {
         YAHOO.Bubbling.fire('onSetTopicListParams', {tag : param});
      }
   }
})();
