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
 * InviteUsers component.
 * 
 * @namespace Alfresco
 * @class Alfresco.InviteUsers
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
    * InviteUsers constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.InviteUsers} The new InviteUsers instance
    * @constructor
    */
   Alfresco.InviteUsers = function(htmlId)
   {
      this.name = "Alfresco.InviteUsers";
      this.id = htmlId;
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "container", "datasource", "datatable", "json", "history"], this.onComponentsLoaded, this);
   
      return this;
   }
   
   Alfresco.InviteUsers.prototype =
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
       * Object container for storing YUI widget instances.
       * 
       * @property widgets
       * @type object
       */
      widgets: {},
      
      listWidgets: [],
      
      searchTerm: "",
      
      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.InviteUsers} returns 'this' for method chaining
       */
      setOptions: function InviteUsers_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },
      
      /**
       * Set messages for this component.
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {Alfresco.InviteUsers} returns 'this' for method chaining
       */
      setMessages: function InviteUsers_setMessages(obj)
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
      onComponentsLoaded: function InviteUsers_onComponentsLoaded()
      {
         Event.onContentReady(this.id, this.onReady, this, true);
      },
   
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function InviteUsers_onReady()
      {  
         // search button
         this.widgets.searchButton = Alfresco.util.createYUIButton(this, "search-button", this.searchButtonClick);

         // DataSource definition  
         var peopleSearchUrl = Alfresco.constants.PROXY_URI + "api/people?";
         this.widgets.dataSource = new YAHOO.util.DataSource(peopleSearchUrl);
         this.widgets.dataSource.responseType = YAHOO.util.DataSource.TYPE_JSON;
         this.widgets.dataSource.connXhrMode = "queueRequests";
         this.widgets.dataSource.responseSchema =
         {
             resultsList: "people",
             fields: ["url", "userName", "avatar", "title", "firstName", "lastName", "organisation", "jobtitle", "email"]
         };
         var me = this;
         this.widgets.dataSource.doBeforeParseData = function InviteUsers_doBeforeParseData(oRequest , oFullResponse)
         {
            var updatedResponse = oFullResponse;
               
            if (oFullResponse)
            {
               var items = [];
               
               // determine list of sites to show
               if (me.searchTerm.length > 0)
               {
                  // filter the results for the search term
                  var lowerCaseTerm = me.searchTerm.toLowerCase();
                  for (var x = 0; x < oFullResponse.people.length; x++)
                  {
                     var personData = oFullResponse.people[x];
                     var firstName = personData.firstName.toLowerCase();
                     var lastName = personData.lastName.toLowerCase();
                     
                     // Determine if person matches search term
                     if (firstName.indexOf(lowerCaseTerm) != -1 ||
                         lastName.indexOf(lowerCaseTerm) != -1)
                     {
                        // add site to list
                        items.push(personData);
                     }
                  }
               }
               
               // we need to wrap the array inside a JSON object so the DataTable is happy
               updatedResponse = {
                  "people": items
               };
            }
            
            return updatedResponse;
         }
         
         // setup of the datatable.
         this._setupDataTable();

         // Finally show the component body here to prevent UI artifacts on YUI button decoration
         Dom.setStyle(this.id + "-body", "visibility", "visible");
      },
      
      _setupDataTable: function InviteUsers_setupDataTable()
      {
         /**
          * DataTable Cell Renderers
          *
          * Each cell has a custom renderer defined as a custom function. See YUI documentation for details.
          * These MUST be inline in order to have access to the Alfresco.InviteUsers class (via the "me" variable).
          */
         var me = this;
          
         /**
          * Thumbnail custom datacell formatter
          *
          * @method renderCellThumbnail
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         renderCellThumbnail = function InviteUsers_renderCellThumbnail(elCell, oRecord, oColumn, oData)
         {
            var avatarUrl = Alfresco.constants.URL_CONTEXT + "/components/images/no-photo.png";
            if (oRecord.getData("avatar") != undefined)
            {
               var avatarUrl = Alfresco.constants.PROXY_URI + oRecord.getData("avatar") + "?c=queue&ph=true";
            }
            oColumn.width = 70;
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");
            Dom.setStyle(elCell.parentNode, "height", oColumn.width + "px");

            elCell.innerHTML = '<span class="avatar-image"><img src="' + avatarUrl + '" alt="avatar image" /></span>';
         };

         /**
          * Description/detail custom datacell formatter
          *
          * @method renderCellDescription
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         renderCellDescription = function InviteUsers_renderCellDescription(elCell, oRecord, oColumn, oData)
         {
            // we currently render all results the same way
            var name = oRecord.getData("userName");
            var firstName = oRecord.getData("firstName");
            var lastName = oRecord.getData("lastName");
            if (firstName != undefined || lastName != undefined)
            {
               name = (firstName != undefined) ? firstName + " " : "";
               name += (lastName != undefined) ? lastName : "";
            }

            var title = (oRecord.getData("title") != undefined) ? oRecord.getData("title") : "";
            var company = (oRecord.getData("company") != undefined) ? oRecord.getData("company") : "";
            desc = '<h3 class="itemname">' + name + '</a></h3>';
            desc += '<div class="detail">' + title + '</div>';
            desc += '<div class="detail">' + company + '</div>';
            elCell.innerHTML = desc;
         };
         
         /**
          * Add button datacell formatter
          *
          * @method renderCellThumbnail
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         renderCellAddButton = function InviteUsers_renderCellAddButton(elCell, oRecord, oColumn, oData)
         {
            oColumn.width = 80;
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");
            
            var id = oRecord.getData('userName');
            var desc = '<span class="addduser" id="' + me.id + '-adduser-' + id + '"></span>';
            elCell.innerHTML = desc;

            // create button
            var button = new YAHOO.widget.Button(
               {
                   type: "button",
                   label: "Add >>",
                   name: me.id + "-adduserbutton-" + id,
                   container: me.id + '-adduser-' + id,
                   onclick: { fn: me.addUserToInvites, obj: oRecord, scope: me }
               }
            );
            me.listWidgets[id] = { button: button };
         };

         // DataTable column defintions
         var columnDefinitions = [
         {
            key: "icon32", label: "Preview", sortable: false, formatter: renderCellThumbnail, width: 70
         },
         {
            key: "person", label: "Description", sortable: false, formatter: renderCellDescription
         },
         {
            key: "actions", label: "Actions", sortable: false, formatter: renderCellAddButton, width: 80
         }];

         // DataTable definition
         YAHOO.widget.DataTable.MSG_EMPTY = "";
         this.widgets.dataTable = new YAHOO.widget.DataTable(this.id + "-userslist", columnDefinitions, this.widgets.dataSource,
         {
            renderLoopSize: 32,
            initialLoad: false
         });
      },

      /**
       * BUBBLING LIBRARY EVENT HANDLERS FOR PAGE EVENTS
       * Disconnected event handlers for inter-component event notification
       */

      addUserToInvites: function InviteUsers_addUserToInvites(event, p_obj)
      {
         // send a onAddInvite bubble event
         YAHOO.Bubbling.fire("onAddInvite",
         {
            userName: p_obj.getData('userName'),
            firstName : p_obj.getData('firstName'),
            lastName : p_obj.getData('lastName'),
            email : p_obj.getData('email')
         });
         
         // disable the add button
         this.listWidgets[p_obj.getData('userName')].button.set('disabled', true);
      },

      /**
       * Path Changed event handler
       *
       * @method onInviteUsers
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      searchButtonClick: function InviteUsers_searchButtonClick(e, p_obj)
      {
         // fetch the firstname, lastname nad email
         var searchTermElem = YAHOO.util.Dom.get(this.id + "-search-text");
         var searchTerm = searchTermElem.value;
          
         if (searchTerm.length < 3)
         {
            Alfresco.util.PopupManager.displayMessage({text: this._msg("inviteusers.mintextlength") });
            return;
         }
         
         this._performSearch(searchTerm);
      },

      
      /**
       * Resets the YUI DataTable errors to our custom messages
       * NOTE: Scope could be YAHOO.widget.DataTable, so can't use "this"
       *
       * @method _setDefaultDataTableErrors
       */
      _setDefaultDataTableErrors: function InviteUsers__setDefaultDataTableErrors()
      {
         var msg = Alfresco.util.message;
         YAHOO.widget.DataTable.MSG_EMPTY = msg("message.empty", "Alfresco.InviteUsers");
         YAHOO.widget.DataTable.MSG_ERROR = msg("message.error", "Alfresco.InviteUsers");
      },
      
      /**
       * Updates document list by calling data webscript with current site and path
       *
       * @method _updateDocList
       * @param path {string} Path to navigate to
       */
      _performSearch: function InviteUsers__performSearch(searchTerm)
      {
         // Reset the custom error messages
         this._setDefaultDataTableErrors();
         
         // Display loading message
         //YAHOO.widget.DataTable.MSG_EMPTY = "Searching term '" + searchTerm + "'"; // this._msg("message.loading");
         YAHOO.widget.DataTable.MSG_EMPTY = "";
         
         // empty results table
         this.widgets.dataTable.deleteRows(0, this.widgets.dataTable.getRecordSet().getLength());
         
         function successHandler(sRequest, oResponse, oPayload)
         {
            this._setDefaultDataTableErrors();
            this.widgets.dataTable.onDataReturnInitializeTable.call(this.widgets.dataTable, sRequest, oResponse, oPayload);
         }
         
         function failureHandler(sRequest, oResponse)
         {
            if (oResponse.status == 401)
            {
               // Our session has likely timed-out, so refresh to offer the login page
               window.location.reload();
            }
            else
            {
               try
               {
                  var response = YAHOO.lang.JSON.parse(oResponse.responseText);
                  YAHOO.widget.DataTable.MSG_ERROR = response.message;
                  this.widgets.dataTable.showTableMessage(response.message, YAHOO.widget.DataTable.CLASS_ERROR);
               }
               catch(e)
               {
                  this._setDefaultDataTableErrors();
               }
            }
         }
         
         this.searchTerm = searchTerm;
         this.widgets.dataSource.sendRequest(this._buildSearchParams(searchTerm),
         {
               success: successHandler,
               failure: failureHandler,
               scope: this
         });
      },

      /**
       * Build URI parameter string for doclist JSON data webscript
       *
       * @method _buildDocListParams
       * @param path {string} Path to query
       */
      _buildSearchParams: function InviteUsers__buildSearchParams(searchTerm)
      {
         var params = YAHOO.lang.substitute("filter={searchTerm}",
         {
            searchTerm : encodeURIComponent(searchTerm)
         });

         return params;
      },
      
      /**
       * Gets a custom message
       *
       * @method _msg
       * @param messageId {string} The messageId to retrieve
       * @return {string} The custom message
       * @private
       */
      _msg: function InviteUsers__msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, "Alfresco.InviteUsers", Array.prototype.slice.call(arguments).slice(1));
      }

   };
})();


