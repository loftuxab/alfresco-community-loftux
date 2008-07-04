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
         // Hook action events
         var me = this;
         YAHOO.Bubbling.addDefaultAction("action-link-"+this.id, function DiscussionsReplies_filterAction(layer, args)
         {
            var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "div");
            if (owner !== null)
            {
               var action = owner.className;
               
               var target = args[1].target;
               if (typeof me[action] == "function")
               {
                  // fetch the id of the reply,
                  var elemId = owner.id.substring((action + "-" + me.id + "-").length);
                  me[action].call(me, elemId);
                  args[1].stop = true;
               }
            }
      		 
            return true;
         });
         
         // Hide the children replies
         YAHOO.Bubbling.addDefaultAction("showHideReply", function DiscussionsReplies_showHideReply(layer, args)
         {
	      	var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "a");
	      	var action = owner.className;
	      	var indentedID = "replies-of-" + owner.id.substring((action + "-").length);
	      	var elem = YAHOO.util.Dom.get(indentedID);
	      	if (YAHOO.util.Dom.hasClass(elem, "hidden")) {
	      		YAHOO.util.Dom.removeClass(elem, "hidden");
	      		owner.innerHTML ="Hide";
	      		
	      	} else {
	      		YAHOO.util.Dom.addClass(elem, "hidden");
	      		owner.innerHTML = "Show";
	      	}
	      	return true;
         });
         
         // an external event can trigger a reply form to be opened
         YAHOO.Bubbling.on("onAddReplyToPost", this.onAddReplyToTopic, this);
         
         // initialize the mouse over functionality for the list
         this.initMouseOverListeners();
      },
      
      
      // Actions
      
      /** Triggers the opening of a reply form. */
      onAddReplyToTopic: function DiscussionsTopicList_onListElementMouseEntered(layer, args)
      {
         this.onAddReply(this.getSaveNodeRef(args[1].parentPostRef));
      },
      
      /**
       * Loads the edit reply form and displays it instead of the reply 
       */
      onAddReply: function DiscussionsReplies_onEdit(replyRef)
      {
         this._loadForm(replyRef, false);
      },

      onEditReply: function DiscussionsReplies_onEditReply(replyRef)
      {
         this._loadForm(replyRef, true);
      },

      onDeleteReply: function DiscussionsReplies_onEditReply(replyRef)
      {
         // make an ajax request to delete the topic
         // we can directly go to alfresco for this
         Alfresco.util.Ajax.request(
		   {
		      url: Alfresco.constants.PROXY_URI + "forum/post/node/" + this.convertSaveToUrlRef(replyRef),
		      method: "DELETE",
		      responseContentType : "application/json",
		      /*dataObj:
		      {
		         site   : this.options.siteId,
		         container : "discussions",
		         path : this.options.topicId
		      },*/
		      successCallback:
		      {
		         fn: this._onDeleted,
		         scope: this,
		         obj: { replyRef : replyRef}
		      },
		      failureMessage: this._msg("replies.msg.failedDeleted2")
		   });
      },
      
      _onDeleted: function DiscussionsTopic__onDeleted(response, object)
      {
         if (response.json.error == undefined)
         {
            //this.hideDiv("reply-" + object.replyRef);
            //this.hideDiv("replies-of-" + object.replyRef);
            Alfresco.util.PopupManager.displayMessage({text: this._msg("replies.msg.deleted")});
            
            // reload the page
            location.reload(true);
         }
         else
         {
            Alfresco.util.PopupManager.displayMessage({text: this._msg("replies.msg.failedDeleted1") + response.json.error});
         }
      },
      

      // Form management

      // CREATE / EDIT MODE
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
		         postRef : this.convertSaveToNodeRef(postRef),
		         isEdit : isEditReply
		      },
		      responseContentType : "application/json",
		      successCallback:
		      {
		         fn: this._onFormLoaded,
		         scope: this,
		         obj : { isEditReply : isEditReply, postRef : postRef }
		      },
		      failureMessage: this._msg("replies.msg.failedLoadTopicForm")
		   });
      },
      
      _onFormLoaded: function(response, object)
	  {
	     // ignore the loaded statement if the mode is already edit
	     if (this.isViewMode())
	     {
	        // check whether we actually got an error back
 	        if (response.json.error != undefined)
	        {
               Alfresco.util.PopupManager.displayMessage({text: this._msg("replies.msg.failedLoadReplyForm")  + response.json.error});   
               return;
	        }
	     
	        // find corre
	        if (! object.isEditReply)
	        {
   	            var formDivId = "reply-add-form-" + object.postRef;
                //var formElem = YAHOO.util.Dom.get(formDivId);
                this.updateAndShowDiv(formDivId, response.json.form);
	            this.addReplyFormElementId = formDivId;
	                            
       	        // register the form handling
	            this._registerReplyForm(object.postRef, formDivId, object.isEditReply);
	        }
	        else
	        {
   	            var formDivId = "reply-edit-form-" + object.postRef;
                //var formElem = YAHOO.util.Dom.get(formDivId);
                this.updateAndShowDiv(formDivId, response.json.form);
	            this.editReplyFormElementId = formDivId;
	            
                // hide the view
                var viewDivId = "reply-" + object.postRef;
                this.hideDiv(viewDivId);
	            this.hiddenViewElementId = viewDivId;
                     
       	        // register the form handling
	            this._registerReplyForm(object.postRef, formDivId, object.isEditReply);
	        }
	     }
	     else
	     {
            Alfresco.util.PopupManager.displayMessage({text: this._msg("replies.msg.editingTwice")});
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
		     toolbar: {
		        titlebar: false,
		        buttons: [
		            { group: 'textstyle', label: this._msg("replies.form.font"),
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
				    { group: 'indentlist', label: this._msg("replies.form.lists"),
				        buttons: [
				            { type: 'push', label: 'Create an Unordered List', value: 'insertunorderedlist' },
				            { type: 'push', label: 'Create an Ordered List', value: 'insertorderedlist' }
				        ]
				    },
				    { type: 'separator' },
				    { group: 'insertitem', label: this._msg("replies.form.link"),
				        buttons: [
				            { type: 'push', label: 'HTML Link CTRL + SHIFT + L', value: 'createlink', disabled: true }
				        ]
				    }
		        ]
		    }
		 });
		 this.editor.render();
         
         // create the form that does the validation/submit
         var replyForm = new Alfresco.forms.Form(this.id + "-" + replyRef + "-form");
         replyForm.setShowSubmitStateDynamically(true);
         replyForm.setSubmitElements(okButton);
         replyForm.setAJAXSubmit(true,
         {
            successCallback:
            {
               fn: this.onFormSubmitSuccess,
               scope: this,
               obj: { replyRef : replyRef, isEditReply : isEditReply }
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
				//The var html will now have the contents of the textarea
				var html = this.editor.get('element').value;
       	   },
       	   //obj: myArbitraryObject,
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
         }
         else
         {
            if (! object.isEditReply)
            {
                // fetch the div that contains the replies for the element
                /*var divId = "replies-of-" + object.replyRef;
                var elem = YAHOO.util.Dom.get(divId);
                elem.innerHTML = elem.innerHTML + response.json.reply;

                // remove the form
                this._hideOpenForms();*/
                
                Alfresco.util.PopupManager.displayMessage({text: this._msg("replies.msg.added")});
                
                //history.go(0);
                location.reload(true);
            }
            else
            {
                // fetch the div that contains the data for the post
                var divId = "reply-" + object.replyRef;
                this.updateAndShowDiv(divId, response.json.reply);

                this._hideOpenForms();
                
                Alfresco.util.PopupManager.displayMessage({text: this._msg("replies.msg.updated")});
            }
         }
      },
      
      onFormSubmitFailure: function DiscussionsTopic_onFormSubmitFailure(response)
      {
         Alfresco.util.PopupManager.displayMessage({text: this._msg("replies.msg.failed")});
         //document.location.href = Alfresco.constants.URL_CONTEXT + "page/collaboration-dashboard?site=" + response.json.shortName;
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
              this.hideAndRemoveDivContent(this.addReplyFormElementId);
              this.addReplyFormElementId = null;
          }
          if (this.editReplyFormElementId != null)
          {
              this.hideAndRemoveDivContent(this.editReplyFormElementId);
              this.editReplyFormElementId = null;
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
          return this.addReplyFormElement == null
       	      && this.editReplyFormElement == null
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
      updateAndShowDiv: function DiscussionsReplies_updateAndShowDiv(divId, newHTML)
      {
          var elem = YAHOO.util.Dom.get(divId);
          elem.innerHTML = newHTML;
	      YAHOO.util.Dom.removeClass(elem, "hidden");
      },

      showDiv: function DiscussionsReplies_updateAndShowDiv(divId)
      {
          var elem = YAHOO.util.Dom.get(divId);
	      YAHOO.util.Dom.removeClass(elem, "hidden");
      },
      
      hideDiv: function DiscussionsReplies_hideDiv(divId)
      {
          var elem = YAHOO.util.Dom.get(divId);
	      YAHOO.util.Dom.addClass(elem, "hidden");
      },
      
      hideAndRemoveDivContent: function DiscussionsReplies_removeDivContent(divId)
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
      
      initMouseOverListeners: function DiscussionsTopicList_initMouseOverListeners()
      {
         var mouseEnteredBubbleEventName = 'onRepliesElementMouseEntered';
         var mouseExitedBubbleEventName = 'onRepliesElementMouseExited';
         var divs = YAHOO.util.Dom.getElementsByClassName('reply', 'div');
         for (var x=0; x < divs.length; x++) {
             this._attachRolloverListener(divs[x], mouseEnteredBubbleEventName, mouseExitedBubbleEventName);
         }
         
         if (this.firstMouseOverInit) {
            this.firstMouseOverInit = false;
            // manage mouse hover/exit
            YAHOO.Bubbling.on(mouseEnteredBubbleEventName, this.onReplyElementMouseEntered, this);
            YAHOO.Bubbling.on(mouseExitedBubbleEventName, this.onReplyElementMouseExited, this);
         }
      },
      
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
      _msg: function DiscussionsReplies_msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, "Alfresco.DiscussionsReplies", Array.prototype.slice.call(arguments).slice(1));
      }
      
   };
})();
