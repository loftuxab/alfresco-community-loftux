/**
 * CalendarMonthView base component.
 * Provides common functionality for all Calendar views.
 * 
 * @namespace Alfresco
 * @class Alfresco.CalendarMonthView
 */
( function() 
{
    
var Dom = YAHOO.util.Dom,
    Event = YAHOO.util.Event,
    Selector = YAHOO.util.Selector,
    fromISO8601 = Alfresco.util.fromISO8601,
    toISO8601 = Alfresco.util.toISO8601,
    dateFormat = Alfresco.thirdparty.dateFormat;
    
YAHOO.lang.augmentObject(Alfresco.CalendarView.prototype, {

   /**
    * Render events to DOM
    *  
    * @method addEvents
    * 
    */
   renderEvents : function CalendarWeekView_renderEvents(events)
   {
      
   },  
   /**
    * Retrieves events from DOM
    * 
    * @method getEvents
    *  
    */      
   getEvents: function CalendarMonthView_getEvents()
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
         [events]
      );
      // add view for events that span multiple days.
      var allDayEvents = Dom.getElementsByClassName('allday','div', Dom.get(this.id));
      for (var i=0,len=allDayEvents.length;i<len;i++)
      {
         this.renderMultipleDay(allDayEvents[i]);
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
     if (data.allday!=='allday')
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

     if (vEventEl && Dom.inDocument(vEventEl))
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
        var numAllDayEvents = Selector.query('div.allday',elUl.parentNode).length;                
        var checkIfOverThreshold = function (listEl)
        {
          var dayEvents = listEl.getElementsByTagName('li');
          var allDayEvents = Selector.query('div.allday',listEl.parentNode);                
          // var threshold = (numAllDayEvents) ? 4 - numAllDayEvents : 4;
          var targetHeight = YAHOO.util.Dom.getRegion(targetEl).height - (YAHOO.util.Dom.getRegion(dayEvents[0]).height*4);
          var numEventsHeight = ((dayEvents.length>0) ? YAHOO.util.Dom.getRegion(dayEvents[0]).height*dayEvents.length:0);
          //add alldayEvents height
          if (allDayEvents.length>0)
          {
             numEventsHeight+=YAHOO.util.Dom.getRegion(allDayEvents[0]).height*allDayEvents.length;
          }
          return (numEventsHeight>=targetHeight);
        };
        var dayEvents = elUl.getElementsByTagName('li');
        if (checkIfOverThreshold(elUl))
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
            elUl.innerHTML +='<li class="moreEvents"><a href="#" class="theme-color-1">'+this.msg('label.show-more')+'</a></li>';
          }
        }
     }
     return vEventEl;
   },
   
   /**
    * Render all day events
    * 
    * @method renderAllDayEvents
    * @param eventEl {object} reference to event element
    * @param data {object} Value object of event data
    * 
    */
   renderAllDayEvents : function CalendarMonthView_renderAllDayEvents(eventEl,data) 
   {
      Dom.generateId(eventEl);
      var targetEl = Dom.getAncestorByClassName(eventEl,'day');
      eventEl = this._addEventInMonthView(targetEl,data,eventEl);

      // add view for events that span multiple days.
      this.renderMultipleDay(eventEl,data);

      Dom.addClass(eventEl,'allday');
      Dom.setStyle(eventEl,'width','100%');
      Dom.setStyle(eventEl,'height','auto');
      Dom.setStyle(eventEl,'top','auto');
      Dom.setStyle(eventEl,'left','auto');              

      return eventEl;
   },

  /**
    * Render event that span multiple days
    * 
    * @method renderMultipleDay
    * @param eventEl {object} reference to event element
    * @param data {object} Value object of event data
    * 
    */   
   renderMultipleDay : function CalendarMonthView_renderMultipleDay(eventEl,data)
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

          var numDays = ~~(1 * (durationObj.D));
          if (durationObj.W && durationObj.W>=1)
          {
             numDays+=(~~(1 * (7*durationObj.W) ));
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
                if (dateCell)
                {
                   targetCell = Dom.getElementsByClassName('day','div',dateCell)[0];                         
                }

                var multipleAllDayEl = document.createElement('div');
                multipleAllDayEl.className='allday multipleAllDay theme-bg-color-1';
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
    * Handler for eventEdited event. Updates event in DOM in response to updated event data.
    * 
    * @method  onEventEdited
    * 
    * @param e {object} event object
    * @param o {object} new event data
    *  
    */
   onEventEdited : function CalendarMonthView_onEventEdited(e,o) 
   {
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

      var evDate = fromISO8601(data.dtstart);
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
      //valid date for view
      else {
       if(data.allday && data.allday!='false')
       {
         data.contEl='div';
         data.hidden='';
         data.el='div';
         if (data.dtstart) { // have to convert
          data = this.convertDataToTemplateData(data);
         }

         var days = data.duration.match(/([0-9]+)D/);
         if (days && days[1])
         {
            data.duration = data.duration.replace(/([0-9]+)D/,++days[1]+'D');
         }
         var currPar = Dom.getAncestorByTagName(eventEl,'div');          
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
       // not allday
       else 
       { 
         //move to correct cell
         Dom.removeClass(eventEl,'allday');
         this.removeMultipleAllDayEvents(eventEl);
         targetEl = Dom.get(id);
         targetEl = Dom.getElementsByClassName('day','div',targetEl)[0];
         eventEl = this._addEventInMonthView(targetEl,data,eventEl);
         this.calEventConfig.draggable = true;
         this.calEventConfig.resizable = false;
         this.events[eventEl.id] = new Alfresco.calendarEvent(eventEl, this.dragGroup,this.calEventConfig);
       }       
      }
      if (data.tags)
      {
       data.category=data.tags;          
      }

      this.events[eventEl.id].update(data);
      YAHOO.Bubbling.fire("eventEditedAfter");
     
   },
   
   /**
    * Handler for when event is saved
    * 
    * @method onEventSaved
    * 
    * @param e {object} event object 
    */
   onEventSaved : function CalendarMonthView_onEventSaved(e)
   {
     var data = YAHOO.lang.JSON.parse(e.serverResponse.responseText).event;

     var dtStartDate = fromISO8601(data.from+'T'+data.start);
     if (this.isValidDateForView(dtStartDate))
     {
       var dtEndDate = fromISO8601(data.to+'T'+data.end);
       data.duration = Alfresco.CalendarHelper.getDuration(dtStartDate,dtEndDate);
       var days = data.duration.match(/([0-9]+)D/);
       if (days && days[1])
       {
          data.duration = data.duration.replace(/([0-9]+)D/,++days[1]+'D');
       }

       //tagname  
       data.el = 'li';  
       //tag with enclosing brackets
       data.contEl = 'div';
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
         data.allday = 'allday';
         data.el = 'div'
         var vEventEl = Alfresco.CalendarHelper.renderTemplate('vevent',data);
         vEventEl = this.renderAllDayEvents(vEventEl,data);
         this.calEventConfig.draggable = YAHOO.util.Dom.hasClass(vEventEl,'allday') ? false : true;
         this.calEventConfig.resizable = (Dom.hasClass(vEventEl,'allday')) ? false : true;
         var newCalEvent = new Alfresco.calendarEvent(vEventEl, this.dragGroup,YAHOO.lang.merge(this.calEventConfig,{performRender:false}));
         this.events[vEventEl.id]=newCalEvent;
       }
       else 
       {
          targetEl = Dom.get('cal-'+data.from.split('T')[0]);
          targetEl = Dom.getElementsByClassName('day','div',targetEl)[0];
          var vEventEl = this._addEventInMonthView(targetEl,data);
          this.calEventConfig.resizable = false;
          this.calEventConfig.draggable = true;                

          var id = Event.generateId(vEventEl);
          var newCalEvent = new Alfresco.calendarEvent(vEventEl, this.dragGroup,YAHOO.lang.merge(this.calEventConfig,{performRender:false}));
          this.events[id]=newCalEvent;

          newCalEvent.on('eventMoved', this.onEventMoved, newCalEvent, this);
       }
     }
     YAHOO.Bubbling.fire("eventSavedAfter");
     this.displayMessage('message.created.success',this.name);
   },
   
   /**
    * Handler for when an event is deleted
    * 
    * @method  onEventDeleted
    *  
    */
   onEventDeleted : function CalendarMonthView_onEventDeleted()
   {
      var id = arguments[1][1].id;
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
      Event.purgeElement(this.events[id].getEl(),true);          
      delete this.events[id];
      YAHOO.Bubbling.fire("eventDeletedAfter");
   },
         
   /**
    * Handler for when an event is moved(dragged). Updates DOM with new event data
    *
    * @method onEventMoved
    * @param args {object} Event arguments
    * @param calEvent {object} CalendarEvent object - the moved event
    * 
    */
   onEventMoved : function CalendarMonthView_onEventMoved(args,calEvent) 
   {

     var calEventEl = calEvent.getEl();
     var targetEl = arguments[0].targetEl || calEventEl;
     var timeReplace = /T([0-9]{2}):([0-9]{2})/;
     var dateReplace = /^([0-9]{4})-([0-9]{2})-([0-9]{2})/;

     this.currentDate = this.getClickedDate(targetEl);
     var date = toISO8601(this.currentDate);
     var newDtStart = calEvent.getData('dtstart');
     if (date !== null)
     {
         newDtStart = newDtStart.replace(dateReplace,date.split('T')[0]);
     }
     var newEndDate = Alfresco.CalendarHelper.getEndDate(newDtStart,calEvent.getData('duration',true));
     var duration = calEvent.getData('duration',true);
     calEvent.update({
         dtstart : newDtStart,
         dtend : newEndDate
     });
     if (args.dropped)
     {
       YAHOO.lang.later(0, this,this.updateEvent,calEvent);
     }
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
       Dom.removeClass([cell,cell.parentNode.parentNode],'theme-bg-color-2');          
       Dom.removeClass(elTarget,'active');
       var hiddenItems = Dom.getElementsByClassName('tohide','li',cell);
       if ( hiddenItems )
       {
           for (var i=0,el;el=hiddenItems[i];i++) {
               Dom.addClass(el,'hidden');
               Dom.removeClass(el,'tohide');
           }
       }
       elTarget.innerHTML=this.msg('label.show-more');
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
       Dom.addClass([cell,cell.parentNode.parentNode],'theme-bg-color-2');
       var allDayEvents = Selector.query('div.allday',cell.parentNode);
       //if there are allday events then we must adjust for that too
       if (allDayEvents.length)
       {
          var pixelFix = (YAHOO.env.ua.ie) ? ((YAHOO.env.ua.ie<8)) ? 0 : 6 : 6;
          Dom.setStyle(cell, 'top', (allDayEvents.length*2)*(Dom.getRegion(allDayEvents[0]).height-pixelFix)+'px');
       }
       Dom.addClass(elTarget,'active');
       var hiddenItems = Dom.getElementsByClassName('hidden','li',cell);
       for (var i=0,el;el=hiddenItems[i];i++) {
           Dom.removeClass(el,'hidden');
           Dom.addClass(el,'tohide');
       }
       elTarget.innerHTML=this.msg('label.show-less');
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
   }
});
})();