/**
 * BlogPostViewView component.
 * 
 * Component to view a blog post
 * 
 * @namespace Alfresco
 * @class Alfresco.BlogPostView
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
    * Post constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.PostView} The new Post instance
    * @constructor
    */
   Alfresco.BlogPostView = function(htmlId)
   {
      this.name = "Alfresco.BlogPostView";
      this.id = htmlId;
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["datasource", "json", "connection", "event", "button", "menu", "editor"], this.onComponentsLoaded, this);
      
      return this;
   }
   
   Alfresco.BlogPostView.prototype =
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
         
         postId: ""
      },
      
      /**
       * Stores the data displayed by this component
       */
      blogPostData: null,
      
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
      setOptions: function BlogPostView_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },
      
      setMessages: function BlogPostView_setMessages(obj)
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
      onComponentsLoaded: function BlogPostView_onComponentsLoaded()
      {
         YAHOO.util.Event.onContentReady(this.id, this.onReady, this, true);
      },
   
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function BlogPostView_onReady()
      {
         var me = this;
          
         var x = 0;
         
         // Hook action events.
         var fnActionHandlerDiv = function CommentList_fnActionHandlerDiv(layer, args)
         {
            var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "div");
            if (owner !== null)
            {
               var action = "";
               action = owner.className;
               if (typeof me[action] == "function")
               {
                  var commentElem = Dom.getAncestorByClassName(owner, 'post');
                  me[action].call(me, me.blogPostData.name);
                  args[1].stop = true;
               }
            }
      		 
            return true;
         }
         YAHOO.Bubbling.addDefaultAction("blogpost-action-link-div", fnActionHandlerDiv);
         
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
          
         // initialize the mouse over listener
         Alfresco.util.rollover.registerHandlerFunctions(this.id, this.onPostElementMouseEntered, this.onPostElementMouseExited);
          
         // load the post data - which in turn will render the UI
         this._loadBlogPostData();
      },
      
      
      /**
       * Loads the comments for the provided nodeRef and refreshes the ui
       */
      _loadBlogPostData: function BlogPostView__loadBlogPostData()
      {
         var url = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "api/blog/post/site/{site}/{container}/{postId}",
         {
            site : this.options.siteId,
            container: this.options.containerId,
            postId: this.options.postId
         });
         Alfresco.util.Ajax.request(
         {
            url: url,
            successCallback:
            {
               fn: this.loadBlogPostDataSuccess,
               scope: this
            },
            failureCallback:
            {
               fn: this.loadBlogPostDataFailed,
               scope: this
            }
         });
      },

      loadBlogPostDataFailed: function BlogPostView_loadBlogPostDataFailed(response)
      {
         // Display success message anyway
         Alfresco.util.PopupManager.displayMessage(
         {
            text: this._msg("message.details.failed")
         });
      },

      loadBlogPostDataSuccess: function CommentsList_loadCommentsSuccess(response)
      {
         var data = response.json.item
         this.blogPostData = data;
         
         var html = this.renderBlogPost(data);
         
         // get the container div
         var viewDiv = Dom.get(this.id + '-post-view-div');
         viewDiv.innerHTML = html;
         
         // attach the rollover listeners
         Alfresco.util.rollover.registerListenersByClassName(this.id, 'post', 'div');
         
         // inform the two comment components about the post
         var eventData = {
            itemNodeRef: data.nodeRef,
            itemTitle: data.title,
            itemName: data.name
         }

         YAHOO.Bubbling.fire("setCommentedNode", eventData);
      },   
      
      renderBlogPost: function(data)
      {
         var html = '';
          
         html += '<div id="' + this.id + '-postview" class="node post postview">'
         
         html += Alfresco.util.blog.generateBlogPostActions(this, data, 'div');
  
         // content
         html += '<div class="nodeContent">';
         html += '<div class="nodeTitle"><a href="' + this._generatePostViewUrl(data.name) + '">' + $html(data.title) + '</a> ';
         html += '<span class="nodeStatus">' + Alfresco.util.blog.generatePostStatusLabel(this, data) + '</span>';
         html += '</div>';
          
         html += '<div class="published">';
         if (! data.isDraft)
         {
            html += '<span class="nodeAttrLabel">' + this._msg("post.info.publishedOn") + ':</span>';
            html += '<span class="nodeAttrValue">' + Alfresco.util.formatDate(data.releasedOn) + '</span>';
            html += '<span class="spacer"> | </span>';
         }
 
         html += '<span class="nodeAttrLabel">' + this._msg("post.info.author") + ':</span>';
         html += '<span class="nodeAttrValue">' + Alfresco.util.blog.generateUserLink(data.author) + '</span>';

         if (data.isPublished && data.postLink != undefined && data.postLink.length > 0)
         {
            html += '<span class="spacer"> | </span>';
            html += '<span class="nodeAttrLabel">' + this._msg("post.info.externalLink") + ': </span>';
            html += '<span class="nodeAttrValue"><a target="_blank" href="' + data.postList + '">' + this._msg("post.info.clickHere") + '</a></span>';
         }
         
         html += '<span class="spacer"> | </span>';
         html += '<span class="nodeAttrLabel tag">' + this._msg("post.tags") + ': </span>';
         if (data.tags.length > 0)
         {
            for (var x=0; x < data.tags.length; x++)
            {
               html += '<span id="' + this.generateTagId(this, data.tags[x]) + '" class="nodeAttrValue tag">';
               html += '<a href="#" class="tag-link" title="' + $html(data.tags[x]) + '">';
               html += '<span>' + $html(data.tags[x]) + '</span></a></span> ';
            }
         }
         else
         {
            html += '<span class="nodeAttrValue">' + this._msg("post.noTags") + '</span>';
         }
         html += '</div>'
      
         html += '<div class="content yuieditor">' + data.content + '</div>';
         html += '</div></div>';
         return html;
      },
      
      
      /**
       * Generate ID alias for tag, suitable for DOM ID attribute
       *
       * @method generateTagId
       * @param scope {object} BlogPostList instance
       * @param tagName {string} Tag name
       * @return {string} A unique DOM-safe ID for the tag
       */
      generateTagId : function BlogPostList_generateTagId(scope, tagName)
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
      },
      
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
       * Tag selected handler (document details)
       *
       * @method onTagSelected
       * @param tagId {string} Tag name.
       * @param target {HTMLElement} Target element clicked.
       */
      onTagSelected: function BlogPostView_onTagSelected(layer, args)
      {
         var obj = args[1];
         if (obj && (obj.tagName !== null))
         {
            var url = YAHOO.lang.substitute(Alfresco.constants.URL_CONTEXT + "page/site/{site}/blog-postlist?container={container}&initialFilterId={filterId}&initialFilterName={filterName}&initialFilterData={filterData}",
            {
               site: this.options.siteId,
               container: this.options.containerId,
               filterId: "tag",
               filterOwner: "Alfresco.BlogPostListTags",
               filterData: obj.tagName
            });
            window.location = url;
         }
      },

      /**
       * Loads the edit post form and displays it instead of the content
       * The div class should have the same name as the above function (onEditNode)
       */
      onEditBlogPost: function BlogPostView_onEditNode(postId)
      {
         var url = YAHOO.lang.substitute(Alfresco.constants.URL_CONTEXT + "page/site/{site}/blog-postedit?container={container}&postId={postId}",
         {
            site: this.options.siteId,
            container: this.options.containerId,
            postId: postId
         });    
         window.location = url;
      },
      
      /**
       * Deletes a post.
       * The div class which contain the Delete link should have the same name as the above function (onEditNode)
       */
      onDeleteBlogPost: function BlogPostView_onDelete(postId)
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

      _onDeleted: function BlogPostView__onDeleted(response)
      {
         if (response.json.error != undefined)
         {
            Alfresco.util.PopupManager.displayMessage({text: this._msg("post.msg.unableDelete", response.json.error)});
         }
         else
         {
            // load the blog post list page
            var url = YAHOO.lang.substitute(Alfresco.constants.URL_CONTEXT + "page/site/{site}/blog-postlist?container={container}",
            {
               site: this.options.siteId,
               container: this.options.containerId
            });    
            window.location = url;
         }
      },
      
      _getPublishingUrl: function BlogPostView__getPublishingUrl(postId)
      {
         return YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "api/blog/post/site/{site}/{container}/{postId}/publishing",
         {
            site: this.options.siteId,
            container: this.options.containerId,
            postId: postId
         });
      },
      
      onPublishExternal: function BlogPostView_onPublishExternal(postId)
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
      
      _onPublished: function BlogPostView__onPublished(response)
      {
         if (response.json.error != undefined)
         {
            Alfresco.util.PopupManager.displayMessage({text: this._msg("post.msg.unablePublishExternal", response.json.error)});
         }
         else
         {
            Alfresco.util.PopupManager.displayMessage({text: this._msg("post.msg.publishedExternal")});

            // re-render the post
            this.loadBlogPostDataSuccess(response);
         }
      },
     
      onUpdateExternal: function BlogPostView_onUpdateExternal(postId)
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

      _onUpdated: function BlogPostView__onUpdated(response)
      {
         if (response.json.error != undefined)
         {
            Alfresco.util.PopupManager.displayMessage({text: this._msg("post.msg.unableUpdateExternal", response.json.error)});
         }
         else
         {
            Alfresco.util.PopupManager.displayMessage({text: this._msg("post.msg.updatedExternal")});

            // re-render the post
            this.loadBlogPostDataSuccess(response);
         }
      },    

      onUnpublishExternal: function BlogPostView_onUnpublishExternal(postId)
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
      
      _onUnpublished: function BlogPostView__onUnpublished(response)
      {
         if (response.json.error != undefined)
         {
            Alfresco.util.PopupManager.displayMessage({text: this._msg("post.msg.unableUnpublishExternal", response.json.error)});
         }
         else
         {
            Alfresco.util.PopupManager.displayMessage({text: this._msg("post.msg.unpublishExternal")});
            
            // re-render the post
            this.loadBlogPostDataSuccess(response);
         }
      },
      
      
      // mouse hover functionality
      
      /** Called when the mouse enters into a list item. */
      onPostElementMouseEntered: function BlogPostView_onListElementMouseEntered(layer, args)
      {
         var elem = args[1].target;
         YAHOO.util.Dom.addClass(elem, 'overNode');
         var editBloc = YAHOO.util.Dom.getElementsByClassName( 'nodeEdit' , null , elem, null );
         YAHOO.util.Dom.addClass(editBloc, 'showEditBloc');
      },
      
      /** Called whenever the mouse exits a list item. */
      onPostElementMouseExited: function BlogPostView_onListElementMouseExited(layer, args)
      {
         var elem = args[1].target;
         YAHOO.util.Dom.removeClass(elem, 'overNode');
         var editBloc = YAHOO.util.Dom.getElementsByClassName( 'nodeEdit' , null , elem , null );
         YAHOO.util.Dom.removeClass(editBloc, 'showEditBloc');
      },

   
      /**
       * PRIVATE FUNCTIONS
       */

      /**
       * Gets a custom message
       *
       * @method _msg
       * @param messageId {string} The messageId to retrieve
       * @return {string} The custom message
       * @private
       */
      _msg: function BlogPostView_msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, "Alfresco.BlogPostView", Array.prototype.slice.call(arguments).slice(1));
      }
   };
})();
