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

            /* Listen for any navigation events */
            YAHOO.Bubbling.on("onNextNav", this.onNextNav, this);
            YAHOO.Bubbling.on("onPrevNav", this.onPrevNav, this);
            YAHOO.Bubbling.on("onTodayNav", this.onTodayNav, this);
            
            /* Load the data */
            this._loadData();

            // Decoupled event listeners
            YAHOO.Bubbling.on("onEventSaved", this.onEventSaved, this);
			   // Listen for when an event has been deleted as view will need refreshing.
			   YAHOO.Bubbling.on("eventDeleted", this.onEventDelete, this);
			   // Listen for when an event has been updated
			   YAHOO.Bubbling.on("eventUpdated", this.onEventUpdate, this);
        },

		/**
		 * Event handlers for the navigation buttons - Next, Prev and Today
		 *
		 */
		
		/**
		 * Fired when the user selects the "Next" button.
		 * For example, if the user is looking at the month view, when the user
		 * selects "Next", the next month will be displayed.
		 *
		 * @method onNextNav
		 * @param e {object} DomEvent
		 */
		onNextNav: function(e, args)
		{
			var DateMath = YAHOO.widget.DateMath;
			var fields = [DateMath.DAY, DateMath.WEEK, DateMath.MONTH, null];
			
			var field = fields[this.tabView.get('activeIndex')];
			if (field)
			{
				this.currentDate = DateMath.add(this.currentDate, field, 1);
				this._refreshCurrentView();
			}
		},
		
		/**
		 * Fired when the user selects the "Previous" button.
		 * For example, if the user is looking at the month view, when the user
		 * selects "Previous", the previous month will be displayed.
		 *
		 * @method onPrevNav
		 * @param e {object} DomEvent
		 */
		onPrevNav: function(e, args)
		{
			var DateMath = YAHOO.widget.DateMath;
			var fields = [DateMath.DAY, DateMath.WEEK, DateMath.MONTH, null];
			
			var field = fields[this.tabView.get('activeIndex')];
			if (field)
			{
				this.currentDate = DateMath.subtract(this.currentDate, field, 1);
				this._refreshCurrentView();
			}
		},
		
		/**
		 * Sets the current date (and view) to today.
		 *
		 * @method onTodayNav
		 * @param e {object} DomEvent
		 */
		onTodayNav: function(e, args)
		{
			this.currentDate = new Date();
			this._refreshCurrentView();
		},

		/**
		 * Loads the most recent event data. Resets the (cached) data.
		 *
		 * @method _loadData
		 */
		_loadData: function()
		{
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
				failureMessage: Alfresco.util.message("load.fail", "Alfresco.CalendarView")
			});
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
		 * Gets called when an event is (successfully) updated.
		 *
		 * @method onEventUpdate
		 * @param e {object} Event fired
		 * @param args {array} Event parameters
		 */
		onEventUpdate: function(e, args)
		{
			this._loadData(); // refresh the data
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
            // Initialise the current view 
            this._refreshCurrentView();

			// Now that the data has been loaded we can display the calendar
	        var Dom = YAHOO.util.Dom;
	        Dom.get('calendar-view').style.visibility = "visible";

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
            var label = Dom.get(this.id + "-monthLabel");
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
                              	d.innerHTML = '<a href="#">' + events[j].name + '</a>';
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
		   // TODO: look at caching this
			var panel = new Alfresco.EventInfo(this.id + "-eventInfo");
			panel.setSiteId(this.siteId);
			panel.show(obj); // event object
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
            var DateMath = YAHOO.widget.DateMath;
            var Dom = YAHOO.util.Dom;

            var startDate = DateMath.subtract(date, DateMath.DAY, date.getDay());
			var endDate = DateMath.add(startDate, DateMath.DAY, 6);
			
			/* Update the week label */
			var weekLabel = "";
			if (startDate.getMonth() === endDate.getMonth())
			{
				weekLabel = startDate.getDate() + " - " + Alfresco.util.formatDate(endDate, "d mmm yyyy");
			}
			else
			{
				weekLabel = Alfresco.util.formatDate(startDate, "d mmm yyyy") + " - " + Alfresco.util.formatDate(endDate, "d mmm yyyy");
			}
			
			var label = Dom.get(this.id + "-weekLabel");
	        label.innerHTML = weekLabel;
	
			var colElem;
			var colDate = startDate;
			/* Update the column headers */
			for (var col=0; col < 7; col++)
			{
				colElem = Dom.get(this.id + "-weekheader-" + col);
				if (colElem)
				{
					colElem.innerHTML = Alfresco.util.formatDate(colDate, "ddd d/m");
				}
				colDate = DateMath.add(colDate, DateMath.DAY, 1);
			}

			// Clear any previous events
			var container = document.getElementById("week-view");
			if (container)
			{
				var elems = this._getWeekViewEvents(container, "cal-event-entry");
				if (elems)
				{	
					for (var i=0; i < elems.length; i++)
					{
						// FIXME: the node should really be deleted
						elems[i].innerHTML = "";  
					}
				}
			}

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
                                d.innerHTML = '<a href="#">' + event.name + '</a>';
								YAHOO.util.Event.addListener(d, 'click', this.onEventClick, event, this);
                                elem.appendChild(d);
                            }
                        }
                    }
                }
                startDate = DateMath.add(startDate, DateMath.DAY, 1);
            }
        },

		_getWeekViewEvents: function(container, className)
		{
			var arrElems = container.getElementsByTagName("div");
			className = className.replace(/\-/g, "\\-");
			var regExp = new RegExp("(^|\\s)" + className + "(\\s|$)");
			var returnElems = [];
			var elem;
			for (var i=0; i < arrElems.length; i++)
			{
				elem = arrElems[i];
				if (regExp.test(elem.className))
				{
					returnElems.push(elem);
				}
			}	
			return returnElems;
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

			/* Change the day label */
	        var label = Dom.get(this.id + "-dayLabel");
	        label.innerHTML = Alfresco.util.formatDate(date, "dd mmm yyyy");

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
                    }
                    // Store the offset for each event
                    indents[i] = indent;

                    // Now display the event
                    var div = document.createElement("div");
                    div.setAttribute("class", "dayEvent cal-event-entry");
                    div.innerHTML = '<a href="#">' + event.name + '</a>';
					YAHOO.util.Event.addListener(div, 'click', this.onEventClick, event, this);
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