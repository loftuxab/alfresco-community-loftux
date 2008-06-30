/**
 * CreateComment component.
 * 
 * @namespace Alfresco
 * @class Alfresco.CreateComment
 */
(function()
{
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
      options:
      {
      },
      
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
         YAHOO.util.Event.onContentReady(this.id, this.onReady, this, true);
      },
   
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function CreateComment_onReady()
      { 
         this.registerCreateCommentForm(); 
      },
      
      /**
       * Registers the form with the html (that should be available in the page)
       * as well as the buttons that are part of the form.
       */
      registerCreateCommentForm: function CreateComment_registerCreateCommentForm()
      {
         // register the okButton
         var okButton = new YAHOO.widget.Button(this.id + "-createcomment-ok-button", {type: "submit"});
         
         // instantiate the simple editor we use for the form
         this.editor = this.createSimpleEditor(this.id + '-createcomment-content');
		 this.editor.render();
         
         // create the form that does the validation/submit
         var commentForm = new Alfresco.forms.Form(this.id + "-createcomment-form");
         commentForm.setShowSubmitStateDynamically(true);
         commentForm.setSubmitElements(okButton);
         commentForm.setAJAXSubmit(true,
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
      
      /**
       * Creates a SimpleEditor for the specified id.
       * 
       * Note: You still need to call return_val.render() to actually render the editor
       * 
       * PENDING: this should be generalized!
       * 
       * @return the editor object.
       */
      createSimpleEditor: function CreateComment_createSimpleEditor(textareaId)
      {
         // instantiate the simple editor we use for the form
		 var editor = new YAHOO.widget.SimpleEditor(textareaId, {
		     height: '250px',
		     width: '538px',
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
       * Called when the form has been successfully submitted.
       */
      onCreateFormSubmitSuccess: function CreateComment_onCreateFormSubmitSuccess(response, object)
      {
          Alfresco.util.PopupManager.displayMessage({text: "Form submit successful"});
          location.reload(true);
      },
      
      /** Called when the form submit failed. */
      onCreateFormSubmitFailure: function CreateComment_onCreateFormSubmitFailure(response)
      {
         Alfresco.util.PopupManager.displayMessage({text: "Creating comment failed"});
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
