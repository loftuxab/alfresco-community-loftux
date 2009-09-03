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
 * RM_ObjectRenderer component
 * 
 * Overrides certain methods so RM doc picker can display RM icons
 * 
 * @namespace Alfresco
 * @class Alfresco.RM_ObjectRenderer
 */
(function RM_ObjectRenderer()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
       Event = YAHOO.util.Event;

   /**
    * RM_ObjectRenderer componentconstructor.
    * 
    * @param {object} Instance of the DocumentPicker
    * @return {Alfresco.module.ObjectRenderer} The new ObjectRenderer instance
    * @constructor
    */
   Alfresco.module.RM_ObjectRenderer = function RM_ObjectRenderer_constructor(DocumentPicker)
   {
      Alfresco.module.RM_ObjectRenderer.superclass.constructor.call(this,DocumentPicker);

      return this;
   };
    
   YAHOO.extend(Alfresco.module.RM_ObjectRenderer, Alfresco.module.ObjectRenderer,
   {
/**
    * Generate item icon URL - displays RM icons depending on type
    *
    * @method getIconURL
    * @param item {object} Item object literal
    * @param size {number} Icon size (16, 32)
    */
   getIconURL : function RM_ObjectRenderer_getIconURL(item, size)
   {
      var types = item.type.split(':');
      if (types[0] !== 'rma' && types[0] !== 'dod')
      {
         return Alfresco.module.RM_ObjectRenderer.superclass.getIconURL.call(this, item, size);
      }
      else
      {
         var type = "";
         switch (types[1])
         {
            case "recordSeries":
            {
               type = 'record-series';
               break;
            }
            case "recordCategory":
            {
               type = 'record-category';
               break;
            }
            case "recordFolder":
            {
               type = 'record-folder';
               break;
            }
            case "nonElectronicDocument":
            {
               type = 'non-electronic';
               break;
            }
            case "metadataStub":
            {
               type = 'meta-stub';
               break;
            }
            default:
            {
               return Alfresco.constants.URL_CONTEXT + 'components/images/filetypes/' + Alfresco.util.getFileIcon(item.name, item.type, size); 
            }
         }
         return Alfresco.constants.URL_CONTEXT + 'components/documentlibrary/images/' + type + '-'+size+'.png';
      }
   }      
   });
   
})();