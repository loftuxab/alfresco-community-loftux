/**
 * Copyright (C) 2005-2008 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of
 * the GPL, you may redistribute this Program in connection with Free/Libre
 * and Open Source Software ("FLOSS") applications as described in Alfresco's
 * FLOSS exception.  You should have recieved a copy of the text describing
 * the FLOSS exception, and it is also available here:
 * http://www.alfresco.com/legal/licensing
 */

/**
 * ConfigBlog module
 *
 * A dialog for creating sites
 *
 * @namespace Alfresco.module
 * @class Alfresco.ConfigBlog
 */
(function()
{

   /**
    * ConfigBlog constructor.
    *
    * @param htmlId {string} A unique id for this component
    * @return {Alfresco.ConfigBlog} The new DocumentList instance
    * @constructor
    */
   Alfresco.ConfigBlog = function(containerId)
   {
      this.name = "Alfresco.ConfigBlog";
      this.id = containerId;
      
      // Register this component
      Alfresco.util.ComponentManager.register(this);

      // Load YUI Components
      Alfresco.util.YUILoaderHelper.require(["button", "container", "connection", "selector", "json", "event"], this.onComponentsLoaded, this);

      return this;
   };

   Alfresco.ConfigBlog.prototype =
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
          * Container this configuration refers to
          */
         containerId: "blog"
      },
       
      /**
       * Panel instance.
       * 
       * @property panel
       * @type YUI.widget.Panel
       */
      panel: null,

      /**
       * Object container for storing YUI widget instances.
       * 
       * @property widgets
       * @type object
       */
      widgets: {},

      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       */
      setOptions: function ConfigBlog_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },
      
      setMessages: function ConfigBlog_setMessages(obj)
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
      onComponentsLoaded: function()
      {
         /* Shortcut for dummy instance */
         if (this.id === null)
         {
            return;
         }
         
         YAHOO.util.Event.onContentReady(this.id, this.onReady, this, true);
      },

      /**
       * Fired by YUI when parent element is available for scripting
       * @method onReady
       */
      onReady: function()
      {
         // listen on onConfigureBlog events
         YAHOO.Bubbling.on("onConfigureBlog", this.onConfigureBlog, this);
      },

      /**
       * Fired by YUI Link when the "Create site" label is clicked
       * @method onCreateSiteLinkClick
       * @param event {domEvent} DOM event
       */
      onConfigureBlog: function(layer, args)
      {
         this.show();
      },

      /**
       * Shows the CreteSite dialog to the user.
       *
       * @method show
       */
      show: function()
      {
         if(this.panel)
         {
            /**
             * The panel gui has been showed before and its gui has already
             * been loaded and created
             */
            this._showPanel();
         }
         else
         {
            /**
             * Load the gui from the server and let the templateLoaded() method
             * handle the rest.
             */
            var url = Alfresco.constants.URL_SERVICECONTEXT + "modules/blog/config/config-blog";
            Alfresco.util.Ajax.request(
            {
               url: url,
               dataObj:
               {
                  site: this.options.siteId,
                  htmlid: this.id
               },
               successCallback:
               {
                  fn: this.onTemplateLoaded,
                  scope: this
               },
               failureMessage: "Could not load config blog template"
            });
         }
      },

      /**
       * Called when the ConfigBlog html template has been returned from the server.
       * Creates the YUI gui objects such as buttons and a panel and shows it.
       *
       * @method onTemplateLoaded
       * @param response {object} a Alfresco.util.Ajax.request response object 
       */
      onTemplateLoaded: function(response)
      {
         // Inject the template from the XHR request into a new DIV element
         var containerDiv = document.createElement("div");
         containerDiv.innerHTML = response.serverResponse.responseText;

         // The panel is created from the HTML returned in the XHR request, not the container
         var panelDiv = YAHOO.util.Dom.getFirstChild(containerDiv);

         this.panel = new YAHOO.widget.Panel(panelDiv,
         {
            modal: true,
            draggable: false,
            fixedcenter: true,
            close: false,
            visible: false
         });

         // Add it to the Dom
         this.panel.render(document.body);

         // Create the cancel button
         this.widgets.cancelButton = Alfresco.util.createYUIButton(this, "cancel-button", this.onCancelButtonClick);

         // Create the ok button, the forms runtime will handle when its clicked
         this.widgets.okButton = Alfresco.util.createYUIButton(this, "ok-button", null,
         {
            type: "submit"
         });

         // Configure the forms runtime
         var configureBlogForm = new Alfresco.forms.Form(this.id + "-configBlog-form");

         // Shortname is mandatory
         //configBlogForm.addValidation(this.id + "-shortName", Alfresco.forms.validation.mandatory, null, "keyup");
         // and can NOT contain whitespace characters
         //configBlogForm.addValidation(this.id + "-shortName", Alfresco.forms.validation.regexMatch, {pattern: /^[^\s]*$/}, "keyup");
         // and should be valid file name
         //configBlogForm.addValidation(this.id + "-shortName", Alfresco.forms.validation.nodeName, null, "keyup");

         // The ok button is the submit button, and it should be enabled when the form is ready
         configureBlogForm.setShowSubmitStateDynamically(true, false);
         configureBlogForm.setSubmitElements(this.widgets.okButton);

         // Submit as an ajax submit
         configureBlogForm.setAJAXSubmit(true,
         {
            successCallback:
            {
               fn: this.onConfigureBlogSuccess,
               scope: this
            },
            failureMessage: "Unable to update blog configuration"
         });
         configureBlogForm.setSubmitAsJSON(true);
         configureBlogForm.init();

         // Show the panel
         this._showPanel();
      },

      /**
       * Called when user clicks on the cancel button.
       * Closes the ConfigBlog panel.
       *
       * @method onCancelButtonClick
       * @param type
       * @param args
       */
      onCancelButtonClick: function(type, args)
      {
        this.panel.hide();
      },

      /**
       * Called when a site has been succesfully created on the server.
       * Redirects the user to the new site.
       *
       * @method onConfigBlogSuccess
       * @param response
       */
      onConfigureBlogSuccess: function(response)
      {
         // refresh page
         Alfresco.util.PopupManager.displayMessage({text: "Blog configuration updated successfully"});
         location.reload(true);
      },

      /**
       * Prepares the gui and shows the panel.
       *
       * @method _showPanel
       * @private
       */
      _showPanel: function()
      {         
         // Show the upload panel
         this.panel.show();

         // Firefox insertion caret fix
         Alfresco.util.caretFix(this.id + "-form");

         // Set the focus on the first field
         YAHOO.util.Dom.get(this.id + "-title").focus();
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
      _msg: function BlogComment_msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, "Alfresco.BlogComment", Array.prototype.slice.call(arguments).slice(1));
      }

   };
})();
