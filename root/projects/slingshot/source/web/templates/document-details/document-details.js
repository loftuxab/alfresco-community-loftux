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
 * DocumentDetails template.
 * 
 * @namespace Alfresco
 * @class Alfresco.DocumentDetails
 */
(function()
{
   /**
    * DocumentDetails constructor.
    * 
    * @return {Alfresco.DocumentDetails} The new DocumentDetails instance
    * @constructor
    */
   Alfresco.DocumentDetails = function DocumentDetails_constructor()
   {
      // Load YUI Components
      Alfresco.util.YUILoaderHelper.require(["editor"], this.onComponentsLoaded, this);
            
      return this;
   };
   
   Alfresco.DocumentDetails.prototype =
   {
      /**
       * Object container for initialization options
       *
       * @property options
       * @type object
       */
      options:
      {
         nodeRef: null
      },

      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.Search} returns 'this' for method chaining
       */
      setOptions: function DocumentDetails_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },
      
      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function DocumentDetails_onComponentsLoaded()
      {
         YAHOO.util.Event.onDOMReady(this.onReady, this, true);
      },
   
      /**
       * Fired by YUI when parent element is available for scripting.
       * Template initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function DocumentDetails_onReady()
      {
         var config = {
            method: "GET",
            url: Alfresco.constants.PROXY_URI + '/slingshot/doclib/doclist/documents/node/' + 
                 this.options.nodeRef.replace(":/", "") + '?filter=node',
            successCallback: 
            { 
               fn: this._getDataSuccess, 
               scope: this 
            },
            failureMessage: "Failed to load data for document details"
         };
         Alfresco.util.Ajax.request(config);
         
      },
      
      /**
       * Success handler called when the AJAX call to the doclist web script returns successfully
       *
       * @response The response object
       */
      _getDataSuccess: function DocumentDetails__getDataSuccess(response)
      {
         if (response.json !== undefined)
         {
            var docData = response.json.items[0];
            
            // fire event to inform any listening components that the data is ready
            YAHOO.Bubbling.fire("documentDetailsAvailable", docData);
            
            // fire event to show comments for document
            var eventData = { 
               itemNodeRef: this.options.nodeRef,
               itemTitle: docData.displayName,
               itemName: docData.displayName
            }
            
            YAHOO.Bubbling.fire("setCommentedNode", eventData);
         }
      }
   };
})();
