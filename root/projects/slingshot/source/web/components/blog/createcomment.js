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
   var Dom = YAHOO.util.Dom
    
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
      Alfresco.util.YUILoaderHelper.require(["event", "editor"], this.onComponentsLoaded, this);
      
      /* Decoupled event listeners */
      YAHOO.Bubbling.on("setCommentedNode", this.onSetCommentedNode, this);      
      
      return this;
   }
   
   Alfresco.CreateComment.prototype =
   {
      /**
       * Object container for initialization options
       *
       * @property options
       * @type object
       */
      options: {
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
          * Title of the item to comment about.
          * TODO: This is used for activity feed and should not be necessary here
          */
         itemTitle: null,
         
         /**
          * Url of the item to comment about.
          * TODO: This is used for activity feed and should not be necessary here
          */
         itemUrl: null,
         
         /**
          * Width to use for comment editor
          */
         width: 538,
         
         /**
          * Height to use for comment editor
          */
         height: 250
      },
      
      /**
       * Object container for storing YUI widget instances.
       * 
       * @property widgets
       * @type object
       */
      widgets: null,
      
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
         if ((obj !== null) && (obj.itemNodeRef !== null) && (obj.itemUrl !== null) && (obj.itemTitle !== null))
         {
            this.options.itemNodeRef = obj.itemNodeRef;
            this.options.itemUrl = obj.itemUrl;
            this.options.itemTitle = obj.itemTitle;
            this.initializeCreateCommentForm(); 
         }
      }, 
      
      /**
       * Initializes the create comment form.
       */
      initializeCreateCommentForm: function CreateComment_initializeCreateCommentForm()
      {
         // action url
         var form = Dom.get(this.id + '-form');         
         var actionUrl = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "api/node/{nodeRef}/comments",
         {
            nodeRef: this.options.itemNodeRef.replace(':/', '')
         });
         form.setAttribute("action", actionUrl);

         // nodeRef            
         var nodeRefElem = Dom.get(this.id + '-nodeRef');
         nodeRefElem.setAttribute("value", this.options.itemNodeRef);
         
         // site
         var siteElem = Dom.get(this.id + '-site');
         siteElem.setAttribute("value", this.options.siteId);
         
         // container
         var containerElem = Dom.get(this.id + '-container');
         containerElem.setAttribute("value", this.options.containerId);
         
         // itemTitle
         var itemTitleElem = Dom.get(this.id + '-itemTitle');
         itemTitleElem.setAttribute("value", this.options.itemTitle);
         
         // browseItemUrl
         var browseItemUrlElem = Dom.get(this.id + '-browseItemUrl');
         browseItemUrlElem.setAttribute("value", this.options.itemUrl);
         
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
         this.widgets.okButton = new YAHOO.widget.Button(this.id + "-submit", {type: "submit"});
         
         // instantiate the simple editor we use for the form
         this.widgets.editor = new YAHOO.widget.SimpleEditor(this.id + '-content', {
            height: this.options.height + 'px',
            width: this.options.width + 'px',
            dompath: false, //Turns on the bar at the bottom
            animate: false, //Animates the opening, closing and moving of Editor windows
            markup: "xhtml",
            toolbar:  Alfresco.util.editor.getTextOnlyToolbarConfig(this._msg)
         });
         this.widgets.editor._render();
         
         // create the form that does the validation/submit
         this.widgets.commentForm = new Alfresco.forms.Form(this.id + "-form");
         this.widgets.commentForm.setShowSubmitStateDynamically(true, false);
         this.widgets.commentForm.setSubmitElements(this.widgets.okButton);
         this.widgets.commentForm.setAJAXSubmit(true,
         {
            successMessage: this._msg("message.createcomment.success"),
            successCallback:
            {
               fn: this.onCreateFormSubmitSuccess,
               scope: this
            },
            failureMessage: this._msg("message.createcomment.failure")
         });
         this.widgets.commentForm.setSubmitAsJSON(true);
         this.widgets.commentForm.doBeforeFormSubmit =
         {
            fn: function(form, obj)
            {
               //Put the HTML back into the text area
               this.widgets.editor.saveHTML();
            },
            scope: this
         }
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
         this.widgets.editor.clearEditorDoc();
            
         // reload the comments list
         YAHOO.Bubbling.fire("refreshComments", {});
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
