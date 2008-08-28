/*
 * Alfresco.TagComponent
 */
(function()
{
   Alfresco.TagComponent = function(htmlId)
   {
      this.name = "Alfresco.TagComponent";
      this.id = htmlId;
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);
      
      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require([], this.componentsLoaded, this);
      
      return this;
   }
   
   Alfresco.TagComponent.prototype =
   {
      /**
       * Object container for initialization options
       *
       * @property options
       * @type object
       */
      options:
      {
         /**
          * Current siteId.
          * 
          * @property siteId
          * @type string
          */
         siteId: "",

         /**
          * ContainerId representing root container
          *
          * @property container
          * @type string
          */
         container: ""
      },      

      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.TagComponent} returns 'this' for method chaining
       */
       setOptions: function TagComponent_setOptions(obj)
       {
          this.options = YAHOO.lang.merge(this.options, obj);
          return this;
       },
       
      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
      componentsLoaded: function TagComponent_componentsLoaded()
      {
         YAHOO.util.Event.onContentReady(this.id, this.onReady, this, true);
      },
   
      /**
       * Fired by YUI when parent element is available for scripting.
       * Registers event handler on 'onTagRefresh' event. If a component wants to refresh
       * the tags component, they need to fire this event.
       *
       * @method onReady
       */   
      onReady: function TagComponent_onReady()
      {
         this._registerDefaultActionHandler();
         
         YAHOO.Bubbling.on("onTagRefresh", this.onTagRefresh, this);
      },
      
      /**
       * Registers a default action listener on <em>all</em> of the tag links in the 
       * component. Fires 'onTagSelected' event with the name of the tag that was selected.
       *
       * To register for the event, interested components should do something like this:
       * YAHOO.Bubbling.on("onTagSelected", this.onTagSelected, this); 
       *
       * @method _registerDefaultActionHandler
       */
      _registerDefaultActionHandler: function TagComponent_registerDefaultActionHandler()
      {
         YAHOO.Bubbling.addDefaultAction('tag-link', function(layer, args)
         {
            var link = args[1].target;
            if (link)
            {
               var tagName = link.firstChild.nodeValue;
               YAHOO.Bubbling.fire('onTagSelected', {
                  "tagname": tagName
               });
            }
            return true;
         });
      },
      
      /**
       * Function that gets called when another component fires ? 
       * Issues a request to the repo to retrieve the latest tag data.
       *
       * @method onTagRefresh
       * @param e {object} DomEvent
       */
      onTagRefresh: function TagComponent_onRefresh(e)
      {
         var url = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "api/tagscopes/site/{site}/{container}/tags?d={d}",
         {
            site: this.options.siteId,
            container: this.options.container,
            d: new Date().getTime()
         });
         
         Alfresco.util.Ajax.request({
            method: Alfresco.util.Ajax.GET,
				url: Alfresco.constants.PROXY_URI + uri,
				successCallback:
				{
					fn: this.onTagsLoaded,
					scope: this
				},
				failureMessage: "Couldn't refresh tag data"
			});
      },
      
      /**
       * Event handler that gets called when the tag data 
       * loads successfully.
       *
       * @method onTagsLoaded
       * @param e {object} DomEvent
       */ 
      onTagsLoaded: function TagComponent_onTagsLoaded(e)
      {
         var resp = YAHOO.lang.JSON.parse(e.serverResponse.responseText);
         if (resp && !YAHOO.lang.isUndefined(resp.tags))
         {
            var html = "";
            
            var tags = resp.tags;
            var tag;
            for (var i=0; i < tags.length; i++)
            {
               tag = tags[i];
               html += this._generateTagMarkup(tag);
            }
            
            var elem = document.getElementById('tagFilterLinks');
            if (elem)
            {
               elem.innerHTML = html;
               this._registerDefaultActionHandler();
            }
         }
      },
      
      /**
       * Generates the HTML for a tag.
       *
       * @method _generateTagMarkup
       * @param tag {Object} the tag to render
       */
      _generateTagMarkup: function (tag)
      {
         var html = '<li class="onTagSelection nav-label">';
         html += '<a href="#" class="tag-link nav-link">' + tag.name + '</a> (' + tag.count + ')';
         html += '</li>';
         return html;
      }
   }
})();