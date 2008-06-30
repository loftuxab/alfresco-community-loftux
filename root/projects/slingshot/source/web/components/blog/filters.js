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
      
      return this;
   }
   
   Alfresco.BlogPostListFilters.prototype =
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
       * @return {Alfresco.BlogPostListFilters} returns 'this' for method chaining
       */
      setOptions: function BlogPostListFilters_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },
      
      /**
       * Set messages for this component.
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {Alfresco.BlogPostListFilters} returns 'this' for method chaining
       */
      setMessages: function BlogPostListFilters_setMessages(obj)
      {
         Alfresco.util.addMessages(obj, this.name);
         return this;
      },
      
      
      componentsLoaded: function BlogPostListFilters_componentsLoaded()
      {
         YAHOO.util.Event.onContentReady(this.id, this.onReady, this, true);
      },
   
      onReady: function BlogPostListFilters_onReady()
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

      showAll: function BlogPostListFilters_showNewTopics()
      {
      	YAHOO.Bubbling.fire('onSetBlogPostListParams', {filter : "all"});
      },

      showLatest: function BlogPostListFilters_showNewTopics()
      {
      	YAHOO.Bubbling.fire('onSetBlogPostListParams', {filter : "latest"});
      },

      showDraft: function BlogPostListFilters_showHotTopics()
      {
      	YAHOO.Bubbling.fire('onSetBlogPostListParams', {filter : "mydraft"});
      },
      
      showMyPublished: function BlogPostListFilters_showAllTopics()
      {
      	YAHOO.Bubbling.fire('onSetBlogPostListParams', {filter : "mypublished"});
      },
      
      showPublishedExt: function BlogPostListFilters_showWatchingTopics()
      {
      	YAHOO.Bubbling.fire('onSetBlogPostListParams', {filter : "publishedexternal"});
      }
	};
   
})();