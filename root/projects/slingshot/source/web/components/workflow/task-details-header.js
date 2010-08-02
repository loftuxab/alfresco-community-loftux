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
 * TaskDetailsHeader component.
 *
 * @namespace Alfresco
 * @class Alfresco.TaskDetailsHeader
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Selector = YAHOO.util.Selector;

   /**
    * TaskDetailsHeader constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.TaskDetailsHeader} The new TaskDetailsHeader instance
    * @constructor
    */
   Alfresco.TaskDetailsHeader = function TDH_constructor(htmlId)
   {
      Alfresco.TaskDetailsHeader.superclass.constructor.call(this, "Alfresco.TaskDetailsHeader", htmlId, ["button"]);

      /* Decoupled event listeners */
      YAHOO.Bubbling.on("taskData", this.onTaskData, this);

      return this;
   };

   YAHOO.extend(Alfresco.TaskDetailsHeader, Alfresco.component.Base,
   {

      /**
       * Event handler called when the "taskData" event is received
       *
       * @method: onTaskData
       */
      onTaskData: function TDH_onMetadataRefresh(layer, args)
      {
         // Set workflow details url and display link
         var workflowId = args[1].workflowInstance.id,
            workflowDetailsUrl = Alfresco.util.siteURL("workflow-details?workflowId=" + encodeURIComponent(workflowId));
         Selector.query(".links a", this.id)[0].setAttribute("href", workflowDetailsUrl);
         Dom.removeClass(Selector.query(".links", this.id), "hidden");
      }

   });

})();
