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
 * Document actions component - DOD5015 extensions.
 * 
 * @namespace Alfresco
 * @class Alfresco.RecordsDocumentActions
 */
(function()
{
   /**
    * RecordsDocumentActions constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.RecordsDocumentActions} The new RecordsDocumentActions instance
    * @constructor
    */
   Alfresco.RecordsDocumentActions = function(htmlId)
   {
      return Alfresco.RecordsDocumentActions.superclass.constructor.call(this, htmlId);
   }
   
   /**
    * Extend from Alfresco.DocumentActions
    */
   YAHOO.extend(Alfresco.RecordsDocumentActions, Alfresco.DocumentActions);
   
   /**
    * Augment prototype with RecordActions module, ensuring overwrite is enabled
    */
   YAHOO.lang.augmentProto(Alfresco.RecordsDocumentActions, Alfresco.doclib.RecordsActions, true);

   /**
    * Augment prototype with main class implementation, ensuring overwrite is enabled
    */
   YAHOO.lang.augmentObject(Alfresco.RecordsDocumentActions.prototype,
   {
      /**
       * The urls to be used when creating links in the action cell
       *
       * @method getActionUrls
       * @override
       * @return {object} Object literal containing URLs to be substituted in action placeholders
       */
      getActionUrls: function RecordsDocumentActions_getActionUrls()
      {
         var urlContextSite = Alfresco.constants.URL_PAGECONTEXT + "site/" + this.options.siteId,
            nodeRef = new Alfresco.util.NodeRef(this.assetData.nodeRef),
            filePlan = this.doclistMetadata.filePlan.replace(":/", "");

         return (
         {
            downloadUrl: Alfresco.constants.PROXY_URI + this.assetData.contentUrl + "?a=true",
            editMetadataUrl: urlContextSite + "/edit-metadata?nodeRef=" + nodeRef.nodeRef,
            transfersZipUrl: Alfresco.constants.PROXY_URI + "api/node/" + filePlan + "/transfers/" + nodeRef.id
         });
      }
   }, true);
})();
