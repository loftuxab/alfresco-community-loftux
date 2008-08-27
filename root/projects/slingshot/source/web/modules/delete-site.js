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
 * DeleteSite component.
 *
 * Displays a dialog with cofirmation to delete a site.
 * 
 * @namespace Alfresco.module
 * @class Alfresco.module.DeleteSite
 */
(function()
{

   /**
    * DeleteSite constructor.
    *
    * DeleteSite is considered a singleton so constructor should be treated as private,
    * please use Alfresco.module.getDeleteSiteInstance() instead.
    *
    * @param {string} htmlId The HTML id of the parent element
    * @return {Alfresco.module.DeleteSite} The new DeleteSite instance
    * @constructor
    * @private
    */
   Alfresco.module.DeleteSite = function(containerId)
   {
      this.name = "Alfresco.module.DeleteSite";
      this.id = containerId;

      var instance = Alfresco.util.ComponentManager.find({id: this.id});
      if (instance !== undefined && instance.length > 0)
      {
         throw new Error("An instance of Alfresco.module.DeleteSite already exists.");
      }

      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      // Load YUI Components
      Alfresco.util.YUILoaderHelper.require(["button", "container", "connection", "selector", "json", "event"], this.onComponentsLoaded, this);

      return this;
   }

   Alfresco.module.DeleteSite.prototype =
   {

      localized: false,

      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function CS_onComponentsLoaded()
      {
      },

      /**
       * Shows the dialog
       * in different ways depending on the config parameter.
       *
       * @method show
       * @param config {object} describes how the upload dialog should be displayed
       * The config object is in the form of:
       * {
       *    site:
       *    {
       *       shortName: {string},    // shortName of site to delete
       *       title: {string}      // Name of site to delete
       *    }
       * }
       */
      show: function FU_show(config)
      {
         var c = config;
         if(this.localized)
         {
             this._showDialog(c);
         }
         else
         {
            Alfresco.util.Ajax.request(
            {
               url: Alfresco.constants.URL_SERVICECONTEXT + "modules/delete-site",
               dataObj:{ htmlid: this.id },
               successCallback:
               {
                  fn: function()
                  {
                     this.onMessagesLoaded(c);
                  },
                  scope: this
               },
               execScripts: true,
               failureMessage: "Could not load delete site messages"
            });
         }

      },


      /**
       * Called when the DeleteSite messages has been returned from the server.
       * Shows the dialog.
       *
       * @method onMessagesLoaded
       * @param config {object} The config for the dialog
       */
      onMessagesLoaded: function DS_onMessagesLoaded(config)
      {
         this._showDialog(config);
      },

      _showDialog: function DS__showDialog(config)
      {
         var me = this;
         var c = config;
         Alfresco.util.PopupManager.displayPrompt(
         {
            title: Alfresco.util.message("title.deleteSite", this.name),
            text: Alfresco.util.message("label.deleteSite", this.name, {"0": c.site.title}),
            noEscape: true,
            buttons: [
               {
                  text: Alfresco.util.message("button.ok", this.name),
                  handler: function DS_delete()
                  {
                     this.destroy();
                     me._onDeleteClick.call(me, c);
                  },
                  isDefault: true
               },
               {
                  text: Alfresco.util.message("button.cancel", this.name),
                  handler: function DL_cancel()
                  {
                     this.destroy();
                  }
               }]
         });
      },
      _onDeleteClick: function(config)
      {
         var me = this;
         var c = config;
         Alfresco.util.PopupManager.displayPrompt(
         {
            title: Alfresco.util.message("title.deleteSite", this.name),
            text: Alfresco.util.message("label.confirmDeleteSite", this.name, {"0": c.site.title}),
            noEscape: true,            
            buttons: [
               {
                  text: Alfresco.util.message("button.ok", this.name),
                  handler: function DS_delete()
                  {
                     this.destroy();
                     me._onConfirmedDeleteClick.call(me, c);
                  },
                  isDefault: true
               },
               {
                  text: Alfresco.util.message("button.cancel", this.name),
                  handler: function DL_cancel()
                  {
                     this.destroy();
                  }
               }]
         });
      },

      _onConfirmedDeleteClick: function(config)
      {
         var c = config;
         var feedbackMessage = Alfresco.util.PopupManager.displayMessage(
         {
            text: Alfresco.util.message("message.deletingSite", this.name),
            spanClass: "wait",
            displayTime: 0
         });

         Alfresco.util.Ajax.request(
         {
            url: Alfresco.constants.PROXY_URI + "api/sites/" + config.site.shortName,
            method: Alfresco.util.Ajax.DELETE,
            successCallback:
            {
               fn: function(response)
               {
                  feedbackMessage.destroy();
                  if(response.json && response.json.success)
                  {
                     Alfresco.util.PopupManager.displayMessage({
                        text: Alfresco.util.message("message.siteDeleted", this.name)
                     });

                     // Tell other components that the site has been deleted
                     YAHOO.Bubbling.fire("siteDeleted",
                     {
                        site: c.site
                     });
                  }
                  else
                  {
                     Alfresco.util.PopupManager.displayMessage({
                        text: Alfresco.util.message("message.deleteFailed", this.name)
                     });
                  }
               },
               scope: this
            },
            failureCallback:
            {
               fn: function(response)
               {
                  feedbackMessage.destroy();
                  Alfresco.util.PopupManager.displayMessage({
                     text: Alfresco.util.message("message.deleteFailed", this.name)
                  });
               },
               scope: this
            }
         });
      }

   }

})();


Alfresco.module.getDeleteSiteInstance = function()
{
   var instanceId = "alfresco-deletesite-instance";
   var instance = Alfresco.util.ComponentManager.find({id: instanceId});
   if (instance !== undefined && instance.length > 0)
   {
      instance = instance[0];
   }
   else
   {
      instance = new Alfresco.module.DeleteSite(instanceId);
   }
   return instance;
}
