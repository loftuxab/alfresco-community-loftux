/**
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
 * PeopleFinder - Cloud extension component.
 * 
 * @namespace Alfresco
 * @class Alfresco.PeopleFinder
 */
(function()
{
   /**
    * Override component implementation
    */
   Alfresco.CloudPeopleFinder = function(htmlId)
   {
      Alfresco.CloudPeopleFinder.superclass.constructor.call(this, htmlId);
      return this;
   };
   
   YAHOO.lang.extend(Alfresco.CloudPeopleFinder, Alfresco.PeopleFinder, 
   {
      /**
       * Extend prototype _renderFollowingActions() function to disable Following functionality
       * depending on user tenant context.
       * 
       * @method _renderFollowingActions
       * @param oRecord {object} datagrid record to rendering Following actions for
       */
      _renderFollowingActions: function Cloud_renderFollowingActions(oRecord)
      {
         // test with original function definition
         var allow = Alfresco.CloudPeopleFinder.superclass._renderFollowingActions.call(this, oRecord);
         
         // test that current and visible user have home tenants (as Public users do not)
         // and that they have the same home tenant to allow the Following actions to display
         var homeTenant = oRecord.getData("homeTenant");
         return (allow && homeTenant && this.options.userHomeTenant && homeTenant == this.options.userHomeTenant);
      }
   });
   
})();