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
         initialSearchTerm : "",
         
         
         /**
          * States whether all sites should be searched.
          * This field only has an effect if siteId != ""
          * 
          * @property searchTag
          * @type string
          */
         initialSearchAll: true
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
       * Search term used for the search.
       */
      searchTerm: "",

      /**
       * Whether the search was over all sites or just the current one
       */
      searchAll: true,
      
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
             fields: ["index", "nodeRef", "qnamePath", "type", "icon32", "name", "displayName", "title", "downloadUrl", "browseUrl", "site", "container", "tags"]
         };
         
         // setup of the datatable.
         this._setupDataTable();
         
         // trigger the initial search
         YAHOO.Bubbling.fire("onSearch",
         {
            searchTerm: this.options.initialSearchTerm,
            searchAll: (this.options.initialSearchAll == 'true')
         });

         // Hook action events
         Alfresco.util.registerDefaultActionHandler(this.id, "search-tag", "span", this);
         Alfresco.util.registerDefaultActionHandler(this.id, "search-scope-toggle", "a", this);

         // tell the header that the search component exists on this page and thus no
         // refresh is required
         YAHOO.Bubbling.fire("searchComponentExists", {});

         // Finally show the component body here to prevent UI artifacts on YUI button decoration
         Dom.setStyle(this.id + "-body", "visibility", "visible");
      },
      
      _setupDataTable: function Search_setupDataTable()
      {
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

            oColumn.width = 100;
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");
            
            var url = me._getBrowseUrlForRecord(oRecord);
            var imageUrl = Alfresco.constants.URL_CONTEXT + 'components/search/images/generic-result.png'; //  oRecord.getData("icon32").substring(1);
            
            // use the preview image for the document library
            if (oRecord.getData("type") == "file")
            {
               imageUrl = Alfresco.constants.PROXY_URI + "api/node/" + oRecord.getData("nodeRef").replace(":/", "");
               imageUrl += "/content/thumbnails/doclib?c=queue&ph=true";
            }
            
            // Render the cell
            // TODO: this should use the correct icon
            elCell.innerHTML = '<span class="demo-other"><a href="' + url + '" target="_blank"><img src="' + imageUrl + '" alt="' + extn + '" /></a></span>';
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
            var site = oRecord.getData("site");
            var url = me._getBrowseUrlForRecord(oRecord);
            var desc = "";
            // title/link to view page
            desc = '<h3 class="itemname"><a target="_blank" href="' + url + '">' + oRecord.getData("displayName") + '</a></h3>';
            // link to the site
            desc += '<div class="detail">';
            desc += '   In Site: <a  target="_blank" href="' + Alfresco.constants.URL_PAGECONTEXT + "site/" + site.shortName + '/dashboard">' + site.title + '</a>';
            desc += '</div>';
            desc += '<div class="details">';
            desc += '   Tags: ';
            var tags = oRecord.getData("tags");
            for (var x=0; x < tags.length; x++)
            {
                desc += '<span id="' + me.id + '-searchByTag-' + tags[x] + '"><a class="search-tag">' + tags[x] + '</a> </span>';
            }
            desc += '</div>';
            elCell.innerHTML = desc;
         };

         // DataTable column defintions
         var columnDefinitions = [
         {
            key: "icon32", label: "Preview", sortable: false, formatter: renderCellThumbnail, width: 100
         },
         {
            key: "fileName", label: "Description", sortable: false, formatter: renderCellDescription
         }];

         // show initial message
         this._setDefaultDataTableErrors();
         if (this.options.initialSearchTerm.length < 1)
         {
            YAHOO.widget.DataTable.MSG_EMPTY = "No search performed yet";
         }

         // DataTable definition
         this.widgets.dataTable = new YAHOO.widget.DataTable(this.id + "-results", columnDefinitions, this.widgets.dataSource,
         {
            renderLoopSize: 32,
            initialLoad: false
         });
         
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
               // clear the empty error message
               YAHOO.widget.DataTable.MSG_EMPTY = "";
               
               // update the results count, update hasMoreResults.
               me.hasMoreResults = (oResponse.results.length > me.options.maxResults);
               if (me.hasMoreResults)
               {
                  oResponse.results = oResponse.results.slice(0, me.options.maxResults);
               }
               me.resultsCount = oResponse.results.length;
               me.renderLoopSize = oResponse.results.length >> (YAHOO.env.ua.gecko) ? 3 : 5;
            }
            // Must return true to have the "Loading..." message replaced by the error message
            return true;
         }
      },

      _getBrowseUrlForRecord: function (oRecord)
      {
         var url = "#";
         if (oRecord.getData("downloadUrl") != undefined)
         {
            // Download urls always go to the repository, use the proxy context therefore
            url = Alfresco.constants.PROXY_URI + oRecord.getData("downloadUrl");
         }
         else if (oRecord.getData("browseUrl") != undefined)
         {
            // browse urls always go to a page. We assume that the url contains the page name and all
            // parameters. What we have to add is the absolute path and the site param
            // PENDING: could we somehow make use of Alfresco.constants.URI_TEMPLATES and pass
            //          the pageid and param list separately?
            var site = oRecord.getData("site");
            url = Alfresco.constants.URL_PAGECONTEXT + "site/" + site.shortName + "/" + oRecord.getData("browseUrl");
         }
         return url;
      },

      /**
       * DEFAULT ACTION EVENT HANDLERS
       * Handlers for standard events fired from YUI widgets, e.g. "click"
       */

      searchByTag: function Search_searchTag(param)
      {
         // send a search bubble event to load the list
         YAHOO.Bubbling.fire("onSearch",
         {
            searchTerm : param
         });
      },
      
      /**
       * Triggered by the search all/site only link
       */
      toggleSearchScope: function Search_switchSearchScope()
      {
         var searchAll = ! this.searchAll;
         // send a search bubble event to load the list
         YAHOO.Bubbling.fire("onSearch",
         {
            searchAll : searchAll
         });
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
            var searchTerm = this.searchTerm;
            if (obj["searchTerm"] !== undefined)
            {
               searchTerm = obj["searchTerm"];
            }
            var searchAll= this.searchAll;
            if (obj["searchAll"] !== undefined)
            {
               searchAll = obj["searchAll"];
            }
            this._performSearch(searchTerm, searchAll);
         }
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
      _performSearch: function Search__performSearch(searchTerm, searchAll)
      {
         // Reset the custom error messages
         this._setDefaultDataTableErrors();
         
         // Display loading message
         YAHOO.widget.DataTable.MSG_EMPTY = "Searching term '" + searchTerm + "'"; // this._msg("message.loading");
         //this.widgets.dataTable.render();
         
         // empty results table
         this.widgets.dataTable.deleteRows(0, this.widgets.dataTable.getRecordSet().getLength());
         
         function successHandler(sRequest, oResponse, oPayload)
         {
            this.searchTerm = searchTerm;
            this.searchAll = searchAll;
            this.widgets.dataTable.onDataReturnInitializeTable.call(this.widgets.dataTable, sRequest, oResponse, oPayload);
            // update the result info
            this._updateResultsInfo();
            this._updateSearchAllLinks();
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
         
         this.widgets.dataSource.sendRequest(this._buildSearchParams(searchAll, searchTerm),
         {
               success: successHandler,
               failure: failureHandler,
               scope: this
         });
      },
      
      _updateResultsInfo: function Search__updateSearchResultsInfo()
      {
         // update the search results field
         var searchFor = '<b>' + this.searchTerm + '</b>';
         var searchIn = (this.searchAll ? this._msg("search.info.inallsites") : this._msg("search.info.insite", '<b>' + this.options.siteId + '</b>'));
         var resultsCount = '<b>' + this.resultsCount + '</b>';
         if (this.hasMoreResults)
         {
            resultsCount = this._msg("search.info.morethan", resultsCount);
         }
         var html = this._msg("search.info.resultinfo", searchFor, searchIn, resultsCount);
         if (this.hasMoreResults)
         {
            html += " " + this._msg("search.info.onlyshowing", this.resultsCount);
         }
         
         var elems = YAHOO.util.Dom.getElementsByClassName("search-result-info");
         for (x in elems)
         {
            elems[x].innerHTML = html;
         }
      },
      
      _updateSearchAllLinks: function Search__updateSearchAllLinks()
      {
         // only proceed if there's a site to switch to
         if (this.options.siteId == "")
         {
            return;
         }
         
         // update the search results field
         var text = "";
         if (this.searchAll)
         {
            text = this._msg("search.searchsiteonly", this.options.siteId);
         }
         else
         {
            text = this._msg("search.searchall");
         }
         
         var elems = YAHOO.util.Dom.getElementsByClassName("search-scope-toggle");
         for (x in elems)
         {
            elems[x].innerHTML = text;
         }
      },

      /**
       * Build URI parameter string for doclist JSON data webscript
       *
       * @method _buildDocListParams
       * @param path {string} Path to query
       */
      _buildSearchParams: function Search__buildSearchParams(searchAll, searchTerm)
      {
         var site = searchAll ? "" : this.options.siteId;
         var container = searchAll ? "" : this.options.containerId;
         var params = YAHOO.lang.substitute("site={site}&container={container}&term={term}&maxResults={maxResults}",
         {
            site: encodeURIComponent(site),
            container: encodeURIComponent(container),
            term : encodeURIComponent(searchTerm),
            maxResults : this.options.maxResults + 1 // to be able to know whether we got more results
         });

         return params;
      },
      
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
      }

   };
})();


