/**
 * Copyright (C) 2005-2009 Alfresco Software Limited.
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
 * RecordsMetaData tool component.
 * 
 * @namespace Alfresco
 * @class Alfresco.RecordsMetaData
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
    * RecordsMetaData constructor.
    * 
    * @param {String} htmlId The HTML id üof the parent element
    * @return {Alfresco.RecordsMetaData} The new RecordsMetaData instance
    * @constructor
    */
   Alfresco.RecordsMetaData = function(htmlId)
   {
      this.name = "Alfresco.RecordsMetaData";
      Alfresco.RecordsMetaData.superclass.constructor.call(this, htmlId);

      /* Register this component */
      Alfresco.util.ComponentManager.register(this);
      
      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "container", "json", "history"], this.onComponentsLoaded, this);
      
      /* Define panel handlers */
      var parent = this;
      
      // NOTE: the panel registered first is considered the "default" view and is displayed first
      
      /* Search Panel Handler */
      ViewPanelHandler = function ViewPanelHandler_constructor()
      {
         ViewPanelHandler.superclass.constructor.call(this, "view");
      };
      
      YAHOO.extend(ViewPanelHandler, Alfresco.ConsolePanelHandler,
      {
         onLoad: function onLoad()
         {
            // widgets
            parent.widgets.newPropertyButton = Alfresco.util.createYUIButton(parent, "newproperty-button", this.onNewPropertyClick);
            
            // TODO: hook up onclick events for selected list object
            // TODO: set initially selected object
            Dom.get(parent.id + "-metadata-item").innerHTML = parent._msg("label.recordseries");
         },
         
         onShow: function onShow()
         {
            // TODO: load meta-data for selected object
         },
         
         onUpdate: function onUpdate()
         {
            
         },
         
         onNewPropertyClick: function onNewPropertyClick()
         {
            
         }
      });
      new ViewPanelHandler();
      
      return this;
   };
   
   YAHOO.extend(Alfresco.RecordsMetaData, Alfresco.ConsoleTool,
   {
      /**
       * Object container for initialization options
       *
       * @property options
       * @type object
       */
      options:
      {
         
      },
      
      /**
       * Current selected type.
       * 
       * @property currentTyp
       * @type string
       */
      currentType: "",
      
      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function RecordsMetaData_onComponentsLoaded()
      {
         Event.onContentReady(this.id, this.onReady, this, true);
      },
      
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function RecordsMetaData_onReady()
      {
         // TODO: setup widgets
         
         // Call super-class onReady() method
         Alfresco.RecordsMetaData.superclass.onReady.call(this);
      },
      
      /**
       * YUI WIDGET EVENT HANDLERS
       * Handlers for standard events fired from YUI widgets, e.g. "click"
       */
      
      /**
       * History manager state change event handler (override base class)
       *
       * @method onStateChanged
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onStateChanged: function RecordsMetaData_onStateChanged(e, args)
      {
         var state = this.decodeHistoryState(args[1].state);
         
         // test if panel has actually changed?
         if (state.panel)
         {
            this.showPanel(state.panel);
         }
         
         // TODO: add history state
      },
      
      /**
       * Encode state object into a packed string for use as url history value.
       * Override base class.
       * 
       * @method encodeHistoryState
       * @param obj {object} state object
       * @private
       */
      encodeHistoryState: function RecordsMetaData_encodeHistoryState(obj)
      {
         // wrap up current state values
         var stateObj = {};
         if (this.currentPanelId !== "")
         {
            stateObj.panel = this.currentPanelId;
         }
         
         // TODO: add history state
         
         // convert to encoded url history state - overwriting with any supplied values
         var state = "";
         if (obj.panel || stateObj.panel)
         {
            state += "panel=" + encodeURIComponent(obj.panel ? obj.panel : stateObj.panel);
         }
         
         return state;
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
      _msg: function RecordsMetaData__msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, "Alfresco.RecordsMetaData", Array.prototype.slice.call(arguments).slice(1));
      }
   });
})();