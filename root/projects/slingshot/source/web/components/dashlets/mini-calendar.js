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
			//var cal = new YAHOO.widget.Calendar("calendar");
			//cal.render();
		
			var uriEvents = Alfresco.constants.PROXY_URI + "calendar/eventList?site=" + this.siteId;
		
			var callback = 
			{
				success: this.onSuccess,
				failure: this.onFailure,
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
		   var html = "";
		   try 
		   {
				var eventList = YAHOO.lang.JSON.parse(o.responseText);
				var now = new Date();
				
				for (var key in eventList)
   			{
   				if (eventList.hasOwnProperty(key))
   				{
   					var dateParts = key.split("/");
   					var date = YAHOO.widget.DateMath.getDate(dateParts[2], (dateParts[0] - 1), dateParts[1]);
   					if (date > now)
   					{
      					html += this._dayRenderer(date, eventList);
   					}
   				}
   			}
         } 
         catch (e)
         {
            // Do nothing
            html = "Could not load calendar data";
         }
         
         var div = document.getElementById(this.id + "-eventsContainer");
         div.innerHTML = html;
		},
		
		_dayRenderer: function(date, eventData)
		{
		   var theDate = Alfresco.util.formatDate(date, "m/d/yyyy");
   		var events = eventData[theDate];
   		var html = "", item;
   		if (events && events.length > 0)
   		{
   			var title = Alfresco.util.formatDate(date, "fullDate");
				var url = Alfresco.constants.URL_CONTEXT + "page/site/" + this.siteId + "/calendar?date=" + theDate;
   			html += '<div class="detail-list-item">'
   			html += '<div class="icon"><img src="' + Alfresco.constants.URL_CONTEXT + '/components/calendar/images/calendar-16.png" alt="day" /></div>'
   			html += '<div class="details2"><h4>' + title + '</h4>';
   			for (var i = 0, ii = events.length; i < ii; i++)
   			{
   				item = events[i];
   				html += '<div><span>' + item.start + ' <a href="' + url + '">' + item.name + '</a></span></div>';
   			}
   			html += '</div></div>';
   		}
   		return html;   
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
			//alert("Failed to load calendar data.");
		}	
   
	};
})();
