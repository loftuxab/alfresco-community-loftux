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
 * FullPreview component.
 *
 * @namespace Alfresco
 * @class Alfresco.FullPreview
 */
(function()
{

   var Dom = YAHOO.util.Dom;

   /**
    * Preview constructor.
    *
    * @param {string} htmlId The HTML id of the parent element
    * @return {Alfresco.Preview} The new Preview instance
    * @constructor
    * @private
    */
   Alfresco.FullPreview = function(containerId)
   {
      this.name = "Alfresco.FullPreview";
      this.id = containerId;
      this.swf = Alfresco.constants.URL_CONTEXT + "yui/swfplayer/assets/SWFPlayer.swf";

      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      // Load YUI Components
      Alfresco.util.YUILoaderHelper.require(["button", "container", "datatable", "datasource", "swfplayer"], this.onComponentsLoaded, this);

      // Listen for events from other components that wants tis component to show itself
      YAHOO.Bubbling.on("showFullPreview", this.onShowFullPreview, this);

      this.widgets = {};

      return this;
   }

   Alfresco.FullPreview.prototype =
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
          * Noderef to the content to display
          *
          * @property nodeRef
          * @type string
          */
         nodeRef: "",

         /**
          * The file name of the content
          *
          * @property name
          * @type string
          */
         name: "",

         /**
          * The icon displayed in the header of the component
          *
          * @property icon
          * @type string
          */
         icon: "",

         /**
          * The mimeType of the node to display, needed to decide what preview
          * that should be used.
          *
          * @property mimeType
          * @type string
          */
         mimeType: "",

         /**
          * A list of previews available for this component
          *
          * @property previews
          * @type Array
          */
         previews: []
      },


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
       * The flash move will dispatch the contentReady event twice,
       * make sure we only react on it twice.
       *
       * @property contentReady
       * @type boolean
       */
      contentReady: false,

      /**
       * Object representing the state of the loaded swf
       *
       * @property loadedSwf
       * @type {object}
       */
      loadedSwf: null,

      /**
       * Object container for storing YUI widget instances.
       *
       * @property widgets
       * @type object
       */
       widgets: null,

      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.Preview} returns 'this' for method chaining
       */
      setOptions: function P_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },

      /**
       * Set messages for this component.
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {Alfresco.Preview} returns 'this' for method chaining
       */
      setMessages: function P_setMessages(obj)
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
      onComponentsLoaded: function FP_onComponentsLoaded()
      {
         // nodeRef is mandatory
         if (this.options.nodeRef === undefined)
         {
             throw new Error("A nodeRef must be provided");
         }

         // Pass in the path to the .swf movie
         YAHOO.widget.SWFPlayer.SWFURL = this.swf;
         
         // Save a reference to the HTMLElement displaying texts so we can alter the texts later
         this.widgets.swfPlayerMessage = Dom.get(this.id + "-swfPlayerMessage-div");
         this.widgets.titleText = Dom.get(this.id + "-title-span");
         this.widgets.titleImg = Dom.get(this.id + "-title-img");

         // Create and save a references to the buttons  so we can alter them later
         this.widgets.previousButton = Alfresco.util.createYUIButton(this, "previous-button", this.onPreviousButtonClick);
         this.widgets.nextButton = Alfresco.util.createYUIButton(this, "next-button", this.onNextButtonClick);
         this.widgets.closeButton = Alfresco.util.createYUIButton(this, "close-button", this.onCloseButtonClick);

         // Listen for changes on the "Jump to page" textfield
         this.widgets.jumpToPageTextField = Dom.get(this.id + "-jumpToPage-textfield");
         YAHOO.util.Event.addListener(this.widgets.jumpToPageTextField, "change", this.onJumpToPageTextFieldChange, this, true);

         // Save a reference for the span element that displays the current and total no of pages of the viewed content
         this.widgets.currentFrameSpan = Dom.get(this.id + "-currentFrame-span");

         // Set the panel title and image
         this.widgets.titleText["innerHTML"] = this.options.name;
         this.widgets.titleImg.src = Alfresco.constants.URL_CONTEXT + this.options.icon.substring(1);

         // Display the current frame status
         var message = Alfresco.util.message("label.currentFrame", this.name, {"0": "0", "1": "0"});
         this.widgets.currentFrameSpan["innerHTML"] = message;

         // Show the hidden buttons now that they have become styled as yui buttons
         Dom.setStyle(this.id + "-hd-div", "visibility", "");

         // Create the YUI panel
         this._createPanel();
      },

      /**
       * Helper method for creating flash object if appropriate
       *
       * @method _createFlash
       */
      _createFlash: function FP__createFlash()
      {
         if(Alfresco.util.hasRequiredFlashPlayer(9, 0, 45))
         {
            if(this._resolvePreviewUrl())
            {
               if(this.swfPlayer == null)
               {
                  // Maximize the height of the swf div
                  var swfPlayerDiv = Dom.get(this.id + "-swfPlayer-div");
                  Dom.setStyle(swfPlayerDiv, "height", Dom.getViewportHeight() - 100 + "px");

                  // Create and save a reference to the swfPlayer so we can call it later
                  this.swfPlayer = new YAHOO.widget.SWFPlayer(this.id + "-swfPlayer-div", {backgroundColor: "#DCDCDC"});
                  this.swfPlayer.subscribe("loadedSwfError", this.onLoadedSwfError, this, true);
                  this.swfPlayer.subscribe("loadedSwfReady", this.onLoadedSwfReady, this, true);
                  this.swfPlayer.subscribe("loadedSwfOnFrame", this.onLoadedSwfOnFrame, this, true);
                  this.swfPlayer.subscribe("contentReady", this.onContentReady, this, true);
               }
            }
            else
            {
               // Cant find a preview
               var url = Alfresco.constants.PROXY_URI + "api/node/content/" + this.options.nodeRef.replace(":/", "") + "/" + encodeURIComponent(this.options.name) + "?a=true";
               var message = Alfresco.util.message("label.noPreview", this.name, {"0": url});
               this.widgets.swfPlayerMessage["innerHTML"] = message;
            }
         }
         else
         {
            // No sufficient flash player installed
            var message = Alfresco.util.message("label.noFlash", this.name);
            this.widgets.swfPlayerMessage["innerHTML"] = message;
         }

      },

      /**
       * Helper method for deciding what preview to use, if any
       *
       * @method _resolvePreviewUrl
       * @return the name of the preview to use or nullif none is appropriate
       */
      _resolvePreviewUrl: function FP__resolvePreviewUrl(event)
      {
         // Create the url to pass in to the flash movie (add a noCacheToken to avoid cache problems)
         var nodeRefAsLink = this.options.nodeRef.replace(":/", "");

         // Try to prioritise usage of imgpreview for images and webpreview for other content
         var ps = this.options.previews;
         var preview, webpreview = "webpreview", imgpreview = "imgpreview";
         if(this.options.mimeType.match(/image\/jpeg|image\/gif|image\/png/))
         {
            var url = Alfresco.constants.PROXY_URI + "api/node/content/" + nodeRefAsLink + "?a=true/";
            return url;
         }
         else
         {
            preview = Alfresco.util.arrayContains(ps, webpreview) ? webpreview : (Alfresco.util.arrayContains(ps, imgpreview) ? imgpreview : null);
            if(preview)
            {
               var url = Alfresco.constants.PROXY_URI + "api/node/" + nodeRefAsLink + "/content/thumbnails/" + preview;
               url += "?c=force&alf_ticket=" + Alfresco.constants.ALF_TICKET + "&noCacheToken=" + new Date().getTime();
               return url;
            }
         }
         return null;
      },

      /**
       * Called when the "wrapping" SWFPlayer-flash movie is loaded
       *
       * @method onContentReady
       */
      onContentReady: function FP_onContentReady(event)
      {
         if(!this.contentReady)
         {
            // Flash movie makes this call twice, make sure we only react on it once
            this.contentReady = true;


            this.swfPlayer.load(this._resolvePreviewUrl());
         }
      },

      /**
       * Helper method for creating the YUI Panel
       *
       * @method _createPanel
       */
      _createPanel: function()
      {
         var panelHeight = Dom.getViewportHeight() - 60;
         this.widgets.fullPreviewPanel = Dom.get(this.id + "-fullPreview-panel");
         this.widgets.panel = new YAHOO.widget.Panel(this.widgets.fullPreviewPanel,
         {
            modal: true,
            draggable: false,
            fixedcenter: true,
            close: false,
            visible: false,
            height: panelHeight + "px",
            underlay: "none"
         });

         // Add it to the Dom
         this.widgets.panel.render();

      },

      /**
       * Fired by the SWFPlayer when the swf for the nodeRef failed to load.
       *
       * @method onLoadedSwfError
       * @param event {object} an SWFPlayer "swfLoadedfError" event
       */
      onLoadedSwfError: function FP_onLoadedSwfError(event)
      {
         // Disable buttons and hide panel
         this.widgets.previousButton.set("disabled", true);
         this.widgets.nextButton.set("disabled", true);

         // Inform the user about the failure
         var message = "Error";
         if (event.code)
         {
            message = Alfresco.util.message(event.code, this.name);
         }
         Alfresco.util.PopupManager.displayMessage(
         {
            text: message
         });

         // Tell other components that the preview failed
         YAHOO.Bubbling.fire("previewFailure",
         {
            error: event.code,
            nodeRef: this.showConfig.nodeRef,
            failureUrl: this.showConfig.failureUrl
         });

      },

      /**
       * Fired by the SWFPlayer when the swf for the nodeRef has been loaded.
       *
       * @method onLoadedSwfReady
       * @param event {object} an SWFPlayer "swfLoadedReady" event
       */
      onLoadedSwfReady: function FP_onLoadedSwfReady(event)
      {
         this._handleSuccessFullLoadedSwfEvent(event);

         // Show the navigation controls if there are more pages than 1
         if(parseInt(event.totalFrames) > 1)
         {
            Dom.setStyle(this.id + "-controls-div", "visibility", "");
         }
      },

      /**
       * Fired by the SWFPlayer when the swf for the nodeRef has been loaded.
       *
       * @method onLoadedSwfOnFrame
       * @param event {object} an SWFPlayer "loadedSwfOnFrame" event
       */
      onLoadedSwfOnFrame: function FP_onLoadedSwfOnFrame(event)
      {
         this._handleSuccessFullLoadedSwfEvent(event);
      },

      /**
       * Updates the gui to match that state of the loaded swf.
       *
       * @method _handleSuccessFullLoadedSwfEvent
       * @param event {object} an SWFPlayer "swfLoadedEnterFrame" or "swfLoadedReady" event
       */
      _handleSuccessFullLoadedSwfEvent: function FP__handleSuccessFullLoadedSwfEvent(event)
      {
         // Update our local model of the loaded swf
         this.loadedSwf =
         {
            currentFrame: parseInt(event.currentFrame),
            totalFrames: parseInt(event.totalFrames)
         };

         // Update label
         var message = Alfresco.util.message("label.currentFrame", this.name);
         message = YAHOO.lang.substitute(message,
         {
            "0": event.currentFrame,
            "1": event.totalFrames
         });
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
      onJumpToPageTextFieldChange: function FP_onJumpToPageTextFieldChange(event)
      {
         var newFrame = parseInt(event.target.value);
         this.swfPlayer.goToFrameNo(newFrame);
x      },

      /**
       * Fired when the user clicks the previous button.
       * Will make a call to the swfPlayer to make sure it changes the frame on 
       * its loaded swf that represent the previewd document.
       *
       * @method onPreviousButtonClick
       * @param event {object} a Button "click" event
       */
      onPreviousButtonClick: function FP_onPreviousButtonClick()
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
      onNextButtonClick: function FP_onNextButtonClick()
      {
         this.swfPlayer.goToFrameNo(this.loadedSwf.currentFrame + 1);
      },

      /**
       * Fired when the user clicks the close button.
       * Will hide this content.
       *
       * @method onCloseButtonClick
       * @param event {object} a Button "click" event
       */
      onCloseButtonClick: function FP_onCloseButtonClick()
      {
         // Tell other components that they can display their flash movies again
         YAHOO.Bubbling.fire("showFlash");

         this.widgets.panel.hide();
      },

      /**
       * Fired any another component, i.e Preview, to displays the panel with the full preview.
       *
       * @method onShowFullPreview
       * @param layer {object} Event fired (unused)
       * @param args {array} Event parameters (unused)
       */
      onShowFullPreview: function FP_onShowFullPreview(layer, args)
      {
         // Tell other components that they should hide their flash movies so they dont overlap
         YAHOO.Bubbling.fire("hideFlash");

         this.widgets.panel.show();

         this._createFlash();

         var mask = Dom.get(this.id + "-fullPreview-panel_mask");
         if(!Dom.hasClass("full-preview-mask"))
         {
            Dom.addClass(mask, "full-preview-mask");            
         }
         Dom.setStyle(this.widgets.fullPreviewPanel, "border-style", "none");
      }


   };

})();
