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
 * BlogPostList component.
 * 
 * @namespace Alfresco
 * @class Alfresco.BlogPostList
 */
(function()
{
   /**
    * BlogPostList constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.BlogPostList} The new BlogPostList instance
    * @constructor
    */
   Alfresco.BlogPostList = function(htmlId)
   {
      this.name = "Alfresco.BlogPostList";
      this.id = htmlId;
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "menu", "container", "dom", "event", "history"], this.onComponentsLoaded, this);
      
      // Decoupled event listeners
      YAHOO.Bubbling.on("onSetBlogPostListParams", this.onSetPostListParams, this);
      YAHOO.Bubbling.on("onStartIndexChanged", this.onStartIndexChanged, this);
      
      return this;
   }
   
   Alfresco.BlogPostList.prototype =
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
          * Id of the container
          */
         containerId: "blog",
         
         /** Path to the blog. */
         path: "",
         
         /**
          * Position of the first displayed element
          */
         startIndex: 0,
         
         /**
          * Size of elements to show
          */
         pageSize: 10,

         /**
          * Used filter. Currently following values are supported:
          * "", "all", "new", "mydrafts", "mypublished", "publishedext"
          */         
         filter: "",
         
         /**
          * Selected tag
          * Only takes effect if the filter is "" or "all"
          */
         tag: "",
         
         /**
          * Selected view mode.
          * Only "simple" and "details" are supported
          */
         viewmode: "",
         
         /**
          * If set, only posts after this date are displayed.
          * Only takes effect if the filter is "" or "all"
          */
         fromDate: "",
         
         /**
          * If set, only posts before this date are displayed.
          * Only takes effect if the filter is "" or all
          */
         toDate: "",
         
         simpleView: false
      },
      
      widgets : {},
      
      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       */
      setOptions: function BlogPostList_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },
      
      setMessages: function BlogPostList_setMessages(obj)
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
      onComponentsLoaded: function BlogPostList_onComponentsLoaded()
      {
         YAHOO.util.Event.onContentReady(this.id, this.onReady, this, true);
      },
   
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function BlogPostList_onReady()
      {
         // Create new post button
         this.widgets.createPost = Alfresco.util.createYUIButton(this, "createPost-button", this.onCreatePost,
         {
         });

         // configure blog button
         this.widgets.configureBlog =  Alfresco.util.createYUIButton(this, "configureBlog-button", this.onConfigureBlog,
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
         
         // attach a listener to the already rendered list elements
         Alfresco.util.rollover.registerListenersByClassName(this.id, 'post', 'div');
         
         // update the filters with the state the list got loaded in
         YAHOO.Bubbling.fire('postListParamsChanged', {
            filter: this.options.filter,
            fromDate: this.options.fromDate,
            toDate: this.options.toDate,
            tag: this.options.tag
         });
      },
      

      // Actions
      
      /**
       * Action handler for the create post button
       */
      onCreatePost: function BlogPostList_onCreatePost(e, p_obj)
      {
         Alfresco.util.blog.loadBlogPostCreatePage(this.options.siteId, this.options.containerId, this.options.path);
         Event.preventDefault(e);
      },
      
      onConfigureBlog: function BlogPostList_onSimpleView(e, p_obj)
      {
         YAHOO.Bubbling.fire('onConfigureBlog', {});
         Event.preventDefault(e);
      },      
      
      onSimpleView: function BlogPostList_onSimpleView(e, p_obj)
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
         
         YAHOO.Bubbling.fire('onSetBlogPostListParams', { "viewmode" : this.options.viewmode });
         
         //YAHOO.Bubbling.fire("doclistRefresh");
         Event.preventDefault(e);
      },
      
      /**
       * Action handler for the view action
       */
      onViewNode: function BlogPostList_onViewPost(htmlId, ownerId, param)
      {
         Alfresco.util.blog.loadBlogPostViewPage(this.options.siteId, this.options.containerId, this.options.path, param);
      },
      
      onEditNode: function BlogPostList_onEditNode(htmlId, ownerId, param)
      {
         Alfresco.util.blog.loadBlogPostEditPage(this.options.siteId, this.options.containerId, this.options.path, param);
      },
      
      /**
       * Handles the click on a tag
       */
      onTagSelection: function Discussions_onTagSelected(htmlId, ownerId, param)
      {
         YAHOO.Bubbling.fire('onSetBlogPostListParams', {tag : param});
      },
      
      /**
       * Deletes a post.
       */
      onDeleteNode: function BlogPostList_onDeletePost(htmlId, ownerId, param)
      {
         this._deleteNode(param);
      },
      
      onPublishExternal: function Blog_onPublishExternal(htmlId, ownerId, param)
      {
         this._publishExternal(param);
      },
      
      onUpdateExternal: function Blog_onUpdateExternal(htmlId, ownerId, param)
      {
         this._updateExternal(param);
      },
      
      onUnpublishExternal: function Blog_onUnpublishExternal(htmlId, ownerId, param)
      {
         this._unpublishExternal(param);
      },
      
      
      // Actions implementation
      
      _deleteNode: function BlogPostList__deleteNode(postId)
      {
         // make an ajax request to the repository to delete the post
         var url = Alfresco.util.blog.getBlogPostRestUrl(this.options.siteId, this.options.containerId,
                                                         this.options.path, postId);
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
            failureMessage: this._msg("post.msg.failedDelete")
         });
      },

      _onDeleted: function BlogPost__onDeleted(response)
      {
         if (response.json.error != undefined)
         {
            Alfresco.util.PopupManager.displayMessage({text: this._msg("post.msg.unableDelete", response.json.error)});
         }
         else
         {
            // reload the list
            this._reloadData();
            Alfresco.util.PopupManager.displayMessage({text: this._msg("post.msg.deleted")});
         }
      },
      
      _publishExternal: function BlogPostList__publishExternal(postId)
      {
         // make an ajax request to publish the post
         var url = Alfresco.util.blog.getPublishingRestUrl(this.options.siteId, this.options.containerId,
                                                           this.options.path, postId);
         Alfresco.util.Ajax.request(
         {
            url: url,
            method: "POST",
            requestContentType : "application/json",
            responseContentType : "application/json",
            dataObj:
            {
               action : "publish"
            },
            successCallback:
            {
               fn: this._onPublished,
               scope: this
            },
            failureMessage: this._msg("post.msg.failedPublishExternal")
         });
      },
      
      _onPublished: function Blog__onPublished(response)
      {
         if (response.json.error != undefined)
         {
            Alfresco.util.PopupManager.displayMessage({text: this._msg("post.msg.unablePublishExternal", response.json.error)});
         }
         else
         {
            location.reload(true);
         }
      },
      
      _updateExternal: function BlogPostList(postId)
      {
         // make an ajax request to publish the post
         var url = Alfresco.util.blog.getPublishingRestUrl(this.options.siteId, this.options.containerId,
                                                           this.options.path, postId);
         Alfresco.util.Ajax.request(
         {
            url: url,
            method: "POST",
            requestContentType : "application/json",
            responseContentType : "application/json",
            dataObj:
            {
               action : "update"
            },
            successCallback:
            {
               fn: this._onUpdated,
               scope: this
            },
            failureMessage: this._msg("post.msg.failedUpdateExternal")
         });
      },

      _onUpdated: function Blog__onUpdated(response)
      {
         if (response.json.error != undefined)
         {
            Alfresco.util.PopupManager.displayMessage({text: this._msg("post.msg.unableUpdateExternal", response.json.error)});
         }
         else
         {
            Alfresco.util.PopupManager.displayMessage({text: this._msg("post.msg.updatedExternal")});
            location.reload(true);
         }
      },

      _unpublishExternal: function BlogPostList__onUnpublishExternal(postId)
      {
         // make an ajax request to publish the post
         var url = Alfresco.util.blog.getPublishingRestUrl(this.options.siteId, this.options.containerId,
                                                           this.options.path, postId);
         Alfresco.util.Ajax.request(
         {
            url: url,
            method: "POST",
            requestContentType : "application/json",
            responseContentType : "application/json",
            dataObj:
            {
               action : "unpublish"
            },
            successCallback:
            {
               fn: this._onUnpublished,
               scope: this
            },
            failureMessage: this._msg("post.msg.failedUnpublishExternal")
         });
      },
      
      _onUnpublished: function Blog__onUnpublished(response)
      {
         if (response.json.error != undefined)
         {
            Alfresco.util.PopupManager.displayMessage({text: this._msg("post.msg.unableUnpublishExternal", response.json.error)});
         }
         else
         {
            Alfresco.util.PopupManager.displayMessage({text: this._msg("post.msg.unpublishExternal")});
            location.reload(true);
         }
      },


      // List update functionality

      /**
       * Updates the filter and reloads the page
       */ 
      onSetPostListParams: function Blog_onPostCategoryChange(layer, args)
      {
         // check the filter or tag
         if (args[1].filter != undefined)
         {
            this.options.filter = args[1].filter;
            this.options.tag = "";
            this.options.fromDate = "";
            this.options.toDate = "";
         }
         else if (args[1].tag != undefined)
         {
            this.options.tag = args[1].tag;
            this.options.filter = "";
            this.options.fromDate = "";
            this.options.toDate = "";
         }
         else if (args[1].fromDate != undefined || args[1].toDate != undefined)
         {
            this.options.filter = "";
            this.options.tag = "";
            if (args[1].fromDate != undefined)
            {
                this.options.fromDate = args[1].fromDate;
            }
            else
            {
                this.options.fromDate = "";
            }
            if (args[1].toDate != undefined)
            {
                this.options.toDate = args[1].toDate;
            }
            else
            {
                this.options.toDate = "";
            }
         }
         
         // check the viewmode param
         if (args[1].viewmode != undefined)
         {
             this.options.viewmode = args[1].viewmode;
         }
         
         // reload the list
         this._reloadData();
      },
     
      /**
       * Handles paginator events
       */
      onStartIndexChanged: function BlogPostList_onPageIndexChanged(layer, args)
      {
         this.options.pageSize = args[1].pageSize;
         this.options.startIndex = args[1].startIndex;
         this._reloadData();
      },
      
      /**
       * Reloads the list using the parameters
       * stored in the options object
       */
      _reloadData: function Blog__reloadData() 
      {
         Alfresco.util.Ajax.request(
         {
            url: Alfresco.constants.URL_SERVICECONTEXT + "modules/blog/postlist/get-blogposts",
            responseContentType : "application/json",
            dataObj:
            {
               site    : this.options.siteId,
               htmlid  : this.id,
               startIndex : this.options.startIndex,
               pageSize : this.options.pageSize,
               filter  : this.options.filter,
               tag     : this.options.tag,
               viewmode: this.options.viewmode,
               fromDate : this.options.fromDate,
               toDate : this.options.toDate
            },
            successCallback:
            {
               fn: this._processData,
               scope: this
            },
            failureMessage: this._msg("topiclist.msg.failedloadingdata")
         });
      },
      
      /**
       * Displays the list data returned by the ajax call
       */
      _processData: function BlogPostList__processData(response)
      {
         // first check whether we got an error back
         if (response.json.error != undefined)
         {
            Alfresco.util.PopupManager.displayMessage({text: this._msg("topiclist.msg.unableloadingdata", response.json.error)});
         }
         else
         {
            // update the paginator. we pass the data using a bubble event
            var paginatorData = response.json.paginatorData;
            YAHOO.Bubbling.fire('onPagingDataChanged', { data: paginatorData });
     
            // update the filters
            YAHOO.Bubbling.fire('postListParamsChanged', {
			   filter: this.options.filter,
			   fromDate: this.options.fromDate,
			   toDate: this.options.toDate,
			   tag: this.options.tag
			});
                        
            // update the internal position fields
            this.options.startIndex = paginatorData.startIndex;
            this.options.pageSize = paginatorData.pageSize;
             
            // update title
            var elem = YAHOO.util.Dom.get(this.id + "-listtitle");
            elem.innerHTML = Alfresco.util.encodeHTML(response.json.listTitle);
            
            // update list
            var elem = YAHOO.util.Dom.get(this.id + "-postlist");
            elem.innerHTML = response.json.listHtml;
            
            // attach the rollover listener to the new elements
            Alfresco.util.rollover.registerListenersByClassName(this.id, 'post', 'div');
         }
      },

      // Mouse over functionality

      /**
       * Called when the mouse enters into a list item.
       */
      onListElementMouseEntered: function BlogPostList_onListElementMouseEntered(layer, args)
      {
         var elem = args[1].target;
         YAHOO.util.Dom.addClass(elem, 'overNode');
         var editBloc = YAHOO.util.Dom.getElementsByClassName('nodeEdit', null, elem, null);
         YAHOO.util.Dom.addClass(editBloc, 'showEditBloc');
      },
      
      /**
       * Called whenever the mouse exits a list item.
       */
      onListElementMouseExited: function BlogPostList_onListElementMouseExited(layer, args)
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
      _msg: function BlogPostList_msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, "Alfresco.BlogPostList", Array.prototype.slice.call(arguments).slice(1));
      }

   };
})();