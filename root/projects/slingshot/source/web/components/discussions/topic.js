/**
 * Topic component.
 * 
 * @namespace Alfresco
 * @class Alfresco.DiscussionsTopic
 */
(function()
{
   /**
    * Topic constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.TopicView} The new Topic instance
    * @constructor
    */
   Alfresco.DiscussionsTopic = function(htmlId)
   {
      this.name = "Alfresco.DiscussionsTopic";
      this.id = htmlId;
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["datasource", "json", "connection", "event", "button", "menu", "editor"], this.onComponentsLoaded, this);
      
      return this;
   }
   
   Alfresco.DiscussionsTopic.prototype =
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
         
         topicId: "",
         
         topicRef: "",
         
         /**
          * define which mode is currently used - could this be a 
          * create, view or edit
          */
         mode: ""
      },
   	
      
      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       */
      setOptions: function DiscussionsTopic_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },
      
      setMessages: function DiscussionsTopic_setMessages(obj)
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
      onComponentsLoaded: function DiscussionsTopic_onComponentsLoaded()
      {
         YAHOO.util.Event.onContentReady(this.id, this.onReady, this, true);
      },
   
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function DiscussionsTopic_onReady()
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
            this._registerTopicForm();
         }
         
         
         // only initialize action hooks and roll-over if we have a topic
         // otherwise it's just a create form
         if (this.options.topicId.length > 0)
         {
             // Hook action events
             var me = this;
             YAHOO.Bubbling.addDefaultAction("action-link-"+this.options.topicId, function DiscussionsTopicList_filterAction(layer, args)
             {
                var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "div");
                if (owner !== null)
                {
                   var action = owner.className;
                   var target = args[1].target;
                   if (typeof me[action] == "function")
                   {
                      // extract the id from the element
                      var elemId = owner.id.substring((action + "-").length);
                      me[action].call(me, elemId);
                      args[1].stop = true;
                   }
                }
          		 
                return true;
             });
             
             // initialize the mouse over functionality for the list
             this.initMouseOverListeners();
         }
      },

      /**
       * Called when the add reply button is clicked
       */
      onAddReply: function DiscussionsTopic_onEdit(elemId)
      {
      	YAHOO.Bubbling.fire('onAddReplyToPost', {parentPostRef : this.options.topicRef});
      },
      
      /**
       * Loads the edit topic form and displays it instead of the content
       * The div class should have the same name as the above function (onEditNode)
       */
      onEditNode: function DiscussionsTopic_onEdit(elemId)
      {
         this._loadForm();
      },
      
      
      /**
       * Deletes a topic.
       * The div class which contain the Delete link should have the same name as the above function (onEditNode)
       */
      onDeleteNode: function DiscussionsTopic_onDeletee(elemId)
      {
         // make an ajax request to delete the topic
         // we can directly go to alfresco for this
         Alfresco.util.Ajax.request(
		   {
		       // PENDING: needs cleanup
		      url: Alfresco.constants.PROXY_URI + "/forum/post/site/" + this.options.siteId + "/discussions/" + this.options.topicId,
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
		         scope: this
		      },
		      failureMessage: this._msg("topic.msg.failedDelete")
		   });
      },

      _onDeleted: function DiscussionsTopic__onDeleted(response)
      {
         if (response.json.error == undefined)
         {
            // redirect to the list page
            window.location = Alfresco.constants.URL_CONTEXT + "page/site/" + this.options.siteId + "/discussions-topiclist";
         }
         else
         {
            Alfresco.util.PopupManager.displayMessage({text: this._msg("topic.msg.unableDelete") + response.json.error});
         }
      },

      /**
       * Updates a div content and makes sure the div is displayed
       */
      updateAndShowDiv: function DiscussionsTopic_updateAndShowDiv(divId, newHTML)
      {
          var elem = YAHOO.util.Dom.get(divId);
          elem.innerHTML = newHTML;
	      YAHOO.util.Dom.removeClass(elem, "hidden");
      },
      
      showDiv: function DiscussionsTopic_updateAndShowDiv(divId)
      {
          var elem = YAHOO.util.Dom.get(divId);
	      YAHOO.util.Dom.removeClass(elem, "hidden");
      },
      
      hideDiv: function DiscussionsTopic_hideDiv(divId)
      {
          var elem = YAHOO.util.Dom.get(divId);
	      YAHOO.util.Dom.addClass(elem, "hidden");          
      },


      // CREATE / EDIT MODE

      _loadForm: function DiscussionsTopic__loadForm()
      {
          // load the form for the topic
         Alfresco.util.Ajax.request(
		   {
		      url: Alfresco.constants.URL_SERVICECONTEXT + "modules/discussions/topic/get-topic-form",
		      dataObj:
		      {
		         site   : this.options.siteId,
		         htmlId : this.id,
		         topicId : this.options.topicId
		      },
		      responseContentType : "application/json",
		      successCallback:
		      {
		         fn: this._onFormLoaded,
		         scope: this,
		         object : { mode : "edit" }
		      },
		      failureMessage: this._msg("topic.msg.failedLoad1")
		   });
      },

      _onFormLoaded: function(response, object)
	  {
	     // ignore the loaded statement if the mode is already edit
	     if (this.options.mode == "view")
	     {
	        // check whether we actually got an error back
 	        if (response.json.error != undefined)
	        {
               Alfresco.util.PopupManager.displayMessage({text: this._msg("topic.msg.failedLoad2") + response.json.error});   
               return;
	        }
	     
	        // update the mode
	        this.options.mode = "edit";
	        
	        // hide the view element
	        this.hideDiv(this.id + "-viewDiv");
	        
	        // insert the new html and display it
	        this.updateAndShowDiv(this.id + "-formDiv", response.json.form);
	        
	        // register the form handling
	        this._registerTopicForm();
	     }
	     else
	     {
            Alfresco.util.PopupManager.displayMessage({text: this._msg("topic.msg.unknownMode")});
	     }
	  },

      /**
       * Registers the form with the html (that should be available in the page)
       * as well as the buttons that are part of the form.
       */
      _registerTopicForm: function DiscussionsTopic__registerTopicForm()
      {
         // register the okButton
         var okButton = new YAHOO.widget.Button(this.id + "-ok-button", {type: "submit"});
         
         // register the cancel button
         var cancelButton = new YAHOO.widget.Button(this.id + "-cancel-button", {type: "button"});
         cancelButton.subscribe("click", this.onFormCancelButtonClick, this, true);
         
         // instantiate the simple editor we use for the form
		 this.editor = new YAHOO.widget.SimpleEditor(this.id + '-content', {
		     height: '300px',
		     width: '538px',
		     dompath: false, //Turns on the bar at the bottom
		     animate: false, //Animates the opening, closing and moving of Editor windows
		     toolbar: {
		        titlebar: false,
		        buttons: [
		            { group: 'textstyle', label: this._msg("topic.form.font"),
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
				    { group: 'indentlist', label: this._msg("topic.form.list"),
				        buttons: [
				            { type: 'push', label: 'Create an Unordered List', value: 'insertunorderedlist' },
				            { type: 'push', label: 'Create an Ordered List', value: 'insertorderedlist' }
				        ]
				    },
				    { type: 'separator' },
				    { group: 'insertitem', label: this._msg("topic.form.link"),
				        buttons: [
				            { type: 'push', label: 'HTML Link CTRL + SHIFT + L', value: 'createlink', disabled: true }
				        ]
				    }
		        ]
		    }
		 });
		 this.editor.render();
         
         // create the form that does the validation/submit
         var topicForm = new Alfresco.forms.Form(this.id + "-form");
         topicForm.setShowSubmitStateDynamically(true, false);
         topicForm.setSubmitElements(okButton);
         topicForm.setAJAXSubmit(true,
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
         topicForm.setSubmitAsJSON(true);
         
         /* The following code is required to write back the editor content to the text area
        	Added doBeforeFormSubmit overridable function to forms-runtime.
        	e.g. (myForm is Alfresco.forms.Form)
        	myForm.doBeforeFormSubmit =
        	{
        	   fn: function(form, obj)
        	   {
        	      ... last chance form processing ...
        	   },
        	   obj: myArbitraryObject,
        	   scope: this
         	}
          */
          
          topicForm.doBeforeFormSubmit =
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
         
         topicForm.init();
      },
      
      onFormSubmitSuccess: function DiscussionsTopic_onFormSubmitSuccess(response)
      {
         // check whether we got an error
         if (response.json.error != undefined)
         {
            Alfresco.util.PopupManager.displayMessage({text: this._msg("topic.msg.error") + response.json.error});
         }
         else
         {
            // check whether we are in create or edit mode. In case of create we will redirect
            // to the view page
            if (this.options.mode == "create")
            {
                window.location =  Alfresco.constants.URL_CONTEXT +
                                   "page/site/" + this.options.siteId + "/discussions-topicview?topicId=" + response.json.topicId;
            }
            else if (this.options.mode == "edit")
            {
                // hide the form
                this.hideDiv(this.id + "-formDiv");
    	      
                // update the viewDiv
                this.updateAndShowDiv(this.id + "-viewDiv", response.json.topic);
                
                // set the mode back to view
                this.options.mode = "view";
                
                // reinit the mouse over listener
                this.initMouseOverListeners();
            }
         }
      },
      
      onFormSubmitFailure: function DiscussionsTopic_onFormSubmitFailure(response)
      {
         Alfresco.util.PopupManager.displayMessage({text: this._msg("topic.msg.failedSubmit")});
      },
      
      onFormOkButtonClick: function(type, args)
      {
        //Put the HTML back into the text area
		this.editor.saveHTML();W
      },
      
      onFormCancelButtonClick: function(type, args)
      {
         if (this.options.mode == "edit")
         {
            // PENDING: do we need to do cleanup of the created YUI elements and the form?
            
            // hide the form and show the view div
            this.hideDiv(this.id + "-formDiv");
	      
            // show the view
            this.showDiv(this.id + "-viewDiv");
            
            this.options.mode = "view";
         }
         else
         {
            history.go(-1);
         }
      },
      
      // mouse hover functionality
      
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
         var mouseEnteredBubbleEventName = 'onTopicElementMouseEntered';
         var mouseExitedBubbleEventName = 'onTopicElementMouseExited';
         var divs = YAHOO.util.Dom.getElementsByClassName('topic', 'div');
         for (var x=0; x < divs.length; x++) {
             this._attachRolloverListener(divs[x], mouseEnteredBubbleEventName, mouseExitedBubbleEventName);
         }
         
         if (this.firstMouseOverInit) {
            this.firstMouseOverInit = false;
            // manage mouse hover/exit
            YAHOO.Bubbling.on(mouseEnteredBubbleEventName, this.onTopicElementMouseEntered, this);
            YAHOO.Bubbling.on(mouseExitedBubbleEventName, this.onTopicElementMouseExited, this);
         }
      },
      
      /** Called when the mouse enters into a list item. */
      onTopicElementMouseEntered: function DiscussionsTopicList_onListElementMouseEntered(layer, args)
      {
         var elem = args[1].target;
         YAHOO.util.Dom.addClass(elem, 'overNode');
         var editBloc = YAHOO.util.Dom.getElementsByClassName( 'nodeEdit' , null , elem, null );
         YAHOO.util.Dom.addClass(editBloc, 'showEditBloc');
      },
      
      /** Called whenever the mouse exits a list item. */
      onTopicElementMouseExited: function DiscussionsTopicList_onListElementMouseExited(layer, args)
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
      _msg: function DiscussionsTopic_msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, "Alfresco.DiscussionsTopic", Array.prototype.slice.call(arguments).slice(1));
      }
   };
})();
