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
 * FileUpload component.
 *
 * Checks if Flash is installed or not and uses either the FlashUpload or
 * HtmlUpload component.
 *
 * A multi file upload scenario could look like:
 *
 * var fileUpload = Alfresco.module.getFileUploadInstance();
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
 * If flash is installed it would use the FlashUpload component in multi upload mode
 * If flash isn't installed it would use the HtmlUpload in single upload mode instead.
 *
 * @namespace Alfresco.module
 * @class Alfresco.module.FileUpload
 */
(function()
{

   /**
    * FileUpload constructor.
    *
    * FileUpload is considered a singleton so constructor should be treated as private,
    * please use Alfresco.module.getFileUploadInstance() instead.
    *
    * @param {string} htmlId The HTML id of the parent element
    * @return {Alfresco.module.FileUpload} The new FileUpload instance
    * @constructor
    * @private
    */
   Alfresco.module.FileUpload = function(containerId)
   {
      this.name = "Alfresco.module.FileUpload";
      this.id = containerId;

      var instance = Alfresco.util.ComponentManager.find({id: this.id});
      if (instance !== undefined && instance.length > 0)
      {
         throw new Error("An instance of Alfresco.module.FileUpload already exists.");
      }

      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      // Create the appropriate uploader component
      this.hasRequiredFlashPlayer = Alfresco.util.hasRequiredFlashPlayer(9, 0, 45);
      if(this.hasRequiredFlashPlayer)        
      {
         this.uploader = Alfresco.module.getFlashUploadInstance();
      }
      else
      {
         this.uploader = Alfresco.module.getHtmlUploadInstance();
      }

      return this;
   }

   Alfresco.module.FileUpload.prototype =
   {

      /**
       * The uploader instance
       *
       * @property uploader
       * @type Alfresco.module.FlashUpload or Alfresco.module.HtmlUpload
       */
      uploader: null,

      /**
       * Shows uploader in single upload mode.
       *
       * @property MODE_SINGLE_UPLOAD
       * @static
       * @type int
       */
      MODE_SINGLE_UPLOAD: 1,

      /**
       * Shows uploader in single update mode.
       *
       * @property MODE_SINGLE_UPDATE
       * @static
       * @type int
       */
      MODE_SINGLE_UPDATE: 2,

      /**
       * Shows uploader in multi upload mode.
       *
       * @property MODE_MULTI_UPLOAD
       * @static
       * @type int
       */
      MODE_MULTI_UPLOAD: 3,

      /**
       * The default config for the gui state for the uploader.
       * The user can override these properties in the show() method to use the
       * uploader for both single & multi uploads and single updates.
       *
       * @property defaultShowConfig
       * @type object
       */
      defaultShowConfig:
      {
         siteId: null,
         containerId: null,
         uploadDirectory: null,
         updateNodeRef: null,
         updateFilename: null,
         mode: this.MODE_SINGLE_UPLOAD,
         filter: [],
         onFileUploadComplete: null,
         overwrite: true,
         thumbnails: null,
         htmlUploadURL: null,
         flashUploadURL: null,
         username: null
      },

      /**
       * The merged result of the defaultShowConfig and the config passed in
       * to the show method.
       *
       * @property defaultShowConfig
       * @type object
       */
      showConfig: {},

      /**
       * Show can be called multiple times and will display the uploader dialog
       * in different ways depending on the config parameter.
       *
       * @method show
       * @param config {object} describes how the upload dialog should be displayed
       * The config object is in the form of:
       * {
       *    siteId: {string},        // site to upload file(s) to
       *    containerId: {string},   // container to upload file(s) to (i.e. a doclib id)
       *    uploadPath: {string},    // directory path inside the component to where the uploaded file(s) should be save
       *    updateNodeRef: {string}, // nodeRef to the document that should be updated
       *    updateFilename: {string},// The name of the file that should be updated, used to display the tip
       *    mode: {int},             // MODE_SINGLE_UPLOAD, MODE_MULTI_UPLOAD or MODE_SINGLE_UPDATE
       *    filter: {array},         // limits what kind of files the user can select in the OS file selector
       *    onFileUploadComplete: null, // Callback after upload
       *    overwrite: true          // If true and in mode MODE_XXX_UPLOAD it tells
       *                             // the backend to overwrite a versionable file with the existing name
       *                             // If false and in mode MODE_XXX_UPLOAD it tells
       *                             // the backend to append a number to the versionable filename to avoid
       *                             // an overwrite and a new version
       *    htmlUploadURL: null,     // Overrides default url to post the file to if the html version is used
       *    flashUploadURL: null,    // Overrides default url to post the files to if the flash version is used
       *    username: null           // If a file should be associated with a user
       * }
       */
      show: function FU_show(config)
      {

         // Merge the supplied config with default config and check mandatory properties
         this.showConfig = YAHOO.lang.merge(this.defaultShowConfig, config);

         // If flash isn't installed multi upload mode isn't supported
         if(!this.hasRequiredFlashPlayer && this.showConfig.mode == this.MODE_MULTI_UPLOAD)
         {
            this.showConfig.mode = this.MODE_SINGLE_UPLOAD;
         }

         if(this.hasRequiredFlashPlayer)
         {
            this.showConfig.uploadURL = this.showConfig.flashUploadURL;
         }
         else
         {
            this.showConfig.uploadURL = this.showConfig.htmlUploadURL;
         }

         // Let the uploader instance show itself
         this.uploader.show(this.showConfig);
      }
   }

})();


Alfresco.module.getFileUploadInstance = function()
{
   var instanceId = "alfresco-fileupload-instance";
   var instance = Alfresco.util.ComponentManager.find({id: instanceId});
   if (instance !== undefined && instance.length > 0)
   {
      instance = instance[0];
   }
   else
   {
      instance = new Alfresco.module.FileUpload(instanceId);
   }
   return instance;
}

/* Create the instance to load optional YUI components and SWF early */
Alfresco.module.getFileUploadInstance();
