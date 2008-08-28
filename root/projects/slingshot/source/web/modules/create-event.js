// Ensure namespaces exist
Alfresco.module.event =  Alfresco.module.event || {}; 
Alfresco.module.event.validation = Alfresco.module.event.validation || {};

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
		/**
	    * AddModule module instance.
	    *
	    * @property panel
	    * @type Alfresco.module.AddEvent
	    */
		panel: null,
				
      /**
        * Object container for initialization options
        *
        * @property options
        * @type object
        */
       options:
       {
          siteId: "",
          /**
    		 * Stores the URI of the event IF an edit is happening 
    		 *
    		 * @property eventURI
    		 * @type String
    		 */
          eventURI: null,
          displayDate: null
       },		

       /**
        * Set multiple initialization options at once.
        *
        * @method setOptions
        * @param obj {object} Object literal specifying a set of options
        */
       setOptions: function Wiki_setOptions(obj)
       {
          this.options = YAHOO.lang.merge(this.options, obj);
          return this;
       },
       
		/**
		 * Fired by YUILoaderHelper when required component script files have
		 * been loaded into the browser.
		 *
		 * @method onComponentsLoaded
		 */
   		componentsLoaded: function()
      	{
         	/* Shortcut for dummy instance */
         	if (this.id === null)
         	{
            	return;
         	}
      	},

		/**
		 * Renders the event create form. If the form has been previously rendered
		 * it clears the form of any previously entered values otherwise fires off a
		 * request to web script that generates the form.
		 *
		 * @method show
		 */
       show: function()
       {
          var args = {
				"htmlid": this.id,
				"site": this.options.siteId
			}
			
			if (this.options.eventURI)
			{
				args['uri'] = this.options.eventURI;
			}
			
			Alfresco.util.Ajax.request(
		    {
		    	url: Alfresco.constants.URL_SERVICECONTEXT + "components/calendar/add-event",
				dataObj: args,
				successCallback:
				{
					fn: this.templateLoaded,
					scope: this
				},
		        failureMessage: "Could not load add event form"
			});
      	},

		/**
		 * Fired when the event create form has loaded successfully.
		 * Sets up the various widgets on the form and initialises the forms runtime.
		 *
		 * @method templateLoaded
		 * @param response {object} DomEvent
		 */
       templateLoaded: function(response)
       {
          // Inject the template from the XHR request into a new DIV element
          var containerDiv = document.createElement("div");
          containerDiv.innerHTML = response.serverResponse.responseText;

          // The panel is created from the HTML returned in the XHR request, not the container
          var panelDiv = YAHOO.util.Dom.getFirstChild(containerDiv);

          this.panel = new YAHOO.widget.Panel(panelDiv,
          {
             modal: true,
             draggable: false,
             fixedcenter: true,
             close: false,
             visible: false
          });

          // Add it to the Dom
          this.panel.render(document.body);

          var Dom = YAHOO.util.Dom;

          // "All day" check box
		 	var allDay = Dom.get(this.id + "-allday");
		 	if (allDay)
		 	{
		 	   YAHOO.util.Event.addListener(allDay, "click", this.onAllDaySelect, this, true);
		 	}
         
         var eventForm = new Alfresco.forms.Form(this.id + "-addEvent-form");
         eventForm.addValidation(this.id + "-title", Alfresco.forms.validation.mandatory, null, "blur");
         eventForm.addValidation(this.id + "-title", Alfresco.forms.validation.nodeName, null, "keyup");
         eventForm.addValidation(this.id + "-tags", Alfresco.module.event.validation.tags, null, "keyup");
         
         var dateElements = ["td", "fd", this.id + "-start", this.id + "-end"];
         for (var i=0; i < dateElements.length; i++)
         {
            eventForm.addValidation(dateElements[i], this._onDateValidation, { "obj": this }, "blur");
         }
         
         // Setup date validation
         eventForm.addValidation("td", this._onDateValidation, { "obj": this }, "focus");
         eventForm.addValidation("fd", this._onDateValidation, { "obj": this }, "focus");
         eventForm.addValidation(this.id + "-start", this._onDateValidation, { "obj": this }, "blur");
         eventForm.addValidation(this.id + "-end", this._onDateValidation, { "obj": this }, "blur");
                     
         // OK Button
         var okButton = Alfresco.util.createYUIButton(this, "ok-button", null,
         {
            type: "submit"
         });
         
         eventForm.setShowSubmitStateDynamically(true);
         eventForm.setSubmitElements(okButton);

			if (!this.options.eventURI) // Create
			{
				eventForm.setAJAXSubmit(true,
				{
					successCallback:
					{
						fn: this.onCreateEventSuccess,
						scope: this
					}
				});
	      
				// Initialise the start and end dates to today
				var today = this.options.displayDate || new Date();
            // Pretty formatting
				var dateStr = Alfresco.util.formatDate(today, "dddd, d mmmm yyyy");
				Dom.get("fd").value = dateStr;
				Dom.get("td").value = dateStr;
			}
			else  // Event Edit
			{   
            var form = document.getElementById(this.id + "-addEvent-form");
            // Reset the "action" attribute
            form.attributes.action.nodeValue = Alfresco.constants.PROXY_URI + this.options.eventURI;
            
            eventForm.setAjaxSubmitMethod(Alfresco.util.Ajax.PUT);
            eventForm.setAJAXSubmit(true,
            {
	            successCallback:
	            {
		            fn: this.onEventUpdated,
		            scope: this
	            }
            });        
	         
	         // Is this an all day event?
	         var startTime = Dom.get(this.id + "-start");
	         var endTime = Dom.get(this.id + "-end");
	         
	         // TODO: perhaps "allday" property to calendar event
	         if (startTime.value === "00:00" && (startTime.value === endTime.value))
	         {
	            allDay.setAttribute("checked", "checked");
	            this._displayTimeFields(false);
	         }
			}
			
			eventForm.setSubmitAsJSON(true);
			eventForm.doBeforeFormSubmit =
         {
            fn: function(form, obj)
            {
               // Set the value of the hidden form variables	
   				var start = Alfresco.util.formatDate(Dom.get("fd").value, "ddd, d mmmm yyyy");
   				Dom.get(this.id + "-from").value = Alfresco.util.formatDate(start, "yyyy/mm/dd");
   				
   				var to = Alfresco.util.formatDate(Dom.get("td").value, "ddd, d mmmm yyyy");
      			Dom.get(this.id + "-to").value = Alfresco.util.formatDate(to, "yyyy/mm/dd");
            },
            scope: this
         }
         // We're in a popup, so need the tabbing fix
         eventForm.applyTabFix();
			eventForm.init();
			
			var cancelButton = Alfresco.util.createYUIButton(this, "cancel-button", this.onCancelButtonClick);

			/**
			 * Button declarations that, when clicked, display
			 * the calendar date picker widget.
			 */
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

			// Display the panel
			this.panel.show();

         // Set intial focus
         YAHOO.util.Dom.get(this.id + "-title").focus();
         console.log(YAHOO.util.Dom.get(this.id + "-title"));
		},
		
		_onDateValidation: function(field, args, event, form, silent)
		{
		   var Dom = YAHOO.util.Dom;
         // Check that the end date is after the start date
         var start = Alfresco.util.formatDate(Dom.get("fd").value, "yyyy/mm/dd");
         var startDate = new Date(start + " " + Dom.get(args.obj.id + "-start").value);
         
         var to = Alfresco.util.formatDate(Dom.get("td").value, "yyyy/mm/dd");
         var toDate = new Date(to + " " + Dom.get(args.obj.id + "-end").value);
         
         var after = YAHOO.widget.DateMath.after(toDate, startDate);
         
         if (Alfresco.logger.isDebugEnabled())
         {
            Alfresco.logger.debug("Current start date: " + start + " " + Dom.get(args.obj.id + "-start").value);
            Alfresco.logger.debug("Current end date: " + to + " " + Dom.get(args.obj.id + "-end").value);
            Alfresco.logger.debug("End date is after start date: " + after);
         }
         
         if (!after && !silent)
         {
            form.addError(form.getFieldLabel(field.id) + " cannot be before the start date.", field);
         }
         
         return after;
		},
		
		/**
		 * Event handler that gets called when the user clicks on the "All day" event
		 * checkbox in the event create / edit form. If selected, hides the time fields
		 * from view.
		 *
		 * @method onAllDaySelect
		 * @param e {object} DomEvent
		 */
		onAllDaySelect: function(e)
		{
		  var checkbox = e.target;
		  var display = true; // Time fields are enabled by default
		  
		  if (checkbox.checked)
		  {
		     display = false;
		  } 
		  
		 this._displayTimeFields(display);
		},
		
      /**
       * If the user selectes the "All day" event option, then the start and end
       * time fields are hidden from view. The date field remains active.
       *
       * @method _displayTimeFields
       * @param display {Boolean} if true, displays the start / end time fields
       */	
		_displayTimeFields: function(display)
		{
		  var ids = [this.id + "-starttime", this.id + "-endtime"];
		  var elem;
		  for (var i=0; i < ids.length; i++)
		  {
		     elem = document.getElementById(ids[i]);
		     if (elem)
		     {
		       elem.style.display = (display ? "inline" : "none");
		     }
		  } 
		},
		
		onEventUpdated: function(e)
		{
			this.panel.destroy();
			// Fire off "eventUpdated" event
			YAHOO.Bubbling.fire('eventUpdated');
		},

		/**
		 * Event handler that gets fired when a user clicks on the date selection
		 * button in the event creation form. Displays a mini YUI calendar.
		 * Gets called for both the start and end date buttons.
		 *
		 * @method onDateSelectButton
		 * @param e {object} DomEvent
		 */
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
         var me = this;
         
			oCalendar.selectEvent.subscribe(function (type, args) {
				var date;
				var Dom = YAHOO.util.Dom;

				if (args) {
					var prettyId, hiddenId;
					if (container.indexOf("enddate") > -1)
					{
						prettyId = "td";
					}
					else
					{
						prettyId = "fd";
					}

					date = args[0][0];
					var selectedDate = new Date(date[0], (date[1]-1), date[2]);

               var elem = Dom.get(prettyId);
					elem.value = Alfresco.util.formatDate(selectedDate, "dddd, d mmmm yyyy");
					elem.focus();			
				}

				oCalendarMenu.hide();
			});
		},

		/**
		 * Event handler that gets fired when a user clicks
		 * on the cancel button in the event create form.
		 *
		 * @method onCancelButtonClick
		 * @param e {object} DomEvent
		 * @param obj {object} Object passed back from addListener method
		 */
		onCancelButtonClick: function(e, obj)
		{
		     this.panel.destroy();
		},

		/**
		 * Event handler that gets fired when an event is (successfully) created.
		 * It in turns fires an 'onEventSaved' event passing in the name and start date
		 * of the newly created event.
		 *
		 * @method onCreateEventSuccess
		 * @param e {object} DomEvent
		 */
	  	onCreateEventSuccess: function(e)
	  	{
			this.panel.destroy();

			var result = YAHOO.lang.JSON.parse(e.serverResponse.responseText);
			if (result.event)
			{
				YAHOO.Bubbling.fire('onEventSaved',
				{
					name: result.event.name,
					from: result.event.from,
					start: result.event.start,
					end: result.event.end,
					uri: result.event.uri,
					tags: result.event.tags
				});
				// Refresh the tag component
				YAHOO.Bubbling.fire('onTagRefresh');
			}
	  	}
   };
})();

/**
 * Tags entry field validation handler, tests that the given field's value is a valid.
 * This is identical to the test for the name for a node in the repository minus the requirement
 * that there must not be any white space; tags are separated by white space.
 *
 * @method nodeName
 * @param field {object} The element representing the field the validation is for
 * @param args {object} Not used
 * @param event {object} The event that caused this handler to be called, maybe null
 * @param form {object} The forms runtime class instance the field is being managed by
 * @param silent {boolean} Determines whether the user should be informed upon failure
 * @static
 */
Alfresco.module.event.validation.tags = function mandatory(field, args, event, form, silent)
{
   if (!args)
   {
      args = {};
   }

   args.pattern = /([\"\*\\\>\<\?\/\:\|]+)|([\.]?[\.]+$)/;
   args.match = false;

   return Alfresco.forms.validation.regexMatch(field, args, event, form, silent); 
};

/* Dummy instance to load optional YUI components early */
new Alfresco.module.AddEvent(null);
