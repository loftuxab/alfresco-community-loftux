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

/**
 * Alfresco.Calendar
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
       Event = YAHOO.util.Event,
       Element = YAHOO.util.Element;
   var dateFormat = Alfresco.thirdparty.dateFormat;
   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML;
   
   Alfresco.CalendarView = function(htmlId)
   {
      this.name = "Alfresco.CalendarView";
      this.id = htmlId;
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);
      
      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["calendar", "button","resize"], this.componentsLoaded, this);
      
      return this;
   };
   
   Alfresco.CalendarView.prototype =
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
        * Current siteId.
        * 
        * @property siteId
        * @type string
        * @default null
        */
         siteId: ""
      },
      /**
       * Object container for storing module instances.
       * 
       * @property modules
       * @type object
       */
        modules: null,      
      
      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       */
      setOptions: function CalendarView_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
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
         Event.onContentReady(this.id, this.init, this, true);
      },
      
      /**
       * Fired by YUI when parent element is available for scripting.
       * Initialises components, including YUI widgets.
       *
       * @method init
       */
      init: function()
      {
         YAHOO.Bubbling.on("eventEdited", this.onEventEdited, this);
         YAHOO.Bubbling.on("eventSaved", this.onEventSaved, this);
         YAHOO.Bubbling.on("eventDeleted", this.onEventDeleted, this);
         YAHOO.Bubbling.on("eventResized", this.onEventResized, this);

         YAHOO.Bubbling.on("tagSelected", this.onTagSelected, this);
         YAHOO.Bubbling.on("todayNav", this.onTodayNav, this);
         YAHOO.Bubbling.on("nextNav", this.onNav, this);
         YAHOO.Bubbling.on("prevNav", this.onNav, this);
         YAHOO.Bubbling.on("viewChanged", this.onViewChanged, this);
         YAHOO.Bubbling.on("dateChanged",this.onCalSelect,this);
         YAHOO.Bubbling.on("formValidationError", this.onFormValidationError,this);         
         
         this.calendarView = this.options.view;
         this.startDate = (YAHOO.lang.isString(this.options.startDate)) ? Alfresco.util.fromISO8601(this.options.startDate): this.options.startDate;
         this.container = Dom.get(this.id);

         this.initDD(); 
         this.initEvents();
         this.initCalendarEvents();
         this.addButton = Alfresco.CalendarHelper.renderTemplate('createEventButton',
         {
             addEventUrl:Alfresco.constants.URL_CONTEXT+'components/calendar/images/add-event-16-2.png',
             addEvent:this._msg('label.add-event')
         });
         
         if (this.calendarView !== Alfresco.CalendarView.VIEWTYPE_MONTH)
         {
            this.getEvents(Alfresco.util.formatDate(this.options.startDate,'yyyy-mm-dd'));
         }
         else 
         {
            var events = [];
            for (var event in this.events)
            {
               events.push(this.events[event].getData().registry);
            }
            //have to delay as mini calendar hasn't registered for event yet as it's not loaded yet.
            YAHOO.lang.later(1000,this,function(events)
            { 
               YAHOO.Bubbling.fire("eventDataLoad",events);
            },
            [events]);
            // add view for events that span multiple days.
            var allDayEvents = Dom.getElementsByClassName('allday');
            for (var i=0,len=allDayEvents.length;i<len;i++)
            {
               this.renderMultipleDay(allDayEvents[i]);
            }

         }
         this.isShowingEarlyRows = true;
         this.titleEl = Dom.get('calTitle');
         switch(this.calendarView)
         {
            case Alfresco.CalendarView.VIEWTYPE_MONTH:
                this.titleEl.innerHTML = Alfresco.util.formatDate(this.options.titleDate,'mmmm yyyy');
                break;
            case Alfresco.CalendarView.VIEWTYPE_WEEK:
                this.titleEl.innerHTML = Alfresco.util.formatDate(this.options.titleDate,'d mmmm yyyy');
                break;
            case Alfresco.CalendarView.VIEWTYPE_DAY:
                this.titleEl.innerHTML = Alfresco.util.formatDate(this.options.titleDate,'dddd, d mmmm');
                break;
         }
         //highlight current date
         if (this.calendarView===Alfresco.CalendarView.VIEWTYPE_MONTH)
         {
            var now = new Date();
            if (this.options.startDate.getFullYear()===now.getFullYear() && (this.options.startDate.getMonth()==now.getMonth()))
            {
              var el = Dom.get('cal-'+(Alfresco.util.toISO8601(now).split('T')[0]));
              Dom.addClass(el,'current');
            }
         }

         if (!YAHOO.widget.DateMath.HOUR)
         {
            YAHOO.widget.DateMath.add = function()
            {
                var origAddFunc = YAHOO.widget.DateMath.add;
                YAHOO.widget.DateMath.HOUR = 'H';
                YAHOO.widget.DateMath.SECOND = 'S';
                YAHOO.widget.DateMath.MINUTE = 'Mn';
                return function(date,field,amount)
                {

                    switch(field){
                        case YAHOO.widget.DateMath.MONTH:
                        case YAHOO.widget.DateMath.DAY:
                        case YAHOO.widget.DateMath.YEAR:
                        case YAHOO.widget.DateMath.WEEK:
                            return origAddFunc.apply(YAHOO.widget.DateMath,arguments);
                            break;
                        case YAHOO.widget.DateMath.HOUR:
                            var newHour = date.getHours()+amount;
                            var day = 0;
                            if (newHour < 0)
                            {
                                while(newHour < 0)
                                {
                                    newHour+=24;
                                    day-=1;

                                }
                                // newHour = 23;
                            }
                            if (newHour > 24)
                            {
                                while(newHour > 24)
                                {
                                    newHour-=24;
                                    day+=1;

                                }
                            }
                            YAHOO.widget.DateMath._addDays(date,day);
                            date.setHours(newHour);                                
                            break;
                        case YAHOO.widget.DateMath.MINUTE:
                            date.setMinutes(date.getMinutes()+amount);
                            break;
                        case YAHOO.widget.DateMath.SECOND:
                            date.setMinutes(date.getSeconds()+amount);
                        
                    }
                    return date;
                };
            }();
         }
      },
      
      /**
       * Initialises drag and drop targets.
       * 
       * @method initDD
       */
      initDD : function() {
          this.dragGroup = (this.calendarView===Alfresco.CalendarView.VIEWTYPE_MONTH) ? 'day' : 'hourSegment';

          var dragTargets = Dom.getElementsByClassName(this.dragGroup,'div',YAHOO.util.Dom.get(this.options.id));
          this.hourSegments = dragTargets;
          dragTargets = dragTargets.concat(Dom.getElementsByClassName('target','div',YAHOO.util.Dom.get(this.options.id)));

          this.dragTargetRegion = YAHOO.util.Dom.getRegion(dragTargets[0]);

          for (var i=0,el;el=dragTargets[i];i++) 
          {
              new YAHOO.util.DDTarget(el, this.dragGroup);
          }
          
          //holder for fixed hourSegments (ie border bleedthrough fix)
          this._fixedHourSegments = [];
      },
      
      /**
       * initialise config object for calendar events
       * 
       * @method initCalendarEvents
       *  
       */
      initCalendarEvents : function() {
         var tickSize = (this.dragTargetRegion.bottom-this.dragTargetRegion.top)/2;
            this.calEventConfig = {
                //work out div.hourSegment half-height so we can get xTick value for resize
                resize: {
                    xTicks :  tickSize
                },
                yTick : (this.calendarView!==Alfresco.CalendarView.VIEWTYPE_MONTH) ? tickSize : null,
                xTick : (this.calendarView!==Alfresco.CalendarView.VIEWTYPE_MONTH) ? 100 : null,
                view  : this.calendarView,
                resizable : ((this.calendarView===Alfresco.CalendarView.VIEWTYPE_WEEK) | (this.calendarView===Alfresco.CalendarView.VIEWTYPE_DAY))
            };
         var vEventEls = Dom.getElementsByClassName('vevent',null,YAHOO.util.Dom.get(this.options.id));
         var numVEvents = vEventEls.length;
         this.events = [];
         if (this.calendarView===Alfresco.CalendarView.VIEWTYPE_DAY)
         {
            this.calEventConfig.performRender = false;
         }
         for (var i=0;i<numVEvents;i++)
         {
            var vEventEl = vEventEls[i];
            var id = Event.generateId(vEventEl);
            vEventEl.id = id;
            if ((this.calendarView===Alfresco.CalendarView.VIEWTYPE_WEEK) | (this.calendarView===Alfresco.CalendarView.VIEWTYPE_DAY)) {
                this.calEventConfig.resizable = (Dom.hasClass(vEventEl,'allday')) ? false : true;
            }
              this.calEventConfig.draggable = Dom.hasClass(vEventEl,'allday') ? false : true;
              this.events[id] = new Alfresco.calendarEvent(vEventEl, this.dragGroup,this.calEventConfig);
              this.events[id].on('eventMoved', this.onEventMoved, this.events[id], this);

            if ((this.calendarView===Alfresco.CalendarView.VIEWTYPE_WEEK) | (this.calendarView===Alfresco.CalendarView.VIEWTYPE_DAY))
            {
                this.adjustHeightByHour(vEventEl);
            }
        }
      },
      
      /**
       * Initialises event handling
       * All events are handled through event delegation via the onInteractionEvent handler
       * 
       * @method initEvents
       * 
       */
      initEvents : function()
      {
          Event.on(this.id,'click',this.onInteractionEvent,this,true);
          Event.on(this.id,'dblclick',this.onInteractionEvent,this,true);
          if (this.calendarView == Alfresco.CalendarView.VIEWTYPE_MONTH) 
          {
            Event.on(this.id,'mouseover',this.onInteractionEvent,this,true);
            Event.on(this.id,'mouseout',this.onInteractionEvent,this,true);
          }
      },
      
      /**
       * Retrieves events from server
       * 
       * @method getEvents
       *  
       */
      getEvents : function()
      {
         Alfresco.util.Ajax.request(
         {
            url: Alfresco.constants.PROXY_URI + "calendar/events/" + this.options.siteId + "/user",
            dataObj:
            {
               from: Alfresco.util.toISO8601(this.options.startDate).split('T')[0]
            },
            
            successCallback: //filter out non relevant events for current view
            {
               fn: function(o) 
               {
                  // var filterThreshold = null;
                  //                   if (this.calendarView===Alfresco.CalendarView.VIEWTYPE_WEEK)
                  //                   {
                  //                       filterThreshold = YAHOO.widget.DateMath.getWeekNumber(this.options.startDate);
                  //                   }
                  //                   if (this.calendarView===Alfresco.CalendarView.VIEWTYPE_WEEK)
                  //                   {
                  //                       filterThreshold = YAHOO.widget.DateMath.getWeekNumber(this.options.startDate);
                  //                   }
                  var data = YAHOO.lang.JSON.parse(o.serverResponse.responseText).events;
                  var siteEvents = [];
                  var events = [];
                  var comparisonFn = null;
                  var site = this.options.siteId;
                  for (var i=0;i<data.length;i++) {
                    var ev = data[i];
                    if (ev.site==site){
                      siteEvents.push(ev);
                    }
                  }
                  data = siteEvents;
                  switch(this.calendarView)
                  {
                    case Alfresco.CalendarView.VIEWTYPE_DAY:
                        comparisonFn = function(d) {
                            var day = this.options.startDate.getDate();
                            var m = this.options.startDate.getMonth();
                            var y = this.options.startDate.getFullYear();

                            return function(d)
                            {
                                return ((d.getMonth()===m) && (y === d.getFullYear()) && (d.getDate()===day) );
                            };
                        }.apply(this);
                        break; 
                    case Alfresco.CalendarView.VIEWTYPE_WEEK:
                        comparisonFn = function() {
                            //have to use a copy as YAHOO.widget.DateMath.getWeekNumber
                            //clears the time of the specified date 
                            var startDateCopy = new Date();
                            startDateCopy.setTime(this.options.startDate.getTime());
                            var currentWeek = YAHOO.widget.DateMath.getWeekNumber(startDateCopy);
                            var y = this.options.startDate.getFullYear();
                            return function(d)
                            {
                                return ((y === d.getFullYear()) && (YAHOO.widget.DateMath.getWeekNumber(d)===currentWeek) ) ;
                            };
                        }.apply(this);
                        break; 
                    case Alfresco.CalendarView.VIEWTYPE_MONTH:
                        break; 
                    case Alfresco.CalendarView.VIEWTYPE_AGENDA:
                        comparisonFn = function() {
                            
                            var m = this.options.startDate.getMonth()+1;
                            var y = this.options.startDate.getFullYear();
                            return function(d)
                            {
                                return ((y === d.getFullYear()) && (m==(d.getMonth()+1)) );
                            };
                        }.apply(this);
                        break;
                  }
                  for (var i=0;i<data.length;i++){
                    var ev = data[i];
                    var date = Alfresco.util.fromISO8601(ev.when);
                    var endDate = Alfresco.util.fromISO8601(ev.endDate);
                    if (comparisonFn(date))
                    {
                        var datum = {};
                        datum.desc = ev.description || '';
                        datum.name = ev.title;
                        datum.where = ev.where;
                        datum.contEl = 'div';
                        datum.from = dateFormat(date,dateFormat.masks.isoDate)+'T'+ev.start;
                        datum.to =dateFormat(endDate,dateFormat.masks.isoDate)+'T'+ev.end; 
                        datum.uri = '/calendar/event/'+this.options.siteId+'/'+ev.name;
                        datum.hidden ='';
                        datum.allday = '';
                        datum.el = 'div';
                        datum.duration = Alfresco.CalendarHelper.getDuration(Alfresco.util.fromISO8601(datum.from),Alfresco.util.fromISO8601(datum.to));
                        var days = datum.duration.match(/([0-9]+)D/);
                        if (days && days[1])
                        {
                           datum.duration = datum.duration.replace(/([0-9]+)D/,++days[1]+'D')                           
                        }

                        datum.key = datum.from.split(":")[0]+':00';
                        datum.start = ev.start;
                        datum.end = ev.end;
                        datum.tags = ev.tags;
                        events.push(datum);
                    };
                  };

                  this.addEvents(events);
               },
               scope: this
            },
               failureMessage: Alfresco.util.message("load.fail", "Alfresco.CalendarView")
           });
      },
      
      /**
       * Add events to DOM
       *  
       * @method addEvents
       * 
       */
      addEvents : function(events) 
      {
        var view = this.calendarView;
        if ( (view===Alfresco.CalendarView.VIEWTYPE_WEEK)  | (view===Alfresco.CalendarView.VIEWTYPE_DAY))
        {
          var offsetTop = 0;
          var min;
          var getTargetEl = function(ev)
          {
            return function(ev) {

                var segments  = Dom.getElementsByClassName('hourSegment','div',Dom.get('cal-'+ev.key));
                var min = parseInt(ev.from.split('T')[1].split(':')[1],10);
                var targetEl = (min>=30) ? segments[1] : segments[0];
                //on the hour or half hour
                if (min%2!==0)
                {
                    var reg = Dom.getRegion(segments[0]);
                    offsetTop = parseInt((reg.bottom-reg.top)/2,10);
                }
                return targetEl;
            };                
          }(this.calendarView);
        
          var len = events.length;
          var tdsWithEvents = [];
          for (var i = 0;i<len;i++)
          {
              var ev = events[i];
              var vEventEl = Alfresco.CalendarHelper.renderTemplate('vevent',ev);
              var id = Event.generateId(vEventEl);
              vEventEl.id = id;
              //all day
              if (ev.start===ev.end) 
              {
                this.renderAllDayEvents(vEventEl,ev);
              }
              else {
                var targetEl = getTargetEl(ev);

                if (targetEl)
                {

                    YAHOO.util.Dom.setStyle(targetEl,'position','relative');
                    targetEl.appendChild(vEventEl);                
                }
                Dom.setStyle(vEventEl,'top',offsetTop+'px');              

                var td = Dom.getAncestorByTagName(vEventEl,'td');

                //maintain list of tds in with events have been added
                if (!tdsWithEvents[td.id])
                {
                  tdsWithEvents[td.id]=td;
                }    
              }
          }
          
        }
        else if (view === Alfresco.CalendarView.VIEWTYPE_AGENDA)
        {
          //sort events by day
          var sortedEvents = [];
          var numEvents = events.length;
          
          for (var i=0;i<numEvents;i++)
          {
            var event = events[i];
            var date = event.from.split('T')[0];
            if (!sortedEvents[date])
            {
              sortedEvents[date]=[];
            }
            sortedEvents[date].push(event);
          }
          //render
          var appendNode = Dom.getElementsByClassName('agendaview')[0];
          this.renderAgendaDayEvents(sortedEvents,appendNode);  
        }
        this.initCalendarEvents();
        if ( (view===Alfresco.CalendarView.VIEWTYPE_WEEK)  | (view===Alfresco.CalendarView.VIEWTYPE_DAY))
        {
          //render multiple events correctly
          this.renderMultipleEvents();
          if (YAHOO.env.ua.ie)
          {
            //set any div.hourSegment to position:static if it does not have any events within
            for (var i=0,len = this.hourSegments.length;i<len;i++)
            {
              var hourSegment = this.hourSegments[i];
              var hasEvent = (YAHOO.util.Dom.getElementsByClassName('vevent','div',hourSegment).length>0);
              if (YAHOO.util.Dom.getStyle(hourSegment,'position')==='relative')
              {
                if (!hasEvent)
                {
                  YAHOO.util.Dom.setStyle(hourSegment,'position','static');
                }
                else
                {
                  if (YAHOO.env.ua.ie>6)
                  {
                    YAHOO.util.Dom.setStyle(hourSegment,'border-bottom-color','transparent');                  
                  }
                  else
                  {
                     YAHOO.util.Dom.setStyle(hourSegment,'border-bottom-width',0);
                  }

                  this._fixedHourSegments.push(hourSegment);
                }
              }            
            }            
          }
        }
        YAHOO.Bubbling.fire("eventDataLoad",events); 
      },
      /** Render events in agenda view
       *  
       * @param events {array} events keyed by date
       * @param targetEl {element} element to add events to.
       * @param insertBeforeNode {element} (optional) if specified, events are added before this element
       */
      renderAgendaDayEvents : function(events,targetEl,insertBeforeNode)
      {
          for (var event in events)
          {
            var date = Alfresco.util.formatDate(Alfresco.util.fromISO8601(events[event][0].from),'dddd, d mmmm');
            var contDiv = document.createElement('div');
            contDiv.id = 'cal-' + events[event][0].from.split('T')[0];
            var header = Alfresco.CalendarHelper.renderTemplate('agendaDay',{date:date});
            var eventsHTML = '';
            var ul = document.createElement('ul');
            var agendaEvts = events[event];
            for (var i = 0; i<agendaEvts.length;i++)
            {
              var event = agendaEvts[i];
              event.hidden='';
              event.contel = 'div';
              event.el = 'li';
              if (event.start===event.end) {
                event.allday = 'allday';
              }
              var evEl = Alfresco.CalendarHelper.renderTemplate('vevent',event);
              Dom.generateId(evEl);
              if (event.start===event.end)
              {
                 var pEl = Dom.getElementsByClassName('dates','p',evEl)[0];
                 Dom.addClass(pEl,'theme-bg-color-1');
                 Dom.addClass(pEl,'theme-color-1');                 
              }
              ul.appendChild(evEl);
            }
            
            contDiv.appendChild(ul);
            
            if (!insertBeforeNode)
            {
               targetEl.appendChild(header);
               targetEl.appendChild(contDiv);               
            }
            else
            {
               targetEl.insertBefore(header,insertBeforeNode);
               // Dom.insertAfter(header,insertAfterNode);
               Dom.insertAfter(contDiv,header);
            }
         }
         return evEl;
      },
      /**
       * Event Delegation handler. Delegates to correct handlers using CSS selectors
       *
       * @method onInteractionEvent
       * @param e {object} DomEvent
       * @param args {array} event arguments
       */
      onInteractionEvent: function(e,args) {
        var elTarget = Event.getTarget(e);
        
        if (e.type === 'mouseover'){
          if ( YAHOO.util.Selector.test(elTarget, 'div.'+this.dragGroup) ) {
              Dom.addClass(elTarget,'highlight');
              if (this.options.permitToCreateEvents==='true')
              {
                if (!Dom.hasClass(elTarget,'disabled'))
                {
                   elTarget.appendChild(this.addButton);                   
                }
              }
          }
        }
        else if (e.type === 'mouseout'){
          if ( YAHOO.util.Selector.test(elTarget, 'div.'+this.dragGroup) ) {
              Dom.addClass(elTarget,'highlight');
          }
        }
        
        if (e.type==='click') 
        {
          if (YAHOO.util.Selector.test(elTarget, 'a#collapseTriggerLink'))
          {
              this.toggleEarlyTableRows();
              YAHOO.util.Event.preventDefault(e);                
          }
          if ( YAHOO.util.Selector.test(elTarget, 'button#addEventButton') )
          {
              this.showAddDialog(elTarget);
          }
          else if ( YAHOO.util.Selector.test(elTarget, 'a.summary') )
          {
              this.showDialog(e,elTarget);
          }
          if ( YAHOO.util.Selector.test(elTarget, 'li.moreEvents a') )
          {
              this.onShowMore(e,args,elTarget);
          }
        }
      },
      /**
       *  Adds events in month view
       * 
       * @method _addEventInMonthView
       * @param targetEl {object} Element in which to add event
       * @param data {object} Value object of event data
       * @param vEventEl {object} reference to exising event element
       * 
       * @return vEventEl {object} reference to event element
       */
      _addEventInMonthView : function(targetEl,data,vEventEl)
      {
        if(targetEl) {
           var ul = targetEl.getElementsByTagName('ul');
        }
        
        
        var elUl = null;
        var someHidden = false;
        if (data.allday!=='true')
        {
          data.el = 'li';  
          data.contEl = 'div';
        }
        else {
          data.el = 'div';
          data.contEl='div';

        }

        if (data.dtstart) { // have to convert
          data = this.convertDataToTemplateData(data);
          data.hidden = '';
        }
        if (vEventEl)
        {
           vEventEl.parentNode.removeChild(vEventEl);
           vEventEl=null;           
        }

        if (data.allday!=='')
        {
          data.allday='allday';
          data.hidden = '';
          var vEventEl = Alfresco.CalendarHelper.renderTemplate('vevent',data);
          YAHOO.util.Dom.generateId(vEventEl);
          var id = (data.fromDate) ? data.fromDate.split('T')[0] : data.from.split('T')[0];
          var targetEl = YAHOO.util.Dom.get('cal-'+id);
          targetEl = YAHOO.util.Dom.getElementsByClassName('day','div',targetEl)[0];
          YAHOO.util.Dom.insertAfter(vEventEl,YAHOO.util.Dom.getElementsByClassName('dayLabel',null,targetEl)[0]);
          return vEventEl;
        }
        else {
           //day has no event so add ul
           if (ul.length === 0) {
             elUl = document.createElement('ul');
             Dom.addClass(elUl,'dayEvents');
             elUl = targetEl.appendChild(elUl);
           }
           else 
           { 
             elUl = ul[0];
           }
           var dayEvents = elUl.getElementsByTagName('li');
           if (dayEvents.length>=5)
           {
             data.hidden = 'hidden';
             someHidden = true;
           }
           else 
           {
              data.hidden = '';
           }
           if (data.tags && YAHOO.lang.isArray(data.tags))
           {
             data.tags = data.tags.join(' ');
           }
           var vEventEl = Alfresco.CalendarHelper.renderTemplate('vevent',data);
           YAHOO.util.Dom.generateId(vEventEl);
           Dom.removeClass(vEventEl,'theme-bg-color-1');
           var moreEventsTrigger = YAHOO.util.Dom.getElementsByClassName('moreEvents','li',elUl);
           if (moreEventsTrigger.length>0)
           {
             elUl.insertBefore(vEventEl,moreEventsTrigger[0]);
           }
           else {
             elUl.appendChild(vEventEl);
             if (someHidden) {
               elUl.innerHTML +='<li class="moreEvents"><a href="">'+this._msg('label.show-more')+'</a></li>';
             }
           }
        }
        return vEventEl;
      },
      
      
      /**  converts data object to template compatible
       *  
       *  @method convertDataToTemplateData
       *  @param data {object} data to convert
       *  @return {object} data object with template compatible variables
       * 
       */
       convertDataToTemplateData : function(data) {
          data.fromDate = data.dtstart;
          data.toDate = data.dtend;
          data.where = data.location;
          data.desc = data.description;
          data.name = data.summary;
          data.duration = Alfresco.CalendarHelper.getDuration(Alfresco.util.fromISO8601(data.dtstart),Alfresco.util.fromISO8601(data.dtend));
          data.start = data.dtstart.split('T')[1].substring(0,5);
          data.end = data.dtend.split('T')[1].substring(0,5);
          data.allday = (data.allday==='true') ? data.allday : '';
          data.tags = (YAHOO.lang.isArray(data.tags)) ? data.tags.join(' ') : data.tags;
          return data; 
       },
      /**
       * Render all day events
       * 
       * @method renderAllDayEvents
       * @param eventEl {object} reference to event element
       * @param data {object} Value object of event data
       * 
       */
      renderAllDayEvents : function(eventEl,data) 
      {
        YAHOO.util.Dom.generateId(eventEl);
        var targetEl;
        // put into all day section
        if (this.calendarView===Alfresco.CalendarView.VIEWTYPE_WEEK)
        {
          var dayOfWeek = Alfresco.util.fromISO8601(data.dtstart || data.from).getDay();
          targetEl = Dom.get('cal-'+(data.dtstart || data.from).split('T')[0]);

          // add view for events that span multiple days.
          this.renderMultipleDay(eventEl,data);
        }
        else if (this.calendarView===Alfresco.CalendarView.VIEWTYPE_DAY)
        { 
          targetEl = Dom.get('alldayCell');
        } 
        else if ((this.calendarView===Alfresco.CalendarView.VIEWTYPE_MONTH))
        {
          targetEl = Dom.get(eventEl.id);
          targetEl = Dom.getAncestorByClassName(targetEl,'day');
          eventEl = this._addEventInMonthView(targetEl,data,eventEl);

          // add view for events that span multiple days.
          this.renderMultipleDay(eventEl,data);

          this.calEventConfig.resizable = false;
          this.calEventConfig.draggable = false;
          this.events[eventEl.id] = new Alfresco.calendarEvent(eventEl, this.dragGroup,this.calEventConfig); 
        }
        else if ((this.calendarView===Alfresco.CalendarView.VIEWTYPE_AGENDA))
        {
           if (data.allday)
           {
               Dom.addClass(Dom.getElementsByClassName('dates','p',eventEl)[0],'theme-bg-color-1');
           }
           if (!data.fromDate)
           {
              data.fromDate = data.from
           }
           targetEl = Dom.get('cal-'+data.fromDate.split('T')[0]);
           //this is an on a date that doesn't have any events on it yet
           //so render required containing elements
           if (!targetEl)
           {
              var event = [];
              var identifier = data.fromDate.split('T')[0];
              event[identifier]= [];
              event[identifier].push(data);
              var adjEl = this.getAgendaInsertNode(identifier);
              eventEl = this.renderAgendaDayEvents(event,Dom.getElementsByClassName('agendaview')[0],adjEl);
              this.calEventConfig.resizable = false;
              this.calEventConfig.draggable = false;
              this.events[eventEl.id] = new Alfresco.calendarEvent(eventEl, this.dragGroup,this.calEventConfig);        
              return;
           }
           targetEl = targetEl.getElementsByTagName('ul')[0];
        }
        if (this.calendarView!==Alfresco.CalendarView.VIEWTYPE_MONTH)
        {
          targetEl.appendChild(eventEl);
        }  
        if ((this.calendarView!==Alfresco.CalendarView.VIEWTYPE_AGENDA))
        {
          Dom.addClass(eventEl,'allday');
          Dom.setStyle(eventEl,'width','100%');
          Dom.setStyle(eventEl,'height','auto');
          Dom.setStyle(eventEl,'top','auto');
          Dom.setStyle(eventEl,'left','auto');              
        }
        return eventEl;
      },
      
      renderMultipleDay : function (eventEl,data)
      {
          if (!data)
          {
             var data = new microformatParser(
             {
                 ufSpec : hcalendar,
                 srcNode : eventEl.id
             });
             data.parse();
             data = data.getAll();
             data = data.parsedData;
          }
          if (YAHOO.lang.isString(data.duration))
          {
             var durationObj = hcalendar.parsers['duration'](data.duration);             
          }
          else
          {
             var durationObj = data.duration;
          }
          if (durationObj && durationObj.D) 
          {

             var numDays = parseInt(durationObj.D);
             // numDays++;
             if (durationObj.W && durationObj.W>=1)
             {
                numDays+=parseInt((7*durationObj.W));
             }
             if (numDays>1)
             {
                var startDate = (data.from) ? Alfresco.util.fromISO8601(data.from) : data.dtstart;
                if (YAHOO.lang.isString(startDate))
                {
                   startDate = Alfresco.util.fromISO8601(startDate);
                }
                for (var i=1,len=numDays;i<numDays;i++)
                {
                   var date = YAHOO.widget.DateMath.add(startDate,YAHOO.widget.DateMath.DAY,i);
                   var dateCell = Dom.get('cal-'+Alfresco.util.toISO8601(date).split('T')[0]);
                   var targetCell = null;
                   //get target el depending on view
                   if ((this.calendarView===Alfresco.CalendarView.VIEWTYPE_MONTH))
                   {
                      if (dateCell)
                      {
                         targetCell = Dom.getElementsByClassName('day','div',dateCell)[0];                         
                      }
                   }
                   else if ((this.calendarView===Alfresco.CalendarView.VIEWTYPE_WEEK))
                   {
                      if (dateCell)
                      {
                         targetCell = dateCell;
                      }
                   }
                   var multipleAllDayEl = document.createElement('div');
                   multipleAllDayEl.className='allday multipleAllDay';
                   multipleAllDayEl.id=eventEl.id+'-multiple'+(i+1);
                   if (targetCell)
                   {
                      var ulEl = targetCell.getElementsByTagName('ul');
                      if (ulEl.length>0)
                      {
                         targetCell.insertBefore(multipleAllDayEl,ulEl[0]);
                      }
                      else 
                      {
                         targetCell.appendChild(multipleAllDayEl);
                      }
                   }
                }
             }
          }
      },
      /**
       * Tests if event is valid for view must be within startdate and (enddate-1 second) of current view
       * 
       * @method date {object} Date to validate
       * 
       * @return true|false
       * 
       */
      //
      isValidDateForView : function(date) 
      {
        return (date.getTime()>=this.options.startDate.getTime()) && (date.getTime()<this.options.endDate.getTime());
      },

      /**
       * Handler for eventEdited event. Updates event in DOM in response to updated event data.
       * 
       * @method  onEventEdited
       * 
       * @param e {object} event object
       * @param o {object} new event data
       *  
       */
      onEventEdited : function(e,o) 
      {
        Alfresco.util.PopupManager.displayMessage(
        {
           text: Alfresco.util.message('message.edited.success',this.name)
        });
        var data = o[1].data;
        var id = o[1].id;

        var event = null;
        if (this.events[id]) {
            event = this.events[id];
        }
        
        var eventEl = event.el;
        
        var targetEl = null;
        var dateParts = data.dtstart.split('T');
        var hour = dateParts[1].split(':')[0];
        var min =  dateParts[1].split(':')[1];
        var id = 'cal-'+dateParts[0];
        
        var evDate = Alfresco.util.fromISO8601(data.dtstart);
        // if event is valid for view must be within startdate and (enddate-1 second) of current view
        if (!this.isValidDateForView(evDate))
        {
          if (this.events[eventEl.id])
          {
            delete this.events[eventEl.id];              
          }
          var currPar = Dom.getAncestorByTagName(eventEl,'div');//div
          eventEl.parentNode.removeChild(eventEl);
        }
        else {
          if(data.allday && data.allday!='false')
          {
            if (data.dtstart) { // have to convert
             data = this.convertDataToTemplateData(data);
            }
            data.contEl='div';
            data.hidden='';
            if (this.calendarView!==Alfresco.CalendarView.VIEWTYPE_AGENDA)
            {
               data.el='div';

            }
            else 
            {
               data.el = 'li';
               data.allday='allday';               
            }
            var days = data.duration.match(/([0-9]+)D/);
            if (days && days[1])
            {
               data.duration = data.duration.replace(/([0-9]+)D/,++days[1]+'D')               
            }
            var currPar = Dom.getAncestorByTagName(eventEl,'div');//div            
            if (Dom.hasClass(eventEl,'allday'))
            {
               this.removeMultipleAllDayEvents(eventEl);
            }
            eventEl.parentNode.removeChild(eventEl);
            
            var eventEl = Alfresco.CalendarHelper.renderTemplate('vevent',data);         
            eventEl = this.renderAllDayEvents(eventEl,data);
            this.calEventConfig.draggable = false;
            this.calEventConfig.resizable = false;
            var newCalEvent = new Alfresco.calendarEvent(eventEl, this.dragGroup,YAHOO.lang.merge(this.calEventConfig,{performRender:false}));
            this.events[eventEl.id]=newCalEvent;
          }
          else 
          { 
            //move to correct cell
            Dom.removeClass(eventEl,'allday');
            this.removeMultipleAllDayEvents(eventEl);
            if ((this.calendarView===Alfresco.CalendarView.VIEWTYPE_MONTH))
            {
              targetEl = Dom.get(id);
              targetEl = Dom.getElementsByClassName('day','div',targetEl)[0];
              eventEl = this._addEventInMonthView(targetEl,data,eventEl);
              this.calEventConfig.draggable = true;
              this.calEventConfig.resizable = false;
              this.events[eventEl.id] = new Alfresco.calendarEvent(eventEl, this.dragGroup,this.calEventConfig);
            }
            else if ((this.calendarView===Alfresco.CalendarView.VIEWTYPE_WEEK) | (this.calendarView===Alfresco.CalendarView.VIEWTYPE_DAY))
            {              
              id += 'T'+hour+':00';
              var index = (parseInt(min,10)>=30) ? 1 : 0;
              targetEl = Dom.get(id);
              targetEl = Dom.getElementsByClassName('hourSegment','div',targetEl)[index];

              YAHOO.util.Dom.setStyle(targetEl,'position','relative');
              targetEl.appendChild(eventEl);

              data.duration = Alfresco.CalendarHelper.getDuration(Alfresco.util.fromISO8601(data.dtstart),Alfresco.util.fromISO8601(data.dtend));
              this.calEventConfig.draggable = YAHOO.util.Dom.hasClass(eventEl,'allday') ? false : true;
              this.calEventConfig.resizable = (Dom.hasClass(eventEl,'allday')) ? false : true;
              this.events[eventEl.id] = new Alfresco.calendarEvent(eventEl, this.dragGroup,this.calEventConfig);
            }
            else {
              data.el = 'li';  
               //tag with enclosing brackets
              data.contEl = 'div';
              data.hidden = '';
              data.allday='';
              data = this.convertDataToTemplateData(data);
              var elNextEvent = eventEl.nextSibling;
              var currPar = Dom.getAncestorByTagName(eventEl,'div');//div
              eventEl.parentNode.removeChild(eventEl);
              
              
              eventEl = Alfresco.CalendarHelper.renderTemplate('vevent',data);
              Dom.generateId(eventEl);
              targetEl = Dom.get('cal-'+data.fromDate.split('T')[0]);
              targetEl = targetEl.getElementsByTagName('ul')[0];
              Dom.removeClass(Dom.getElementsByClassName('dates','p',eventEl)[0],'theme-bg-color-1');
              if (!elNextEvent)
              {
                 targetEl.appendChild(eventEl);                 
              }
              else
              {
                 targetEl.insertBefore(eventEl,elNextEvent);
              }
              this.calEventConfig.draggable = false;
              this.calEventConfig.resizable = false;
              this.events[eventEl.id] = new Alfresco.calendarEvent(eventEl, this.dragGroup,this.calEventConfig);
            }
          }       
        }
        if (data.tags)
        {
          data.category=data.tags;          
        }

        if ((this.calendarView===Alfresco.CalendarView.VIEWTYPE_AGENDA))
        {
           this.cleanUpAgendaView(currPar);
        }
        this.events[eventEl.id].update(data);

        if (!Dom.hasClass(eventEl,'allday') && (this.calendarView!==Alfresco.CalendarView.VIEWTYPE_AGENDA))
        {
          this.adjustHeightByHour(eventEl);
        }
        // Refresh the tag component
        if ((this.calendarView===Alfresco.CalendarView.VIEWTYPE_WEEK) && (this.calendarView===Alfresco.CalendarView.VIEWTYPE_DAY))
        {
          this.renderMultipleEvents();
        }
        YAHOO.Bubbling.fire("tagRefresh");
        
      },
      /**
       * 
       * if old parent has no li els anymore (event has been moved to new day)
       * delete parent and h2 
       * 
       * @param el {element}
       */
      cleanUpAgendaView : function(el)
      {

        if (el.getElementsByTagName('li').length==0)
        {
           var h2El = Dom.getPreviousSibling(el);
           el.parentNode.removeChild(el);
           h2El.parentNode.removeChild(h2El);
        }
         
      },
      /**
       * Handler for when event is saved
       * 
       * @method onEventSaved
       * 
       * @param e {object} event object 
       */
      onEventSaved : function (e)
      {
        Alfresco.util.PopupManager.displayMessage(
        {
           text: Alfresco.util.message('message.created.success',this.name)
        });
        
        var data = YAHOO.lang.JSON.parse(e.serverResponse.responseText).event;

        var dtStartDate = Alfresco.util.fromISO8601(data.from+'T'+data.start);
        if (this.isValidDateForView(dtStartDate))
        {
          var dtEndDate = Alfresco.util.fromISO8601(data.to+'T'+data.end);
          data.duration = Alfresco.CalendarHelper.getDuration(dtStartDate,dtEndDate);
          if ((this.calendarView === Alfresco.CalendarView.VIEWTYPE_MONTH) || (this.calendarView === Alfresco.CalendarView.VIEWTYPE_WEEK)) 
          {
            var days = data.duration.match(/([0-9]+)D/);
            if (days && days[1])
            {
               data.duration = data.duration.replace(/([0-9]+)D/,++days[1]+'D');
            }
          }
          //tagname
          if (this.calendarView === Alfresco.CalendarView.VIEWTYPE_AGENDA)
          {
             data.el = 'li';  
             //tag with enclosing brackets
             data.contEl = 'div';
          }
          else {
             data.el = 'div';  
             //tag with enclosing brackets
             data.contEl = 'div';
          }
          data.hidden ='';
          data.tags = data.tags.join(' ');
          data.allday = (YAHOO.lang.isUndefined(data.allday)) ? '' : data.allday;

          data.from = data.from +'T'+data.start;
          data.to = data.to +'T'+data.end;
          //get containing date TD cell for event
          var targetEl = Dom.get('cal-'+data.from.split(':')[0]+':00');
          //render into allday section
          if(data.allday)
          {
            data.allday = 'true';
            var vEventEl = Alfresco.CalendarHelper.renderTemplate('vevent',data);
            vEventEl = this.renderAllDayEvents(vEventEl,data);
            this.calEventConfig.draggable = YAHOO.util.Dom.hasClass(vEventEl,'allday') ? false : true;
            this.calEventConfig.resizable = (Dom.hasClass(vEventEl,'allday')) ? false : true;
            var newCalEvent = new Alfresco.calendarEvent(vEventEl, this.dragGroup,YAHOO.lang.merge(this.calEventConfig,{performRender:false}));
            this.events[vEventEl.id]=newCalEvent;
          }
          else {
            if (this.calendarView === Alfresco.CalendarView.VIEWTYPE_MONTH)
            {
                targetEl = Dom.get('cal-'+data.from.split('T')[0]);
                targetEl = Dom.getElementsByClassName('day','div',targetEl)[0];
                var vEventEl = this._addEventInMonthView(targetEl,data);
                this.calEventConfig.resizable = false;
                this.calEventConfig.draggable = true;                
            
            }
            else if (this.calendarView !== Alfresco.CalendarView.VIEWTYPE_AGENDA) {
                var vEventEl = Alfresco.CalendarHelper.renderTemplate('vevent',data);
                var min = data.from.split('T')[1].split(':')[1];
                var segments  = Dom.getElementsByClassName('hourSegment','div',targetEl);
                targetEl = (parseInt(min,10)>=30) ? segments[1] : segments[0];
                YAHOO.util.Dom.setStyle(targetEl,'position','relative');
                targetEl.appendChild(vEventEl);
                this.calEventConfig.resizable = true;
                this.calEventConfig.draggable = true;
            }
            else {
               data.el = 'li';  
               //tag with enclosing brackets
               data.contEl = 'div';
               data.hidden = '';
               
               var vEventEl;
               targetEl = Dom.get('cal-'+data.from.split('T')[0]);
               // date div doesn't exist in agenda view so let's create it.
               if (!targetEl)
               {
                  var event = [];
                  var identifier = data.from.split('T')[0];
                  event[identifier] = [];
                  event[identifier].push(data);
                  var adjEl = this.getAgendaInsertNode(identifier);
                  vEventEl = this.renderAgendaDayEvents(event,Dom.getElementsByClassName('agendaview')[0],adjEl)
               } 
               else 
               {
                  targetEl = targetEl.getElementsByTagName('ul')[0];
                  vEventEl = Alfresco.CalendarHelper.renderTemplate('vevent',data);
                  targetEl.appendChild(vEventEl);
               }
               this.calEventConfig.resizable = false;
               this.calEventConfig.draggable = false;
               // this.events[vEventEl.id] = new Alfresco.calendarEvent(vEventEl, this.dragGroup,this.calEventConfig);
            }
            var id = Event.generateId(vEventEl);
            var newCalEvent = new Alfresco.calendarEvent(vEventEl, this.dragGroup,YAHOO.lang.merge(this.calEventConfig,{performRender:false}));
            this.events[id]=newCalEvent;

            newCalEvent.on('eventMoved', this.onEventMoved, newCalEvent, this);
            if ((this.calendarView === Alfresco.CalendarView.VIEWTYPE_DAY || (this.calendarView === Alfresco.CalendarView.VIEWTYPE_WEEK))) {
               this.adjustHeightByHour(vEventEl);
               this.renderMultipleEvents();
               this._fixIEBorderBleedThrough(newCalEvent.getEl());
            }
          }
        }
        
        YAHOO.Bubbling.fire("eventSaved",data);
        // Refresh the tag component
        YAHOO.Bubbling.fire("tagRefresh");
      },
      /**
       * Gets a correct DOM element to insert new events before in agenda view.
       * 
       * @param   date {Date} Date in ISO format
       */
      getAgendaInsertNode : function(date)
      {
         var day = date.split('-');
         var partialId = day[0]+'-'+day[1];
         day = day[2];
         var adjEl;
         while(day<=31)
         {
            day++;
            adjEl=Dom.get('cal-'+partialId+'-'+Alfresco.CalendarHelper.padZeros(day));
            if (adjEl)
            {
               break;
            }
         };
         return Dom.getPreviousSibling(adjEl); 
      },
      /**
       * Handler for when an event is deleted
       * 
       * @method  onEventDeleted
       *  
       */
      onEventDeleted : function ()
      {
        Alfresco.util.PopupManager.displayMessage(
        {
           text: Alfresco.util.message('message.deleted.success',this.name)
        });
        
        var id = arguments[1][1].id;
        if (this.calendarView === Alfresco.CalendarView.VIEWTYPE_MONTH)
        {
          var evt = this.events[id].getElement();
          //if allday remove multiday els too
          if (Dom.hasClass(evt,'allday'))
          {
             this.removeMultipleAllDayEvents(evt);
          }
          var el = Dom.getNextSibling(evt);
          if (Dom.hasClass(el,'hidden') ) {
              Dom.removeClass(el,'hidden');
          }
          
          if (Dom.hasClass(el,'moreEvents') ) {
              Event.purgeElement(el,true);
              el.parentNode.removeChild(el);
          }
          Event.purgeElement(evt,true);
          evt.parentNode.removeChild(evt);
        }
        else {
            var currPar = Dom.getAncestorByTagName(this.events[id].getElement(),'div');//div
            var evt = this.events[id].getElement();
            //if allday remove multiday els too
            if (Dom.hasClass(evt,'allday'))
            {
               this.removeMultipleAllDayEvents(evt);
            }
            this.events[id].deleteEvent();
        }
        if ((this.calendarView===Alfresco.CalendarView.VIEWTYPE_AGENDA))
        {
           this.cleanUpAgendaView(currPar);
        }
        Event.purgeElement(this.events[id].getEl(),true);          
        delete this.events[id];
        if ((this.calendarView  === Alfresco.CalendarView.VIEWTYPE_DAY) | (this.calendarView  === Alfresco.CalendarView.VIEWTYPE_WEEK) ) {
           this.renderMultipleEvents();
        }
        // Refresh the tag component
        YAHOO.Bubbling.fire("tagRefresh");
      },
      
      removeMultipleAllDayEvents : function(srcEl)
      {
         var els = Dom.getElementsByClassName('multipleAllDay','div');
         //remove sibling events in other days
         for (var i=0,len=els.length;i<len;i++)
         {
           var elem = els[i];
           if (elem.id.indexOf(srcEl.id)!=-1)
           {
             elem.parentNode.removeChild(elem);
           }
         }
      },
      /**
       * Handler for when today button is clicked
       * 
       * @method onTodayNav
       * 
       */
      onTodayNav : function() 
      {
        var today = new Date();
        var params = Alfresco.util.getQueryStringParameters();
        params.date = today.getFullYear() + '-'+Alfresco.CalendarHelper.padZeros(parseInt(today.getMonth(),10)+1)+'-'+Alfresco.CalendarHelper.padZeros(today.getDate());
        window.location = window.location.href.split('?')[0] + Alfresco.util.toQueryString(params);          
      },
      
      /**
       * Handler for when calendar view is changed (day|week|month button is clicked)
       * 
       * @method onViewChanged
       *  
       */
      onViewChanged : function() 
      {
        var views = [Alfresco.CalendarView.VIEWTYPE_DAY,Alfresco.CalendarView.VIEWTYPE_WEEK,Alfresco.CalendarView.VIEWTYPE_MONTH,Alfresco.CalendarView.VIEWTYPE_AGENDA];
        var params = Alfresco.util.getQueryStringParameters();
        params.view = views[arguments[1][1].activeView];
        window.location = window.location.href.split('?')[0] + Alfresco.util.toQueryString(params);
      },

      /**
       * Handler for when previous or next button is clicked
       * 
       * @method onNav 
       * @param e {object}
       *  
       */
      onNav : function(e) 
      {
        var increment = 1;
        if (e==='prevNav') {
            increment = -1;
        }
        var date = YAHOO.widget.DateMath.add(this.options.startDate,YAHOO.widget.DateMath[this.calendarView.toUpperCase()],increment);
        var params = Alfresco.util.getQueryStringParameters();
        params.date = Alfresco.util.formatDate(date,'yyyy-mm-dd');
        var newLoc = window.location.href.split('?')[0] + Alfresco.util.toQueryString(params);
        window.location = newLoc;
      },
      /**
       * Handler for when date mini calendar is selected
       * 
       * @method onNav 
       * @param e {object}
       *  
       */
      onCalSelect : function(e,args) 
      {
        var date = args[1].date;
        var params = Alfresco.util.getQueryStringParameters();
        params.date = Alfresco.util.formatDate(date,'yyyy-mm-dd');
        var newLoc = window.location.href.split('?')[0] + Alfresco.util.toQueryString(params);
        window.location = newLoc;
      },       
      /**
       * Handler for when a tag is selected
       * 
       * @method onTagSelected
       *  
       */
      onTagSelected : function (e,args)
      {
        var tagName = arguments[1][1].tagname;
        var showAllTags = false;
        //all tags
        if (tagName == Alfresco.util.message('label.all-tags','Alfresco.TagComponent'))
        {
          for (var event in this.events)
          {
            this.events[event].show();
          }
          showAllTags = true;
        }
        else {
          for (var event in this.events)
          { 
            var event = this.events[event];
            var eventTags = event.getData('category',true);
            if (YAHOO.lang.isArray(eventTags))
            {
               (Alfresco.util.arrayContains(eventTags,tagName)) ? event.show() : event.hide();
            }
            else 
            {
              event.hide();
            }
          }          
        }
        //add tag info to title
        var tagTitleEl = this.titleEl.getElementsByTagName('span');
        
        if (tagTitleEl.length>1)
        {
          this.titleEl.removeChild(tagTitleEl[0]);
        }
        if (!showAllTags)
        {
          tagTitleEl = Alfresco.CalendarHelper.renderTemplate('taggedTitle',{taggedWith:this._msg('label.tagged-with'),tag:tagName});
          this.titleEl.appendChild(tagTitleEl);          
        }

      },

      /**
       * Adjusts height of specifed event depending on its duration
       *  
       * @method adjustHeightByHour
       * @param el {object} Event element to adjust
       */
      adjustHeightByHour : function(el)
      {
        //TODO - get this from css class;
        var hourHeight = 4.75; //em
        //adjust height dependant on durations
        if (this.calendarView != Alfresco.CalendarView.VIEWTYPE_MONTH)
        {
          console.log(el.id);
          console.log(this);
          console.log(this.events);
          console.trace();
          var durationObj = hcalendar.parsers['duration'](this.events[el.id].getData('duration'));
          if (durationObj)
          {
            var height = (hourHeight*(durationObj.H||0));
            if (durationObj.M){
                height += (hourHeight*(1/(60/durationObj.M)));
            }
            if (el && height)
            {
              Dom.setStyle(el,'height',height+'em');              
            }
          }
        }  
      },

      /**
       * Displays add dialog
       * 
       * @method showAddDialog
       * @param elTarget {object} Element in which the event occured (click)
       *  
       */
      showAddDialog: function CalendarView_showAddDialog(elTarget)
      {
         var displayDate;
         //if from toolbar add event
         if (YAHOO.lang.isUndefined(elTarget))
         {
            if (this.calendarView === Alfresco.CalendarView.VIEWTYPE_MONTH)
            {
               elTarget = Dom.get('cal-'+Alfresco.util.toISO8601(this.options.startDate).split('T')[0]);
            }
            else if (this.calendarView !== Alfresco.CalendarView.VIEWTYPE_AGENDA)
            {
               elTarget = Dom.get('cal-'+Alfresco.util.toISO8601(this.options.startDate).split(':')[0]+':00');
            }
            this.currentDate = displayDate = (Alfresco.util.getQueryStringParameter('date')) ? Alfresco.util.fromISO8601(Alfresco.util.getQueryStringParameter('date')) : new Date();
         }
         else
         {
            // from cell
            this.currentDate = displayDate = this.getClickedDate(elTarget);
         }

         if (!this.eventDialog)
         {
            this.eventDialog = Alfresco.util.DialogManager.getDialog('CalendarView.addEvent');
            this.eventDialog.id = this.id+ "-addEvent";
            if (this.eventDialog.tagLibrary == undefined)
            {
               this.eventDialog.tagLibrary = new Alfresco.module.TagLibrary(this.eventDialog.id);
               this.eventDialog.tagLibrary.setOptions(
               {
                  siteId: this.options.siteId
               });
            }
         }
         var options = 
         {
            site: this.options.siteId,
            displayDate: displayDate,
            actionUrl: Alfresco.constants.PROXY_URI+ "/calendar/create",
            templateUrl: Alfresco.constants.URL_SERVICECONTEXT + "components/calendar/add-event",
            templateRequestParams:
            {
               site : this.options.siteId
            },
            doBeforeFormSubmit:
            {
               fn: function(form, obj)
               {                           
                  // Update the tags set in the form
                  this.tagLibrary.updateForm(this.id + "-form", "tags");
                  // Avoid submitting the input field used for entering tags
                  var tagInputElem = YAHOO.util.Dom.get(this.id + "-tag-input-field");
                  if (tagInputElem)
                  {
                     tagInputElem.disabled = true;
                  }
               },
               scope: this.eventDialog
            },
            doBeforeAjaxRequest:
            {
               fn: function(p_config, p_obj) 
               {
                  if (p_config.dataObj.tags)
                  {
                     p_config.dataObj.tags = p_config.dataObj.tags.join(' ');
                  }
                  return true;
               },
               scope: this.eventDialog
            },
            onSuccess:
            {
               fn: this.onEventSaved,
               scope: this
            },
            onFailure:
            {
               fn:function()
               {
                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: Alfresco.util.message('message.created.failure',this.name)
                  });
               },
               scope:this
            }
         };
         this.eventDialog.setOptions(options);
         this.eventDialog.show();
      },

      /**
       * shows edits or add dialog depending on source of event
       * 
       * @method showDialog
       * @param e {object} Event object
       * @param elTarget {object} Element in which event occured 
       *  
       */
      showDialog : function(e,elTarget)
      {
          //show create event dialog
          if (YAHOO.util.Selector.test(elTarget, 'button#addEventButton')  )
          {
            this.showAddDialog(elTarget);
          }
          //show edit dialog
          else if ( YAHOO.util.Selector.test(elTarget, 'a.summary')  )
          {
            var elPar = Dom.getAncestorByClassName(elTarget,'vevent');
            this.editEvent = this.events[elPar.id];
            var div = document.createElement('div');
            div.id = 'eventInfoPanel';
            document.body.appendChild(div);
            this.eventInfoPanel= new Alfresco.EventInfo(this.id + "");
            
            if (!this.eventInfoPanel.isShowing)
            {
               this.eventInfoPanel.setOptions(
               {
                siteId : this.options.siteId,
                eventUri : 'calendar/'+elTarget.href.split('/calendar/')[1],
                displayDate : this.currentDate,
                event  : Dom.getAncestorByClassName(elTarget,'vevent').id,
                permitToEditEvents : this.options.permitToCreateEvents
               }
               );

               this.eventInfoPanel.show({
                 uri : 'calendar/'+elTarget.href.split('/calendar/')[1],
                 name : this.editEvent.getData('summary')
               });
               
            }
          }

          YAHOO.util.Event.preventDefault(e);

      },
      
      /**
       * Returns the current date that the user clicked on
       * 
       * @method getClickedDate
       * @param el {DOMElement} the element that was clicked on
       * @returns {Date}
       */
      getClickedDate : function(el) 
      {
          if (el.nodeName.toUpperCase()!=='TD')
          {
              el = Dom.getAncestorByTagName(el,'td');
          }
          return Alfresco.util.fromISO8601(el.id.replace('cal-',''));
      },
      
      /**
       * Handler for when an event is moved(dragged). Updates DOM with new event data
       *
       * @method onEventMoved
       * @param args {object} Event arguments
       * @param calEvent {object} CalendarEvent object - the moved event
       * 
       */
      onEventMoved : function(args,calEvent) 
      {

        var calEventEl = calEvent.getEl();
        var targetEl = arguments[0].targetEl || calEventEl;

        var timeReplace = /T([0-9]{2}):([0-9]{2})/;
        var dateReplace = /^([0-9]{4})-([0-9]{2})-([0-9]{2})/;

        this.currentDate = this.getClickedDate(targetEl);

        var date = Alfresco.util.toISO8601(this.currentDate);
        var newDtStart = calEvent.getData('dtstart');
        if (date !== null)
        {
            newDtStart = newDtStart.replace(dateReplace,date.split('T')[0]);
        }
        if ((this.calendarView  === Alfresco.CalendarView.VIEWTYPE_DAY) | (this.calendarView  === Alfresco.CalendarView.VIEWTYPE_WEEK) ) {
            var hour = Alfresco.CalendarHelper.determineHourSegment(Dom.getRegion(calEventEl),targetEl);
            newDtStart = newDtStart.replace(timeReplace,'T'+hour);
        }
        var newEndDate = Alfresco.CalendarHelper.getEndDate(newDtStart,calEvent.getData('duration',true));

        calEvent.update({
            dtstart : newDtStart,
            dtend : newEndDate
        });
        if (args.dropped)
        {
          this.updateEvent(calEvent);
          if ((this.calendarView  === Alfresco.CalendarView.VIEWTYPE_DAY) | (this.calendarView  === Alfresco.CalendarView.VIEWTYPE_WEEK) ) {
            this.renderMultipleEvents();
           
            if (YAHOO.env.ua.ie)
            {
              this._fixIEBorderBleedThrough(calEventEl);
            }
          }
        }
      },
      
      /**
       * 
       * Updates event to database
       * 
       * @method updateEvent
       * @param calEvent {object} The CalendarEvent object to update 
       */
      updateEvent : function(calEvent)
      {
        
        var eventUri = Dom.getElementsByClassName('summary','a',calEvent.getEl())[0].href;
        var dts  = Alfresco.util.fromISO8601(calEvent.getData('dtstart'));
        var dte  = Alfresco.util.fromISO8601(calEvent.getData('dtend'));
        // IE's slowness sometimes means that dtend is incorrectly parsed when an
        // event is quickly resized.
        // so we must add a recheck. Interim fix. 
        if (YAHOO.lang.isNull(dte))
        {          
          var dtendData = YAHOO.util.Dom.getElementsByClassName('dtend','span',calEvent.getElement())[0];
          var endTime = dtendData.innerHTML.split(':')
          dte = new Date();
          dte.setTime(dts.getTime());
          dte.setHours(endTime[0]);
          dte.setMinutes(endTime[1]);
          calEvent.update({dtend:Alfresco.util.toISO8601(dte)});
        }
        var dataObj = {
            "site" : this.options.siteId,
            "page":"calendar",
            "from":Alfresco.util.formatDate(dts, 'yyyy/mm/dd'),
            "to":Alfresco.util.formatDate(dte, 'yyyy/mm/dd'),
            "what":calEvent.getData('summary'),
            "where":calEvent.getData('location'),
            "desc":YAHOO.lang.isNull(calEvent.getData('description')) ? '' : calEvent.getData('description'),
            "fromdate":Alfresco.util.formatDate(dts, "dddd, d mmmm yyyy"),
            "start":calEvent.getData('dtstart').split('T')[1].substring(0,5),
            "todate":Alfresco.util.formatDate(dte, "dddd, d mmmm yyyy"),
            "end":calEvent.getData('dtend').split('T')[1].substring(0,5),
            'tags':calEvent.getData('category')
        };

        Alfresco.util.Ajax.request(
         {
            method: Alfresco.util.Ajax.PUT,
            url: Alfresco.constants.PROXY_URI + 'calendar/'+eventUri.split('/calendar/')[1]+'?page=calendar',
            dataObj : dataObj,
            requestContentType : "application/json",
            responseContentType : "application/json",
            successCallback:
            {
               fn: function(){
                 Alfresco.util.PopupManager.displayMessage(
                   {
                          text: Alfresco.util.message('message.edited.success',this.name)
                   });

               },
               scope: this
            },
            failureMessage: Alfresco.util.message('message.edited.failure','Alfresco.CalendarView')
         });
      },
      
      /**
       * Updates date field in dialog when date in selected in popup calendar
       * 
       * @method onDateSelected
       * @param e {object} Event object
       * @param args {object} Event argument object
       */
      onDateSelected : function(e,args,context) {
        if (this.currPopUpCalContext) {
          //ugly
          for (var i=1;i<args[0][0].length;i++)
          {
              args[0][0][i] = Alfresco.CalendarHelper.padZeros(args[0][0][i]); 
          }
          Dom.get(this.currPopUpCalContext).value = args[0][0].join('-');
          //add one hour as default
          if (this.currPopUpCalContext==='dtend')
          {
              Dom.get(this.currPopUpCalContext+'time').value = YAHOO.widget.DateMath.add(
                  Alfresco.util.fromISO8601( Dom.get('dtstart').value+'T'+Dom.get('dtstarttime').value ),
                  YAHOO.widget.DateMath.HOUR,
                  1).format(dateFormat.masks.isoTime);
              
          }
        }
      },
      /**
       * Handler for cancelling dialog
       *  
       * @method onCancelDialog
       * 
       */
      onCancelDialog : function() {
          this.eventDialog.hide();
      },

      /**
       * Handler for showing/hiding the 'show more' overlay
       *
       * @method onShowMore
       * @param e {object} DomEvent
       * @param args {array} event arguments
       * @param elTarget {} HTML element 
       */
      onShowMore : function(e,args,elTarget) {
        
        var cell = Dom.getAncestorByTagName(elTarget,'ul');
        
        //stop events in next cell *down* from showing through
        if (YAHOO.env.ua.ie)
        {
          var tdCell = Dom.getAncestorByTagName(cell,'td');
          var row = Dom.getAncestorByTagName(cell,'tr');
          var table = Dom.getAncestorByTagName(tdCell,'table');
          var tableRows = table.rows;
          var cellIndex = tdCell.cellIndex;
          var nextRowIndex = (row.rowIndex+1);
        }
        //already showing more so show less
        if ( Dom.hasClass(elTarget,'active') )
        {
          //reshow original data
          Dom.removeClass(cell,'showing');
          Dom.removeClass(elTarget,'active');
          var hiddenItems = Dom.getElementsByClassName('tohide','li',cell);
          if ( hiddenItems )
          {
              for (var i=0,el;el=hiddenItems[i];i++) {
                  Dom.addClass(el,'hidden');
                  Dom.removeClass(el,'tohide');
              }
          }
          elTarget.innerHTML=this._msg('label.show-more');
          //reshow any hidden day
          if (YAHOO.env.ua.ie)
          {            
            if (tableRows[nextRowIndex])
            {
              var nextCell = tableRows[nextRowIndex].cells[cellIndex];
              // if (!Dom.hasClass(nextCell,'disabled'))
              {
                this.hiddenDay = Dom.getElementsByClassName('day','div',nextCell)[0];
                Dom.setStyle(this.hiddenDay,'visibility','visible');
              }              
            }
          }
          
        }
        else //show more
        {

          Dom.addClass(cell,'showing');

          Dom.addClass(elTarget,'active');
          var hiddenItems = Dom.getElementsByClassName('hidden','li',cell);
          for (var i=0,el;el=hiddenItems[i];i++) {
              Dom.removeClass(el,'hidden');
              Dom.addClass(el,'tohide')
          }
          elTarget.innerHTML=this._msg('label.show-less');
          //stop events in next cell *down* from showing through
          if (YAHOO.env.ua.ie)
          {            
            if (tableRows[nextRowIndex])
            {
              var nextCell = tableRows[nextRowIndex].cells[cellIndex];
              // if (!Dom.hasClass(nextCell,'disabled'))
              {
                this.hiddenDay = Dom.getElementsByClassName('day','div',nextCell)[0];
                Dom.setStyle(this.hiddenDay,'visibility','hidden');
              }              
            }
          }
        }
        Event.preventDefault(e);
      },

      /**
       * Handler for when an event is resized
       * 
       * @method onEventResized
       * 
       * @param e {object} Event object
       * @param o {object} Event argument 
       */
      onEventResized : function(e,o){
          this.updateEvent(o[1]);
      },
      /**
       * Render (overlapping) multiple events correctly by calulating
       * events that overlap and resizing their widths accordingly.
       * It is called every time an event is created, edited, moved or deleted.
       * 
       * @method renderMultipleEvents
       *  
       */
      renderMultipleEvents : function()
      {
        var existingEvents = YAHOO.util.Dom.getElementsByClassName('vevent','div');

        var numExistingEvents = existingEvents.length;
        var intersectEvents = [];
        
        for (var i=0;i<numExistingEvents;i++)
        {
           var elEv = existingEvents[i];
           if (!YAHOO.util.Dom.hasClass(elEv,'allday'))
           {
              YAHOO.util.Dom.setStyle(elEv,'width','99.3%');
              YAHOO.util.Dom.setStyle(elEv,'left',0+'px');           
              intersectEvents[elEv.id] = [];

              var reg = YAHOO.util.Dom.getRegion(elEv);
              for (var j=0;j<numExistingEvents;j++)
              {
                 var el = existingEvents[j];
                 if (el.id!==elEv.id)
                 {
                   var intersectRegion = reg.intersect(YAHOO.util.Dom.getRegion(el));

                   if (!YAHOO.lang.isNull(intersectRegion))
                   {
                      intersectEvents[elEv.id].push(el);
                   }
                 }
              }
              
           }
        }
        var processedElems = [];
        for (var p in intersectEvents){
         var numOfIntersectedEvents = intersectEvents[p].length;
         if (numOfIntersectedEvents>0)
         {
            var parRegion = YAHOO.util.Dom.getRegion(document.getElementById(p).parentNode);

            var newWidth= (parRegion.right-parRegion.left)/(numOfIntersectedEvents+1);
            YAHOO.util.Dom.setStyle(p,'width',newWidth+'px');
            YAHOO.util.Dom.setStyle(p,'left',0+'px');         
            processedElems.push(p);
            for (var i=0;i<numOfIntersectedEvents;i++)
            {
               var el = intersectEvents[p][i];
               YAHOO.util.Dom.setStyle(el,'width',newWidth+'px');
               YAHOO.util.Dom.setStyle(el,'left',newWidth*(i+1)+'px');
               processedElems.push(el.id);
            }
         }
        }
      },
      
      /**
       * 
       * @method onFormValidationError
       *  
       * @param e {object} Event object
       * @param args {object} Value object referencing elements that are invalid
       */
      onFormValidationError : function onFormValidationError(e,args)
      {
        var args = args[1];
        Alfresco.util.PopupManager.displayMessage(
        {
           text: args.msg
        });
      },
      
      /**
       * Shows/hides the early hours of day (midnight till 7am)
       * 
       * @method toggleEarlyTableRows
       *  
       */
      toggleEarlyTableRows : function() {

        var triggerEl = YAHOO.util.Dom.get('collapseTrigger');
        this.earlyEls = YAHOO.util.Dom.getElementsByClassName('early','tr',triggerEl.parentNode);
        var displayStyle = (YAHOO.env.ua.ie) ? 'block' : 'table-row';
        for (var i=0;i<this.earlyEls.length;i++)
        {
          var el = this.earlyEls[i];
         YAHOO.util.Dom.setStyle(el,'display',(this.isShowingEarlyRows) ? 'none' : displayStyle);
        }
        this.isShowingEarlyRows = !this.isShowingEarlyRows;
      },
      /**
       * PRIVATE FUNCTIONS
       */

      /**
       * Gets a custom message
       *
       * @method _msg
       * @param messageId {string} The messageId to retrieve
       * @return {string} The custom message
       * @private
       */
      _msg: function DL__msg(messageId)
      {
         // return messageId;
         return Alfresco.util.message.call(this, messageId, this.name, Array.prototype.slice.call(arguments).slice(1));
      },
      /** 
       *  fix table cell border bleedthrough in IE
       * 
       *  Find overlapping elements and make border transparent
       *  for div.hourSegment that contain events. This is only done for
       *  the column that the event has been moved to.
       *  
       *  @param calEventEl {Element} Calendar Event object
       */ 
      _fixIEBorderBleedThrough : function(calEventEl)
      {
        if (!YAHOO.env.ua.ie)
        {
          return;
        }

        var calTable = Dom.getAncestorByTagName(calEventEl,'table');

        var parentCell = Dom.getAncestorByTagName(calEventEl,'td');
        var cellIndex = parentCell.cellIndex;
        var evRegion = Dom.getRegion(calEventEl);
        var rows = calTable.rows;

        for (var i = 2,len=rows.length;i<len;i++)
        {
         var cell = rows[i].cells[cellIndex];
         if (cell)
         {
           var cellEvents = Dom.getElementsByClassName('vevent','div',cell);
           if (cellEvents.length>0 && cell!=parentCell)
           {
             for (var j=0,evLen=cellEvents.length;j<evLen;j++)
             {
               if (evRegion.intersect(Dom.getRegion(cellEvents[j])))
               {
                 var hourSegment = Dom.getAncestorByClassName(cellEvents[j],'hourSegment','div');
                 //ie6 doesn't do transparent borders so we remove the bottom border for that browser
                 if (YAHOO.env.ua.ie && YAHOO.env.ua.ie>6)
                 {
                   YAHOO.util.Dom.setStyle(hourSegment,'border-bottom-color','transparent');
                 }
                 else
                 {
                   YAHOO.util.Dom.setStyle(hourSegment,'border-bottom-width',0);
                 }
                 this._fixedHourSegments.push(hourSegment);
               }
             }
           }
         }
        }
        //set any div.hourSegment to position:static if it does not have any events within
        for (var i=0,len = this.hourSegments.length;i<len;i++)
        {
          var hourSegment = this.hourSegments[i];
          var hasEvent = (YAHOO.util.Dom.getElementsByClassName('vevent','div',hourSegment).length>0);
          if (!hasEvent && YAHOO.util.Dom.getStyle(hourSegment,'position')==='relative')
          {
            YAHOO.util.Dom.setStyle(hourSegment,'position','static');
          }
        }
        // tmp container for clean array of fixed elements
        var newFixedHourSegments = [];
        for (var i=0,len = this._fixedHourSegments.length;i<len;i++)
        {
          var hourSegment = this._fixedHourSegments[i];
          var hasEvent = (YAHOO.util.Dom.getElementsByClassName('vevent','div',hourSegment).length>0);
          if (!hasEvent)
          {
            YAHOO.util.Dom.setStyle(hourSegment,'border-bottom-color','#eaeaea');
            YAHOO.util.Dom.setStyle(hourSegment,'border-bottom-width','1px');                  
          }
          else
          {
            newFixedHourSegments.push(hourSegment);
          }
        }
        this._fixedHourSegments = newFixedHourSegments;
      }
   };
   Alfresco.CalendarView.VIEWTYPE_WEEK = 'week';
   Alfresco.CalendarView.VIEWTYPE_MONTH = 'month';
   Alfresco.CalendarView.VIEWTYPE_DAY = 'day';
   Alfresco.CalendarView.VIEWTYPE_AGENDA = 'agenda'; 
})();


