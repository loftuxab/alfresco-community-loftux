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
         
         /**
          * Position inside the pages
          */
         pos: 0,
         
         /**
          * Size of elements to show
          */
         size: 10,
         
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
         // used by the filter, tag and view buttons to set options
         YAHOO.Bubbling.on("onSetBlogPostListParams", this.onSetPostListParams, this);

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
          
         // Event on Paginator
         var me = this;
         YAHOO.Bubbling.addDefaultAction("pagerItem", function Blog_onPostPaginationClicked(layer, args)
         {
         	// TODO: Changer filter to use current filter-
            //var owner = YAHOO.Bubbling.fire('onSetPostListParams', {page : args[1].el.id, filter : 'new'});
            //return true;
            var elem = args[1].anchor;
            if (elem.id.substring(0, "GoToPrevious".length) == "GoToPrevious") {
                me.options.pos = me.options.pos - me.options.size;
                me._reloadData();
            }
            else if (elem.id.substring(0, "GoToNext".length) == "GoToNext") {
                me.options.pos = me.options.pos + me.options.size;
                me._reloadData();
            }
            else if (elem.id.substring(0, "GoToPage-".length) == "GoToPage-") {
                var page = elem.id.substring("GoToPage-".length);
                me.options.pos = (page-1) * me.options.size;
                me._reloadData();
            }
         });
         
         // Hook action events
         YAHOO.Bubbling.addDefaultAction("action-link", function BlogPostList_filterAction(layer, args)
         {
            var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "div");
            if (owner !== null)
            {
               var action = owner.className;
               var target = args[1].target;
               if (typeof me[action] == "function")
               {
                  // extract the id from the element
                  var elemId = owner.id.substring((action + "-").length);
                  me[action].call(me, elemId);
                  args[1].stop = true;
               }
            }
      		 
            return true;
         });
         
         // initialize the mouse over functionality for the list
         this.initMouseOverListeners();
         
      },
      
      /**
       * Attaches a listener to all passed elements.
       */
      _attachRolloverListener: function(elem, mouseOverEventName, mouseOutEventName)
      {  
         var eventElem = elem;
         
         var mouseOverHandler = function(e)
         {
             // find out whether we actually moved inside the 
             if (! e) var e = window.event;
             var relTarg = e.relatedTarget || e.fromElement;
             while (relTarg != null && relTarg != eventElem && relTarg.nodeName != 'BODY') {
                relTarg = relTarg.parentNode
             }
             if (relTarg == eventElem) return;
             
             // the mouse entered the element, fire an event to inform about it
             YAHOO.Bubbling.fire(mouseOverEventName, {event : e, target : eventElem});
         };
         
         var mouseOutHandler = function(e)
         {
             // find out whether we actually moved inside the 
             if (! e) var e = window.event;
             var relTarg = e.relatedTarget || e.toElement;
             while (relTarg != null && relTarg != eventElem && relTarg.nodeName != 'BODY') {
                relTarg = relTarg.parentNode
             }
             if (relTarg == eventElem) return;
             
             // the mouse exited the element, fire an event to inform about it
             YAHOO.Bubbling.fire(mouseOutEventName, {event : e, target : eventElem});
         };
         
         YAHOO.util.Event.addListener(elem, 'mouseover', mouseOverHandler);
         YAHOO.util.Event.addListener(elem, 'mouseout', mouseOutHandler);
      },
      
      firstMouseOverInit: true,
      
      initMouseOverListeners: function BlogPostList_initMouseOverListeners()
      {
         var mouseEnteredBubbleEventName = 'onPostListElementMouseEntered';
         var mouseExitedBubbleEventName = 'onPostListElementMouseExited';
         var divs = YAHOO.util.Dom.getElementsByClassName('post', 'div');
         for (var x=0; x < divs.length; x++) {
             this._attachRolloverListener(divs[x], mouseEnteredBubbleEventName, mouseExitedBubbleEventName);
         }
         
         if (this.firstMouseOverInit) {
            this.firstMouseOverInit = false;
            // manage mouse hover/exit
            YAHOO.Bubbling.on(mouseEnteredBubbleEventName, this.onListElementMouseEntered, this);
            YAHOO.Bubbling.on(mouseExitedBubbleEventName, this.onListElementMouseExited, this);
         }
      },
      
      /** Called when the mouse enters into a list item. */
      onListElementMouseEntered: function BlogPostList_onListElementMouseEntered(layer, args)
      {
         var elem = args[1].target;
         YAHOO.util.Dom.addClass(elem, 'overNode');
         var editBloc = YAHOO.util.Dom.getElementsByClassName( 'nodeEdit' , null , elem, null );
         YAHOO.util.Dom.addClass(editBloc, 'showEditBloc');
      },
      
      /** Called whenever the mouse exits a list item. */
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
      onViewNode: function BlogPostList_onViewPost(id)
      {
         var url = Alfresco.constants.URL_CONTEXT + "page/site/" + this.options.siteId + "/blog-postview?postId=" + id;
         window.location = url;
      },
      
      onEditNode: function BlogPostList_onEditNode(id)
      {
         var url = Alfresco.constants.URL_CONTEXT + "page/site/" + this.options.siteId + "/blog-postedit?container=" + this.options.containerId + "&postId=" + id;
         window.location = url;
      },
      
      /**
       * Deletes a post.
       */
      onDeleteNode: function BlogPost_onDeletePost(id)
      {
         // make an ajax request to delete the post
         // we can directly go to alfresco for this
         Alfresco.util.Ajax.request(
		   {
		      url: Alfresco.constants.PROXY_URI + "forum/post/site/" + this.options.siteId + "/" + this.options.containerId + "/" /* ADD PATH */ + id,
		      method: "DELETE",
		      responseContentType : "application/json",
		      /*dataObj:
		      {
		         site   : this.options.siteId,
		         container : "blog",
		         path : this.options.postId
		      },*/
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
      
      onPublishExternal: function Blog_onPublishExternal(id)
      {
         var me = this;
         // make an ajax request to publish the post
         Alfresco.util.Ajax.request(
		   {
		      url: me._getPublishingRestUrl(id),
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
     
      onUpdateExternal: function Blog_onUpdateExternal(id)
      {
         // make an ajax request to publish the post
         Alfresco.util.Ajax.request(
		   {
		      url: this._getPublishingRestUrl(id),
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

      onUnpublishExternal: function Blog_onUnpublishExternal(id)
      {
         // make an ajax request to publish the post
         Alfresco.util.Ajax.request(
		   {
		      url: this._getPublishingRestUrl(id),
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
      
      _reloadData: function Blog__reloadData() 
      {
      	Alfresco.util.Ajax.request(
		   {
		      url: Alfresco.constants.URL_SERVICECONTEXT + "modules/blog/postlist/get-blogposts",
		      responseContentType : "application/json",
		      dataObj:
		      {
		         site    : this.options.siteId,
		         pos     : this.options.pos,
		         size    : this.options.size,
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
	        // update paginator
  	        var elem = YAHOO.util.Dom.get(this.id + "-paginator");
            elem.innerHTML = response.json.paginatorHtml;
	        
	        // update the internal position counter
	        this.options.pos = response.json.startIndex;
	         
	        // update title
	        var elem = YAHOO.util.Dom.get(this.id + "-listtitle");
            elem.innerHTML = response.json.listTitle;
	         
	        // update list
	        var elem = YAHOO.util.Dom.get(this.id + "-postlist");
            elem.innerHTML = response.json.listHtml;
            this.initMouseOverListeners();
            
	     }
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