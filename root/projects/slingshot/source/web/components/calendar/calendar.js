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
		/**
       * AddEvent module instance.
       * 
       * @property eventDialog
       * @type Alfresco.module.AddEvent
       */
      eventDialog: null,

		/**
       * A reference to the YAHOO calendar component.
       * 
       * @property calendar
       * @type YAHOO.widget.Calendar
       */
		calendar: null,

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
			var Dom = YAHOO.util.Dom;
		
			/* Add Event Button */
         var aeButton = Alfresco.util.createYUIButton(this, "thisMonth-button", this.onButtonClick);
		
		 	// Separate the (initial) rendering of the calendar from the data loading.
		 	// If for some reason the data fails to load, the calendar will still display.
			this.calendar = new YAHOO.widget.Calendar("calendar");
			// Set localised properties
			this.calendar.cfg.setProperty("MONTHS_SHORT", eval(Alfresco.util.message("yui.calendar.months_short", this.name)));
			this.calendar.cfg.setProperty("MONTHS_LONG", eval(Alfresco.util.message("yui.calendar.months_long", this.name)));
			this.calendar.cfg.setProperty("WEEKDAYS_1CHAR", eval(Alfresco.util.message("yui.calendar.weekdays_1char", this.name)));
			this.calendar.cfg.setProperty("WEEKDAYS_SHORT", eval(Alfresco.util.message("yui.calendar.weekdays_short", this.name)));
			this.calendar.cfg.setProperty("WEEKDAYS_MEDIUM", eval(Alfresco.util.message("yui.calendar.weekdays_medium", this.name)));
			this.calendar.cfg.setProperty("WEEKDAYS_LONG", eval(Alfresco.util.message("yui.calendar.weekdays_long", this.name)));
			this.calendar.render();
			
			// Register for changes to the calendar data
			YAHOO.Bubbling.on('onEventDataLoad', this.onEventDataLoad, this);
			YAHOO.Bubbling.on('onEventSaved', this.onEventSaved, this);
		},
		
		/*
		 * This method is called when the "onEventSaved" event is fired; this
		 * usually occurs when an event is successfully created. The calendar 
		 * updates its view to hightlight the date of the event.
		 *
		 * @method onEventSaved
		 * @param e {object} Event fired
		 * @param args {array} Event parameters (depends on event type)
		 */
		onEventSaved: function(e, args)
		{
			var params = args[1];
			if (params)
			{
				var from = params.from;
				var selectedDates = this.calendar.getSelectedDates();
				
				var dates = selectedDates.map(function(d)
				{
					return Alfresco.util.formatDate(d, "mm/dd/yyyy");
				});
				dates.push(from);
				
				this.calendar.cfg.setProperty("selected", dates.join(","));
				this.calendar.render();
			}
		},
		
		/*
		 * This method is called when the "onEventDataLoad" event is fired; this
		 * usually occurs when the page first loads. The calendar data is retrieved
		 * and is used to update the view with the corresponding events.
		 *
		 * @method onEventDataLoad
		 * @param e {object} Event fired
		 * @param args {array} Event parameters (depends on event type)
		 */
		onEventDataLoad: function(e, args)
		{
			var params = args[1];
			if (params)
			{
				// Grab the source of the event
				var source = params.source;
				if (source)
				{
					var events = source.eventData;
					var selectedDates = [];
					
					for (var key in events)
					{
						if (events.hasOwnProperty(key)) {
							selectedDates.push(key);
						}
					}
					
					// Get the data and refresh the view
					this.calendar.cfg.setProperty("selected", selectedDates.join(","));
					this.calendar.render();
				}
			}
		},
	
		/*
	 	 * Fired when the "Add Event" button is clicked.
	 	 * Displays the event creation form. Initialises the 
	    * form if it hasn't been initialised. 
	 	 *
	 	 * @param e {object} DomEvent
	 	 * @param obj {object} Object passed back from addListener method
	 	 * @method  onButtonClick
	 	 */
		onButtonClick: function(e, oValue)
		{
		   var today = new Date();
		   this.calendar.cfg.setProperty("pagedate", today.getMonth()+1+"/"+today.getFullYear());
		   this.calendar.render();
		}
   };
})();