/**
 * Alfresco.CalendarEvent
 * Represents an calendar event
 * 
 * @constructor
 * @subclass YAHOO.util.DD
 * 
 * @param id {String} Id of event element 
 * @param sGroup {String} Name of draggable group
 * @param config {object} Configuration object
 * 
 */
Alfresco.calendarEvent = function(id, sGroup, config) {
    this.el = YAHOO.util.Dom.get(id);
    if (config.draggable)
    {
      Alfresco.calendarEvent.superclass.constructor.apply(this, arguments);
      this.initDDBehaviour(id, sGroup, config);      
    }
    this.initEventData(id, (YAHOO.lang.isUndefined(config.performRender)) ? true : config.performRender ) ;
    this.initEvents();
    YAHOO.util.DDM.mode = YAHOO.util.DDM.INTERSECT; 
    
    if (config.resizable===true)
    {
      this.resize = new YAHOO.util.Resize(this.getEl(),{
          handles:['b'],
          hover:true,
          xTicks:config.resize.xTicks // half height of div.hourSegment
      });
      /**
       * Over large resize actions (or after multiple resizes), the bottom edge does quite line up correctly with the hour segments.
       * The handler divides the height of the resized element by the xTick value. This gives the number of
       * 'ticked' positions there are in the current height. This value is then divided by 2 (2 div.hourSegment per hour)
       * and finally by 5. This last number is added to the height which lines up correctly.
       * 
       * numOfTickPos = height/xTick
       * numOfHourSegments = numOfTickPos/2
       * delta = numOfHourSegments/5
       * (dividing by 5 seems to work best here)
       * (delta = numOfTickPos/10 uses one less division)
       */
      this.resize.on('resize',this.onResize,this,true);
      this.resize.on('endResize',function endResize(args){
      this.onResize(args);
      YAHOO.Bubbling.fire('eventResized',this);
     },this,true);
   }
};

