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
      
      // Decoupled event listeners
      YAHOO.Bubbling.on("topicListParamsChanged", this.onTopicListParamsChanged, this);
	  
      return this;
   }
   
   Alfresco.TopicListTags.prototype =
   {
      selectedTag : null,
      
      componentsLoaded: function TopicListTags_componentsLoaded()
      {
         YAHOO.util.Event.onContentReady(this.id, this.onReady, this, true);
      },
   
      onReady: function TopicListTags_onReady()
      {
         Alfresco.util.registerDefaultActionHandler(this.id, "tag-link-li", "li", this);
      },

      selectTag: function TopicListTags_selectTag(htmlId, ownerId, param)
      {
         YAHOO.Bubbling.fire('onSetTopicListParams', {tag : param});
      },
      
	  
      /**
       * Fired when the currently active filter has chnged
       * @method onFilterChanged
       * @param layer {string} the event source
       * @param args {object} arguments object
       */
      onTopicListParamsChanged: function TopicListTags_onTopicListParamsChanged(layer, args)
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
