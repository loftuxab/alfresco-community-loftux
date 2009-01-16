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
 * CollaborationTitle component
 *
 * The title component of a collaboration site
 *
 * @namespace Alfresco
 * @class Alfresco.CollaborationTitle
 */
(function()
{

   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event;

   /**
    * CollaborationTitle constructor.
    *
    * @param htmlId {string} A unique id for this component
    * @return {Alfresco.CollaborationTitle} The new DocumentList instance
    * @constructor
    */
   Alfresco.CollaborationTitle = function(containerId)
   {
      this.name = "Alfresco.CollaborationTitle";
      this.id = containerId;
      this.widgets = {};

      // Register this component
      Alfresco.util.ComponentManager.register(this);

      // Load YUI Components
      Alfresco.util.YUILoaderHelper.require(["event"], this.onComponentsLoaded, this);

      return this;
   };

   Alfresco.CollaborationTitle.prototype =
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
          * The current user
          *
          * @property user
          * @type string
          */
         user: null,

         /**
          * The current site
          *
          * @property site
          * @type string
          */
         site: null
      },

      /**
       * Holds references to ui widgets
       *
       * @property widgets
       * @type object
       */
      widgets: null,

      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.CollaborationTitle} returns 'this' for method chaining
       */
      setOptions: function DL_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },

      /**
       * Set messages for this module.
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {Alfresco.CollaborationTitle} returns 'this' for method chaining
       */
      setMessages: function CollaborationTitle_setMessages(obj)
      {
         Alfresco.util.addMessages(obj, this.name);
         return this;
      },


      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function CollaborationTitle_onComponentsLoaded()
      {
         Event.onContentReady(this.id, this.onReady, this, true);
      },

      /**
       * Fired by YUI when parent element is available for scripting.
       * Initial History Manager event registration
       *
       * @method onReady
       */
      onReady: function CollaborationTitle_onReady()
      {
         // Add event listeners for join link if present
         var joinLink = document.getElementById(this.id + "-join-link");
         if(joinLink)
         {
            Event.addListener(joinLink, "click",
                    function (event, obj)
                    {
                       // Call join site from a scope wokring in all browsers where we have all info
                       obj.thisComponent.joinSite(obj.thisComponent.options.user, obj.thisComponent.options.site);
                    },
            {
               thisComponent: this
            });
         }

         // Add event listeners for leave link if present
         var leaveLink = document.getElementById(this.id + "-leave-link");
         if(leaveLink)
         {
            Event.addListener(leaveLink, "click",
                    function (event, obj)
                    {
                       // Call leave site from a scope wokring in all browsers where we have all info
                       obj.thisComponent.leaveSite(obj.thisComponent.options.user, obj.thisComponent.options.site);
                    },
            {
               thisComponent: this
            });
         }
      },


      /**
       * Called when the user clicks on the join site button
       *
       * @method joinSite
       * @param user {string} The user to join to the site
       * @param site {string} The site to join the user to
       */
      joinSite: function CollaborationTitle_joinSite(user, site)
      {
         user = encodeURIComponent(user);

         // make ajax call to site service to join current user
         Alfresco.util.Ajax.jsonRequest(
         {
            url: Alfresco.constants.PROXY_URI + "api/sites/" + site + "/memberships/" + user,
            method: "PUT",
            dataObj:
            {
               role: "SiteConsumer",
               person:
               {
                  userName: user
               }
            },
            successCallback:
            {
               fn: this._joinSiteSuccess,
               scope: this
            },
            failureCallback:
            {
               fn: this._joinSiteFailure,
               scope: this
            }
         });

         // Let the user know something is happening
         this.widgets.feedbackMessage = Alfresco.util.PopupManager.displayMessage(
         {
            text: Alfresco.util.message("message.joining", this.name, user, site),
            spanClass: "wait",
            displayTime: 0
         });
      },

      /**
       * Callback handler used when the current user successfully has joined the current site
       *
       * @method _joinSiteSuccess
       * @param response {object}
       */
      _joinSiteSuccess: function CollaborationTitle__joinSiteSuccess(response)
      {
         // Reload page to make sure all new actions on the current page are available to the user
         document.location.reload();
      },

      /**
       * Callback handler used when adding user from site failed
       *
       * @method _joinSiteFailure
       * @param response {object}
       */
      _joinSiteFailure: function CollaborationTitle__joinSiteFailure(response)
      {
         // Hide the feedback message
         this.widgets.feedbackMessage.destroy();
         
         // Display error message
         Alfresco.util.PopupManager.displayPrompt(
         {
            text: Alfresco.util.message("message.join-failure", this.name, encodeURIComponent(this.options.user), this.options.site)
         });
      },

      /**
       * Called when the user clicks on the leave site button
       *
       * @method leaveSite
       * @param user {string} The user to join to the site
       * @param site {string} The site to join the user to
       */
      leaveSite: function CollaborationTitle_leaveSite(user, site)
      {
         user = encodeURIComponent(user);

         // make ajax call to site service to join user
         Alfresco.util.Ajax.request(
         {
            url: Alfresco.constants.PROXY_URI + "api/sites/" + site + "/memberships/" + user,
            method: "DELETE",
            successCallback:
            {
               fn: this._leaveSiteSuccess,
               scope: this
            },
            failureCallback:
            {
               fn: this._leaveSiteFailure,               
               scope: this
            }
         });

         // Let the user know something is happening
         this.widgets.feedbackMessage = Alfresco.util.PopupManager.displayMessage(
         {
            text: Alfresco.util.message("message.leaving", this.name, user, site),
            spanClass: "wait",
            displayTime: 0
         });
      },

      /**
       * Callback handler used when the current user successfully has left the current site
       *
       * @method _leaveSiteSuccess
       * @param response {object}
       */
      _leaveSiteSuccess: function CollaborationTitle__leaveSiteSuccess(response)
      {
         // Hide the feedback message
         this.widgets.feedbackMessage.destroy();

         // Reload user dashboard as they are no longer a member of this site
         document.location.href = Alfresco.constants.URL_PAGECONTEXT + "user/" + this.options.user + "/dashboard";
      },

      /**
       * Callback handler used when removing user from site failed
       *
       * @method _leaveSiteFailure
       * @param response {object}
       */
      _leaveSiteFailure: function CollaborationTitle__leaveSiteFailure(response)
      {
         // Hide the feedback message
         this.widgets.feedbackMessage.destroy();

         // Display error message
         Alfresco.util.PopupManager.displayPrompt(
         {
            text: Alfresco.util.message("message.leave-failure", this.name, encodeURIComponent(this.options.user), this.options.site)
         });

      }

   };
})();
