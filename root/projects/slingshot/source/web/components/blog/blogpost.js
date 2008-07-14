/**
 * Post component.
 * 
 * @namespace Alfresco
 * @class Alfresco.BlogPost
 */
(function()
{
   /**
    * Post constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.PostView} The new Post instance
    * @constructor
    */
   Alfresco.BlogPost = function(htmlId)
   {
      this.name = "Alfresco.BlogPost";
      this.id = htmlId;
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["datasource", "json", "connection", "event", "button", "menu", "editor"], this.onComponentsLoaded, this);
      
      return this;
   }
   
   Alfresco.BlogPost.prototype =
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
         
         postId: "",
         
         postRef: "",
         
         /**
          * define which mode is currently used - could this be a 
          * create, view or edit
          */
         mode: ""
      },

      /** Reference to the Alfresco.forms.Form object if available. */   	
   	  postForm: null,
      /** Reference to the save button.
       * This is used by the save&publish actions, as those first adapt
       * the form state before triggering the submit action of the saveButton.
       */
   	  saveButton: null,

   	  /** Listens to taglibrary changes. */
   	  tagLibraryListener: null,
   	  
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
      setOptions: function BlogPost_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },
      
      setMessages: function BlogPost_setMessages(obj)
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
      onComponentsLoaded: function BlogPost_onComponentsLoaded()
      {
         YAHOO.util.Event.onContentReady(this.id, this.onReady, this, true);
      },
   
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function BlogPost_onReady()
      { 
         // don't do anything if we don't have a mode
         if ((this.options.mode === null) || (this.options.mode == ""))
         {
            return;
         }
         
         // check whether we are in edit mode, in this case we have to register the
         // form with the html
         if ((this.options.mode == "edit") || (this.options.mode == "create"))
         {
            this._registerPostForm();
         }

         // action hooks
         Alfresco.util.registerDefaultActionHandler(this.id, "blogpost-action-link", "div", this);
         
         // initialize the mouse over listener
         Alfresco.util.rollover.registerHandlerFunctions(this.id, this.onPostElementMouseEntered, this.onPostElementMouseExited);
         
         // as the list got already rendered on the server, already attach the listener to the rendered elements
         Alfresco.util.rollover.registerListenersByClassName(this.id, 'post', 'div');
      },

      /**
       * Registers the form with the htl (that should be available in the page)
       * as well as the buttons that are part of the form.
       */
      _registerPostForm: function BlogPost__registerPostForm()
      {  
         // register the tag listener
         this.tagLibraryListener = new Alfresco.TagLibraryListener(this.id+"-form", "tags");
          
         // register the Button
         var saveButtonId = this.id + "-save-button";
         this.saveButton = new YAHOO.widget.Button(saveButtonId, {type: "submit"});

         // publishing of a draft post button
         var publishButtonId = this.id + "-publish-button";
         if (YAHOO.util.Dom.get(publishButtonId) != null)
         {
            var publishButton = new YAHOO.widget.Button(publishButtonId, {type: "button"});
            publishButton.subscribe("click", this.onFormPublishButtonClick, this, true);
         }
         
         // publishing external as well
         var publishExternalButtonId = this.id + "-publishexternal-button";
         if (YAHOO.util.Dom.get(publishExternalButtonId) != null)
         {
            var publishExternalButton = new YAHOO.widget.Button(publishExternalButtonId, {type: "button"});
            publishExternalButton.subscribe("click", this.onFormPublishExternalButtonClick, this, true);
         }
                  
         // register the cancel button
         var cancelButton = new YAHOO.widget.Button(this.id + "-cancel-button", {type: "button"});
         cancelButton.subscribe("click", this.onFormCancelButtonClick, this, true);
         
         // instantiate the simple editor we use for the form
		 this.editor = new YAHOO.widget.SimpleEditor(this.id + '-content', {
		     height: '300px',
		     width: '538px',
		     dompath: false, //Turns on the bar at the bottom
		     animate: false, //Animates the opening, closing and moving of Editor windows
		     toolbar: Alfresco.util.editor.getTextOnlyToolbarConfig(this._msg)
		 });

		 this.editor.render();
         
         // create the form that does the validation/submit
         var postForm = new Alfresco.forms.Form(this.id + "-form");
         postForm.setShowSubmitStateDynamically(true, false);
         postForm.setSubmitElements(this.saveButton);
         postForm.setAJAXSubmit(true,
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
         if (this.options.mode != "create")
         {
             postForm.ajaxSubmitMethod = "PUT";
         }
         postForm.setSubmitAsJSON(true);
         postForm.doBeforeFormSubmit =
        	{
        	   fn: function(form, obj)
        	   {
			        //Put the HTML back into the text area
					this.editor.saveHTML();
					// update the tags set in the form
					this.tagLibraryListener.updateForm();
        	   },
        	   scope: this
         	}
         
         postForm.init();
         this.postForm = postForm;
      },
      
      onFormPublishButtonClick: function BlogPost_onFormSaveButtonClick(type, args)
      {
          // make sure we set the draft flag to false
          var draftElem = YAHOO.util.Dom.get(this.id + "-draft");
          draftElem.value=false;
          
          // submit the form
          this.saveButton.fireEvent("click");
      },
      
      onFormPublishExternalButtonClick: function BlogPost_onFormSaveButtonClick(type, args)
      {
          // make sure we set the draft flag to false
          var draftElem = YAHOO.util.Dom.get(this.id + "-draft");
          draftElem.value=false;
          
          // make sure that the post gets also externally published
          this.performExternalPublish = true;
          
          // submit the form
          this.saveButton.fireEvent("click");
      },
      
      onFormSubmitSuccess: function BlogPost_onFormSubmitSuccess(response)
      {
         // check whether we have to do an external publich
         if (this.performExternalPublish)
         {
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
             // simply show the view page
             Alfresco.util.blog.loadBlogPostViewPage(this.options.siteId, this.options.containerId, response.json.item.name);
         }
      },
      
      onFormSubmitFailure: function BlogPost_onFormSubmitFailure(response)
      {
         Alfresco.util.PopupManager.displayMessage({text: this._msg("post.msg.failedSubmit")});
      },
      
      onFormCancelButtonClick: function(type, args)
      {
         // redirect to the page we came from
         history.go(-1);
      },
      
      // Actions
      
      /**
       * Loads the edit post form and displays it instead of the content
       * The div class should have the same name as the above function (onEditNode)
       */
      onEditNode: function BlogPostList_onEditNode(htmlId, ownerId, param)
      {
         Alfresco.util.blog.loadBlogPostEditPage(this.options.siteId, this.options.containerId, param);
      },
      
      /**
       * Deletes a post.
       * The div class which contain the Delete link should have the same name as the above function (onEditNode)
       */
      onDeleteNode: function BlogPost_onDelete(htmlId, ownerId, param)
      {
         // make an ajax request to delete the post
         // we can directly go to alfresco for this
         Alfresco.util.Ajax.request(
		   {
		      url: Alfresco.constants.PROXY_URI + "blog/post/site/" + this.options.siteId + "/" + this.options.containerId + "/" + param,
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
         Alfresco.util.blog.loadBlogPostListPage(this.options.siteId, this.options.containerId);
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
          Alfresco.util.blog.loadBlogPostViewPage(this.options.siteId, this.options.containerId, response.json.item.name);
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
          Alfresco.util.blog.loadBlogPostViewPage(this.options.siteId, this.options.containerId, response.json.item.name);
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
      
      _onUnpublished: function BlogPost__onUnpublished(response)
      {
          Alfresco.util.PopupManager.displayMessage({text: "Unpublished!"});
          Alfresco.util.blog.loadBlogPostViewPage(this.options.siteId, this.options.containerId, response.json.item.name);
      },
      
      
      // mouse hover functionality
      
      /** Called when the mouse enters into a list item. */
      onPostElementMouseEntered: function BlogPostList_onListElementMouseEntered(layer, args)
      {
         var elem = args[1].target;
         YAHOO.util.Dom.addClass(elem, 'overNode');
         var editBloc = YAHOO.util.Dom.getElementsByClassName( 'nodeEdit' , null , elem, null );
         YAHOO.util.Dom.addClass(editBloc, 'showEditBloc');
      },
      
      /** Called whenever the mouse exits a list item. */
      onPostElementMouseExited: function BlogPostList_onListElementMouseExited(layer, args)
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
      _msg: function BlogPost_msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, "Alfresco.BlogPost", Array.prototype.slice.call(arguments).slice(1));
      }
   };
})();
