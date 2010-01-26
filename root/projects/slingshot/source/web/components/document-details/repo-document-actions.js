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
 * Repository Document actions component.
 * 
 * @namespace Alfresco
 * @class Alfresco.RepositoryDocumentActions
 */
(function()
{
   /**
    * RepositoryDocumentActions constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.RepositoryDocumentActions} The new RepositoryDocumentActions instance
    * @constructor
    */
   Alfresco.RepositoryDocumentActions = function(htmlId)
   {
      return Alfresco.RepositoryDocumentActions.superclass.constructor.call(this, htmlId);
   };

   /**
    * Extend prototype with main class implementation and overrides
    */
   YAHOO.extend(Alfresco.RepositoryDocumentActions, Alfresco.DocumentActions,
   {
      /**
       * The urls to be used when creating links in the action cell
       *
       * @override
       * @method getActionUrls
       * @return {object} Object literal containing URLs to be substituted in action placeholders
       */
      getActionUrls: function RepositoryDocumentActions_getActionUrls()
      {
         var urlContext = Alfresco.constants.URL_PAGECONTEXT + "/",
            nodeRef = this.assetData.nodeRef;

         return (
         {
            downloadUrl: Alfresco.constants.PROXY_URI + this.assetData.contentUrl + "?a=true",
            viewUrl:  Alfresco.constants.PROXY_URI + this.assetData.contentUrl + "\" target=\"_blank",
            documentDetailsUrl: urlContext + "document-details?nodeRef=" + nodeRef,
            editMetadataUrl: urlContext + "edit-metadata?nodeRef=" + nodeRef,
            workingCopyUrl: urlContext + "document-details?nodeRef=" + (this.assetData.custom.workingCopyNode || nodeRef),
            originalUrl: urlContext + "document-details?nodeRef=" + (this.assetData.custom.workingCopyOriginal || nodeRef)
         });
      }
   });
})();