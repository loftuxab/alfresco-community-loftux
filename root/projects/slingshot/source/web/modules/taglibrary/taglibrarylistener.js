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
 * TagLibraryListener
 * 
 * Listener for tag library events.
 * Helper object to keep track of tag selection changes as well
 * as to update a form with the latest selection of tags-
 *
 * @namespace Alfresco
 * @class Alfresco.TagLibraryListener
 */
(function()
{
   /**
    * Create a new TagLibraryListener.
    * @param formId The id of the form
    * @param tagsFieldName The name of the field to use for the tags. The name should not include the []
    *                      as this is added by updateTagsField()
    */
   Alfresco.TagLibraryListener = function(formId, tagsFieldName)
   {
      this.formId = formId;
      this.tagsFieldName = tagsFieldName;
      
      this._registerTagsChangedListener();
      return this;
   };

   Alfresco.TagLibraryListener.prototype =
   {
      /**
       * Currently selected tags.
       * 
       * @type: array of strings
       * @default null
       */
      selectedTags: null,
      
      /**
       * Registers the bubble listener for onTagLibraryTagsChanged events.
       */
      _registerTagsChangedListener: function TagLibraryListener__registerTagsChangedListener()
      {
         YAHOO.Bubbling.on("onTagLibraryTagsChanged", this.onTagLibraryTagsChanged, this);
      },
      
      /** Called when a onTagLibraryTagsChanged occurs. */
      onTagLibraryTagsChanged: function TagLibraryListener_onTagLibraryTagsChanged(layer, args)
      {
          this.selectedTags = args[1].tags;
      },
      
      /**
       * Updates the form with the last selected tags.
       * Note: this method does nothing if the tags haven't been changed
       *       at all.
       */
      updateForm: function TagLibraryListener_updateForm()
      {
         if (this.selectedTags == null)
         {
            return;
         }
         
         // construct the complete name to use for the field
         var fullFieldName = this.tagsFieldName + '[]';
         
         // clean out the currently available tag inputs
         var formElem = YAHOO.util.Dom.get(this.formId);
         
         // find all input fields, delete the inputs that match the field name
         var inputs = formElem.getElementsByTagName("input");
         for (var x=0; x < inputs.length; x++)
         {
            if (inputs[x].name == fullFieldName)
            {
                // remove the field
                inputs[x].parentNode.removeChild(inputs[x]);
                x--;
            }
         }
         
         // generate inputs for the selected tags
         for (var x=0; x < this.selectedTags.length; x++)
         {
            var tagName = this.selectedTags[x];
            var elem = document.createElement('input');
            elem.setAttribute('name', fullFieldName);
            elem.setAttribute('value', tagName);
            elem.setAttribute('type', 'hidden');
            formElem.appendChild(elem);
         }
      }
   };     
})();
