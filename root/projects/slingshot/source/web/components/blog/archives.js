/*
 * Alfresco.BlogPostListArchives
 */
(function()
{
   Alfresco.BlogPostListArchives = function(htmlId)
   {
      this.name = "Alfresco.BlogPostListArchives";
      this.id = htmlId;
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);
      
      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require([], this.componentsLoaded, this);
      
      return this;
   }
   
   Alfresco.BlogPostListArchives.prototype =
   {            
      
      componentsLoaded: function BlogPostListArchives_componentsLoaded()
      {
         YAHOO.util.Event.onContentReady(this.id, this.onReady, this, true);
      },
   
      onReady: function DiscussionsTopicListTags_onReady()
      {
         // action hooks
         Alfresco.util.registerDefaultActionHandler(this.id, "archive-link", "li", this);
      },

      onMonthSelection: function DiscussionsTopicListTags_showNewTopics(htmlId, ownerId, param)
      {
         // extract the tag name from the element id
         var year = parseInt(param.substring(0, yearandmonth.indexOf('-')));
         var month = parseInt(param.substring(yearandmonth.indexOf('-') + 1));
         
         // create the date
         var fromDate = new Date(year, month, 1);
         var toDate = new Date(year, month+1, 1);
         toDate = new Date(toDate.getTime() - 1);
         
         YAHOO.Bubbling.fire('onSetBlogPostListParams', {fromDate : fromDate.getTime(), toDate : toDate.getTime()});
      }
   }
})();