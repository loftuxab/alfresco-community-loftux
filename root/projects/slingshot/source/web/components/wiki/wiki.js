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
       * Object container for storing module instances.
       * 
       * @property modules
       * @type object
       */
      modules: {},

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
			if (this.options.mode === "edit") 
			{
			  this._setupEditForm();
			}
			else if (this.options.mode === "details")
			{
			   this._setupPageDetails();
			}
			
			var pageText = document.getElementById("#page"); // Content area
			if (pageText)
			{
				this.parser.URL = this._getAbsolutePath();
				// Format any wiki markup
				pageText.innerHTML = this.parser.parse(pageText.innerHTML);
			}
		
		},
		
		_setupPageDetails: function()
		{
		   // Add 'onchange' handler to dropdown
		   YAHOO.util.Event.addListener(this.id + "-selectVersion", 'change', this.onSelectChange, null, this);
		   
		   var revertButton = Alfresco.util.createYUIButton(this, "revert-button", this.onRevert,
   		{
   			type: "push"
   		});
   		
   		// Hide the revert button
   		var div  = YAHOO.util.Dom.get(this.id + "-revertPanel");
   		if (div)
   		{
   		   div.style.display = "none";
   		}
		},
		
		onRevert: function(e)
		{
		   // Make a PUT request 
		   var actionUrl = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "slingshot/wiki/page/{site}/{title}",
         {
            site: this.options.siteId,
            title: this.options.pageTitle,
         });
		   
		   var div = YAHOO.util.Dom.get(this.id + "-pagecontent");
   		var obj = {
   		   "pagecontent": div.innerHTML
   	   }
   		   
		   Alfresco.util.Ajax.request(
			{
				method: Alfresco.util.Ajax.PUT,
		      url: actionUrl,
		      dataObj: obj,
		      requestContentType: Alfresco.util.Ajax.JSON,
				successCallback:
				{
					fn: this.onPageUpdated,
					scope: this
				},
		      failureMessage: "Could not update page"
		   });
		},
		
		_setupEditForm: function()
		{
		   // fetch the tag listener
         this.modules.tagLibrary = Alfresco.util.ComponentManager.findFirst("Alfresco.module.TagLibrary");
         this.modules.tagLibrary.initialize();
         
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
               this.modules.tagLibrary.updateForm(this.id + "-form", "tags");
               
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
		
		onSelectChange: function(e)
		{
		   var select = e.target;
		   var id = select.options[select.selectedIndex].value; 
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
		   
		   // Decide whether to display the revert button or not
		   var div  = YAHOO.util.Dom.get(this.id + "-revertPanel");
		   if (select.selectedIndex === 0)
		   {
		      div.style.display = "none";
		   }
		   else
		   {
		      div.style.display = "block";
		   }
		},
		
		onVersionInfo: function(e)
		{
		   var page = YAHOO.util.Dom.get("#page");
		   page.innerHTML = this.parser.parse(e.serverResponse.responseText);
		   
		   var pagecontent = YAHOO.util.Dom.get(this.id + "-pagecontent");
		   if (pagecontent)
		   {
		      // We store the raw content in this hidden div
		      pagecontent.innerHTML = e.serverResponse.responseText;
		   }
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
		   this._redirect();
		},
		
		/*
		 * Event handler that gets fired when a page is successfully updated.
		 * This follows the "onSaveSelect" and "onRevert" event handlers.
		 * 
		 * @method onPageUpdated
		 * @param e {object} Event fired
		 */
		onPageUpdated: function(e)
		{
		   this._redirect();
		},
		
		_redirect: function()
		{
		   // Redirect to the "view" tab
		   var url = this._getAbsolutePath();
		   url += this.options.pageTitle;
		   window.location = url;   
		}
			
	};	

})();

