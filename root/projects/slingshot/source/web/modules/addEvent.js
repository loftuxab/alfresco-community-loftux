/*
 *** Alfresco.module.AddEvent
*/
(function()
{
   Alfresco.module.AddEvent = function(containerId)
   {
      this.name = "Alfresco.module.AddEvent";
      this.id = containerId;
      
      this.panel = null;
      
      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "calendar", "container", "connection"], this.componentsLoaded, this);

      return this;
   };

   Alfresco.module.AddEvent.prototype =
   {
		setSiteId: function(siteId)
		{
			this.siteId = siteId;
		},
		
   	componentsLoaded: function()
      {
         /* Shortcut for dummy instance */
         if (this.id === null)
         {
            return;
         }
      },
      
      show: function()
      {
			Alfresco.util.Ajax.request(
	      {
	      	url: Alfresco.constants.URL_SERVICECONTEXT + "components/calendar/add-event",
				dataObj: { "htmlid" : this.id, "site" : this.siteId },
				successCallback:
				{
					fn: this.templateLoaded,
					scope: this
				},
	        	failureMessage: "Could not load add event template"
	      });
      },
      
      templateLoaded: function(response)
      {
         var div = document.createElement("div");
         div.innerHTML = response.serverResponse.responseText;

         this.panel = new YAHOO.widget.Panel(div,
         {
            fixedcenter: true,
            visible: false,
            constraintoviewport: true
         });

		 	this.panel.render(document.body);
		
			var okButton = new YAHOO.widget.Button(this.id + "-ok-button", {type: "submit"});
       	okButton.subscribe("click", this.onOkButtonClick, this, true);
		
		 	var eventForm = new Alfresco.forms.Form(this.id + "-addEvent-form");
         eventForm.setShowSubmitStateDynamically(true);
         eventForm.setSubmitElements(okButton);
			eventForm.setAJAXSubmit(true, 
			{
				successCallback:
				{
					fn: this.onCreateEventSuccess,
					scope: this
				}
			});
       	eventForm.init();

			var cancelButton = new YAHOO.widget.Button(this.id + "-cancel-button", {type: "button"});
         cancelButton.subscribe("click", this.onCancelButtonClick, this, true);
		
			var startButton = new YAHOO.widget.Button({
				 type: "push", 
             id: "calendarpicker", 
             container: this.id + "-startdate"
			});
			startButton.on("click", this.onDateSelectButton);
		                                      
			var endButton = new YAHOO.widget.Button({
				type: "push",
				id: "calendarendpicker",
				container: this.id + "-enddate"											
			});
			endButton.on("click", this.onDateSelectButton);									
			
         this.panel.show();
      },

		onDateSelectButton: function(e)
		{
			var oCalendarMenu = new YAHOO.widget.Overlay("calendarmenu");
			oCalendarMenu.setBody("&#32;");
			oCalendarMenu.body.id = "calendarcontainer";

			var container = this.get("container");
			// Render the Overlay instance into the Button's parent element
			oCalendarMenu.render(container);

			// Align the Overlay to the Button instance
			oCalendarMenu.align();
		       
   		var oCalendar = new YAHOO.widget.Calendar("buttoncalendar", oCalendarMenu.body.id);
			oCalendar.render();
		
			oCalendar.changePageEvent.subscribe(function () {
				window.setTimeout(function () {
					oCalendarMenu.show();
				}, 0);
			});
		
			oCalendar.selectEvent.subscribe(function (type, args) {
				var date;
				var Dom = YAHOO.util.Dom;
				
				if (args) {
					date = args[0][0];
					var selDate = new Date(date[0], (date[1]-1), date[2]);
				
					var wStr = oCalendar.cfg.getProperty("WEEKDAYS_LONG")[selDate.getDay()];
					var dStr = selDate.getDate();
					var mStr = oCalendar.cfg.getProperty("MONTHS_LONG")[selDate.getMonth()];
					var yStr = selDate.getFullYear();
					
					var prettyId, hiddenId;
					if (container.indexOf("enddate") > -1)
					{
						prettyId = "td";
						hiddenId = "to";
					}
					else 
					{
						prettyId = "fd";
						hiddenId = "from"
					}

					Dom.get(prettyId).value = (wStr + ", " + dStr + " " + mStr + " " + yStr);
					Dom.get(hiddenId).value = (yStr + "/" + (selDate.getMonth()+1) + "/"+ dStr);
				}
			
				oCalendarMenu.hide();
			});
		},

		onOkButtonClick: function(type, args)
		{
			
			
		},

		onCancelButtonClick: function()
	   {
	   	this.panel.hide();
	   },

	  	onCreateEventSuccess: function(e)
	  	{
			this.panel.hide();
			
			var result = YAHOO.lang.JSON.parse(e.serverResponse.responseText);
			if (result.event)
			{
				YAHOO.Bubbling.fire('onEventSaved', 
				{
					name: result.event.name,
					from: result.event.from
				});	
			}
	  	},
      
      templateFailed: function()
      {
         YAHOO.util.Dom.get(this.id).innerHTML = "<b>Couldn't get template</b>";
      }
   };
})();

/* Dummy instance to load optional YUI components early */
new Alfresco.module.AddEvent(null);