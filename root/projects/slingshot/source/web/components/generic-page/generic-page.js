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
 * SitePage component
 *
 *
 * @namespace Alfresco
 * @class Alfresco.SitePage
 */

(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
         Event = YAHOO.util.Event,
         Element = YAHOO.util.Element;
   var $html = Alfresco.util.encodeHTML;

   /**
    * SitePage constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.SiteFinder} The new SiteFinder instance
    * @constructor
    */
   Alfresco.SitePage = function(htmlId)
   {

      this.id = htmlId;
      this.name = "Alfresco.SitePage";
      this.currentFilter = {};

      this.widgets = {};

      /**
       * The edited link CSS style.
       */
      this.EDITEDCLASS = "edit-node";

      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "container", "datasource", "datatable", "json", "resize"], this.onComponentsLoaded, this);
   }

   Alfresco.SitePage.prototype =
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
          */
         siteId: "",


         /**
          * Initially used filter name and id.
          */
         initialFilter:
         {
         },

         /**
          * Number of items displayed per page
          *
          * @property pageSize
          * @type int
          */
         pageSize: 10,

         /**
          * Length of preview content loaded for each topic
          */
         maxContentLength: 512,

         /**
          * The pagination flag.
          *
          * @property: usePagination
          * @type: boolean
          * @default: true
          */
         usePagination : true,


         /**
          * ContainerId representing root container
          *
          * @property containerId
          * @type string
          * @default "SitePage"
          */
         containerId: "generic-page"
      },

      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       */
      setOptions: function SitePage_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },

      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function SitePage_onComponentsLoaded()
      {
         Event.onContentReady(this.id, this.onReady, this, true);
      },

      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function SitePage_onReady()
      {
         this.activate();

         // Refresh button
         this.widgets.refresh = Alfresco.util.createYUIButton(this, "refresh-button", this.onRefresh);
      },

      /**
       * Action handler for the refresh toggle button
       *
       * @method onRefresh
       */
      onRefresh: function SitePage_onRefresh(e, p_obj)
      {
         //this.options.simpleView = !this.options.simpleView;
         //p_obj.set("label", this._msg(this.options.simpleView ? "header.detailList" : "header.simpleList"));

         // refresh the list
         //YAHOO.Bubbling.fire("blogpostlistRefresh");
         this.updateSitePagesList({page : 1});
         Event.preventDefault(e);
      },

      /** Object container for storing YUI widget instances.
       *
       * @property widgets
       * @type object
       */
      widgets :
      {
      },

      /**
       * init DataSource
       * @method createDataSource
       * @return {Alfresco.SitePage} returns 'this' for method chaining
       */
      createDataSource : function SitePage_createDataSource()
      {
         //TODO: for testing back-end process
         var uriResults = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "api/pagedata/{pageid}",
         {
              pageid: "pageid"
         });

         this.widgets.dataSource = new YAHOO.util.DataSource(uriResults);
         this.widgets.dataSource.responseType = YAHOO.util.DataSource.TYPE_JSON;
         this.widgets.dataSource.connXhrMode = 'queueRequests';
         this.widgets.dataSource.responseSchema =
         {
            resultsList: 'items',
            fields: ['name','type','reference'],
            metaFields:
            {
               paginationRecordOffset:'startIndex',
               totalRecords:'total'
            }
         }

         return this;
      },

      /**
       * Set messages for this component.
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {Alfresco.SitePage} returns 'this' for method chaining
       */
      setMessages: function SitePage_setMessages(obj)
      {
         Alfresco.util.addMessages(obj, this.name);
         return this;
      },

      /**init DataTable
       * @method createDataTable
       * @return {Alfresco.SitePage} returns 'this' for method chaining
       */
      createDataTable : function SitePage_createDataTable()
      {
         var me = this;
         var renderCellShortName = function SitePage_renderCellShortName(elCell, oRecord, oColumn, oData)
         {
            var name = oRecord.getData('name');
            elCell.innerHTML = '<div><span class="name-site-page-and-url">' + name + '</span></div>'
            elCell.parentNode.oRecord = oRecord;
         };

         var renderCellType = function SitePage_renderCellType(elCell, oRecord, oColumn, oData)
         {
            var type = oRecord.getData('type');
            elCell.innerHTML = '<div><span class="name-site-page-and-url">'
                    + type + '</span></div><div class="property-sheet" id="' + me.id + '-edit"></div>';
            elCell.parentNode.oRecord = oRecord;
         }

         var renderCellReference = function SitePage_renderCellReference(elCell, oRecord, oColumn, oData)
         {
            var ref = oRecord.getData('reference');
            var name = oRecord.getData('name');
            elCell.innerHTML = "<a href='#'>"+ref+"</a>";
            elCell.firstChild.onclick = function()
            {
               var url = YAHOO.lang.substitute(Alfresco.constants.URL_CONTEXT + "page/node-info?path={path}",
               {
                  path: name
               });
               window.location = url;
            }
         }

         var columnDefinitions =
         [
               {
                 key: 'shortName',
                 label: 'Name',
                 sortable: true,
                 formatter: renderCellShortName,
                 resizable: true
               },
               {
                  key: 'type',
                  label: 'Type',
                  sortable: true,
                  formatter: renderCellType,
                  editor:"textbox",
                  resizable: true
               },
               {
                  key : 'reference',
                  label : "Reference",
                  sortable : true,
                  formatter : renderCellReference,
                  resizable: true
               }
         ];

         YAHOO.widget.DataTable.CLASS_SELECTED = "site-page-selected-row";

         YAHOO.widget.DataTable.MSG_EMPTY = '<span class="datatable-msg-empty">' +
                                            Alfresco.util.message("message.empty", "Alfresco.SitePage") + '</span>';

         this.widgets.paginator = new YAHOO.widget.Paginator(
         {
            containers: [this.id + "-paginator"],
            rowsPerPage: this.options.pageSize,
            initialPage: 1,
            template: this._msg("pagination.template"),
            pageReportTemplate: this._msg("pagination.template.page-report")
         });

         // called by the paginator on state changes
         var handlePagination = function SitePage_handlePagination(state, dt)
         {
            me.updateSitePagesList({ page : state.page });
         }

         this.widgets.dataTable = new YAHOO.widget.DataTable(this.id + '-site-page', columnDefinitions, this.widgets.dataSource,
         {
            renderLoopSize: 32,
            initialLoad : false,
            paginationEventHandler: handlePagination,
            paginator: this.widgets.paginator
         });

         this.widgets.dataTable.doBeforeLoadData = function SitePage_doBeforeLoadData(sRequest, oResponse, oPayload)
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
               }

            }
            else if (oResponse.results && !me.options.usePagination)
            {
               this.renderLoopSize = oResponse.results.length >> (YAHOO.env.ua.gecko) ? 3 : 5;
            }

            return true;
         }

         this.widgets.dataTable.subscribe("tableMsgShowEvent", function(oArgs)
         {
            this._elMsgTbody.parentNode.style.width = "";
         });

         this.widgets.dataTable.set("selectionMode", "single");

         this.updateSitePagesList({ page: 1 });

      },

      /**
       * Update current page list
       *
       * @method updateSitePage
       */
      updateSitePagesList:function SitePage_updateSitePagesList(p_obj)
      {

         function successHandler(sRequest, oResponse, oPayload)
         {
            this.widgets.dataTable.onDataReturnInitializeTable.call(this.widgets.dataTable, sRequest, oResponse, oPayload);
         }

         function failureHandler(sRequest, oResponse)
         {

            if (oResponse.status == 401)
            {
               // Our session has likely timed-out, so refresh to offer the login page
               window.location.reload(true);
            }
            else
            {
               try
               {
                  var response = YAHOO.lang.JSON.parse(oResponse.responseText);
                  YAHOO.widget.DataTable.MSG_ERROR = response.message;
                  this.widgets.dataTable.showTableMessage(response.message, YAHOO.widget.DataTable.CLASS_ERROR);
                  if (oResponse.status == 404)
                  {
                     YAHOO.Bubbling.fire("deactivateAllControls");
                  }
               }
               catch(e)
               {
               }
            }
         }
         this.widgets.dataSource.sendRequest(this._buildSitePageParams(p_obj || {}),
         {
            success: successHandler,
            failure: failureHandler,
            scope: this
         });

      },


      /**
       * Deactivate All Controls event handler
       *
       * @method onDeactivateAllControls
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onDeactivateAllControls: function SitePage_onDeactivateAllControls(layer, args)
      {
         for (var widget in this.widgets)
         {
            this.widgets[widget].set("disabled", true);
         }
      },

      /**
       * activation of components
       * @method activate.
       */
      activate : function SitePage_activate()
      {
         this.currentFilter = {
            filterOwner: "Alfresco.SitePageFilter",
            filterId:"all"
         }

         this.createDataSource();
         this.createDataTable();
      },

      /**Build URI parameter string for doclist JSON data webscript
       *
       * @method _buildDocListParams
       * @param p_obj.page {string} Page number
       * @param p_obj.pageSize {string} Number of items per page
       */
      _buildSitePageParams: function SitePage_buildSitePageParams(p_obj)
      {
         var params = {
            contentLength: this.options.maxContentLength,
            fromDate: null,
            toDate: null,
            tag: null,

            page: this.widgets.paginator.get("page") || "1",
            pageSize: this.widgets.paginator.get("rowsPerPage")
         }

         // Passed-in overrides
         if (typeof p_obj == "object")
         {
            params = YAHOO.lang.merge(params, p_obj);
         }

         // calculate the startIndex param
         params.startIndex = (params.page - 1) * params.pageSize;

         // check what url to call and with what parameters
         var filterOwner = this.currentFilter.filterOwner;
         var filterId = this.currentFilter.filterId;
         var filterData = this.currentFilter.filterData;

         // check whether we got a filter or not
         var url = "";
         if (filterOwner == "Alfresco.SitePageFilter")
         {
            // latest only
            switch (filterId)
                  {
               case "all":url = "?filter=all";break;
            }
         }
         else if (filterOwner == "Alfresco.LinkTags")
         {
            url = "?filter=tag";
            params.tag = filterData;
         }

         // build the url extension
         var urlExt = "";
         for (var paramName in params)
         {
            if (params[paramName] !== null)
            {
               urlExt += "&" + paramName + "=" + encodeURIComponent(params[paramName]);
            }
         }
         if (urlExt.length > 0)
         {
            urlExt = urlExt.substring(1);
         }
         return url + '&format=json' + "&" + urlExt;

      },

      /**
       * Gets a custom message
       *
       * @method _msg
       * @param messageId {string} The messageId to retrieve
       * @return {string} The custom message
       * @private
       */
      _msg:function SitePage_msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, this.name, Array.prototype.slice.call(arguments).slice(1));
      }

   }
})();