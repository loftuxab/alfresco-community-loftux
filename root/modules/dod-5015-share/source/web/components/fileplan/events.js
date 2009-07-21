/**
 * Copyright (C) 2005-2009 Alfresco Software Limited.
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
 * Events component.
 *
 * @namespace Alfresco
 * @class Alfresco.Events
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
         Event = YAHOO.util.Event,
         Selector = YAHOO.util.Selector;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML;

   /**
    * Events constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.Events} The new component instance
    * @constructor
    */
   Alfresco.Events = function Events_constructor(htmlId)
   {
      Alfresco.Events.superclass.constructor.call(this, "Alfresco.Events", htmlId, ["button", "container"]);

      return this;
   };

   YAHOO.extend(Alfresco.Events, Alfresco.component.Base,
   {

      /**
       * Object container for initialization options
       *
       * @property options
       * @type object
       */
      options:
      {
         /**
          * The nodeRef to the object that owns the disposition schedule that is configured
          *
          * @property nodeRef
          * @type {string}
          */
         nodeRef: null
      },

      /**
       * Fired by YUI when parent element is available for scripting
       * @method onReady
       */
      onReady: function Events_onReady()
      {
         // Save a reference to important elements
         this.widgets.completedEventsEl = Dom.get(this.id + "-completed-events");
         this.widgets.incompleteEventsEl = Dom.get(this.id + "-incomplete-events");

         // Get the templates and remove them from the DOM
         this.widgets.completedEventTemplate = Dom.get(this.id + "-completedEventTemplate");
         this.widgets.completedEventTemplate.parentNode.removeChild(this.widgets.completedEventTemplate);
         this.widgets.incompleteEventTemplate = Dom.get(this.id + "-incompleteEventTemplate");
         this.widgets.incompleteEventTemplate.parentNode.removeChild(this.widgets.incompleteEventTemplate);

         // Load events data
         this._refreshEvents();
      },

      /**
       * Refresh the events list
       *
       * @method _refreshEvents
       * @private
       */
      _refreshEvents: function Events__refreshEvents()
      {
         // TEMPORARY FOR TEST
         var nextDispositionAction = {
            asOf: "2009-12-12",
            events: [
               {
                  label: "Test event 1",
                  automatic: true,
                  completedAt: "2007-01-01",
                  completedBy: "Erik H",
                  complete: true
               },
               {
                  label: "Test event 2",
                  automatic: true,
                  completedAt: "2007-12-12",
                  completedBy: "Erik W",
                  complete: true
               },
               {
                  label: "Test event 3",
                  automatic: false,
                  asOf: "2008-12-12",
                  complete: false
               }
            ]
         };
         this._onEventsLoaded(nextDispositionAction);

         // TODO USE THIS INSTEAD WHEN IT WORKS
         return;
         Alfresco.util.Ajax.jsonGet(
         {
            url: Alfresco.constants.PROXY_URI_RELATIVE + "api/node/" + this.options.nodeRef.replace(":/", "") + "/nextdispositionaction",
            successCallback:
            {
               fn: function(response)
               {
                  this._onEventsLoaded(response.json.data);
               },
               scope: this
            },
            failureMessage: this.msg("message.getEventFailure")
         });
      },

      /**
       * Called when the events information has been loaded
       *
       * @method _onEventsLoaded
       * @private
       */
      _onEventsLoaded: function Events__onEventsLoaded(nextDispositionAction)
      {
         this.widgets.completedEventsEl.innerHTML = "";
         this.widgets.incompleteEventsEl.innerHTML = "";
         var events = nextDispositionAction.events ? nextDispositionAction.events : [];
         for(var i = 0; i < events.length; i++)
         {
            var event = events[i];
            if(event.complete)
            {
               var eventEl = this._createEvent(event, [
                  { "name" : event.label },
                  { "automatic" : event.automatic ? this.msg("label.automatic") : this.msg("label.manual") },
                  { "completed-at" : event.completedAt },
                  { "completed-by" : event.completedBy }
               ], "undo-button", this.onCompleteEventButtonClick, this.widgets.completedEventTemplate);
               eventEl = this.widgets.completedEventsEl.appendChild(eventEl);
            }
            else
            {
               var eventEl = this._createEvent(event, [
                  { "name" : event.label },
                  { "automatic" : event.automatic ? this.msg("label.automatic") : this.msg("label.manual") },
                  { "asof" : nextDispositionAction.asOf }
               ], "complete-button", this.onUndoEventButtonClick, this.widgets.incompleteEventTemplate);
               eventEl = this.widgets.incompleteEventsEl.appendChild(eventEl);
            }
         }
      },

      /**
       * Create an event
       *
       * @method _createEvent
       * @param event The event info object
       * @private
       */
      _createEvent: function Events__createEvent(event, attributes, buttonClass, clickHandler, template)
      {
         // Clone template
         var eventEl = template.cloneNode(true);
         var elId = Dom.generateId();
         eventEl.setAttribute("id", elId);

         // Display data
         for(var i = 0; i < attributes.length; i++)
         {
            var attribute = attributes[i];
            for(var key in attribute)
            {
               Selector.query("." + key + " .value", eventEl, true).innerHTML = attribute[key];
               break;
            }
         }

         // Create button
         var buttonEl = Dom.getElementsByClassName(buttonClass, "span", eventEl)[0];
         var eventButton = Alfresco.util.createYUIButton(this, buttonClass, null, {}, buttonEl);
         eventButton.on("click", clickHandler,
         {
            event: event,
            eventEl: eventEl,
            button: eventButton
         }, this);

         return eventEl;
      },

      /**
       * Fired when the user clicks the complete button for an event.
       *
       * @method onCompleteEventButtonClick
       * @param e {object} a "click" event
       * @param obj.event {object} object with event info
       * @param obj.eventEl {HTMLElement} The html element representing an event
       * @param obj.button {YAHOO.widget.Button} The button that was clicked
       */
      onCompleteEventButtonClick: function Events_onCompleteEventButtonClick(e, obj)
      {
         // Disable buttons to avoid double submits or cancel during post
         obj.button.set("disabled", true);

         alert('Not timplemented yet');

      },

      /**
       * Fired when the user clicks the undo button for an event.
       *
       * @method onUndoEventButtonClick
       * @param e {object} a "click" event
       * @param obj.event {object} object with event info
       * @param obj.eventEl {HTMLElement} The html element representing an event
       * @param obj.button {YAHOO.widget.Button} The button that was clicked
       */
      onUndoEventButtonClick: function Events_onUndoEventButtonClick(e, obj)
      {
         // Disable buttons to avoid double submits or cancel during post
         obj.button.set("disabled", true);

         alert('Not timplemented yet');

      },

      /**
       * Fired when the user clicks the undo button for an event.
       *
       * @method _doEventAction
       * @param action The name of action the action to be invoked
       * @param nodeRef the nodeRef to the event to perform the aciton on
       */
      _doEventAction: function Events__doEventAction(action, nodeRef, pendingMessage, failureMessage)
      {
         var feedbackMessage = Alfresco.util.PopupManager.displayMessage(
         {
            text: this.msg(pendingMessage),
            spanClass: "wait",
            displayTime: 0
         });

         Alfresco.util.Ajax.jsonPost(
         {
            url: Alfresco.constants.PROXY_URI_RELATIVE + "api/rma/actions/ExecutionQueue",
            dataObj: {
               nodeRef : nodeRef,
               name : action,
               params : {}
            },
            successHandler:
            {
               fn: function(serverReponse)
               {
                  feedbackMessage.destroy();
                  this._refreshEvents();
               },
               scope: this
            },
            failureMessage: failureMessage
         });
      }

   });
})();