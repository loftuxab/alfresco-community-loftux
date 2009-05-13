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
 * Folder links component.
 * 
 * @namespace Alfresco
 * @class Alfresco.FolderLinks
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
   
   /**
    * FolderLinks constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.FolderLinks} The new FolderLinks instance
    * @constructor
    */
   Alfresco.FolderLinks = function(htmlId)
   {
      this.name = "Alfresco.FolderLinks";
      this.id = htmlId;
      
      // initialise prototype properties
      this.widgets = {};
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button"], this.onComponentsLoaded, this);
   
      /* Decoupled event listeners */
      YAHOO.Bubbling.on("folderDetailsAvailable", this.onFolderDetailsAvailable, this);
      
      return this;
   }
   
   Alfresco.FolderLinks.prototype =
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
          * External authentication being used for login
          * 
          * @property externalAuth
          * @type boolean
          */
         externalAuth: false
      },
      
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
       * @return {Alfresco.Search} returns 'this' for method chaining
       */
      setOptions: function FolderLinks_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },
      
      /**
       * Set messages for this component.
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {Alfresco.Search} returns 'this' for method chaining
       */
      setMessages: function FolderLinks_setMessages(obj)
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
      onComponentsLoaded: function FolderLinks_onComponentsLoaded()
      {
         // don't need to do anything we will be informed via an event when data is ready
      },
            
      /**
       * Event handler called when the "folderDetailsAvailable" event is received
       */
      onFolderDetailsAvailable: function FolderLinks_onFolderDetailsAvailable(layer, args)
      {
         var folderData = args[1];
         
         var clipboard = (window.clipboardData && clipboardData.setData);
         
         // populate the text field with the page url
         Dom.get(this.id + "-page-url").value = window.location.href;
         
         // create YUI button for page copy button
         if (clipboard)
         {
            this.widgets.pageCopyButton = Alfresco.util.createYUIButton(this, "page-button", null,
            {
               onclick:
               {
                  fn: this._handleCopyClick,
                  obj: "-page-url",
                  scope: this
               }
            });
         }
 
         // add focus event handlers to fields        
         Event.addListener(Dom.get(this.id + "-page-url"), "focus", this._handleFocus, "-page-url", this);
      },
      
      /**
       * Event handler to copy URLs to the system clipboard
       * 
       * @method _handleCopyClick
       * @param event The event
       * @param urlId The id of the element holding the URL to copy
       */
      _handleCopyClick: function FolderLinks__handleCopyClick(event, urlId)
      {
         clipboardData.setData("Text", Dom.get(this.id + urlId).value);
      },
      
      /**
       * Event handler used to select text in the field when focus is received
       *
       * @method _handleFocus
       * @param event The event
       * @field The suffix of the id of the field to select
       */
      _handleFocus: function FolderLinks__handleFocus(event, field)
      {
         YAHOO.util.Event.stopEvent(event);
         
         var fieldObj = Dom.get(this.id + field);
         if (fieldObj && fieldObj.select)
         {
            fieldObj.select();
         }
      }
   };
})();
