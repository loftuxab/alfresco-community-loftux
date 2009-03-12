/*
 *** Alfresco.WikiCreateForm
*/
(function()
{
   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML;
    
   Alfresco.WikiCreateForm = function(containerId)
   {
      this.name = "Alfresco.WikiCreateForm";
      this.id = containerId;

      // Initialise prototype properties
      this.widgets = {};

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "container", "connection", "editor"], this.onComponentsLoaded, this);

      return this;
   };
   
   Alfresco.WikiCreateForm.prototype =
   {
      /**
       * Object container for storing YUI widget instances.
       * 
       * @property widgets
       * @type object
       */
      widgets: null,
      /**
       * Object container for initialization options
       *
       * @property options
       * @type object
       */
      options:
      {
         siteId: "",
         pageTitle: "",
         mode: "view", // default is "view" mode
         tags: [],
         pages: [],
         versions: []
      },
      /**
        * Sets the current site for this component.
        * 
        * @property siteId
        * @type string
        */
      setSiteId: function WikiCreateForm_setSiteId(siteId)
      {
         this.siteId = siteId;
         return this;
      },
      
      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       */
      setOptions: function WikiCreateForm_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      }, 
       /**
        * Set messages for this component.
        *
        * @method setMessages
        * @param obj {object} Object literal specifying a set of messages
        * @return {Alfresco.WikiCreateForm} returns 'this' for method chaining
        */
       setMessages: function WikiCreateForm_setMessages(obj)
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
       onComponentsLoaded: function WikiCreateForm_onComponentsLoaded()
       {
          YAHOO.util.Event.onContentReady(this.id, this.init, this, true);
       },
      
      /**
       * Fired by YUI when parent element is available for scripting.
       * Initialises components, including YUI widgets.
       *
       * @method init
       */
      init: function WikiCreateForm_init()
      {
         this.tagLibrary = new Alfresco.module.TagLibrary(this.id);
         this.tagLibrary.setOptions(
         {
            siteId: this.siteId
         });
         this.tagLibrary.initialize();

         this.widgets.pageEditor = Alfresco.util.createImageEditor(this.id + '-pagecontent',
         // {
         //    height: "300px",
         //    width: "600px",
         //    dompath: false, // Turns on the bar at the bottom
         //    animate: false, // Animates the opening, closing and moving of Editor windows
         //    markup: "xhtml",
         //    siteId: this.siteId
         // }
         {
         //Tiny MCE
         height: 300,
         width: 600,
         theme:'advanced',
         theme_advanced_buttons1 : "bold,italic,underline,strikethrough,|,justifyleft,justifycenter,justifyright,justifyfull,|,formatselect,fontselect,fontsizeselect,forecolor,backcolor",
         theme_advanced_buttons2 :"bullist,numlist,|,outdent,indent,blockquote,|,undo,redo,|,link,unlink,anchor,alfresco-imagelibrary,image,cleanup,help,code,removeformat",
         theme_advanced_toolbar_location : "top",
         theme_advanced_toolbar_align : "left",
         theme_advanced_statusbar_location : "bottom",
         theme_advanced_path : false,
         theme_advanced_resizing : true,
         theme_advanced_buttons3 : null,
         siteId: this.siteId,
         language:this.options.locale         
         }
         );
         this.widgets.pageEditor.render();

         this.widgets.saveButton = new YAHOO.widget.Button(this.id + "-save-button",
         {
            type: "submit"
         });

         Alfresco.util.createYUIButton(this, "cancel-button", null,
         {
            type: "link"
         });
         
         // Create the form that does the validation/submit
         var form = new Alfresco.forms.Form(this.id + "-form");
         form.addValidation(this.id + "-pageTitle", Alfresco.forms.validation.mandatory, null, "blur");
         form.addValidation(this.id + "-pageTitle", Alfresco.forms.validation.nodeName, null, "keyup");
         form.setShowSubmitStateDynamically(true);
         form.setSubmitElements(this.widgets.saveButton);
         form.setAJAXSubmit(true,
         {
            successCallback:
            {
               fn: this.onPageCreated,
               scope: this
            },
            failureMessage: "Page create failed"
         });

         form.setSubmitAsJSON(true);
         form.setAjaxSubmitMethod(Alfresco.util.Ajax.PUT);
         form.doBeforeFormSubmit =
         {
            fn: function(form, obj)
            {
               // Disable save button to prevent double-submission
               this.widgets.saveButton.set("disabled", true);
               // Put the HTML back into the text area
               this.widgets.pageEditor.save();
               // Update the tags set in the form
               this.tagLibrary.updateForm(this.id + "-form", "tags");

               // Avoid submitting the input field used for entering tags
               var tagInputElem = YAHOO.util.Dom.get(this.id + "-tag-input-field");
               if (tagInputElem)
               {
                  tagInputElem.disabled = true;
               }
               
               var title = YAHOO.util.Dom.get(this.id + "-pageTitle").value;
               title = title.replace(/\s+/g, "_");
               // Set the "action" attribute of the form based on the page title
               form.action =  Alfresco.constants.PROXY_URI + "slingshot/wiki/page/" + this.siteId + "/" + title;
               
               // Display pop-up to indicate that the page is being saved
               var savingMessage = Alfresco.util.PopupManager.displayMessage(
               {
                  displayTime: 0,
                  text: '<span class="wait">' + $html(Alfresco.util.message("message.saving", this.name)) + '</span>',
                  noEscape: true
               });
            },
            scope: this
         }

         form.init();         
         
      },

      /**
       * Event handler that gets called when the page is successfully created.
       * Redirects the user to the newly created page.
       *
       * @method onPageCreated
       * @param e {object} DomEvent
       */      
      onPageCreated: function WikiCreateForm_onPageCreated(e)
      {
         var name = "Main_Page"; // safe default
         
         var obj = YAHOO.lang.JSON.parse(e.serverResponse.responseText);
         if (obj)
         {
            name = obj.name;
         }
      
         // Redirect to the page that has just been created
         window.location =  Alfresco.constants.URL_CONTEXT + "page/site/" + this.siteId + "/wiki-page?title=" + name;
      }
   };
      
})();      