YAHOO.extend(Alfresco.calendarEvent, YAHOO.util.DD, {
    /**
     *  Get event dom element
     */ 
     getElement : function() {
       return this.el;
     },
    /**
     * Initialises custom events
     * 
     * @method initEvents 
     */
    initEvents : function() {
        this.createEvent('eventMoved');
    },
    /**
     * Initialises event data by parsing the element's microformat information
     * 
     * @method initEventData
     * @param id {string} Id of element to use as source of event data
     * @param performRender {Boolean} Flag denoting whether to render data after parsing 
     */
    initEventData: function (id,performRender) {
        this.eventData = new microformatParser(
                      {
                          ufSpec : hcalendar,
                          srcNode : id
                      });
        this.eventData.parse();

        if (performRender===true)
        {
            this.eventData.render();
        }
    },
    
    /**
     * Initialise drag and drop behaviour
     * 
     * @method initDDBehaviour
     *  
     * @param id {String} Id of event element 
     * @param sGroup {String} Name of draggable group
     * @param config {object} Configuration object 
     */
    initDDBehaviour: function(id, sGroup, config) {
      if (!id) { 
          return; 
      }

      if (!(YAHOO.util.Dom.hasClass(this.getEl(),'allday')))
      {
          YAHOO.util.DDM.mode = YAHOO.util.DDM.INTERSECT; 
          var el = this.getDragEl();

          // specify that this is not currently a drop target
          this.isTarget = false;
      
      }
      this.initConstraints(YAHOO.util.Dom.getAncestorByTagName(el,'tbody'));
      if (this.config.yTick!==null)
      {
          this.setYTicks(0,this.config.yTick);            
      }
      if (this.config.xTick!==null)
      {
          this.setXTicks(0,this.config.xTick);
      }
    },
    
    /**
     * handler for startDrag event
     * 
     * @method startDrag
     * 
     */
    startDrag: function(x, y) 
    {
      YAHOO.util.Dom.setStyle(this.getEl(),'z-index','99');
    },

    /**
     * Handler for endDrag event
     * 
     * @method endDrag 
     */
    endDrag: function(e) {
      YAHOO.util.Dom.setStyle(this.getEl(),'z-index','1');
    },

    /**
     * Handler for dragDrop event
     * 
     * @method onDragDrop
     */
    onDragDrop: function(e, id) 
    {
       // get the drag and drop object that was targeted
      var oDD;
      if ("string" == typeof id) 
      {
          oDD = YAHOO.util.DDM.getDDById(id);
      }
      else 
      {
          oDD = YAHOO.util.DDM.getBestMatch(id);
      }
      //elem that dragged el was dropped on
      var targetEl = oDD.getEl(); 
      var el = this.getEl();
      var currTd;
      //allda
      
      if ( (YAHOO.util.Dom.hasClass(targetEl,'day')) )
      {
          currTd = targetEl;
          if (YAHOO.util.Dom.hasClass(el,'allday'))
          {
              targetEl.appendChild(el);
              //force a reparse as dom refs get out of sync
              this.eventData.parse(el.parentNode);
          }
          else {
            var ul = targetEl.getElementsByTagName('ul');
            var elUl = null;
            var dayHasExistingEvents = false;
            //day has no event so add ul
            if (ul.length === 0) {

                elUl = document.createElement('ul');
                elUl.className=el.parentNode.className;
                elUl = targetEl.appendChild(elUl);
            }
            // just add to existing ul
            //TODO sort ul by time
            else {
                dayHasExistingEvents = true;
                elUl = ul[0];
            }
            //if dragged onto different day
            if (elUl!==el.parentNode)
            {
              //make sure source UL shows all available events eg unhide (hidden)
              var dayEventsHidden = YAHOO.util.Dom.getElementsByClassName('hidden','li',el.parentNode);
              if (dayEventsHidden.length>0) 
              {
                  YAHOO.util.Dom.removeClass(dayEventsHidden[0],'hidden');
              }
              //must sort and not insert after showmore
              if (dayHasExistingEvents)
              {
                var dayEvents = elUl.getElementsByTagName('li');
                
                if (dayEvents.length>=5)
                {
                    if (!YAHOO.util.Dom.hasClass(elUl,'showing'))
                    {
                        YAHOO.util.Dom.addClass(el,'hidden');
                    }
                    var moreEventsTrigger = YAHOO.util.Dom.getElementsByClassName('moreEvents','li',elUl);
                    if (moreEventsTrigger.length>0)
                    {
                      elUl.insertBefore(el,moreEventsTrigger[0]);
                    }
                    else
                    {
                      elUl.appendChild(el);
                      elUl.innerHTML +='<li class="moreEvents"><a href="" class="theme-color-1">'+Alfresco.util.message('label.show-more','Alfresco.CalendarView')+'</a></li>';
                    }
                }
                else {
                    elUl.appendChild(el);
                }
              }
              else {
                  elUl.appendChild(el);
              }
              
              //force a reparse as dom refs get out of sync
              this.eventData.parse(el.parentNode);
            }
            YAHOO.util.Dom.setStyle(el,'position','static');
          }
          
      }
      if ( (YAHOO.util.Dom.hasClass(targetEl,'hourSegment')) )
      {
        currTd = YAHOO.util.Dom.getAncestorByTagName(el.parentNode,'td');

        targetEl = this.targetEl;
        if (targetEl)
        {
            var delta  =  YAHOO.util.Dom.getY(el)-YAHOO.util.Dom.getY(targetEl);
            //move el
            YAHOO.util.Dom.setStyle(targetEl,'position','relative');
            targetEl.appendChild(el);
            //reset to 0,0 origin
            YAHOO.util.DDM.moveToEl(el,targetEl);
            // if not dragged to top left pos move to delta
            if (parseInt(delta,10)>1)
            {
                YAHOO.util.Dom.setStyle(el,'top',delta+'px');
            }
        }
        this.targetEl = targetEl;
      }
      this.fireEvent('eventMoved',{targetEl:this.targetEl,dropped:true,previousTargetEl:currTd});
    },
    swap: function(el1, el2) 
    {
        var Dom = YAHOO.util.Dom;
        var pos1 = Dom.getXY(el1);
        var pos2 = Dom.getXY(el2);
        Dom.setXY(el1, pos2);
        Dom.setXY(el2, pos1);
    },

    /**
     * Handler for dragOver method
     * 
     * @method onDragOver 
     */
    onDragOver: function(e, id) 
    {
       if ("string" == typeof id) 
       {
        oDD = YAHOO.util.DDM.getDDById(id);
       }
       else 
       {
        oDD = YAHOO.util.DDM.getBestMatch(id);
       }
       //elem that dragged el was dropped on
       var targetEl = this.getBestMatch(id);
       if (targetEl)
       {
         //week and day view
         if ( (YAHOO.util.Dom.hasClass(targetEl,'hourSegment')) )
         {
             var el = this.getEl();
             //resize according to target's width and x coord
             YAHOO.util.Dom.setX(el,Math.max(0,parseInt(YAHOO.util.Dom.getX(targetEl),10)));

         }
         this.targetEl = targetEl;
         this.fireEvent('eventMoved',{targetEl:this.targetEl,dropped:false});
       }
     },
    
    /**
     * Setup co-ordinates to constrain dragging behaviour. Contrains dragging 
     * to tbody element except for first two rows if in Day or Week view
     * 
     * @method initConstraints
     * @param constraintEl {object} Element to which to constrain dragging behaviour 
     */
    initConstraints : function(constraintEl) {
      var Dom = YAHOO.util.Dom;
      if (constraintEl)
      {
        //Get the top, right, bottom and left positions
        var region = Dom.getRegion(constraintEl);
        //Get the element we are working on
        var el = this.getEl();

        //Get the xy position of it
        var xy = Dom.getXY(el);

        //Get the width and height
        var elRegion = Dom.getRegion(el);
        var width = parseInt(elRegion.right-elRegion.left, 10);
        var height = parseInt(elRegion.bottom-elRegion.top, 10);
        //must not include allday and toggle rows
        if (this.config.view===Alfresco.CalendarView.VIEWTYPE_DAY | this.config.view===Alfresco.CalendarView.VIEWTYPE_WEEK)
        {
          var trRows= constraintEl.getElementsByTagName('tr');
          var alldayRegion = Dom.getRegion(trRows[0]);
          var toggleRegion = Dom.getRegion(trRows[1]);
          region.top+= (alldayRegion.bottom-alldayRegion.top) + (toggleRegion.bottom-toggleRegion.top);
        }
        //Set the constraints based on the above calculations
        this.setXConstraint(xy[0] - region.left,region.right - xy[0] - width);
        this.setYConstraint(xy[1] - region.top, region.bottom - xy[1] - height);
      }
    },
    
    /**
     * updates event data (and DOM) using microformat structure
     * 
     * @method update
     * @param vevent {object} Value object containing event data
     */ 
    update : function(vevent) {
      this.eventData.update(vevent,true);
    },
    
    /**
     * Gets the correct target based on top left position and area
     * 
     * @method getBestMatch
     * @param els {Array} Array of ids of elements to test
     * @return targetEl {object} Best matching HTML element
     */
    getBestMatch : function(els) 
    {
        var range = 2;
        var area = 0;
        var targetEl = null;
        var top = YAHOO.util.Dom.getRegion(this.getEl()).top;
        for (var item in els)
        {
          var el = els[item];
          var overlap = el.overlap;

          if (overlap) 
          { 
            if ((overlap.top - top)<range)
            {
              if (overlap.getArea() > area) {
                   targetEl = el._domRef;
                   area = overlap.getArea();
              }
            }
          }
        }
        return targetEl;
    },
    
    /**
     * Returns specified event data. If no fieldName is passed then returns a dump
     * of all event data
     * 
     * @method getData
     * @param fieldName {String} Name of event to retrieve
     * @param parsedValue {Boolean} Flag to denote whether to return data as parsed or not 
     * 
     * @return {object} field value 
     */
    getData : function(fieldName,parsedValue)
    {
        if (fieldName)
        {
            return this.eventData.get(fieldName,parsedValue);            
        }
        else {
            return this.eventData.getAll();
        }

    },
    
    /**
     * Change specified field of event data
     * 
     * @method setData
     * @param fieldName {String} Name of field to change 
     * @param value {object} value of field to change to
     */
    setData : function(fieldName,value)
    {
        if (!YAHOO.lang.isUndefined(this.eventData[fieldName]))
        {
            this.eventData[fieldName] = value;
        }
    },
    
    /**
     * Deletes event from DOM
     * 
     * @method  deleteEvent
     *  
     */
    deleteEvent : function() {
       this.el.parentNode.removeChild(this.el);
    },
    
    /**
     * Handler for onResize method
     * 
     * @method onResize
     * @param args {object} event argument object
     */    
    onResize : function(args){
       var xTick = args.target.get('xTicks');
       this.delta = Math.ceil((args.height/xTick)/10);
       YAHOO.util.Dom.setStyle(args.target.getWrapEl(),'height',args.height+this.delta+'px');
       var hours = args.height/args.target.get('xTicks')/4;
       var mins = hours*60;
       var duration = "PT"+parseInt(hours,10)+'H'+mins%60+'M';
       var endDate = Alfresco.CalendarHelper.getEndDate(this.getData('dtstart'),hcalendar.parsers['duration'](duration));
       this.update({
          dtend : endDate,
          duration:duration
       });
    },
    /**
     * Shows event
     *
     */
    show : function()
    {
      YAHOO.util.Dom.setStyle(this.getEl(),'display','');
    },
    /**
     * Hides event
     *
     */
    hide : function()
    {
      YAHOO.util.Dom.setStyle(this.getEl(),'display','none');
    }
});

