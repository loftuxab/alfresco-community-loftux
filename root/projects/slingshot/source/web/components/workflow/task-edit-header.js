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
 * TaskEditHeader component.
 *
 * @namespace Alfresco
 * @class Alfresco.TaskEditHeader
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      Selector = YAHOO.util.Selector;

   /**
    * Alfresco Slingshot aliases
    */
    var $html = Alfresco.util.encodeHTML;

   /**
    * TaskEditHeader constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.TaskEditHeader} The new TaskEditHeader instance
    * @constructor
    */
   Alfresco.TaskEditHeader = function TaskEditHeader_constructor(htmlId)
   {
      Alfresco.TaskEditHeader.superclass.constructor.call(this, htmlId, ["button"]);

      // Re-register with our own name
      this.name = "Alfresco.TaskEditHeader";
      Alfresco.util.ComponentManager.reregister(this);

      /* Decoupled event listeners */
      YAHOO.Bubbling.on("taskDetailedData", this.onTaskDetailedData, this);

      return this;
   };

   YAHOO.extend(Alfresco.TaskEditHeader, Alfresco.FormManager,
   {
      /**
       * Event handler called when the "taskDetailedData" event is received
       *
       * @method: onTaskDetailedData
       */
      onTaskDetailedData: function TEH_onTaskDetailedData(layer, args)
      {
         var task = args[1];
         Selector.query("h1 span", this.id, true).innerHTML = $html(task.title);
         if (task && task.owner && task.owner.userName)
         {
            // Task is assigned
            if (task.owner && task.owner.userName == Alfresco.constants.USERNAME)
            {
               Alfresco.util.createYUIButton(this, "reassign", function TEH_onMetadataRefresh_onReassignClick()
               {
                  alert('Not implemented');
               });
               Alfresco.util.createYUIButton(this, "release", function TEH_onMetadataRefresh_onReleaseClick()
               {
                  alert('Not implemented');
               });
               Dom.removeClass(Selector.query(".actions .assigned", this.id), "hidden");               
            }
         }
         else
         {
            // Task is unassigned
            Alfresco.util.createYUIButton(this, "claim", function TEH_onMetadataRefresh_onClaimClick()
            {
               alert('Not implemented');
            });
            Alfresco.util.createYUIButton(this, "assign", function TEH_onMetadataRefresh_onAssignClick()
            {
               alert('Not implemented');
            });
            Dom.removeClass(Selector.query(".actions .unassigned", this.id), "hidden");
            Dom.removeClass(Selector.query(".info.unassigned", this.id), "hidden");
         }
      }
   });
})();
