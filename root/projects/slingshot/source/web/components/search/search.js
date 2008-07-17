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
 * Search component.
 * 
 * @namespace Alfresco
 * @class Alfresco.Search
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      Element = YAHOO.util.Element;
   
   /**
    * Search constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.Search} The new Search instance
    * @constructor
    */
   Alfresco.Search = function(htmlId)
   {
      this.name = "Alfresco.Search";
      this.id = htmlId;
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "container", "datasource", "datatable", "json", "history"], this.onComponentsLoaded, this);
      
      // Decoupled event listeners
      YAHOO.Bubbling.on("onSearch", this.onSearch, this);
   
      return this;
   }
   
   Alfresco.Search.prototype =
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
          * siteId to search in. "" if search should be cross-site
          * 
          * @property siteId
          * @type string
          */
         siteId: "",

         /**
          * ContainerId representing root container
          *
          * @property containerId
          * @type string
          * @default ""
          */
         containerId: "",
         
         /**
          * Maximum number of results displayed.
          * 
          * @property maxResults
          * @type int
          * @default 100
          */
         maxResults: 100,
         
         /**
          * Search term to use for
          * @property searchTerm
          * @type string
          * @default ""
          */
         searchTerm : ""
         
         /**
          * Current tag used for the search
          * 
          * @property searchTag
          * @type string
          */
         //searchTag: "",
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
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.Search} returns 'this' for method chaining
       */
      setOptions: function Search_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },
      
      /**
       * Set messages for this component.
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {Alfresco.Search} returns 'this' for method chaining
       */
      setMessages: function Search_setMessages(obj)
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
      onComponentsLoaded: function Search_onComponentsLoaded()
      {
         Event.onContentReady(this.id, this.onReady, this, true);
      },
   
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function Search_onReady()
      {  
         // Temporary search button
         this.widgets.searchButton = Alfresco.util.createYUIButton(this, "search-button", this.searchButtonClick);

         // DataSource definition
         var uriSearchResults = Alfresco.constants.PROXY_URI + "slingshot/search?";
         this.widgets.dataSource = new YAHOO.util.DataSource(uriSearchResults);
         this.widgets.dataSource.responseType = YAHOO.util.DataSource.TYPE_JSON;
         this.widgets.dataSource.connXhrMode = "queueRequests";
         this.widgets.dataSource.responseSchema =
         {
             resultsList: "items",
             fields: ["index", "nodeRef", "qnamePath", "type", "icon32", "name", "displayName", "title", "viewUrl", "detailsUrl", "containerUrl"]
         };
         
         /**
          * DataTable Cell Renderers
          *
          * Each cell has a custom renderer defined as a custom function. See YUI documentation for details.
          * These MUST be inline in order to have access to the Alfresco.Search class (via the "me" variable).
          */
         var me = this;
          
         /**
          * Thumbnail custom datacell formatter
          *
          * @method renderCellThumbnail
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         renderCellThumbnail = function Search_renderCellThumbnail(elCell, oRecord, oColumn, oData)
         {
            var name = oRecord.getData("name");
            var extn = name.substring(name.lastIndexOf("."));

            oColumn.width = 80;
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");
              
            // Render the cell
            // TODO: this should use the correct icon
            elCell.innerHTML = '<span class="demo-other"><img src="' + Alfresco.constants.URL_CONTEXT + oRecord.getData("icon32").substring(1) + '" alt="' + extn + '" /></span>';
         };

         /**
          * Description/detail custom datacell formatter
          *
          * @method renderCellDescription
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         renderCellDescription = function Search_renderCellDescription(elCell, oRecord, oColumn, oData)
         {
            // we currently render all results the same way
            
            var desc = "";
            // title/link to view page
            desc = '<h3 class="itemname"><a target="_blank" href="' + Alfresco.constants.PROXY_URI + oRecord.getData("viewUrl") + '">' + oRecord.getData("displayName") + '</a></h3>';
            // link to details page
            desc += '<div class="detail">';
            desc += '   <a href="' + Alfresco.constants.PROXY_URI + oRecord.getData("detailsUrl") + '">Details page</a>';
            desc += '</div>';
            desc += '<div class="detail">';
            desc += '   <a href="' + Alfresco.constants.PROXY_URI + oRecord.getData("containerUrl") + '">Container page</a>';
            desc += '</div>'
            desc += '<div class="detail"><span><b>Description:</b> ' + oRecord.getData("description") + '</span></div>';
            elCell.innerHTML = desc;
         };

         // DataTable column defintions
         var columnDefinitions = [
         {
            key: "icon32", label: "Preview", sortable: false, formatter: renderCellThumbnail, width: 80
         },
         {
            key: "fileName", label: "Description", sortable: false, formatter: renderCellDescription
         }];

         // Temporary "empty datatable" message
         YAHOO.widget.DataTable.MSG_EMPTY = this._msg("message.loading");

         // DataTable definition
         this.widgets.dataTable = new YAHOO.widget.DataTable(this.id + "-results", columnDefinitions, this.widgets.dataSource,
         {
            renderLoopSize: 32,
            initialLoad: false
         });
         
         // Custom error messages
         this._setDefaultDataTableErrors();
         
         // Override abstract function within DataTable to set custom error message
         this.widgets.dataTable.doBeforeLoadData = function Search_doBeforeLoadData(sRequest, oResponse, oPayload)
         {
            if (oResponse.error)
            {
               try
               {
                  var response = YAHOO.lang.JSON.parse(oResponse.responseText);
                  YAHOO.widget.DataTable.MSG_ERROR = response.message;
               }
               catch(e)
               {
                  me._setDefaultDataTableErrors();
               }
            }
            else if (oResponse.results)
            {
               this.renderLoopSize = oResponse.results.length >> (YAHOO.env.ua.gecko) ? 3 : 5;
            }
            // Must return true to have the "Loading..." message replaced by the error message
            return true;
         }
         
         // Enable row highlighting
         this.widgets.dataTable.subscribe("rowMouseoverEvent", this.onEventHighlightRow, this, true);
         this.widgets.dataTable.subscribe("rowMouseoutEvent", this.onEventUnhighlightRow, this, true);
         
         // Fire pathChanged event for first-time population
         /*YAHOO.Bubbling.fire("pathChanged",
         {
            doclistInitialNav: true,
            path: this.currentPath
         });*/
         
         // Set the default view filter to be "path" and the owner to be "Alfresco.DocListTree"
         /*var filterObj = YAHOO.lang.merge(
         {
            filterId: "path",
            filterOwner: "Alfresco.DocListTree"
         }, this.options.initialFilter);

         YAHOO.Bubbling.fire("filterChanged", filterObj);*/

         // Finally show the component body here to prevent UI artifacts on YUI button decoration
         Dom.setStyle(this.id + "-body", "visibility", "visible");
      },


      /**
       * YUI WIDGET EVENT HANDLERS
       * Handlers for standard events fired from YUI widgets, e.g. "click"
       */


      // TEMPORARY
      searchButtonClick: function Search_searchButtonClick(e, p_obj)
      {
         // fetch the search text field
         var textElem = Dom.get(this.id + "-search-text");
         var text = textElem.value;
          
         // send a search bubble event to load the list
         YAHOO.Bubbling.fire("onSearch",
         {
            searchTerm : text,
            maxResults : this.options.maxResults,
            site : this.options.siteId,
            container : this.options.containerId
         });
         Event.preventDefault(e);
      },


      /**
       * Custom event handler to highlight row.
       *
       * @method onEventHighlightRow
       * @param oArgs.event {HTMLEvent} Event object.
       * @param oArgs.target {HTMLElement} Target element.
       */
      onEventHighlightRow: function Search_onEventHighlightRow(oArgs)
      {         
         // Call through to get the row highlighted by YUI
         this.widgets.dataTable.onEventHighlightRow.call(this.widgets.dataTable, oArgs);
      },

      /**
       * Custom event handler to unhighlight row.
       *
       * @method onEventUnhighlightRow
       * @param oArgs.event {HTMLEvent} Event object.
       * @param oArgs.target {HTMLElement} Target element.
       */
      onEventUnhighlightRow: function Search_onEventUnhighlightRow(oArgs)
      {
         // Call through to get the row unhighlighted by YUI
         this.widgets.dataTable.onEventUnhighlightRow.call(this.widgets.dataTable, oArgs);
      },


      /**
       * BUBBLING LIBRARY EVENT HANDLERS FOR PAGE EVENTS
       * Disconnected event handlers for inter-component event notification
       */

      /**
       * Path Changed event handler
       *
       * @method onSearch
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onSearch: function Search_onSearch(layer, args)
      {
         var obj = args[1];
         if (obj !== null)
         {
            var site = (obj["site"] !== undefined) ? obj["site"] : "";
            var container = (obj["container"] !== undefined) ? obj["container"] : "";
            var searchTerm = (obj["searchTerm"] !== undefined) ? obj["searchTerm"] : "";
            
            this._performSearch(site, container, searchTerm);
         }
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
      _msg: function Search__msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, "Alfresco.Search", Array.prototype.slice.call(arguments).slice(1));
      },
      
      /**
       * Resets the YUI DataTable errors to our custom messages
       * NOTE: Scope could be YAHOO.widget.DataTable, so can't use "this"
       *
       * @method _setDefaultDataTableErrors
       */
      _setDefaultDataTableErrors: function Search__setDefaultDataTableErrors()
      {
         var msg = Alfresco.util.message;
         YAHOO.widget.DataTable.MSG_EMPTY = msg("message.empty", "Alfresco.Search");
         YAHOO.widget.DataTable.MSG_ERROR = msg("message.error", "Alfresco.Search");
      },
      
      /**
       * Updates document list by calling data webscript with current site and path
       *
       * @method _updateDocList
       * @param path {string} Path to navigate to
       */
      _performSearch: function Search__updateDocList(site, container, searchTerm)
      {
         // Reset the custom error messages
         this._setDefaultDataTableErrors();
         
         function successHandler(sRequest, oResponse, oPayload)
         {
            this.options.searchTerm = searchTerm;
            this.options.siteId = site;
            this.options.containerId = container;
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
                  YAHOO.widget.DataTable.MSG_ERROR = response.message;
                  this.widgets.dataTable.showTableMessage(response.message, YAHOO.widget.DataTable.CLASS_ERROR);
               }
               catch(e)
               {
                  this._setDefaultDataTableErrors();
               }
            }
         }
         
         this.widgets.dataSource.sendRequest(this._buildSearchParams(site, container, searchTerm),
         {
               success: successHandler,
               failure: failureHandler,
               scope: this
         });
      },

      /**
       * Build URI parameter string for doclist JSON data webscript
       *
       * @method _buildDocListParams
       * @param path {string} Path to query
       */
      _buildSearchParams: function Search__buildSearchParams(site, container, searchTerm)
      {
         var params = YAHOO.lang.substitute("site={site}&container={container}&term={term}&maxResults={maxResults}",
         {
            site: encodeURIComponent(site),
            container: encodeURIComponent(container),
            term : encodeURIComponent(searchTerm),
            maxResults : this.options.maxResults
         });

         return params;
      }
   };
})();
