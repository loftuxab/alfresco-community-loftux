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
         var me = this;
         
         YAHOO.Bubbling.addDefaultAction("archive-link", function BlogPostListArchives_filterAction(layer, args)
         {
            var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "li");
            if (owner !== null)
            {
               var action = owner.id.substring(0, owner.id.indexOf('-'));
               if (typeof me[action] == "function")
               {
                  me[action].call(me, owner);
               }
           }
      		 
            return true;
         });
      },

      onMonthSelection: function DiscussionsTopicListTags_showNewTopics(owner)
      {
         // extract the tag name from the element id
         var yearandmonth = owner.id.substring(("onMonthSelection-").length);
         var year = parseInt(yearandmonth.substring(0, yearandmonth.indexOf('-')));
         var month = parseInt(yearandmonth.substring(yearandmonth.indexOf('-') + 1));
         
         // create the date
         var fromDate = new Date(year, month, 1);
         var toDate = new Date(year, month+1, 1);
         toDate = new Date(toDate.getTime() - 1);
         
         YAHOO.Bubbling.fire('onSetBlogPostListParams', {fromDate : fromDate.getTime(), toDate : toDate.getTime()});
      }
   }
})();