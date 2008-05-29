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
	  	setSiteId: function(siteId)
		{
			this.siteId = siteId;
		},
		
 		componentsLoaded: function()
    	{
			YAHOO.util.Event.onContentReady(this.id, this.init, this, true);
		},
       
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
				
					cal.addRenderer(selected, function(workingDate, cell) {
						cell.innerHTML = '<a href="calendar?view=tab" class="' + this.Style.CSS_CELL_SELECTOR + '">' + this.buildDayLabel(workingDate) + "</a>"; 
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
	
		onFailure: function(o)
		{
			/* Failed */
			alert("Authentication failed");
		}	
   
	};
})();
