/**
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
 
/**
 * TaskDetails template.
 * 
 * @namespace Alfresco
 * @class Alfresco.TaskDetails
 */
(function()
{
   /**
    * TaskDetails constructor.
    * 
    * @return {Alfresco.TaskDetails} The new TaskDetails instance
    * @constructor
    */
   Alfresco.TaskDetails = function TaskDetails_constructor()
   {
      Alfresco.TaskDetails.superclass.constructor.call(this, null, "Alfresco.TaskDetails", ["button"]);
      return this;
   };
   
   YAHOO.extend(Alfresco.TaskDetails, Alfresco.component.Base,
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
          * taskId of task being viewed
          * 
          * @property taskId
          * @type String
          */
         taskId: null
      },

      /**
       * Fired by YUILoaderHelper when required component script files have been loaded into the browser.
       * NOTE: This component doesn't have an htmlId, so we can't use onContentReady.
       *
       * @override
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function TaskDetails_onComponentsLoaded()
      {
         YAHOO.util.Event.onDOMReady(this.onReady, this, true);
      },

      /**
       * Fired by YUI when parent element is available for scripting.
       * Template initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function TaskDetails_onReady()
      {
         var url = Alfresco.constants.PROXY_URI + 'api/task-instances/' + encodeURIComponent(this.options.taskId) + "?detailed=true";
         Alfresco.util.Ajax.jsonGet(
         {
            method: "GET",
            url: url,
            successCallback: 
            { 
               fn: this._getDataSuccess, 
               scope: this 
            },
            failureCallback: 
            { 
               fn: this._getDataFailure, 
               scope: this 
            }
         });
      },
      
      /**
       * Success handler called when the web script returns successfully.
       * Will fire an event with data used by the components on the page.
       *
       * @method _getDataSuccess
       * @param response {object} The response object
       * @private
       */
      _getDataSuccess: function TaskDetails__getDataSuccess(response)
      {
         if (response.json !== undefined)
         {
            // Fire event with parent metadata
            YAHOO.Bubbling.fire("taskData", response.json.data);
         }
      },

      /**
       * Failure handler called when the web script fails.
       * Will display an error message in a prompt dialog.
       *
       * @method _getDataFailure
       * @param response {object} The response object
       * @private
       */
      _getDataFailure: function TaskDetails__getDataFailure(response)
      {
         Alfresco.util.PopupManager.displayPrompt(
         {
            text: this.msg("message.item-missing"),
            modal: true
         });
      }
   });
})();