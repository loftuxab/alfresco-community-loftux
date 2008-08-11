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
    * Comment constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.CreateComment} The new CreateComment instance
    * @constructor
    */
   Alfresco.CreateComment = function(htmlId)
   {
      this.name = "Alfresco.CreateComment";
      this.id = htmlId;
      
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
          itemNodeRef: null,
          itemTitle: null,
          itemName: null
      },
      
      widgets: {},
      
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
         if ((obj !== null) && (obj.itemNodeRef !== null) && (obj.itemName !== null) && (obj.itemTitle !== null))
         {
            this.options.itemNodeRef = obj.itemNodeRef;
            this.options.itemName = obj.itemName;
            this.options.itemTitle = obj.itemTitle;
            this.initializeCreateCommentForm(); 
         }
      }, 
      
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
         var browseUrl = YAHOO.lang.substitute(Alfresco.constants.URL_PAGECONTEXT + "site/{site}/blog-postview?postId={itemName}",
         {
            site: this.options.siteId,
            itemName: this.options.itemName
         });
         browseItemUrlElem.setAttribute("value", browseUrl);
         
         // register the behaviour with the form / display the form
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
            height: '250px',
            width: '538px',
            dompath: false, //Turns on the bar at the bottom
            animate: false, //Animates the opening, closing and moving of Editor windows
            markup: "xhtml",
            toolbar:  Alfresco.util.editor.getTextOnlyToolbarConfig(this._msg)
         });
         this.widgets.editor.render();
         
         // create the form that does the validation/submit
         this.widgets.commentForm = new Alfresco.forms.Form(this.id + "-form");
         this.widgets.commentForm.setShowSubmitStateDynamically(true, false);
         this.widgets.commentForm.setSubmitElements(this.widgets.okButton);
         this.widgets.commentForm.setAJAXSubmit(true,
         {
            successCallback:
            {
               fn: this.onCreateFormSubmitSuccess,
               scope: this
            },
            failureCallback:
            {
               fn: this.onCreateFormSubmitFailure,
               scope: this
            }
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
       * Called when the form has been successfully submitted.
       */
      onCreateFormSubmitSuccess: function CreateComment_onCreateFormSubmitSuccess(response, object)
      {
         if (response.json.error != undefined)
         {
            Alfresco.util.PopupManager.displayMessage({text: this._msg("comments.msg.unableCreateComment", response.json.error)});
         }
         else
         {
            Alfresco.util.PopupManager.displayMessage({text: this._msg("comments.msg.commentCreated")});

            // clear the content of the comment editor
            this.widgets.editor.setEditorHTML('');
            
            // reload the comments list
            YAHOO.Bubbling.fire("refreshComments", {});
         }

      },
      
      /** Called when the form submit failed. */
      onCreateFormSubmitFailure: function CreateComment_onCreateFormSubmitFailure(response)
      {
         Alfresco.util.PopupManager.displayMessage({text: this._msg("comments.msg.failedCreateComment")});
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
