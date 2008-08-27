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
 * DiscussionsTopicList component.
 * 
 * @namespace Alfresco
 * @class Alfresco.DiscussionsTopicList
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
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML;
    
    
   /**
    * DiscussionsTopicList constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.DiscussionsTopicList} The new DiscussionsTopicList instance
    * @constructor
    */
   Alfresco.DiscussionsTopicList = function(htmlId)
   {
      /* Mandatory properties */
      this.name = "Alfresco.DiscussionsTopicList";
      this.id = htmlId;
      
      /* Initialise prototype properties */
      this.currentFilter = {};
      this.widgets = {};
      this.modules = {};
      this.tagId =
      {
         id: 0,
         tags: {}
      };
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "dom", "datasource", "datatable", "event", "element"], this.onComponentsLoaded, this);
      
      /* Decoupled event listeners */
      YAHOO.Bubbling.on("tagSelected", this.onTagSelected, this);
      YAHOO.Bubbling.on("filterChanged", this.onFilterChanged, this);
      YAHOO.Bubbling.on("topiclistRefresh", this.onDiscussionsTopicListRefresh, this);
      
      return this;
   }
   
   Alfresco.DiscussionsTopicList.prototype =
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
          * Current containerId.
          * 
          * @property containerId
          * @type string
          */    
         containerId: "discussions",

         /**
          * Initially used filter name and id.
          */
         initialFilter: {
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
         maxContentLength: 512
      },
      
      /**
       * Current filter to filter topic list.
       * 
       * @property currentFilter
       * @type object
       */
      currentFilter: null,
      
      /**
       * Object container for storing YUI widget instances.
       * 
       * @property widgets
       * @type object
       */
      widgets : null,
      
      /**
       * Object container for storing module instances.
       * 
       * @property modules
       * @type object
       */
      modules: null,
      
      /**
       * Object literal used to generate unique tag ids
       * 
       * @property tagId
       * @type object
       */
      tagId: null,
      
      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       */
      setOptions: function DiscussionsTopicList_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },
      
      /**
       * Set messages for this component.
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {Alfresco.DocumentList} returns 'this' for method chaining
       */
      setMessages: function DiscussionsTopicList_setMessages(obj)
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
      onComponentsLoaded: function DiscussionsTopicList_onComponentsLoaded()
      {
         YAHOO.util.Event.onContentReady(this.id, this.onReady, this, true);
      },
   
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function DiscussionsTopicList_onReady()
      {
         // Reference to self used by inline functions
         var me = this;
         
         // Create new post button
         this.widgets.createPost = Alfresco.util.createYUIButton(this, "createTopic-button", this.onCreateTopic,
         {
         });
         
         // initialize rss feed link
         this._generateRSSFeedUrl();
         
         // Simple view button
         this.widgets.simpleView = Alfresco.util.createYUIButton(this, "simpleView-button", this.onSimpleView,
         {
            type: "checkbox",
            checked: this.options.simpleView
         });
         
         // YUI Paginator definition
         this.widgets.paginator = new YAHOO.widget.Paginator(
         {
            containers: [this.id + "-paginator"],
            rowsPerPage: this.options.pageSize,
            initialPage: 1,
            template: this._msg("pagination.template"),
            pageReportTemplate: this._msg("pagination.template.page-report")
         });

         // Hook action events for details view
         var fnActionHandlerDiv = function DiscussionsTopicList_fnActionHandlerDiv(layer, args)
         {
            var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "div");
            if (owner !== null)
            {
               var action = owner.className;
               var target = args[1].target;
               if (typeof me[action] == "function")
               {
                  me[action].call(me, target.offsetParent, owner);
                  args[1].stop = true;
               }
            }
      		 
            return true;
         }
         YAHOO.Bubbling.addDefaultAction("topic-action-link-div", fnActionHandlerDiv);
         
         // Hook action events for simple view
         var fnActionHandlerSpan = function DiscussionsTopicList_fnActionHandlerSpan(layer, args)
         {
            var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "span");
            if (owner !== null)
            {
               var action = owner.className;
               var target = args[1].target;
               if (typeof me[action] == "function")
               {
                  me[action].call(me, target.offsetParent, owner);
                  args[1].stop = true;
               }
            }
      		 
            return true;
         }
         YAHOO.Bubbling.addDefaultAction("topic-action-link-span", fnActionHandlerSpan);
         
         // register tag action handler, which will issue tagSelected bubble events.
         Alfresco.util.tags.registerTagActionHandler(this);

         // DataSource definition
         var uriDiscussionsTopicList = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "api/forum/site/{site}/{container}/posts",
         {
            site: this.options.siteId,
            container: this.options.containerId
         });
         this.widgets.dataSource = new YAHOO.util.DataSource(uriDiscussionsTopicList);
         this.widgets.dataSource.responseType = YAHOO.util.DataSource.TYPE_JSON;
         this.widgets.dataSource.connXhrMode = "queueRequests";
         this.widgets.dataSource.responseSchema =
         {
            resultsList: "items",
            fields:
            [ "name", "totalReplyCount", "lastReplyOn", "lastReplyBy", "tags", "url", "repliesUrl",
              "nodeRef", "title", "createdOn", "modifiedOn", "isUpdated", "updatedOn",
              "author", "content", "replyCount", "permissions"
            ],
            metaFields:
            {
               paginationRecordOffset: "startIndex",
               totalRecords: "total",
               forumPermissions: "forumPermissions"
            }
         };
         
         var generateTopicActions = function(me, data, tagName)
         {
            var html = '';
            // begin actions
            html += '<div class="nodeEdit">';
            html += '<' + tagName + ' class="onViewTopic"><a href="#" class="topic-action-link-' + tagName + '">' + me._msg("action.view") + '</a></' + tagName + '>';   
            /*if (data.permissions.edit)
            {
               html += '<' + tagName + ' class="onEditTopic"><a href="#" class="topic-action-link-' + tagName + '">' + me._msg("action.edit") + '</a></' + tagName + '>';
            }*/
            if (data.permissions['delete'])
            {
               html += '<' + tagName + ' class="onDeleteTopic"><a href="#" class="topic-action-link-' + tagName + '">' + me._msg("action.delete") + '</a></' + tagName + '>';
            }
            html += '</div>';
            return html;
         };        
         
         /**
          * Topic element renderer. We use the data table as a one-column table, this renderer
          * thus renders the complete element.
          *
          * @method renderTopic
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         var renderTopic = function DiscussionsTopicList_renderTopic(elCell, oRecord, oColumn, oData)
         {
            // hide the parent temporarily as we first insert the structure and then the content
            // to avoid problems caused by broken xhtml
            Dom.addClass(elCell, 'hidden');
             
            // precalculate some values
            var data = oRecord.getData();
            var topicViewUrl = Alfresco.util.discussions.getTopicViewPage(me.options.siteId, me.options.containerId, data.name);
            var authorLink = Alfresco.util.people.generateUserLink(data.author);
            
            var html = "";
            
            // detailed view
            if (! me.options.simpleView)
            {
               html += '<div class="node topic">';

               // actions
               html += generateTopicActions(me, data, 'div');
   
               // begin view
               html += '<div class="nodeContent">';
               html += '<span class="nodeTitle"><a href="' + topicViewUrl + '">' + $html(data.title) + '</a> ';
               if (data.isUpdated)
               {
                  html += '<span class="nodeStatus">(' + me._msg("post.updated") + ')</span>';
               }
               html += '</span>';
               html += '<div class="published">';
               html += '<span class="nodeAttrLabel">' + me._msg("post.createdOn") + ': </span>';
               html += '<span class="nodeAttrValue">' + Alfresco.util.formatDate(data.createdOn) + '</span>';
               html += '<span class="spacer"> | </span>';
               html += '<span class="nodeAttrLabel">' + me._msg("post.author") + ': </span>';
               html += '<span class="nodeAttrValue">' + authorLink + '</span>';
               html += '<br />';
               if (data.lastReplyBy)
               {
                  html += '<span class="nodeAttrLabel">' + me._msg("post.lastReplyBy") + ': </span>';
                  html += '<span class="nodeAttrValue">' + Alfresco.util.people.generateUserLink(data.lastReplyBy) + '</span>';                  
                  html += '<span class="spacer"> | </span>';
                  html += '<span class="nodeAttrLabel">' + me._msg("post.lastReplyOn") + ': </span>';
                  html += '<span class="nodeAttrValue">' + Alfresco.util.formatDate(data.lastReplyOn) + '</span>';
               }
               else
               {
                  html += '<span class="nodeAttrLabel">' + me._msg("replies.label") + ': </span>';
                  html += '<span class="nodeAttrValue">' + me._msg("replies.noReplies") + '</span>';                  
               }
               html += '</div>';
               
               html += '<div class="userLink">' + authorLink + ' ' + me._msg("said") + ':</div>';
               html += '<div class="content yuieditor"></div>';
               html += '</div>'
               // end view

               html += '</div>';

               // begin footer
               html += '<div class="nodeFooter">';
               html += '<span class="nodeAttrLabel replyTo">' + me._msg("replies.label") + ': </span>';
               html += '<span class="nodeAttrValue">(' + data.totalReplyCount + ')</span>';
               html += '<span class="spacer"> | </span>';
               
               html += '<span class="nodeAttrValue"><a href="' + topicViewUrl + '">' + me._msg("action.read") + '</a></span>';
               html += '<span class="spacer"> | </span>';
               
               html += '<span class="nodeAttrLabel tag">' + me._msg("tags.label") +': </span>';
               if (data.tags.length > 0)
               {
                  for (var x=0; x < data.tags.length; x++)
                  {
                     if (x > 0)
                     {
                        html += ", ";
                     }
                     html += Alfresco.util.tags.generateTagLink(me, data.tags[x]);
                  }
               }
               else
               {
                  html += '<span class="nodeAttrValue">' + me._msg("tags.noTags") + '</span>';
               }
               html += '</div></div>';
               // end
            }
            
            // simple view
            else
            {
               // add a class to the parent div so that we can add a separator line in the simple view
               Dom.addClass(elCell, 'row-separator');
                
               html += '<div class="node topic simple">';
               
               // begin actions
               html += generateTopicActions(me, data, 'span');
   
               // begin view
               html += '<div class="nodeContent">';
               html += '<span class="nodeTitle"><a href="' + topicViewUrl + '">' + $html(data.title) + '</a> ';
               if (data.isUpdated)
               {
                  html += '<span class="nodeStatus">(' + me._msg("post.updated") + ')</span>';
               }
               html += '</div>';
               html += '</div>';
            }
             
            // assign html        
            elCell.innerHTML = html;
            
            // finally add the content. We do this here to avoid a broken page layout, as
            // data.content isn't valid xhtml.
            if (! me.options.simpleView)
            {
               var contentElem = Dom.getElementsByClassName("content", "div", elCell);
               if (contentElem.length == 1)
               {
                  contentElem[0].innerHTML = data.content
               }
            }
            
            // now show the element
            Dom.removeClass(elCell, 'hidden');
         }

         // DataTable column defintions
         var columnDefinitions = [
         {
            key: "topics", label: "Topics", sortable: false, formatter: renderTopic
         }];

         // Temporary "empty datatable" message
         YAHOO.widget.DataTable.MSG_EMPTY = this._msg("message.loading");

         // called by the paginator on state changes
         var handlePagination = function DL_handlePagination(state, dt)
         {
            //me.currentPage = state.page;
            me._updateDiscussionsTopicList({ page : state.page });
         }
         
         // DataTable definition
         this.widgets.dataTable = new YAHOO.widget.DataTable(this.id + "-topiclist", columnDefinitions, this.widgets.dataSource,
         {
            initialLoad: false,
            paginationEventHandler: handlePagination,
            paginator: this.widgets.paginator
         });
         
         // Custom error messages
         this._setDefaultDataTableErrors();

         // Hook tableMsgShowEvent to clear out fixed-pixel width on <table> element (breaks resizer)
         this.widgets.dataTable.subscribe("tableMsgShowEvent", function(oArgs)
         {
            // NOTE: Scope needs to be DataTable
            this._elMsgTbody.parentNode.style.width = "";
         });
         
         // Override abstract function within DataTable to set custom error message
         this.widgets.dataTable.doBeforeLoadData = function DiscussionsTopicList_doBeforeLoadData(sRequest, oResponse, oPayload)
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
            else if (oResponse.results && !me.options.usePagination)
            {
               this.renderLoopSize = oResponse.results.length >> (YAHOO.env.ua.gecko) ? 3 : 5;
            }
            
            // adapt the toolbar if we got permissions
            if (oResponse.meta.forumPermissions)
            {
               me._updateToolbar(oResponse.meta.forumPermissions);
            }
            
            // Must return true to have the "Loading..." message replaced by the error message
            return true;
         }
         
         // Enable row highlighting
         this.widgets.dataTable.subscribe("rowMouseoverEvent", this.onEventHighlightRow, this, true);
         this.widgets.dataTable.subscribe("rowMouseoutEvent", this.onEventUnhighlightRow, this, true);
         
         // issue a filterChanged bubble event to load the list and to
         // update the other components on the page
         var filterObj = YAHOO.lang.merge(
         {
            filterId: "new",
            filterOwner: "Alfresco.DiscussionsTopicListFilter",
            filterData: null
         }, this.options.initialFilter);
         YAHOO.Bubbling.fire("filterChanged", filterObj);
      },
      
      /**
       * Updates the toolbar using the provided forumPermissions.
       */
      _updateToolbar: function DiscussionsTopicList__updateToolbar(forumPermissions)
      {
         if (forumPermissions.create)
         {
            var elem = Dom.get(this.id + '-create-topic-container');
            Dom.removeClass(elem, 'hidden');
         }
      },
      
      /**
       * Generates the HTML mark-up for the RSS feed link
       *
       * @method _generateRSSFeedUrl
       * @private
       */
      _generateRSSFeedUrl: function DiscussionsTopicList__generateRSSFeedUrl()
      {
         var divFeed = Dom.get(this.id + "-rssFeed");
         if (divFeed)
         {
            var url = Alfresco.constants.URL_CONTEXT + "service/components/discussions/rss?site=" + this.options.siteId;
            divFeed.innerHTML = '<a href="' + url + '">' + this._msg("header.rssFeed") + '</a>';
         }
      },
      
      /**
       * Action handler for the create post button
       */
      onCreateTopic: function DiscussionsTopicList_onCreateTopic(e, p_obj)
      {
         var url = YAHOO.lang.substitute(Alfresco.constants.URL_CONTEXT + "page/site/{site}/discussions-createtopic?container={container}",
         {
            site: this.options.siteId,
            container: this.options.containerId
         });
         window.location = url;
         Event.preventDefault(e);
      },
      
      /**
       * Action handler for the simple view toggle button
       */
      onSimpleView: function DiscussionsTopicList_onSimpleView(e, p_obj)
      {
         this.options.simpleView = !this.options.simpleView;
         p_obj.set("checked", this.options.simpleView);

         // update the list
         YAHOO.Bubbling.fire("topiclistRefresh");
         Event.preventDefault(e);
      },
      
      /**
       * Handler for the view topic action links
       *
       * @method onActionDelete
       * @param row {object} DataTable row representing file to be actioned
       */
      onViewTopic: function DiscussionsTopicList_onViewTopic(row)
      {
         var record = this.widgets.dataTable.getRecord(row);
         window.location = Alfresco.util.discussions.getTopicViewPage(this.options.siteId, this.options.containerId, record.getData('name'));
      },

      /**
       * Handler for the edit topic action links
       */
      onEditTopic: function DiscussionsTopicList_onEditTopic(row)
      {
         var record = this.widgets.dataTable.getRecord(row);
         var url = YAHOO.lang.substitute(Alfresco.constants.URL_CONTEXT + "page/site/{site}/discussions-topicview?container={container}&topicId={topicId}",
         {
            site: this.options.siteId,
            container: this.options.containerId,
            topicId: record.getData('name')
         });
         window.location = url;
      },
      
      /**
       * Tag selected handler (document details)
       *
       * @method onTagSelected
       * @param tagId {string} Tag name.
       * @param target {HTMLElement} Target element clicked.
       */
      onTagSelected: function DiscussionsTopicList_onTagSelected(layer, args)
      {
         var obj = args[1];
         if (obj && (obj.tagName !== null))
         {
            var filterObj = {
               filterId: obj.tagName,
               filterOwner: "Alfresco.DiscussionsTopicListTags",
               filterData: null
            };
            YAHOO.Bubbling.fire("filterChanged", filterObj);
         }
      },
      
      /**
       * Handler for the delete topic action links.
       */
      onDeleteTopic: function DiscussionsTopicList_onDeleteTopic(row)
      {
         var record = this.widgets.dataTable.getRecord(row);
         var me = this;
         Alfresco.util.PopupManager.displayPrompt(
         {
            text: this._msg("message.confirm.delete", $html(record.getData('title'))),
            buttons: [
            {
               text: this._msg("button.delete"),
               handler: function DiscussionsTopicList_onDeleteTopic_delete()
               {
                  this.destroy();
                  me._deleteTopicConfirm.call(me, record.getData('name'));
               },
               isDefault: true
            },
            {
               text: this._msg("button.cancel"),
               handler: function DiscussionsTopicList_onDeleteTopic_cancel()
               {
                  this.destroy();
               }
            }]
         });
      },
      
      /**
       * Delete a topic.
       * 
       * @param topicId {string} the id of the topic to delete
       */
      _deleteTopicConfirm: function DiscussionsTopicList__deleteTopicConfirm(topicId)
      {
         // ajax request success handler
         var onDeleted = function DiscussionsTopicList__deleteTopic_onDeleted(response)
         {
            // reload the table
            this._updateDiscussionsTopicList();
         };
          
         // construct the url to call
         var url = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "api/forum/post/site/{site}/{container}/{topicId}",
         {
            site: this.options.siteId,
            container: this.options.containerId,
            topicId: topicId
         });
         
         // execute the request
         Alfresco.util.Ajax.request(
         {
            url: url,
            method: "DELETE",
            responseContentType : "application/json",
            successMessage: this._msg("message.delete.success"),
            successCallback:
            {
               fn: onDeleted,
               scope: this
            },
            failureMessage: this._msg("message.delete.failure")
         });
      },
      
      /**
       * Custom event handler to highlight row.
       *
       * @method onEventHighlightRow
       * @param oArgs.event {HTMLEvent} Event object.
       * @param oArgs.target {HTMLElement} Target element.
       */
      onEventHighlightRow: function DiscussionsTopicList_onEventHighlightRow(oArgs)
      {
         // only highlight if we got actions to show
         var record = this.widgets.dataTable.getRecord(oArgs.target);
         var permissions = record.getData('permissions');
         if (! (permissions.edit || permissions["delete"]))
         {
            return;
         }
         
         var target = oArgs.target;
         var elem = YAHOO.util.Dom.getElementsByClassName('topic', null, target, null);
         YAHOO.util.Dom.addClass(elem, 'overNode');
         var editBlock = YAHOO.util.Dom.getElementsByClassName('nodeEdit', null, target, null);
         YAHOO.util.Dom.addClass(editBlock, 'showEditBlock');
      },

      /**
       * Custom event handler to unhighlight row.
       *
       * @method onEventUnhighlightRow
       * @param oArgs.event {HTMLEvent} Event object.
       * @param oArgs.target {HTMLElement} Target element.
       */
      onEventUnhighlightRow: function DiscussionsTopicList_onEventUnhighlightRow(oArgs)
      {
         var target = oArgs.target;
         var elem = YAHOO.util.Dom.getElementsByClassName('topic', null, target, null);
         YAHOO.util.Dom.removeClass(elem, 'overNode');
         var editBlock = YAHOO.util.Dom.getElementsByClassName('nodeEdit', null, target, null);
         YAHOO.util.Dom.removeClass(editBlock, 'showEditBlock');
      },
      
      /**
       * DiscussionsTopicList View Filter changed event handler
       *
       * @method onFilterChanged
       * @param layer {object} Event fired (unused)
       * @param args {array} Event parameters (new filterId)
       */
      onFilterChanged: function DiscussionsTopicList_onFilterChanged(layer, args)
      {
         var obj = args[1];
         if ((obj !== null) && (obj.filterId !== null))
         {
            // Should be a filterId in the arguments
            this.currentFilter =
            {
               filterId: obj.filterId,
               filterOwner: obj.filterOwner,
               filterData: obj.filterData
            };
            this._updateDiscussionsTopicList({ page: 1 });
         }
      },
      
      /**
       * Deactivate All Controls event handler
       *
       * @method onDeactivateAllControls
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onDeactivateAllControls: function DiscussionsTopicList_onDeactivateAllControls(layer, args)
      {
         for (widget in this.widgets)
         {
            this.widgets[widget].set("disabled", true);
         }
      },
      
      /**
       * Update the list title.
       */
      updateListTitle: function DiscussionsTopicList_updateListTitle()
      {
         var elem = Dom.get(this.id + '-listtitle');
         var title = this._msg("title.generic");

         var filterOwner = this.currentFilter.filterOwner;
         var filterId = this.currentFilter.filterId;
         var filterData = this.currentFilter.filterData;
         if (filterOwner == "Alfresco.TopicListFilter")
         {
            if (filterId == "new")
            {
                title = this._msg("title.newtopics");
            }
            if (filterId == "hot")
            {
               title = this._msg("title.hottopics");
            }
            else if (filterId == "all")
            {
               title = this._msg("title.alltopics");
            }
            else if (filterId == "mine")
            {
               title = this._msg("title.mytopics");
            }
         }
         else if (filterOwner == "Alfresco.TopicListTags")
         {
            title = this._msg("title.bytag", $html(filterData));
         }
         
         elem.innerHTML = title;
      },

      /**
       * DiscussionsTopicList Refresh Required event handler
       *
       * @method onDiscussionsTopicListRefresh
       * @param layer {object} Event fired (unused)
       * @param args {array} Event parameters (unused)
       */
      onDiscussionsTopicListRefresh: function DiscussionsTopicList_onDiscussionsTopicListRefresh(layer, args)
      {
         this._updateDiscussionsTopicList({});
      },

      /**
       * Gets a custom message
       *
       * @method _msg
       * @param messageId {string} The messageId to retrieve
       * @return {string} The custom message
       * @private
       */
      _msg: function DiscussionsTopicList_msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, "Alfresco.DiscussionsTopicList", Array.prototype.slice.call(arguments).slice(1));
      },

      /**
       * Resets the YUI DataTable errors to our custom messages
       * NOTE: Scope could be YAHOO.widget.DataTable, so can't use "this"
       *
       * @method _setDefaultDataTableErrors
       */
      _setDefaultDataTableErrors: function DiscussionsTopicList__setDefaultDataTableErrors()
      {
         var msg = Alfresco.util.message;
         YAHOO.widget.DataTable.MSG_EMPTY = msg("message.empty", "Alfresco.DiscussionsTopicList");
         YAHOO.widget.DataTable.MSG_ERROR = msg("message.error", "Alfresco.DiscussionsTopicList");
      },
      
      /**
       * Updates topic list by calling data webscript with current site and filter information
       *
       * @method _updateDiscussionsTopicList
       */
      _updateDiscussionsTopicList: function DiscussionsTopicList__updateDiscussionsTopicList(p_obj)
      {
         // Reset the custom error messages
         this._setDefaultDataTableErrors();
         
         var successHandler = function DiscussionsTopicList__updateDiscussionsTopicList_successHandler(sRequest, oResponse, oPayload)
         {
            //this.currentPath = successPath;
            this.widgets.dataTable.onDataReturnInitializeTable.call(this.widgets.dataTable, sRequest, oResponse, oPayload);
            this.updateListTitle();
         }
         
         var failureHandler = function DiscussionsTopicList__updateDiscussionsTopicList_failureHandler(sRequest, oResponse)
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
                     // Site or container not found - deactivate controls
                     YAHOO.Bubbling.fire("deactivateAllControls");
                  }
               }
               catch(e)
               {
                  this._setDefaultDataTableErrors();
               }
            }
         }
         
         // get the url to call
         this.widgets.dataSource.sendRequest(this._buildParams(p_obj || {}),
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
       * @param p_obj.page {string} Page number
       * @param p_obj.pageSize {string} Number of items per page
       */
      _buildParams: function DiscussionsTopicList__buildParams(p_obj)
      {
         var params = {
            contentLength: this.options.maxContentLength,
            tag: null,
            
            page: this.widgets.paginator.get("page") || "1",
            pageSize: this.widgets.paginator.get("rowsPerPage")
         }
         
         // Passed-in overrides
         if (typeof p_obj == "object")
         {
            params = YAHOO.lang.merge(params, p_obj);
         }

         // add the pageSize param
         params.startIndex = (params.page-1) * params.pageSize;

         // check what url to call and with what parameters
         var filterOwner = this.currentFilter.filterOwner;
         var filterId = this.currentFilter.filterId;
         var filterData = this.currentFilter.filterData;       
         
         // check whether we got a filter or not
         var url = "";
         if (filterOwner == "Alfresco.TopicListFilter")
         {
            // latest only
            if (filterId == "all")
            {
                url = "";
            }
            if (filterId == "new")
            {
                url = "/new";
            }
            else if (filterId == "hot")
            {
                url = "/hot"
            }
            else if (filterId == "mine")
            {
                url = "/myposts"
            }
         }
         else if (filterOwner == "Alfresco.TopicListTags")
         {
            params.tag = filterData;
         }
         
         // build the url extension
         var urlExt = "";
         for (paramName in params)
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
         return url + "?" + urlExt;
      }
   };
})();