/**
 * Register a default action handler for a set of elements described by a common class name.
 * The common enclosing tag should hold an id of the form ${htmlid}-methodToCall-param.
 * 
 * @param htmlId the id of the component
 * @param className the classname that is common to all to be handled elements
 * @param ownerTagName the enclosing element's tag name. This element needs to have
 *        an id of type {htmlid}-methodToCall[-param], the param is optional.
 * @param handlerObject the object that handles the actions. Upon action, the methodToCall of this
 *        object is called, passing in the param as specified in the ownerTagName's id.
 */
Alfresco.util.registerDefaultActionHandler = function(htmlId, className, ownerTagName, handlerObject)
{         
   // Hook the tag events
   YAHOO.Bubbling.addDefaultAction(className,
      function genericDefaultAction(layer, args)
      {
         var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, ownerTagName);
         if (owner !== null)
         {
            // check that the html id matches, abort otherwise
            var tmp = owner.id;
            if (tmp.indexOf(htmlId) != 0)
            {
               return true;
            }
            var tmp = tmp.substring(htmlId.length + 1);
            var parts = tmp.split('-');
            if (parts.length < 1)
            {
               // stop here
               return true;
            }
            // the first entry is the handler method to call
            var action = parts[0];
            if (typeof handlerObject[action] == "function")
            {
               // extract the param part of the id
               var param = parts.length > 1 ? tmp.substring(action.length + 1) : null;
               handlerObject[action].call(handlerObject, param);
               args[1].stop = true;
            }
         }
         return true;
      }
   );
}