/**
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * Mini Calendar component.
 * 
 * @namespace Alfresco.dashlet
 * @class Alfresco.dashlet.MiniCalendar
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML;

   Alfresco.dashlet.MiniCalendar = function MiniCalendar_constructor(htmlId)
   {
      return Alfresco.dashlet.MiniCalendar.superclass.constructor.call(this, "Alfresco.dashlet.MiniCalendar", htmlId, ["calendar"]);
   };
   
   YAHOO.extend(Alfresco.dashlet.MiniCalendar, Alfresco.component.Base,
   {
      /**
       * Fired by YUI when parent element is available for scripting.
       * Initialises components, including YUI widgets.
       *
       * @method onReady
       */ 
      onReady: function MiniCalendar_onReady()
      {
         /* 
          * Separate the (initial) rendering of the calendar from the data loading.
          * If for some reason the data fails to load, the calendar will still display.
          */
         var uriEvents = Alfresco.constants.PROXY_URI + "calendar/eventList?site=" + this.options.siteId;
         
         var callback = 
         {
            success: this.onSuccess,
            failure: this.onFailure,
            scope: this
         };
         
         YAHOO.util.Connect.asyncRequest('GET', uriEvents, callback);
      },

      /**
       * Event handler that gets fired when the calendar data for the current site.
       * is loaded successfully.
       *
       * @method onSuccess
       * @param o {object} Result of AJAX call
       */
      onSuccess: function MiniCalendar_onSuccess(o)
      {
         var noEventHTML = '<div class="detail-list-item first-item last-item"><span>' + this.msg("label.no-items") + '</span></div>';
         var eventHTML = '';
         var hasEvents = false;
         try 
         {
            var eventList = YAHOO.lang.JSON.parse(o.responseText);
            var now = new Date();
            now.setHours(0, 0, 0, 0);
            
            for (var key in eventList)
            {
               if (eventList.hasOwnProperty(key))
               {
                  var dateParts = key.split("/");
                  var date = YAHOO.widget.DateMath.getDate(dateParts[2], (dateParts[0] - 1), dateParts[1]);
                  if (date >= now)
                  {
                     hasEvents = true;                     
                     eventHTML += this.renderDay(date, eventList);
                  }
               }
            }
         } 
         catch (e)
         {
            // Do nothing
            eventHTML = "Could not load calendar data";
         }
         Dom.get(this.id + "-eventsContainer").innerHTML = hasEvents ? eventHTML : noEventHTML;
      },

      /**
       * Render an event
       *
       * @method renderDay
       * @param data {Date} Date to render
       * @param eventData {object} Event data
       */
      renderDay: function MiniCalendar_renderDay(date, eventData)
      {
         var theStupidDate = Alfresco.util.formatDate(date, "m/d/yyyy");
         var theDate = Alfresco.util.toISO8601(date,
         {
            selector: "date"
         });
         var events = eventData[theStupidDate];
         var html = "", item;
         if (events && events.length > 0)
         {
            var title = Alfresco.util.formatDate(date, "ddd, d mmm yyyy");
            var url = Alfresco.constants.URL_CONTEXT + "page/site/" + this.siteId + "/calendar?view=day&date=" + theDate;
            html += '<div class="detail-list-item">';
            html += '<div class="icon"><img src="' + Alfresco.constants.URL_CONTEXT + 'components/calendar/images/calendar-16.png" alt="day" /></div>';
            html += '<div class="details2"><h4><a href="'+url+'" class="theme-color-1">' + title + '</a></h4>';
            for (var i = 0, ii = events.length; i < ii; i++)
            {
               item = events[i];
               //if start and end match it is an allday or multiday event
               if (item.start === item.end)
               {
                  html += '<div><span><a href="' + url + '">' + $html(item.name) + '</a></span></div>';                  
               }
               else
               {
                  html += '<div><span>' + item.start + ' <a href="' + url + '">' + $html(item.name) + '</a></span></div>';                  
               }

            }
            html += '</div></div>';
         }
         return html;   
      },
      
      /**
       * Event handler that gets fired when the calendar data for the current site.
       * fails to load. Displays an alert informing the user that the data didn't load.
       *
       * @method onFailure
       * @param e {object} DomEvent
       */
      onFailure: function MiniCalendar_onFailure(o)
      {
         Dom.get(this.id + "-eventsContainer").innerHTML = "Failed to load calendar data.";
      }
   });
})();