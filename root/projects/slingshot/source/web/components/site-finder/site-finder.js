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
 * Site Finder component.
 * 
 * @namespace Alfresco
 * @class Alfresco.SiteFinder
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
    * SiteFinder constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.SiteFinder} The new SiteFinder instance
    * @constructor
    */
   Alfresco.SiteFinder = function(htmlId)
   {
      this.name = "Alfresco.SiteFinder";
      this.id = htmlId;
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "container", "datasource", "datatable", "json", "history"], this.onComponentsLoaded, this);
   
      return this;
   }
   
   Alfresco.SiteFinder.prototype =
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
          * Maximum number of results displayed.
          * 
          * @property maxResults
          * @type int
          * @default 100
          */
         maxResults: 100,
         
         /**
          * Flag to indicate whether private sites should be displayed
          * 
          * @property showPrivateSites
          * @type boolean
          * @default false
          */
         showPrivateSites: false,
         
         /**
          * The userid of the current user
          * 
          * @property currentUser
          * @type string
          */
         currentUser: ""
      },

      /**
       * Object container for storing YUI widget instances.
       * 
       * @property widgets
       * @type object
       */
      widgets: {},
      
      /**
       * List of Join/Leave buttons
       * 
       * @property buttons
       * @type array
       */
      buttons: [],

      /**
       * Object container for storing module instances.
       * 
       * @property modules
       * @type object
       */
      modules: {},

      /**
       * Search term used for the site search.
       * 
       * @property searchTerm
       * @type string
       */
      searchTerm: "",
      
      /**
       * List of sites the current user is a member of
       * 
       * @property memberOfSites
       * @type object
       */
      memberOfSites: {},
      
      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.Search} returns 'this' for method chaining
       */
      setOptions: function SiteFinder_setOptions(obj)
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
      setMessages: function SiteFinder_setMessages(obj)
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
      onComponentsLoaded: function SiteFinder_onComponentsLoaded()
      {
         Event.onContentReady(this.id, this.onReady, this, true);
      },
      
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function SiteFinder_onReady()
      {  
         var me = this;
         
         // build a list of sites the current user is a member of
         var config = {
            method: "GET",
            url: Alfresco.constants.PROXY_URI + "api/people/" + this.options.currentUser + "/sites",
            successCallback: 
            { 
               fn: this._processMembership, 
               scope: this 
            },
            failureMessage: Alfresco.util.message("site-finder.no-membership-detail", "Alfresco.SiteFinder")
         };
         Alfresco.util.Ajax.request(config);
         
         // DataSource definition
         var uriSearchResults = Alfresco.constants.PROXY_URI + "api/sites?";
         this.widgets.dataSource = new YAHOO.util.DataSource(uriSearchResults);
         this.widgets.dataSource.responseType = YAHOO.util.DataSource.TYPE_JSON;
         this.widgets.dataSource.connXhrMode = "queueRequests";
         this.widgets.dataSource.responseSchema =
         {
             resultsList: "items",
             fields: ["url", "sitePreset", "shortName", "title", "description", "node", "tagScope", "isPublic", "button"]
         };
         this.widgets.dataSource.doBeforeParseData = function SiteFinder_doBeforeParseData(oRequest , oFullResponse)
         {
            var updatedResponse = oFullResponse;
               
            if (oFullResponse)
            {
               var items = [];
               
               // determine list of sites to show
               if (me.searchTerm.length == 0 && me.showPrivateSites)
               {
                  // if no search term and private sites are to be shown
                  // just pass response through
                  items = oFullResponse;
               }
               else
               {
                  for (var x = 0; x < oFullResponse.length; x++)
                  {
                     var siteData = oFullResponse[x];
                     var shortName = siteData.shortName;
                     var title = siteData.title;
                     var isPublic = siteData.isPublic;
                     
                     // Filter out private sites if necessary
                     if (me.options.showPrivateSites ||
                         (!me.options.showPrivateSites && isPublic))
                     {
                        // Determine if site matches search term
                        if (shortName.toLowerCase().indexOf(me.searchTerm.toLowerCase()) != -1 ||
                            title.toLowerCase().indexOf(me.searchTerm.toLowerCase()) != -1)
                        {
                           // add site to list
                           items.push(siteData);
                        }
                     }
                  }
               }
               
               // we need to wrap the array inside a JSON object so the DataTable is happy
               updatedResponse = {
                  "items": items
               };
            }
            
            return updatedResponse;
         }
         
         // setup of the datatable.
         this._setupDataTable();
         
         // setup the button
         this.widgets.searchButton = Alfresco.util.createYUIButton(this, "button", this.doSearch);
         this.widgets.searchButton.set("disabled", true);
         
         // register the "enter" event on the search text field
         var searchIinput = Dom.get(this.id + "-term");
         new YAHOO.util.KeyListener(searchIinput, { keys:13 }, 
         {
            fn: function() 
            {
               me.doSearch()
            },
            scope:this,
            correctScope:true
         }, 
         "keydown" 
         ).enable();

         // Finally show the component body here to prevent UI artifacts on YUI button decoration
         Dom.setStyle(this.id + "-body", "visibility", "visible");
      },
      
      _processMembership: function SiteFinder__processMembership(response)
      {
         if (response.json.error === undefined)
         {
            var sites = response.json;
            for (var x = 0; x < sites.length; x++)
            {
               var site = sites[x];
               
               this.memberOfSites[site.shortName] = true;
            }
            
            // enable the search button
            this.widgets.searchButton.set("disabled", false);
         }
      },
      
      _setupDataTable: function SiteFinder_setupDataTable()
      {
         /**
          * DataTable Cell Renderers
          *
          * Each cell has a custom renderer defined as a custom function. See YUI documentation for details.
          * These MUST be inline in order to have access to the Alfresco.SiteFinder class (via the "me" variable).
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
         renderCellThumbnail = function SiteFinder_renderCellThumbnail(elCell, oRecord, oColumn, oData)
         {
            var shortName = oRecord.getData("shortName");
            var url = Alfresco.constants.URL_PAGECONTEXT + "site/" + shortName + "/dashboard";
            var siteName = oRecord.getData("title");

            // Render the icon
            elCell.innerHTML = '<a href="' + url + '"><img src="' + 
               Alfresco.constants.URL_CONTEXT + '/components/site-finder/images/site_large.gif' + 
               '" alt="' + siteName + '" title="' + siteName + '" /></a>';
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
         renderCellDescription = function SiteFinder_renderCellDescription(elCell, oRecord, oColumn, oData)
         {
            var shortName = oRecord.getData("shortName");
            var url = Alfresco.constants.URL_PAGECONTEXT + "site/" + shortName + "/dashboard";         
            var title = oRecord.getData("title");
            var desc = oRecord.getData("description");
            var isPublic = oRecord.getData("isPublic");
            
            // title/link to site page
            var details = '<h3 class="sitename"><a href="' + url + '">' + title + '</a></h3>';
            // description
            details += '<div class="sitedescription">' + desc + '</div>';
            
            elCell.innerHTML = details;
         };
         
         /**
          * Actions custom datacell formatter
          *
          * @method renderCellActions
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         renderCellActions = function InvitationList_renderCellActions(elCell, oRecord, oColumn, oData)
         {
            var isPublic = oRecord.getData("isPublic");
            if (isPublic)
            {
               var shortName = oRecord.getData("shortName");
               var action = '<span id="' + me.id + '-button-' + shortName + '"></span>';
               elCell.innerHTML = action;
               
               // create button
               var button = new YAHOO.widget.Button(
               {
                   container: me.id + '-button-' + shortName,
               });
               
               // if the user is already a member of the site show leave button
               // otherwise show join button
               if (shortName in me.memberOfSites)
               {
                  button.set("label", "Leave");
                  button.set("onclick", { fn: me.doLeave, obj: shortName, scope: me});
               }
               else
               {
                  button.set("label", "Join");
                  button.set("onclick", { fn: me.doJoin, obj: shortName, scope: me});
               }
               
               me.buttons[shortName] = { button: button };
            }
         };

         // DataTable column defintions
         var columnDefinitions = [
         {
            key: "shortName", label: "Short Name", sortable: false, formatter: renderCellThumbnail
         },
         {
            key: "title", label: "Title", sortable: false, formatter: renderCellDescription
         },
         {
            key: "description", label: "Description", formatter: renderCellActions
         }
         ];

         YAHOO.widget.DataTable.MSG_EMPTY = Alfresco.util.message("site-finder.enter-search-term", "Alfresco.SiteFinder");

         // DataTable definition
         this.widgets.dataTable = new YAHOO.widget.DataTable(this.id + "-sites", columnDefinitions, this.widgets.dataSource,
         {
            renderLoopSize: 32,
            initialLoad: false
         });
         
         // Override abstract function within DataTable to set custom error message
         this.widgets.dataTable.doBeforeLoadData = function SiteFinder_doBeforeLoadData(sRequest, oResponse, oPayload)
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
               if (oResponse.results.length == 0)
               {
                  YAHOO.widget.DataTable.MSG_EMPTY = '<span style="white-space: nowrap;">' + 
                     Alfresco.util.message("message.empty", "Alfresco.SiteFinder") + '</span>';
               }
               me.renderLoopSize = oResponse.results.length >> (YAHOO.env.ua.gecko) ? 3 : 5;
            }
            
            // Must return true to have the "Searching..." message replaced by the error message
            return true;
         }
      },
      
      /**
       * Search event handler
       *
       * @method doSearch
       */
      doSearch: function SiteFinder_doSearch()
      {
         this.searchTerm = document.getElementById(this.id + "-term").value;
         this._performSearch(this.searchTerm);
      },
      
      /**
       * Join event handler
       * 
       * @method doJoin
       * @param event {object} The event object
       * @param site {string} The shortName of the site to join
       */
      doJoin: function SiteFinder_doJoin(event, site)
      {
         var user = this.options.currentUser;
         
         // make ajax call to site service to join user
         Alfresco.util.Ajax.jsonRequest(
         {
            url: Alfresco.constants.PROXY_URI + "api/sites/" + site + "/memberships/" + user,
            method: "PUT",
            dataObj:
            {
               role: "SiteConsumer",
               person:
               {
                  userName: user,
                  url: "/alfresco/service/api/people/" + user
               },
               url: "/alfresco/service/api/sites/" + site + "/memberships/" + user
            },
            successCallback:
            {
               fn: this._joinSuccess,
               obj: site,
               scope: this
            },
            failureMessage: Alfresco.util.message("site-finder.join-failure", "Alfresco.SiteFinder", this.options.currentUser, site)
         });
      },
      
      /**
       * Callback handler used when a user is successfully added to a site
       * 
       * @method _joinSuccess
       * @param response {object}
       * @param siteData {object}
       */
      _joinSuccess: function SiteFinder__joinSuccess(response, site)
      {
         // add site to site membership list
         this.memberOfSites[site] = true;
         
         // show popup message to confirm
         Alfresco.util.PopupManager.displayMessage(
         {
            text: Alfresco.util.message("site-finder.join-success", "Alfresco.SiteFinder", this.options.currentUser, site)
         });
         
         // redo the search again to get updated info
         this.doSearch();
      },
      
      /**
       * Leave event handler
       * 
       * @method doLeave
       * @param event {object} The event object
       * @param site {string} The shortName of the site to leave
       */
      doLeave: function SiteFinder_doLeave(event, site)
      {
         var user = this.options.currentUser;
         
         // make ajax call to site service to join user
         Alfresco.util.Ajax.request(
         {
            url: Alfresco.constants.PROXY_URI + "api/sites/" + site + "/memberships/" + user,
            method: "DELETE",
            successCallback:
            {
               fn: this._leaveSuccess,
               obj: site,
               scope: this
            },
            failureMessage: Alfresco.util.message("site-finder.leave-failure", "Alfresco.SiteFinder", this.options.currentUser, site)
         });
      },
      
      /**
       * Callback handler used when a user is successfully removed from a site
       * 
       * @method _leaveSuccess
       * @param response {object}
       * @param siteData {object}
       */
      _leaveSuccess: function SiteFinder__leaveSuccess(response, site)
      {
         // remove site from site membership list
         delete this.memberOfSites[site];
         
         // show popup message to confirm
         Alfresco.util.PopupManager.displayMessage(
         {
            text: Alfresco.util.message("site-finder.leave-success", "Alfresco.SiteFinder", this.options.currentUser, site)
         });
         
         // redo the search again to get updated info
         this.doSearch();
      },
      
      /**
       * Resets the YUI DataTable errors to our custom messages
       * NOTE: Scope could be YAHOO.widget.DataTable, so can't use "this"
       *
       * @method _setDefaultDataTableErrors
       */
      _setDefaultDataTableErrors: function SiteFinder__setDefaultDataTableErrors()
      {
         var msg = Alfresco.util.message;
         YAHOO.widget.DataTable.MSG_EMPTY = msg("message.empty", "Alfresco.SiteFinder");
         YAHOO.widget.DataTable.MSG_ERROR = msg("message.error", "Alfresco.SiteFinder");
      },
      
      /**
       * Updates site list by calling data webscript with current search term
       *
       * @method _performSearch
       * @param searchTerm {string} The term to search for
       */
      _performSearch: function SiteFinder__performSearch(searchTerm)
      {
         // Reset the custom error messages
         this._setDefaultDataTableErrors();
         
         // Display loading message
         YAHOO.widget.DataTable.MSG_EMPTY = Alfresco.util.message("site-finder.searching", "Alfresco.SiteFinder");
         
         // empty results table
         this.widgets.dataTable.deleteRows(0, this.widgets.dataTable.getRecordSet().getLength());
         
         function successHandler(sRequest, oResponse, oPayload)
         {
            this.searchTerm = searchTerm;
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
         
         this.widgets.dataSource.sendRequest(this._buildSearchParams(searchTerm),
         {
               success: successHandler,
               failure: failureHandler,
               scope: this
         });
      },

      /**
       * Build URI parameter string for finding sites
       *
       * @method _buildSearchParams
       * @param searchTerm {string} Path to query
       */
      _buildSearchParams: function SiteFinder__buildSearchParams(searchTerm)
      {
         var params = YAHOO.lang.substitute("size={maxResults}",
         {
            maxResults : this.options.maxResults
         });

         return params;
      }
   };
})();
