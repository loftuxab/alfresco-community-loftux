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
 * CustomisePages component.
 *
 * @namespace Alfresco
 * @class Alfresco.CustomisePages
 */
(function()
{

   var Dom = YAHOO.util.Dom;
   var Event = YAHOO.util.Event;
   var DDM = YAHOO.util.DragDropMgr;

   /**
    * Alfresco.CustomisePages constructor.
    *
    * @param {string} htmlId The HTML id of the parent element
    * @return {Alfresco.CustomisePages} The new CustomisePages instance
    * @constructor
    */
   Alfresco.CustomisePages = function(htmlId)
   {
      this.name = "Alfresco.CustomisePages";
      this.id = htmlId;

      // Register this component
      Alfresco.util.ComponentManager.register(this);

      // Load YUI Components
      Alfresco.util.YUILoaderHelper.require(["button", "container", "datasource"], this.onComponentsLoaded, this);

      return this;
   }

   Alfresco.CustomisePages.prototype =
   {

      /**
       * Object container for storing YUI widget instances.
       *
       * @property widgets
       * @type object
       */
      widgets: {

         /**
          * Remove buttons for each page
          *
          * @property removeButtons
          * @type object Contains other objects of type {pageId: YAHOO.util.Button}
          */
         infoButtons: {},

         /**
          * Remove buttons for each page
          *
          * @property removeButtons
          * @type object Contains other objects of type {pageId: YAHOO.util.Button}
          */
         removeButtons: {},

         /**
          * Select buttons for each page
          *
          * @property selectButtons
          * @type object Contains other objects of type {pageId: YAHOO.util.Button}
          */
         selectButtons: {}
      },


      /**
       * Object container for initialization options
       *
       * @property options
       * @type object
       */
      options:
      {

         /**
          * The avaiable layouts
          *
          * @property layouts
          * @type {object} {"page.pageId":{pageId: "", title: "", description: "", originallyInUse: boolean}}
          */
         pages: {}
      },

      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.CustomisePages} returns 'this' for method chaining
       */
      setOptions: function CP_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },

      /**
       * Set messages for this module.
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {Alfresco.CustomisePages} returns 'this' for method chaining
       */
      setMessages: function CP_setMessages(obj)
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
      onComponentsLoaded: function CP_onComponentsLoaded()
      {
         // Shortcut for dummy instance
         if (this.id === null)
         {
            return;
         }

         // Save reference to buttons so we can change label and such later
         this.widgets.addPagesDiv = Dom.get(this.id + "-addPages-div");
         this.widgets.pagesDiv = Dom.get(this.id + "-pages-div");
         var closeAddPagesLink = document.getElementById(this.id + "-closeAddPages-link");
         YAHOO.util.Event.addListener(closeAddPagesLink, "click", this.onCloseAddPagesLinkClick, this, true);

         this.widgets.addPagesButton = Alfresco.util.createYUIButton(this, "addPages-button", this.onAddPagesButtonClick);
         this.widgets.saveButton = Alfresco.util.createYUIButton(this, "save-button", this.onSaveButtonClick);
         this.widgets.cancelButton = Alfresco.util.createYUIButton(this, "cancel-button", this.onCancelButtonClick);

         // Create the select buttons for all layouts
         this.widgets.selectButtons = [];
         for (var pageId in this.options.pages)
         {
            this.widgets.infoButtons[pageId] = Alfresco.util.createYUIButton(this, "info-button-" + pageId, this.onInfoButtonClick);            
            this.widgets.removeButtons[pageId] = Alfresco.util.createYUIButton(this, "remove-button-" + pageId, this.onRemoveButtonClick);
            this.widgets.selectButtons[pageId] = Alfresco.util.createYUIButton(this, "select-button-" + pageId, this.onSelectButtonClick);
         }
         this._adjustEmptyMessages();
      },

      /**
       * Fired when the user clicks one of the select buttons for a page.
       * Adds the selected page to the current pages.
       *
       * @method onSelectButtonClick
       * @param event {object} an "click" event
       */
      onSelectButtonClick: function CP_onSelectButtonClick(event, button)
      {
         // Find out what layout that is chosen by looking at the clicked button's id
         var buttonId = button.get("id");
         var selectedPageId = buttonId.substring((this.id + "-select-button-").length);

         //Dom.setStyle(this.id + "-currentPage-li-" + selectedPageId, "display", "");
         Dom.setStyle(this.id + "-page-li-" + selectedPageId, "display", "none");
         var page = Dom.get(this.id + "-currentPage-li-" + selectedPageId);
         var container = page.parentNode;
         container.appendChild(page);
         Alfresco.util.Anim.fadeIn(page);

         this._adjustEmptyMessages();
      },

      /**
       * Fired when the user clicks one of the info buttons for a page.
       * Removes the selected page from the current pages.
       *
       * @method onInfoButtonClick
       * @param event {object} an "click" event
       */
      onInfoButtonClick: function CP_onInfoButtonClick(event, button)
      {
         // Find out what layout that is chosen by looking at the clicked button's id
         var buttonId = button.get("id");
         var selectedLayoutId = buttonId.substring((this.id + "-info-button-").length);

         alert("info:" + selectedLayoutId);
      },

      /**
       * Fired when the user clicks one of the add buttons for a page.
       * Displays the pages.
       *
       * @method onAddButtonClick
       * @param event {object} an "click" event
       */
      onAddPagesButtonClick: function CP_onAddPageButtonClick(event, button)
      {
         // Hide add dashlets button and fade in available dashlets
         YAHOO.util.Dom.setStyle(this.widgets.addPagesDiv, "display", "none");
         Alfresco.util.Anim.fadeIn(this.widgets.pagesDiv);
      },

      /**
       * Fired when the user clicks one of the close link.
       * Hides the pages.
       *
       * @method onCloseAddPagesLinkClick
       * @param event {object} an "click" event
       */
      onCloseAddPagesLinkClick: function CP_onCloseAddPagesLinkClick(event)
      {
         // Show add dashlets button and hide available dashlets
         YAHOO.util.Dom.setStyle(this.widgets.addPagesDiv, "display", "");
         YAHOO.util.Dom.setStyle(this.widgets.pagesDiv, "display", "none");
      },

      /**
       * Fired when the user clicks one of the remove buttons for a page.
       * Removes the selected page from the current pages.
       *
       * @method onRemoveButtonClick
       * @param event {object} an "click" event
       */
      onRemoveButtonClick: function CP_onRemoveButtonClick(event, button)
      {
         // Find out what layout that is chosen by looking at the clicked button's id
         var buttonId = button.get("id");
         var selectedPageId = buttonId.substring((this.id + "-remove-button-").length);

         Dom.setStyle(this.id + "-currentPage-li-" + selectedPageId, "display", "none")
         //Dom.setStyle(this.id + "-page-li-" + selectedPageId, "display", "")
         var page = Dom.get(this.id + "-page-li-" + selectedPageId);
         var container = page.parentNode;
         container.appendChild(page);
         Alfresco.util.Anim.fadeIn(page);


         //Alfresco.util.Anim.fadeIn(this.id + "-page-li-" + selectedPageId);
         this._adjustEmptyMessages();
      },

      /*
       * Fired when the user clicks cancel layout button.
       * Hides the layout list.
       *
       * @method onCancelButtonClick
       * @param event {object} an "click" event
       */
      onCancelButtonClick: function CP_onCancelButtonClick(event)
      {
         alert("cancel");
      },

      _adjustEmptyMessages: function CP_adjustEmptyMessages()
      {
         this._adjustEmptyMessage(Dom.get(this.id + "-pages-empty-li"));
         this._adjustEmptyMessage(Dom.get(this.id + "-currentPages-empty-li"));
      },

      _adjustEmptyMessage: function CP_adjustEmptyMessage(li)
      {
         var parentUl = li.parentNode;
         var children = Dom.getChildrenBy(parentUl, function (el)
         {
            return el.tagName.toLowerCase() == ("li") &&
                   !Dom.hasClass(el, "empty") &&
                   Dom.getStyle(el, "display") != "none";
         });
         if(children.length > 0)
         {
            Dom.setStyle(li, "display", "none");
         }
         else
         {
            Dom.setStyle(li, "display", "");
         }
      }

   }

})();

/* Dummy instance to load optional YUI components early */
new Alfresco.CustomisePages(null);
