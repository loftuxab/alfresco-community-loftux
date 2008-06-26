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
				failureMessage: "Couldn't load event data"
			});
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
			var eventData = null;
			try 
			{
				eventData = YAHOO.lang.JSON.parse(e.serverResponse.responseText);
			}
			catch (e)
			{
				alert(e.toString());
			}
			
			if (eventData)
			{
				var events = eventData.events;
				var len = events.length;
				var event;
				for (var i=0; i < len; i++)
				{
					event = events[i];
					this.container.innerHTML+= this._renderEvent(event);
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
			var html = "<table><tr><td>"
			html += '<span class="eventTitle">' + event.title + '</span>';
			html += 'From: ' + event.start;
			html += "</td></tr></table>";
			
			return html;
		}
	
	};
	
})();