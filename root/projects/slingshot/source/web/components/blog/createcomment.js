/**
 * CreateComment component.
 * 
 * @namespace Alfresco
 * @class Alfresco.CreateComment
 */
(function()
{
   /**
   * YUI Library aliases
   */
   var Dom = YAHOO.util.Dom;
    
   /**
    * CreateComment constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.CreateComment} The new CreateComment instance
    * @constructor
    */
   Alfresco.CreateComment = function(htmlId)
   {
      /* Mandatory properties */
      this.name = "Alfresco.CreateComment";
      this.id = htmlId;
      
      /* Initialise prototype properties */
      this.widgets = {};
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["event", "json", "editor"], this.onComponentsLoaded, this);
      
      /* Decoupled event listeners */
      YAHOO.Bubbling.on("setCommentedNode", this.onSetCommentedNode, this);
      YAHOO.Bubbling.on("setCanCreateComment", this.onSetCanCreateComment, this);
      return this;
   };
   
   Alfresco.CreateComment.prototype =
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
          * Node reference of the item to comment about
          */
         itemNodeRef: null,
         
         /**
          * Title of the item to comment about for activites service.
          */
         activityTitle: null,
         
         /**
          * Page for activities link.
          */
         activityPage: null,

         /**
          * Params for activities link.
          */
         activityPageParams: null,

         /**
          * Width to use for comment editor
          */
         width: 538,
         
         /**
          * Height to use for comment editor
          */
         height: 250,
         
         /**
          * Tells whether the user can create comments.
          * The component is not displayed unless this field is true
          * 
          * @property canCreateComment
          * @type boolean
          * @default false
          */
         canCreateComment: false
      },
      
      /**
       * Object container for storing YUI widget instances.
       * 
       * @property widgets
       * @type object
       */
      widgets: null,
      
      /**
       * States whether the view has already been initialized
       *
       * @property initialized
       * @type boolean
       */
      initialized: false,

      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       */
      setOptions: function CreateComment_setOptions(obj)
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
      setMessages: function CreateComment_setMessages(obj)
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
      onComponentsLoaded: function CreateComment_onComponentsLoaded()
      {
      },

      /**
       * Called by a bubble event to set the node for which comments should be displayed
       */
      onSetCommentedNode: function CommentList_onSetCommentedNode(layer, args)
      {
         var obj = args[1];
         if ((obj !== null) && (obj.nodeRef !== null) && (obj.title !== null) && (obj.page !== null))
         {
            this.options.itemNodeRef = obj.nodeRef;
            this.options.activityTitle = obj.title;
            this.options.activityPage = obj.page;
            this.options.activityPageParams = obj.pageParams;
            this.initializeCreateCommentForm();
         }
      },
      
      /**
       * Called by a bubble event to set whether the user is allowed to comment.
       */
      onSetCanCreateComment: function CommentList_onSetCanCreateComment(layer, args)
      {

         var obj = args[1];
         if ((obj !== null) && (obj.canCreateComment !== null))
         {
            this.options.canCreateComment = obj.canCreateComment;
            this.initializeCreateCommentForm();
         }
      },
      
      /**
       * Initializes the create comment form.
       */
      initializeCreateCommentForm: function CreateComment_initializeCreateCommentForm()
      {
         // only continue if the user is allowed to create a comment
         if (!this.options.canCreateComment)
         {
            return;
         }

         // return if we have already been initialized
         if (this.initialized)
         {
            return;
         }
         this.initialized = true;
         
          
         // action url
         var form = Dom.get(this.id + '-form');         
         var actionUrl = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "api/node/{nodeRef}/comments",
         {
            nodeRef: this.options.itemNodeRef.replace(':/', '')
         });
         form.setAttribute("action", actionUrl);

         // nodeRef            
         Dom.get(this.id + '-nodeRef').setAttribute("value", this.options.itemNodeRef);
         
         // site
         Dom.get(this.id + '-site').setAttribute("value", this.options.siteId);
         
         // container
         Dom.get(this.id + '-container').setAttribute("value", this.options.containerId);
         
         // itemTitle
         Dom.get(this.id + '-itemTitle').setAttribute("value", this.options.activityTitle);

         // page
         Dom.get(this.id + '-page').setAttribute("value", this.options.activityPage);

         // pageParams
         Dom.get(this.id + '-pageParams').setAttribute("value", YAHOO.lang.JSON.stringify(this.options.activityPageParams));

         // register the behaviour with the form and display it finally
         this.registerCreateCommentForm();
      },
      
      /**
       * Registers the form with the html (that should be available in the page)
       * as well as the buttons that are part of the form.
       */
      registerCreateCommentForm: function CreateComment_registerCreateCommentForm()
      {
         // register the okButton
         this.widgets.okButton = new YAHOO.widget.Button(this.id + "-submit",
         {
            type: "submit"
         });

         // instantiate the simple editor we use for the form
         this.widgets.editor = new Alfresco.util.RichEditor(Alfresco.constants.HTML_EDITOR,this.id + '-content', this.options.editorConfig);
         this.widgets.editor.render();

         // Add validation to the yui editor
         this.widgets.validateOnZero = 0;
         var keyUpIdentifier = (Alfresco.constants.HTML_EDITOR === 'YAHOO.widget.SimpleEditor') ? 'editorKeyUp' : 'onKeyUp';
         this.widgets.editor.subscribe(keyUpIdentifier, function (e)
         {
            /**
             * Doing a form validation on every key stroke is process consuming, below we try to make sure we only do
             * a form validation if it's necessarry.
             * NOTE: Don't check for zero-length in commentsLength, due to HTML <br>, <span> tags, etc. possibly
             * being present. Only a "Select all" followed by delete will clean all tags, otherwise leftovers will
             * be there even if the form looks empty.
             */                       
            if (this.widgets.editor.getContent().length < 20 || this.widgets.okButton.get("disabled"))
            {
               // Submit was disabled and something has been typed, validate and submit will be enabled
               this.widgets.editor.save();
               this.widgets.commentForm.updateSubmitElements();
            }
         
         }, this, true);

         // create the form that does the validation/submit
         this.widgets.commentForm = new Alfresco.forms.Form(this.id + "-form");
         this.widgets.commentForm.setShowSubmitStateDynamically(true, false);
         this.widgets.commentForm.addValidation(this.id + "-content", Alfresco.forms.validation.mandatory, null);         
         this.widgets.commentForm.setSubmitElements(this.widgets.okButton);
         this.widgets.commentForm.setAJAXSubmit(true,
         {
            successMessage: this._msg("message.createcomment.success"),
            successCallback:
            {
               fn: this.onCreateFormSubmitSuccess,
               scope: this
            },
            failureMessage: this._msg("message.createcomment.failure"),
            failureCallback:
            {
               fn: function()
               {
                  this.enableInputs();
               },
               scope: this
            }
         });

         this.widgets.commentForm.setSubmitAsJSON(true);
         this.widgets.commentForm.doBeforeFormSubmit =
         {
            fn: function(form, obj)
            {
               //Put the HTML back into the text area
               this.widgets.editor.save();

               this.widgets.editor.disable();
               this.widgets.okButton.set("disabled", true);
               this.widgets.feedbackMessage = Alfresco.util.PopupManager.displayMessage(
               {
                  text: Alfresco.util.message("message.creating", this.name),
                  spanClass: "wait",
                  displayTime: 0
               });
            },
            scope: this
         };
         this.widgets.commentForm.init();
         
         // finally show the form
         var contanerElem = Dom.get(this.id + '-form-container');
         Dom.removeClass(contanerElem, 'hidden');
      },

      /**
       * Success handler for the form submit ajax request
       */
      onCreateFormSubmitSuccess: function CreateComment_onCreateFormSubmitSuccess(response, object)
      {
         // clear the content of the comment editor
         this.widgets.editor.clear();
         
         // reload the comments list
         YAHOO.Bubbling.fire("refreshComments",
         {
            reason: "created"
         });
         
         this.enableInputs();
      },

      /**
       * Reenables the inputs which got disabled as part of a comment submit
       */
      enableInputs: function CreateComment_enableInputs()
      {
         this.widgets.feedbackMessage.destroy();
         this.widgets.okButton.set("disabled", false);
         this.widgets.editor.enable();
      },

      /**
       * Gets a custom message
       *
       * @method _msg
       * @param messageId {string} The messageId to retrieve
       * @return {string} The custom message
       * @private
       */
      _msg: function CreateComment_msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, "Alfresco.CreateComment", Array.prototype.slice.call(arguments).slice(1));
      }
   };
})();
