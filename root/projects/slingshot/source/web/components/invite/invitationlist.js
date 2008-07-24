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
 * InvitationList component.
 * 
 * @namespace Alfresco
 * @class Alfresco.InvitationList
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
    * InvitationList constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.InvitationList} The new InvitationList instance
    * @constructor
    */
   Alfresco.InvitationList = function(htmlId)
   {
      this.name = "Alfresco.InvitationList";
      this.id = htmlId;
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "container", "datasource", "datatable", "json", "history"], this.onComponentsLoaded, this);
      
      // Decoupled event listeners
      //YAHOO.Bubbling.on("onAddInvite", this.onAddInvite, this);
   
      return this;
   }
   
   Alfresco.InvitationList.prototype =
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
          * siteId to InvitationList in. "" if InvitationList should be cross-site
          * 
          * @property siteId
          * @type string
          */
         siteId: ""
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
       * InvitationList term used for the InvitationList.
       */
      InvitationListTerm: "",

      /**
       * Whether the InvitationList was over all sites or just the current one
       */
      InvitationListAll: true,
      
      /**
       * Number of InvitationList results.
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
       * @return {Alfresco.InvitationList} returns 'this' for method chaining
       */
      setOptions: function InvitationList_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },
      
      /**
       * Set messages for this component.
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {Alfresco.InvitationList} returns 'this' for method chaining
       */
      setMessages: function InvitationList_setMessages(obj)
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
      onComponentsLoaded: function InvitationList_onComponentsLoaded()
      {
         //Event.onContentReady(this.id, this.onReady, this, true);
      },
   
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function InvitationList_onReady()
      {  
         // Temporary InvitationList button
         this.widgets.InvitationListButton = Alfresco.util.createYUIButton(this, "InvitationList-button", this.InvitationListButtonClick);

         // DataSource definition
         var uriInvitationListResults = Alfresco.constants.PROXY_URI + "slingshot/InvitationList?";
         this.widgets.dataSource = new YAHOO.util.DataSource(uriInvitationListResults);
         this.widgets.dataSource.responseType = YAHOO.util.DataSource.TYPE_JSON;
         this.widgets.dataSource.connXhrMode = "queueRequests";
         this.widgets.dataSource.responseSchema =
         {
             resultsList: "items",
             fields: ["index", "nodeRef", "qnamePath", "type", "icon32", "name", "displayName", "title", "downloadUrl", "browseUrl", "site", "container", "tags"]
         };
         
         // setup of the datatable.
         this._setupDataTable();
         
         // trigger the initial InvitationList
         YAHOO.Bubbling.fire("onInvitationList",
         {
            InvitationListTerm: this.options.initialInvitationListTerm,
            InvitationListAll: (this.options.initialInvitationListAll == 'true')
         });

         // Hook action events
         Alfresco.util.registerDefaultActionHandler(this.id, "InvitationList-tag", "span", this);
         Alfresco.util.registerDefaultActionHandler(this.id, "InvitationList-scope-toggle", "a", this);

         // tell the header that the InvitationList component exists on this page and thus no
         // refresh is required
         YAHOO.Bubbling.fire("InvitationListComponentExists", {});

         // Finally show the component body here to prevent UI artifacts on YUI button decoration
         Dom.setStyle(this.id + "-body", "visibility", "visible");
      },
      
      _setupDataTable: function InvitationList_setupDataTable()
      {
         /**
          * DataTable Cell Renderers
          *
          * Each cell has a custom renderer defined as a custom function. See YUI documentation for details.
          * These MUST be inline in order to have access to the Alfresco.InvitationList class (via the "me" variable).
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
         renderCellThumbnail = function InvitationList_renderCellThumbnail(elCell, oRecord, oColumn, oData)
         {
            var name = oRecord.getData("name");
            var extn = name.substring(name.lastIndexOf("."));

            oColumn.width = 40;
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
         renderCellDescription = function InvitationList_renderCellDescription(elCell, oRecord, oColumn, oData)
         {
            // we currently render all results the same way
            var site = oRecord.getData("site");
            var url = "";
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
               url = Alfresco.constants.URL_PAGECONTEXT + "site/" + site.shortName + "/" + oRecord.getData("browseUrl");
           }
            var desc = "";
            // title/link to view page
            desc = '<h3 class="itemname"><a href="' + url + '">' + oRecord.getData("displayName") + '</a></h3>';
            // link to the site
            desc += '<div class="detail">';
            desc += '   In Site: <a href="' + Alfresco.constants.URL_PAGECONTEXT + "site/" + site.shortName + '/dashboard">' + site.title + '</a>';
            desc += '</div>';
            desc += '<div class="details">';
            desc += '   Tags: ';
            var tags = oRecord.getData("tags");
            for (var x=0; x < tags.length; x++)
            {
                desc += '<span id="' + me.id + '-InvitationListByTag-' + tags[x] + '"><a class="InvitationList-tag">' + tags[x] + '</a> </span>';
            }
            desc += '</div>';
            elCell.innerHTML = desc;
         };

         // DataTable column defintions
         var columnDefinitions = [
         {
            key: "icon32", label: "Preview", sortable: false, formatter: renderCellThumbnail, width: 40
         },
         {
            key: "fileName", label: "Description", sortable: false, formatter: renderCellDescription
         }];

         // show initial message
         this._setDefaultDataTableErrors();
         if (this.options.initialInvitationListTerm.length < 1)
         {
            YAHOO.widget.DataTable.MSG_EMPTY = "No InvitationList performed yet";
         }

         // DataTable definition
         this.widgets.dataTable = new YAHOO.widget.DataTable(this.id + "-results", columnDefinitions, this.widgets.dataSource,
         {
            renderLoopSize: 32,
            initialLoad: false
         });
         
         // Override abstract function within DataTable to set custom error message
         this.widgets.dataTable.doBeforeLoadData = function InvitationList_doBeforeLoadData(sRequest, oResponse, oPayload)
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


      /**
       * DEFAULT ACTION EVENT HANDLERS
       * Handlers for standard events fired from YUI widgets, e.g. "click"
       */

      InvitationListByTag: function InvitationList_InvitationListTag(param)
      {
         // send a InvitationList bubble event to load the list
         YAHOO.Bubbling.fire("onInvitationList",
         {
            InvitationListTerm : param
         });
      },
      
      /**
       * Triggered by the InvitationList all/site only link
       */
      toggleInvitationListScope: function InvitationList_switchInvitationListScope()
      {
         var InvitationListAll = ! this.InvitationListAll;
         // send a InvitationList bubble event to load the list
         YAHOO.Bubbling.fire("onInvitationList",
         {
            InvitationListAll : InvitationListAll
         });
      },

      /**
       * BUBBLING LIBRARY EVENT HANDLERS FOR PAGE EVENTS
       * Disconnected event handlers for inter-component event notification
       */

      /**
       * Path Changed event handler
       *
       * @method onInvitationList
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onInvitationList: function InvitationList_onInvitationList(layer, args)
      {
         var obj = args[1];
         if (obj !== null)
         {
            var InvitationListTerm = this.InvitationListTerm;
            if (obj["InvitationListTerm"] !== undefined)
            {
               InvitationListTerm = obj["InvitationListTerm"];
            }
            var InvitationListAll= this.InvitationListAll;
            if (obj["InvitationListAll"] !== undefined)
            {
               InvitationListAll = obj["InvitationListAll"];
            }
            this._performInvitationList(InvitationListTerm, InvitationListAll);
         }
      },

      
      /**
       * Resets the YUI DataTable errors to our custom messages
       * NOTE: Scope could be YAHOO.widget.DataTable, so can't use "this"
       *
       * @method _setDefaultDataTableErrors
       */
      _setDefaultDataTableErrors: function InvitationList__setDefaultDataTableErrors()
      {
         var msg = Alfresco.util.message;
         YAHOO.widget.DataTable.MSG_EMPTY = msg("message.empty", "Alfresco.InvitationList");
         YAHOO.widget.DataTable.MSG_ERROR = msg("message.error", "Alfresco.InvitationList");
      },
      
      /**
       * Updates document list by calling data webscript with current site and path
       *
       * @method _updateDocList
       * @param path {string} Path to navigate to
       */
      _performInvitationList: function InvitationList__performInvitationList(InvitationListTerm, InvitationListAll)
      {
         // Reset the custom error messages
         this._setDefaultDataTableErrors();
         
         // Display loading message
         YAHOO.widget.DataTable.MSG_EMPTY = "InvitationListing term '" + InvitationListTerm + "'"; // this._msg("message.loading");
         //this.widgets.dataTable.render();
         
         // empty results table
         this.widgets.dataTable.deleteRows(0, this.widgets.dataTable.getRecordSet().getLength());
         
         function successHandler(sRequest, oResponse, oPayload)
         {
            this.InvitationListTerm = InvitationListTerm;
            this.InvitationListAll = InvitationListAll;
            this.widgets.dataTable.onDataReturnInitializeTable.call(this.widgets.dataTable, sRequest, oResponse, oPayload);
            // update the result info
            this._updateResultsInfo();
            this._updateInvitationListAllLinks();
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
         
         this.widgets.dataSource.sendRequest(this._buildInvitationListParams(InvitationListAll, InvitationListTerm),
         {
               success: successHandler,
               failure: failureHandler,
               scope: this
         });
      },
      
      _updateResultsInfo: function InvitationList__updateInvitationListResultsInfo()
      {
         // update the InvitationList results field
         var InvitationListFor = '<b>' + this.InvitationListTerm + '</b>';
         var InvitationListIn = (this.InvitationListAll ? this._msg("InvitationList.info.inallsites") : this._msg("InvitationList.info.insite", '<b>' + this.options.siteId + '</b>'));
         var resultsCount = '<b>' + this.resultsCount + '</b>';
         if (this.hasMoreResults)
         {
            resultsCount = this._msg("InvitationList.info.morethan", resultsCount);
         }
         var html = this._msg("InvitationList.info.resultinfo", InvitationListFor, InvitationListIn, resultsCount);
         if (this.hasMoreResults)
         {
            html += " " + this._msg("InvitationList.info.onlyshowing", this.resultsCount);
         }
         
         var elems = YAHOO.util.Dom.getElementsByClassName("InvitationList-result-info");
         for (x in elems)
         {
            elems[x].innerHTML = html;
         }
      },
      
      _updateInvitationListAllLinks: function InvitationList__updateInvitationListAllLinks()
      {
         // only proceed if there's a site to switch to
         if (this.options.siteId == "")
         {
            return;
         }
         
         // update the InvitationList results field
         var text = "";
         if (this.InvitationListAll)
         {
            text = this._msg("InvitationList.InvitationListsiteonly", this.options.siteId);
         }
         else
         {
            text = this._msg("InvitationList.InvitationListall");
         }
         
         var elems = YAHOO.util.Dom.getElementsByClassName("InvitationList-scope-toggle");
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
      _buildInvitationListParams: function InvitationList__buildInvitationListParams(InvitationListAll, InvitationListTerm)
      {
         var site = InvitationListAll ? "" : this.options.siteId;
         var container = InvitationListAll ? "" : this.options.containerId;
         var params = YAHOO.lang.substitute("site={site}&container={container}&term={term}&maxResults={maxResults}",
         {
            site: encodeURIComponent(site),
            container: encodeURIComponent(container),
            term : encodeURIComponent(InvitationListTerm),
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
      _msg: function InvitationList__msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, "Alfresco.InvitationList", Array.prototype.slice.call(arguments).slice(1));
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