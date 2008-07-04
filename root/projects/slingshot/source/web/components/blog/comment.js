/**
 * Comment component.
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
          * Current siteId.
          * 
          * @property siteId
          * @type string
          */
         siteId: "",
         
         /**
          * Stores the reference of the post for which this comments component
          * displays comments.
          */
         nodeRef: ""
      },
      
      /** Stores the ref of the currently edited comment. */
      editCommentFormElementId : null,
      
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
         // Hook action events
         var me = this;
         YAHOO.Bubbling.addDefaultAction("action-link-"+this.id, function BlogComment_filterAction(layer, args)
         {
            var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "div");
            if (owner !== null)
            {
              //  alert(owner.className);
               var action = owner.className;
               
               var target = args[1].target;
               if (typeof me[action] == "function")
               {
                  // fetch the id of the comment,
                  var saveCommentRef = owner.id.substring((action + "-" + me.id + "-").length);
                  me[action].call(me, saveCommentRef);
                  args[1].stop = true;
               }
            }
      		 
            return true;
         });         
         
         // initialize the mouse over functionality for the list
         this.initMouseOverListeners();
      },      
      
      
      // Actions

      onEditComment: function BlogComment_onEditComment(saveCommentRef)
      {
         this._loadForm(saveCommentRef);
      },

      onDeleteComment: function BlogComment_onEditComment(saveCommentRef)
      {
         // make an ajax request to delete the post
         // we can directly go to alfresco for this
         Alfresco.util.Ajax.request(
		   {
		      url: Alfresco.constants.PROXY_URI + "/comment/node/" + this.convertSaveToUrlRef(saveCommentRef),
		      method: "DELETE",
		      responseContentType : "application/json",
		      successCallback:
		      {
		         fn: this._onDeleted,
		         scope: this,
		         obj: { saveCommentRef : saveCommentRef}
		      },
		      failureMessage: this._msg("comments.msg.failedDeleted2")
		   });
      },
      
      _onDeleted: function BlogComment__onDeleted(response, object)
      {
          Alfresco.util.PopupManager.displayMessage({text: this._msg("comments.msg.deleted")});
          
          // reload the page
          location.reload(true);
      },
      

      // Form management

      _loadForm: function BlogComment__loadForm(saveCommentRef)
      {   
          // make sure no other forms are displayed
          this._hideOpenForms();
          
          // load the form for the post
         Alfresco.util.Ajax.request(
		   {
		      url: Alfresco.constants.URL_SERVICECONTEXT + "modules/blog/comments/comment-edit-form",
		      dataObj:
		      {
		         htmlid : this.id,
		         nodeRef : this.convertSaveToNodeRef(saveCommentRef)
		      },
		      responseContentType : "application/json",
		      successCallback:
		      {
		         fn: this._onFormLoaded,
		         scope: this,
		         obj : { saveCommentRef : saveCommentRef }
		      },
		      failureMessage: this._msg("comments.msg.failedLoadPostForm")
		   });
      },
      
      _onFormLoaded: function(response, object)
	  {
	     // ignore the loaded statement if the mode is already edit
	     if (! this.isViewMode())
	     {
	         Alfresco.util.PopupManager.displayMessage({text: this._msg("comments.msg.editingTwice")});
	         return;
	     }
	     
         var formDivId = "comment-edit-form-" + object.saveCommentRef;
         this.updateAndShowDiv(formDivId, response.json.form);
         this.editCommentFormElementId = formDivId;
        
         // hide the view
         var viewDivId = "comment-" + object.saveCommentRef;
         this.hideDiv(viewDivId);
         this.hiddenViewElementId = viewDivId;
             
         // register the form handling
         this._registerEditCommentForm(object.saveCommentRef, formDivId);
	  },
      
      
      createSimpleEditor: function CreateComment_createSimpleEditor(textareaId)
      {
         // instantiate the simple editor we use for the form
		 var editor = new YAHOO.widget.SimpleEditor(textareaId, {
		     height: '180px',
		     width: '700px',
		     dompath: false, //Turns on the bar at the bottom
		     animate: false, //Animates the opening, closing and moving of Editor windows
		     toolbar: {
		        titlebar: false,
		        buttons: [
		            { group: 'textstyle', label: this._msg("comments.form.font"),
		                buttons: [
				            { type: 'push', label: 'Bold CTRL + SHIFT + B', value: 'bold' },
				            { type: 'push', label: 'Italic CTRL + SHIFT + I', value: 'italic' },
				            { type: 'push', label: 'Underline CTRL + SHIFT + U', value: 'underline' },
		                    { type: 'separator' },
		                    { type: 'color', label: 'Font Color', value: 'forecolor', disabled: true },
		                    { type: 'color', label: 'Background Color', value: 'backcolor', disabled: true }
		                ]
		            },
		            { type: 'separator' },
				    { group: 'indentlist', label: this._msg("comments.form.lists"),
				        buttons: [
				            { type: 'push', label: 'Create an Unordered List', value: 'insertunorderedlist' },
				            { type: 'push', label: 'Create an Ordered List', value: 'insertorderedlist' }
				        ]
				    },
				    { type: 'separator' },
				    { group: 'insertitem', label: this._msg("comments.form.link"),
				        buttons: [
				            { type: 'push', label: 'HTML Link CTRL + SHIFT + L', value: 'createlink', disabled: true }
				        ]
				    }
		        ]
		    }
		 });
		 return editor;
      },
      
      /**
       * Registers the form with the html (that should be available in the page)
       * as well as the buttons that are part of the form.
       */
      _registerEditCommentForm: function BlogComment__registerEditCommentForm(saveCommentRef, formDivId)
      {
         // base id for all elements on the form
         var formBaseId = this.id + "-" + saveCommentRef + "-editform";
          
         // register the okButton
         var okButton = new YAHOO.widget.Button(formBaseId + "-ok-button", {type: "submit"});
         
         // register the cancel button
         var cancelButton = new YAHOO.widget.Button(formBaseId + "-cancel-button", {type: "button"});
         cancelButton.subscribe("click", this.onEditFormCancelButtonClick, this, true);
         
         // instantiate the simple editor we use for the form
         this.editor = this.createSimpleEditor(formBaseId + '-content');
		 this.editor.render();
         
         // create the form that does the validation/submit
         var commentForm = new Alfresco.forms.Form(formBaseId);
         commentForm.setShowSubmitStateDynamically(true);
         commentForm.setSubmitElements(okButton);
         commentForm.setAJAXSubmit(true,
         {
            successCallback:
            {
               fn: this.onEditFormSubmitSuccess,
               scope: this,
               obj: { saveCommentRef : saveCommentRef }
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
         // fetch the div that contains the data for the post
         var divId = "comment-" + object.saveCommentRef;
         this.updateAndShowDiv(divId, response.json.html);

         this._hideOpenForms();
                
         Alfresco.util.PopupManager.displayMessage({text: "Comment updated"});
      },
      
      onEditFormSubmitFailure: function BlogComment_onCreateFormSubmitFailure(response)
      {
         Alfresco.util.PopupManager.displayMessage({text: "Updating comment failed"});
      },
      
      
      onEditFormCancelButtonClick: function(type, args)
      {
          this._hideOpenForms();
      },
      
      // Misc
      
      _hideOpenForms: function()
      {
          if (this.editCommentFormElementId != null)
          {
              this.hideAndRemoveDivContent(this.editCommentFormElementId);
              this.editCommentFormElementId = null;
          }
          if (this.hiddenViewElementId != null)
          {
              this.showDiv(this.hiddenViewElementId);
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
      
      getSaveNodeRef: function(nodeRef)
      {
          return nodeRef.replace(/\:\/\//, "_").replace(/\//, "_");
      },
      
      convertSaveToNodeRef: function(saveNodeRef)
      {
          return saveNodeRef.replace(/_/, "://").replace(/_/, "/");
      },
      
      convertSaveToUrlRef: function(saveNodeRef)
      {
          return saveNodeRef.replace(/_/g, "/");
      },
      
      // showing / hiding of divs
      
      /**
       * Updates a div content and makes sure the div is displayed
       */
      updateAndShowDiv: function BlogComment_updateAndShowDiv(divId, newHTML)
      {
          var elem = YAHOO.util.Dom.get(divId);
          elem.innerHTML = newHTML;
	      YAHOO.util.Dom.removeClass(elem, "hidden");
      },

      showDiv: function BlogComment_updateAndShowDiv(divId)
      {
          var elem = YAHOO.util.Dom.get(divId);
	      YAHOO.util.Dom.removeClass(elem, "hidden");
      },
      
      hideDiv: function BlogComment_hideDiv(divId)
      {
          var elem = YAHOO.util.Dom.get(divId);
	      YAHOO.util.Dom.addClass(elem, "hidden");
      },
      
      hideAndRemoveDivContent: function BlogComment_removeDivContent(divId)
      {
          var elem = YAHOO.util.Dom.get(divId);
	      YAHOO.util.Dom.addClass(elem, "hidden");
          elem.innerHTML = "";
      },
      
      
      
      // Overlay functionality      
      /**
       * Attaches a listener to all passed elements.
       */
      _attachRolloverListener: function(elem, mouseOverEventName, mouseOutEventName)
      {  
         var eventElem = elem;
         
         var mouseOverHandler = function(e)
         {
             // find out whether we actually moved inside the 
             if (! e) var e = window.event;
             var relTarg = e.relatedTarget || e.fromElement;
             while (relTarg != null && relTarg != eventElem && relTarg.nodeName != 'BODY') {
                relTarg = relTarg.parentNode
             }
             if (relTarg == eventElem) return;
             
             // the mouse entered the element, fire an event to inform about it
             YAHOO.Bubbling.fire(mouseOverEventName, {event : e, target : eventElem});
         };
         
         var mouseOutHandler = function(e)
         {
             // find out whether we actually moved inside the 
             if (! e) var e = window.event;
             var relTarg = e.relatedTarget || e.toElement;
             while (relTarg != null && relTarg != eventElem && relTarg.nodeName != 'BODY') {
                relTarg = relTarg.parentNode
             }
             if (relTarg == eventElem) return;
             
             // the mouse exited the element, fire an event to inform about it
             YAHOO.Bubbling.fire(mouseOutEventName, {event : e, target : eventElem});
         };
         
         YAHOO.util.Event.addListener(elem, 'mouseover', mouseOverHandler);
         YAHOO.util.Event.addListener(elem, 'mouseout', mouseOutHandler);
      },
      
      firstMouseOverInit: true,
      
      initMouseOverListeners: function BlogComment_initMouseOverListeners()
      {
         var mouseEnteredBubbleEventName = 'onCommentsElementMouseEntered';
         var mouseExitedBubbleEventName = 'onCommentsElementMouseExited';
         var divs = YAHOO.util.Dom.getElementsByClassName('comment', 'div');
         for (var x=0; x < divs.length; x++) {
             this._attachRolloverListener(divs[x], mouseEnteredBubbleEventName, mouseExitedBubbleEventName);
         }
         
         if (this.firstMouseOverInit) {
            this.firstMouseOverInit = false;
            // manage mouse hover/exit
            YAHOO.Bubbling.on(mouseEnteredBubbleEventName, this.onCommentElementMouseEntered, this);
            YAHOO.Bubbling.on(mouseExitedBubbleEventName, this.onCommentElementMouseExited, this);
         }
      },
      
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