/**
 * Alfresco.CalendarHelper. Helper object consisting of useful helper methods
 * 
 * @constructor 
 */
Alfresco.CalendarHelper = ( function() {
    var Dom = YAHOO.util.Dom;
    var templates = [];
    return {
        /**
         * Calculates end date depending on specified duration, in ISO8601 format
         * 
         * @method getEndDate
         * @param dateISO {String} startDate in ISO8601 format
         * @param duration {object} Duration object
         */
        getEndDate : function(dateISO,duration) {
          var newDate = Alfresco.util.fromISO8601(dateISO);
          for (var item in duration) {
              newDate = YAHOO.widget.DateMath.add(newDate,(item==='M') ? YAHOO.widget.DateMath.MINUTE : item ,parseInt(duration[item],10));
          }
          return Alfresco.util.toISO8601(newDate).split('+')[0];//newDate.toISO8601String(5);
        },
        
        /**
         * Correctly determines which hour segment the event element is in. Returns the hour
         * 
         * @method determineHourSegment
         * @param ePos {object} Object containing XY position of element to test
         * @param el {object} Event element
         * @return {string} Hour 
         */
        determineHourSegment : function(ePos,el) {
          var r = Dom.getRegion(el);
          var y = ePos[1];
          var threshold = (r.bottom - r.top)/2;
          var inFirstHalfHour = (!Dom.getPreviousSibling(el)); // first half of hour

          var hour = Dom.getAncestorByTagName(el,'tr').getElementsByTagName('h2')[0].innerHTML;
          if (inFirstHalfHour===true) 
          {
              hour = ( y-r.top < threshold) ? hour : hour.replace(':00',':15');

          }
          else {
              hour = ( y-r.top < threshold) ? hour.replace(':00',':30') : hour.replace(':00',':45');

          }
          return hour;
      },
      
      /**
       * calculates duration based on specified start and end dates
       * 
       * 
       * @method getDuration 
       * @param dtStartDate {Date} start date
       * @param dtEndDate {Date} end date
       * @return {String} Duration in ical format eg PT2H15M
       */
      getDuration : function(dtStartDate,dtEndDate){
          var diff = dtEndDate.getTime() - dtStartDate.getTime() ;
          var dateDiff = {};
          var duration = 'P';
          var diff = new Date();
          diff.setTime(Math.abs(dtEndDate.getTime() - dtStartDate.getTime()));
          var timediff = diff.getTime();

          dateDiff[YAHOO.widget.DateMath.WEEK] = Math.floor(timediff / (1000 * 60 * 60 * 24 * 7));
          timediff -= dateDiff[YAHOO.widget.DateMath.WEEK] * (1000 * 60 * 60 * 24 * 7);

          dateDiff[YAHOO.widget.DateMath.DAY] = (Math.floor(timediff / (1000 * 60 * 60 * 24))); 
          timediff -= dateDiff[YAHOO.widget.DateMath.DAY] * (1000 * 60 * 60 * 24);

          dateDiff[YAHOO.widget.DateMath.HOUR] = Math.floor(timediff / (1000 * 60 * 60)); 
          timediff -= dateDiff[YAHOO.widget.DateMath.HOUR] * (1000 * 60 * 60);

          dateDiff[YAHOO.widget.DateMath.MINUTE] = Math.floor(timediff / (1000 * 60)); 
          timediff -= dateDiff[YAHOO.widget.DateMath.MINUTE] * (1000 * 60);

          dateDiff[YAHOO.widget.DateMath.SECOND] = Math.floor(timediff / 1000); 
          timediff -= dateDiff[YAHOO.widget.DateMath.SECOND] * 1000;

          if (dateDiff[YAHOO.widget.DateMath.WEEK]>0){
              duration+=dateDiff[YAHOO.widget.DateMath.WEEK]+YAHOO.widget.DateMath.WEEK;
          }
          if (dateDiff[YAHOO.widget.DateMath.DAY]>0){
              duration+=dateDiff[YAHOO.widget.DateMath.DAY]+YAHOO.widget.DateMath.DAY;
          }
          duration+='T';
          if (dateDiff[YAHOO.widget.DateMath.HOUR]>0){
              duration+=dateDiff[YAHOO.widget.DateMath.HOUR]+YAHOO.widget.DateMath.HOUR;
          }
          if (dateDiff[YAHOO.widget.DateMath.MINUTE]>0){
              duration+=dateDiff[YAHOO.widget.DateMath.MINUTE]+'M';
          }
          if (dateDiff[YAHOO.widget.DateMath.SECOND]>0){
              duration+=dateDiff[YAHOO.widget.DateMath.SECOND]+YAHOO.widget.DateMath.SECOND;
          }
          return duration;
      },

      /**
       * Pads specified value with zeros if value is less than 10
       * 
       * @method padZeros 
       * 
       * @param value {Object} value to pad
       * @return {String} padded value
       */
      padZeros : function(value) 
      {
          return (value<10) ? '0'+value : value;
      },
      
      /**
       * Converts a "dddd, d mmmm yyyy" string format to a Date object
       * 
       * @param strDate {String} date in "dddd, d mmmm yyyy" format
       *   
       */
      //
      getDateFromField : function(strDate) 
      {
       var arrDateValue = strDate.split(',')[1].split(' ').slice(1);
       var d = YAHOO.widget.DateMath.getDate(arrDateValue[2],Alfresco.util.arrayIndex(Alfresco.util.message("months.long").split(','),arrDateValue[1],arrDateValue[1]),arrDateValue[0]);  
       return d; 
      },
      
      /**
       * Add an template using specified name as a reference
       */
      addTemplate : function(name,template){
          templates[name] = template;
      },
      
      /**
       * Retreives specified template
       * 
       * @method getTemplate
       * @param name {string} Name of template to retrieve
       * @return {string} template
       */
      getTemplate : function(name) {
          return templates[name];
      },
      /**
       * renders template as a DOM HTML element. Element is *not* added to document
       * 
       * @param name Name of template to render
       * @param data Data to render template against
       * @return HTMLElement Newly created div
       */
      renderTemplate : function(name,data) {
        
          var el = document.createElement('div');
          if (templates[name] && el)
          {
              var el = YAHOO.lang.isString(el) ? Dom.get(el) : el;
              var template = templates[name];
              var div = document.createElement('div');
              if (data)
              {
                  template = YAHOO.lang.substitute(template,data);
              }

              div.innerHTML = template;
              el.appendChild(div.firstChild);
                                    
              return el.lastChild;
          }
      },
      
      /**
       * Checks whether start date is earlier than end date.
       * 
       * @method isValidDate
       * @param {Date} dtStartDate Start date
       * @param {Date} dtEndDate End date
       * 
       * @return {Boolean} flag denoting whether date is valid or not.
       */
      isValidDate : function(dtStartDate,dtEndDate) {
          return dtStartDate.getTime() < dtEndDate.getTime();
      }
  };
} ) (); 

