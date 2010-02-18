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
      Event = YAHOO.util.Event;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML;

   /**
    * Search constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.Search} The new Search instance
    * @constructor
    */
   Alfresco.Search = function(htmlId)
   {
      /* Mandatory properties */
      this.name = "Alfresco.Search";
      this.id = htmlId;
      
      /* Initialise prototype properties */
      this.widgets = {};
      this.modules = {};
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "container", "datasource", "datatable", "json"], this.onComponentsLoaded, this);
      
      // Decoupled event listeners
      YAHOO.Bubbling.on("onSearch", this.onSearch, this);
      
      return this;
   };
   
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
          * siteId to search in, empty if search should be cross-site
          * 
          * @property siteId
          * @type string
          */
         siteId: "",
         
         /**
          * Title/name of the site
          */
         siteName: "",
         
         /**
          * Maximum number of results displayed.
          * 
          * @property maxSearchResults
          * @type int
          * @default 100
          */
         maxSearchResults: 100,
         
         /**
          * Search term to use for the initial search
          * @property initialSearchTerm
          * @type string
          * @default ""
          */
         initialSearchTerm: "",
         
         /**
          * Search tag to use for the initial search
          * @property initialSearchTag
          * @type string
          * @default ""
          */
         initialSearchTag: "",
         
         /**
          * States whether all sites should be searched.
          * This field only has an effect if siteId != ""
          * 
          * @property initialSearchAll
          * @type string
          */
         initialSearchAll: true,
         
         /**
          * Number of characters required for a search.
          *
          * @property minSearchTermLength
          * @type int
          * @default 1
          */
         minSearchTermLength: 1,
         
         /**
          * Maximum number of items to display in the results list
          *
          * @property maxSearchResults
          * @type int
          * @default 100
          */
         maxSearchResults: 100         
      },
      
      /**
       * Object container for storing YUI widget instances.
       * 
       * @property widgets
       * @type object
       */
      widgets: null,
      
      /**
       * Object container for storing module instances.
       * 
       * @property modules
       * @type object
       */
      modules: null,
      
      /**
       * Search term used for the last search.
       */
      searchTerm: "",
      
      /**
       * Search tag used for the last search.
       */
      searchTag: "",
      
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
         // Toggle scope link
         var toggleScopeLink = Dom.get(this.id + "-scope-toggle-link");
         Event.addListener(toggleScopeLink, "click", this.onToggleSearchScope, this, true);

         // DataSource definition
         var uriSearchResults = Alfresco.constants.PROXY_URI + "slingshot/search?";
         this.widgets.dataSource = new YAHOO.util.DataSource(uriSearchResults);
         this.widgets.dataSource.responseType = YAHOO.util.DataSource.TYPE_JSON;
         this.widgets.dataSource.connXhrMode = "queueRequests";
         this.widgets.dataSource.responseSchema =
         {
             resultsList: "items",
             fields: ["nodeRef", "type", "name", "displayName", "description",
                      "modifiedOn", "modifiedByUser", "modifiedBy", "size",
                      "title", "browseUrl", "site", "tags"]
         };
         
         // setup of the datatable.
         this._setupDataTable();
         
         // trigger the initial search
         YAHOO.Bubbling.fire("onSearch",
         {
            searchTerm: this.options.initialSearchTerm,
            searchTag: this.options.initialSearchTag,
            searchAll: (this.options.initialSearchAll == 'true')
         });
         
         // Hook action events
         Alfresco.util.registerDefaultActionHandler(this.id, "search-tag", "span", this);
         Alfresco.util.registerDefaultActionHandler(this.id, "search-scope-toggle", "a", this);
         
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
            oColumn.width = 100;
            oColumn.height = 100;
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");
            Dom.setStyle(elCell.parentNode, "height", oColumn.height + "px");
            Dom.setStyle(elCell.parentNode, "text-align", "center");
            
            var url = me._getBrowseUrlForRecord(oRecord);
            var imageUrl = Alfresco.constants.URL_CONTEXT + 'components/search/images/generic-result.png';
            
            // use the preview image for a document type
            switch (oRecord.getData("type"))
            {
               case "document":
                  imageUrl = Alfresco.constants.PROXY_URI + "api/node/" + oRecord.getData("nodeRef").replace(":/", "");
                  imageUrl += "/content/thumbnails/doclib?c=queue&ph=true";
                  break;
               
               case "folder":
                  imageUrl = Alfresco.constants.URL_CONTEXT + 'components/search/images/folder.png';
                  break;
               
               case "blogpost":
                  imageUrl = Alfresco.constants.URL_CONTEXT + 'components/search/images/blog-post.png';
                  break;
               
               case "forumpost":
                  imageUrl = Alfresco.constants.URL_CONTEXT + 'components/search/images/topic-post.png';
                  break;
               
               case "calendarevent":
                  imageUrl = Alfresco.constants.URL_CONTEXT + 'components/search/images/calendar-event.png';
                  break;
               
               case "wikipage":
                  imageUrl = Alfresco.constants.URL_CONTEXT + 'components/search/images/wiki-page.png';
                  break;
                  
               case "link":
                  imageUrl = Alfresco.constants.URL_CONTEXT + 'components/search/images/link.png';
                  break;
            }
            
            // Render the cell
            var name = oRecord.getData("displayName");
            var htmlName = $html(name);
            elCell.innerHTML = '<span><a href="' + encodeURI(url) + '"><img src="' + imageUrl + '" alt="' + htmlName + '" title="' + htmlName + '" /></a></span>';
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
            // apply styles
            Dom.setStyle(elCell.parentNode, "line-height", "1.5em");
            
            // we currently render all results the same way
            var site = oRecord.getData("site");
            var url = me._getBrowseUrlForRecord(oRecord);
            
            // title/link to view page
            var desc = '<h3 class="itemname"><a href="' + encodeURI(url) + '" class="theme-color-1">' + $html(oRecord.getData("displayName")) + '</a></h3>';
            
            // description (if any)
            var txt = oRecord.getData("description");
            if (txt !== undefined && txt !== "")
            {
               desc += '<div class="details">' + $html(txt) + '</div>';
            }
            
            // type information
            desc += '<div class="details">';
            var type = oRecord.getData("type");
            switch (type)
            {
               case "document":
               case "folder":
               case "blogpost":
               case "forumpost":
               case "calendarevent":
               case "wikipage":
               case "link":
                  desc += me._msg("label." + type);
                  break;

               default:
                  desc += me._msg("label.unknown");
                  break;
            }
            
            // link to the site and other meta-data details
            desc += ' ' + me._msg("message.insite");
            desc += ' <a href="' + Alfresco.constants.URL_PAGECONTEXT + 'site/' + $html(site.shortName) + '/dashboard">' + $html(site.title) + '</a>';
            if (oRecord.getData("size") !== -1)
            {
               desc += ' ' + me._msg("message.ofsize");
               desc += ' ' + Alfresco.util.formatFileSize(oRecord.getData("size"));
            }
            desc += ' ' + me._msg("message.modifiedby");
            desc += ' <a href="' + Alfresco.constants.URL_PAGECONTEXT + 'user/' + encodeURI(oRecord.getData("modifiedByUser")) + '/profile">' + $html(oRecord.getData("modifiedBy")) + '</a> ';
            desc += me._msg("message.modifiedon");
            desc += ' ' + Alfresco.util.formatDate(oRecord.getData("modifiedOn"));
            desc += '</div>';
            
            // tags (if any)
            var tags = oRecord.getData("tags");
            if (tags.length !== 0)
            {
               var i, j;
               desc += '<div class="details"><span class="tags">' + me._msg("message.tags") + ': ';
               for (i = 0, j = tags.length; i < j; i++)
               {
                   desc += '<span id="' + me.id + '-searchByTag-' + $html(tags[i]) + '"><a class="search-tag" href="#">' + $html(tags[i]) + '</a> </span>';
               }
               desc += '</span></div>';
            }
            
            elCell.innerHTML = desc;
         };

         // DataTable column defintions
         var columnDefinitions = [
         {
            key: "image", label: me._msg("message.preview"), sortable: false, formatter: renderCellThumbnail, width: 100
         },
         {
            key: "summary", label: me._msg("message.desc"), sortable: false, formatter: renderCellDescription
         }];

         // DataTable definition
         this.widgets.dataTable = new YAHOO.widget.DataTable(this.id + "-results", columnDefinitions, this.widgets.dataSource,
         {
            renderLoopSize: 32,
            initialLoad: false
         });

         // show initial message
         this._setDefaultDataTableErrors(this.widgets.dataTable);
         if (this.options.initialSearchTerm.length === 0 && this.options.initialSearchTag.length === 0)
         {
            this.widgets.dataTable.set("MSG_EMPTY", "");
         }
         
         // Override abstract function within DataTable to set custom error message
         this.widgets.dataTable.doBeforeLoadData = function Search_doBeforeLoadData(sRequest, oResponse, oPayload)
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
               me.hasMoreResults = (oResponse.results.length > me.options.maxSearchResults);
               if (me.hasMoreResults)
               {
                  oResponse.results = oResponse.results.slice(0, me.options.maxSearchResults);
               }
               me.resultsCount = oResponse.results.length;
               me.renderLoopSize = oResponse.results.length >> (YAHOO.env.ua.gecko === 1.8) ? 3 : 5;
            }
            // Must return true to have the "Loading..." message replaced by the error message
            return true;
         };
      },

      /**
       * Constructs the browse url for a given record.
       */
      _getBrowseUrlForRecord: function (oRecord)
      {
         var url = "#";
         if (oRecord.getData("browseUrl") !== undefined)
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

      /**
       * Perform a search for a given tag
       * The tag is simply handled as search term
       */
      searchByTag: function Search_searchTag(param)
      {
         this.refreshSearch(
         {
            searchTag: param,
            searchTerm: ""
         });
      },
      
      /**
       * Triggered by the search all/site only link
       */
      onToggleSearchScope: function Search_onToggleSearchScope()
      {
         var searchAll = !this.searchAll;
         this.refreshSearch(
         {
            searchAll: searchAll
         });
      },
      
      /**
       * Refresh the search page by full URL refresh
       *
       * @method refreshSearch
       * @param args {object} search args
       */
      refreshSearch: function Search_refreshSearch(args)
      {
         var searchTerm = this.searchTerm;
         if (args.searchTerm !== undefined)
         {
            searchTerm = args.searchTerm;
         }
         var searchTag = this.searchTag;
         if (args.searchTag !== undefined)
         {
            searchTag = args.searchTag;
         }
         var searchAll= this.searchAll;
         if (args.searchAll !== undefined)
         {
            searchAll = args.searchAll;
         }
         
         // redirect to the search page
         var url = Alfresco.constants.URL_CONTEXT + "page/";
         if (this.options.siteId.length !== 0)
         {
            url += "site/" + this.options.siteId + "/";
         }
         url += "search?t=" + encodeURIComponent(searchTerm);
         url += "&tag=" + encodeURIComponent(searchTag);
         url += "&a=" + searchAll;
         window.location = url;
      },

      /**
       * BUBBLING LIBRARY EVENT HANDLERS FOR PAGE EVENTS
       * Disconnected event handlers for inter-component event notification
       */

      /**
       * Execute Search event handler
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
            if (obj.searchTerm !== undefined)
            {
               searchTerm = obj.searchTerm;
            }
            var searchTag = this.searchTag;
            if (obj.searchTag !== undefined)
            {
               searchTag = obj.searchTag;
            }
            var searchAll= this.searchAll;
            if (obj.searchAll !== undefined)
            {
               searchAll = obj.searchAll;
            }
            this._performSearch(searchTerm, searchTag, searchAll);
         }
      },

      /**
       * Resets the YUI DataTable errors to our custom messages
       * NOTE: Scope could be YAHOO.widget.DataTable, so can't use "this"
       *
       * @method _setDefaultDataTableErrors
       * @param dataTable {object} Instance of the DataTable
       */
      _setDefaultDataTableErrors: function Search__setDefaultDataTableErrors(dataTable)
      {
         var msg = Alfresco.util.message;
         dataTable.set("MSG_EMPTY", msg("message.empty", "Alfresco.Search"));
         dataTable.set("MSG_ERROR", msg("message.error", "Alfresco.Search"));
      },
      
      /**
       * Updates search results list by calling data webscript with current site and query term
       *
       * @method _performSearch
       */
      _performSearch: function Search__performSearch(searchTerm, searchTag, searchAll)
      {
         var searchTerm = YAHOO.lang.trim(searchTerm);
         var searchTag = YAHOO.lang.trim(searchTag);
         if (searchTag.length === 0 && searchTerm.replace(/\*/g, "").length < this.options.minSearchTermLength)
         {
            Alfresco.util.PopupManager.displayMessage(
            {
               text: this._msg("message.minimum-length", this.options.minSearchTermLength)
            });
            return;
         }
         
         // empty results table
         this.widgets.dataTable.deleteRows(0, this.widgets.dataTable.getRecordSet().getLength());
         
         // update the ui to show that a search is on-going
         this.widgets.dataTable.set("MSG_EMPTY", "");
         this.widgets.dataTable.render();
         
         function successHandler(sRequest, oResponse, oPayload)
         {
            this.searchTerm = searchTerm;
            this.searchTag = searchTag;
            this.searchAll = searchAll;
            this.widgets.dataTable.onDataReturnInitializeTable.call(this.widgets.dataTable, sRequest, oResponse, oPayload);
            // update the result info
            this._updateSearchInfo();
            this._updateSearchScopeLink();
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
         
         this.widgets.dataSource.sendRequest(this._buildSearchParams(searchAll, searchTerm, searchTag),
         {
            success: successHandler,
            failure: failureHandler,
            scope: this
         });
      },
      
      /**
       * Updates the search info field.
       * 
       * @param isSearch {boolean} if true, a search is currently being performed, false otherwise
       */
      _updateSearchInfo: function Search__updateSearchInfo()
      {
         // update the search results field
         var searchFor;
         if (this.searchTerm.length !== 0)
         {
            searchFor = '<b>' + $html(this.searchTerm) + '</b>';
         }
         else
         {
            searchFor = '<b>' + $html(this.searchTag) + '</b>';
         }
         var searchIn = (this.searchAll ? this._msg("search.info.inallsites") : this._msg("search.info.insite", '<b>' + $html(this.options.siteName) + '</b>'));
         var resultsCount = '<b>' + this.resultsCount + '</b>';
         if (this.hasMoreResults)
         {
            resultsCount = this._msg("search.info.morethan", resultsCount);
         }
         var text = this._msg("search.info.resultinfo", searchFor, searchIn, resultsCount);
         if (this.hasMoreResults)
         {
            text += " " + this._msg("search.info.onlyshowing", this.resultsCount);
         }
         
         // set the text
         Dom.get(this.id + '-search-info').innerHTML = text;
      },
      
      /**
       * Updates the search scope link
       */
      _updateSearchScopeLink: function Search__updateSearchScopeLink()
      {
         // only proceed if there's a site to switch to
         if (this.options.siteId === "")
         {
            return;
         }
         
         // update the search results field
         var text = this.searchAll ? this._msg("search.searchsiteonly", $html(this.options.siteName)) : this._msg("search.searchall");
         
         // Update the link text
         var elem = Dom.get(this.id + '-scope-toggle-link');
         elem.innerHTML = text;
         
         // show the link if it was hidden
         var container = Dom.get(this.id + '-scope-toggle-container');
         Dom.removeClass(container, "hidden");
      },

      /**
       * Build URI parameter string for search JSON data webscript
       *
       * @method _buildSearchParams
       */
      _buildSearchParams: function Search__buildSearchParams(searchAll, searchTerm, searchTag)
      {
         var site = searchAll ? "" : this.options.siteId;
         var params = YAHOO.lang.substitute("site={site}&term={term}&tag={tag}&maxResults={maxResults}",
         {
            site: encodeURIComponent(site),
            term: encodeURIComponent(searchTerm),
            tag: encodeURIComponent(searchTag),
            maxResults: this.options.maxSearchResults + 1 // to calculate whether more results were available
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
   YAHOO.Bubbling.addDefaultAction(className, function genericDefaultAction(layer, args)
   {
      var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, ownerTagName);
      if (owner !== null)
      {
         var tmp, parts, action, param;
         
         // check that the html id matches, abort otherwise
         tmp = owner.id;
         if (tmp.indexOf(htmlId) !== 0)
         {
            return true;
         }
         tmp = tmp.substring(htmlId.length + 1);
         parts = tmp.split('-');
         if (parts.length < 1)
         {
            // stop here
            return true;
         }
         // the first entry is the handler method to call
         action = parts[0];
         if (typeof handlerObject[action] == "function")
         {
            // extract the param part of the id
            param = parts.length > 1 ? tmp.substring(action.length + 1) : null;
            handlerObject[action].call(handlerObject, param);
            args[1].stop = true;
         }
      }
      return true;
   });
};