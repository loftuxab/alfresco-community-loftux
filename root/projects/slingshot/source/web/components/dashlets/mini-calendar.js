/*
 *** Alfresco.MiniCalendar
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

   Alfresco.MiniCalendar = function(htmlId)
   {
      this.name = "Alfresco.MiniCalendar";
      this.id = htmlId;
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);
      
      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["calendar"], this.onComponentsLoaded, this);
      
      return this;
   };
   
   Alfresco.MiniCalendar.prototype =
   {
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
      onComponentsLoaded: function()
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
         /* 
          * Separate the (initial) rendering of the calendar from the data loading.
          * If for some reason the data fails to load, the calendar will still display.
          */
         var uriEvents = Alfresco.constants.PROXY_URI + "calendar/eventList?site=" + this.siteId;
         
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
       * @param e {object} DomEvent
       */
      onSuccess: function(o)
      {
         var noEventHTML = '<div class="detail-list-item first-item last-item"><span>'+this._msg("label.no-items")+'</span></div>';
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
         Dom.get(this.id + "-eventsContainer").innerHTML = (hasEvents) ? eventHTML : noEventHTML;
      },
      
      renderDay: function(date, eventData)
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
      onFailure: function(o)
      {
         /* Failed */
         //alert("Failed to load calendar data.");
      },
      /**
       * Gets a custom message
       *
       * @method _msg
       * @param messageId {string} The messageId to retrieve
       * @return {string} The custom message
       * @private
       */
      _msg: function Activities__msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, "Alfresco.MiniCalendar", Array.prototype.slice.call(arguments).slice(1));
      },
      /**
       * Set messages for this component
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       */
      setMessages: function setMessages(obj)
      {
         Alfresco.util.addMessages(obj, this.name);
         return this;
      }      
   
   };
})();
