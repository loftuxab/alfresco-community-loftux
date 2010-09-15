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
    * Alfresco Slingshot aliases
    */
    var $html = Alfresco.util.encodeHTML;

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
      YAHOO.Bubbling.on("taskDetailedData", this.onTaskDetailsData, this);

      return this;
   };

   YAHOO.extend(Alfresco.TaskDetailsHeader, Alfresco.component.Base,
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
          * Add referrer to the url if present
          *
          * @property referrer
          * @type String
          */
         referrer: null
      },

      /**
       * Event handler called when the "taskDetailedData" event is received
       *
       * @method: onTaskDetailsData
       */
      onTaskDetailsData: function TDH_onTaskDetailsData(layer, args)
      {
         // Set workflow details url and display link
         var task = args[1],
            taskId = task.id,
            message = task.properties["bpm_description"],
            workflowId = task.workflowInstance.id,
            workflowDetailsUrl = "workflow-details?workflowId=" + workflowId + "&taskId=" + taskId;
         if (this.options.referrer)
         {
            workflowDetailsUrl += "&referrer=" + this.options.referrer;
         }
         if (message && message != task.title)
         {
            message = this.msg("label.message", $html(message), $html(task.title));
         }
         else
         {
            message = this.msg("label.noMessage", $html(task.title));
         }
         Selector.query(".links a", this.id, true).setAttribute("href", Alfresco.util.siteURL(workflowDetailsUrl));
         Dom.removeClass(Selector.query(".links", this.id, true), "hidden");
         Selector.query("h1 span", this.id, true).innerHTML = message;
      }
   });
})();
