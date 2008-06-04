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
 * CreateSite module
 *
 * A dialog for creating sites
 *
 * @namespace Alfresco.module
 * @class Alfresco.module.CreateSite
 */
(function()
{

   /**
    * CreateSite constructor.
    *
    * @param htmlId {string} A unique id for this component
    * @return {Alfresco.CreateSite} The new DocumentList instance
    * @constructor
    */
   Alfresco.module.CreateSite = function(containerId)
   {
      this.name = "Alfresco.module.CreateSite";
      this.id = containerId;
      
      this.dialog = null;

      // Register this component
      Alfresco.util.ComponentManager.register(this);

      // Load YUI Components
      Alfresco.util.YUILoaderHelper.require(["button", "container", "connection", "selector", "json", "event"], this.componentsLoaded, this);

      return this;
   };

   Alfresco.module.CreateSite.prototype =
   {

      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
      componentsLoaded: function()
      {
         /* Shortcut for dummy instance */
         if (this.id === null)
         {
            return;
         }
      },

      /**
       * Shows the CreteSite dialog to the user.
       *
       * @method show
       */
      show: function()
      {
         if(this.dialog)
         {
            /**
             * The dialog gui has been showed before and its gui has already
             * been loaded and created
             */
            this.dialog.show();
         }
         else
         {
            /**
             * Load the gui from the server and let the templateLoaded() method
             * handle the rest.
             */
            Alfresco.util.Ajax.request(
            {
               url: Alfresco.constants.URL_SERVICECONTEXT + "modules/create-site",
               dataObj:
               {
                  htmlid: this.id
               },
               successCallback:
               {
                  fn: this.templateLoaded,
                  scope: this
               },
               failureMessage: "Could not load create site template"
            });
         }
      },

      /**
       * Called when the CreateSite html template has been returned from the server.
       * Creates the YUI gui objects such as buttons and a panel and shows it.
       *
       * @method onTemplateLoaded
       * @param response {object} a Alfresco.util.Ajax.request response object 
       */
      templateLoaded: function(response)
      {
         // Create a placeHolder for the template string to be rendered in
         var Dom = YAHOO.util.Dom;
         var div = document.createElement("div");
         div.innerHTML = response.serverResponse.responseText;

         // Move the template node out from the placeHolder and create a panel from it
         div = Dom.getElementsByClassName("create-site", "div", div)[0];
         this.dialog = new YAHOO.widget.Panel(div,
         {
            modal: true,
            draggable: false,
            fixedcenter: true,
            close: false,
            visible: false
         });

         // Add it to the Dom
         this.dialog.render(document.body);

         // Create the cancel button
         var cancelButton = new YAHOO.widget.Button(this.id + "-cancel-button", {type: "button"});
         cancelButton.subscribe("click", this.onCancelButtonClick, this, true);

         // Create the ok button, the forms runtime will handle when its clicked
         var okButton = new YAHOO.widget.Button(this.id + "-ok-button", {type: "submit"});

         // Configure the forms runtime
         var createSiteForm = new Alfresco.forms.Form(this.id + "-createSite-form");

         // Shortname is mandatory
         createSiteForm.addValidation(this.id + "-shortName", Alfresco.forms.validation.mandatory, null, "blur");
         // and can NOT contain whitespace characters
         createSiteForm.addValidation(this.id + "-shortName", Alfresco.forms.validation.regexMatch, {pattern: /^[^\s]*$/}, "blur");
         // and should be valid file name
         createSiteForm.addValidation(this.id + "-shortName", Alfresco.forms.validation.nodeName, null, "blur");

         // The ok button is the submit button, and it should be enabled when the form is ready
         createSiteForm.setShowSubmitStateDynamically(true);
         createSiteForm.setSubmitElements(okButton);

         // Submit as an ajax submit (not leave the page), in json format
         createSiteForm.setAJAXSubmit(true,
         {
            successCallback:
            {
               fn: this.onCreateSiteSuccess,
               scope: this
            }
         });
         createSiteForm.setSubmitAsJSON(true);
         createSiteForm.init();

         // Show the dialog
         this.dialog.show();
      },

      /**
       * Called when user clicks on the cancel button.
       * Closes the CreateSite dialog.
       *
       * @method onCancelButtonClick
       * @param type
       * @param args
       */
      onCancelButtonClick: function(type, args)
      {
        this.dialog.hide();
      },

      /**
       * Called when a site has been succesfully created on the server.
       * Redirects the user to the new site.
       *
       * @method onCreateSiteSuccess
       * @param response
       */
      onCreateSiteSuccess: function(response)
      {
         if (response.json === undefined || response.json.shortName === undefined || response.json.shortName.length == 0)
         {
            // We have got a positive status code from the server,
            // but the response doesn't look as it should.
            Alfresco.util.PopupManager.displayMessage({text: "Received a success message with missing variables (shortname)"});
         }
         else
         {
            // The site has been successfully created, redirect the user to it.
            document.location.href = Alfresco.constants.URL_CONTEXT + "page/collaboration-dashboard?site=" + response.json.shortName;
         }
      }

   };
})();

/* Dummy instance to load optional YUI components early */
new Alfresco.module.CreateSite(null);