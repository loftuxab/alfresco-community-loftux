/*
 *** Alfresco.MiniCalendar
 */
(function()
{
   Alfresco.MiniCalendar = function(htmlId)
   {
      this.name = "Alfresco.MiniCalendar";
      this.id = htmlId;
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);
      
      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["calendar"], this.componentsLoaded, this);
      
      return this;
   }
   
   Alfresco.MiniCalendar.prototype =
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
			/* 
		 	* Separate the (initial) rendering of the calendar from the data loading.
		 	* If for some reason the data fails to load, the calendar will still display.
		 	*/
			var cal = new YAHOO.widget.Calendar("calendar");
			cal.render();
		
			var uriEvents = Alfresco.constants.PROXY_URI + "calendar/eventList?site=" + this.siteId;
		
			var callback = 
			{
				success: this.onSuccess,
				failure: this.onFailure,
				argument: [cal],
				scope: this
			};
		
			YAHOO.util.Connect.asyncRequest('GET', uriEvents, callback);
		},

		/**
		 * Event handler that gets fired when the calendar data for the current site.
	    * is loaded successfully.
		 *
		 * @method onSuccess
		 * @param e {object} DomEvent
		 */
		onSuccess: function(o)
		{
			try {
				var eventList = YAHOO.lang.JSON.parse(o.responseText); 
				var eventDates = [];
			
				var key; /* The key is the date of the event */
				for (key in eventList)
				{
					eventDates.push(key);
				}
			
				if (eventDates.length > 0)
				{
					var cal = o.argument[0];
					var selected = eventDates.join(",");
					var site = this.siteId;
					
					cal.addRenderer(selected, function(workingDate, cell) {
						var date = new Date(workingDate);
						var dateStr = date.getFullYear() + "/" + (date.getMonth()+1) + "/" + date.getDate();
						cell.innerHTML = '<a href="calendar?site=' + site + '&date=' + dateStr + '">' + this.buildDayLabel(workingDate) + "</a>"; 
						YAHOO.util.Dom.addClass(cell, "highlight1"); 
						return YAHOO.widget.Calendar.STOP_RENDER; 
					});
				
					cal.cfg.setProperty("selected", selected);
					cal.render();
				}
			}
			catch(e) {
				alert("Failed to parse webscript response: " + e);
			}
		},
	
		/**
		 * Event handler that gets fired when the calendar data for the current site.
	    * fails to load. Displays an alert informing the user that the data didn't load.
		 *
		 * @method onFailure
		 * @param e {object} DomEvent
		 */
		onFailure: function(o)
		{
			/* Failed */
			alert("Failed to load calendar data.");
		}	
   
	};
})();