Alfresco.CalendarHelper.addTemplate('vevent',
    '<{el} class="vevent {allday} {hidden} theme-bg-color-1 theme-border-2"> ' +
	'<{contEl}>' +
		'<p class="dates">' +
		'<span class="dtstart" title="{from}">{start}</span> - ' +
	 	'<span class="dtend" title="{to}">{end}</span>' +
	 	'</p>' +
	  	'<p class="description">{desc}</p>' +
	  	'<a class="summary theme-color-1" href="{uri}">{name}</a>'+
        '<span class="location">{where}</span>' +
		'<span class="duration" title="{duration}">{duration}</span>'+
		'<span class="category">{tags}</span>'+
	'</{contEl}>' +
'</{el}>');
Alfresco.CalendarHelper.addTemplate('agendaDay','<h2>{date}</h2>');
  
Alfresco.CalendarHelper.addTemplate('agendaDayItem',
  '<li class="vevent"><span>{start} - {end}</span>'+
  '<a href="{uri}" class="summary">{name}</a></li>');

Alfresco.CalendarHelper.addTemplate('createEventButton','<button id="addEventButton"><img src="{addEventUrl}" alt="{addEvent}" /></button>');
Alfresco.CalendarHelper.addTemplate('taggedTitle',"<span class=\"tagged\">{taggedWith} <span>'{tag}'</span></span>");

