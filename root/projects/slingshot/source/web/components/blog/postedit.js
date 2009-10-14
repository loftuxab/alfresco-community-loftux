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
   };
   
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
         Event.onContentReady(this.id, this.onReady, this, true);
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
            var data = response.json.item;
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
         var actionUrl, draft = true, title = "", content = "";
         if (this.options.editMode)
         {
            actionUrl = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "api/blog/post/node/{nodeRef}",
            {
               nodeRef: this.blogPostData.nodeRef.replace(':/', '')
            });
         }
         else
         {
            actionUrl = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "api/blog/site/{site}/{container}/posts",
            {
               site: this.options.siteId,
               container: this.options.containerId
            });
         }         
         Dom.get(this.id + '-form').setAttribute("action", actionUrl);

         // site and container
         Dom.get(this.id + '-site').setAttribute("value", this.options.siteId);
         Dom.get(this.id + '-container').setAttribute("value", this.options.containerId);
                  
         // draft
         if (this.options.editMode)
         {
            draft = this.blogPostData.isDraft;
         }
         Dom.get(this.id + '-draft').setAttribute("value", draft);
         
         // title
         if (this.options.editMode)
         {
            title = this.blogPostData.title;
         }
         Dom.get(this.id + '-title').setAttribute("value", title);
         
         // content
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
         this.modules.tagLibrary.setOptions(
         {
            siteId: this.options.siteId
         });
         
         // add the tags that are already set on the post
         if (this.options.editMode && this.blogPostData.tags.length > 0)
         {
            this.modules.tagLibrary.setTags(this.blogPostData.tags);
         }
         
         // create the Button
         this.widgets.saveButton = new YAHOO.widget.Button(this.id + "-save-button",
         {
            type: "submit",
            label: this._msg(this.options.editMode ? "action.update" : "action.saveAsDraft")
         });

         // publishing of a draft post button - only visible if post is a draft
         if (!this.options.editMode || this.blogPostData.isDraft)
         {
            this.widgets.publishButton = Alfresco.util.createYUIButton(this, "publish-button", this.onFormPublishButtonClick);
            Dom.removeClass(this.id + "-publish-button", "hidden");
         }
         
         // publishing internal and external button / update internal and publish external
         var publishExternalButtonLabel = '';
         if (!this.options.editMode)
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
         this.widgets.publishExternalButton = Alfresco.util.createYUIButton(this, "publishexternal-button", this.onFormPublishExternalButtonClick,
         {
            label: publishExternalButtonLabel
         });

         // Cancel button
         this.widgets.cancelButton = Alfresco.util.createYUIButton(this, "cancel-button", this.onFormCancelButtonClick);

         // Instantiate the simple editor we use for the form
         this.widgets.editor = new Alfresco.util.RichEditor(Alfresco.constants.HTML_EDITOR,this.id + '-content', this.options.editorConfig);
         this.widgets.editor.addPageUnloadBehaviour(this._msg("message.unsavedChanges.blog"));
         this.widgets.editor.render();

         // Add validation to the rich text editor
         this.widgets.validateOnZero = 0;
         var keyUpIdentifier = (Alfresco.constants.HTML_EDITOR === 'YAHOO.widget.SimpleEditor') ? 'editorKeyUp' : 'onKeyUp';         
         this.widgets.editor.subscribe(keyUpIdentifier, function (e)
         {
            this.widgets.validateOnZero++;
            YAHOO.lang.later(1000, this, this.validateAfterEditorChange);
         }, this, true);

         // Create the form that does the validation/submit
         this.widgets.postForm = new Alfresco.forms.Form(this.id + "-form");

         // Title is mandatory
         this.widgets.postForm.addValidation(this.id + "-title", Alfresco.forms.validation.mandatory, null, "blur");
         this.widgets.postForm.addValidation(this.id + "-title", Alfresco.forms.validation.nodeName, null, "keyup");
         this.widgets.postForm.addValidation(this.id + "-title", Alfresco.forms.validation.length,
         {
            max: 256,
            crop: true
         }, "keyup");

         // Text is mandatory
         this.widgets.postForm.addValidation(this.id + "-content", Alfresco.forms.validation.mandatory, null);

         this.widgets.postForm.setShowSubmitStateDynamically(true, false);
         if (this.widgets.publishButton)
         {
            this.widgets.postForm.setSubmitElements([this.widgets.saveButton, this.widgets.publishExternalButton, this.widgets.publishButton]);            
         }
         else
         {
            this.widgets.postForm.setSubmitElements([this.widgets.saveButton, this.widgets.publishExternalButton]);
         }
         this.widgets.postForm.setAJAXSubmit(true,
         {
            successCallback:
            {
               fn: this.onFormSubmitSuccess,
               scope: this
            },
            failureMessage: this._msg("message.savepost.failure"),
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
               this.widgets.editor.save();

               // Make sure the user has written a text
               if (Dom.get(this.id + '-content').value.length == 0)
               {
                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: Alfresco.util.message("message.noText", this.name)
                  });
                  return;
               }


                // disable ui elements
               this.widgets.saveButton.set("disabled", true);
               if (this.widgets.publishButton)
               {
                  this.widgets.publishButton.set("disabled", true);
               }
               this.widgets.publishExternalButton.set("disabled", true);
               this.widgets.cancelButton.set("disabled", true);

               // update the tags set in the form
               this.modules.tagLibrary.updateForm(this.id + "-form", "tags");
               
               // show a wait message
               this.widgets.feedbackMessage = Alfresco.util.PopupManager.displayMessage(
               {
                  text: Alfresco.util.message(this._msg("message.submitting")),
                  spanClass: "wait",
                  displayTime: 0
               });
            },
            scope: this
         };
         this.modules.tagLibrary.initialize(this.widgets.postForm);
         this.widgets.postForm.init();
         
         // finally display the form
         Dom.removeClass(this.id + "-div", "hidden");
         Dom.get(this.id + "-title").focus();
      },

      /**
       * Called when a key was pressed in the rich text editor.
       * Will trigger form validation after the last key stroke after a seconds pause.
       *
       * @method validateAfterEditorChange
       */
      validateAfterEditorChange: function()
      {
         this.widgets.validateOnZero--;
         if (this.widgets.validateOnZero == 0)
         {
            var oldLength = Dom.get(this.id + '-content').value.length;
            this.widgets.editor.save();
            var newLength = Dom.get(this.id + '-content').value.length;
            if ((oldLength == 0 && newLength != 0) || (oldLength > 0 && newLength == 0))
            {
               this.widgets.postForm.updateSubmitElements();
            }
         }
      },

      /**
       * Publish button click handler
       */
      onFormPublishButtonClick: function BlogPostEdit_onFormPublishButtonClick(type, args)
      {
         // make sure we set the draft flag to false
         Dom.get(this.id + "-draft").setAttribute("value", false);
          
         // submit the form
         this.widgets.saveButton.fireEvent("click",
         {
            type: "click"
         });
      },
      
      /**
       * Publish external button click handler
       */
      onFormPublishExternalButtonClick: function BlogPostEdit_onFormPublishExternalButtonClick(type, args)
      {
         // make sure we set the draft flag to false
         Dom.get(this.id + "-draft").setAttribute("value", false);
          
         // make sure that the post gets also externally published
         this.performExternalPublish = true;
          
         // submit the form
         this.widgets.saveButton.fireEvent("click",
         {
            type: "click"
         });
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
         // hide the wait message
         this.widgets.feedbackMessage.destroy();
         
         // check whether we have to do an external publich
         if (this.performExternalPublish)
         {
            // show a new wait message
            this.widgets.feedbackMessage = Alfresco.util.PopupManager.displayMessage(
            {
               text: Alfresco.util.message(this._msg("message.postSavedNowPublish")),
               spanClass: "wait",
               displayTime: 0
            });
             
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
            Alfresco.util.PopupManager.displayMessage(
            {
               text: this._msg("message.savepost.success")
            });
            this._loadPostViewPage(response.json.item.name);
         }
      },

      /**
       * Reenables the inputs which got disabled as part of a comment submit
       */
      onFormSubmitFailure: function BlogPostEdit_onFormSubmitFailure()
      {
         // enable the buttons
         this.widgets.saveButton.set("disabled", false);
         if (this.widgets.publishButton)
         {
            this.widgets.publishButton.set("disabled", false);
         }
         this.widgets.publishExternalButton.set("disabled", false);
         this.widgets.cancelButton.set("disabled", false);
         
         // hide the wait message
         this.widgets.feedbackMessage.destroy();
      },

      /**
       * Publishes the blog post to an external blog.
       */
      onPublishExternal: function BlogPostEdit_onPublishExternal(postId)
      {
         // publish request success handler
         var onPublished = function BlogPostEdit_onPublished(response)
         {
            this._loadPostViewPage(postId);
         };
         
         // publish request failure handler
         var onPublishFailed = function BlogPostEdit_onPublishFailed(response)
         {
            // let the user know that the publish failed, then redirect to the view page
            this.widgets.feedbackMessage.destroy();
            var me = this;
            Alfresco.util.PopupManager.displayPrompt(
            {
               text: this._msg("message.publishExternal.failure"),
               buttons: [
               {
                  text: this._msg('button.ok'),
                  handler: function()
                  {
                     me._loadPostViewPage(postId);
                  },
                  isDefault: true
               }]
            });
            
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
            successMessage: this._msg("message.publishExternal.success"),
            successCallback:
            {
               fn: onPublished,
               scope: this
            },
            failureCallback:
            {
               fn: onPublishFailed,
               scope: this
            }
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
            this._loadPostViewPage(postId);
         };
         
         // update request failure handler
         var onUpdateFailed = function BlogPostEdit_onUpdateFailed(response)
         {
            // let the user know that the publish failed, then redirect to the view page
            this.widgets.feedbackMessage.destroy();
            var me = this;
            Alfresco.util.PopupManager.displayPrompt(
            {
               text: this._msg("message.updateExternal.failure"),
               buttons: [
               {
                  text: this._msg('button.ok'),
                  handler: function()
                  {
                     me._loadPostViewPage(postId);
                  },
                  isDefault: true
               }]
            });
            
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
            successMessage: this._msg("message.updateExternal.success"),
            successCallback:
            {
               fn: onUpdated,
               scope: this
            },
            failureCallback:
            {
               fn: onUpdateFailed,
               scope: this
            }
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
