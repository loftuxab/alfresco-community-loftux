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
         
         containerId: "blog",
         
         path: "",
         
         /**
          * Position of the first displayed element
          */
         startIndex: 0,
         
         /**
          * Size of elements to show
          */
         pageSize: 10,
         
         filter: "",
         
         tag: "",
         
         viewmode: "",
         
         fromDate: "",
         
         toDate: ""
      },
   	
      
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
         // Links - PENDING: this should be done in a more generic way
         YAHOO.util.Event.addListener("simple-list-view", "click",
            function simpleListViewClicked(e) {
               var owner = YAHOO.Bubbling.fire('onSetBlogPostListParams', {viewmode: 'simple'});
               return true;
            }
         );
         YAHOO.util.Event.addListener("detailed-list-view", "click",
            function detailedListViewClicked(e) {
               var owner = YAHOO.Bubbling.fire('onSetBlogPostListParams', {viewmode: 'details'});
               return true;
            }
         ); 

         // used by the filter, tag and view buttons to set options
         YAHOO.Bubbling.on("onSetBlogPostListParams", this.onSetPostListParams, this);

         // catch paginator events
         YAHOO.Bubbling.on("onStartIndexChanged", this.onStartIndexChanged, this);
         
         // Hook action events
         Alfresco.util.registerDefaultActionHandler(this.id, "action-link-div", "div", this);
         Alfresco.util.registerDefaultActionHandler(this.id, "action-link-span", "span", this);
         
         // initialize the mouse over listener
         Alfresco.util.rollover.registerHandlerFunctions(this.id, this.onListElementMouseEntered, this.onListElementMouseExited);
         
         // as the list got already rendered on the server, already attach the listener to the rendered elements
         Alfresco.util.rollover.registerListenersByClassName(this.id, 'post', 'div');
      },
      
      /**
       * Called when the mouse enters into a list item.
       */
      onListElementMouseEntered: function BlogPostList_onListElementMouseEntered(layer, args)
      {
         var elem = args[1].target;
         YAHOO.util.Dom.addClass(elem, 'overNode');
         var editBloc = YAHOO.util.Dom.getElementsByClassName( 'nodeEdit' , null , elem, null );
         YAHOO.util.Dom.addClass(editBloc, 'showEditBloc');
      },
      
      /**
       * Called whenever the mouse exits a list item.
       */
      onListElementMouseExited: function BlogPostList_onListElementMouseExited(layer, args)
      {
         var elem = args[1].target;
         YAHOO.util.Dom.removeClass(elem, 'overNode');
         var editBloc = YAHOO.util.Dom.getElementsByClassName( 'nodeEdit' , null , elem , null );
         YAHOO.util.Dom.removeClass(editBloc, 'showEditBloc');
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
       * Deletes a post.
       */
      onDeleteNode: function BlogPost_onDeletePost(htmlId, ownerId, param)
      {
         // make an ajax request to delete the post
         // we can directly go to alfresco for this
         Alfresco.util.Ajax.request(
		   {
		      url: Alfresco.constants.PROXY_URI + "blog/post/site/" + this.options.siteId + "/" + this.options.containerId + "/" /* ADD PATH */ + param,
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
         if (response.json.error == undefined)
         {
            // reload the list
            this._reloadData();
            Alfresco.util.PopupManager.displayMessage({text: this._msg("post.msg.deleted")});
         }
         else
         {
            Alfresco.util.PopupManager.displayMessage({text: this._msg("post.msg.unableDelete") + response.json.error});
         }
      },
      
      _getPublishingRestUrl: function Blog__getPublishingRestUrl(postId)
      {
          return Alfresco.constants.PROXY_URI + "blog/post/site/" +
                    this.options.siteId + "/" + this.options.containerId + "/" + postId + "/publishing";
      },
      
      onPublishExternal: function Blog_onPublishExternal(htmlId, ownerId, param)
      {
         var me = this;
         // make an ajax request to publish the post
         Alfresco.util.Ajax.request(
		   {
		      url: me._getPublishingRestUrl(param),
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
		      failureMessage: "Unable to publish"
		   });
      },
      
      _onPublished: function Blog__onPublished(response)
      {
          Alfresco.util.PopupManager.displayMessage({text: "Published!"});
          location.reload(true);
      },
     
      onUpdateExternal: function Blog_onUpdateExternal(htmlId, ownerId, param)
      {
         // make an ajax request to publish the post
         Alfresco.util.Ajax.request(
		   {
		      url: this._getPublishingRestUrl(param),
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
		      failureMessage: "Unable to publish"
		   });
      },

      _onUpdated: function Blog__onUpdated(response)
      {
          Alfresco.util.PopupManager.displayMessage({text: "Updated!"});
          location.reload(true);
      },    

      onUnpublishExternal: function Blog_onUnpublishExternal(htmlId, ownerId, param)
      {
         // make an ajax request to publish the post
         Alfresco.util.Ajax.request(
		   {
		      url: this._getPublishingRestUrl(param),
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
		      failureMessage: "Unable to unpublish"
		   });
      },
      
      _onUnpublished: function Blog__onUnpublished(response)
      {
          Alfresco.util.PopupManager.displayMessage({text: "Unpublished!"});
          location.reload(true);
      },


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
         
         // reload the table
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
		      failureMessage: this._msg("post.msg.failedLoad1")
		   });
      },
      
      _processData: function Blog__processData(response)
	  {
	     // first check whether we got an error back
	     if (response.json.error != undefined)
	     {
	        Alfresco.util.PopupManager.displayMessage({text: this._msg("post.msg.failedLoadData") + response.json.error});
	     }
	     else
	     {
	        // update the paginator
	        var paginatorData = response.json.paginatorData;
	        this._updatePaginator(paginatorData);
	        
	        // update the internal position fields
	        this.options.startIndex = paginatorData.startIndex;
	        this.options.pageSize = paginatorData.pageSize;
	         
	        // update title
	        var elem = YAHOO.util.Dom.get(this.id + "-listtitle");
            elem.innerHTML = response.json.listTitle;
	         
	        // update list
	        var elem = YAHOO.util.Dom.get(this.id + "-postlist");
            elem.innerHTML = response.json.listHtml;
            
            Alfresco.util.rollover.registerListenersByClassName(this.id, 'post', 'div');
	     }
	  },

      _updatePaginator: function BlogPostList__updatePaginator(paginatorData)
      {
         YAHOO.Bubbling.fire('onPagingDataChanged',
            {
               data: paginatorData
            }
         );
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