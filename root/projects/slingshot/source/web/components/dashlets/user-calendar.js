/*
 *** Alfresco.UserCalendar
 *
 * Aggregates events from all the sites the user belongs to.
 * For use on the user's dashboard.
 *
 */
(function()
{
	Alfresco.UserCalendar = function(htmlId)
   	{
    	this.name = "Alfresco.UserCalendar";
      	this.id = htmlId;
      
      	/* Register this component */
      	Alfresco.util.ComponentManager.register(this);
      
      	/* Load YUI Components */
      	Alfresco.util.YUILoaderHelper.require(["calendar"], this.componentsLoaded, this);
      
      	return this;
   	}

  	Alfresco.UserCalendar.prototype =
  	{
		/**
		 * The DOM element that holds the event data.
		 *
		 * @property container
	     * @type DOM node
		 */
		container: null,
		
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
			this.container = document.getElementById(this.id + "-eventContainer");
			
			Alfresco.util.Ajax.request({
				url: Alfresco.constants.PROXY_URI + "calendar/events/user",
				successCallback:
				{
					fn: this.onEventsLoaded,
					scope: this
				},
				failureCallback: 
				{
				   fn: this.onLoadFailed,
				   scope: this
				}
			});
		},

		/**
		 * Event handler that gets fired when the event data 
	    * fails to load.
		 *
		 * @method onLoadFailed
		 * @param e {object} DomEvent
		 */		
		onLoadFailed: function(e)
		{
		   this.container.innerHTML = "No calendars configured";
		},
		
		/**
		 * Event handler that gets fired when the event data 
	     * for the current user loads successfully.
		 *
		 * @method onEventsLoaded
		 * @param e {object} DomEvent
		 */
		onEventsLoaded: function(e)
		{
			// Display the event data
			var eventData;
			try 
			{
				eventData = YAHOO.lang.JSON.parse(e.serverResponse.responseText);
			}
			catch (e)
			{
				eventData = null;
			}
			
			if (eventData)
			{
				var events = eventData.events;
				var len = events.length;
				if (len > 0)
				{
				   var event;
   				for (var i=0; i < len; i++)
   				{
   					event = events[i];
   					this.container.innerHTML+= this._renderEvent(event);
   				}   
				}
				else
				{
				   this.container.innerHTML = "No upcoming events";
				}
			}
		},
		
		/** 
		 * Generates the HTML for an event
		 *
		 * @method _renderEvent
		 * @param event {Object} represents the event
		 */
		_renderEvent: function(event)
		{
			var html = "<table class='cal-events-dashlet'><tr class='cal-event'><td class='cal-icon'><img src='" + Alfresco.constants.URL_CONTEXT + "/components/calendar/images/calendar-16.png'/></td><td>";
			html += '<div class="cal-header"><a href="' + Alfresco.constants.URL_CONTEXT + event.url + '">' + event.title + '</a></div>';
			html += '<div>' + event.when + '  (' + event.start + ' - ' + event.end + ')</div>';
			html += '<div>In: <a href="' + Alfresco.constants.URL_CONTEXT + 'page/site/' + event.site + '/dashboard">' + event.site + '</a></div>';
			html += "</td></tr></table>";
			
			return html;
		}
	
	};
	
})();