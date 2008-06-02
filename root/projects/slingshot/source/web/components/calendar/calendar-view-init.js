/**
 * Copyright (C) 2005-2008 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing
 */

/**
 * Alfresco.CalendarView
 */
(function()
{
	Alfresco.CalendarView = function(htmlId)
	{
		this.name = "Alfresco.CalendarView";
	   this.id = htmlId;

		this.currentDate = new Date();
		
	   /* Register this component */
	   Alfresco.util.ComponentManager.register(this);
	
	   /* Load YUI Components */
	   Alfresco.util.YUILoaderHelper.require(["tabview", "button"], this.onComponentsLoaded, this);
	
	   return this;
	}
	
	Alfresco.CalendarView.prototype = 
	{
		/**
		 * Array of the number of days in each calendar month starting with January
		 *
		 * TODO: move to separate date utilities class
		 *
		 * @property days
		 * @type array
		 */
		DAYS : [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31],
		
		/**
       * Array of month names.
       * 
       * @property months
       * @type array
       */
		MONTHS: [
			"January", 
			"February", 
			"March", 
			"April", 
			"May", 
			"June", 
			"July", 
			"August",
			"September",
			"October",
			"November",
			"December"
		],
		
		/**
       * Event data (cached).
       * 
       * @property eventData
       * @type object
       */
		eventData: {},
		
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
		onComponentsLoaded: function()
		{
			YAHOO.util.Event.onContentReady(this.id, this.init, this, true);
		},
		
		/**
		 * Fired by YUI when parent element is available for scripting.
		 * Initialises components, including YUI widgets and loads event data.
		 *
		 * @method init
		 */
		init: function()
		{	
			var tabView = new YAHOO.widget.TabView('calendar-view'); 
			
			/* Initialise buttons and handlers */
			Alfresco.util.createYUIButton(this, "next-button", this.displayNextMonth, { type: "push" });
			Alfresco.util.createYUIButton(this, "prev-button", this.displayPrevMonth, { type: "push" });
			Alfresco.util.createYUIButton(this, "current-button", this.displayCurrentMonth, { type: "push" });
		
			/* Initialise the current view */
			Alfresco.util.Ajax.request({
				url: Alfresco.constants.PROXY_URI + "calendar/eventList",
				dataObj: 
				{ 
					"site": this.siteId 
				},
				successCallback:
				{
					fn: this.onDataLoad,
					scope: this
				},
				failureMessage: "Could not load calendar data"
			});
				
			// Decoupled event listener
	      YAHOO.Bubbling.on("onEventSaved", this.onEventSaved, this);
		},
		
		/**
       * View Refresh Required event handler.
   	 * Called when a new event has been created.
 		 * Updates the current view with details of the newly created event.
       *
       * @method onEventSaved
       * @param e {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
		onEventSaved: function(e, args)
		{
			var obj = args[1];
			if (obj !== null)
			{
				var events = this.eventData[obj.from];
				if (events === undefined)
				{
					events = [];
					this.eventData[obj.from] = events;
				}

				events.push({
					"name": obj.name
				});

				// Check to see if we need to refresh the current view
				var dateStr = obj.from.split("/");
				
				var fromDate = new Date();
				fromDate.setYear(dateStr[2]);
				fromDate.setMonth((dateStr[0]-1));
				fromDate.setDate(dateStr[1]);
				
				// Is it the same month?
				if (fromDate.getFullYear() === this.currentDate.getFullYear() 
					&& fromDate.getMonth() === this.currentDate.getMonth())
				{
					this.refresh(this.currentDate.getFullYear(), this.currentDate.getMonth());
				}
			}
		},
		
		/**
		 * Fired when the event data has loaded successfully.
		 * Caches the data locally and updates the view with the current event data.
		 * 
		 * @method onDataLoad
		 * @param o {object} DomEvent 
		 */
		onDataLoad: function(o)
		{
			this.eventData = YAHOO.lang.JSON.parse(o.serverResponse.responseText);
			this.refresh(this.currentDate.getFullYear(), this.currentDate.getMonth());
		},
		
		/**
		 * Updates the view to display events that occur during the specified period
		 * as indicated by the "year" and "month" parameters.
		 *
		 * @method refresh
		 * @param year {integer}
		 * @param month {integer} 
		 */
		refresh: function(year, month)
		{
			/* Set to the first day of the month */
			var date = new Date(year, month);
			var startDay = date.getDay();
			
			var Dom = YAHOO.util.Dom;

			/* Change the month label */
			var label = Dom.get("monthLabel");
			label.innerHTML = this.MONTHS[date.getMonth()] + " " + date.getFullYear();

			var days_in_month = this.DAYS[month]; /* TODO: Add check for leap year */
			var daynum = 1;
			
			for (var i = 0; i < 42; i++)
			{
				var elem = Dom.get("cal_month_t_" + i);
				if (elem !== null)
				{
					elem.innerHTML = ""; /* reset */
					if (startDay <= i && i < (startDay + days_in_month)) 
					{
						var h = document.createElement('div');
						Dom.addClass(h, 'dayLabel');
						h.innerHTML = "<a href=\"#\">" + daynum + "</a>"; /* JavaScript days are 1 less */
						elem.appendChild(h);
						
						var key = (month+1) + "/" + daynum + "/" + year;
						var events = this.eventData[key];
						if (events) 
						{
							for (var j=0; j < events.length; j++)
							{
								var d = document.createElement('div');
								Dom.addClass(d, 'cal-event-entry');
								d.innerHTML = events[j].name;
								elem.appendChild(d);
							}
						}
						++daynum;
					}
				}
			}
		},
		
		/*
		 * Fired when the "This Month" button is clicked.
	    * Updates currentDate to today then refreshes the view.
		 * 
		 * @param e {object} DomEvent
		 * @param obj {object} Object passed back from addListener method
		 * @method  displayCurrentMonth
		 */
		displayCurrentMonth: function(e, obj)
		{
			this.currentDate = new Date();
			/* Add check to see what the date is. If it hasn't changed, don't load the data */
			this.refresh(this.currentDate.getFullYear(), this.currentDate.getMonth());
		},
		
		/*
		 * Fired when the "Next" button is clicked.
		 * Updates currentDate to the next month then refreshes the view.
		 * 
		 * @param e {object} DomEvent
		 * @param obj {object} Object passed back from addListener method
		 * @method  displayNextMonth
		 */
		displayNextMonth: function(e, obj)
		{
			this.currentDate.setMonth( this.currentDate.getMonth() + 1 );
			this.refresh(this.currentDate.getFullYear(), this.currentDate.getMonth());
		},
		
		/*
		 * Fired when the "Previous" button is clicked.
		 * Updates currentDate to the previous month then refreshes the view.
		 * 
		 * @param e {object} DomEvent
		 * @param obj {object} Object passed back from addListener method
		 * @method  displayPrevMonth
		 */
		displayPrevMonth: function(e, obj)
		{
			this.currentDate.setMonth( this.currentDate.getMonth() - 1 );
			this.refresh(this.currentDate.getFullYear(), this.currentDate.getMonth());
		}
	};
}) ();