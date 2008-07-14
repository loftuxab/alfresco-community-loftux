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
       
         containerId: "discussions",
       
         path: "",
       
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
       
         // default action handler
         Alfresco.util.registerDefaultActionHandler(this.id, 'topic-action-link', 'div', this);
         Alfresco.util.registerDefaultActionHandler(this.id, 'tag-link-span', 'span', this);
          
         // initialize the mouse over listener
         Alfresco.util.rollover.registerHandlerFunctions(this.id, this.onTopicElementMouseEntered, this.onTopicElementMouseExited);
       
         // as the list got already rendered on the server, already attach the listener to the rendered elements
         Alfresco.util.rollover.registerListenersByClassName(this.id, 'topic', 'div');          
      },


      // Action handlers

      /**
       * Called when the add reply button is clicked
       */
      onAddReply: function DiscussionsTopic_onAddNode(htmlId, ownerId, param)
      {
         YAHOO.Bubbling.fire('onAddReplyToPost', {parentPostRef : this.options.topicRef});
      },
     
      /**
       * Loads the edit topic form and displays it instead of the content
       * The div class should have the same name as the above function (onEditNode)
       */
      onEditNode: function DiscussionsTopic_onEditNode(htmlId, ownerId, param)
      {
         this._loadForm();
      },
     
      /**
       * Deletes a topic.
       * The div class which contain the Delete link should have the same name as the above function (onEditNode)
       */
      onDeleteNode: function DiscussionsTopic_onDeleteNode(htmlId, ownerId, param)
      {
         // make an ajax request to delete the topic
         // we can directly go to alfresco for this
         var url = Alfresco.util.discussions.getTopicRestUrl(this.options.siteId,
                  this.options.containerId, this.options.path, param);
         Alfresco.util.Ajax.request(
         {
            url: url,
            method: "DELETE",
            responseContentType : "application/json",
            successCallback:
            {
               fn: this._onDeleted,
               scope: this
            },
            failureMessage: this._msg("topic.msg.failedDelete")
         });
      },
      
      onTagSelection: function DiscussionsTopic_onTagSelection(htmlId, ownerId, param)
      {
         // redirect to list page, but request the given tag
         Alfresco.util.discussions.loadForumPostListPage(this.options.siteId, this.options.containerId, this.options.path, null, param);
      },
      
      // Action functionality

      _onDeleted: function DiscussionsTopic__onDeleted(response)
      {
         if (response.json.error == undefined)
         {
            // redirect to the list page
            Alfresco.util.discussions.loadForumPostListPage(this.options.siteId, this.options.containerId, this.options.path);
         }
         else
         {
            Alfresco.util.PopupManager.displayMessage({text: this._msg("topic.msg.unableDelete", response.json.error)});
         }
      },


      // Editing mode

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
            successCallback:
            {
               fn: this._onFormLoaded,
               scope: this,
               object : { mode : "edit" }
            },
            failureMessage: this._msg("topic.msg.failedloadeditform"),
         });
      },

      _onFormLoaded: function(response, object)
      {  
         // ignore the loaded statement if the mode is already edit
         if (this.options.mode != "view")
         {
            Alfresco.util.PopupManager.displayMessage({text: this._msg("topic.msg.unknownMode")});
            return;
         }
      
         // update the mode
         this.options.mode = "edit";
         
         // hide the view element
         Alfresco.util.dom.hideDiv(this.id + "-viewDiv");
         
         // insert the new html into the document and then load the javascript
         var html = response.serverResponse.responseText;
         var javascript = Alfresco.util.ajaxtools.extractScripts(html);
         html = Alfresco.util.ajaxtools.removeScripts(response.serverResponse.responseText);
         Alfresco.util.dom.updateAndShowDiv(this.id + "-formDiv", html);
         Alfresco.util.ajaxtools.loadJSCode(javascript);
         
         // register the form handling
         this._registerTopicForm();
      },

      /**
       * Registers the form with the html (that should be available in the page)
       * as well as the buttons that are part of the form.
       */
      _registerTopicForm: function DiscussionsTopic__registerTopicForm()
      {
         // register the tag listener
         this.tagLibraryListener = new Alfresco.TagLibraryListener(this.id+"-form", "tags");
        
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
            toolbar: Alfresco.util.editor.getTextOnlyToolbarConfig(this._msg)
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
         topicForm.doBeforeFormSubmit =
         {
            fn: function(form, obj)
            {
               //Put the HTML back into the text area
               this.editor.saveHTML();
               // update the tags set in the form
               this.tagLibraryListener.updateForm();
            },
            scope: this
         }
         topicForm.init();
      },
     
      onFormSubmitSuccess: function DiscussionsTopic_onFormSubmitSuccess(response)
      {
         // check whether we got an error
         if (response.json.error != undefined)
         {
            Alfresco.util.PopupManager.displayMessage({text: this._msg("topic.msg.formsubmiterror", response.json.error)});
            return;
         }

         // check whether we are in create or edit mode. In case of create we will redirect
         // to the view page
         if (this.options.mode == "create")
         {
            Alfresco.util.discussions.loadForumPostViewPage(this.options.siteId, this.options.containerId, this.options.path, response.json.topicId);
         }
         else if (this.options.mode == "edit")
         {
            // hide the form
            Alfresco.util.dom.hideDiv(this.id + "-formDiv");
          
            // update the viewDiv
            Alfresco.util.dom.updateAndShowDiv(this.id + "-viewDiv", response.json.topic);
            
            // set the mode back to view
            this.options.mode = "view";
            
            // reinit the mouse over listener
            Alfresco.util.rollover.registerListenersByClassName(this.id, 'topic', 'div');
         }
      },
     
      onFormSubmitFailure: function DiscussionsTopic_onFormSubmitFailure(response)
      {
         Alfresco.util.PopupManager.displayMessage({text: this._msg("topic.msg.formsubmitfailed")});
      },
     
      onFormCancelButtonClick: function(type, args)
      {
         if (this.options.mode == "edit")
         {
            // hide the form and show the view div
            Alfresco.util.dom.hideDiv(this.id + "-formDiv");
       
            // show the view
            Alfresco.util.dom.showDiv(this.id + "-viewDiv");
         
            this.options.mode = "view";
         }
         else
         {
            history.go(-1);
         }
      },
     
      /**
       * Called when the mouse enters into the topic div
       */
      onTopicElementMouseEntered: function DiscussionsTopicList_onListElementMouseEntered(layer, args)
      {
         var elem = args[1].target;
         YAHOO.util.Dom.addClass(elem, 'overNode');
         var editBloc = YAHOO.util.Dom.getElementsByClassName( 'nodeEdit' , null , elem, null );
         YAHOO.util.Dom.addClass(editBloc, 'showEditBloc');
      },
     
      /**
       * Called whenever the mouse exits the topic div
       */
      onTopicElementMouseExited: function DiscussionsTopicList_onListElementMouseExited(layer, args)
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
      _msg: function DiscussionsTopic_msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, "Alfresco.DiscussionsTopic", Array.prototype.slice.call(arguments).slice(1));
      }
   };
})();
