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
		/**
		 * An instance of a Wiki parser for this page.
		 * 
		 * @property parser
		 * @type Alfresco.WikiParser
		 */
		parser : null,
		
		/**
	     * Sets the current site for this component.
	     * 
	     * @property siteId
	     * @type string
	     */
		setSiteId: function(siteId)
		{
			this.siteId = siteId;
			return this;
		},
		
		/**
		 * Sets the page title of the page.
		 *
		 * @property pageTitle
		 * @type string
		 */
		setPageTitle: function(pageTitle)
		{
			this.pageTitle = pageTitle;
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
			this.tabs = new YAHOO.widget.TabView(this.id + "-wikipage");
			
			this.pageEditor = new YAHOO.widget.SimpleEditor(this.id + '-pagecontent', {
   		     height: '300px',
   		     width: '538px',
   		     dompath: false, //Turns on the bar at the bottom
   		     animate: false //Animates the opening, closing and moving of Editor windows
   		 });

   		this.pageEditor.render();
			
			var saveButton = Alfresco.util.createYUIButton(this, "save-button", this.onSaveSelect,
	        {
	        	type: "push"
	        });
	
			var cancelButton = Alfresco.util.createYUIButton(this, "cancel-button", this.onCancelSelect,
			{
				type: "push"
			});
			
			var pageText = document.getElementById("#page"); // Content area
			if (pageText)
			{
				this.parser.URL = this._getAbsolutePath();
				// Format any wiki markup
				pageText.innerHTML = this.parser.parse(pageText.innerHTML);
			}
			
			var Dom = YAHOO.util.Dom;
			Dom.get(this.id + "-wikipage").style.visibility = "visible";
		},
		
		/*
		 * This method gets fired when the user saves
		 * a page they are editing. Fires off of a request to 
		 * the repo to update the page content.
		 *
		 * @method onSaveSelect
		 * @param e {object} Event fired
		 */
		onSaveSelect: function(e)
		{
			var data = {};
		
			this.pageEditor.saveHTML();
			
			var html = this.pageEditor.get('element').value; 
			if (html)
			{
				data["pagecontent"] = html;
			}
			
			var URI = "/slingshot/wiki/page/" + this.siteId + "/" + this.pageTitle;
			// Add the page context
			URI += "?context=" + encodeURIComponent(this._getAbsolutePath() + this.pageTitle);
			
			// Submit PUT request 
			Alfresco.util.Ajax.request(
			{
				method: Alfresco.util.Ajax.PUT,
		      	url: Alfresco.constants.PROXY_URI + URI,
				requestContentType: Alfresco.util.Ajax.JSON,
				dataObj: data,
				successCallback:
				{
					fn: this.onPageUpdated,
					scope: this
				},
		      	failureMessage: "Page update failed"
		   });
		},
		
		/**
		 * Returns the absolute path (URL) to a wiki page, minus the title of the page.
		 *
		 * @method _getAbsolutePath
		 */
		_getAbsolutePath: function()
		{
			return Alfresco.constants.URL_CONTEXT + "page/site/" + this.siteId + "/wiki-page?title=";	
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
			this.tabs.set('activeIndex', 0);
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
			// Switch back to page view - update with the new content.
			var response = YAHOO.lang.JSON.parse(e.serverResponse.responseText);
			if (response && !YAHOO.lang.isUndefined(response.pagetext))
			{
				var tab0 = this.tabs.getTab(0);
				// TODO: fix me
				this.parser.URL = this._getAbsolutePath();
				tab0.set('content', this.parser.parse(response.pagetext));
				
				this.pageEditor.get('element').value = response.pageText;
			}
			
			this.tabs.set('activeIndex', 0);
		}
			
	};	

})();

