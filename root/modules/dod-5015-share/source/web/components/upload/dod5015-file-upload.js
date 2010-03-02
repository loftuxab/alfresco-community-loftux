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
       * Shows uploader in single import mode.
       *
       * @property MODE_SINGLE_IMPORT
       * @static
       * @type int
       */
      MODE_SINGLE_IMPORT: 4,


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
         overwrite: false,
         thumbnails: null,
         htmlUploadURL: null,
         flashUploadURL: null,
         username: null,
         importDestination: null,
         htmlImportURL: null,
         flashImportURL: null
      },

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
      },

      /**
       * Show can be called multiple times and will display the uploader dialog
       * in different ways depending on the config parameter.
       *
       * @method show
       * @param config {object} describes how the upload dialog should be displayed
       * The config may contain all attributes that the super class (Alfresco.FileUpload)
       * accepts in its show method, and also the following:
       * {
       *    siteId: {string},        // site to upload file(s) to
       *    containerId: {string},   // container to upload file(s) to (i.e. a doclib id)
       *    uploadPath: {string},    // directory path inside the component to where the uploaded file(s) should be save
       *    updateNodeRef: {string}, // nodeRef to the document that should be updated
       *    updateFilename: {string},// The name of the file that should be updated, used to display the tip
       *    mode: {int},             // MODE_SINGLE_UPLOAD, MODE_MULTI_UPLOAD or MODE_SINGLE_UPDATE
       *    filter: {array},         // limits what kind of files the user can select in the OS file selector
       *    onFileUploadComplete: null, // Callback after upload
       *    overwrite: false         // If true and in mode MODE_XXX_UPLOAD it tells
       *                             // the backend to overwrite a versionable file with the existing name
       *                             // If false and in mode MODE_XXX_UPLOAD it tells
       *                             // the backend to append a number to the versionable filename to avoid
       *                             // an overwrite and a new version
       *    htmlUploadURL: null,     // Overrides default url to post the file to if the html version is used
       *    flashUploadURL: null,    // Overrides default url to post the files to if the flash version is used
       *    username: null           // If a file should be associated with a user
       *    importDestination: {string},   // nodeRef to the filePlan to which the acp file shall be imported to
       *    htmlImportURL: null,     // Overrides default url to import the file to if the html version is used
       *    flashImportURL: null     // Overrides default url to import the files to if the flash version is used
       * }
       */
      show: function FU_show(config)
      {
         // Check which import url to use
         if (this.hasRequiredFlashPlayer)
         {
            config.importURL = this.showConfig.flashImportURL;
         }
         else
         {
            config.importURL = this.showConfig.htmlImportURL;
         }

         // Let superclass do the actual show
         Alfresco.RecordsFileUpload.superclass.show.call(this, config);
      }
   });
})();

Alfresco.getRecordsFileUploadInstance = function()
{
   var instanceId = "alfresco-recordsfileupload-instance";
   return Alfresco.util.ComponentManager.get(instanceId) || new Alfresco.RecordsFileUpload(instanceId);
};