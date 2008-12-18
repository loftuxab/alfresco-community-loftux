/*
 * Alfresco.DiscussionsToolbar
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      Element = YAHOO.util.Element;

   Alfresco.DiscussionsToolbar = function(containerId)
   {
      this.name = "Alfresco.DiscussionsToolbar";
      this.id = containerId;
      this.widgets = {};
      this.modules = {};
      this.options = {};

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "container", "connection"], this.onComponentsLoaded, this);

      return this;
   };

   Alfresco.DiscussionsToolbar.prototype =
   {

      /**
       * Object container for storing YUI widget instances.
       *
       * @property widgets
       * @type object
       */
      widgets : null,

      /**
       * Object container for initialization options
       *
       * @property options
       * @type {object} object literal
       */
      options:
      {
         /**
        * Sets the current site for this component.
        *
        * @property siteId
        * @type string
        */
         siteId: null,

         /**
           * The containerId in the current site
           *
           * @property containerId
           * @type string
           */
         containerId: null,

         /**
           * Decides if the create button should be enabled or not
           *
           * @property allowCreate
           * @type string
           */
         allowCreate: null

      },


      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.DocumentList} returns 'this' for method chaining
       */
      setOptions: function DV_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },

      /**
       * Set messages for this component.
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {Alfresco.DocListTree} returns 'this' for method chaining
       */
      setMessages: function(obj)
      {
         Alfresco.util.addMessages(obj, this.name);
         return this;
      },

      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function()
      {
         Event.onContentReady(this.id, this.init, this, true);
      },

      /**
       * Fired by YUI when parent element is available for scripting.
       * Initialises components, including YUI widgets.
       *
       * @method init
       */
      init: function()
      {
         // Create button
         this.widgets.createButton = Alfresco.util.createYUIButton(this, "create-button", this.onNewTopicClick,
         {
            disabled: !this.options.allowCreate
         });

         // Rss Feed button
         this.widgets.rssFeedButton = Alfresco.util.createYUIButton(this, "rssFeed-button", null,
         {
            type: "link"
         });

         // initialize rss feed link
         this._generateRSSFeedUrl();
      },

      /**
       * Dispatches the browser to the create a new forum topic
       *
       * @method onNewTopicClick
       * @param e {object} DomEvent
       */
      onNewTopicClick: function (e)
      {         
         window.location.href = Alfresco.constants.URL_CONTEXT + "page/site/" + this.options.siteId + "/discussions-createtopic";
      },

      /**
       * Generates the HTML mark-up for the RSS feed link
       *
       * @method _generateRSSFeedUrl
       * @private
       */
      _generateRSSFeedUrl: function DiscussionsTopicList__generateRSSFeedUrl()
      {
         var url = Alfresco.constants.URL_FEEDSERVICECONTEXT + "components/discussions/rss?site=" + this.options.siteId;
         this.widgets.rssFeedButton.set("href", url);
      }

   };

})();