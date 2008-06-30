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
 * CustomiseLayout component.
 *
 * @namespace Alfresco
 * @class Alfresco.CustomiseLayout
 */
(function()
{

   var Dom = YAHOO.util.Dom;
   var Event = YAHOO.util.Event;
   var DDM = YAHOO.util.DragDropMgr;

   /**
    * Alfresco.CustomiseLayout constructor.
    *
    * @param {string} htmlId The HTML id of the parent element
    * @return {Alfresco.CustomiseLayout} The new CustomiseLayout instance
    * @constructor
    */
   Alfresco.CustomiseLayout = function(htmlId)
   {
      this.name = "Alfresco.CustomiseLayout";
      this.id = htmlId;      

      // Register this component
      Alfresco.util.ComponentManager.register(this);

      // Load YUI Components
      Alfresco.util.YUILoaderHelper.require(["button", "container", "datasource"], this.onComponentsLoaded, this);

      return this;
   }

   Alfresco.CustomiseLayout.prototype =
   {

      /**
       * Object container for storing YUI widget instances.
       * 
       * @property widgets
       * @type object
       */
      widgets: {},

      /**
       * Object container for initialization options
       *
       * @property options
       * @type object
       */
      options:
      {
         /**
          * The current layout
          *
          * @property layouts
          * @type {object} {id: "", description: "", icon: ""}
          */
         currentLayout: {},

         /**
          * The avaiable layouts
          *
          * @property layouts
          * @type {object} {"layout.id":{id: "", description: "", icon: ""}}
          */
         layouts: {}
      },

      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.CustomiseLayout} returns 'this' for method chaining
       */
      setOptions: function DL_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },

      /**
       * Set messages for this module.
       *       
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {Alfresco.CustomiseLayout} returns 'this' for method chaining
       */
      setMessages: function CD_setMessages(obj)
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
      onComponentsLoaded: function FU_onComponentsLoaded()
      {
         // Shortcut for dummy instance
         if (this.id === null)
         {
            return;
         }

         // Save reference to buttons so we can change label and such later
         this.widgets.changeButton = Alfresco.util.createYUIButton(this, "change-button", this.onChangeButtonClick);
         this.widgets.useCurrentButton = Alfresco.util.createYUIButton(this, "useCurrent-button", this.onUseCurrentButtonClick);

         // Create the select buttons for all layouts
         this.widgets.selectButtons = [];
         for (var layoutId in this.options.layouts)
         {
               this.widgets.selectButtons[layoutId] = Alfresco.util.createYUIButton(this, "select-button-" + layoutId, this.onSelectButtonClick);
         }
      },

      /**
       * Fired when the user clicks one of the select buttons for a layout.
       * Changes the current layout to the selected layout and throws an global
       * event, that can be captured by other components, such as
       * Alfresco.CustomiseDashlets. 
       *
       * @method onSelectButtonClick
       * @param event {object} an "click" event
       */
      onSelectButtonClick: function CD_onSelectButtonClick(event, button)
      {
         // Get references to the divs that should be shown or hidden
         var layoutsDiv = Dom.get(this.id + "-layouts-div");
         var currentLayoutDiv = Dom.get(this.id + "-currentLayout-div");

         // Find out what layout that is chosen by lokking at the clicked button's id
         var buttonId = button.get("id");
         var selectedLayoutId = buttonId.substring((this.id + "-select-button-").length);

         // Hide the div that displays the available layouts
         Dom.addClass(layoutsDiv , "hiddenComponents");
         for (var layoutId in this.options.layouts)
         {
            var layoutLi = Dom.get(this.id + "-layout-li-" + layoutId);
            if(selectedLayoutId == layoutId)
            {
               // Set the current layout
               var selectedLayout = this.options.layouts[layoutId];
               this.options.currentLayout = selectedLayout;

               // Hide the newly selected layout
               Dom.addClass(layoutLi, "hiddenComponents");
               var descriptionDiv = Dom.getElementsByClassName("layoutDescription", "div", currentLayoutDiv)[0];
               descriptionDiv.innerHTML = selectedLayout.description;
               var iconImg = Dom.getElementsByClassName("layoutIcon", "img", currentLayoutDiv)[0];
               iconImg.src = selectedLayout.icon;

               // Send out event to let other component know that the layout has changed
               YAHOO.Bubbling.fire("onDashboardLayoutChanged", {dashboardLayout: selectedLayout});
            }
            else
            {
               // Show all the previous layout (should have been hidden)
               if(Dom.hasClass(layoutLi, "hiddenComponents"))
               {
                  Dom.removeClass(layoutLi, "hiddenComponents");
               }
            }
         }
         // Show the currently selected layout-div
         Dom.removeClass(currentLayoutDiv, "hiddenComponents");
      },

      /**
       * Fired when the user clicks change layout button.
       * Hides or shows the layout list.
       *
       * @method changeLayoutButton
       * @param event {object} an "click" event
       */
      onChangeButtonClick: function CD_onChangeButtonClick(event)
      {
         // Get references to the divs that should be shown or hidden
         var layoutsDiv = Dom.get(this.id + "-layouts-div");
         var currentLayoutDiv = Dom.get(this.id + "-currentLayout-div");

         // Hide the available layouts-div
         Dom.addClass(currentLayoutDiv, "hiddenComponents");

         // Show the currently selected layout-div
         Dom.removeClass(layoutsDiv , "hiddenComponents");
      },

      /**
       * Fired when the user clicks cancel layout button.
       * Hides the layout list.
       *
       * @method onUseCurrentButtonClick
       * @param event {object} an "click" event
       */
      onUseCurrentButtonClick: function CD_onUseCurrentButtonClick(event)
      {
         // Get references to the divs that should be shown or hidden
         var layoutsDiv = Dom.get(this.id + "-layouts-div");
         var currentLayoutDiv = Dom.get(this.id + "-currentLayout-div");

         // Hide the available layouts-div
         Dom.addClass(layoutsDiv, "hiddenComponents");

         // Show the currently selected layout-div
         Dom.removeClass(currentLayoutDiv , "hiddenComponents");
      }

   }

})();

/* Dummy instance to load optional YUI components early */
new Alfresco.CustomiseLayout(null);
