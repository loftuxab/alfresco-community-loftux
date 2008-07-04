/*
 *** Alfresco.Wiki
*/
(function()
{
	Alfresco.WikiToolbar = function(containerId)
   {
	   this.name = "Alfresco.WikiToolbar";
      this.id = containerId;

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "container", "connection"], this.componentsLoaded, this);

      return this;
   };

	Alfresco.WikiToolbar.prototype =
	{
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
   	  * The title of the current page.
   	  * 
   	  * @property siteId
   	  * @type string
   	  */
   	setTitle: function(title)
   	{
   	   this.title = title;
   	   return this;
   	},
   	
   	/**
       * Set messages for this component.
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {Alfresco.DocListTree} returns 'this' for method chaining
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
   	   this.panel = new YAHOO.widget.Panel(this.id + "-createpanel", { width:"320px", visible:false, constraintoviewport:true } );
	      this.panel.render();
	      
	      var saveButton = Alfresco.util.createYUIButton(this, "save-button", this.onSaveClick,
	      {
	      	type: "push"
	      });
	      
	      // Create button
	      var createButton = Alfresco.util.createYUIButton(this, "create-button", this.onCreateClick,
	      {
	      	type: "push"
	      });
	      
	      // Delete button
	      var deleteButton = Alfresco.util.createYUIButton(this, "delete-button", this.onDeleteClick,
	      {
	      	type: "push"
	      });
	      
	      // Labels
	      var yes = Alfresco.util.message("button.yes", this.name);
	      var no = Alfresco.util.message("button.no", this.name);
	      var confirmText = Alfresco.util.message("panel.confirm.delete-msg", this.name);
	      
	      this.deleteDialog = new YAHOO.widget.SimpleDialog("deleteDialog", 
	      {
	         width: "300px",
            fixedcenter: true,
            visible: false,
            draggable: false,
            close: true,
            text: confirmText,
            icon: YAHOO.widget.SimpleDialog.ICON_HELP,
            constraintoviewport: true,
            buttons: [ { text: yes, handler: { fn: this.onConfirm, scope: this }, isDefault: true },
            			{ text: no,  handler: { fn: this.onCancel, scope: this } }]
	      });
	      var headerText = Alfresco.util.message("panel.confirm.header", this.name);
	      this.deleteDialog.setHeader(headerText);
	      this.deleteDialog.render(this.id + "-body");
	   },
	   
	   /**
		 * Fired when the user confirms that they want to delete a page. 
		 * Kicks off a DELETE request to the Alfresco repo to remove an event.
		 *
		 * @method onConfirm
		 * @param e {object} DomEvent
		 */
	   onConfirm: function(e)
	   {
	      Alfresco.util.Ajax.request(
   		{
   		   method: Alfresco.util.Ajax.DELETE,
   		   url: Alfresco.constants.PROXY_URI + "slingshot/wiki/page/" + this.siteId + "/" + this.title,
   		   successCallback:
   		   {
   			   fn: this.onPageDeleted,
   				scope: this
   			},
   		   failureMessage: Alfresco.util.message("load.fail", this.name)
   		});
	   },
	   
      /**
   	 * Fired when the user decides not to delete a page.
   	 * Hides the confirmation dialog.
   	 *
   	 * @method onCancel
   	 * @param e {object} DomEvent
   	 */
	   onCancel: function(e)
	   {
	      this.deleteDialog.hide();
	   },
	   
	   /**
       * Callback handler then gets invoked when a page is 
   	 * successfully deleted.
   	 * 
   	 * @method onPageDeleted
   	 * @param e {object} DomEvent
   	 */
	   onPageDeleted: function(e)
	   {
	      this.deleteDialog.hide();
	      // Redirect to the wiki landing page
         window.location =  Alfresco.constants.URL_CONTEXT + "page/site/" + this.siteId + "/wiki";
	   },
	   
	   /**
   	 * Event handler for when the user hits the 'New Page' button.
   	 * Opens up the create page panel.
   	 *
   	 * @method onCreateClick
   	 * @param e {object} DomEvent
   	 */
	   onCreateClick: function(e)
	   {
	      this.panel.show();
	   },
	   
	   /**
   	 * Event handler for save button on the page creation panel.
   	 * Modifies the URL to redirect the user to the page with the title they
   	 * specified in the text field.
   	 *
   	 * @method onSaveClick
   	 * @param e {object} DomEvent
   	 */
	   onSaveClick: function(e)
	   {
	      var elem = YAHOO.util.Dom.get(this.id + "-title");
	      if (elem)
	      {
	         var title = elem.value;
	         if (title)
	         {
	            title = title.replace(/\s+/g, "_");
	            // Change the location bar
   	         window.location = Alfresco.constants.URL_CONTEXT + "site/" + this.siteId + "/page/wiki?title=" + title;
	         } 
	      } 
	   },
	   
      /**
       * Event handler for the delete button in the toolbar.
       * Pops up the delete confirmation dialog.
   	 *
   	 * @method onDeleteClick
   	 * @param e {object} DomEvent
   	 */
	   onDeleteClick: function(e)
	   {
	      this.deleteDialog.show();
	   }
   };

})();   