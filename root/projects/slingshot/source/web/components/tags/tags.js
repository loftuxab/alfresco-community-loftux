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
       * Registers a default action listener on <em>all</em> of the tag links in the 
       * component. Fires 'onTagSelected' event with the name of the tag that was selected.
       *
       * To register for the event, interested components should do something like this:
       * YAHOO.Bubbling.on("onTagSelected", this.onTagSelected, this); 
       *
       * @method onReady
       */   
      onReady: function TagComponent_onReady()
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
      }
   }
})();