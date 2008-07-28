/**
 * Reply component.
 * 
 * @namespace Alfresco
 * @class Alfresco.DiscussionsReplies
 */
(function()
{
   /**
    * Reply constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.Reply} The new Reply instance
    * @constructor
    */
   Alfresco.DiscussionsReplies = function(htmlId)
   {
      this.name = "Alfresco.DiscussionsReplies";
      this.id = htmlId;
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["event"], this.onComponentsLoaded, this);
      
      return this;
   }
   
   Alfresco.DiscussionsReplies.prototype =
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
         
         containerId: "discussions",
         
         path: "",
         
         /**
          * Stores the reference of the topic for which this replies component
          * displays replies.
          */
         topicRef: ""
      },
      
      /** Stores the ref of the currently edited reply. */
      editReplyFormElementId : null,
      
      /** Stores the ref of reply for which a reply form is opened. */
      addReplyFormElementId : null,
      
      hiddenViewElementId : null,
      
      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       */
      setOptions: function DiscussionsReplies_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },
      
      setMessages: function DiscussionsReplies_setMessages(obj)
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
      onComponentsLoaded: function DiscussionsReplies_onComponentsLoaded()
      {
         YAHOO.util.Event.onContentReady(this.id, this.onReady, this, true);
      },
   
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function DiscussionsReplies_onReady()
      {   
         // Hide the children replies
         YAHOO.Bubbling.addDefaultAction("showHideReply", function DiscussionsReplies_showHideReply(layer, args)
         {
            var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "a");
            var action = owner.className;
            var indentedID = "replies-of-" + owner.id.substring((action + "-").length);
            var elem = YAHOO.util.Dom.get(indentedID);
            if (YAHOO.util.Dom.hasClass(elem, "hidden")) {
               YAHOO.util.Dom.removeClass(elem, "hidden");
               owner.innerHTML = this._msg("replies.footer.hide");
            } else {
               YAHOO.util.Dom.addClass(elem, "hidden");
               owner.innerHTML = this._msg("replies.footer.show");
            }
            return true;
         });
         
         // an external event can trigger a reply form to be opened
         YAHOO.Bubbling.on("onAddReplyToPost", this.onAddReplyToTopic, this);
         
         // default action handler
         Alfresco.util.registerDefaultActionHandler(this.id, 'reply-action-link', 'div', this);
          
         // initialize the mouse over listener
         Alfresco.util.rollover.registerHandlerFunctions(this.id, this.onReplyElementMouseEntered, this.onReplyElementMouseExited);
       
         // as the list got already rendered on the server, already attach the listener to the rendered elements
         Alfresco.util.rollover.registerListenersByClassName(this.id, 'reply', 'div');  
      },
      
      
      // Actions
      
      /** Triggers the opening of a reply form. */
      onAddReplyToTopic: function DiscussionsTopicList_onListElementMouseEntered(layer, args)
      {
         this._loadForm(Alfresco.util.noderefs.escape(args[1].parentPostRef), false);
      },
      
      /**
       * Loads the edit reply form and displays it instead of the reply 
       */
      onAddReply: function DiscussionsReplies_onEdit(htmlId, ownerId, param)
      {
         this._loadForm(param, false);
      },

      onEditReply: function DiscussionsReplies_onEditReply(htmlId, ownerId, param)
      {
         this._loadForm(param, true);
      },

      onDeleteReply: function DiscussionsReplies_onEditReply(htmlId, ownerId, param)
      {
         this._deleteReply(param);
      },
      
      
      // Actions implementation
      
      _deleteReply: function DiscussionsReplies__deleteReply(replyRef)
      {
         // make an ajax request to delete the topic
         var url = Alfresco.constants.PROXY_URI + "api/forum/post/node/" + Alfresco.util.noderefs.escapedToUrl(replyRef);
         Alfresco.util.Ajax.request(
         {
            url: url,
            method: "DELETE",
            responseContentType : "application/json",
            successCallback:
            {
               fn: this._onDeleted,
               scope: this,
               obj: { replyRef : replyRef}
            },
            failureMessage: this._msg("replies.msg.failedDelete")
         });
      },
      
      _onDeleted: function DiscussionsTopic__onDeleted(response, object)
      {
         if (response.json.error == undefined)
         {
            Alfresco.util.PopupManager.displayMessage({text: this._msg("replies.msg.deleted")});
            
            // reload the page
            location.reload(true);
         }
         else
         {
            Alfresco.util.PopupManager.displayMessage({text: this._msg("replies.msg.unableDelete") + response.json.error});
         }
      },
      

      // Form management

      _loadForm: function DiscussionsReplies__loadForm(postRef, isEditReply)
      {   
         // make sure no other forms are displayed
         this._hideOpenForms();
          
         // load the form for the topic
         Alfresco.util.Ajax.request(
         {
            url: Alfresco.constants.URL_SERVICECONTEXT + "modules/discussions/replies/get-reply-form",
            dataObj:
            {
               htmlid : this.id,
               site: this.options.siteId,
               container: this.options.containerId,
               path: this.options.path,
               postRef : Alfresco.util.noderefs.unescape(postRef),
               isEdit : isEditReply
            },
            responseContentType : "application/json",
            successCallback:
            {
               fn: this._onFormLoaded,
               scope: this,
               obj : {
                  isEditReply : isEditReply,
                  postRef : postRef
               }
            },
            failureMessage: this._msg("replies.msg.failedloadeditform")
         });
      },
      
      _onFormLoaded: function(response, object)
      {
         // ignore the loaded statement if the mode is already edit
         if (! this.isViewMode())
         {
            Alfresco.util.PopupManager.displayMessage({text: this._msg("replies.msg.wrongmode")});
            return;
         }

         // check whether we actually got an error back
         if (response.json.error != undefined)
         {
            Alfresco.util.PopupManager.displayMessage({text: this._msg("replies.msg.unableloadeditform", response.json.error)});   
            return;
         }
        
         // check whether it is an edit or reply form, add to correct
         // parent
         if (object.isEditReply === false)
         {
            var formDivId = "reply-add-form-" + object.postRef;
            //var formElem = YAHOO.util.Dom.get(formDivId);
            Alfresco.util.dom.updateAndShowDiv(formDivId, response.json.form);
            this.addReplyFormElementId = formDivId;
                               
            // register the form handling
            this._registerReplyForm(object.postRef, formDivId, object.isEditReply);
         }
         else
         {
            var formDivId = "reply-edit-form-" + object.postRef;
            Alfresco.util.dom.updateAndShowDiv(formDivId, response.json.form);
            this.editReplyFormElementId = formDivId;
               
            // hide the view
            var viewDivId = "reply-" + object.postRef;
            Alfresco.util.dom.hideDiv(viewDivId);
            this.hiddenViewElementId = viewDivId;
                     
            // register the form handling
            this._registerReplyForm(object.postRef, formDivId, object.isEditReply);
         }
      },
      
      /**
       * Registers the form with the html (that should be available in the page)
       * as well as the buttons that are part of the form.
       */
      _registerReplyForm: function DiscussionsTopic__registerTopicForm(replyRef, formDivId, isEditReply)
      {
         // register the okButton
         var okButton = new YAHOO.widget.Button(this.id + "-" + replyRef + "-ok-button", {type: "submit"});
         
         // register the cancel button
         var cancelButton = new YAHOO.widget.Button(this.id + "-" + replyRef + "-cancel-button", {type: "button"});
         cancelButton.subscribe("click", this.onFormCancelButtonClick, this, true);
         
         // instantiate the simple editor we use for the form
         this.editor = new YAHOO.widget.SimpleEditor(this.id + '-replyContent', {
            height: '250px',
            width: '538px',
            dompath: false, //Turns on the bar at the bottom
            animate: false, //Animates the opening, closing and moving of Editor windows
            markup: "xhtml",
            toolbar: Alfresco.util.editor.getTextOnlyToolbarConfig(this._msg)
         });
         this.editor.render();
         
         // create the form that does the validation/submit
         var replyForm = new Alfresco.forms.Form(this.id + "-" + replyRef + "-form");
         replyForm.setShowSubmitStateDynamically(true, false);
         replyForm.setSubmitElements(okButton);
         replyForm.setAJAXSubmit(true,
         {
            successCallback:
            {
               fn: this.onFormSubmitSuccess,
               scope: this,
               obj: {
                  replyRef : replyRef,
                  isEditReply : isEditReply
               }
            },
            failureCallback:
            {
               fn: this.onFormSubmitFailure,
               scope: this
            }
         });
         replyForm.setSubmitAsJSON(true);
         replyForm.doBeforeFormSubmit =
         {
            fn: function(form, obj)
            {
               //Put the HTML back into the text area
               this.editor.saveHTML();
            },
            scope: this
         }
         
         replyForm.init();
      },
      
      onFormSubmitSuccess: function DiscussionsTopic_onFormSubmitSuccess(response, object)
      {
         // check whether we got an error
         if (response.json.error != undefined)
         {
            Alfresco.util.PopupManager.displayMessage({text:  this._msg("replies.msg.error") + response.json.error});
            return;
         }

         if (! object.isEditReply)
         {
            Alfresco.util.PopupManager.displayMessage({text: this._msg("replies.msg.added")});
               
            //history.go(0);
            location.reload(true);
         }
         else
         {
            // fetch the div that contains the data for the post
            var divId = "reply-" + object.replyRef;
            Alfresco.util.dom.updateAndShowDiv(divId, response.json.reply);
            this._hideOpenForms();
                
            Alfresco.util.PopupManager.displayMessage({text: this._msg("replies.msg.updated")});
         }
      },
      
      onFormSubmitFailure: function DiscussionsTopic_onFormSubmitFailure(response)
      {
         Alfresco.util.PopupManager.displayMessage({text: this._msg("replies.msg.submitfailed")});
      },
      
      onFormCancelButtonClick: function(type, args)
      {
         this._hideOpenForms();
      },
      
      
      // Misc
      
      _hideOpenForms: function()
      {
         if (this.addReplyFormElementId != null)
         {
            Alfresco.util.dom.hideAndRemoveDivContent(this.addReplyFormElementId);
            this.addReplyFormElementId = null;
         }
         if (this.editReplyFormElementId != null)
         {
            Alfresco.util.dom.hideAndRemoveDivContent(this.editReplyFormElementId);
            this.editReplyFormElementId = null;
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
         return this.addReplyFormElement == null
                && this.editReplyFormElement == null
      },
      
      
      // mouse hover functionality
      
      /** Called when the mouse enters into a list item. */
      onReplyElementMouseEntered: function DiscussionsTopicList_onListElementMouseEntered(layer, args)
      {
         var elem = args[1].target;
         YAHOO.util.Dom.addClass(elem, 'overNode');
         var editBloc = YAHOO.util.Dom.getElementsByClassName( 'nodeEdit' , null , elem, null );
         YAHOO.util.Dom.addClass(editBloc, 'showEditBloc');
      },
      
      /** Called whenever the mouse exits a list item. */
      onReplyElementMouseExited: function DiscussionsTopicList_onListElementMouseExited(layer, args)
      {
         var elem = args[1].target;
         YAHOO.util.Dom.removeClass(elem, 'overNode');
         var editBloc = YAHOO.util.Dom.getElementsByClassName( 'nodeEdit' , null , elem , null );
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
      _msg: function DiscussionsReplies_msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, "Alfresco.DiscussionsReplies", Array.prototype.slice.call(arguments).slice(1));
      }
      
   };
})();
