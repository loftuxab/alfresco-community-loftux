/**
 * BlogComment component.
 * 
 * Displays comments of a blog post.
 * The component does not really refer to the blog and could
 * be used in other cases as well.
 * 
 * @namespace Alfresco
 * @class Alfresco.BlogComment
 */
(function()
{
   /**
    * Comment constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.Comment} The new Comment instance
    * @constructor
    */
   Alfresco.BlogComment = function(htmlId)
   {
      this.name = "Alfresco.BlogComment";
      this.id = htmlId;
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["event", "editor"], this.onComponentsLoaded, this);
      
      return this;
   }
   
   Alfresco.BlogComment.prototype =
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
          * Stores the reference of the node for which to display the comments.
          */
         nodeRef: ""
      },
      
      /** root element of the current edit form. */
      editCommentFormElementId : null,
      
      /** Hidden comment view element, null if no comment edit
       * form is currently displayed.
       */
      hiddenViewElementId : null,
      
      /**
       * Set multiple initialization options at once.
       * 
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       */
      setOptions: function BlogComment_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },
      
      setMessages: function BlogComment_setMessages(obj)
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
      onComponentsLoaded: function BlogComment_onComponentsLoaded()
      {
         YAHOO.util.Event.onContentReady(this.id, this.onReady, this, true);
      },
   
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function BlogComment_onReady()
      { 
         // action hooks
         Alfresco.util.registerDefaultActionHandler(this.id, "blogcomment-action", "div", this);

         // initialize the mouse over listener
         Alfresco.util.rollover.registerHandlerFunctions(this.id, this.onCommentElementMouseEntered, this.onCommentElementMouseExited);
         
         // as the list got already rendered on the server, already attach the listener to the rendered elements
         Alfresco.util.rollover.registerListenersByClassName(this.id, 'comment', 'div');
      },      
      
      
      // Actions

      onEditComment: function BlogComment_onEditComment(htmlId, ownerId, param)
      {
         this._loadForm(param);
      },

      onDeleteComment: function BlogComment_onEditComment(htmlId, ownerId, param)
      {
         this._deleteComment(param);
      },


      // Action implementations
      
      _deleteComment: function BlogComment__deleteComment(escapedRef)
      {
         // make an ajax request to the repository to delete the post
         var url = Alfresco.constants.PROXY_URI + "/api/comment/node/" + Alfresco.util.noderefs.escapedToUrl(escapedRef);
         Alfresco.util.Ajax.request(
         {
            url: url,
            method: "DELETE",
            responseContentType : "application/json",
            successCallback:
            {
               fn: this._onDeleted,
               scope: this,
               obj: { escapedRef : escapedRef}
            },
            failureMessage: this._msg("comments.msg.failedDeleted")
         });
      },
      
      _onDeleted: function BlogComment__onDeleted(response, object)
      {
         if (response.json.error != undefined)
         {
            Alfresco.util.PopupManager.displayMessage({text: this._msg("comments.msg.unableDeleted", response.json.error)});
         }
         else
         {
            Alfresco.util.PopupManager.displayMessage({text: this._msg("comments.msg.deleted")});
          
            // reload the page
            location.reload(true);
         }
      },
      

      // Form management

      _loadForm: function BlogComment__loadForm(escapedRef)
      {   
         // make sure no other forms are displayed
         this._hideOpenForms();
          
         // load the form for the post
         var url = Alfresco.constants.URL_SERVICECONTEXT + "modules/blog/comments/comment-edit-form";
         Alfresco.util.Ajax.request(
         {
            url: url,
            dataObj:
            {
               htmlid : this.id,
               nodeRef : Alfresco.util.noderefs.unescape(escapedRef)
            },
            responseContentType : "application/json",
            successCallback:
            {
               fn: this._onFormLoaded,
               scope: this,
               obj : {
                  escapedRef : escapedRef
               }
            },
            failureMessage: this._msg("comments.msg.failedLoadEditForm")
         });
      },
      
      _onFormLoaded: function(response, object)
      {
         // ignore the loaded statement if the mode is already edit
         if (! this.isViewMode())
         {
             Alfresco.util.PopupManager.displayMessage({text: this._msg("comments.msg.alreadyEditing")});
             return;
         }
         
         var formDivId = "comment-edit-form-" + object.escapedRef;
         Alfresco.util.dom.updateAndShowDiv(formDivId, response.json.form);
         this.editCommentFormElementId = formDivId;
        
         // hide the view
         var viewDivId = "comment-" + object.escapedRef;
         Alfresco.util.dom.hideDiv(viewDivId);
         this.hiddenViewElementId = viewDivId;
             
         // register the form handling
         this._registerEditCommentForm(object.escapedRef, formDivId);
      },
      
      /**
       * Registers the form with the html (that should be available in the page)
       * as well as the buttons that are part of the form.
       */
      _registerEditCommentForm: function BlogComment__registerEditCommentForm(escapedRef, formDivId)
      {
         // base id for all elements on the form
         var formBaseId = this.id + "-" + escapedRef + "-editform";
          
         // register the okButton
         var okButton = new YAHOO.widget.Button(formBaseId + "-ok-button", {type: "submit"});
         
         // register the cancel button
         var cancelButton = new YAHOO.widget.Button(formBaseId + "-cancel-button", {type: "button"});
         cancelButton.subscribe("click", this.onEditFormCancelButtonClick, this, true);
         
         // instantiate the simple editor we use for the form
         this.editor = new YAHOO.widget.SimpleEditor(formBaseId + '-content', {
             height: '180px',
             width: '700px',
             dompath: false, //Turns on the bar at the bottom
             animate: false, //Animates the opening, closing and moving of Editor windows
             toolbar:  Alfresco.util.editor.getTextOnlyToolbarConfig(this._msg)
         });
         this.editor.render();
         
         // create the form that does the validation/submit
         var commentForm = new Alfresco.forms.Form(formBaseId);
         commentForm.setShowSubmitStateDynamically(true, false);
         commentForm.setSubmitElements(okButton);
         commentForm.setAJAXSubmit(true,
         {
            successCallback:
            {
               fn: this.onEditFormSubmitSuccess,
               scope: this,
               obj: { escapedRef : escapedRef }
            },
            failureCallback:
            {
               fn: this.onEditFormSubmitFailure,
               scope: this
            }
         });
         commentForm.setSubmitAsJSON(true);
         commentForm.doBeforeFormSubmit =
         {
              fn: function(form, obj)
              {
                //Put the HTML back into the text area
                this.editor.saveHTML();
              },
              scope: this
         }
         
         commentForm.init();
      },
      
      onEditFormSubmitSuccess: function BlogComment_onCreateFormSubmitSuccess(response, object)
      {
         if (response.json.error != undefined)
         {
            Alfresco.util.PopupManager.displayMessage({text: this._msg("comments.msg.submitErrorReturn", response.json.error)});
         }
         else
         {
            // fetch the div that contains the data for the post
            var divId = "comment-" + object.escapedRef;
            Alfresco.util.dom.updateAndShowDiv(divId, response.json.html);

            this._hideOpenForms();  
            Alfresco.util.PopupManager.displayMessage({text: this._msg("comments.msg.commentUpdated")});
         }
      },
      
      onEditFormSubmitFailure: function BlogComment_onCreateFormSubmitFailure(response)
      {
         Alfresco.util.PopupManager.displayMessage({text: this._msg("comments.msg.formSubmitFailed")});
      },
      
      
      onEditFormCancelButtonClick: function(type, args)
      {
          this._hideOpenForms();
      },
      
      /**
       * Makes sure that all forms get removed and if available the hidden content
       * elements displayed again.
       */
      _hideOpenForms: function()
      {
          if (this.editCommentFormElementId != null)
          {
              Alfresco.util.dom.hideAndRemoveDivContent(this.editCommentFormElementId);
              this.editCommentFormElementId = null;
          }
          if (this.hiddenViewElementId != null)
          {
              Alfresco.util.dom.showDiv(this.hiddenViewElementId);
              this.hiddenViewElementId = null;
          }
      },
      
      /** Returns whether the component is in view mode.
       * Returns false if any forms are currently displayed.
       */
      isViewMode: function()
      {
         return this.editCommentFormElement == null;
      },
      
      // showing / hiding of divs      
      
      /** Called when the mouse enters into a list item. */
      onCommentElementMouseEntered: function BlogComment_onListElementMouseEntered(layer, args)
      {
         var elem = args[1].target;
         YAHOO.util.Dom.addClass(elem, 'overNode');
         var editBloc = YAHOO.util.Dom.getElementsByClassName( 'nodeEdit' , null , elem, null );
         YAHOO.util.Dom.addClass(editBloc, 'showEditBloc');
      },
      
      /** Called whenever the mouse exits a list item. */
      onCommentElementMouseExited: function BlogComment_onListElementMouseExited(layer, args)
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
      _msg: function BlogComment_msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, "Alfresco.BlogComment", Array.prototype.slice.call(arguments).slice(1));
      }
      
   };
})();
