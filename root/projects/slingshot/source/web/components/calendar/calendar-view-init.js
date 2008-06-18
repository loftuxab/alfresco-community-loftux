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
			 * The index of the tab that is currently selected:
			 * 0 -> Day; 1 -> Week; 2 -> Month; 3 -> Agenda
			 * 
			 * All tab indices are 0-based.
			 *
			 * @property activeIndex
			 * @type integer
			 */
			activeIndex: 2, // default value
			
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
		 * Set messages for this component
		 *
		 * @method setMessages
		 * @param obj {object} Object literal specifying a set of messages
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
            this.tabView = new YAHOO.widget.TabView('calendar-view');

            var tabs = this.tabView.get('tabs');
            for (var i=0; i < tabs.length; i++)
            {
                var tab = tabs[i];
                tab.on("click", this.onTabSelected, this, true);
            }

            /* Initialise buttons and handlers */
            Alfresco.util.createYUIButton(this, "next-button", this.displayNextMonth, { type: "push" });
            Alfresco.util.createYUIButton(this, "prev-button", this.displayPrevMonth, { type: "push" });
            Alfresco.util.createYUIButton(this, "current-button", this.displayCurrentMonth, { type: "push" });

            /* Initialise the current view */
            Alfresco.util.Ajax.request(
				{
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
                failureMessage: Alfresco.util.message("load.fail", "Alfresco.CalendarView")
            });

            // Decoupled event listeners
            YAHOO.Bubbling.on("onEventSaved", this.onEventSaved, this);
			// Listen for when an event has been deleted as view will need refreshing.
			YAHOO.Bubbling.on("eventDeleted", this.onEventDelete, this);
        },

		  /**
			* Gets fired when one of the tabs is selected. Records which tab is currently selected.
			*
			* @method onTabSelected
			* @param e {object} Event fired
			*/
        onTabSelected: function(e)
        {
            var idx = this.tabView.get('activeIndex');
			// Record the tab that is currently selected
			this.activeIndex = idx;
			this._refreshCurrentView();
        },

		_refreshCurrentView: function()
		{
			var funcs = [this.refreshDay, this.refreshWeek, this.refreshMonth, this.refreshAgenda];
			
			var idx = this.tabView.get('activeIndex');
			var f = funcs[idx];
			if (f)
			{
				var args = [this.currentDate];
				f.apply(this, args);
			}
		},

		/**
		 * Gets fired when an event is deleted. Removes the event from the cached data
		 * and refreshes the view, if necessary.
		 * 
		 * @method onEventDelete
		 * @param e {object} Event fired
		 * @param args {array} Event parameters
		 */
		onEventDelete: function(e, args)
		{
			var obj = args[1];
			if (obj)
			{
				var events = this.eventData[obj.from];
				if (events)
				{
					// Try and find the event that was just deleted and remove it
					for (var i=0; i < events.length; i++)
					{
						var e = events[i];
						if (e.name === obj.name)
						{
							// Remove it
							events.splice(i, 1);
							this._refreshCurrentView();
							break;
						}
					}
				}
			}
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
            if (obj)
            {
            	var events = this.eventData[obj.from];
               	if (events === undefined)
               	{
                	events = [];
                    this.eventData[obj.from] = events;
               	}

               	events.push({
                    	"name": obj.name,
						"start": obj.start,
						"end": obj.end,
						"uri": obj.uri
               	});
					
				// Need to re-order on start time
				events.sort(function(a,b)
				{
					var startTimeA = a.start.split(":");
					var startTimeB = b.start.split(":");
						
					if (startTimeA[0] < startTimeB[0] || startTimeA[1] < startTimeB[1])
					{
						return 0;
					}
					else
					{
						return 1;
					}
				});
					
               	var dateStr = obj.from.split("/");

               	var eventDate = new Date();
               	eventDate.setYear(dateStr[2]);
               	eventDate.setMonth((dateStr[0]-1));
               	eventDate.setDate(dateStr[1]);

 				var DateMath = YAHOO.widget.DateMath;
				var dateBegin, dateEnd, f;
				// For the current view, figure out if it needs updating based on the event that was just created
				switch (this.activeIndex)
				{
					case 0: // day
						dateBegin = DateMath.getDate(this.currentDate.getFullYear(), this.currentDate.getMonth(), this.currentDate.getDate());
						dateEnd = DateMath.add(dateBegin, DateMath.DAY, 1);
						f = this.refreshDay;
						break;
					case 1: // week
						dateBegin = DateMath.subtract(this.currentDate, DateMath.DAY, this.currentDate.getDay());
						dateBegin.setHours(0, 0, 0);
						dateEnd = DateMath.add(dateBegin, DateMath.DAY, 7);
						f = this.refreshWeek;
						break;
					case 2: // month
						dateBegin = DateMath.findMonthStart(this.currentDate);
						var monthEnd = DateMath.findMonthEnd(this.currentDate);
						// This is to catch events that occur on the last day of the current mont
						dateEnd = DateMath.add(monthEnd, DateMath.DAY, 1);
						f = this.refreshMonth;
						break;
				}

				if (DateMath.between(eventDate, dateBegin, dateEnd))
				{
					var args = [this.currentDate];
	               	f.apply(this, args);
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
            // Initialise the default view (month)
            this.refreshMonth(this.currentDate);
				// Now that the data has been loaded we can display the calendar
	         var Dom = YAHOO.util.Dom;
	         Dom.get('calendar-view').style.visibility = "visible";
			
				//this.refreshWeek(this.currentDate);
				//this.refreshDay(this.currentDate);

            // Fire "onEventDataLoad" event to inform other components to refresh their view
            YAHOO.Bubbling.fire('onEventDataLoad',
            {
                source: this
            });
        },

        /**
         * Updates the view to display events that occur during the specified period
         * as indicated by the "year" and "month" parameters.
         *
         * @method refresh
         * @param year {integer}
         * @param month {integer}
         */
        refreshMonth: function(date)
        {
				/* Set to the first day of the month */
				var startDate = YAHOO.widget.DateMath.findMonthStart(date);
            var startDay = startDate.getDay();

            var Dom = YAHOO.util.Dom;

            /* Change the month label */
            var month = date.getMonth();
            var label = Dom.get("monthLabel");
            label.innerHTML = this.MONTHS[month] + " " + date.getFullYear();

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

                        var key = (month+1) + "/" + daynum + "/" + date.getFullYear();
                        var events = this.eventData[key];
                        if (events)
                        {
                            for (var j=0; j < events.length; j++)
                            {
                            	var d = document.createElement('div');
                              	Dom.addClass(d, 'cal-event-entry');
                              	d.innerHTML = events[j].name;
								YAHOO.util.Event.addListener(d, 'click', this.onEventClick, events[j], this);
                              	elem.appendChild(d);
                            }
                        }
                        ++daynum;
                    }
                }
            }
        },

		onEventClick: function(e, obj)
		{
			var panel = new Alfresco.EventInfo(this.id + "-eventInfo");
			panel.setSiteId(this.siteId);
			panel.show(obj); // event object
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
            this.refreshMonth(this.currentDate);
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
            this.refreshMonth(this.currentDate);
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
            this.refreshMonth(this.currentDate);
        },

        /**
         * Functions specific to the week view
         *
         */

        /**
         * Given a date displays the event(s) for the week the date falls in.
         * Weeks start from Sunday. For example, if given a date that falls on a Wednesday,
         * events that start from the previous Sunday will be displayed.
         *
         * @param date {Date} JavaScript date object
         * @method  displayPrevMonth
         */
        refreshWeek: function(date)
        {
            // TODO: refresh column headers
            var DateMath = YAHOO.widget.DateMath;
            var Dom = YAHOO.util.Dom;

            var startDate = DateMath.subtract(date, DateMath.DAY, date.getDay());

            for (var day=0; day < 7; day++)
            {
                /* Event data is keyed on m/d/yyyy */
                var events = this.eventData[ Alfresco.util.formatDate(startDate, "m/d/yyyy") ];
                if (events)
                {
                    for (var i=0; i < events.length; i++)
                    {
                        var event = events[i];
                        var startTime = event.start;
                        if (startTime)
                        {
                            var parts = startTime.split(":");
                            var hours = parseInt(parts[0]);
                            var minutes = parseInt(parts[1]);

                            // Figure out where the event should be placed
                            var row = hours * 2 + (minutes > 0 ? 1 : 0);
                            var col = startDate.getDay();
                            var id = this.id + "_calendar_cell" + (row*7 + col);

                            var elem = Dom.get(id);
									 elem.innerHTML = ""; // reset
                            if (elem)
                            {
                                var d = document.createElement('div');
                                Dom.addClass(d, 'cal-event-entry');
                                d.innerHTML = event.name;
                                elem.appendChild(d);
                            }
                        }
                    }
                }
                startDate = DateMath.add(startDate, DateMath.DAY, 1);
            }
        },

        /**
         * Functions specific to the day view
         *
         *
         */

        /**
         * Given a date displays the event(s) for that date.
         * Figures out if events overlap and, if so, alters how the events
         * are displayed appropriately.
         *
         * @param date {Date} JavaScript date object
         * @method  refreshDay
         */
        refreshDay: function(date)
        {
            var DateMath = YAHOO.widget.DateMath;
            var Dom = YAHOO.util.Dom;

            var WIDTH = 82; // 80px + 1px + 1px
            var HEIGHT = 22;
            var DENOM = 1000 * 60 * 30; // 30 minute slots

            var container = Dom.get(this.id + "-dayEventsView");
				container.innerHTML = ""; // reset
            var events = this.eventData[ Alfresco.util.formatDate(date, "m/d/yyyy") ];
            var total = events.length;
            if (total > 0)
            {
                var indents = [];
                // Assumes that events are sorted by start time
                for (var i=0; i < total; i++)
                {
                    var event = events[i];
                    // TODO: sort this out
                    var startDate = new Date(date.getTime());
                    var startTime = event.start.split(":");
                    startDate.setHours(startTime[0], startTime[1]);

                    var endDate = new Date(date.getTime());
                    if (!event.end)
                    {
                        event.end = "23:00"; // TODO: choose a sensible default
                    }
                    var endTime = event.end.split(":");
                    endDate.setHours(endTime[0], endTime[1]);

                    indents[i] = 0; // initialise
                    var indent = 0;
                    // Check the previous events for overlap
                    for (var j = i-1; j >= 0; j--)
                    {
                        /**
                         * Events are already sorted by start time
                         */
                        var e = events[j];
                        var sDate = new Date(date.getTime());
                        var sTime = e.start.split(":");
                        sDate.setHours(sTime[0], sTime[1]);

                        var eDate = new Date(date.getTime());
                        var eTime = e.end.split(":");
                        eDate.setHours(eTime[0], eTime[1]);
                        
                        // Check to see if the events overlap
                        if (YAHOO.widget.DateMath.after(eDate, startDate)) 
                        {
                          if (indent === indents[j]) 
                          {
                            indent += 1;
                          }
                        }
                        
                        /**
                        if (YAHOO.widget.DateMath.between(startDate, sDate, eDate) ||
                            YAHOO.widget.DateMath.between(endDate, sDate, eDate))
                        {
                            if (indent === indents[j])
                            {
                                indent += 1;
                            }
                        }
                        */
                    }
                    // Store the offset for each event
                    indents[i] = indent;

                    // Now display the event
                    var div = document.createElement("div");
                    div.setAttribute("class", "dayEvent");
                    div.innerHTML = event.name;
                    // Figure out the height of the div based upon
                    // the number of half hour slots it occupies
                    var span = Math.round((endDate.getTime() - startDate.getTime()) / DENOM);
                    div.style.height = (HEIGHT * span) + "px";
                    // Set the position
                    var top = startDate.getHours() * 2 + (startDate.getMinutes() > 0 ? 1 : 0);
                    div.style.top = (HEIGHT * top) + "px";
                    div.style.left = (WIDTH * indent) + "px";

                    container.appendChild(div);
                }
            }

        },

		/**
		 * Methods specific to the agenda view 
		 * of events.
		 */

		/**
		 * Updates the agenda view. Currently displays ALL the events for a site.
		 *
		 * @method refreshAgenda
		 */
		refreshAgenda: function()
		{
			var Dom = YAHOO.util.Dom;
			var innerHTML = "";
			
			for (var key in this.eventData)
			{
				if (this.eventData.hasOwnProperty(key)) {
					var dateParts = key.split("/");
					var eventdate = YAHOO.widget.DateMath.getDate(dateParts[2], (dateParts[0]-1), dateParts[1]);
					innerHTML += this.agendaDayCellRenderer(eventdate);
				}
			}
			
			var elem = Dom.get(this.id + "-agendaContainer");
			elem.innerHTML = innerHTML;
		},
		
		/**
		 * This method generates the HTML to display the events 
		 * for a given day, specified by the "date" parameter.
		 * 
		 * @method agendaItemCellRenderer
		 * @param date {Date} JavaScript date object
		 */
		agendaDayCellRenderer: function(date)
		{
			var thedate = Alfresco.util.formatDate(date, "m/d/yyyy");
			var events = this.eventData[ Alfresco.util.formatDate(date, "m/d/yyyy") ];
			var html = "";
			if (events && events.length > 0)
			{
				var title = Alfresco.util.formatDate(date, "mediumDate");
				html += '<div class="agenda-item">'
				html += '<div class="dayheader">' + title + '</div>';
				html += '<table class="daytable">'
				for (var i=0; i < events.length; i++)
				{
					var event = events[i];
					html += '<tr><td class="timelabel">' + event.start + '</td><td>' + event.name + '</td></tr>';
				}
				html += '</table></div>';
			}
			return html;
		}

    };
}) ();