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
 * DocumentPreview component.
 *
 * Notice: This is a singleton class: messages (i18n) for singelton classes
 * are set directly with the Alfresco.util.addMessages().
 *
 * @namespace Alfresco.module
 * @class Alfresco.module.DocumentPreview
 */
(function()
{

   /**
    * DocumentPreview constructor.
    *
    * DocumentPreview is considered a singleton so constructor should be treated as private,
    * please use Alfresco.module.getDocumentPreviewInstance() instead.
    *
    * @param {string} htmlId The HTML id of the parent element
    * @return {Alfresco.module.DocumentPreview} The new DocumentPreview instance
    * @constructor
    * @private
    */
   Alfresco.module.DocumentPreview = function(containerId)
   {
      this.name = "Alfresco.module.DocumentPreview";
      this.id = containerId;
      this.swf = Alfresco.constants.URL_CONTEXT + "yui/swfplayer/assets/SWFPlayer.swf";

      var instance = Alfresco.util.ComponentManager.find({id: this.id});
      if(instance !== undefined && instance.length > 0)
      {
         throw new Error("An instance of Alfresco.module.DocumentPreview already exists.");
      }


      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      // Load YUI Components
      Alfresco.util.YUILoaderHelper.require(["button", "container", "datatable", "datasource", "swfplayer"], this.onComponentsLoaded, this);

      return this;
   }

   Alfresco.module.DocumentPreview.prototype =
   {

      /**
       * The default config for the gui state for document previewer.
       * The user can override these properties in the show() method to use the
       * document preview from several components that wants to display them
       * differently.
       *
       * @property defaultShowConfig
       * @type object
       */
      defaultShowConfig:
      {
         nodeRef: "",
         fileName : "",
         icon32: "",
         buttons: [{id: "previous"}, {id: "next"}]
      },

      /**
       * The merged result of the defaultShowConfig and the config passed in
       * to the show method.
       *
       * @property showConfig
       * @type object
       */
      showConfig: {},

      /**
       * Contains the upload gui
       *
       * @property panel
       * @type YAHOO.widget.Panel
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
       * YUI class that controls the swf-player.swf that controls the
       * preview-swf-movie (which is received from the thumbnails service).
       *
       * This is a class created by Alfresco to fit in the YUI library.
       *
       * @property swfPlayer
       * @type YAHOO.widget.SWFPlayer
       */
      swfPlayer: null,

      /**
       * Objectrepresenting the state of the loaded swf
       *
       * @property loadedSwf
       * @type {object}
       */
      loadedSwf: null,

      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function DP_onComponentsLoaded()
      {
         // Tell the YUI class where the swf is
         YAHOO.widget.SWFPlayer.SWFURL = this.swf;

         // Shortcut for dummy instance
         if (this.id === null)
         {
            return;
         }
      },

      /**
       * Show can be called multiple times and will display the preview dialog
       * in different ways and with different documents.
       *
       * @method show
       * @param config {object} describes how the upload dialog should be displayed
       * The config object is in the form of:
       * {
       *    buttons: {array}, // Array of the buttons to display
       *    nodeRef: {string} // noderef to the document to display
       * }
       */
      show: function DP_show(config)
      {
         // Merge the supplied config with default config and check mandatory properties
         this.showConfig = YAHOO.lang.merge(this.defaultShowConfig, config);
         if (this.showConfig.nodeRef === undefined)
         {
             throw new Error("A nodeRef must be provided");
         }
         // Check if the uploader has been shoed before
         if (this.panel)
         {
            this._showPanel();
         }
         else
         {
            // If it hasn't load the gui (template) from the server
            Alfresco.util.Ajax.request(
            {
               url: Alfresco.constants.URL_SERVICECONTEXT + "modules/document-preview?htmlid=" + this.id,
               successCallback:
               {
                  fn: this.onTemplateLoaded,
                  scope: this
               },
               failureMessage: "Could not load document preview template.",
               execScripts: true
            });
            
         }
      },


      /**
       * Called when the uploader html template has been returned from the server.
       * Creates the YIU gui objects such as the data table and panel,
       * saves references to HTMLElements inside the template for easy access
       * during upload progress and finally shows the panel with the gui inside.
       *
       * @method onTemplateLoaded
       * @param response {object} a Alfresco.util.Ajax.request response object
       */
      onTemplateLoaded: function DP_onTemplateLoaded(response)
      {
         var Dom = YAHOO.util.Dom;

         // Inject the template from the XHR request into a new DIV element
         var containerDiv = document.createElement("div");
         containerDiv.innerHTML = response.serverResponse.responseText;         

         var panelWidth = document.width > 1000 ? 1000 : document.width - 20;
         var panelHeight = document.height - 100;

         // Create the panel from the HTML returned in the server reponse
         var dialogDiv = YAHOO.util.Dom.getFirstChild(containerDiv);
         this.panel = new YAHOO.widget.Panel(dialogDiv,
         {
            modal: true,
            draggable: false,
            fixedcenter: true,
            visible: false,
            width: panelWidth,
            height: panelHeight
         });

         /**
          * Render the server reponse so the contents get inserted in the Dom.
          * Scripts in the template, such as setMessage(),  will also get run
          * at this moment. 
          */
         this.panel.render(document.body);

         // Save a reference to the HTMLElement displaying texts so we can alter the texts later
         this.widgets.titleText = Dom.get(this.id + "-title-span");
         this.widgets.titleImg = Dom.get(this.id + "-title-img");

         // Create and save a references to the buttons so we can alter them later
         this.widgets.previousButton = Alfresco.util.createYUIButton(this, "previous-button", this.onPreviousButtonClick);
         this.widgets.nextButton = Alfresco.util.createYUIButton(this, "next-button", this.onNextButtonClick);

         this.widgets.jumpToPageTextField = Dom.get(this.id + "-jumpToPage-textfield");
         YAHOO.util.Event.addListener(this.widgets.jumpToPageTextField, "change", this.onJumpToPageTextFieldChange, this, true);

         this.widgets.currentFrameSpan = Dom.get(this.id + "-currentFrame-span");

         // Create and save a reference to the swfPlayer so we can call it later
         this.swfPlayer = new YAHOO.widget.SWFPlayer(this.id + "-swfPlayer-div", {backgroundColor: "#5C5C5C"});
         this.swfPlayer.subscribe("contentReady", this.onContentReady, this, true);
         this.swfPlayer.subscribe("loadedSwfError", this.onLoadedSwfError, this, true);
         this.swfPlayer.subscribe("loadedSwfReady", this.onLoadedSwfReady, this, true);
         this.swfPlayer.subscribe("loadedSwfOnFrame", this.onLoadedSwfOnFrame, this, true);
      },

      /**
       * Fired by the YUIAdapter when the swfplayer.swf has loaded.
       *
       * @method onContentReady
       * @param event {object} an YUIAdapater "onContentReady" event
       */
      onContentReady: function onContentReady(event)
      {
         // Show the preview panel
         this._showPanel();
      },

      /**
       * Fired by the SWFPlayer when the swf for the nodeRef failed to load.
       *
       * @method onLoadedSwfError
       * @param event {object} an SWFPlayer "swfLoadedfError" event
       */
      onLoadedSwfError: function onLoadedSwfError(event)
      {
         alert("Error");
         this.widgets.previousButton.set("disabled", true);
         this.widgets.nextButton.set("disabled", true);
      },

      /**
       * Fired by the SWFPlayer when the swf for the nodeRef has been loaded.
       *
       * @method onLoadedSwfReady
       * @param event {object} an SWFPlayer "swfLoadedReady" event
       */
      onLoadedSwfReady: function onLoadedSwfReady(event)
      {
         this._handleSuccessFulLoadedSwfEvent(event);
      },

      /**
       * Fired by the SWFPlayer when the swf for the nodeRef has been loaded.
       *
       * @method onLoadedSwfOnFrame
       * @param event {object} an SWFPlayer "loadedSwfOnFrame" event
       */
      onLoadedSwfOnFrame: function DP_onLoadedSwfOnFrame(event)
      {
         this._handleSuccessFulLoadedSwfEvent(event);
      },

      /**
       * Updates the gui to match that state of the loaded swf.
       *
       * @method _handleSuccessFulLoadedSwfEvent
       * @param event {object} an SWFPlayer "swfLoadedEnterFrame" or "swfLoadedReady" event
       */
      _handleSuccessFulLoadedSwfEvent: function DP__handleSuccessFulLoadedSwfEvent(event)
      {
         // Update our local model of the loaded swf
         this.loadedSwf = {
            currentFrame: parseInt(event.currentFrame),
            totalFrames: parseInt(event.totalFrames)
         };

         // Update label
         var message = Alfresco.util.message("label.currentFrame", this.name);
         message = YAHOO.lang.substitute(message, {"0": event.currentFrame, "1": event.totalFrames});
         this.widgets.currentFrameSpan["innerHTML"] = message;

         // Enable buttons to navigate in the loaded swf
         this.widgets.previousButton.set("disabled", !(this.loadedSwf.currentFrame > 1));
         this.widgets.nextButton.set("disabled", !(this.loadedSwf.currentFrame < this.loadedSwf.totalFrames));
      },


      /**
       * Fired when the user clicks the previous button.
       * Will make a call to the swfPlayer to make sure it changes the frame on
       * its loaded swf that represent the previewd document.
       *
       * @method onJumpToPageTextFieldChange
       * @param event {object} a input text "change" event
       */
      onJumpToPageTextFieldChange: function DP_onJumpToPageTextFieldChange(event)
      {
         var newFrame = parseInt(event.target.value);
         if(newFrame > this.loadedSwf.totalFrames)
         {
            var message = Alfresco.util.message("message.invalidFrame", this.name);
            message = YAHOO.lang.substitute(message, {"0": "1", "1": this.loadedSwf.totalFrames});
            Alfresco.util.PopupManager.displayMessage({text: message});
         }
         else
         {
            this.swfPlayer.goToFrameNo(newFrame);
         }
      },

      /**
       * Fired when the user clicks the previous button.
       * Will make a call to the swfPlayer to make sure it changes the frame on 
       * its loaded swf that represent the previewd document.
       *
       * @method onPreviousButtonClick
       * @param event {object} a Button "click" event
       */
      onPreviousButtonClick: function DP_onPreviousButtonClick()
      {
         this.swfPlayer.goToFrameNo(this.loadedSwf.currentFrame - 1);
      },

      /**
       * Fired when the user clicks the next button.
       * Will make a call to the swfPlayer to make sure it changes the frame on
       * its loaded swf that represent the previewd document.
       *
       * @method onNextButtonClick
       * @param event {object} a Button "click" event
       */
      onNextButtonClick: function DP_onNextButtonClick()
      {
         this.swfPlayer.goToFrameNo(this.loadedSwf.currentFrame + 1);
      },

      /**
       * Adjust the gui according to the config passed into the show method.
       *
       * @method _applyConfig
       * @private
       */
      _applyConfig: function DP__applyConfig()
      {
         var Dom = YAHOO.util.Dom;

         // Set the panel title
         this.widgets.titleText["innerHTML"] = this.showConfig.fileName;
         this.widgets.titleImg.src = this.showConfig.icon32;

         var message = Alfresco.util.message("label.currentFrame", this.name);
         message = YAHOO.lang.substitute(message, {"0": "0", "1": "0"});
         this.widgets.currentFrameSpan["innerHTML"] = message;

         var nodeRef = this.showConfig.nodeRef;
         var space = nodeRef.substring(0, nodeRef.indexOf(":"));
         var store = nodeRef.substring(nodeRef.indexOf("://") + 3, nodeRef.lastIndexOf("/"));
         var ref = nodeRef.substring(nodeRef.lastIndexOf("/") + 1);
         var url = Alfresco.constants.PROXY_URI + "api/node/" + space + "/" + store + "/" + ref + "/content/thumbnails/webpreview"
         url += "?qc=true&alf_ticket=" + Alfresco.constants.ALF_TICKET;

         this.swfPlayer.load(url);
      },

      /**
       * Prepares the gui and shows the panel.
       *
       * @method _showPanel
       * @private                   
       */
      _showPanel: function DP__showPanel()
      {
         // Reset references and the gui before showing it
         this.widgets.previousButton.set("disabled", true);
         this.widgets.nextButton.set("disabled", true);

         // Apply the config before it is showed
         this._applyConfig();

         // Show the upload panel
         this.panel.show();
      }

   };

})();


Alfresco.module.getDocumentPreviewInstance = function()
{
   var instanceId = "alfresco-documentPreview-instance";
   var instance = Alfresco.util.ComponentManager.find({id: instanceId});
   if(instance !== undefined && instance.length > 0)
   {
      instance = instance[0];
   }
   else
   {
      instance = new Alfresco.module.DocumentPreview(instanceId);
   }
   return instance;
}

/* Create the instance to load optional YUI components and SWF early */
Alfresco.module.getDocumentPreviewInstance();       
