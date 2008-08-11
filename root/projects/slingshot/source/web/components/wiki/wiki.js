/*
 *** Alfresco.Wiki
*/
(function()
{
	Alfresco.Wiki = function(containerId)
   {
	   this.name = "Alfresco.Wiki";
      this.id = containerId;

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "container", "connection", "editor", "tabview"], this.componentsLoaded, this);

		this.parser = new Alfresco.WikiParser();

      return this;
   };

	Alfresco.Wiki.prototype =
	{
	   
	   selectedTags: [],
		/**
		 * An instance of a Wiki parser for this page.
		 * 
		 * @property parser
		 * @type Alfresco.WikiParser
		 */
		parser : null,
		
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
         mode: "view" // default is "view" mode
      },		

      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       */
      setOptions: function Wiki_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },
		
		/**
		 * Fired by YUILoaderHelper when required component script files have
		 * been loaded into the browser.
		 *
		 * @method onComponentsLoaded
		 */
		componentsLoaded: function()
		{
			YAHOO.util.Event.onContentReady(this.id, this.init, this, true);
		},
		
		/**
		 * Fired by YUI when parent element is available for scripting.
		 * Initialises components, including YUI widgets.
		 *
		 * @method init
		 */
		init: function()
		{
			var me = this;
			
			YAHOO.Bubbling.addDefaultAction('view-link', function(layer, args)
         {
            var link = args[1].target;
            if (link)
            {
               var id = link.id;
               me._displayVersion(id);      
            }
            return true;
         });
       
			if (this.options.mode === "edit") 
			{
			  this._setupEditForm();
			}
			
			var pageText = document.getElementById("#page"); // Content area
			if (pageText)
			{
				this.parser.URL = this._getAbsolutePath();
				// Format any wiki markup
				pageText.innerHTML = this.parser.parse(pageText.innerHTML);
			}
		
		},
		
		_setupEditForm: function()
		{
		   // register the tag listener
         this.tagLibraryListener = new Alfresco.TagLibraryListener(this.id + "-form", "tags");
         
         this.pageEditor = new YAHOO.widget.SimpleEditor(this.id + '-pagecontent', {
      	   height: '300px',
      		width: '538px',
      		dompath: false, //Turns on the bar at the bottom
      		animate: false, //Animates the opening, closing and moving of Editor windows
      	   markup: "xhtml"
      	});

      	this.pageEditor.render();

         var saveButtonId = this.id + "-save-button";
         var saveButton = new YAHOO.widget.Button(saveButtonId, {type: "submit"});

   		var cancelButton = Alfresco.util.createYUIButton(this, "cancel-button", this.onCancelSelect,
   		{
   			type: "push"
   		});		   
   		
         // create the form that does the validation/submit
         var form = new Alfresco.forms.Form(this.id + "-form");
         form.setShowSubmitStateDynamically(true, false);
         form.setSubmitElements(saveButton);
         form.setAJAXSubmit(true,
         {
            successCallback:
            {
               fn: this.onPageUpdated,
               scope: this
            },
            failureMessage: "Page update failed"
         });
       
         form.setSubmitAsJSON(true);
         form.setAjaxSubmitMethod(Alfresco.util.Ajax.PUT);
         form.doBeforeFormSubmit =
         {
            fn: function(form, obj)
            {
               // Put the HTML back into the text area
               this.pageEditor.saveHTML();
               // Update the tags set in the form
               this.tagLibraryListener.updateForm();
               
               // Avoid submitting the input field used for entering tags
               var tagInputElem = YAHOO.util.Dom.get(this.id + "-tag-input-field");
               if (tagInputElem)
               {
                  tagInputElem.disabled = true;
               }
            },
            scope: this
         }
         
         form.init();   		
   		
   		YAHOO.Bubbling.on("onTagLibraryTagsChanged", this.onTagLibraryTagsChanged, this);
		},
		
		onTagLibraryTagsChanged: function(layer, args)
		{
		   this.selectedTags = args[1].tags;
		},
		
		_displayVersion: function(id)
		{
         var actionUrl = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "slingshot/wiki/version/{site}/{title}/{version}",
         {
            site: this.options.siteId,
            title: this.options.pageTitle,
            version: id
         });
		   
		   Alfresco.util.Ajax.request(
			{
				method: Alfresco.util.Ajax.GET,
		      url: actionUrl,
				successCallback:
				{
					fn: this.onVersionInfo,
					scope: this
				},
		      failureMessage: "Could not retrieve version information"
		   });
		   
		},
		
		onVersionInfo: function(e)
		{
		   var panel = new YAHOO.widget.Panel("versionPanel", 
		   { 
		      width:"320px", 
		      visible:false, 
		      draggable:true, 
		      close:true, 
		      fixedcenter:true
		   });
		   
		   panel.setHeader("Version Info");
		   panel.setBody(this.parser.parse(e.serverResponse.responseText));
		   panel.render(document.body);
		   panel.show();
		},
		
		/**
		 * Returns the absolute path (URL) to a wiki page, minus the title of the page.
		 *
		 * @method _getAbsolutePath
		 */
		_getAbsolutePath: function()
		{
			return Alfresco.constants.URL_CONTEXT + "page/site/" + this.options.siteId + "/wiki-page?title=";	
		},
		
		/*
	   	 * Gets called when the user cancels an edit in progress.
		 * Returns the user to the page view of a page.
		 *
		 * @method onCancelSelect
		 * @param e {object} Event fired
		 */
		onCancelSelect: function(e)
		{
			//this.tabs.set('activeIndex', 0);
		},
		
		/*
		 * Event handler that gets fired when a page is successfully updated.
		 * This follows the "onSaveSelect" event handler.
		 * 
		 * @method onPageUpdated
		 * @param e {object} Event fired
		 */
		onPageUpdated: function(e)
		{
		   // Display pop-up
		    Alfresco.util.PopupManager.displayMessage({text: "Page Updated"});
		}
			
	};	

})();