/**
 * Register a default action handler for a set of elements described by a common class name.
 * The common enclosing tag should hold an id of the form ${htmlid}-methodToCall-param.
 * 
 * @param htmlId the id of the component
 * @param className the classname that is common to all to be handled elements
 * @param ownerTagName the enclosing element's tag name. This element needs to have
 *        an id of type {htmlid}-methodToCall[-param], the param is optional.
 * @param handlerObject the object that handles the actions. Upon action, the methodToCall of this
 *        object is called, passing in the param as specified in the ownerTagName's id.
 */
Alfresco.util.registerDefaultActionHandler = function(htmlId, className, ownerTagName, handlerObject)
{         
   // Hook the tag events
   YAHOO.Bubbling.addDefaultAction(className,
      function genericDefaultAction(layer, args)
      {
         var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, ownerTagName);
         if (owner !== null)
         {
            // check that the html id matches, abort otherwise
            var tmp = owner.id;
            if (tmp.indexOf(htmlId) != 0)
            {
               return true;
            }
            var tmp = tmp.substring(htmlId.length + 1);
            var parts = tmp.split('-');
            if (parts.length < 1)
            {
               // stop here
               return true;
            }
            // the first entry is the handler method to call
            var action = parts[0];
            if (typeof handlerObject[action] == "function")
            {
               // extract the param part of the id
               var param = parts.length > 1 ? tmp.substring(action.length + 1) : null;
               handlerObject[action].call(handlerObject, param);
               args[1].stop = true;
            }
         }
         return true;
      }
   );
}