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
 * Records results common code component.
 * 
 * @namespace Alfresco
 * @class Alfresco.RecordsResults
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
    * @return {Alfresco.RecordsResults} The new RecordsSearch instance
    * @constructor
    */
   Alfresco.RecordsResults = function(htmlId)
   {
      /* Mandatory properties */
      this.id = htmlId;
      
      return this;
   };
   
   Alfresco.RecordsResults.prototype =
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
          * siteId to search in.
          * 
          * @property siteId
          * @type string
          */
         siteId: "",

         /**
          * Maximum number of results displayed.
          * 
          * @property maxResults
          * @type int
          * @default 100
          */
         maxResults: 100
      },

      /**
       * Object container for storing YUI widget instances.
       * 
       * @property widgets
       * @type object
       */
      widgets: {},

      /**
       * Object container for storing module instances.
       * 
       * @property modules
       * @type object
       */
      modules: {},

      /**
       * Used defined full text search terms for the search or report.
       */
      searchTerms: "",

      /**
       * Number of search results.
       */
      resultsCount: 0,
      
      /**
       * True if there are more results than the ones listed in the table.
       */
      hasMoreResults: false,
      
      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.RecordsResults} returns 'this' for method chaining
       */
      setOptions: function RecordsResults_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },
      
      /**
       * Set messages for this component.
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {Alfresco.RecordsResults} returns 'this' for method chaining
       */
      setMessages: function RecordsResults_setMessages(obj)
      {
         Alfresco.util.addMessages(obj, this.name);
         return this;
      },
      
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function RecordsResults_onReady()
      {
         var me = this;
         
         // Sorting option menus
         this.widgets.sortMenu1 = new YAHOO.widget.Button(this.id + "-sort1",
         {
            type: "split",
            menu: this.id + "-sort1-menu"
         });
         //this.widgets.sortMenu1.on("click", this.onSortFilterClicked, this, true);
         this.widgets.sortMenu1.getMenu().subscribe("click", function(p_sType, p_aArgs)
         {
            var menuItem = p_aArgs[1];
            if (menuItem)
            {
               me.widgets.sortMenu1.set("label", menuItem.cfg.getProperty("text"));
               //me.onSortFilterClicked.call(me, p_aArgs[1]);
            }
         });
         
         this.widgets.sortMenu2 = new YAHOO.widget.Button(this.id + "-sort2",
         {
            type: "split",
            menu: this.id + "-sort2-menu"
         });
         //this.widgets.sortMenu2.on("click", this.onSortFilterClicked, this, true);
         this.widgets.sortMenu2.getMenu().subscribe("click", function(p_sType, p_aArgs)
         {
            var menuItem = p_aArgs[1];
            if (menuItem)
            {
               me.widgets.sortMenu2.set("label", menuItem.cfg.getProperty("text"));
               //me.onSortFilterClicked.call(me, p_aArgs[1]);
            }
         });
         
         this.widgets.sortMenu3 = new YAHOO.widget.Button(this.id + "-sort3",
         {
            type: "split",
            menu: this.id + "-sort3-menu"
         });
         //this.widgets.sortMenu3.on("click", this.onSortFilterClicked, this, true);
         this.widgets.sortMenu3.getMenu().subscribe("click", function(p_sType, p_aArgs)
         {
            var menuItem = p_aArgs[1];
            if (menuItem)
            {
               me.widgets.sortMenu3.set("label", menuItem.cfg.getProperty("text"));
               //me.onSortFilterClicked.call(me, p_aArgs[1]);
            }
         });
         
         // Column hide/show meta-data options
         var onMetadataClick = function onMetadataClick(e)
         {
            var el = Event.getTarget(e);
            var columnKey = el.id.substring(el.id.lastIndexOf('-') + 1);
            var col = me.widgets.dataTable.getColumn(columnKey)
            if (col)
            {
               if (col.hidden)
               {
                  me.widgets.dataTable.showColumn(col);
               }
               else
               {
                  me.widgets.dataTable.hideColumn(col);
               }
            }
         };
         
         var elAcceptor = function(el)
         {
            return (el.id.indexOf("-metadata-") != -1);
         };
         
         var elVisitor = function(el)
         {
            Event.on(el, "click", onMetadataClick);
         };
         
         // Apply meta-data field event handlers via element visitor pattern
         Dom.getElementsBy(elAcceptor, "input", Dom.get(this.id + "-metadata"), elVisitor);
         
         var onResultOptionsClick = function onResultOptionsClick(e)
         {
            var elToggle = Dom.get(me.id + "-options-toggle");
            var el = Dom.get(me.id + "-options");
            if (el.style.display === "none")
            {
               el.style.display = "block";
               Dom.setStyle(elToggle, "background-image", "url(" + Alfresco.constants.URL_CONTEXT + "components/images/expanded.png)");
            }
            else
            {
               el.style.display = "none";
               Dom.setStyle(elToggle, "background-image", "url(" + Alfresco.constants.URL_CONTEXT + "components/images/collapsed.png)");
            }
         };
         
         // Click handler for result options
         Event.on(Dom.get(this.id + "-options-toggle"), "click", onResultOptionsClick);
         // Initial image background css
         Dom.setStyle(this.id + "-options-toggle", "url(" + Alfresco.constants.URL_CONTEXT + "components/images/expanded.png)");
         
         // DataSource definition
         var uriSearchResults = Alfresco.constants.PROXY_URI + "slingshot/rmsearch?";
         this.widgets.dataSource = new YAHOO.util.DataSource(uriSearchResults);
         this.widgets.dataSource.responseType = YAHOO.util.DataSource.TYPE_JSON;
         this.widgets.dataSource.connXhrMode = "queueRequests";
         this.widgets.dataSource.responseSchema =
         {
             resultsList: "items",
             fields: ["nodeRef", "name", "title", "description", "modifiedOn", "modifiedByUser", "modifiedBy",
                      "createdOn", "createdByUser", "createdBy", "size", "browseUrl",
                      "properties.identifier", "properties.dateFiled", "properties.publicationDate", "properties.dateReceived",
                      "properties.originatingOrganization", "properties.mediaType", "properties.format", "properties.location",
                      "properties.supplementalMarkingList", "properties.reviewAsOf"]
         };
         
         // setup of the datatable.
         this._setupDataTable();
         
         // Finally show the component body here to prevent UI artifacts on YUI button decoration
         Dom.setStyle(this.id + "-body", "visibility", "visible");
      },
      
      _setupDataTable: function RecordsResults_setupDataTable()
      {
         /**
          * DataTable Cell Renderers
          *
          * Each cell has a custom renderer defined as a custom function. See YUI documentation for details.
          * These MUST be inline in order to have access to the Alfresco.RecordsResults class (via the "me" variable).
          */
         var me = this;
         
         /**
          * Record Icon image custom datacell formatter
          *
          * @method renderCellImage
          */
         var renderCellImage = function RecordsResults_renderCellImage(elCell, oRecord, oColumn, oData)
         {
            oColumn.width = 64;
            oColumn.height = 64;
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");
            Dom.setStyle(elCell.parentNode, "height", oColumn.height + "px");
            Dom.setStyle(elCell.parentNode, "text-align", "center");
            
            var url = me._getBrowseUrlForRecord(oRecord);
            var imageUrl = Alfresco.constants.URL_CONTEXT + 'components/documentlibrary/images/record-32.png';
            
            var name = $html(oRecord.getData("name"));
            elCell.innerHTML = '<span><a href="' + encodeURI(url) + '"><img src="' + imageUrl + '" alt="' + name + '" title="' + name + '" /></a></span>';
         };
         
         /**
          * Vital Record indicator custom datacell formatter
          *
          * @method renderCellImage
          */
         var renderCellVitalRecord = function RecordsResults_renderCellVitalRecord(elCell, oRecord, oColumn, oData)
         {
            var reviewDate = oRecord.getData("properties.reviewAsOf");
            if (reviewDate)
            {
               // found a vital record - is it due for review?
               var html;
               if (Alfresco.util.fromISO8601(reviewDate) < new Date())
               {
                  var imageUrl = Alfresco.constants.URL_CONTEXT + 'components/documentlibrary/images/warning-16.png';
                  var review = $html(me._msg("label.dueForReview"));
                  html = '<span>' + $html(me._msg("label.yes")) + '&nbsp;<img src="' + imageUrl + '" alt="' + review + '" title="' + review + '"/></span>';
               }
               else
               {
                  html = '<span>' + $html(me._msg("label.yes")) + '</span>';
               }
               elCell.innerHTML = html;
            }
         };
         
         /**
          * URI custom datacell formatter
          *
          * @method renderCellURI
          */
         var renderCellURI = function RecordsResults_renderCellURI(elCell, oRecord, oColumn, oData)
         {
            var url = me._getBrowseUrlForRecord(oRecord);
            elCell.innerHTML = '<span><a href="' + encodeURI(url) + '">' + oRecord.getData("properties.identifier") + '</a></span>';
         };
         
         /**
          * Date custom datacell formatter
          *
          * @method renderCellDate
          */
         var renderCellDate = function RecordsResults_renderCellDate(elCell, oRecord, oColumn, oData)
         {
            if (oData)
            {
               elCell.innerHTML = Alfresco.util.formatDate(Alfresco.util.fromISO8601(oData));
            }
         };
         
         /**
          * Generic HTML-safe custom datacell formatter
          */
         var renderCellSafeHTML = function renderCellSafeHTML(elCell, oRecord, oColumn, oData)
         {
            elCell.innerHTML = $html(oData);
         };
         
         /**
          * URI custom datacell sorter
          */
         var sortCellURI = function sortCellURI(a, b, desc)
         {
            // identifier format is: YYYY-NNNNNNNNNN where Y=4 digit year and N=zero padded DBID
            var sa = a.getData("properties.identifier");
            var sb = b.getData("properties.identifier");
            var numA = parseInt(sa.substring(0, 4) + sa.substring(5)),
                numB = parseInt(sb.substring(0, 4) + sb.substring(5));
            
            if (desc)
            {
               return (numA < numB ? 1 : (numA > numB ? -1 : 0));
            }
            return (numA < numB ? -1 : (numA > numB ? 1 : 0));
         };
         
         /**
          * Vital Record custom datacell sorter
          */
         var sortCellVitalRecord = function sortCellVitalRecord(a, b, desc)
         {
            var sa = null;
            var sb = null;
            if (a.getData("properties.reviewAsOf"))
            {
               sa = Alfresco.util.fromISO8601(a.getData("properties.reviewAsOf"));
            }
            if (b.getData("properties.reviewAsOf"))
            {
               sb = Alfresco.util.fromISO8601(b.getData("properties.reviewAsOf"));
            }
            if (sa === null && sb === null) return 0;
            if (desc)
            {
               if (sa === null) return -1;
               if (sb === null) return 1;
               return (sa < sb ? 1 : (sa > sb ? -1 : 0));
            }
            if (sa === null) return 1;
            if (sb === null) return -1;
            return (sa < sb ? -1 : (sa > sb ? 1 : 0));
         };
         
         // DataTable column defintions
         var columnDefinitions =
         [
            { key: "image", label: "", sortable: false, formatter: renderCellImage, width: "64px" },
            { key: "identifier", label: me._msg("label.identifier"), sortable: true, sortOptions: {sortFunction: sortCellURI}, resizeable: true, formatter: renderCellURI },
            { key: "name", label: me._msg("label.name"), field: "name", sortable: true, resizeable: true, formatter: renderCellSafeHTML },
            { key: "title", label: me._msg("label.title"), field: "title", sortable: true, resizeable: true, formatter: renderCellSafeHTML },
            { key: "originator", label: me._msg("label.originator"), field: "createdBy", sortable: true, resizeable: true, formatter: renderCellSafeHTML },
            { key: "dateFiled", label: me._msg("label.dateFiled"), field: "properties.dateFiled", sortable: true, resizeable: true, formatter: renderCellDate },
            { key: "publicationDate", label: me._msg("label.publicationDate"), field: "properties.publicationDate", sortable: true, resizeable: true, formatter: renderCellDate },
            { key: "vitalRecord", label: me._msg("label.vitalRecord"), sortable: true, sortOptions: {sortFunction: sortCellVitalRecord}, resizeable: false, formatter: renderCellVitalRecord },
            { key: "originatingOrganization", label: me._msg("label.originatingOrganization"), field: "properties.originatingOrganization", sortable: true, resizeable: true, hidden: true },
            { key: "mediaType", label: me._msg("label.mediaType"), field: "properties.mediaType", sortable: true, resizeable: true, hidden: true },
            { key: "format", label: me._msg("label.format"), field: "properties.format", sortable: true, resizeable: true, hidden: true },
            { key: "dateReceived", label: me._msg("label.dateReceived"), field: "properties.dateReceived", sortable: true, resizeable: true, formatter: renderCellDate, hidden: true },
            { key: "location", label: me._msg("label.location"), field: "properties.location", sortable: true, resizeable: true, hidden: true },
            { key: "supplementalMarkingList", label: me._msg("label.supplementalMarkingList"), field: "properties.supplementalMarkingList", sortable: true, resizeable: true, hidden: true }
         ];
         
         // DataTable definition
         this.widgets.dataTable = new YAHOO.widget.DataTable(this.id + "-results", columnDefinitions, this.widgets.dataSource,
         {
            renderLoopSize: 32,
            draggableColumns: true,
            initialLoad: false
         });
         
         // show initial message
         this._setDefaultDataTableErrors(this.widgets.dataTable);
         this.widgets.dataTable.set("MSG_EMPTY", "");
         
         // Override abstract function within DataTable to set custom error message
         this.widgets.dataTable.doBeforeLoadData = function RecordsResults_doBeforeLoadData(sRequest, oResponse, oPayload)
         {
            if (oResponse.error)
            {
               try
               {
                  var response = YAHOO.lang.JSON.parse(oResponse.responseText);
                  me.widgets.dataTable.set("MSG_ERROR", response.message);
               }
               catch(e)
               {
                  me._setDefaultDataTableErrors(me.widgets.dataTable);
               }
            }
            else if (oResponse.results)
            {
               // clear the empty error message
               me.widgets.dataTable.set("MSG_EMPTY", "");
               
               // update the results count, update hasMoreResults.
               me.hasMoreResults = (oResponse.results.length > me.options.maxResults);
               if (me.hasMoreResults)
               {
                  oResponse.results = oResponse.results.slice(0, me.options.maxResults);
               }
               me.resultsCount = oResponse.results.length;
               me.renderLoopSize = 32;
               
               if (oResponse.results.length === 0)
               {
                  var msg = Alfresco.util.message;
                  me.widgets.dataTable.set("MSG_EMPTY", me._msg("message.empty"));
               }
            }
            
            // Must return true to have the "Loading..." message replaced by the error message
            return true;
         };
      },

      /**
       * BUBBLING LIBRARY EVENT HANDLERS FOR PAGE EVENTS
       * Disconnected event handlers for inter-component event notification
       */

      /**
       * Resets the YUI DataTable errors to our custom messages
       * NOTE: Scope could be YAHOO.widget.DataTable, so can't use "this"
       *
       * @method _setDefaultDataTableErrors
       * @param dataTable {object} Instance of the DataTable
       */
      _setDefaultDataTableErrors: function RecordsResults__setDefaultDataTableErrors(dataTable)
      {
         var msg = Alfresco.util.message;
         dataTable.set("MSG_EMPTY", msg("message.empty", "Alfresco.RecordsResults"));
         dataTable.set("MSG_ERROR", msg("message.error", "Alfresco.RecordsResults"));
      },
      
      /**
       * Updates document list by calling data webscript with current site and path
       * 
       * @method _performSearch
       * @param query {string} Query to execute
       * @param terms {string} Full text search terms
       */
      _performSearch: function RecordsResults__performSearch(query, terms)
      {
         // empty results table
         this.widgets.dataTable.deleteRows(0, this.widgets.dataTable.getRecordSet().getLength());
          
         // update the ui to show that a search is on-going
         this.widgets.dataTable.set("MSG_EMPTY", "");
         this.widgets.dataTable.render();
         
         function successHandler(sRequest, oResponse, oPayload)
         {
            this.searchTerms = terms;
            this.widgets.dataTable.onDataReturnInitializeTable.call(this.widgets.dataTable, sRequest, oResponse, oPayload);
         }
         
         function failureHandler(sRequest, oResponse)
         {
            if (oResponse.status == 401)
            {
               // Our session has likely timed-out, so refresh to offer the login page
               window.location.reload();
            }
            else
            {
               try
               {
                  var response = YAHOO.lang.JSON.parse(oResponse.responseText);
                  this.widgets.dataTable.set("MSG_ERROR", response.message);
                  this.widgets.dataTable.showTableMessage(response.message, YAHOO.widget.DataTable.CLASS_ERROR);
               }
               catch(e)
               {
                  this._setDefaultDataTableErrors(this.widgets.dataTable);
                  this.widgets.dataTable.render();
               }
            }
         }
         
         this.widgets.dataSource.sendRequest(this._buildSearchParams(query, terms),
         {
            success: successHandler,
            failure: failureHandler,
            scope: this
         });
      },

      /**
       * Build URI parameter string for search JSON data webscript
       *
       * @method _buildSearchParams
       * @param query {string} Query to execute
       * @param terms {string} Full text search terms
       */
      _buildSearchParams: function RecordsResults__buildSearchParams(query, terms)
      {
         var params = YAHOO.lang.substitute("site={site}&query={query}&terms={terms}&maxResults={maxResults}",
         {
            site: encodeURIComponent(this.options.siteId),
            query : encodeURIComponent(query),
            terms : encodeURIComponent(terms),
            maxResults : this.options.maxResults + 1 // to be able to know whether we got more results
         });
         
         return params;
      },
      
      /**
       * Constructs the browse url for a given record.
       */
      _getBrowseUrlForRecord: function _getBrowseUrlForRecord(oRecord)
      {
         var url = "#";
         if (oRecord.getData("browseUrl") !== undefined)
         {
            url = Alfresco.constants.URL_PAGECONTEXT + "site/" + this.options.siteId + "/" + oRecord.getData("browseUrl");
         }
         return url;
      }
   };
})();