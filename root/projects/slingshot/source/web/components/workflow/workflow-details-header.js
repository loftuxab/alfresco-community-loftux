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
 * WorkflowDetailsHeader component.
 *
 * @namespace Alfresco
 * @class Alfresco.WorkflowDetailsHeader
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
    * WorkflowDetailsHeader constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.WorkflowDetailsHeader} The new WorkflowDetailsHeader instance
    * @constructor
    */
   Alfresco.WorkflowDetailsHeader = function WorkflowDetailsHeader_constructor(htmlId)
   {
      Alfresco.WorkflowDetailsHeader.superclass.constructor.call(this, "Alfresco.WorkflowDetailsHeader", htmlId, ["button", "container", "datasource", "datatable"]);

      /* Decoupled event listeners */
      YAHOO.Bubbling.on("workflowDetailedData", this.onWorkflowDetailedData, this);

      return this;
   };

   YAHOO.extend(Alfresco.WorkflowDetailsHeader, Alfresco.component.Base,
   {

      /**
       * Event handler called when the "onWorkflowDetailedData" event is received
       *
       * @method: onWorkflowDetailedData
       */
      onWorkflowDetailedData: function TDH_onWorkflowDetailedData(layer, args)
      {
         // Display workflow description
         Selector.query("h1 span", this.id, true).innerHTML = $html(args[1].title);
      }

   });

})();
