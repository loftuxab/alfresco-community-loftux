/*
 *** Alfresco.WikiFilter
*/
(function()
{
   Alfresco.WikiFilter = function(containerId)
   {
	   this.name = "Alfresco.WikiFilter";
      this.id = containerId;

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require([], this.componentsLoaded, this);
      return this;
   };
   
   Alfresco.WikiFilter.prototype = 
   {
      /**
       * Highlights the currently selected link filter.
       *
       * @method setSelected
       * @param selected {String} the id of the (currently) selected link
       */
      setSelected: function(selected)
      {
         this.selected = (selected === "") ? "all" : selected; 
      },
      
      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
       componentsLoaded: function()
       {
          YAHOO.util.Event.onContentReady(this.id, this.init, this, true);
       },

       /**
        * Fired by YUI when parent element is available for scripting.
        * Initialises components, including YUI widgets.
        *
        * @method init
        */
        init: function()
        {
           var li = YAHOO.util.Dom.get(this.id + "-" + this.selected);
           if (li)
           {
              // Apply the appropriate style
              YAHOO.util.Dom.addClass(li, "selected");
           }
        }
   };
   
})();