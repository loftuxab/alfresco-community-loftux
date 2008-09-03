/*
 *** Alfresco.CalendarToolbar
*/
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      Element = YAHOO.util.Element;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML;
   
   Alfresco.CalendarToolbar = function(containerId)
   {
      this.name = "Alfresco.CalendarToolbar";
      this.id = containerId;

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "container", "connection"], this.componentsLoaded, this);

      return this;
   };

   Alfresco.CalendarToolbar.prototype =
   {
      /**
       * Sets the current site for this component.
       * 
       * @property siteId
       * @type string
       */
      setSiteId: function(siteId)
      {
         this.siteId = siteId;
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
         /* Add Event Button */
         if (YAHOO.util.Dom.get(this.id + "-addEvent-button"))
         {
            Alfresco.util.createYUIButton(this, "addEvent-button", this.onButtonClick);
         }
      
         Alfresco.util.createYUIButton(this, "next-button", this.onNextNav);
         Alfresco.util.createYUIButton(this, "prev-button", this.onPrevNav);
         Alfresco.util.createYUIButton(this, "today-button", this.onTodayNav);
         var navButtonGroup = new YAHOO.widget.ButtonGroup(this.id + "-navigation");
         navButtonGroup.on("checkedButtonChange", this.onNavigation, navButtonGroup, this);
      },

      onNextNav: function(e)
      {
         this._fireEvent("nextNav");
      },

      onPrevNav: function(e)
      {
         this._fireEvent("prevNav");	      
      },

      onTodayNav: function(e)
      {
         this._fireEvent("todayNav");
      },

      onNavigation: function(e)
      {
         YAHOO.Bubbling.fire("viewChanged",
         {
            activeView: e.newValue.index
         })
      },

      _fireEvent: function(type)
      {
         YAHOO.Bubbling.fire(type, 
         {
            source: this
         });
      },

      /*
       * Fired when the "Add Event" button is clicked.
       * Displays the event creation form. Initialises the 
       * form if it hasn't been initialised. 
       *
       * @param e {object} DomEvent
       * @param obj {object} Object passed back from addListener method
       * @method  onButtonClick
      */	   
      onButtonClick: function(e)
      {
         // TODO: look at caching this
         var eventDialog = new Alfresco.module.AddEvent(this.id + "-addEvent");
         var options =
         {
            "siteId": this.siteId
         };

         var obj = Alfresco.util.ComponentManager.findFirst("Alfresco.CalendarView");
         if (obj)
         {
            options["displayDate"] = obj.currentDate;
         }

         eventDialog.setOptions(options);
         eventDialog.show();
      }

   };

})();