/**
 * Alfresco.util.DialogManager. Helper object to manage dialogs.
 * 
 * @constructor 
 */
Alfresco.util.DialogManager = ( function () {
        var dialogs = [];
        var dialogConfig = 
        { 
           width: "42em",
           displayDate : null,
           doBeforeDialogShow :
           {
                fn : function (form)
                {
                    var Dom = YAHOO.util.Dom;
                    var date = new Date();
                    // Pretty formatting
                    var dateStr = Alfresco.util.formatDate(this.options.displayDate, "dddd, d mmmm yyyy");
                    Dom.get("fd").value = dateStr;
                    Dom.get("td").value = dateStr;
                    Dom.get(this.id+"-from").value = Dom.get(this.id+"-to").value = Alfresco.util.formatDate(this.options.displayDate,'yyyy/mm/dd');
                    Dom.get(this.id + "-tag-input-field").disabled=false;
                    Dom.get(this.id + "-tag-input-field").tabIndex = 8;
                    Dom.get(this.id + "-add-tag-button").tabIndex = 9;
                    form.errorContainer=null;   
                    //hide mini-cal
                    this.dialog.hideEvent.subscribe(function() {
                     Alfresco.util.ComponentManager.findFirst('Alfresco.CalendarView').oCalendar.hide();
                    },this,true);
                },
               scope: Alfresco.util.ComponentManager.findFirst('Alfresco.CalendarView')
           },
           doSetupFormsValidation:
           {
              fn: function (form)
              {
                   var Dom = YAHOO.util.Dom;
                   var cal = Alfresco.util.ComponentManager.findFirst('Alfresco.CalendarView');
                   
                   //validate text fields
                   var validateTextRegExp = {pattern:/({|})/, match:false };
                   var textElements = [this.id+"-title", this.id+"-location", this.id+"-description"];
                   form.addValidation(textElements[0], Alfresco.forms.validation.mandatory, null, "blur");
                   form.addValidation(textElements[0], Alfresco.forms.validation.mandatory, null, "keyup");

                   for (var i=0; i < textElements.length; i++)
                   {
                      form.addValidation(textElements[i],Alfresco.forms.validation.regexMatch, validateTextRegExp, "blur");
                      form.addValidation(textElements[i],Alfresco.forms.validation.regexMatch, validateTextRegExp, "keyup");
                   }
                   //validate time fields
                   var validateTimeRegExp = {pattern:/^\d{1,2}:\d{2}/, match:true};
                   var timeElements = [this.id + "-start", this.id + "-end"];
                   for (var i=0; i < timeElements.length; i++)
                   {
                      form.addValidation(timeElements[i],Alfresco.forms.validation.regexMatch, validateTimeRegExp, "blur",cal._msg('message.invalid-time'));
                   }

                   form.addValidation(this.id + "-tag-input-field", Alfresco.module.event.validation.tags, null, "keyup");

                   this.tagLibrary.initialize(form);

                   var dateElements = ["td", "fd", this.id + "-start", this.id + "-end"];
                   for (var i=0; i < dateElements.length; i++)
                   {
                      form.addValidation(dateElements[i],this.options._onDateValidation, { "obj": this }, "blur");
                   }

                   // Setup date validation
                   form.addValidation("td", this.options._onDateValidation, { "obj": this }, "focus");
                   form.addValidation("fd", this.options._onDateValidation, { "obj": this }, "focus");

                   form.setShowSubmitStateDynamically(true, true);
                   form.setSubmitElements(this.okButton);
                   
                   /**
                    * keyboard handler for popup calendar button. Requried as YUI button's click
                    * event doesn't fire in firefox
                    */
                   var buttonKeypressHandler = function()
                   {
                     var dialogObject = Alfresco.util.DialogManager.getDialog('CalendarView.addEvent');
                     return function(e)
                     {
                       if (e.keyCode===YAHOO.util.KeyListener.KEY['ENTER'])
                       {
                         dialogObject.options.onDateSelectButton.apply(this,arguments);
                         return false;
                       }
                     }
                   }();

                  /**
                     * Button declarations that, when clicked, display
                     * the calendar date picker widget.
                     */
                    if (!this.startButton)
                    {
                       this.startButton = new YAHOO.widget.Button(
                       {
                           type: "link",
                           id: "calendarpicker",
                           label:'',
                           href:'',
                           tabindex:4,                        
                           container: this.id + "-startdate"
                       });
                    
                       this.startButton.on("click", this.options.onDateSelectButton);
                       this.startButton.on("keypress", buttonKeypressHandler);                       
                    }
                    if (!this.endButton)
                    {
                       this.endButton = new YAHOO.widget.Button(
                       {
                          type: "link",                       
                          id: "calendarendpicker",
                          label:'',
                          href:'test',
                          tabindex:6,     
                          container: this.id + "-enddate"
                       });
                    
                       this.endButton.on("click", this.options.onDateSelectButton);
                       this.endButton.on("keypress", buttonKeypressHandler);                       
                    }
                    YAHOO.Bubbling.on('formValidationError',Alfresco.util.ComponentManager.findFirst('Alfresco.CalendarView').onFormValidationError,this);
              },
               scope: Alfresco.util.ComponentManager.findFirst('Alfresco.CalendarView')
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

             YAHOO.util.Event.stopEvent(e);
             var o = Alfresco.util.ComponentManager.findFirst('Alfresco.CalendarView');
             o.oCalendarMenu = new YAHOO.widget.Overlay("calendarmenu",{
               context:[YAHOO.util.Event.getTarget(e),'tr','br']
             });
             o.oCalendarMenu.setBody("&#32;");
             o.oCalendarMenu.body.id = "calendarcontainer";
             var container = this.get('container');

             if (YAHOO.env.ua.ie)
             {
               o.oCalendarMenu.render(YAHOO.util.Dom.get(container).parentNode);
             }
             else {
               o.oCalendarMenu.render(YAHOO.util.Dom.getAncestorByClassName(YAHOO.util.Event.getTarget(e),'yui-panel','div'));
             }

             var d = Alfresco.CalendarHelper.getDateFromField((container.indexOf("enddate") > -1) ? YAHOO.util.Dom.get('td').value : YAHOO.util.Dom.get('fd').value);
             var pagedate = Alfresco.CalendarHelper.padZeros(d.getMonth()+1)+'/'+d.getFullYear();

             o.oCalendar = new YAHOO.widget.Calendar("buttoncalendar", o.oCalendarMenu.body.id,{pagedate:pagedate});
             o.oCalendar.cfg.setProperty("MONTHS_SHORT", Alfresco.util.message("months.short").split(","));
             o.oCalendar.cfg.setProperty("MONTHS_LONG", Alfresco.util.message("months.long").split(","));
             o.oCalendar.cfg.setProperty("WEEKDAYS_1CHAR", Alfresco.util.message("days.initial").split(","));
             o.oCalendar.cfg.setProperty("WEEKDAYS_SHORT", Alfresco.util.message("days.short").split(","));
             o.oCalendar.cfg.setProperty("WEEKDAYS_MEDIUM", Alfresco.util.message("days.medium").split(","));
             o.oCalendar.cfg.setProperty("WEEKDAYS_LONG", Alfresco.util.message("days.long").split(","));
             o.oCalendar.render();
             
             o.oCalendar.selectEvent.subscribe(function (type, args) {
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
                   
                   if(prettyId == "fd")
                   {
                      // If a new fromDate was selected
                      var toDate = Alfresco.CalendarHelper.getDateFromField(Dom.get("td").value);
                      if(YAHOO.widget.DateMath.before(toDate, selectedDate))
                      {                     
                         //...adjust the toDate if toDate is earlier than the new fromDate
                         var tdEl = Dom.get("td");
                         tdEl.value = Alfresco.util.formatDate(selectedDate, "dddd, d mmmm yyyy");
                         document.getElementsByName('to')[0].value = Alfresco.util.formatDate(selectedDate,'yyyy/mm/dd');
                      }
                      document.getElementsByName('from')[0].value = Alfresco.util.formatDate(selectedDate,'yyyy/mm/dd');
                   }
                   else
                   {
                     var toDate = Alfresco.CalendarHelper.getDateFromField(elem.value);
                     document.getElementsByName('to')[0].value = Alfresco.util.formatDate(toDate,'yyyy/mm/dd');
                   }
                }
                o.oCalendarMenu.hide();
                (container.indexOf("enddate") > -1) ? YAHOO.util.Dom.get('calendarendpicker-button').focus() : YAHOO.util.Dom.get('calendarpicker-button').focus();
             },o,true);
             
             o.oCalendarMenu.body.tabIndex=-1;
             o.oCalendar.oDomContainer.tabIndex=-1
             o.oCalendarMenu.body.focus();                            
             o.oCalendarMenu.show();
             o.oCalendar.show();
             return false;
         },
           _onDateValidation: function _onDateValidation(field, args, event, form, silent)
             {
                var Dom = YAHOO.util.Dom;
                var fromHours = Dom.get(args.obj.id + "-start").value.split(':');
                var toHours = Dom.get(args.obj.id + "-end").value.split(':');
                
                // Check that the end date is after the start date
                var startDate = Alfresco.CalendarHelper.getDateFromField(Dom.get("fd").value, "yyyy/mm/dd");
                startDate.setHours(fromHours[0]);
                startDate.setMinutes(fromHours[1]);
                
                var toDate = Alfresco.CalendarHelper.getDateFromField(Dom.get("td").value, "yyyy/mm/dd");
                toDate.setHours(toHours[0]);
                toDate.setMinutes(toHours[1]);

                //allday events; the date and time can be exactly the same so test for this too
                if (startDate.getTime()===toDate.getTime())
                {
                  return true;
                }
                var after = YAHOO.widget.DateMath.after(toDate, startDate);

                if (Alfresco.logger.isDebugEnabled())
                {
                   Alfresco.logger.debug("Current start date: " + startDate + " " + Dom.get(args.obj.id + "-start").value);
                   Alfresco.logger.debug("Current end date: " + toDate + " " + Dom.get(args.obj.id + "-end").value);
                   Alfresco.logger.debug("End date is after start date: " + after);
                }

                if (!after && !silent)
                {
                   form.addError(Alfresco.util.message('message.invalid-date', 'Alfresco.CalendarView'), field);
                }
                return after;
             }
        };
        return {
            registerDialog : function(dialogName)
            {

                var dialog = new Alfresco.module.SimpleDialog();
                dialog.setOptions(dialogConfig);
                dialogs[dialogName] = dialog;
                return dialogs[dialogName];
            },
            getDialog : function(dialogName)
            {
                return dialogs[dialogName] || null;
            }
        };
    } ) ();   
                
Alfresco.util.DialogManager.registerDialog('CalendarView.addEvent');





