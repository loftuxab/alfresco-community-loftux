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
	    Alfresco.util.YUILoaderHelper.require(["tabview", "button"], this.componentsLoaded, this);

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
		
		componentsLoaded: function()
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
			this.loadData(this.currentDate.getFullYear(), this.currentDate.getMonth());
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
			obj.loadData(obj.currentDate.getFullYear(), obj.currentDate.getMonth());
		},
		
		displayNextMonth: function(e, obj)
		{
			obj.currentDate.setMonth( obj.currentDate.getMonth() + 1 );
			obj.loadData(obj.currentDate.getFullYear(), obj.currentDate.getMonth());
		},
		
		displayPrevMonth: function(e, obj)
		{
			obj.currentDate.setMonth( obj.currentDate.getMonth() - 1 );
			obj.loadData(obj.currentDate.getFullYear(), obj.currentDate.getMonth());
		},
		
		loadData: function(year, month)
		{
			var workspace = "workspace://SpacesStore/bd8ace13-1d07-11dd-b77e-7720ead70151";
			var uriEvents = Alfresco.constants.PROXY_URI + "calendar/RetrieveMonthEvents.json?";
			uriEvents += "s=" + workspace +"&";
			uriEvents += "d=" + year + "/" + (month+1) + "/1&";
			
			var callback =
			{
				success: this.onSuccess,
				failure: this.onFailure,
				argument: [year, month],
				scope: this
			};

			YAHOO.util.Connect.asyncRequest('GET', uriEvents, callback);
		},
		
		/**
		 * 
		 * @param o JSON response
		 */
		onSuccess: function(o)
		{
			if (o.responseText === "")
			{
				return; /* error */
			}
			var eventlist = YAHOO.lang.JSON.parse(o.responseText);
			var year = o.argument[0];
			var month = o.argument[1];
			
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
						
						/* TODO: change key to yyyy-mm-dd */
						var events = eventlist[daynum];
						if (events) 
						{
							for (var j=0; j < events.length; j++)
							{
								var d = document.createElement('div');
								Dom.addClass(d, 'cal-event-entry');
								d.innerHTML = events[j];
								elem.appendChild(d);
							}
						}
						++daynum;
					}
				}
			}
		},
		
		onFailure: function(o)
		{
			alert("Failed to load data");
		}
	};
}) ();