/**
 * Copyright (C) 2005-2009 Alfresco Software Limited.
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
 * Dashboard Records Management component.
 * 
 * @namespace Alfresco
 * @class Alfresco.dashlet.RMA
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
       Event = YAHOO.util.Event;
   
   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML;
   
   /**
    * Dashboard RMA constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.dashlet.RMA} The new component instance
    * @constructor
    */
   Alfresco.dashlet.RMA = function RMA_constructor(htmlId)
   {
      Alfresco.dashlet.RMA.superclass.constructor.call(this, "Alfresco.dashlet.RMA", htmlId, ["container"]);

      return this;
   };
   
   YAHOO.extend(Alfresco.dashlet.RMA, Alfresco.component.Base,
   {
      /**
       * Fired by YUI when parent element is available for scripting
       * @method onReady
       */
      onReady: function RMA_onReady()
      {
         var me = this;
         
         this.widgets.feedbackMessage = null;
         
         // setup link events
         Event.on(this.id + "-create-site-link", "click", this.onCreateSite, null, this);
         Event.on(this.id + "-load-data-link", "click", this.onLoadTestData, null, this);
         Event.on(this.id + "-role-report-link", "click", this.onUserRoleReport, null, this);
      },
      
      /**
       * Create Site link click event handler
       * 
       * @method onCreateSite
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onCreateSite: function RMA_onCreateSite(e, args)
      {
         if (this.widgets.feedbackMessage === null)
         {
            this.widgets.feedbackMessage = Alfresco.util.PopupManager.displayMessage(
            {
               text: this.msg("message.creating"),
               spanClass: "wait",
               displayTime: 0
            });
            
            // call web-tier to perform site creation
            Alfresco.util.Ajax.request(
            {
               method: Alfresco.util.Ajax.GET,
               url: Alfresco.constants.URL_SERVICECONTEXT + "utils/create-rmsite?shortname=rm",
               successCallback:
               {
                  fn: function()
                  {
                     this.widgets.feedbackMessage.destroy();
                     Alfresco.util.PopupManager.displayMessage(
                     {
                        text: this.msg("message.create-ok")
                     });
                     
                     // refresh UI appropriately
                     Dom.setStyle(this.id + "-create-site", "display", "none");
                     Dom.setStyle(this.id + "-display-site", "display", "block");
                     Dom.setStyle(this.id + "-load-data", "display", "block");
                     Alfresco.util.Anim.pulse(this.id + "-display-site");
                     
                     // reset feedback message - to allow another action if required
                     this.widgets.feedbackMessage = null;
                  },
                  scope: this
               },
               failureCallback:
               {
                  fn: function()
                  {
                     this.widgets.feedbackMessage.destroy();
                     Alfresco.util.PopupManager.displayMessage(
                     {
                        text: this.msg("message.create-fail")
                     });
                     
                     // reset feedback message - to allow another action if required
                     this.widgets.feedbackMessage = null;
                  },
                  scope: this
               }
            });
         }
      },
      
      /**
       * Load Test Data link click event handler
       * 
       * @method onLoadTestData
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onLoadTestData: function RMA_onLoadTestData(e, args)
      {
         if (this.widgets.feedbackMessage === null)
         {
            this.widgets.feedbackMessage = Alfresco.util.PopupManager.displayMessage(
            {
               text: this.msg("message.importing"),
               spanClass: "wait",
               displayTime: 0
            });
            
            // call repo-tier to perform test data import
            Alfresco.util.Ajax.request(
            {
               method: Alfresco.util.Ajax.GET,
               url: Alfresco.constants.PROXY_URI + "api/rma/bootstraptestdata?site=rm",
               successCallback:
               {
                  fn: function()
                  {
                     this.widgets.feedbackMessage.destroy();
                     Alfresco.util.PopupManager.displayMessage(
                     {
                        text: this.msg("message.import-ok")
                     });
                     
                     // reset feedback message - to allow another action if required
                     this.widgets.feedbackMessage = null;
                  },
                  scope: this
               },
               failureCallback:
               {
                  fn: function()
                  {
                     this.widgets.feedbackMessage.destroy();
                     Alfresco.util.PopupManager.displayMessage(
                     {
                        text: this.msg("message.import-fail")
                     });
                     
                     // reset feedback message - to allow another action if required
                     this.widgets.feedbackMessage = null;
                  },
                  scope: this
               }
            });
         }
      },
      
      /**
       * User Role Report link click event handler
       * 
       * @method onUserRoleReport
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onUserRoleReport: function RMA_onUserRoleReport(e, args)
      {
      }
   });
})();