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
         var aeButton = Alfresco.util.createYUIButton(this, "addEvent-button", this.onButtonClick);
		
		 	// Separate the (initial) rendering of the calendar from the data loading.
		 	// If for some reason the data fails to load, the calendar will still display.
			var cal = new YAHOO.widget.Calendar("calendar");
			cal.render();
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
			if (this.eventDialog === null)
			{
				this.eventDialog = new Alfresco.module.AddEvent(this.id + "-addEvent");
			}
			this.eventDialog.setSiteId(this.siteId);
			this.eventDialog.show();
		}
   };
})();
