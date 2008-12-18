/*
 *** Alfresco.Wiki
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
    
	Alfresco.Wiki = function(containerId)
   {
	   this.name = "Alfresco.Wiki";
      this.id = containerId;
      this.widgets = {};

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "container", "connection", "editor", "tabview"], this.componentsLoaded, this);

		this.parser = new Alfresco.WikiParser();

      return this;
   };

	Alfresco.Wiki.prototype =
	{

      /**
       * Object container for storing YUI widget instances.
       *
       * @property widgets
       * @type object
       */
      widgets: {},
      
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
         mode: "view", // default is "view" mode
         tags: [],
         pages: [],
         versions: []
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
       * Set messages for this component.
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {Alfresco.WikiCreateForm} returns 'this' for method chaining
       */
      setMessages: function(obj)
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
			
			var pageText = document.getElementById(this.id + "-page"); // Content area
			if (pageText)
			{
				this.parser.URL = this._getAbsolutePath();
				// Format any wiki markup
				pageText.innerHTML = this.parser.parse(pageText.innerHTML, this.options.pages);
			}

		},
		
		_setupPageDetails: function()
		{
		   // Add 'onchange' handler to dropdown
		   //YAHOO.util.Event.addListener(this.id + "-selectVersion", 'change', this.onSelectChange, null, this);

         var versions = this.options.versions;

         // Versioning drop down
         if(versions.length > 0)
         {
            this.widgets.versionSelect = Alfresco.util.createYUIButton(this, "selectVersion-button", this.onVersionSelectChange,
            {
               type: "menu",
               menu: "selectVersion-menu"
            });
         }

         // Listen on clicks for revert version icons
         var myThis = this;
         for (var i = 0; i < versions.length; i++)
         {
            var revertSpan = YAHOO.util.Dom.get(this.id + "-revert-span-" + i);
            if(revertSpan)
            {
               YAHOO.util.Event.addListener(revertSpan, "click",
                       function (event, obj)
                       {
                          // Find the index of the version link by looking at its id
                          var version = versions[obj.versionIndex];

                          // Find the version through the index and display the revert dialog for the version
                          Alfresco.module.getRevertWikiVersionInstance().show({
                             siteId: obj.siteId,
                             pageTitle: obj.pageTitle,
                             version: version.label,
                             versionId: version.versionId,
                             onRevertWikiVersionComplete: {
                                fn: this.onRevertWikiVersionComplete,
                                scope: this
                             }
                          });
                       }, {siteId: this.options.siteId, pageTitle: this.options.pageTitle, versionIndex: i}, this);
            }

            // Listen on clicks on the version - date row so we can expand and collapse it
            var expandDiv = YAHOO.util.Dom.get(this.id + "-expand-div-" + i);
            var moreVersionInfoDiv = YAHOO.util.Dom.get(this.id + "-moreVersionInfo-div-" + i);
            if(expandDiv)
            {
               YAHOO.util.Event.addListener(expandDiv, "click",
                       function (event, obj)
                       {
                          //alert(obj.versionIndex);
                          var Dom = YAHOO.util.Dom;
                          //var moreVersionInfoDiv = Dom.get(this.id + "-moreVersionInfo-div-" + obj.versionIndex);
                          if(obj.moreVersionInfoDiv && Dom.hasClass(obj.expandDiv, "collapsed"))
                          {
                             Alfresco.util.Anim.fadeIn(obj.moreVersionInfoDiv);
                             Dom.removeClass(obj.expandDiv, "collapsed");
                             Dom.addClass(obj.expandDiv, "expanded");
                          }
                          else
                          {
                             Dom.setStyle(obj.moreVersionInfoDiv, "display", "none");
                             Dom.removeClass(obj.expandDiv, "expanded");
                             Dom.addClass(obj.expandDiv, "collapsed");
                          }
                       },
               {
                  expandDiv: expandDiv,
                  moreVersionInfoDiv: moreVersionInfoDiv
               }, this);
            }

            // Format and display the createdDate
            var createdDateSpan = document.getElementById(this.id + "-createdDate-span-" + i);
            createdDateSpan.innerHTML = Alfresco.util.formatDate(versions[i].createdDate);
         }
		         
		},

      /**
       * Fired by the Revert Version component after a successfull revert.
       *
       * @method onRevertWikiVersionComplete
       */
      onRevertWikiVersionComplete: function DV_onRevertWikiVersionComplete()
      {
         Alfresco.util.PopupManager.displayMessage({ text: Alfresco.util.message("message.revertComplete", this.name) });

         window.location.reload();
      },

      /* REMOVE */
		onRevert: function(e)
		{
		   // Make a PUT request 
		   var actionUrl = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "slingshot/wiki/page/{site}/{title}",
         {
            site: this.options.siteId,
            title: this.options.pageTitle
         });
		   
		   var div = Dom.get(this.id + "-pagecontent");
   		var obj =
   		{
   		   pagecontent: div.innerHTML,
   		   page: "wiki-page"
   	   };
   		   
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
		   var width = Dom.get(this.id + "-form").offsetWidth - 316; //860
		   var height = YAHOO.env.ua.ie > 0 ? document.body.clientHeight : document.height;
		   this.tagLibrary = new Alfresco.module.TagLibrary(this.id);
		   this.tagLibrary.setOptions(
		   {
		      siteId: this.options.siteId
		   });
         this.tagLibrary.initialize();
         if (this.options.tags.length > 0)
         {
            this.tagLibrary.setTags(this.options.tags);
         }
                
         this.pageEditor = Alfresco.util.createImageEditor(this.id + '-pagecontent',
         {
            height: Math.min(height - 450, 300) + 'px',
            width: width + 'px',
            dompath: false, // Turns on the bar at the bottom
            animate: false, // Animates the opening, closing and moving of Editor windows
            markup: "xhtml",
            siteId: this.options.siteId
         });
         
         this.pageEditor.render();

         var saveButtonId = this.id + "-save-button";
         var saveButton = new YAHOO.widget.Button(saveButtonId,
         {
            type: "submit"
         });

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
               // Display pop-up to indicate that the page is being saved
               var savingMessage = Alfresco.util.PopupManager.displayMessage(
               {
                  displayTime: 0,
                  text: '<span class="wait">' + $html(Alfresco.util.message("message.saving", this.name)) + '</span>',
                  noEscape: true
               });
                  
               // Put the HTML back into the text area
               this.pageEditor.saveHTML();
               // Update the tags set in the form
               this.tagLibrary.updateForm(this.id + "-form", "tags");
               
               // Avoid submitting the input field used for entering tags
               var tagInputElem = Dom.get(this.id + "-tag-input-field");
               if (tagInputElem)
               {
                  tagInputElem.disabled = true;
               }
            },
            scope: this
         };
         
         form.init();   		
   		
         YAHOO.Bubbling.on("onTagLibraryTagsChanged", this.onTagLibraryTagsChanged, this);
      },
		
      onTagLibraryTagsChanged: function(layer, args)
      {
         this.selectedTags = args[1].tags;
      },

       /**
        * Called when the user selects a version in the version list
        *
        * @method onVersionSelectChange
        * @param sType {string} Event type, e.g. "click"
        * @param aArgs {array} Arguments array, [0] = DomEvent, [1] = EventTarget
        * @param p_obj {object} Object passed back from subscribe method
        */
      onVersionSelectChange: function Wiki_onVersionSelectChange(sType, aArgs, p_obj)
		{
         var versionId = aArgs[1].value;
         var actionUrl = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "slingshot/wiki/version/{site}/{title}/{version}",
         {
            site: this.options.siteId,
            title: this.options.pageTitle,
            version: versionId
         });
		   
		   Alfresco.util.Ajax.request(
			{
				method: Alfresco.util.Ajax.GET,
		      url: actionUrl,
				successCallback:
				{
					fn: this.onVersionInfo,
					scope: this,
               obj: {index: aArgs[1].index}
				},
		      failureMessage: "Could not retrieve version information"
		   });
		   
		},

       /**
        * Called when the content for a new version has been loaded
        * (because of a user click in the version select menu).
        *
        * @method onVersionInfo
        * @param event {object} event from Alfresco.ajax.request
        * @param obj {object} contians the index of the selected version in the versions array
        */
		onVersionInfo: function Wiki_onVersionInfo(event, obj)
		{        
         // Show the content
		   var page = Dom.get(this.id + "-page");
		   page.innerHTML = this.parser.parse(event.serverResponse.responseText, this.options.pages);

         // Update the version label in the header
         var versionHeaderSpan = Dom.get(this.id + "-version-header");
         if(versionHeaderSpan)
         {
            versionHeaderSpan.innerHTML = Alfresco.util.message("label.shortVersion", this.name) + this.options.versions[obj.index].label;
         }

         // Update the label in the version select menu
         var label = this.options.versions[obj.index].label + " (" + (obj.index == 0 ?  Alfresco.util.message("label.latest", this.name) : Alfresco.util.message("label.earlier", this.name)) + ")";
         this.widgets.versionSelect.set("label", label);
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
		   // "Redirect" to the "view" tab
		   var url = this._getAbsolutePath();
		   url += this.options.pageTitle;
		   window.location = url;   
		}
			
	};	

})();

