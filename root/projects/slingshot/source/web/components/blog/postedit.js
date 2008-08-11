/**
 * BlogPostEdit component.
 * 
 * Component provides blog post creation/edit functionality.s
 * 
 * @namespace Alfresco
 * @class Alfresco.BlogPostEdit
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
   Alfresco.BlogPostEdit = function(htmlId)
   {
      this.name = "Alfresco.BlogPostEdit";
      this.id = htmlId;
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["datasource", "json", "connection", "event", "button", "menu", "editor"], this.onComponentsLoaded, this);
      
      return this;
   }
   
   Alfresco.BlogPostEdit.prototype =
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
          * True if the component should be in edit mode.
          */
         editMode: false,
         
         /**
          * Id of the post to edit. Only relevant if editMode is true
          */
         postId: ""
      },

      /**
       * Object container for storing YUI widget instances.
       * 
       * @property widgets
       * @type object
       */
      widgets : {},

      modules : {},

      /**
       * Stores the data of the currently edited blog post
       */
      blogPostData: null,
        
      /**
       * If true, an external publish will be executed once the
       * post has been saved.
       */
      performExternalPublish: false,
      
      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       */
      setOptions: function BlogPostEdit_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },
      
      setMessages: function BlogPostEdit_setMessages(obj)
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
      onComponentsLoaded: function BlogPostEdit_onComponentsLoaded()
      {
         YAHOO.util.Event.onContentReady(this.id, this.onReady, this, true);
      },
   
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function BlogPostEdit_onReady()
      { 
         if (this.options.editMode)
         {
            // load the blog post data
            this._loadBlogPostData();
         }
         else
         {
            this._initializeBlogPostForm();
         }
      },

      /**
       * Loads the comments for the provided nodeRef and refreshes the ui
       */
      _loadBlogPostData: function BlogPostEdit__loadBlogPostData()
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
         
         // TODO: make sure to only call this function if data has been loaded successfully
         this._initializeBlogPostForm();
      },   

      _initializeBlogPostForm: function BlogPostEdit__initializeBlogPostForm()
      {
         var actionUrl = '';
         if (this.options.editMode)
         {
            var actionUrl = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "api/blog/post/node/{nodeRef}",
            {
               nodeRef: this.blogPostData.nodeRef.replace(':/', '')
            });
         }
         else
         {
            var actionUrl = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "api/blog/site/{site}/{container}/posts",
            {
               site: this.options.siteId,
               container: this.options.containerId
            });
         }         
         var form = Dom.get(this.id + '-form');
         form.setAttribute("action", actionUrl);

         // site and container
         Dom.get(this.id + '-site').setAttribute("value", this.options.siteId);
         Dom.get(this.id + '-container').setAttribute("value", this.options.containerId);
                  
         // browsePostUrl
         var browseUrl = YAHOO.lang.substitute(Alfresco.constants.URL_PAGECONTEXT + "/site/${site}/blog-postview?container=${container}&amp;postId={post.name}", // post.name will be replaced by the rest api
         {
            site: this.options.siteId,
            container: this.options.containerId
         });
         Dom.get(this.id + '-browsePostUrl').setAttribute("value", browseUrl);
         
         // draft
         var draft = true;
         if (this.options.editMode)
         {
            draft = this.blogPostData.isDraft;
         }
         Dom.get(this.id + '-draft').setAttribute("value", draft);
         
         // title
         var title = '';
         if (this.options.editMode)
         {
            title = this.blogPostData.title;
         }
         Dom.get(this.id + '-title').setAttribute("value", title);
         
         // content
         var content = '';
         if (this.options.editMode)
         {
            content = this.blogPostData.content;
         }
         Dom.get(this.id + '-content').value = content;
         
         // register the behaviour with the form / display the form
         this._registerBlogPostForm();
      },

      /**
       * Registers the form with the htl (that should be available in the page)
       * as well as the buttons that are part of the form.
       */
      _registerBlogPostForm: function BlogPostEdit__registerPostForm()
      {  
         // register the tag listener
         this.modules.tagLibraryListener = new Alfresco.TagLibraryListener(this.id+"-form", "tags");
         
         // add the tags that are already set on the post
         if (this.options.editMode && this.blogPostData.tags.length > 0)
         {
            // find the tag library component
            var taglibrary = Alfresco.util.ComponentManager.findFirst("Alfresco.TagLibrary");
            taglibrary.addTags(this.blogPostData.tags);
         }
         
         // register the Button
         var saveButtonLabel = '';
         if (this.options.editMode)
         {
            saveButtonLabel = this._msg('post.form.update');
         }
         else
         {
            saveButtonLabel = this._msg('post.form.saveAsDraft');
         }
         this.widgets.saveButton = new YAHOO.widget.Button(this.id + "-save-button", {type: "submit", label: saveButtonLabel });

         // publishing of a draft post button - only visible if post is a draft
         if ((! this.options.editMode) || (this.blogPostData.isDraft))
         {
            var publishButtonElem = YAHOO.util.Dom.get(this.id + "-publish-button");
            this.widgets.publishButton = new YAHOO.widget.Button(this.id + "-publish-button", {type: "button"});
            this.widgets.publishButton.subscribe("click", this.onFormPublishButtonClick, this, true);
            Dom.removeClass(publishButtonElem, "hidden");
         }
         
         // publishing internal and external button / update internal and publish external
         var publishExternalButtonLabel = ''
         if (! this.options.editMode)
         {
            publishExternalButtonLabel = this._msg('post.form.publishIntAndExt');
         }
         else if (this.blogPostData.isPublished)
         {
            publishExternalButtonLabel = this._msg('post.form.updateIntAndExt');
         }
         else
         {
            publishExternalButtonLabel = this._msg('post.form.updateIntAndPublishExt');
         }
         this.widgets.publishExternalButton = new YAHOO.widget.Button(this.id + "-publishexternal-button", {type: "button", label: publishExternalButtonLabel });
         this.widgets.publishExternalButton.subscribe("click", this.onFormPublishExternalButtonClick, this, true);         
                  
         // register the cancel button
         this.widgets.cancelButton = new YAHOO.widget.Button(this.id + "-cancel-button", {type: "button"});
         this.widgets.cancelButton.subscribe("click", this.onFormCancelButtonClick, this, true);
         
         // instantiate the simple editor we use for the form
         this.widgets.editor = new YAHOO.widget.SimpleEditor(this.id + '-content', {
            height: '300px',
            width: '538px',
            dompath: false, //Turns on the bar at the bottom
            animate: false, //Animates the opening, closing and moving of Editor windows
            markup: "xhtml",
            toolbar: Alfresco.util.editor.getTextOnlyToolbarConfig(this._msg)
         });

         this.widgets.editor.render();
         
         // create the form that does the validation/submit
         this.widgets.postForm = new Alfresco.forms.Form(this.id + "-form");
         this.widgets.postForm.setShowSubmitStateDynamically(true, false);
         this.widgets.postForm.setSubmitElements(this.widgets.saveButton);
         this.widgets.postForm.setAJAXSubmit(true,
         {
            successCallback:
            {
               fn: this.onFormSubmitSuccess,
               scope: this
            },
            failureCallback:
            {
               fn: this.onFormSubmitFailure,
               scope: this
            }
         });
         if (this.options.editMode)
         {
             this.widgets.postForm.setAjaxSubmitMethod(Alfresco.util.Ajax.PUT);
         }
         this.widgets.postForm.setSubmitAsJSON(true);
         this.widgets.postForm.doBeforeFormSubmit =
         {
            fn: function(form, obj)
            {
               //Put the HTML back into the text area
               this.widgets.editor.saveHTML();
               // update the tags set in the form
               this.modules.tagLibraryListener.updateForm();
            },
            scope: this
         }
         
         this.widgets.postForm.init();
         
         // finally display the form
         var containerElem = YAHOO.util.Dom.get(this.id + "-container");
         Dom.removeClass(containerElem, "hidden");
      },
      
      onFormPublishButtonClick: function BlogPostEdit_onFormSaveButtonClick(type, args)
      {
         // make sure we set the draft flag to false
         var draftElem = YAHOO.util.Dom.get(this.id + "-draft");
         draftElem.value=false;
          
         // submit the form
         this.widgets.saveButton.fireEvent("click");
      },
      
      onFormPublishExternalButtonClick: function BlogPostEdit_onFormSaveButtonClick(type, args)
      {
         // make sure we set the draft flag to false
         var draftElem = YAHOO.util.Dom.get(this.id + "-draft");
         draftElem.value=false;
          
         // make sure that the post gets also externally published
         this.performExternalPublish = true;
          
         // submit the form
         this.widgets.saveButton.fireEvent("click");
      },
      
      onFormSubmitSuccess: function BlogPostEdit_onFormSubmitSuccess(response)
      {
         if (response.json.error != undefined)
         {
            Alfresco.util.PopupManager.displayMessage({text: this._msg("post.msg.errorFormSubmit", response.json.error)});
            return;
         }
          
         // check whether we have to do an external publich
         if (this.performExternalPublish)
         {
            Alfresco.util.PopupManager.displayMessage({text: this._msg("post.msg.postSavedNowPublish")});             
             
            //var nodeRef = response.json.item.nodeRef;    
            var postId = response.json.item.name;
            if (response.json.item.isPublished)
            {
               // perform an update
               this.onUpdateExternal(postId);
            }
            else
            {
               // perform a publish
               this.onPublishExternal(postId);
            }
         }
         else
         {
            Alfresco.util.PopupManager.displayMessage({text: this._msg("post.msg.postSaved")});
            this._loadPostViewPage(response.json.item.name);
         }
      },
      
      onFormSubmitFailure: function BlogPostEdit_onFormSubmitFailure(response)
      {
         Alfresco.util.PopupManager.displayMessage({text: this._msg("post.msg.failedFormSubmit")});
      },
      
      onFormCancelButtonClick: function(type, args)
      {
         // redirect to the page we came from
         history.go(-1);
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
      
      onPublishExternal: function Blog_onPublishExternal(postId)
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
      
      _onPublished: function Blog__onPublished(response)
      {
         if (response.json.error != undefined)
         {
            Alfresco.util.PopupManager.displayMessage({text: this._msg("post.msg.unablePublishExternal", response.json.error)});
         }
         else
         {
            Alfresco.util.PopupManager.displayMessage({text: this._msg("post.msg.publishedExternal")});
            this._loadPostViewPage(response.json.item.name);
         }
      },
     
      onUpdateExternal: function Blog_onUpdateExternal(postId)
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

      _onUpdated: function Blog__onUpdated(response)
      {
         if (response.json.error != undefined)
         {
            Alfresco.util.PopupManager.displayMessage({text: this._msg("post.msg.unableUpdateExternal", response.json.error)});
         }
         else
         {
            Alfresco.util.PopupManager.displayMessage({text: this._msg("post.msg.updatedExternal")});
            this._loadPostViewPage(response.json.item.name);
         }
      },    

      
      /**
       * PRIVATE FUNCTIONS
       */
          
      /**
       * Loads the blog post view page
       */
      _loadPostViewPage: function BlogPostEdit__loadPostViewPage(postId)
      {
         var url = YAHOO.lang.substitute(Alfresco.constants.URL_CONTEXT + "page/site/{site}/blog-postview?container={container}&postId={postId}",
         {
            site: this.options.siteId,
            container: this.options.containerId,
            postId: postId
         });
         window.location = url;
      },

      /**
       * Gets a custom message
       *
       * @method _msg
       * @param messageId {string} The messageId to retrieve
       * @return {string} The custom message
       * @private
       */
      _msg: function BlogPostEdit_msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, "Alfresco.BlogPostEdit", Array.prototype.slice.call(arguments).slice(1));
      }
   };
})();
