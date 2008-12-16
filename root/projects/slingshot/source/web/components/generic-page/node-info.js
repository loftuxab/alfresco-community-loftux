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
 * @class Alfresco.NodeInfo
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
    * NodeInfo constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.SiteFinder} The new SiteFinder instance
    * @constructor
    */
   Alfresco.NodeInfo = function(htmlId)
   {

      this.id = htmlId;
      this.name = "Alfresco.NodeInfo";
      this.currentFilter = {};

      this.widgets = {};

      /**
       * Object literal used to generate unique tag ids
       *
       * @property tagId
       * @type object
       */

      this.tagId =
      {
         id: 0,
         tags: {}
      };

      /**
       * The edited link CSS style.
       */
      this.EDITEDCLASS = "edit-node";

      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "container", "datasource", "datatable", "json", "resize"], this.onComponentsLoaded, this);
   }

   Alfresco.NodeInfo.prototype =
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
         nodeId: "",


         /**
          * Initially used filter name and id.
          */
         initialFilter:
         {
         }

      },

      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       */
      setOptions: function NodeInfo_setOptions(obj)
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
      onComponentsLoaded: function NodeInfo_onComponentsLoaded()
      {
         Event.onContentReady(this.id, this.onReady, this, true);
      },

      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function NodeInfo_onReady()
      {
         this.activate();
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
       * @return {Alfresco.NodeInfo} returns 'this' for method chaining
       */
      createPropDataSource : function NodeInfo_createPropDataSource()
      {
         //TODO: for testing back-end process
         var uriResults = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "api/node-info/{path}",
         {
              path: this.options.nodeId
         });

         this.widgets.dataSource = new YAHOO.util.DataSource(uriResults);
         this.widgets.dataSource.responseType = YAHOO.util.DataSource.TYPE_JSON;
         this.widgets.dataSource.connXhrMode = 'queueRequests';
         this.widgets.dataSource.responseSchema =
         {
            resultsList: 'properties',
            fields: ['name','value'],
            metaFields : {
                  "nodedata" : "nodedata"
            }
         }

         return this;
      },

      /**
       * Set messages for this component.
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {Alfresco.NodeInfo} returns 'this' for method chaining
       */
      setMessages: function NodeInfo_setMessages(obj)
      {
         Alfresco.util.addMessages(obj, this.name);
         return this;
      },

      /**init DataTable
       * @method createPropTable
       * @return {Alfresco.NodeInfo} returns 'this' for method chaining
       */
      createPropTable : function NodeInfo_createPropTable(data)
      {
         var me = this;

         this.widgets.propDataSource = new YAHOO.util.DataSource(data);
         this.widgets.propDataSource.responseType = YAHOO.util.DataSource.TYPE_JSON;
         this.widgets.propDataSource.responseSchema =
         {
            fields: ['name','value']
         }
          
         var renderCellShortName = function NodeInfo_renderCellShortName(elCell, oRecord, oColumn, oData)
         {
            var name = oRecord.getData('name');
            elCell.innerHTML = '<div><span class="">' + name + '</span></div>'
            elCell.parentNode.oRecord = oRecord;
         };

         var renderCellValue = function NodeInfo_renderCellValue(elCell, oRecord, oColumn, oData)
         {
            var value = oRecord.getData('value');
            elCell.innerHTML = '<div><span class="">' + value + '</span></div>';
            elCell.parentNode.oRecord = oRecord;
         }

         var renderCellType = function NodeInfo_renderCellType(elCell, oRecord, oColumn, oData)
         {
            var propertyType = oRecord.getData('propertyType');
            elCell.innerHTML = '<div><span class="">' + propertyType + '</span></div>';
            elCell.parentNode.oRecord = oRecord;

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
                  key: 'value',
                  label: 'Value',
                  sortable: true,
                  formatter: renderCellValue,
                  editor:"textbox",
                  resizable: true
               }/*,
               {
                  key : 'propertyType',
                  label : "Property Type",
                  sortable : false,
                  formatter : renderCellType,
                  resizable: true
               }*/
         ];

         YAHOO.widget.DataTable.CLASS_SELECTED = "";

         YAHOO.widget.DataTable.MSG_EMPTY = '<span class="datatable-msg-empty">' +
                                            Alfresco.util.message("message.empty", "Alfresco.NodeInfo") + '</span>';



         this.widgets.propTable = new YAHOO.widget.DataTable(this.id + '-node-info-list', columnDefinitions, this.widgets.propDataSource);

         this.widgets.propTable.doBeforeLoadData = function NodeInfo_doBeforeLoadData(sRequest, oResponse, oPayload)
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

         this.widgets.propTable.subscribe("tableMsgShowEvent", function(oArgs)
         {
            this._elMsgTbody.parentNode.style.width = "";
         });

         this.widgets.propTable.set("selectionMode", "single");



      },

      createAspectsTable : function NodeInfo_createAspectsTable(data)
      {
         var me = this;
         var renderCellShortName = function NodeInfo_renderCellShortName(elCell, oRecord, oColumn, oData)
         {
            var name = oRecord.getData('name');
            elCell.innerHTML = '<div><span class="">' + name + '</span></div>'
            elCell.parentNode.oRecord = oRecord;
         };

         var columnDefinitions =
         [
               {
                 key: 'shortName',
                 label: 'Name',
                 sortable: true,
                 formatter: renderCellShortName,
                 resizable: true
               }
         ];

          this.widgets.aspDataSource = new YAHOO.util.DataSource(data);
          this.widgets.aspDataSource.responseType = YAHOO.util.DataSource.TYPE_JSON;
          this.widgets.aspDataSource.responseSchema =
          {
            fields: ['name']
          }

         
         this.widgets.AspectsTable = new YAHOO.widget.DataTable(this.id + '-node-info-aspects', columnDefinitions, this.widgets.aspDataSource);

         this.widgets.AspectsTable.doBeforeLoadData = function NodeInfo_doBeforeLoadData(sRequest, oResponse, oPayload)
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

         this.widgets.AspectsTable.subscribe("tableMsgShowEvent", function(oArgs)
         {
            this._elMsgTbody.parentNode.style.width = "";
         });

         this.widgets.AspectsTable.set("selectionMode", "single");


      },

      /**
       * Update current page list by calling data webscript with current site and filter information
       *
       * @method updateNodeInfo
       */
      updateNodeInfosList:function NodeInfo_updateNodeInfosList()
      {
         var me = this;
         function successHandler(data)
         { 
            var nodeData = data.json.nodedata;
            Dom.get(this.id + "-listtitle").innerHTML = YAHOO.lang.substitute(me._msg("title.details"),{nodeName : nodeData.name});
            Dom.get(this.id + "-node-type").innerHTML = nodeData.type;
            Dom.get(this.id + "-node-reference").innerHTML = nodeData.reference;
            Dom.get(this.id + "-node-url").innerHTML = nodeData.url;

            //this.widgets.dataTable.onDataReturnInitializeTable.call(this.widgets.dataTable, sRequest, oResponse, oPayload);
            // this.widgets.propTable.onDataReturnInitializeTable.call(this.widgets.propTable,null, oResponse, oPayload);
             this.createPropTable(data.json.nodeinfo.properties);
             this.createAspectsTable(data.json.nodeinfo.aspects);
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
                  this.widgets.propTable.showTableMessage(response.message, YAHOO.widget.DataTable.CLASS_ERROR);
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


        /* this.widgets.dataSource.sendRequest(this._buildNodeInfoParams(),
         {
            success: successHandler,
            failure: failureHandler,
            scope: this
         }); */

         var url = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "api/node-info/{path}",
         {
              path: this.options.nodeId
         });

         url +=  this._buildNodeInfoParams();

         Alfresco.util.Ajax.request(
         {
            method: Alfresco.util.Ajax.GET,
				url: url,
				successCallback:
				{
					fn: successHandler,
					scope: this
				},
				failureCallback:
				{
					fn: failureHandler,
					scope: this
				}
			});

      },


      /**
       * Deactivate All Controls event handler
       *
       * @method onDeactivateAllControls
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onDeactivateAllControls: function NodeInfo_onDeactivateAllControls(layer, args)
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
      activate : function NodeInfo_activate()
      {
         this.currentFilter = {
            filterOwner: "Alfresco.NodeInfoFilter",
            filterId:"all"
         }

         //this.createPropDataSource();
         //this.createPropTable();
         //this.createAspectsTable();
          
         this.updateNodeInfosList({ page: 1 });
      },

      /**Build URI parameter string for doclist JSON data webscript
       *
       * @method _buildDocListParams
      */
      _buildNodeInfoParams: function NodeInfo_buildNodeInfoParams()
      {
         var params = {
            fromDate: null,
            toDate: null,
            tag: null
         }


         // check what url to call and with what parameters
         var filterOwner = this.currentFilter.filterOwner;
         var filterId = this.currentFilter.filterId;
         var filterData = this.currentFilter.filterData;

         // check whether we got a filter or not
         var url = "";
         if (filterOwner == "Alfresco.NodeInfoFilter")
         {
            // latest only
            switch (filterId)
            {
               case "all":url = "?filter=all";break;
            }
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
         
         return url + '&format=json' + urlExt;

      },

      /**
       * Gets a custom message
       *
       * @method _msg
       * @param messageId {string} The messageId to retrieve
       * @return {string} The custom message
       * @private
       */
      _msg:function NodeInfo_msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, this.name, Array.prototype.slice.call(arguments).slice(1));
      }

   }
})();