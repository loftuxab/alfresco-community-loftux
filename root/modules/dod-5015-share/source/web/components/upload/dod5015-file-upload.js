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
 * Records FileUpload component.
 *
 * Checks if Flash is installed or not and uses either the RecordsFlashUpload or
 * RecordsHtmlUpload component.
 *
 * A multi file upload scenario could look like:
 *
 * var fileUpload = Alfresco.getRecordsFileUploadInstance();
 * var multiUploadConfig =
 * {
 *    siteId: siteId,
 *    containerId: doclibContainerId,
 *    path: docLibUploadPath,
 *    filter: [],
 *    mode: fileUpload.MODE_MULTI_UPLOAD,
 * }
 * this.fileUpload.show(multiUploadConfig);
 *
 * If flash is installed it would use the RecordsFlashUpload component in multi upload mode
 * If flash isn't installed it would use the RecordsHtmlUpload in single upload mode instead.
 *
 * @namespace Alfresco.component
 * @class Alfresco.component.RecordsFileUpload
 * @extends Alfresco.FileUpload
 */
(function()
{
   /**
    * RecordsFileUpload constructor.
    *
    * RecordsFileUpload is considered a singleton so constructor should be treated as private,
    * please use Alfresco.getRecordsFileUploadInstance() instead.
    *
    * @param {string} htmlId The HTML id of the parent element
    * @return {Alfresco.RecordsFileUpload} The new RecordsFileUpload instance
    * @constructor
    * @private
    */
   Alfresco.RecordsFileUpload = function(instanceId)
   {
      var instance = Alfresco.util.ComponentManager.get(instanceId);
      if (instance !== null)
      {
         throw new Error("An instance of Alfresco.RecordsFileUpload already exists.");
      }

      Alfresco.RecordsFileUpload.superclass.constructor.call(this, instanceId);
      this.name = "Alfresco.RecordsFileUpload";
      Alfresco.util.ComponentManager.reregister(this);

      return this;
   };

   YAHOO.extend(Alfresco.RecordsFileUpload, Alfresco.FileUpload,
   {
      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       * @override
       */
      onComponentsLoaded: function FU_onComponentsLoaded()
      {
         // Create the appropriate uploader component
         var uploadType = this.hasRequiredFlashPlayer ? "Alfresco.RecordsFlashUpload" : "Alfresco.RecordsHtmlUpload",
            uploadInstance = Alfresco.util.ComponentManager.findFirst(uploadType);
         
         if (uploadInstance)
         {
            this.uploader = uploadInstance;
         }
         else
         {
            throw new Error("No instance of uploader type '" + uploadType + "' exists.");            
         }
      }
   });
})();

Alfresco.getRecordsFileUploadInstance = function()
{
   var instanceId = "alfresco-recordsfileupload-instance";
   return Alfresco.util.ComponentManager.get(instanceId) || new Alfresco.RecordsFileUpload(instanceId);
};