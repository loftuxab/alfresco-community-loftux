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
 * Dashboard Activities common component.
 * 
 * @namespace Alfresco
 * @class Alfresco.Activities
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
    * Dashboard Activities constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.Activities} The new component instance
    * @constructor
    */
   Alfresco.Activities = function Activities_constructor(htmlId)
   {
      this.name = "Alfresco.Activities";
      this.id = htmlId;
      
      this.widgets = {};

      // Register this component
      Alfresco.util.ComponentManager.register(this);

      // Load YUI Components
      Alfresco.util.YUILoaderHelper.require(["button", "container"], this.onComponentsLoaded, this);

      return this;
   }

   Alfresco.Activities.prototype =
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
          * Dashlet mode
          * 
          * @property mode
          * @type string
          * @default "site"
          */
         mode: "site",

         /**
          * Current siteId.
          * 
          * @property siteId
          * @type string
          */
         siteId: "",

         /**
          * Currently active filter.
          * 
          * @property activeFilter
          * @type string
          * @default "today"
          */
         activeFilter: "today"
      },
      
      /**
       * Object container for storing YUI widget instances.
       * 
       * @property widgets
       * @type object
       */
      widgets: null,

      /**
       * Activity list DOM container.
       * 
       * @property activityList
       * @type object
       */
      activityList: null,

      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.Activities} returns 'this' for method chaining
       */
      setOptions: function Activities_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },
      
      /**
       * Set messages for this component.
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {Alfresco.Activities} returns 'this' for method chaining
       */
      setMessages: function Activities_setMessages(obj)
      {
         Alfresco.util.addMessages(obj, this.name);
         return this;
      },
      
      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function Activities_onComponentsLoaded()
      {
         Event.onContentReady(this.id, this.onReady, this, true);
      },

      /**
       * Fired by YUI when parent element is available for scripting
       * @method onReady
       */
      onReady: function Activities_onReady()
      {
         var me = this;

         // "Today" filter
         this.widgets.today = new YAHOO.widget.Button(this.id + "-today",
         {
            type: "checkbox",
            value: "today",
            checked: true
         });
         this.widgets.today.on("checkedChange", this.onTodayCheckedChanged, this.widgets.today, this);
         
         // Dropdown filter
         this.widgets.range = new YAHOO.widget.Button(this.id + "-range",
         {
            type: "split",
            menu: this.id + "-range-menu"
         });
         this.widgets.range.on("click", this.onDateFilterClicked, this, true);
         this.widgets.range.getMenu().subscribe("click", function (p_sType, p_aArgs)
         {
            var menuItem = p_aArgs[1];
            if (menuItem)
            {
               me.widgets.range.set("label", menuItem.cfg.getProperty("text"));
               me.onDateFilterChanged.call(me, p_aArgs[1]);
            }
         });
         this.widgets.range.value = "7";
         
         // The activity list container
         this.activityList = Dom.get(this.id + "-activityList");
         
         // Populate the activity list
         this.populateActivityList("today");
      },
      
      /**
       * Populate the activity list via Ajax request
       * @method populateActivityList
       */
      populateActivityList: function Activities_populateActivityList(filter)
      {
         // Load the activity list
         Alfresco.util.Ajax.request(
         {
            url: Alfresco.constants.URL_SERVICECONTEXT + "components/dashlets/activities/list",
            dataObj:
            {
               site: this.options.siteId,
               mode: this.options.mode,
               filter: filter
            },
            successCallback:
            {
               fn: this.onListLoaded,
               scope: this,
               obj: filter
            },
            failureCallback:
            {
               fn: this.onListLoadFailed,
               scope: this
            },
            scope: this
         });
      },
      
      /**
       * List loaded successfully
       * @method onListLoaded
       * @param p_response {object} Response object from request
       */
      onListLoaded: function Activities_onListLoaded(p_response, p_obj)
      {
         this.options.activeFilter = p_obj;
         this.activityList.innerHTML = p_response.serverResponse.responseText;
         this.updateFeedLink();
      },

      /**
       * List load failed
       * @method onListLoadFailed
       */
      onListLoadFailed: function Activities_onListLoadFailed()
      {
         this.activityList.innerHTML = '<div class="detail-list-item first-item last-item">' + this._msg("label.load-failed") + '</div>';
      },
      
      /**
       * Sets the active filter highlight in the UI
       * @method setActiveFilter
       * @param filter {string} The current filter
       */
      setActiveFilter: function Activities_setActiveFilter(filter)
      {
         switch (filter)
         {
            case "today":
               Dom.removeClass(this.widgets.range.get("element"), "yui-checkbox-button-checked");
               break;
            
            default:
               this.widgets.today.set("checked", false, true);
               Dom.addClass(this.widgets.range.get("element"), "yui-checkbox-button-checked");
               break;
         }
         
      },
      
      /**
       * Updates the href attribute on the feed link
       * @method updateFeedLink
       */
      updateFeedLink: function Activities_updateFeedLink(filter)
      {
         var link = Dom.get(this.id + "-feedLink");
         if (link)
         {
            var url = Alfresco.constants.URL_FEEDSERVICECONTEXT + "components/dashlets/activities/list?";
            var dataObj =
            {
               format: "atomfeed",
               mode: this.options.mode,
               site: this.options.siteId,
               filter: this.options.activeFilter
            }
            url += Alfresco.util.Ajax.jsonToParamString(dataObj, true);
            link.setAttribute("href", url);
         }
      },
      

      /**
       * YUI WIDGET EVENT HANDLERS
       * Handlers for standard events fired from YUI widgets, e.g. "click"
       */

      /**
       * Today only
       * @method onTodayCheckedChanged
       * @param p_oEvent {object} Button event
       * @param p_obj {object} Button
       */
      onTodayCheckedChanged: function Activities_onTodayCheckedChanged(p_oEvent, p_obj)
      {
         this.setActiveFilter("today");
         this.populateActivityList("today");
         p_obj.set("checked", true, true);
      },

      /**
       * Date button clicked event handler
       * @method onDateFilterClicked
       * @param p_oEvent {object} Dom event
       */
      onDateFilterClicked: function Activities_onDateFilterClicked(p_oEvent)
      {
         var filter = this.widgets.range.value;
         this.setActiveFilter(filter);
         this.populateActivityList(filter);
      },
      
      /**
       * Date drop-down changed event handler
       * @method onDateFilterChanged
       * @param p_oMenuItem {object} Selected menu item
       */
      onDateFilterChanged: function Activities_onDateFilterChanged(p_oMenuItem)
      {
         var filter = p_oMenuItem.value;
         this.widgets.range.value = filter;
         this.setActiveFilter(filter);
         this.populateActivityList(filter);
      },


      /**
       * Gets a custom message
       *
       * @method _msg
       * @param messageId {string} The messageId to retrieve
       * @return {string} The custom message
       * @private
       */
      _msg: function Activities__msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, "Alfresco.Activities", Array.prototype.slice.call(arguments).slice(1));
      }
   };
})();
