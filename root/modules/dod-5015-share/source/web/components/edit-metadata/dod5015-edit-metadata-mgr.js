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
 * RecordsEditMetadataMgr template.
 * 
 * @namespace Alfresco
 * @class Alfresco.RecordsEditMetadataMgr
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom;
      
   /**
    * RecordsEditMetadataMgr constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.RecordsEditMetadataMgr} The new RecordsEditMetadataMgr instance
    * @constructor
    */
   Alfresco.RecordsEditMetadataMgr = function RecordsEditMetadataMgr_constructor(htmlId)
   {
      Alfresco.RecordsEditMetadataMgr.superclass.constructor.call(this, htmlId);
      this.name = "Alfresco.RecordsEditMetadataMgr";

      return this;
   };

   YAHOO.extend(Alfresco.RecordsEditMetadataMgr, Alfresco.EditMetadataMgr,
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
          * The nodeRef to the node to be used in a backlink for the node being edited.
          * I.e. If a disposition schedule is being edited tha back link will contain the
          * nodeRef to the record category that owns the dispositions schedule,
          * However if it's the record-category that shall be edited the backLinkNodeRef
          * will be the same as nodeRef.
          *
          * @property backLinkNodeRef
          * @type string
          */
         backLinkNodeRef: null
      },

      /**
       * Displays the corresponding details page for the current node
       *
       * @method _navigateForward
       * @private
       */
      _navigateForward: function RecordsEditMetadataMgr__navigateForward()
      {
         /* Did we come from the document library? If so, then direct the user back there */
         if (document.referrer.match(/documentlibrary([?]|$)/))
         {
            // go back to the referrer page
            history.go(-1);
         }
         else
         {
            // go back to the appropriate details page for the node
            var pageUrl = Alfresco.constants.URL_PAGECONTEXT + "site/" + this.options.siteId + 
               "/" + this.options.nodeType + "-details?nodeRef=" + this.options.backLinkNodeRef;

            window.location.href = pageUrl;
         }
      }

   });
   
})();
