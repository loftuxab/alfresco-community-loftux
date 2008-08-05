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
      
      // Decoupled event listeners
      YAHOO.Bubbling.on("postListParamsChanged", this.onPostListParamsChanged, this);
      
      return this;
   }
   
   Alfresco.BlogPostListFilters.prototype =
   {  
      /** Stores the currently selected filter. */
      selectedFilter: null,
       
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
            if (obj.filter != undefined && obj.filter.length > 0)
            {
               // Remove the old highlight, as it might no longer be correct
               if (this.selectedFilter !== null)
               {
                  YAHOO.util.Dom.removeClass(this.selectedFilter, "selected");
               }

               // Need to find the selectedFilter element, from the current filterId
               this.selectedFilter = YAHOO.util.Dom.get(this.id + "-selectFilter-" + obj.filter);

               // This component now owns the active filter
			   if (this.selectedFilter != null)
			   {
                  YAHOO.util.Dom.addClass(this.selectedFilter, "selected");
			   }
            }
            else
            {
               // Currently filtering by something other than this component
               if (this.selectedFilter !== null)
               {
                  YAHOO.util.Dom.removeClass(this.selectedFilter, "selected");
               }
            }
         }
      }
      
   };
})();