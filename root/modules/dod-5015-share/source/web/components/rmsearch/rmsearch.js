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
 * Records Search component.
 * 
 * @namespace Alfresco
 * @class Alfresco.RecordsSearch
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
       Event = YAHOO.util.Event;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML;
   
   /**
    * Search constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.RecordsSearch} The new RecordsSearch instance
    * @constructor
    */
   Alfresco.RecordsSearch = function(htmlId)
   {
      /* Mandatory properties */
      this.name = "Alfresco.RecordsSearch";
      
      /* Super class constructor call */
      Alfresco.RecordsSearch.superclass.constructor.call(this, htmlId);
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);
      
      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "container", "datasource", "datatable", "json", "menu", "tabview"], this.onComponentsLoaded, this);
      
      return this;
   };
   
   YAHOO.extend(Alfresco.RecordsSearch, Alfresco.RecordsResults,
   {
      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function RecordsSearch_onComponentsLoaded()
      {
         Event.onContentReady(this.id, this.onReady, this, true);
      },
      
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function RecordsSearch_onReady()
      {
         var me = this;
         
         // Wire up tab component
         this.widgets.tabs = new YAHOO.widget.TabView(this.id + "-tabs");
         
         // Buttons
         this.widgets.searchButton = Alfresco.util.createYUIButton(this, "search-button", this.onSearchClick);
         
         // Call super class onReady() method
         Alfresco.RecordsSearch.superclass.onReady.call(this);
      },
      
      /**
       * BUBBLING LIBRARY EVENT HANDLERS FOR PAGE EVENTS
       * Disconnected event handlers for inter-component event notification
       */
      
      /**
       * Search button click event handler
       * 
       * @method onSearchClick
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onSearchClick: function RecordsSearch_onSearchClick(e, args)
      {
         var searchTermElem = Dom.get(this.id + "-query");
         var searchTerm = searchTermElem.value;
         
         var query = null;
         if (Dom.get(this.id + "-undeclared").checked === false)
         {
            query = '+ASPECT:"{http://www.alfresco.org/model/recordsmanagement/1.0}declaredRecord"';
         }
         
         // switch to results tab
         this.widgets.tabs.selectTab(1);
         
         // execute the search and populate the results
         this._performSearch(query, searchTerm);
      },
      
      /**
       * Gets a custom message
       *
       * @method _msg
       * @param messageId {string} The messageId to retrieve
       * @return {string} The custom message
       * @private
       */
      _msg: function RecordsSearch__msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, "Alfresco.RecordsSearch", Array.prototype.slice.call(arguments).slice(1));
      }
   });
})();