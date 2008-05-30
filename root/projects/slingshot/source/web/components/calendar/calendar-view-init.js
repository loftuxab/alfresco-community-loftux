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
		/* TODO: move to separate date utilities class */
		days : [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31],
		
		months: [
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
		
		setSiteId: function(siteId)
		{
			this.siteId = siteId;
		},
		
		onComponentsLoaded: function()
		{
			YAHOO.util.Event.onContentReady(this.id, this.init, this, true);
		},
		
		init: function()
		{
			var tabView = new YAHOO.widget.TabView('calendar-view'); 
			
			/* Initialise buttons and handlers */
			this._addButton(this.id + "-next-button", this.displayNextMonth);
			this._addButton(this.id + "-prev-button", this.displayPrevMonth);
			this._addButton(this.id + "-current-button", this.displayCurrentMonth);
		
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
				
			// Decoupled event listeners
	      YAHOO.Bubbling.on("onEventSaved", this.onEventSaved, this);
		},
		
		/**
       * View Refresh Required event handler.
   	 * Called when a new event has been created.
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
		
		onDataLoad: function(o)
		{
			this.eventData = YAHOO.lang.JSON.parse(o.serverResponse.responseText);
			this.refresh(this.currentDate.getFullYear(), this.currentDate.getMonth());
		},
		
		refresh: function(year, month)
		{
			/* Set to the first day of the month */
			var date = new Date(year, month);
			var startDay = date.getDay();
			
			var Dom = YAHOO.util.Dom;

			/* Change the month label */
			var label = Dom.get("monthLabel");
			label.innerHTML = this.months[date.getMonth()] + " " + date.getFullYear();

			var days_in_month = this.days[month]; /* TODO: Add check for leap year */
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
		
		_addButton: function(id, func)
		{
			var Dom = YAHOO.util.Dom;
			
			var elem = Dom.get(id);
			if (elem !== null) 
			{
				var button = new YAHOO.widget.Button(elem,
				{
						type: "push"
				});
				button.addListener("click", func, this);	
			}
		},
		
		displayCurrentMonth: function(e, obj)
		{
			obj.currentDate = new Date();
			/* Add check to see what the date is. If it hasn't changed, don't load the data */
			obj.refresh(obj.currentDate.getFullYear(), obj.currentDate.getMonth());
		},
		
		displayNextMonth: function(e, obj)
		{
			obj.currentDate.setMonth( obj.currentDate.getMonth() + 1 );
			obj.refresh(obj.currentDate.getFullYear(), obj.currentDate.getMonth());
		},
		
		displayPrevMonth: function(e, obj)
		{
			obj.currentDate.setMonth( obj.currentDate.getMonth() - 1 );
			obj.refresh(obj.currentDate.getFullYear(), obj.currentDate.getMonth());
		}
	};
}) ();