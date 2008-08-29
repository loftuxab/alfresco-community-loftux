/**
 * CreateTopic component.
 * Logic for a topic creation form.
 * 
 * @namespace Alfresco
 * @class Alfresco.CreateTopic
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
   * CreateTopic constructor.
   * 
   * @param {String} htmlId The HTML id of the parent element
   * @return {Alfresco.CreateTopic} The new Topic instance
   * @constructor
   */
   Alfresco.CreateTopic = function(htmlId)
   {
      /* Mandatory properties */
      this.name = "Alfresco.CreateTopic";
      this.id = htmlId;
      
      /* Initialise prototype properties */
      this.widgets = {};
      this.modules = {};
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["datasource", "json", "connection", "event", "button", "menu", "editor"], this.onComponentsLoaded, this);
           
      return this;
   }
   
   Alfresco.CreateTopic.prototype =
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
          * Current containerId.
          * 
          * @property containerId
          * @type string
          */       
         containerId: "discussions"
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
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       */
      setOptions: function CreateTopic_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },
     
      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       */
      setMessages: function CreateTopic_setMessages(obj)
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
      onComponentsLoaded: function CreateTopic_onComponentsLoaded()
      {
         YAHOO.util.Event.onContentReady(this.id, this.onReady, this, true);
      },
  
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function CreateTopic_onReady()
      {
         // initialize the form
         this._initFormValues();
      },
      
      /**
       * Initializes the create topic form dom.
       * The html is already in the dom when the component gets loaded
       */
      _initFormValues: function CreateTopic_initFormValues()
      {          
         // insert current values into the form
         var actionUrl = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "api/forum/site/{site}/{container}/posts",
         {
            site: this.options.siteId,
            container : this.options.containerId
         });
         Dom.get(this.id + "-form").setAttribute("action", actionUrl);
         Dom.get(this.id + "-site").setAttribute("value", this.options.siteId);
         Dom.get(this.id + "-container").setAttribute("value", this.options.containerId);
         // construct the browseUrl. {post.name} gets replaced on the server
         var browseUrl = YAHOO.lang.substitute(Alfresco.constants.URL_PAGECONTEXT + "site/{site}/discussions-topicview?container={container}&topicId={post.name}",
         {
            site: this.options.siteId,
            container: this.options.containerId
         });
         Dom.get(this.id + "-browseTopicUrl").setAttribute("value", browseUrl);
             
         // and finally register the form handling
         this._registerCreateTopicForm();
      },

      /**
       * Registers the form logic.
       */
      _registerCreateTopicForm: function CreateTopic__registerCreateTopicForm()
      {
         // initialize the tag library
         this.modules.tagLibrary = new Alfresco.module.TagLibrary(this.id);
         this.modules.tagLibrary.setOptions({ siteId: this.options.siteId });
         this.modules.tagLibrary.initialize();
         
         // register the okButton
         this.widgets.okButton = new YAHOO.widget.Button(this.id + "-submit", {type: "submit"});
         
         // register the cancel button
         this.widgets.cancelButton = new YAHOO.widget.Button(this.id + "-cancel", {type: "button"});
         this.widgets.cancelButton.subscribe("click", this.onFormCancelButtonClick, this, true);
         
         // instantiate the simple editor we use for the form
         this.widgets.editor = new YAHOO.widget.SimpleEditor(this.id + '-content', {
             height: '180px',
             width: '700px',
             dompath: false, //Turns on the bar at the bottom
             animate: false, //Animates the opening, closing and moving of Editor windows
             toolbar:  Alfresco.util.editor.getTextOnlyToolbarConfig(this._msg)
         });
         this.widgets.editor._render();
         
         // create the form that does the validation/submit
         var topicForm = new Alfresco.forms.Form(this.id + "-form");
         topicForm.setShowSubmitStateDynamically(true, false);
         topicForm.setSubmitElements(this.widgets.okButton);
         topicForm.setAJAXSubmit(true,
         {
            successMessage: this._msg("message.savetopic.success"),
            successCallback:
            {
               fn: this.onFormSubmitSuccess,
               scope: this
            },
            failureMessage: this._msg("message.savetopic.failure"),
            failureCallback:
            {
               fn: this.onFormSubmitFailure,
               scope: this
            },
         });
         topicForm.setSubmitAsJSON(true);
         topicForm.doBeforeFormSubmit =
         {
            fn: function(form, obj)
            {
               // disable buttons
               this.widgets.okButton.set("disabled", false);
               this.widgets.cancelButton.set("disabled", false);
                
               //Put the HTML back into the text area
               this.widgets.editor.saveHTML();
               
               // update the tags set in the form
               this.modules.tagLibrary.updateForm(this.id + '-form', 'tags');
               
               // show a wait message
               this.widgets.feedbackMessage = Alfresco.util.PopupManager.displayMessage(
               {
                  text: Alfresco.util.message(this._msg("message.submitting")),
                  spanClass: "wait",
                  displayTime: 0
               });
            },
            scope: this
         }
         
         topicForm.init();
         
         // show the form
         var editDiv = Dom.get(this.id + "-topic-create-div");
         Dom.removeClass(editDiv, "hidden");
      },
      
      /**
       * Form submit success handler
       */
      onFormSubmitSuccess: function CreateTopic_onFormSubmitSuccess(response, object)
      {
         // the response contains the data of the created topic. redirect to the topic view page
         var url = Alfresco.util.discussions.getTopicViewPage(this.options.siteId, this.options.containerId, response.json.item.name);
         window.location = url;
      },
      
      /**
       * Reenables the inputs which got disabled as part of a comment submit
       */
      onFormSubmitFailure: function CreateComment_onFormSubmitFailure()
      {
         // enable buttons
         this.widgets.okButton.set("disabled", false);
         this.widgets.cancelButton.set("disabled", false);
         
         // hide message
         this.widgets.feedbackMessage.destroy();
      },
      
      /**
       * Form cancel button click handler
       */
      onFormCancelButtonClick: function CreateTopic_onFormCancelButtonClick(type, args)
      {
         // return to the page we came from
         history.go(-1);
      },

      /**
       * Gets a custom message
       *
       * @method _msg
       * @param messageId {string} The messageId to retrieve
       * @return {string} The custom message
       * @private
       */
      _msg: function CreateTopic_msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, "Alfresco.CreateTopic", Array.prototype.slice.call(arguments).slice(1));
      }
   };
})();
