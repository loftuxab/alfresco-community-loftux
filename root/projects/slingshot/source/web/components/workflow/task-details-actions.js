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
      YAHOO.Bubbling.on("taskData", this.onTaskData, this);

      return this;
   };

   YAHOO.extend(Alfresco.TaskDetailsActions, Alfresco.component.Base,
   {

      /**
       * Event handler called when the "taskData" event is received
       *
       * @method: onTaskData
       */
      onTaskData: function TDA_onMetadataRefresh(layer, args)
      {
         var task = args[1],
            owner = task.owner ? task.owner : {};
         // todo also check against initiator once its in the REST api response.
         if ((task.isPooled && !owner.userName ) || (owner.userName == Alfresco.constants.USERNAME))
         {
            Alfresco.util.createYUIButton(this, "edit", function TDA_onMetadataRefresh_onEditClick()
            {
               window.location.href = Alfresco.util.siteURL("task-edit?taskId=" + encodeURIComponent(task.id));
            });
            Dom.removeClass(Selector.query(".actions", this.id), "hidden");
         }
      }

   });

})();
