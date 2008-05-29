/*
 *** Alfresco.Calendar
 */
(function()
{
   Alfresco.Calendar = function(htmlId)
   {
      this.name = "Alfresco.Calendar";
      this.id = htmlId;
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);
      
      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["calendar", "button"], this.componentsLoaded, this);
      
      return this;
   }
   
   Alfresco.Calendar.prototype =
   {
      
 	componentsLoaded: function()
    {
		YAHOO.util.Event.onContentReady(this.id, this.init, this, true);
	},
       
    init: function()
    {
		var Dom = YAHOO.util.Dom;
		
		/* Add Event Button */
	    var aeButton = Dom.get(this.id + "-addEvent-button");
	    var addEventButton = new YAHOO.widget.Button(aeButton,
	    {
	    	type: "push" 
	    });
		addEventButton.addListener("click", this.onButtonClick, this);

		/* 
		 * Separate the (initial) rendering of the calendar from the data loading.
		 * If for some reason the data fails to load, the calendar will still display.
		 */
		var cal = new YAHOO.widget.Calendar("calendar");
		cal.render();
		
		var today = new Date();
		var year = today.getFullYear();
		var month = today.getMonth();
		
		var workspace = "workspace://SpacesStore/bd8ace13-1d07-11dd-b77e-7720ead70151";
		var uriEvents = Alfresco.constants.PROXY_URI + "calendar/RetrieveMonthEvents.json?";
		uriEvents += "s=" + workspace +"&";
		uriEvents += "d=" + year + "/" + (month + 1) + "/1&";
		
		var callback = 
		{
			success: this.onSuccess,
			failure: this.onFailure,
			argument: [cal],
			scope: this
		};
		
		YAHOO.util.Connect.asyncRequest('GET', uriEvents, callback);
	},
	
	/*
	 * Fired when the "Add Event" button is clicked.
	 * Displays pop-up form.
	 *
	 * @param e the event object
	 * @param obj
	 * @method  onButtonClick
	 */
	onButtonClick: function(e, obj)
	{
		new Alfresco.module.AddEvent(obj.id + "-addEvent").show();
	},

	onSuccess: function(o)
	{
		try {
			var eventlist = YAHOO.lang.JSON.parse(o.responseText); 
			var events = [];
			
			var year = 2008;
			var month = 5;
			
			var key; /* The key is the day of the current month */
			for (key in eventlist)
			{
				events.push(month + "/" + key + "/" + year);
			}
			
			if (events.length > 0)
			{
				var cal = o.argument[0];
				cal.cfg.setProperty("selected", events.join(","));
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
