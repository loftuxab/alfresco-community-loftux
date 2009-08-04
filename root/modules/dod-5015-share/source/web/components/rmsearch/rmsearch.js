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
      
      YAHOO.Bubbling.on("savedSearchAdded", this.onSavedSearchAdded, this);
      YAHOO.Bubbling.on("searchComplete", this.onSearchComplete, this);
      
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
         this.widgets.searchButton.set("disabled", true);
         this.widgets.saveButton = Alfresco.util.createYUIButton(this, "savesearch-button", this.onSaveSearch);
         this.widgets.saveButton.set("disabled", true);
         this.widgets.newButton = Alfresco.util.createYUIButton(this, "newsearch-button", this.onNewSearch);
         this.widgets.printButton = Alfresco.util.createYUIButton(this, "print-button", this.onPrint);
         this.widgets.printButton.set("disabled", true);
         this.widgets.exportButton = Alfresco.util.createYUIButton(this, "export-button", this.onExport);
         this.widgets.exportButton.set("disabled", true);
         
         // retrieve the public saved searches
         // TODO: user specific searches?
         Alfresco.util.Ajax.request(
         {
            url: Alfresco.constants.PROXY_URI + "slingshot/rmsavedsearches/site/" + this.options.siteId,
            successCallback:
            {
               fn: this.onSavedSearchesLoaded,
               scope: this
            },
            failureMessage: me._msg("message.errorloadsearches")
         });
         
         // Field insert menu
         this.widgets.insertFieldMenu = new YAHOO.widget.Button(this.id + "-insertfield",
         {
            type: "menu",
            menu: this.id + "-insertfield-menu"
         });
         this.widgets.insertFieldMenu.getMenu().subscribe("click", function(p_sType, p_aArgs)
         {
            var menuItem = p_aArgs[1];
            if (menuItem)
            {
               // get the namespaced attribute name (e.g. rma:location)
               var attribute = ' ' + menuItem.value + ':';
               Alfresco.util.insertAtCursor(Dom.get(me.id + "-terms"), attribute);
            }
         });
         
         // wire up keydown event on query field
         Event.on(me.id + "-terms", "keyup", this.onQueryKeyup, this, true);
         
         // Call super class onReady() method
         Alfresco.RecordsSearch.superclass.onReady.call(this);
      },
      
      /**
       * BUBBLING LIBRARY EVENT HANDLERS FOR PAGE EVENTS
       * Disconnected event handlers for inter-component event notification
       */
      
      /**
       * Saved Searches AJAX success callback
       * 
       * @method onSavedSearchesLoaded
       * @param res {object} Server response
       */
      onSavedSearchesLoaded: function RecordsSearch_onSavedSearchesLoaded(res)
      {
         var me = this;
         
         var searches = this.options.savedSearches,
             items = YAHOO.lang.JSON.parse(res.serverResponse.responseText).items,
             index;
         
         for (index in items)
         {
            if (items.hasOwnProperty(index))
            {
               s = items[index];
               searches.push(
               {
                  id: s.name,
                  label: s.name,
                  description: s.description,
                  query: s.query,
                  params: s.params
               });
            }
         }
         
         this._initSavedSearchMenu();
      },
      
      /**
       * Query field keydown event handler
       * 
       * @method onQueryKeyup
       * @param e {object} DomEvent
       */
      onQueryKeyup: function RecordsSearch_onQueryKeyup(e)
      {
         var disable = (YAHOO.lang.trim(Dom.get(this.id + "-terms").value).length == 0);
         this.widgets.saveButton.set("disabled", disable);
         this.widgets.searchButton.set("disabled", disable);
      },
      
      /**
       * Search button click event handler
       * 
       * @method onSearchClick
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onSearchClick: function RecordsSearch_onSearchClick(e, args)
      {
         // switch to results tab
         this.widgets.tabs.selectTab(1);
         
         // execute the search and populate the results
         this._performSearch(this._buildSearchQuery());
      },
      
      /**
       * Save Search button click event handler
       * 
       * @method onSaveSearch
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onSaveSearch: function RecordsSearch_onSaveSearch(e, args)
      {
         // get values to pass to the module
         var query = this._buildSearchQuery();
         
         // build up params to pass to the module
         var params = "";
         // search query terms
         var termsElem = Dom.get(this.id + "-terms");
         var terms = YAHOO.lang.trim(termsElem.value);
         params += "terms=" + encodeURIComponent(terms);
         // search undeclared records
         params += "&undeclared=" + (Dom.get(this.id + "-undeclared").checked);
         
         // TODO: prepopulate dialog with current saved search name if any selected
         
         // display the SaveSearch module dialog
         var module = Alfresco.module.getSaveSearchInstance();
         module.setOptions(
            {
               siteId: this.options.siteId,
               query: query,
               params: params
            });
         module.show();
      },
      
      /**
       * New Search button click event handler
       * 
       * @method onNewSearch
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onNewSearch: function RecordsSearch_onNewSearch(e, args)
      {
         // reset fields and clear values
         if (this.widgets.savedSearchMenu)
         {
            this.widgets.savedSearchMenu.set("label", this._msg("button.savedsearches"));
         }
         Dom.get(this.id + "-undeclared").checked = false;
         Dom.get(this.id + "-terms").value = "";
         
         this.widgets.saveButton.set("disabled", true);
         this.widgets.searchButton.set("disabled", true);
         
         // switch to query builder tab
         this.widgets.tabs.selectTab(0);
      },
      
      /**
       * Saved Search has been added
       *
       * @method onSavedSearchAdded
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onSavedSearchAdded: function RecordsSearch_onSavedSearchAdded(layer, args)
      {
         var searchObj = args[1];
         if (searchObj)
         {
            // add to our search to the list
            this.options.savedSearches.push(searchObj);
            
            // rebuild the menu component
            this._initSavedSearchMenu();
            
            // refresh Saved Searches menu button label to the selected item
            this.widgets.savedSearchMenu.set("label", searchObj.label);
         }
      },
      
      /**
       * Search has been complete
       *
       * @method onSearchComplete
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onSearchComplete: function RecordsSearch_onSearchComplete(layer, args)
      {
         var resultCount = args[1].count;
         var disable = (resultCount == 0);
         this.widgets.printButton.set("disabled", disable);
         this.widgets.exportButton.set("disabled", disable);
      },
      
      /**
       * Print button click event handler
       * 
       * @method onPrint
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onPrint: function RecordsSearch_onPrint(e, args)
      {
         // simple and quick way to give a "print friendly" layout - the YUI grid print output
         // is acceptable, so remove the header and footer areas from Share and Search screen
         if (Dom.getStyle("alf-hd", "display") !== "none")
         {
            this.widgets.printButton.set("label", this._msg("button.screen"));
            Dom.setStyle("alf-hd", "display", "none");
            Dom.setStyle("alf-ft", "display", "none");
            Dom.setStyle(this.id + "-tabset", "display", "none");
            Dom.setStyle(this.id + "-header", "display", "none");
         }
         else
         {
            this.widgets.printButton.set("label", this._msg("button.print"));
            Dom.setStyle("alf-hd", "display", "");
            Dom.setStyle("alf-ft", "display", "");
            Dom.setStyle(this.id + "-tabset", "display", "");
            Dom.setStyle(this.id + "-header", "display", "");
         }
      },
      
      /**
       * Export button click event handler
       * 
       * @method onExport
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onExport: function RecordsSearch_onExport(e, args)
      {
         
      },
      
      /**
       * Builds the search query based on the current search terms and parameters.
       *
       * @method _buildSearchQuery
       * @return {string} Full query string for execution
       * @private
       */
      _buildSearchQuery: function RecordsSearch__buildSearchQuery()
      {
         var queryElem = Dom.get(this.id + "-terms");
         var userQuery = YAHOO.lang.trim(queryElem.value);
         
         var query = "";
         if (Dom.get(this.id + "-undeclared").checked === false)
         {
            query = 'ASPECT:"rma:declaredRecord"';
         }
         if (userQuery.length != 0)
         {
            query = query + (query.length != 0 ? (' AND (' + userQuery + ')') : userQuery);
         }
         
         return query;
      },
      
      /**
       * Inits the Saved Searches menu component and handlers.
       *
       * @method _initSavedSearchMenu
       * @private
       */
      _initSavedSearchMenu: function RecordsSearch__initSavedSearchMenu()
      {
         var me = this;
         
         // Saved Searches menu
         this.widgets.savedSearchMenu = new YAHOO.widget.Button(this.id + "-savedsearches-button",
         {
            type: "menu",
            menu: this._buildSavedSearchesMenu()
         });
         
         // Click handler for Saved Search menu items
         this.widgets.savedSearchMenu.getMenu().subscribe("click", function(p_sType, p_aArgs)
         {
            var menuItem = p_aArgs[1];
            if (menuItem)
            {
               // Update the menu button label to be the selected Saved Search
               me.widgets.savedSearchMenu.set("label", menuItem.cfg.getProperty("text"));
               
               // Rebuild search UI based on saved search parameters
               // Params are packed into a single string URL encoded
               var searchObj = me.options.savedSearches[menuItem.value];
               var params = (searchObj.params ? searchObj.params.split("&") : []);
               for (var i in params)
               {
                  var pair = params[i].split("=");
                  switch (pair[0])
                  {
                     case "undeclared":
                     {
                        Dom.get(me.id + "-undeclared").checked = (pair[1] === "true");
                        break;
                     }
                     
                     case "terms":
                     {
                        Dom.get(me.id + "-terms").value = decodeURIComponent(pair[1]);
                        break;
                     }
                  }
               }
               
               me.widgets.saveButton.set("disabled", false);
               me.widgets.searchButton.set("disabled", false);
            }
         });
      },
      
      /**
       * Builds the menuitems for the Saved Searches menu based on the list of saved searches.
       *
       * @method _buildSavedSearchesMenu
       * @return {object} Saved Searches menu item objects
       * @private
       */
      _buildSavedSearchesMenu: function RecordsSearch__buildSavedSearchesMenu()
      {
         this.options.savedSearches.sort(this._sortByLabel);
         var searches = [];
         for (var i=0, j=this.options.savedSearches.length; i<j; i++)
         {
            var search = this.options.savedSearches[i];
            searches.push(
            {
               text: search.label,
               value: i
            });
         }
         return searches;
      },
      
      /**
       * Helper to Array.sort() by the 'label' field of an object..
       *
       * @method _sortByLabel
       * @return {Number}
       * @private
       */
      _sortByLabel: function RecordsSearch__sortByLabel(s1, s2)
      {
         var ss1 = s1.label.toLowerCase(), ss2 = s2.label.toLowerCase();
         return (ss1 > ss2) ? 1 : (ss1 < ss2) ? -1 : 0;
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