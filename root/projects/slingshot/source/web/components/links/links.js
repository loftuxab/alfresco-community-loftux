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
 * Links component
 *
 *
 * @namespace Alfresco
 * @class Alfresco.Links
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
    * Links constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.SiteFinder} The new SiteFinder instance
    * @constructor
    */
   Alfresco.Links = function(htmlId)
   {

      this.id = htmlId;
      this.name = "Alfresco.Links";
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
       * The deleted link CSS style.
       */
      this.DELETEDCLASS = "delete-link";

      /**
       * The edited link CSS style.
       */
      this.EDITEDCLASS = "edit-link";

      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "container", "datasource", "datatable", "json", "resize"], this.onComponentsLoaded, this);

      YAHOO.Bubbling.on("filterChanged", this.onFilterChanged, this);
      YAHOO.Bubbling.on("linksListRefresh", this.onLinksListRefresh, this);

      this.newLinkBtn = null;
      this.changeListViewBtn = null;
      this.linksMenu = null;
   }

   Alfresco.Links.prototype =
   {
      /**
       * Tells whether an action is currently ongoing.
       *
       * @property busy
       * @type boolean
       * @see _setBusy/_releaseBusy
       */
      busy: false,

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
          * Flag indicating whether the list shows a detailed view or a simple one.
          *
          * @property simpleView
          * @type boolean
          */
         simpleView: false,

         /**
          * Length of preview content loaded for each topic
          */
         maxContentLength: 512,

         /**
          * Minimal length of filter panel
          */

         MIN_FILTER_PANEL_WIDTH : 150,

         /**
          * Maximal length of filter panel
          */

         MAX_FILTER_PANEL_WIDTH : 640 - ((YAHOO.env.ua.ie > 0) && (YAHOO.env.ua.ie < 7) ? 160 : 0),

         /**
          * The pagination flag.
          *
          * @property: usePagination
          * @type: boolean
          * @default: true
          */
         usePagination : true,

         /**
          * Minimal height of filter panel
          */
         MAX_FILTER_PANEL_HEIGHT : 200,

         /**
          * ContainerId representing root container
          *
          * @property containerId
          * @type string
          * @default "links"
          */
         containerId: "links"
      },

      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       */
      setOptions: function Links_setOptions(obj)
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
      onComponentsLoaded: function Links_onComponentsLoaded()
      {
         Event.onContentReady(this.id, this.onReady, this, true);
      },

      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function Links_onReady()
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
       * @return {Alfresco.Links} returns 'this' for method chaining
       */
      createDataSource : function Links_createDataSource()
      {
         var uriResults = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "api/links/site/{site}/{container}",
         {
            site: this.options.siteId,
            container: this.options.containerId
         });

         this.widgets.dataSource = new YAHOO.util.DataSource(uriResults);
         this.widgets.dataSource.responseType = YAHOO.util.DataSource.TYPE_JSON;
         this.widgets.dataSource.connXhrMode = 'queueRequests';
         this.widgets.dataSource.responseSchema =
         {
            resultsList: 'items',
            fields: ['name','title', 'description', 'url', 'tags','internal','isUpdated'],
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
       * @return {Alfresco.Links} returns 'this' for method chaining
       */
      setMessages: function Links_setMessages(obj)
      {
         Alfresco.util.addMessages(obj, this.name);
         return this;
      },

      /**init DataTable
       * @method createDataTable
       * @return {Alfresco.Links} returns 'this' for method chaining
       */
      createDataTable : function Links_createDataTable()
      {
         var me = this;
         var renderCellThumbnail = function Links_renderCellThumbnail(elCell, oRecord, oColumn, oData)
         {
            elCell.innerHTML = '<input class="checkbox-column" type="checkbox" />';
            elCell.firstChild.onclick = function()
            {
               var count = me.getSelectedLinks().length;
               me.linksMenu.set("disabled", count == 0);
            }
         };

         var renderCellDescription = function Links_renderCellDescription(elCell, oRecord, oColumn, oData)
         {
            var data = oRecord.getData(); 
            var name = oRecord.getData('title');
            var url = oRecord.getData('url');
            var description = oRecord.getData('description');
            var tags = oRecord.getData('tags');
            var isUpdated = oRecord.getData('isUpdated');
            var linksViewUrl = me.generateLinksViewUrl(me.options.siteId, me.options.containerId, data.name);
            var tagsStr = "";
             if (tags.length > 0)
             {
                 for (var i = 0; i < tags.length; i++)
                 {

                     tagsStr += me._generateTagLink(tags[i]);
                     if (i != (tags.length - 1)) tagsStr += ', &nbsp;';
                 }
             }
             else
             {
                 tagsStr = me._msg("dialog.tags.none");
             }
             elCell.innerHTML = '<div><span class="name-links-and-url"><a href="' + linksViewUrl + '">' + name + '</a></span>' +
                                '<span>&nbsp;</span>' + '<span class="nodeStatus-isUpdate">' + ((eval(isUpdated)) ? "(Updated)" : "") + '</span></div>' +
                                ((!me.options.simpleView) ? (description + '<br />' + 'URL : <a target="_blank" href="' + url + '">' + url + '</a><br />' +
                                                            'tags : ' + tagsStr) : "");
         }

         var renderCellActions = function Links_renderCellActions(elCell, oRecord, oColumn, oData)
         {
            var prefix = oRecord.getData('title');
            elCell.style.display = "none";

            elCell.innerHTML = "<div class='" + me.EDITEDCLASS + ((me.options.simpleView)?" simple-view":"") + "'><a id='edit-" + prefix + "'><span>" + me._msg("links.edit") + "</a></span></div>" +
                               "<div class='" + me.DELETEDCLASS + ((me.options.simpleView)?" simple-view":"") + "'><a id='delete-" + prefix + "'><span>" + me._msg("links.delete") + "</a></span></div>";

            var elink = elCell.getElementsByTagName("a")[0];
            var dlink = elCell.getElementsByTagName("a")[1];

            elink.parentNode.onclick = function Links_onEditLink()
            {
               var url = YAHOO.lang.substitute(Alfresco.constants.URL_CONTEXT + "page/site/{site}/links-linkedit?container={container}&linkId={linkId}",
               {
                  site: me.options.siteId,
                  container: me.options.containerId,
                  linkId: oRecord.getData('name')
               });
               window.location = url;
            }
            /*elink.parentNode.onclick = function Links_updateHandler()
            {
               me.showEditLinkDlg(oRecord)
            };*/

            elink.parentNode.onmouseover = function()
            {
               Dom.addClass(this, me.EDITEDCLASS + "-over");
            };

            elink.parentNode.onmouseout = function()
            {
               Dom.removeClass(this, me.EDITEDCLASS + "-over");
            };

            dlink.parentNode.onclick = function ()
            {
               var mes = me._msg("dialog.confirm.message.delete").replace('{0}', prefix);
               var callback = function() {
                  me.deleteLinks([oRecord])
               };
               me.showConfirmDialog(mes, callback);
            };

            dlink.parentNode.onmouseover = function()
            {
               Dom.addClass(this, me.DELETEDCLASS + "-over");
            };

            dlink.parentNode.onmouseout = function()
            {
               Dom.removeClass(this, me.DELETEDCLASS + "-over");
            };

            Dom.setStyle(elCell.parentNode, "border-left", "3px solid #fff");
         };

         var columnDefinitions =
               [{
                  key: 'shortName', label: 'Short Name', sortable: false, formatter: renderCellThumbnail
               }, {
                  key: 'title', label: 'Title', sortable: false, formatter: renderCellDescription, editor:"textbox"
               }, {
                  key: 'description', label: 'Description', formatter: renderCellActions
               }
               ];

         YAHOO.widget.DataTable.CLASS_SELECTED = "links-selected-row";

         YAHOO.widget.DataTable.MSG_EMPTY = '<span class="datatable-msg-empty">' +
                                            Alfresco.util.message("links.empty", "Alfresco.Links") + '</span>';

         this.widgets.paginator = new YAHOO.widget.Paginator(
         {
            containers: [this.id + "-paginator"],
            rowsPerPage: this.options.pageSize,
            initialPage: 1,
            template: this._msg("pagination.template"),
            pageReportTemplate: this._msg("pagination.template.page-report")
         });

         // called by the paginator on state changes
         var handlePagination = function Links_handlePagination (state, dt)
         {
            me.updateLinks({ page : state.page });
         }

         this.widgets.dataTable = new YAHOO.widget.DataTable(this.id + '-links', columnDefinitions, this.widgets.dataSource,
         {
            renderLoopSize: 32,
            initialLoad : false,
            paginationEventHandler: handlePagination,
            paginator: this.widgets.paginator
         });

         this.widgets.dataTable.doBeforeLoadData = function Links_doBeforeLoadData(sRequest, oResponse, oPayload)
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

            // Must return true to have the "Loading..." message replaced by the error message
            return true;
         }

         this.widgets.dataTable.subscribe("tableMsgShowEvent", function(oArgs)
         {
            // NOTE: Scope needs to be DataTable
            this._elMsgTbody.parentNode.style.width = "";
         });

         this.widgets.dataTable.set("selectionMode", "single");

         var onRowMouseover = function(e)
         {
            me.widgets.dataTable.selectRow(e.target);
            e.target.cells[2].childNodes[0].style.display = "";
            e.target.cells[2].style.borderLeft = "1px solid #C5E6E9";
         }

         var onRowMouseout = function(e)
         {
            me.widgets.dataTable.unselectRow(e.target);
            e.target.cells[2].childNodes[0].style.display = "none";
            e.target.cells[2].style.borderLeft = "1px solid #FFF";
         }

         this.widgets.dataTable.subscribe("rowMouseoverEvent", onRowMouseover);
         this.widgets.dataTable.subscribe("rowMouseoutEvent", onRowMouseout);

         var filterObj = YAHOO.lang.merge(
         {
            filterId: "all",
            filterOwner: "Alfresco.LinkFilter",
            filterData: null
         }, this.options.initialFilter);
         YAHOO.Bubbling.fire("filterChanged", filterObj);

         this.widgets.dataTable.subscribe("initEvent",this._adjustFilterHeight,this,true);

      },

      /**
      * Generate a view url for a given site, link id.
      *
      * @param linkId the id/name of the post
      * @return an url to access the post
      */
      generateLinksViewUrl: function Links_generateLinksViewUrl(site, container, linkId)
      {
         var url = YAHOO.lang.substitute(Alfresco.constants.URL_CONTEXT + "page/site/{site}/links-view?container={container}&linkId={linkId}",
         {
            site: site,
            container: container,
            linkId: linkId
         });
         return url;
      },

      /**
       * Links Filter changed event handler
       *
       * @method onFilterChanged
       * @param layer {object} Event fired (unused)
       * @param args {array} Event parameters (new filterId)
       */
      onFilterChanged: function Links_onFilterChanged(layer, args)
      {
         var obj = args[1];
         if ((obj !== null) && (obj.filterId !== null))
         {
            this.currentFilter =
            {
               filterId: obj.filterId,
               filterOwner: obj.filterOwner,
               filterData: obj.filterData
            };
            this.updateLinks({ page: 1 });
         }
      },

      /**
       * Links Refresh Required event handler
       *
       * @method onLinksListRefresh
       * @param layer {object} Event fired (unused)
       * @param args {array} Event parameters (unused)
       */
      onLinksListRefresh: function Links_onLinksListRefresh(layer, args)
      {
         this.updateLinks();
      },

      /**
       * Updates links list by calling data webscript with current site and filter information
       *
       * @method updateLinks
       */
      updateLinks:function Links_updateLinks(p_obj)
      {

         function successHandler(sRequest, oResponse, oPayload)
         {
            this.widgets.dataTable.onDataReturnInitializeTable.call(this.widgets.dataTable, sRequest, oResponse, oPayload);
            this.updateListTitle();
            this.linksMenu.set("disabled", this.getSelectedLinks().length == 0);
            this._adjustFilterHeight();
            YAHOO.Bubbling.fire("tagRefresh", null);
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
         this.widgets.dataSource.sendRequest(this._buildLinksParams(p_obj || {}),
         {
            success: successHandler,
            failure: failureHandler,
            scope: this
         });

      },

      /**
       * Update the list title.
       * @method updateListTitle
       */
      updateListTitle: function Links_updateListTitle()
      {
         var elem = Dom.get(this.id + '-listtitle');
         var title = this._msg("title.generic");

         var filterOwner = this.currentFilter.filterOwner;
         var filterId = this.currentFilter.filterId;
         var filterData = this.currentFilter.filterData;
         if (filterOwner == "Alfresco.LinkFilter")
         {
            switch (filterId)
            {
               case "all":title = this._msg("title.all"); break;
               case "internal":title = this._msg("title.internal"); break;
               case "www":title = this._msg("title.www"); break;
            }

         }
         else if (filterOwner == "Alfresco.LinkTags")
         {
            title = this._msg("title.bytag", $html(filterData));
         }

         elem.innerHTML = title;
      },

      /**
       * Deactivate All Controls event handler
       *
       * @method onDeactivateAllControls
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onDeactivateAllControls: function Links_onDeactivateAllControls(layer, args)
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
      activate : function Links_activate()
      {
         this.attachButtons();
         Dom.setStyle(this.id + '-links-header', 'visibility', 'visible');
         Dom.setStyle(this.id + '-body', 'visibility', 'visible');
         this._generateRSSFeedUrl();
         this.createDataSource();
         this._createResizeCovers();
         this._attachResize();
         this.createDataTable();
         //this._initCreateLinkDialog();
      },

      /**
       * create of components
       * @method _createResizeCovers.
       */
      _createResizeCovers : function Links__createResizeCovers()
      {
         this.leftCover = document.createElement("div");
         this.rightCover = document.createElement("div");
         this.leftCover.className = this.rightCover.className = "cover";
         if (YAHOO.env.ua.ie) this.leftCover.style.backgroundColor = this.rightCover.style.backgroundColor = "red"
      },

      /**
       * topic list resize event handler
       * @method onTopicListResize.
       * @param width {int}
       */
      onTopicListResize : function Links_onTopicListResize(width)
      {
         if (width)
         {
            //Dom.setStyle(Dom.get("divLinkFilters"), "height", "auto");
            Dom.setStyle(Dom.get("divLinkList"), "margin-left", width + 3 + "px");
            this._adjustFilterHeight();
         }
      },

      /**
      * menu item event handler
      * @method onMenuItemClick.
      * @param sType, aArgs, p_obj
      */
      onMenuItemClick:function Links_onMenuItemClick(sType, aArgs, p_obj)
      {
         var me = this;
         switch (aArgs[1]._oAnchor.className.split(" ")[0])
         {
            case "delete-item":
               var callback = function()
               {
                  var arrLinks = me.getSelectedLinks();
                  me.deleteLinks(arrLinks);
               };
               this.showConfirmDialog(this._msg("dialog.confirm.message.delete.selected"), callback);
               break;
            case "deselect-item" :
               this.deselectAll();
               this.linksMenu.set("disabled", true);
               break;
         }

      },

      /**
      * deselect all links
      * @method deselectAll.
      * @param no params
      */
      deselectAll : function Links_deselectAll()
      {
         var rows = this.widgets.dataTable.getTbodyEl().rows;
         for (var i = 0; i < rows.length; i++)
         {
            rows[i].cells[0].getElementsByTagName('input')[0].checked = false;

         }
      },

      /**
       * init links buttons
       * @method attachButtons.
       */
      attachButtons : function Links_attachButtons()
      {
         var me = this;
         this.newLinkBtn = new YAHOO.widget.Button(this.id + '-create-link-button');
         this.newLinkBtn.addListener("click", this.showCreateLinkDlg, this, true);

         this.linksMenu = new YAHOO.widget.Button(this.id + '-selected-i-dd',
         {
            type: "menu",
            menu: this.id + "-selectedItems-menu"
         });
         this.linksMenu.set("disabled", true);
         this.linksMenu.getMenu().subscribe("click", this.onMenuItemClick, this, true);
         this.changeListViewBtn = new YAHOO.widget.Button(this.id + "-viewMode-button",
         {
            type : "checkbox",
            checked : false
         });
         this.changeListViewBtn.addListener('click', this.changeListView, this, true);

         this.linksSelectMenu = new YAHOO.widget.Button(this.id + '-select-button',
         {
            type: "menu",
            menu: this.id + "-selecItems-menu"
         });

         this.linksSelectMenu.getMenu().subscribe('click', this.onSelectItemClick, this, true);
      },

      /**
       * Handler on Menu Item Click
       * @param sType
       * @param aArgs
       * @param p_obj
       * @method onSelectItemClick
       */
      onSelectItemClick : function Links_onSelectItemClick(sType, aArgs, p_obj)
      {
         var elem = YAHOO.env.ua.ie ? aArgs[0].srcElement : aArgs[0].target;
         if (elem.tagName.toLocaleLowerCase() != "span") elem = elem.getElementsByTagName("span")[0];
         switch (elem.className.split(" ")[0])
         {
            case "links-action-deselect-all" :
               this.deselectAll();
               this.linksMenu.set("disabled", true);
               break;

            case "links-action-select-all" :
               this.selectAll();
               this.linksMenu.set("disabled", !this.getSelectedLinks().length);
               break;

            case "links-action-invert-selection" :
               this.invertAll();
               break;
         }
      },

      /**
       * Invert All Selection on the page
       * @method invertAll
       */
      invertAll : function Links_invertAll()
      {
         var isDisable = false;
         var rows = this.widgets.dataTable.getTbodyEl().rows;
         for (var i = 0; i < rows.length; i++)
         {
            var ipt = rows[i].cells[0].getElementsByTagName('input')[0];
            ipt.checked = !ipt.checked;
            isDisable = (ipt.checked) ? true : isDisable;
         }
         this.linksMenu.set("disabled", !isDisable);
      },

      /**
       * select All Tags on the page
       * @method selectAll
       */
      selectAll : function Links_selectAll()
      {
         var rows = this.widgets.dataTable.getTbodyEl().rows;
         for (var i = 0; i < rows.length; i++)
         {
            rows[i].cells[0].getElementsByTagName('input')[0].checked = true;
         }
      },

      /**
       * show 'Create Link' dialog
       * @method showCreateLinkDlg.
       */
      showCreateLinkDlg : function Links_showCreateLinkDlg()
      {
         /*this.widgets.createLinkDlg.setOptions(
         {
            onSuccess:
            {
               fn: this.createLink,
               scope: this
            },
            editMode: false,
            clearForm: true
         });
         this.widgets.createLinkDlg.show();*/
         var url = YAHOO.lang.substitute(Alfresco.constants.URL_CONTEXT + "page/site/{site}/links-linkedit?container={container}",
         {
            site: this.options.siteId,
            container: this.options.containerId
         });
         window.location = url;
      },

      /**
       * change list view
       * @method changeListView.
       */
      changeListView : function Links_changeListView()
      {
         var records = this.widgets.dataTable.getRecordSet().getRecords();
         var rows = this.widgets.dataTable.getTbodyEl().rows;
         var colDefinitions = this.widgets.dataTable.getColumnSet().getDefinitions();

         this.options.simpleView = !this.options.simpleView;
         var j = 0;
         for (var i in records)
         {
            colDefinitions[1].formatter.call(this, rows[j].cells[1].firstChild, records[i]);
            colDefinitions[2].formatter.call(this, rows[j].cells[2].firstChild, records[i]);
            j++;
         }
         this._adjustFilterHeight();
      },

      /**
       * Shows 'Edit link' dialog.
       * @method editLink.
       */
      showEditLinkDlg: function Links_showEditLinkDlg(row)
      {
         this.widgets.createLinkDlg.setOptions(
         {
            onSuccess:
            {
               fn: this.onUpdateLink,
               obj: row,
               scope: this
            },
            editMode: true,
            clearForm: true
         });
         this.widgets.createLinkDlg.show(row.getData());
      },

      /**
       * @method deleteLinks
       * @param arr {array}
       */
      deleteLinks: function Links_deleteLinks(arr)
      {
         var me = this;
         if (! this._setBusy(this._msg('message.wait')))
         {
            return;
         }

         // get the url to call
         var ids = new Array();
         for (var i in arr)
         {
            ids.push(arr[i].getData().name);
         }

         var url = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "api/links/delete/site/{site}/{container}",
         {
            site: this.options.siteId,
            container: this.options.containerId
         });

         // ajax request success handler
         var onDeletedSuccess = function Links_deleteLinkPostConfirm_onDeletedSuccess(response)
         {
            // remove busy message
            this._releaseBusy();

            // reload the table data
            this.updateLinks();
         };

         // execute ajax request
         Alfresco.util.Ajax.request(
         {
            url: url,
            method: "POST",
            requestContentType : "application/json",
            successMessage: this._msg("message.delete.success"),
            successCallback:
            {
               fn: onDeletedSuccess,
               scope: this
            },
            failureMessage: this._msg("message.delete.failure"),
            failureCallback:
            {
               fn: function(response)
               {
                  this._releaseBusy();
               },
               scope: this
            },
            dataObj :
            {
               items : ids
            }
         });

      },

      /**
       * Adds the link.
       *
       * @param rowData {object} the row's data.
       * @method createLink.
       */
      createLink: function Links_createLink(data)
      {
         this.updateLinks({ page: 1 });
      },

      /**
       * Updates the link.
       *
       * @param rowData {object} the row's data.
       * @param row {YAHOO.widget.Record}.
       * @method updateLink.
       */
      onUpdateLink: function Links_onUpdateLink(rowData, row)
      {
         this.updateLinks();

      },

      /**
       * Show delete confirm dialog.
       * @param row {YAHOO.widget.Record} the row which needs for delete.
       */
      showConfirmDialog: function Links_showConfirmDialog(mes, callback)
      {
         var me = this;

         var prompt = Alfresco.util.PopupManager.displayPrompt(
         {
            text: mes,
            buttons: [
               {
                  text: this._msg("button.delete"),
                  handler: function()
                  {
                     callback();
                     this.destroy();
                  },
                  isDefault: true
               },
               {
                  text: this._msg("button.cancel"),
                  handler: function()
                  {
                     this.destroy();
                  }
               }]
         });
         if (YAHOO.env.ua.ie)
         {
            prompt.element.style.width = "300px";
            prompt.underlay.style.width = "300px";
            prompt.center();
         }
      },

      /**
       * Gets the array of selected links.
       *
       * @method getSelectedLinks
       */
      getSelectedLinks: function Links_getSelectedLinks()
      {
         var arr = [];
         var rows = this.widgets.dataTable.getTbodyEl().rows;
         for (var i = 0; i < rows.length; i++)
         {
            if (rows[i].cells[0].getElementsByTagName('input')[0].checked)
            {
               var data = this.widgets.dataTable.getRecord(i);
               if (data)arr.push(data);
            }
         }

         return arr;
      },

      /**
       * PRIVATE FUNCTIONS
       */

      /**
       * activation of resize
       * @method _attachResize.
       */
      _attachResize : function Links__attachResize()
      {
         var me = this;
         this.widgets.horizResize = new YAHOO.util.Resize("divLinkFilters",
         {
            handles: ["r"],
            minWidth: this.options.MIN_FILTER_PANEL_WIDTH,
            maxWidth: this.options.MAX_FILTER_PANEL_WIDTH
         });

         var resizeCovers = function()
         {
            var leftWidth = me.widgets.horizResize._handles['r'].offsetLeft + 10;
            me.leftCover.style.width = leftWidth + "px";
            me.rightCover.style.left = (leftWidth + 5) + "px";
            me.rightCover.style.width = (Dom.get("doc3").offsetWidth - leftWidth + 15) + "px";
            me.leftCover.style.height = me.rightCover.style.height = (Dom.get("ft").offsetHeight + Dom.get("doc3").offsetHeight) + "px";
         }

         this.widgets.horizResize.on("startResize",
               function()
               {
                  document.body.appendChild(me.leftCover);
                  document.body.appendChild(me.rightCover);
                  resizeCovers();
               }, this, true);

         this.widgets.horizResize.on("resize",
               function(eventTarget)
               {
                  resizeCovers();
                  this.onTopicListResize(eventTarget.width);
               }, this, true);

         this.widgets.horizResize.on("endResize",
               function()
               {
                  document.body.removeChild(me.leftCover);
                  document.body.removeChild(me.rightCover);
                  me._adjustFilterHeight();
               }, this, true);

         document.body.onresize = function()
         {
            me._adjustFilterHeight();
         }
         this.widgets.horizResize.resize(null, null, this.options.MIN_FILTER_PANEL_WIDTH, 0, 0, true);
      },

      /**
       * Generate ID alias for tag, suitable for DOM ID attribute
       *
       * @method _generateTagId
       * @param tagName {string} Tag name
       * @return {string} A unique DOM-safe ID for the tag
       */
      _generateTagId : function Links__generateTagId(tagName)
      {
         var id = 0;
         var tagId = this.tagId;
         if (tagName in tagId.tags) {
            id = tagId.tags[tagName];
         }
         else
         {
            tagId.id++;
            id = tagId.tags[tagName] = tagId.id;
         }
         return this.id + "-tagId-" + id;
      },

      /**
       * Adjusts the height of filter panel.
       *
       * @method _adjustFilterHeight
       */
      _adjustFilterHeight: function Links__adjustFilterHeight()
      {
         var h = Dom.get("ft").parentNode.offsetTop - Dom.get("hd").offsetHeight;
         if (YAHOO.env.ua.ie == 6)
         {
            var hd = Dom.get("hd"), tmpHeight = -5;
            tmpHeight += hd.childNodes[0].offsetHeight;
            tmpHeight += hd.childNodes[1].offsetHeight;
            tmpHeight += hd.childNodes[2].offsetHeight;
            h = Dom.get("ft").parentNode.offsetTop - tmpHeight; 
         }
         if (this.options.MAX_FILTER_PANEL_HEIGHT > h)
         {
            h = this.options.MAX_FILTER_PANEL_HEIGHT;
         }
         this.widgets.horizResize._handles['r'].style.height = h + 'px';
      },

      /**Build URI parameter string for doclist JSON data webscript
       *
       * @method _buildDocListParams
       * @param p_obj.page {string} Page number
       * @param p_obj.pageSize {string} Number of items per page
       */
      _buildLinksParams: function Links_buildLinksParams(p_obj)
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
         if (filterOwner == "Alfresco.LinkFilter")
         {
            // latest only
            switch (filterId)
            {
               case "all":url = "?filter=all";break;
               case "internal":url = "?filter=internal";break;
               case "www":url = "?filter=www";break;
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
      _msg:function Links_msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, this.name, Array.prototype.slice.call(arguments).slice(1));
      },

      /**
       * Init the 'Create Link' dialog.
       *
       * @method _initCreateLinkDialog.
       */
      _initCreateLinkDialog: function Links_initCreateLinkDialog()
      {
         this.widgets.createLinkDlg = new Alfresco.LinksEditDialog(this.id + "-editdlg");
         this.widgets.createLinkDlg.setOptions(
         {
            siteId:this.options.siteId,
            containerId:this.options.containerId,
            templateUrl: Alfresco.constants.URL_SERVICECONTEXT + "components/links/modaldialogs/edit-dialog"
         });
         this.widgets.createLinkDlg.init();
      },

      /**
       * Removes the busy message and marks the component as non-busy
       */
      _releaseBusy: function Links_releaseBusy()
      {
         if (this.busy)
         {
            this.widgets.busyMessage.destroy();
            this.busy = false;
            return true;
         }
         else
         {
            return false;
         }
      },

      /**
       * Displays the provided busyMessage but only in case
       * the component isn't busy set.
       *
       * @return true if the busy state was set, false if the component is already busy
       */
      _setBusy: function Links__setBusy(busyMessage)
      {
         if (this.busy)
         {
            return false;
         }
         this.busy = true;
         this.widgets.busyMessage = Alfresco.util.PopupManager.displayMessage(
         {
            text: busyMessage,
            spanClass: "wait",
            displayTime: 0
         });
         return true;
      },

      /**
       * Generate the html markup for a tag link.
       *
       * @method _generateTagLink
       * @param tagName {string} the tag to create a link for
       * @return {string} the markup for a tag
       */
      _generateTagLink : function Links_generateTagLink(tagName)
      {
         var encodedTagName = $html(tagName);
         var html = '';
         html += '<span id="' + this._generateTagId(tagName) + '" class="nodeAttrValue">';
         html += '<a href="#" class="tag-link" title="' + encodedTagName + '">';
         html += '<span>' + encodedTagName + '</span>';
         html += '</a>';
         html += '</span>';
         return html;
      },

            /**
       * Generates the HTML mark-up for the RSS feed link
       *
       * @method _generateRSSFeedUrl
       * @private
       */
      _generateRSSFeedUrl: function Links__generateRSSFeedUrl()
      {
         var divFeed = Dom.get(this.id + "-rssFeed");
         if (divFeed)
         {
            var url = YAHOO.lang.substitute(Alfresco.constants.URL_CONTEXT + "service/components/links/rss?site={site}&amp;container={container}",
            {
               site: this.options.siteId,
               container: this.options.containerId
            });
            divFeed.innerHTML = '<a href="' + url + '">' + this._msg("header.rssFeed") + '</a>';
         }
      }
   }
})();