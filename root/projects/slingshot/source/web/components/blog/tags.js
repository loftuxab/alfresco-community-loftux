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
      
      // Decoupled event listeners
      YAHOO.Bubbling.on("postListParamsChanged", this.onPostListParamsChanged, this);
	  
      return this;
   }
   
   Alfresco.BlogPostListTags.prototype =
   {
      /** Currently selected tag. */
      selectedTag: null,
   
      componentsLoaded: function BlogPostListTags_componentsLoaded()
      {
         YAHOO.util.Event.onContentReady(this.id, this.onReady, this, true);
      },
   
      onReady: function BlogPostListTags_onReady()
      {
         Alfresco.util.registerDefaultActionHandler(this.id, "tag-link", "li", this);
      },

      selectTag: function BlogPostListTags_onTagSelection(htmlId, ownerId, param)
      {
         YAHOO.Bubbling.fire('onSetBlogPostListParams', {tag : param});
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
            if (obj.tag != undefined && obj.tag.length > 0)
            {
               // Remove the old highlight, as it might no longer be correct
               if (this.selectedTag !== null)
               {
                  YAHOO.util.Dom.removeClass(this.selectedTag, "selected");
               }

			   var d = new Date(obj.fromDate);
			   
               // Need to find the selectedFilter element, from the current filterId
               this.selectedTag = YAHOO.util.Dom.get(this.id + "-selectTag-" + obj.tag);

               // This component now owns the active filter
			   if (this.selectedTag != null)
			   {
                  YAHOO.util.Dom.addClass(this.selectedTag, "selected");
			   }
            }
            else
            {
               // Currently filtering by something other than this component
               if (this.selectedTag !== null)
               {
                  YAHOO.util.Dom.removeClass(this.selectedTag, "selected");
               }
            }
         }
      }
   }
})();