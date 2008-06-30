/*
 * Alfresco.DiscussionsTopicListFilters
 */
(function()
{
   Alfresco.DiscussionsTopicListFilters = function(htmlId)
   {
      this.name = "Alfresco.DiscussionsTopicListFilters";
      this.id = htmlId;
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);
      
      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require([], this.componentsLoaded, this);
      
      return this;
   }
   
   Alfresco.DiscussionsTopicListFilters.prototype =
   {

      /**
       * Object container for initialization options
       *
       * @property options
       * @type object
       */
      options:
      {
      },

      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.DiscussionsTopicListFilters} returns 'this' for method chaining
       */
      setOptions: function DiscussionsTopicListFilters_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },
      
      /**
       * Set messages for this component.
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {Alfresco.DiscussionsTopicListFilters} returns 'this' for method chaining
       */
      setMessages: function DiscussionsTopicListFilters_setMessages(obj)
      {
         Alfresco.util.addMessages(obj, this.name);
         return this;
      },
      
      
      componentsLoaded: function DiscussionsTopicListFilters_componentsLoaded()
      {
         YAHOO.util.Event.onContentReady(this.id, this.onReady, this, true);
      },
   
      onReady: function DiscussionsTopicListFilters_onReady()
      {
         var me = this;
         
         YAHOO.Bubbling.addDefaultAction("filter-link", function DLF_filterAction(layer, args)
         {
            var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "li");
            if (owner !== null)
            {
               var action = owner.id.substring((me.id+"-").length);
               if (typeof me[action] == "function")
               {
                  me[action].call(me);
               }
            }
      		 
            return true;
         });
      },

      showNewTopics: function DiscussionsTopicListFilters_showNewTopics()
      {
      	YAHOO.Bubbling.fire('onSetTopicListParams', {filter : "new"});
      },

      showHotTopics: function DiscussionsTopicListFilters_showHotTopics()
      {
      	YAHOO.Bubbling.fire('onSetTopicListParams', {filter : "hot"});
      },
      
      showAllTopics: function DiscussionsTopicListFilters_showAllTopics()
      {
      	YAHOO.Bubbling.fire('onSetTopicListParams', {filter : "all"});
      },
      
      showWatchingTopics: function DiscussionsTopicListFilters_showWatchingTopics()
      {
      	YAHOO.Bubbling.fire('onSetTopicListParams', {filter : "watching"});
      }
	};
   
})();