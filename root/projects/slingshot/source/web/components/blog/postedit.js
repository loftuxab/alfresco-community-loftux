/**
 * BlogPostEdit component.
 * 
 * Component provides blog post creation/edit functionality.
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
    * BlogPostEdit constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.BlogPostEdit} The new Post instance
    * @constructor
    */
   Alfresco.BlogPostEdit = function(htmlId)
   {
      /* Mandatory properties */
      this.name = "Alfresco.BlogPostEdit";
      this.id = htmlId;
      
      /* Initialise prototype properties */
      this.widgets = {};
      this.modules = {};
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["json", "connection", "event", "button", "menu", "editor"], this.onComponentsLoaded, this);
      
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
         
         /**
          * ContainerId representing root container
          *
          * @property containerId
          * @type string
          * @default "blog"
          */
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
      widgets: null,

      /**
       * Object container for storing module instances.
       * 
       * @property modules
       * @type object
       */
      modules: null,

      /**
       * Stores the data of the currently edited blog post
       */
      blogPostData: null,
        
      /**
       * If true, an external publish/update will be executed after the post has been
       * saved/updated.
       */
      performExternalPublish: false,
      
      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.BlogPostEdit} returns 'this' for method chaining
       */
      setOptions: function BlogPostEdit_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },
      
      /**
       * Set messages for this component.
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {Alfresco.BlogPostEdit} returns 'this' for method chaining
       */
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
            // load the blog post data prior to initializing the form
            this._loadBlogPostData();
         }
         else
         {
            // directly initialize the form
            this._initializeBlogPostForm();
         }
      },

      /**
       * Loads the comments for the provided nodeRef and refreshes the ui
       */
      _loadBlogPostData: function BlogPostEdit__loadBlogPostData()
      {
         // ajax request success handler
         var me = this;
         var loadBlogPostDataSuccess = function CommentsList_loadCommentsSuccess(response)
         {
            // set the blog data
            var data = response.json.item
            me.blogPostData = data;
            
            // now initialize the form, which will use the data we just loaded
            me._initializeBlogPostForm();
         };
         
         // construct the request url
         var url = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "api/blog/post/site/{site}/{container}/{postId}",
         {
            site : this.options.siteId,
            container: this.options.containerId,
            postId: this.options.postId
         });
         
         // execute ajax request
         Alfresco.util.Ajax.request(
         {
            url: url,
            method: "GET",
            responseContentType : "application/json",
            successCallback:
            {
               fn: loadBlogPostDataSuccess,
               scope: this
            },
            failureMessage: this._msg("message.loadpostdata.failure")
         });
      },

      /**
       * Initializes the blog post form with create/edit dependent data.
       */
      _initializeBlogPostForm: function BlogPostEdit__initializeBlogPostForm()
      {
         // construct the actionUrl, which is different for creating/updating a post
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
         
         // register the behaviour with the form and display the form
         this._registerBlogPostForm();
      },

      /**
       * Registers the form logic
       */
      _registerBlogPostForm: function BlogPostEdit__registerPostForm()
      {
         // initialize the tag library
         this.modules.tagLibrary = new Alfresco.module.TagLibrary(this.id);
         this.modules.tagLibrary.setOptions({ siteId: this.options.siteId });
         this.modules.tagLibrary.initialize();
          
         // add the tags that are already set on the post
         if (this.options.editMode && this.blogPostData.tags.length > 0)
         {
            this.modules.tagLibrary.setTags(this.blogPostData.tags);
         }
         
         // create the Button
         var saveButtonLabel = '';
         if (this.options.editMode)
         {
            saveButtonLabel = this._msg('action.update');
         }
         else
         {
            saveButtonLabel = this._msg('action.saveAsDraft');
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
            publishExternalButtonLabel = this._msg('action.publishIntAndExt');
         }
         else if (this.blogPostData.isPublished)
         {
            publishExternalButtonLabel = this._msg('action.updateIntAndExt');
         }
         else
         {
            publishExternalButtonLabel = this._msg('action.updateIntAndPublishExt');
         }
         this.widgets.publishExternalButton = new YAHOO.widget.Button(this.id + "-publishexternal-button", {type: "button", label: publishExternalButtonLabel });
         this.widgets.publishExternalButton.subscribe("click", this.onFormPublishExternalButtonClick, this, true);         
                  
         // cancel button
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
         this.widgets.editor._render();
         
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
            failureMessage: this._msg("message.savepost.failure")
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
               this.modules.tagLibrary.updateForm(this.id + "-form", "tags");
            },
            scope: this
         }
         this.widgets.postForm.init();
         
         // finally display the form
         var containerElem = YAHOO.util.Dom.get(this.id + "-container");
         Dom.removeClass(containerElem, "hidden");
      },
      
      /**
       * Publish button click handler
       */
      onFormPublishButtonClick: function BlogPostEdit_onFormPublishButtonClick(type, args)
      {
         // make sure we set the draft flag to false
         var draftElem = YAHOO.util.Dom.get(this.id + "-draft");
         draftElem.value=false;
          
         // submit the form
         this.widgets.saveButton.fireEvent("click");
      },
      
      /**
       * Publish external button click handler
       */
      onFormPublishExternalButtonClick: function BlogPostEdit_onFormPublishExternalButtonClick(type, args)
      {
         // make sure we set the draft flag to false
         var draftElem = YAHOO.util.Dom.get(this.id + "-draft");
         draftElem.value=false;
          
         // make sure that the post gets also externally published
         this.performExternalPublish = true;
          
         // submit the form
         this.widgets.saveButton.fireEvent("click");
      },
      
      /**
       * Cancel button click handler
       */
      onFormCancelButtonClick: function BlogPostEdit_onFormCancelButtonClick(type, args)
      {
         // redirect to the page we came from
         history.go(-1);
      },
      
      /**
       * Form submit success handler
       */
      onFormSubmitSuccess: function BlogPostEdit_onFormSubmitSuccess(response)
      {          
         // check whether we have to do an external publich
         if (this.performExternalPublish)
         {
            Alfresco.util.PopupManager.displayMessage({text: this._msg("message.postSavedNowPublish")});             
             
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
            Alfresco.util.PopupManager.displayMessage({text: this._msg("message.savepost.success")});
            this._loadPostViewPage(response.json.item.name);
         }
      },

      /**
       * Publishes the blog post to an external blog.
       */
      onPublishExternal: function BlogPostEdit_onPublishExternal(postId)
      {
         // publish request success handler
         var onPublished = function BlogPostEdit_onPublished(response)
         {
            this._loadPostViewPage(response.json.item.name);
         };
         
         // get the url to call
         var url = Alfresco.util.blog.generatePublishingRestURL(this.options.siteId, this.options.containerId, postId);
         
         // execute ajax request
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
            successMessage: this._msg("message.publishedExternal.success"),
            successCallback:
            {
               fn: onPublished,
               scope: this
            },
            failureMessage: this._msg("message.publishExternal.failure")
         });
      },
      
      /**
       * Updates the external published blog post.
       */
      onUpdateExternal: function BlogPostEdit_onUpdateExternal(postId)
      {
         // update request success handler
         var onUpdated = function BlogPostEdit_onUpdated(response)
         {
            this._loadPostViewPage(response.json.item.name);
         };
         
         // get the url to call
         var url = Alfresco.util.blog.generatePublishingRestURL(this.options.siteId, this.options.containerId, postId);
         
         // execute ajax request
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
            successMessage: this._msg("message.updatedExternal.success"),
            successCallback:
            {
               fn: onUpdated,
               scope: this
            },
            failureMessage: this._msg("message.updateExternal.failure")
         });
      },
      
      
      /**
       * PRIVATE FUNCTIONS
       */
          
      /**
       * Loads the blog post view page
       */
      _loadPostViewPage: function BlogPostEdit__loadPostViewPage(postId)
      {
         window.location = Alfresco.util.blog.generateBlogPostViewUrl(this.options.siteId, this.options.containerId, postId);
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
