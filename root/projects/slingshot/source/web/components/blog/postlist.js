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
      Alfresco.util.YUILoaderHelper.require(["button", "dom", "datasource", "datatable", "event", "element"], this.onComponentsLoaded, this);
      
      /* Decoupled event listeners */
      YAHOO.Bubbling.on("tagSelected", this.onTagSelected, this);
      YAHOO.Bubbling.on("filterChanged", this.onFilterChanged, this);
      //YAHOO.Bubbling.on("filterChanged", this.onFilterChanged, this);
      YAHOO.Bubbling.on("blogpostlistRefresh", this.onBlogPostListRefresh, this);
      
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
          * Id of the container to fetch the data from
          */
         containerId: "blog",

         /**
          * Initially used filter name and id.
          */         
         initialFilter: {},

         /**
          * Number of items per page
          * 
          * @property pageSize
          * @type int
          */
         pageSize: 2,

         /**
          * Flag indicating whether the list shows a detailed view or a simple one.
          * 
          * @property simpleView
          * @type boolean
          */
         simpleView: false,
         
         maxContentLength: 512
      },
      
      /**
       * Current filter to filter blog post list.
       * 
       * @property currentFilter
       * @type object
       */
      currentFilter:
      {
         filterId: "path",
         filterOwner: "",
         filterData: ""
      },
      
      /**
       * Current page being browsed.
       * 
       * @property currentPage
       * @type int
       * @default 1
       */
      //currentPage: 1,
      
      /**
       * Object container for storing YUI widget instances.
       * 
       * @property widgets
       * @type object
       */
      widgets : {},
      
      /**
       * Object container for storing module instances.
       * 
       * @property modules
       * @type object
       */
      modules: {},
      
      /**
       * Object literal used to generate unique tag ids
       * 
       * @property tagId
       * @type object
       */
      tagId:
      {
         id: 0,
         tags: {}
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
      
      /**
       * Set messages for this component.
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {Alfresco.DocumentList} returns 'this' for method chaining
       */
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
         // Reference to self used by inline functions
         var me = this;
          
         // Create new post button
         this.widgets.createPost = Alfresco.util.createYUIButton(this, "createPost-button", this.onCreatePost,
         {
         });

         // Configure blog button
         this.widgets.configureBlog =  Alfresco.util.createYUIButton(this, "configureBlog-button", this.onConfigureBlog,
         {
         });

         // Simple view button
         this.widgets.fileSelect = Alfresco.util.createYUIButton(this, "simpleView-button", this.onSimpleView,
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

         // initialize rss feed link
         this._generateRSSFeedUrl();

         // DataSource definition
         var uriBlogPostList = Alfresco.constants.PROXY_URI + "api/blog/site/" +
                            this.options.siteId + "/" + this.options.containerId + "/posts";
         this.widgets.dataSource = new YAHOO.util.DataSource(uriBlogPostList);
         this.widgets.dataSource.responseType = YAHOO.util.DataSource.TYPE_JSON;
         this.widgets.dataSource.connXhrMode = "queueRequests";
         this.widgets.dataSource.responseSchema =
         {
            resultsList: "items",
            fields:
            [
                "url", "commentsUrl",  "nodeRef", "name", "title", "content", "author", "createdOn", "modifiedOn",
                "permissions", "commentCount", "tags", "isDraft", "releasedOn", "isUpdated", "updatedOn", "publishedOn",
                "updatedOn", "postId", "postLink", "outOfDate", "isPublished"
            ],
            metaFields:
            {
               paginationRecordOffset: "startIndex",
               totalRecords: "total"
            }
         };
         
         /**
          * Generate ID alias for tag, suitable for DOM ID attribute
          *
          * @method generateTagId
          * @param scope {object} BlogPostList instance
          * @param tagName {string} Tag name
          * @return {string} A unique DOM-safe ID for the tag
          */
         var generateTagId = function BlogPostList_generateTagId(scope, tagName)
         {
            var id = 0;
            var tagId = scope.tagId;
            if (tagName in tagId.tags)
            {
                id = tagId.tags[tagName];
            }
            else
            {
               tagId.id++;
               id = tagId.tags[tagName] = tagId.id;
            }
            return scope.id + "-tagId-" + id;
         }



         
         /**
          * Blog post element. We only have a list and not an acutal table, there is therefore
          * only one column renderer
          *
          * @method renderBlogPost
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         var renderBlogPost = function BlogPostList_renderBlogPost(elCell, oRecord, oColumn, oData)
         {
            var data = oRecord.getData();
            
            var desc = "";
            
            // detailed view
            if (! me.options.simpleView)
            {
               desc += '<div class="node post">';

               // actions
               desc += Alfresco.util.blog.generateBlogPostActions(me, data, 'div');
   
               // begin view
               desc += '<div class="nodeContent">';
               desc += '<span class="nodeTitle"><a href="' + me._generatePostViewUrl(data.name) + '">' + $html(data.title) + '</a> ';
               desc += '<span class="nodeStatus">' + Alfresco.util.blog.generatePostStatusLabel(me, data) + '</span></span>';
               desc += '<div class="published">';
               if (data.isDraft)
               {
                  desc += '<span class="nodeAttrLabel">' + me._msg("post.info.publishedOn") + ': </span>';
                  desc += '<span class="nodeAttrValue">' + Alfresco.util.formatDate(data.releasedOn) + '</span>';
                  desc += '<span class="spacer"> | </span>';
               }
               desc += '<span class="nodeAttrLabel">' + me._msg("post.info.author") + ': </span>';
               desc += '<span class="nodeAttrValue">' + Alfresco.util.blog.generateUserLink(data.author) + '</span>';
               if (data.isPublished && data.postLink && data.postLink.length > 0)
               {
                  desc += '<span class="spacer"> | </span>';
                  desc += '<span class="nodeAttrLabel">' + me._msg("post.info.externalLink") + ': </span>';
                  desc += '<span class="nodeAttrValue"><a target="_blank" href="' + data.postLink + '">' + me._msg("post.info.clickHere") + '</a></span>';
               }
               desc += '</div>';
               desc += '<div class="content yuieditor">' + data.content + '</div>';
               desc += '</div>'
               // end view

               desc += '</div>';

               // begin footer
               desc += '<div class="nodeFooter">';
               desc += '<span class="nodeAttrLabel replyTo">' + me._msg("post.footer.replies") + ': </span>';
               desc += '<span class="nodeAttrValue">(' + data.commentCount + ')</span>';
               desc += '<span class="spacer"> | </span>';
               desc += '<span class="nodeAttrValue"><a href="' + me._generatePostViewUrl(data['name']) + '">' + me._msg("post.footer.read") + '</a></span>';
               desc += '<span class="spacer"> | </span>';
               
               desc += '<span class="nodeAttrLabel tag">' + me._msg("post.tags") +': </span>';
               if (data.tags.length > 0)
               {
                  for (var x=0; x < data.tags.length; x++)
                  {
                     desc += ' <span id="' + generateTagId(me, data.tags[x]) + '" class="nodeAttrValue">';
                     desc += '<a href="#" class="tag-link" title="' + $html(data.tags[x]) + '">';
                     desc += '<span>' + $html(data.tags[x]) + '</span></a></span> ';
                  }
               }
               else
               {
                  desc += '<span class="nodeAttrValue">' + me._msg("post.noTags") + '</span>';
               }
               desc += '</div></div>';
               // end
            }
            
            // simple view
            else
            {
               desc += '<div class="node post simple">';
               
               // begin actions
               desc += Alfresco.util.blog.generateBlogPostActions(me, data, 'span');
   
               // begin view
               desc += '<div class="nodeContent">';
               desc += '<span class="nodeTitle"><a href="' + me._generatePostViewUrl(data['name']) + '">' + $html(oRecord.getData("title")) + '</a> ';
               desc += '<span class="nodeStatus">' + Alfresco.util.blog.generatePostStatusLabel(me, data) + '</span></span>';
               desc += '</div>';
               desc += '</div>';
            }
             
            // assign html        
            elCell.innerHTML = desc;
         }


         // DataTable column defintions
         var columnDefinitions = [
         {
            key: "blogposts", label: "BlogPosts", sortable: false, formatter: renderBlogPost
         }];

         // Temporary "empty datatable" message
         YAHOO.widget.DataTable.MSG_EMPTY = this._msg("message.loading");

         // called by the paginator on state changes
         var handlePagination = function DL_handlePagination(state, dt)
         {
            //me.currentPage = state.page;
            me._updateBlogPostList({ page : state.page });
         }
         
         // DataTable definition
         this.widgets.dataTable = new YAHOO.widget.DataTable(this.id + "-postlist", columnDefinitions, this.widgets.dataSource,
         {
            initialLoad: false,
            /*renderLoopSize: this.options.usePagination ? 4 : 32,
            //initialLoad: false,
            initialRequest: this._buildDocListParams(
            {
               page: this.currentPage
            }),*/
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
         this.widgets.dataTable.doBeforeLoadData = function BlogPostList_doBeforeLoadData(sRequest, oResponse, oPayload)
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
            // Must return true to have the "Loading..." message replaced by the error message
            return true;
         }
         
         // Enable row highlighting
         this.widgets.dataTable.subscribe("rowMouseoverEvent", this.onEventHighlightRow, this, true);
         this.widgets.dataTable.subscribe("rowMouseoutEvent", this.onEventUnhighlightRow, this, true);
         
         // Set the default view filter to be "new" and the owner to be "Alfresco.BlogPostListFilter"
         var filterObj = YAHOO.lang.merge(
         {
            filterId: "filter",
            filterOwner: "Alfresco.BlogPostListFilter",
            filterData: null
         }, this.options.initialFilter);

         YAHOO.Bubbling.fire("filterChanged", filterObj);

         // Hook action events for details view
         var fnActionHandlerDiv = function BlogPostList_fnActionHandlerDiv(layer, args)
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
         YAHOO.Bubbling.addDefaultAction("blogpost-action-link-div", fnActionHandlerDiv);
         
         // Hook action events for simple view
         var fnActionHandlerSpan = function BlogPostList_fnActionHandlerSpan(layer, args)
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
         YAHOO.Bubbling.addDefaultAction("blogpost-action-link-span", fnActionHandlerSpan);
         
         // Hook tag clicks
         var fnTagHandler = function BlogPostList_fnTagHandler(layer, args)
         {
            var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "span");
            if (owner !== null)
            {
               var tagId = owner.id;
               tagId = tagId.substring(tagId.lastIndexOf("-") + 1);
               for (tag in me.tagId.tags)
               {
                  if (me.tagId.tags[tag] == tagId)
                  {
                     YAHOO.Bubbling.fire("tagSelected",
                     {
                        tagName: tag
                     });
                     break;
                  }
               }
               args[1].stop = true;
            }
      		 
            return true;
         }
         YAHOO.Bubbling.addDefaultAction("tag-link", fnTagHandler);
      },
      
      /**
       * Generates the HTML mark-up for the RSS feed link
       *
       * @method _generateRSSFeedUrl
       * @private
       */
      _generateRSSFeedUrl: function BlogPostList__generateRSSFeedUrl()
      {
         var divFeed = Dom.get(this.id + "-rssFeed");
         if (divFeed)
         {  
            var url = Alfresco.constants.URL_CONTEXT + "service/components/blog/rss?site=" + this.options.siteId;
            divFeed.innerHTML = '<a href="' + url + '">' + this._msg("header.blogRSS") + '</a>';
         }
      },

      /**
       * Generates a view url for a given blog post id, using
       * the current component configuration for site and container
       * @param postId the id/name of the post
       * @return an url to access the post
       */
      _generatePostViewUrl: function BlogPostList__generatePostViewUrl(postId)
      {
         var url = YAHOO.lang.substitute(Alfresco.constants.URL_CONTEXT + "page/site/{site}/blog-postview?container={container}&postId={postId}",
         {
            site: this.options.siteId,
            container: this.options.containerId,
            postId: postId
         });
         return url;
      },  

      // Actions
      
      /**
       * Action handler for the create post button
       */
      onCreatePost: function BlogPostList_onCreatePost(e, p_obj)
      {
         var url = YAHOO.lang.substitute(Alfresco.constants.URL_CONTEXT + "page/site/{site}/blog-postedit?container={container}",
         {
            site: this.options.siteId,
            container: this.options.containerId
         });
         window.location = url;
         Event.preventDefault(e);
      },
      
      /**
       * Action handler for the configure blog button
       */
      onConfigureBlog: function BlogPostList_onConfigureBlog(e, p_obj)
      {
         if (!this.modules.configblog)
         {
            this.modules.configblog = new Alfresco.module.ConfigBlog(this.id + "-configblog");
         }

         this.modules.configblog.setOptions(
         {
            siteId: this.options.siteId,
            containerId: this.options.containerId
         }).showDialog();
         Event.preventDefault(e);
      },      
      
      /**
       * Action handler for the simple view toggle button
       */
      onSimpleView: function BlogPostList_onSimpleView(e, p_obj)
      {
         this.options.simpleView = !this.options.simpleView;
         p_obj.set("checked", this.options.simpleView);

         // update the list
         YAHOO.Bubbling.fire("blogpostlistRefresh");
         Event.preventDefault(e);
      },
      
      /**
       * Handler for the view blog action links
       *
       * @method onActionDelete
       * @param row {object} DataTable row representing file to be actioned
       */
      onViewBlogPost: function BlogPostList_onViewNode(row)
      {
         var record = this.widgets.dataTable.getRecord(row);
         window.location = this._generatePostViewUrl(record.getData('name'));
      },

      /**
       * Handler for the edit blog action links
       */
      onEditBlogPost: function BlogPostList_onEditBlogPost(row)
      {
         var record = this.widgets.dataTable.getRecord(row);
         var url = YAHOO.lang.substitute(Alfresco.constants.URL_CONTEXT + "page/site/{site}/blog-postedit?container={container}&postId={postId}",
         {
            site: this.options.siteId,
            container: this.options.containerId,
            postId: record.getData('name')
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
      onTagSelected: function BlogPostList_onTagSelected(layer, args)
      {
         var obj = args[1];
         if (obj && (obj.tagName !== null))
         {
            var filterObj = {
               filterId: obj.tagName,
               filterOwner: "Alfresco.BlogPostListTags",
               filterData: null
            };
            YAHOO.Bubbling.fire("filterChanged", filterObj);
         }
      },
      
      /**
       * Deletes a post.
       */
      onDeleteBlogPost: function BlogPostList_onDeletePost(row)
      {
         var record = this.widgets.dataTable.getRecord(row);
         this._deleteNode(record.getData('name'));
      },
      
      onPublishExternal: function Blog_onPublishExternal(row)
      {
         var record = this.widgets.dataTable.getRecord(row);
         this._publishExternal(record.getData('name'));
      },
      
      onUpdateExternal: function Blog_onUpdateExternal(row)
      {
         var record = this.widgets.dataTable.getRecord(row);
         this._updateExternal(record.getData('name'));
      },
      
      onUnpublishExternal: function Blog_onUnpublishExternal(row)
      {
         var record = this.widgets.dataTable.getRecord(row);
         this._unpublishExternal(record.getData('name'));
      },
      
      
      // Actions implementation
      
      _deleteNode: function BlogPostList__deleteNode(postId)
      {
         var url = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "api/blog/post/site/{site}/{container}/{postId}",
         {
            site: this.options.siteId,
            container: this.options.containerId,
            postId: postId
         });
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
            Alfresco.util.PopupManager.displayMessage({text: this._msg("post.msg.deleted")});
            location.reload(true);
         }
      },
      
      _getPublishingUrl: function BlogPostList__getPublishingUrl(postId)
      {
         return YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "api/blog/post/site/{site}/{container}/{postId}/publishing",
         {
            site: this.options.siteId,
            container: this.options.containerId,
            postId: postId
         });
      },
      
      _publishExternal: function BlogPostList__publishExternal(postId)
      {
         Alfresco.util.Ajax.request(
         {
            url: this._getPublishingUrl(postId),
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
      
      _onPublished: function BlogPostList__onPublished(response)
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
         Alfresco.util.Ajax.request(
         {
            url: this._getPublishingUrl(postId),
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

      _onUpdated: function BlogPostList__onUpdated(response)
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
         Alfresco.util.Ajax.request(
         {
            url: this._getPublishingUrl(postId),
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
      
      _onUnpublished: function BlogPostList__onUnpublished(response)
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

      
      /**
       * Custom event handler to highlight row.
       *
       * @method onEventHighlightRow
       * @param oArgs.event {HTMLEvent} Event object.
       * @param oArgs.target {HTMLElement} Target element.
       */
      onEventHighlightRow: function BlogPostList_onEventHighlightRow(oArgs)
      {
         var target = oArgs.target;
         var elem = YAHOO.util.Dom.getElementsByClassName('post', null, target, null);
         YAHOO.util.Dom.addClass(elem, 'overNode');
         var editBloc = YAHOO.util.Dom.getElementsByClassName('nodeEdit', null, target, null);
         YAHOO.util.Dom.addClass(editBloc, 'showEditBlock');
      },

      /**
       * Custom event handler to unhighlight row.
       *
       * @method onEventUnhighlightRow
       * @param oArgs.event {HTMLEvent} Event object.
       * @param oArgs.target {HTMLElement} Target element.
       */
      onEventUnhighlightRow: function BlogPostList_onEventUnhighlightRow(oArgs)
      {
         var target = oArgs.target;
         var elem = YAHOO.util.Dom.getElementsByClassName('post', null, target, null);
         YAHOO.util.Dom.removeClass(elem, 'overNode');
         var editBloc = YAHOO.util.Dom.getElementsByClassName('nodeEdit', null, target, null);
         YAHOO.util.Dom.removeClass(editBloc, 'showEditBlock');
      },
      
      
      
      /**
       * DocList View Filter changed event handler
       *
       * @method onFilterChanged
       * @param layer {object} Event fired (unused)
       * @param args {array} Event parameters (new filterId)
       */
      onFilterChanged: function BlogPostList_onFilterChanged(layer, args)
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
            this._updateBlogPostList({ page: 1 });
         }
      },
      
      /**
       * Deactivate All Controls event handler
       *
       * @method onDeactivateAllControls
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onDeactivateAllControls: function BlogPostList_onDeactivateAllControls(layer, args)
      {
         for (widget in this.widgets)
         {
            this.widgets[widget].set("disabled", true);
         }
      },
      
      /**
       * Updates the list title considering the current filter.
       */
      updateListTitle: function BlogPostList_updateListTitle()
      {
         var elem = Dom.get(this.id + '-listtitle');
         var title = "<unknown filter>";

         var filterOwner = this.currentFilter.filterOwner;
         var filterId = this.currentFilter.filterId;
         var filterData = this.currentFilter.filterData;
         if (filterOwner == "Alfresco.BlogPostListFilter")
         {
            if (filterId == "all")
            {
                title = this._msg("postlist.title.allposts");
            }
            if (filterId == "new")
            {
               title = this._msg("postlist.title.newposts");
            }
            else if (filterId == "mydrafts")
            {
               title = this._msg("postlist.title.mydrafts");
            }
            else if (filterId == "mypublished")
            {
               title = this._msg("postlist.title.mypublished");
            }
            else if (filterId == "publishedext")
            {
               title = this._msg("postlist.title.publishedext");
            }
         }
         else if (filterOwner == "Alfresco.BlogPostListTags")
         {
            title = this._msg("postlist.title.bytag", $html(filterData));
         }
         else if (filterOwner == "Alfresco.BlogPostListArchive" && filterId == "bymonth")
         {
            var date = new Date(filterData.year, filterData.month, 1);
            var formattedDate = Alfresco.util.formatDate(date, "mmmm yyyy")
            title = this._msg("postlist.title.bymonth", formattedDate);
         }
         
         elem.innerHTML = title;
      },


      /**
       * BlogPostList Refresh Required event handler
       *
       * @method onBlogPostListRefresh
       * @param layer {object} Event fired (unused)
       * @param args {array} Event parameters (unused)
       */
      onBlogPostListRefresh: function BlogPostList_onBlogPostListRefresh(layer, args)
      {
         this._updateBlogPostList({});
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
      },

      /**
       * Resets the YUI DataTable errors to our custom messages
       * NOTE: Scope could be YAHOO.widget.DataTable, so can't use "this"
       *
       * @method _setDefaultDataTableErrors
       */
      _setDefaultDataTableErrors: function BlogPostList__setDefaultDataTableErrors()
      {
         var msg = Alfresco.util.message;
         YAHOO.widget.DataTable.MSG_EMPTY = msg("message.empty", "Alfresco.BlogPostList");
         YAHOO.widget.DataTable.MSG_ERROR = msg("message.error", "Alfresco.BlogPostList");
      },
      
      /**
       * Updates blog post list by calling data webscript with current site and filter information
       *
       * @method _updateBlogPostList
       */
      _updateBlogPostList: function BlogPostList__updateBlogPostList(p_obj)
      {
         // Reset the custom error messages
         this._setDefaultDataTableErrors();
         
         var successHandler = function BlogPostList__updateBlogPostList_successHandler(sRequest, oResponse, oPayload)
         {
            //this.currentPath = successPath;
            this.widgets.dataTable.onDataReturnInitializeTable.call(this.widgets.dataTable, sRequest, oResponse, oPayload);
            this.updateListTitle();
         }
         
         var failureHandler = function BlogPostList__updateBlogPostList_failureHandler(sRequest, oResponse)
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
         this.widgets.dataSource.sendRequest(this._buildBlogPostListParams(p_obj || {}),
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
       * @param p_obj.path {string} Path to query
       * @param p_obj.type {string} Filetype to filter: "all", "documents", "folders"
       * @param p_obj.site {string} Current site
       * @param p_obj.container {string} Current container
       * @param p_obj.filter {string} Current filter
       */
      _buildBlogPostListParams: function DL__buildDocListParams(p_obj)
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

         // check what url to call and with what parameters
         var filterOwner = this.currentFilter.filterOwner;
         var filterId = this.currentFilter.filterId;
         var filterData = this.currentFilter.filterData;       
         
         // check whether we got a filter or not
         var url = "";
         if (filterOwner == "Alfresco.BlogPostListFilter")
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
            else if (filterId == "mydrafts")
            {
                url = "/mydrafts"
            }
            else if (filterId == "mypublished")
            {
                url = "/mypublished"
            }
            else if (filterId == "publishedext")
            {
                url = "/publishedext"
            }
         }
         else if (filterOwner == "Alfresco.BlogPostListTags")
         {
            params.tag = filterId;
         }
         else if (filterOwner == "Alfresco.BlogPostListArchive" && filterId == "bymonth")
         {
            var fromDate = new Date(filterData.year, filterData.month, 1);
            var toDate = new Date(filterData.year, filterData.month + 1, 1);
            toDate = new Date(toDate.getTime() - 1);
            params.fromDate = fromDate.getTime();
            params.toDate = toDate.getTime();
         }
         
         // build the url
         var urlExt = "";
         for (paramName in params)
         {
            if (params[paramName] === null)
            {
               continue;
            }
            urlExt += "&" + paramName;
            urlExt += "=";
            urlExt += encodeURIComponent(params[paramName]);
         }
         urlExt += "&startIndex=" + (params.page-1) * params.pageSize;

         return url + "?" + urlExt.substring(1);
      }
   };
})();