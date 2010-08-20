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
 * TaskDetailsActions component.
 *
 * @namespace Alfresco
 * @class Alfresco.TaskDetailsActions
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Selector = YAHOO.util.Selector;

   /**
    * TaskDetailsActions constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.TaskDetailsActions} The new TaskDetailsActions instance
    * @constructor
    */
   Alfresco.TaskDetailsActions = function TDA_constructor(htmlId)
   {
      Alfresco.TaskDetailsActions.superclass.constructor.call(this, "Alfresco.TaskDetailsActions", htmlId, ["button"]);

      /* Decoupled event listeners */
      YAHOO.Bubbling.on("taskDetailedData", this.onTaskDetailsData, this);

      return this;
   };

   YAHOO.extend(Alfresco.TaskDetailsActions, Alfresco.component.Base,
   {
      /**
       * Event handler called when the "taskDetailedData" event is received
       *
       * @method: onTaskDetailsData
       */
      onTaskDetailsData: function TDA_onTaskDetailsData(layer, args)
      {
         var task = args[1];
         if (task.isEditable)
         {
            Alfresco.util.createYUIButton(this, "edit", function TDA_onMetadataRefresh_onEditClick()
            {
               window.location.href = Alfresco.util.siteURL("task-edit?taskId=" + task.id);
            });
            Dom.removeClass(Selector.query(".actions", this.id), "hidden");
         }
      }
   });
})();
