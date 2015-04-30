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
   "alfresco/core/Core",
   "dojo/_base/lang",
   "cloud/components/console/create-users",
   "cloud/components/account/invite"],
   function(declare, AlfCore, lang) {

      return declare([AlfCore], {

         cssRequirements: [
            {cssFile:"/cloud/components/console/create-users.css", mediaType:"screen"},
            {cssFile:"/cloud/components/account/invite.css", mediaType:"screen"}
         ],

         /**
          * Sets up the subscriptions for the NavigationService
          *
          * @constructor
          * @param {array} args Constructor arguments
          */
         constructor: function cloud_services_InvitationService__constructor(args) {
            this.alfSubscribe("CLOUD_INVITE_PEOPLE", lang.hitch(this, "invitePeople"));
            this.alfSubscribe("CLOUD_SITE_INVITE", lang.hitch(this, "siteInvite"));
         },

         /**
          * Displays the Cloud invite dialog.
          *
          * @method invitePeople
          */
         invitePeople: function cloud_services_InvitationService__invitePeople(config) {
            Alfresco.util.loadWebscript({
               url: Alfresco.constants.URL_SERVICECONTEXT + "cloud/core/components/console/create-users",
               properties:
               {
                  source: "header-invite-button"
               }
            });
         },

         /**
          * Displays the cloud site invite dialog
          *
          * @method siteInvite
          * @param config {Object} Contains the site to invite to
          */
         siteInvite: function cloud_services_InvitationService__siteInvite(config) {
            Alfresco.util.loadWebscript({
               url: Alfresco.constants.URL_SERVICECONTEXT + "cloud/core/components/account/invite",
               properties:
               {
                  site: config.site
               }
            });
         }
      });
});