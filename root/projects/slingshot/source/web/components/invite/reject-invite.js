/**
 * Copyright (C) 2005-2008 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing
 */
 
/**
 * RejectInvite component.
 * 
 * @namespace Alfresco
 * @class Alfresco.RejectInvite
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom;

   /**
    * RejectInvite constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.RejectInvite} The new RejectInvite instance
    * @constructor
    */
   Alfresco.RejectInvite = function(htmlId)
   {
      return Alfresco.RejectInvite.superclass.constructor.call(this, "Alfresco.RejectInvite", htmlId, ["json"]);
   };
   
   YAHOO.extend(Alfresco.RejectInvite, Alfresco.component.Base,
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
          * Current inviteId.
          * 
          * @property inviteId
          * @type string
          */
         inviteId: "",   
         
         /**
          * Current ticket.
          * 
          * @property ticket
          * @type string
          */
         inviteTicket: ""
      },

      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function RejectInvite_onReady()
      {         
         // Create YUI buttons
         this.widgets.declineButton = Alfresco.util.createYUIButton(this, "decline-button", this.onDeclineClick);
         this.widgets.acceptButton = Alfresco.util.createYUIButton(this, "accept-button", this.onAcceptClick);
      },

      /**
       * Decline button click event handler
       *
       * @method onDeclineClick
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onDeclineClick: function RejectInvite_onDeclineClick(e, p_obj)
      {
         // success handler
         var success = function RejectInvite_onDeclineClick_success(response)
         {
            // show the decline confirmed message
            Dom.addClass(Dom.get(this.id + "-confirm"), "hidden");
            Dom.removeClass(Dom.get(this.id + "-declined"), "hidden");
         };
         
         // construct the url to call
         var url = YAHOO.lang.substitute(window.location.protocol + "//" + window.location.host +
            Alfresco.constants.URL_CONTEXT + "proxy/alfresco-noauth/api/invite/{inviteId}/{inviteTicket}/reject",
            {
               inviteId : this.options.inviteId,
               inviteTicket : this.options.inviteTicket
            });

         // make a backend call to decline the request
         Alfresco.util.Ajax.request(
         {
            method: "PUT",
            url: url,
            responseContentType: "application/json",
            successCallback:
            {
               fn: success,
               scope: this
            },
            failureMessage: this._msg("message.decline.failure")
         });
      },

      /**
       * Accept button click event handler
       *
       * @method onAcceptClick
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onAcceptClick: function RejectInvite_onAcceptClick(e, p_obj)
      {
         // redirect to the accept invite page
         var url = YAHOO.lang.substitute(Alfresco.constants.URL_PAGECONTEXT + "accept-invite" +
            "?inviteId={inviteId}&inviteTicket={inviteTicket}",
            {
               inviteId : this.options.inviteId,
               inviteTicket : this.options.inviteTicket
            });
         window.location = url;
      }
   });
})();
