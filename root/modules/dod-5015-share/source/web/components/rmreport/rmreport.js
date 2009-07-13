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
 * Records Report component.
 * 
 * @namespace Alfresco
 * @class Alfresco.RecordsReport
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
       Event = YAHOO.util.Event;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML;

   /**
    * Search constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.RecordsReport} The new RecordsReport instance
    * @constructor
    */
   Alfresco.RecordsReport = function(htmlId)
   {
      /* Mandatory properties */
      this.name = "Alfresco.RecordsReport";
      
      /* Super class constructor call */
      Alfresco.RecordsReport.superclass.constructor.call(this, htmlId);
      
      /* Decoupled event listeners */
      YAHOO.Bubbling.on("filterChanged", this.onFilterChanged, this);
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "calendar", "container", "datasource", "datatable", "json"], this.onComponentsLoaded, this);
      
      return this;
   };
   
   YAHOO.extend(Alfresco.RecordsReport, Alfresco.RecordsResults,
   {
      /**
       * Current filter query id
       * 
       * @property filter
       * @type string
       */
      filter: null,
      
      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function RecordsReport_onComponentsLoaded()
      {
         Event.onContentReady(this.id, this.onReady, this, true);
      },
      
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function RecordsReport_onReady()
      {
         var me = this;
         
         // Buttons
         this.widgets.reportButton = Alfresco.util.createYUIButton(this, "create-report-button", this.onReportClick);
         this.widgets.exportButton = Alfresco.util.createYUIButton(this, "export-report-button", this.onExportClick);
         this.widgets.printButton = Alfresco.util.createYUIButton(this, "print-report-button", this.onPrintClick);
         
         // function to setup calendar localisation properties
         var calendarSetup = function calendarSetup(cal)
         {
            cal.cfg.setProperty("MONTHS_SHORT", me._msg("months.short").split(","));
            cal.cfg.setProperty("MONTHS_LONG", me._msg("months.long").split(","));
            cal.cfg.setProperty("WEEKDAYS_1CHAR", me._msg("days.initial").split(","));
            cal.cfg.setProperty("WEEKDAYS_SHORT", me._msg("days.short").split(","));
            cal.cfg.setProperty("WEEKDAYS_MEDIUM", me._msg("days.medium").split(","));
            cal.cfg.setProperty("WEEKDAYS_LONG", me._msg("days.long").split(","));
            cal.render();
         };
         
         // Generate calendar controls
         this.widgets.approvedFromCalendar = new YAHOO.widget.Calendar(this.id + "-approved-from");
         calendarSetup(this.widgets.approvedFromCalendar);
         this.widgets.approvedToCalendar = new YAHOO.widget.Calendar(this.id + "-approved-to");
         calendarSetup(this.widgets.approvedToCalendar);
         this.widgets.reviewedFromCalendar = new YAHOO.widget.Calendar(this.id + "-reviewed-from");
         calendarSetup(this.widgets.reviewedFromCalendar);
         this.widgets.reviewedToCalendar = new YAHOO.widget.Calendar(this.id + "-reviewed-to");
         calendarSetup(this.widgets.reviewedToCalendar);
         // TODO: bind events
         //this.widgets.approvedFromCalendar.selectEvent.subscribe(this.onDateSelected, this, true);
         
         // Call super class onReady() method
         Alfresco.RecordsReport.superclass.onReady.call(this);
      },
      
      /**
       * BUBBLING LIBRARY EVENT HANDLERS FOR PAGE EVENTS
       * Disconnected event handlers for inter-component event notification
       */
      
      /**
       * Create Report button click event handler
       * 
       * @method onReportClick
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onReportClick: function RecordsReport_onReportClick(e, args)
      {
         Dom.setStyle(this.id + "-summary", "visibility", "visible");
         Dom.setStyle(this.id + "-results", "visibility", "visible");
         
         var query = "";
         if (Dom.get(this.id + "-undeclared").checked === false)
         {
            query = '+ASPECT:"{http://www.alfresco.org/model/recordsmanagement/1.0}declaredRecord"';
         }
         
         // TEMP: hardcoded queries for demo - replaced with canned query index and move to repo side
         // TODO: generate query for selected report filter plus params from UI elements
         var today = new Date();
         if (this.filter === "review")
         {
            // TEMP: vital records for review query
            query += ' +ASPECT:"{http://www.alfresco.org/model/recordsmanagement/1.0}vitalRecord"' +
                     ' +@rma\\:reviewAsOf:[MIN TO ' + today.getFullYear() + '\\-' + (today.getMonth()+1) + '\\-' + today.getDate() + 'T00:00:00]';
         }
         else
         {
            // TEMP: assume disposition query for now
            query += ' +ASPECT:"{http://www.alfresco.org/model/recordsmanagement/1.0}scheduled"' +
                     ' +@rma\\:dispositionAction:' + this.filter +
                     ' +@rma\\:dispositionAsOf:[MIN TO ' + today.getFullYear() + '\\-' + (today.getMonth()+1) + '\\-' + today.getDate() + 'T00:00:00]';
         }
         
         this._performSearch(query, "");
      },
      
      /**
       * Export Report button click event handler
       * 
       * @method onExportClick
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onExportClick: function RecordsReport_onExportClick(e, args)
      {
      },
      
      /**
       * Print Report button click event handler
       * 
       * @method onPrintClick
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onPrintClick: function RecordsReport_onPrintClick(e, args)
      {
      },
      
      /**
       * Fired when the currently active filter has changed
       * @method onFilterChanged
       * @param layer {string} the event source
       * @param args {object} arguments object
       */
      onFilterChanged: function ReportFilter_onFilterChanged(layer, args)
      {
         var obj = args[1];
         if ((obj !== null) && (obj.filterId !== null))
         {
            if (obj.filterOwner === "Alfresco.ReportFilter")
            {
               // set the report title string as per the filter label
               Dom.get(this.id + "-summary-title").innerHTML = obj.filterData;
               
               // set the current filter value which will be used to pickup the correct query
               this.filter = obj.filterId;
               
               // hide the summary and results table until the report is executed
               Dom.setStyle(this.id + "-summary", "visibility", "hidden");
               Dom.setStyle(this.id + "-results", "visibility", "hidden");
            }
         }
      },
      
      /**
       * Gets a custom message
       *
       * @method _msg
       * @param messageId {string} The messageId to retrieve
       * @return {string} The custom message
       * @private
       */
      _msg: function RecordsReport__msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, "Alfresco.RecordsReport", Array.prototype.slice.call(arguments).slice(1));
      }
   });
})();