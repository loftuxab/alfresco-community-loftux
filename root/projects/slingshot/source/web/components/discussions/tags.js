/*
 * Alfresco.DiscussionsTopicListTags
 */
(function()
{
   Alfresco.DiscussionsTopicListTags = function(htmlId)
   {
      this.name = "Alfresco.DiscussionsTopicListTags";
      this.id = htmlId;
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);
      
      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require([], this.componentsLoaded, this);
      
      return this;
   }
   
   Alfresco.DiscussionsTopicListTags.prototype =
   {
      componentsLoaded: function DiscussionsTopicListTags_componentsLoaded()
      {
         YAHOO.util.Event.onContentReady(this.id, this.onReady, this, true);
      },
   
      onReady: function DiscussionsTopicListTags_onReady()
      {
         var me = this;
         
         YAHOO.Bubbling.addDefaultAction("tag-link", function DLF_filterAction(layer, args)
         {
            var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "li");
            if (owner !== null)
            {
               var action = owner.className;
               if (typeof me[action] == "function")
               {
                  me[action].call(me, owner);
               }
            }
      		 
            return true;
         });
      },

      onTagSelection: function DiscussionsTopicListTags_showNewTopics(owner)
      {
         // extract the tag name from the element id
         var tagName = owner.id.substring((this.id + "-tag-").length);
         YAHOO.Bubbling.fire('onSetTopicListParams', {tag : tagName});
      }
   }
})();