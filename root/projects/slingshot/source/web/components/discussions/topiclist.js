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
    * DiscussionsTopicList constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.DiscussionsTopicList} The new DiscussionsTopicList instance
    * @constructor
    */
   Alfresco.DiscussionsTopicList = function(htmlId)
   {
      this.name = "Alfresco.DiscussionsTopicList";
      this.id = htmlId;
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "menu", "container"], this.onComponentsLoaded, this);
      
      // Decoupled event listeners
      YAHOO.Bubbling.on("onSetTopicListParams", this.onSetTopicListParams, this);
      YAHOO.Bubbling.on("onStartIndexChanged", this.onStartIndexChanged, this);
      
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
         
         containerId: "discussions",
         
         path: "",
         
         /**
          * Start index of displayed elements
          */
         startIndex: 0,
         
         /**
          * Size of elements to show
          */
         pageSize: 10,
         
         filter: "",
         
         tag: "",
         
         viewmode: "",
         
         simpleView: false
      },
      
      widgets: {},
      
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
      
      setMessages: function DiscussionsTopic_setMessages(obj)
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
         // Create new post button
         this.widgets.createPost = Alfresco.util.createYUIButton(this, "createTopic-button", this.onCreateTopic,
         {
         });

         // Simple view button
         this.widgets.fileSelect = Alfresco.util.createYUIButton(this, "simpleView-button", this.onSimpleView,
         {
            type: "checkbox",
            checked: this.options.simpleView
         });
         
         // Hook action events
         Alfresco.util.registerDefaultActionHandler(this.id, "action-link-div", "div", this);
         Alfresco.util.registerDefaultActionHandler(this.id, "action-link-span", "span", this);

         Alfresco.util.registerDefaultActionHandler(this.id, "tag-link-span", "span", this);
         
         // initialize the mouse over listener
         Alfresco.util.rollover.registerHandlerFunctions(this.id, this.onListElementMouseEntered, this.onListElementMouseExited);
         
         // attach the listener to the already rendered list elements
         Alfresco.util.rollover.registerListenersByClassName(this.id, 'topic', 'div');
         
         // tell the filters in what state the list initially loaded
         YAHOO.Bubbling.fire('topicListParamsChanged', {
            filter: this.options.filter,
            tag: this.options.tag
         });
      },
      
      
      // Action handlers

      /**
       * Action handler for the create topic button
       */
      onCreateTopic: function DiscussionsTopicList_onCreateTopic(e, p_obj)
      {
         Alfresco.util.blog.loadForumPostCreatePage(this.options.siteId, this.options.containerId, this.options.path);
         Event.preventDefault(e);
      },

      /**
       * Action handler for the view action
       */
      onViewNode: function DiscussionsTopicList_onViewTopic(htmlId, ownerId, param)
      {
         Alfresco.util.discussions.loadForumPostViewPage(this.options.siteId, this.options.containerId, this.options.path, param);
      },
      
      /**
       * Action handler for the edit action
       */
      onEditNode: function DiscussionsTopicList_onViewTopic(htmlId, ownerId, param)
      {
         Alfresco.util.discussions.loadForumPostEditPage(this.options.siteId, this.options.containerId, this.options.path, param);             
      },
      
      /**
       * Action handler for the delete action
       */
      onDeleteNode: function DiscussionsTopic_onDeleteTopic(htmlId, ownerId, param)
      {
         this._deleteNode(param);
      },
      
      /**
       * Handles the click on a tag
       */
      onTagSelection: function DiscussionsTopicList_onTagSelected(htmlId, ownerId, param)
      {
         YAHOO.Bubbling.fire('onSetTopicListParams', {tag : param});
      },
      
      onSimpleView: function DiscussionsTopicList_onSimpleView(e, p_obj)
      {
         this.options.simpleView = !this.options.simpleView;
         p_obj.set("checked", this.options.simpleView);

         // PENDING: cleanup
         if (this.options.simpleView)
         {
            this.options.viewmode = "simple";
         }
         else
         {
            this.options.viewmode = "details";
         }
         
         YAHOO.Bubbling.fire('onSetTopicListParams', { "viewmode" : this.options.viewmode });
         
         //YAHOO.Bubbling.fire("doclistRefresh");
         Event.preventDefault(e);
      },
      
      // Actions functionality
      
      _deleteNode: function(topicId)
      {
         // make an ajax request to delete the topic
         var url = Alfresco.util.discussions.getTopicRestUrl(this.options.siteId,
                        this.options.containerId, this.options.path, topicId);
         url += "?site=" + this.options.siteId;
         url += "&container=" + this.options.containerId;
         Alfresco.util.Ajax.request(
         {
            url: url,
            method: "DELETE",
            responseContentType : "application/json",
            successCallback:
            {
               fn: this._onDeleted,
               scope: this
            },
            failureMessage: this._msg("topic.msg.failedDelete")
         });
      },

      _onDeleted: function DiscussionsTopic__onDeleted(response)
      {
         if (response.json.error == undefined)
         {
            // reload the list
            this._reloadData();
            Alfresco.util.PopupManager.displayMessage({text: this._msg("topic.msg.deleted")});
         }
         else
         {
            Alfresco.util.PopupManager.displayMessage({text: this._msg("topic.msg.unableDelete", response.json.error)});
         }
      },
      
      
      // List event handlers
      
      /**
       * Handles paginator events
       */
      onStartIndexChanged: function Discussions_onPageIndexChanged(layer, args)
      {
         this.options.pageSize = args[1].pageSize;
         this.options.startIndex = args[1].startIndex;
         this._reloadData();
      },

      /**
       * Updates the filter and reloads the page
       */ 
      onSetTopicListParams: function Discussions_onTopicCategoryChange(layer, args)
      {
         // check the filter or tag
         if (args[1].filter != undefined)
         {
            this.options.filter = args[1].filter;
            this.options.tag = "";
         }
         else if (args[1].tag != undefined)
         {
            this.options.tag = args[1].tag;
            this.options.filter = "";
         }
         
         // check the viewmode param
         if (args[1].viewmode != undefined)
         {
             this.options.viewmode = args[1].viewmode;
         }
         
         // reload the table
         this._reloadData();
      },
      
      _reloadData: function Discussions__reloadData() 
      {
         var url = Alfresco.constants.URL_SERVICECONTEXT + "modules/discussions/topiclist/get-topics";
         Alfresco.util.Ajax.request(
         {
            url: url,
            responseContentType : "application/json",
            dataObj:
            {
               site    : this.options.siteId,
               htmlid : this.id,
               startIndex : this.options.startIndex,
               pageSize    : this.options.pageSize,
               filter  : this.options.filter,
               tag     : this.options.tag,
               viewmode: this.options.viewmode
            },
            successCallback:
            {
               fn: this._processData,
               scope: this
            },
            failureMessage: this._msg("topiclist.msg.failedloadingdata")
         });
      },
      
      _processData: function Discussions__processData(response)
      {
         // first check whether we got an error back
         if (response.json.error != undefined)
         {
            Alfresco.util.PopupManager.displayMessage({text: this._msg("topiclist.msg.unableloadingdata", response.json.error)});
         }
         else
         {
            // update the paginator
            var paginatorData = response.json.paginatorData;
            this._updatePaginator(paginatorData);
           
            // update the filters
            YAHOO.Bubbling.fire('topicListParamsChanged', {
			   filter: this.options.filter,
			   tag: this.options.tag
			});
           
            // update the internal position fields
            this.options.startIndex = paginatorData.startIndex;
            this.options.pageSize = paginatorData.pageSize;
            
            // update title
            var elem = YAHOO.util.Dom.get(this.id + "-listtitle");
            elem.innerHTML = Alfresco.util.encodeHTML(response.json.listTitle);
            
            // update list
            var elem = YAHOO.util.Dom.get(this.id + "-topiclist");
            elem.innerHTML = response.json.listHtml;
            
            // init mouse over events
            Alfresco.util.rollover.registerListenersByClassName(this.id, 'topic', 'div');
         }
      },

      _updatePaginator: function Discussions__updatePaginator(paginatorData)
      {
         YAHOO.Bubbling.fire('onPagingDataChanged',
            {
               data: paginatorData
            }
         );
      },
      
      
      // Rollover effect
      
      /**
       * Called when the mouse enters into a list item.
       */
      onListElementMouseEntered: function DiscussionsTopicList_onListElementMouseEntered(layer, args)
      {
         var elem = args[1].target;
         YAHOO.util.Dom.addClass(elem, 'overNode');
         var editBloc = YAHOO.util.Dom.getElementsByClassName('nodeEdit', null, elem, null);
         YAHOO.util.Dom.addClass(editBloc, 'showEditBloc');
      },
      
      /**
       * Called whenever the mouse exits a list item.
       */
      onListElementMouseExited: function DiscussionsTopicList_onListElementMouseExited(layer, args)
      {
         var elem = args[1].target;
         YAHOO.util.Dom.removeClass(elem, 'overNode');
         var editBloc = YAHOO.util.Dom.getElementsByClassName('nodeEdit', null, elem, null);
         YAHOO.util.Dom.removeClass(editBloc, 'showEditBloc');
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
      }

   };
})();