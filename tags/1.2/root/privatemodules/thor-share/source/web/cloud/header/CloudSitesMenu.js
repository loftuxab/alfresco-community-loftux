/**
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
define(["dojo/_base/declare",
   "alfresco/header/AlfSitesMenu"],
   function(declare, AlfSitesMenu) {

      /**
       * This extends "alfresco/header/AlfSitesMenu" and asserts that the create site option only is available when
       * current tenant is the same as the user's home tenant.
       */
      return declare([AlfSitesMenu], {

         /**
          * Will call super class method but afterwards remove the create site option if current tenant doesn't
          * equal the home tenant.
          *
          * @method addUsefulGroup
          * @param {boolean} showAddFavourite Indicates whether or not to display the "Add Favourite" menu item
          * @param {boolean} showRemoveFavourite Indicates whether or not to display the "Remove Favourite" menu item
          */
         addUsefulGroup: function alf_header_AlfSitesMenu__addUsefulGroup(showAddFavourite, showRemoveFavourite) {
            this.inherited(arguments);
            if (this.params.removeCreateSiteMenuItem)
            {
               this.usefulGroup.removeChild(this.createSite);
            }
         }
      });
   }
);
