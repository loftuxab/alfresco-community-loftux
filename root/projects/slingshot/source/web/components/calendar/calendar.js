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
		componentsLoaded: function()
    	{
			YAHOO.util.Event.onContentReady(this.id, this.init, this, true);
		},
       
    	init: function()
    	{
			var Dom = YAHOO.util.Dom;
		
			/* Add Event Button */
         var aeButton = Alfresco.util.createYUIButton(this, "addEvent-button", this.onButtonClick);
			
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
		
			//YAHOO.util.Connect.asyncRequest('GET', uriEvents, callback);
		},
	
		/*
	 	 * Fired when the "Add Event" button is clicked.
	 	 * Displays pop-up form.
	 	 *
	 	 * @param e the event object
	 	 * @param obj
	 	 * @method  onButtonClick
	 	 */
		onButtonClick: function(e, oValue)
		{
			if (this.eventDialog === null)
			{
				this.eventDialog = new Alfresco.module.AddEvent(this.id + "-addEvent");
			}
			this.eventDialog.setSiteId(this.siteId);
			this.eventDialog.show();
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
