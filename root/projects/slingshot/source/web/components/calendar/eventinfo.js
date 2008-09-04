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
 *** Alfresco.EventInfo
*/
(function()
{
   Alfresco.EventInfo = function(containerId)
   {
      this.name = "Alfresco.EventInfo";
      this.id = containerId;

      this.panel = null;

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "container", "connection"], this.componentsLoaded, this);

      return this;
   };

   Alfresco.EventInfo.prototype =
   {
      /**
       * EventInfo instance.
       *
       * @property panel
       * @type Alfresco.EventInfo
       */
      panel: null,
      
      /**
       * A reference to the current event. 
       * !!CHANGE ME!!
       *
       * @property event
       * @type object
       */
      event: null,
      
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
       * Renders the event info panel. 
       *
       * @method show
       * @param event {object} JavaScript object representing an event
       */
      show: function(event)
      {
         Alfresco.util.Ajax.request(
         {
            url: Alfresco.constants.URL_SERVICECONTEXT + "components/calendar/info",
            dataObj: { "htmlid" : this.id, "uri" : "/" + event.uri },
            successCallback:
            {
               fn: this.templateLoaded,
               scope: this
            },
            failureMessage: "Could not load event info panel"
         });

         this.event = event;
      },

      /**
       * Fired when the event info panel has loaded successfully.
       *
       * @method templateLoaded
       * @param response {object} DomEvent
       */
      templateLoaded: function(response)
      {
         var div = document.getElementById("eventInfoPanel");
         div.innerHTML = response.serverResponse.responseText;

         this.panel = new YAHOO.widget.Panel(div,
         {
            modal: true,
            fixedcenter: true,
            visible: false,
            constraintoviewport: true,
            width: "35em"
         });
         this.panel.render(document.body);

         // Buttons
         Alfresco.util.createYUIButton(this, "delete-button", this.onDeleteClick);
         Alfresco.util.createYUIButton(this, "edit-button", this.onEditClick);
         Alfresco.util.createYUIButton(this, "cancel-button", this.onCancelClick);

         // Display the panel
         this.panel.show();
      },
      
      /**
       * Fired when the use selected the "Cancel" button.
       *
       * @method onCancelClick
       * @param e {object} DomEvent
       */
      onCancelClick: function(e)
      {
         this.panel.hide();
      },
      
      /**
       * Fired when the user selects the "Edit" button.
       *
       * @method onEventClick
       * @param e {object} DomEvent
       */
      onEditClick: function(e)
      {
         this.panel.hide();
         
         var eventDialog = new Alfresco.module.AddEvent(this.id + "-addEvent");
         eventDialog.setOptions({
            "siteId": this.siteId,
            "eventURI": "/" + this.event.uri
         });
         eventDialog.show();
      },
      
      /**
       * Fired when the delete button is clicked. Kicks off a DELETE request
       * to the Alfresco repo to remove an event.
       *
       * @method onDeleteClick
       * @param e {object} DomEvent
       */
      onDeleteClick: function(e)
      {
         Alfresco.util.Ajax.request(
         {
            method: Alfresco.util.Ajax.DELETE,
               url: Alfresco.constants.PROXY_URI + this.event.uri,
            successCallback:
            {
               fn: this.onDeleted,
               scope: this
            },
               failureMessage: "Could not delete event"
         });
      },
      
      /**
       * Called when an event is successfully deleted.
       *
       * @method onDeleted
       * @param e {object} DomEvent
       */
      onDeleted: function(e)
      {
         this.panel.hide();
         
         YAHOO.Bubbling.fire('eventDeleted',
         {
            name: this.event.name, // so we know which event we are dealing with
            from: this.event.from // grab the events for this date and remove the event
         });         
      }
   };
})();
