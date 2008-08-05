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
      
      // Decoupled event listeners
      YAHOO.Bubbling.on("postListParamsChanged", this.onPostListParamsChanged, this);
	  
      return this;
   }
   
   Alfresco.BlogPostListArchives.prototype =
   {            
   
      /** Stores the currently selected month. */
      selectedMonth: null,
      
      componentsLoaded: function BlogPostListArchives_componentsLoaded()
      {
         YAHOO.util.Event.onContentReady(this.id, this.onReady, this, true);
      },
   
      onReady: function DiscussionsTopicListTags_onReady()
      {
         // action hooks
         Alfresco.util.registerDefaultActionHandler(this.id, "archive-link", "li", this);
      },

      selectMonth: function DiscussionsTopicListTags_showNewTopics(htmlId, ownerId, param)
      {
         // extract the tag name from the element id
         var year = parseInt(param.substring(0, param.indexOf('-')));
         var month = parseInt(param.substring(param.indexOf('-') + 1));
         
         // create the date
         var fromDate = new Date(year, month, 1);
         var toDate = new Date(year, month+1, 1);
         toDate = new Date(toDate.getTime() - 1);
         
         YAHOO.Bubbling.fire('onSetBlogPostListParams', {fromDate : fromDate.getTime(), toDate : toDate.getTime()});
      },
	  
      /**
       * Fired when the currently active filter has chnged
       * @method onFilterChanged
       * @param layer {string} the event source
       * @param args {object} arguments object
       */
      onPostListParamsChanged: function BlogPostListFilters_onPostListParamsChanged(layer, args)
      {
         var obj = args[1];
         if ((obj !== null))
         {
            if (obj.fromDate != undefined)
            {
               // Remove the old highlight, as it might no longer be correct
               if (this.selectedMonth !== null)
               {
                  YAHOO.util.Dom.removeClass(this.selectedMonth, "selected");
               }

			   var d = new Date(obj.fromDate);
			   
               // Need to find the selectedFilter element, from the current filterId
               this.selectedMonth = YAHOO.util.Dom.get(this.id + "-selectMonth-" + d.getFullYear() + "-" + d.getMonth());

               // This component now owns the active filter
			   if (this.selectedMonth != null)
			   {
                  YAHOO.util.Dom.addClass(this.selectedMonth, "selected");
			   }
            }
            else
            {
               // Currently filtering by something other than this component
               if (this.selectedMonth !== null)
               {
                  YAHOO.util.Dom.removeClass(this.selectedMonth, "selected");
               }
            }
         }
      }
   }
})();