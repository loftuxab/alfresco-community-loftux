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
 * Rich text control component.
 * 
 * This component renders a TinyMCE editor. If this component is
 * being used for a content property the type of editor i.e. tinymce
 * or the default textarea is determined by the content's mimetype.
 * 
 * @namespace Alfresco
 * @class Alfresco.RichTextControl
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom;

   /**
    * RichTextControl constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.RichTextControl} The new RichTextControl instance
    * @constructor
    */
   Alfresco.RichTextControl = function(htmlId)
   {
      return Alfresco.RichTextControl.superclass.constructor.call(this, "Alfresco.RichTextControl", htmlId, ["button"]);
   };
   
   YAHOO.extend(Alfresco.RichTextControl, Alfresco.component.Base,
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
          * The current value
          *
          * @property currentValue
          * @type string
          */
         currentValue: "",
         
         /**
          * Flag to determine whether the picker is in disabled mode
          *
          * @property disabled
          * @type boolean
          * @default false
          */
         disabled: false,
         
         /**
          * Flag to indicate whether the field is mandatory
          *
          * @property mandatory
          * @type boolean
          * @default false
          */
         mandatory: false,
         
         /**
          * Object to hold the parameters for the editor
          * 
          * @property editorParameters
          * @type object
          */
         editorParameters: null,
         
         /**
          * Flag to indicate whether the field is a content property
          * 
          * @property contentProperty
          * @type boolean
          * @default false
          */
         contentProperty: false,
         
         /**
          * Current Form Mode: edit or create
          * 
          * @property formMode
          * @type string
          */
         formMode: "edit",
         
         /**
          * NodeRef of the item the form is for
          * 
          * @property nodeRef
          * @type string
          */
         nodeRef: null,
         
         /**
          * The mimetype of the content being created
          * 
          * @property mimeType
          * @type string
          */
         mimeType: null,
         
         /**
          * Comma separated list of mime types that will be shown
          * in a textarea
          * 
          * @property plainMimeTypes
          * @type string
          */
         plainMimeTypes: "text/plain,text/xml",
         
         /**
          * Comma separated list of mime types that will be shown
          * in the TinyMCE editor
          * 
          * @property richMimeTypes
          * @type string
          */
         richMimeTypes: "text/html,text/xhtml"
      },

      /**
       * The editor instance for the control
       * 
       * @property editor
       * @type object
       */
      editor: null,

      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function RichTextControl_onReady()
      {
         if (Alfresco.logger.isDebugEnabled())
         {
            Alfresco.logger.debug("Rendering rich text control for element '" + this.id + 
                  "', value = '" + this.options.currentValue + "', contentProperty = '" + this.options.contentProperty +
                  "', nodeRef = '" + this.options.nodeRef + "', mimetype = '" + this.options.mimeType + "'");
            Alfresco.logger.debug("Configured plain mimetypes for element '" + this.id + "': " + this.options.plainMimeTypes);
            Alfresco.logger.debug("Configured rich mimetypes for element '" + this.id + "': " + this.options.richMimeTypes);
            Alfresco.logger.debug("Editor parameters for element '" + this.id + "': " + 
                  YAHOO.lang.dump(this.options.editorParameters));
         }
         
         if (this.options.contentProperty)
         {
            // get the mimetype of the content
            var contentMimetype = this._determineMimeType();
               
            if (contentMimetype !== null)
            {
               if (this._isRichMimeType(contentMimetype))
               {
                  // populate the textarea with the content
                  this._populateContent();
                  
                  // render the TinyMCE editor for rich mimetypes
                  // but only when the field is not disabled
                  if (!this.options.disabled)
                  {
                     this._renderEditor();
                  }
               }
               else if (this._isPlainMimeType(contentMimetype))
               {
                  // populate the textarea with the content
                  this._populateContent();
               }
               else
               {
                  this._hideField();
                  Alfresco.logger.debug("Hidden field '" + this.id + "' as the content for the mimetype can not be displayed");
               }
            }
            else
            {
               this._hideField();
               Alfresco.logger.debug("Hidden field '" + this.id + "' as the mimetype is unknown");
            }
         }
         else
         {
            if (!this.options.disabled)
            {
               // always render the TinyMCE editor for non content properties
               // that are not disabled
               this._renderEditor();
            }
         }
      },
      
      /**
       * Retrieves and populates the content for the current control
       * 
       * @method _populateContent
       * @private
       */
      _populateContent: function RichTextControl__populateContent()
      {
         if (this.options.nodeRef !== null && this.options.nodeRef.length > 0)
         {
            Alfresco.logger.debug("Retrieving content for field '" + this.id + "' using nodeRef: " + this.options.nodeRef);
            
            // success handler, show the content
            var onSuccess = function RichTextControl_populateContent_onSuccess(response)
            {
               Dom.get(this.id).value = response.serverResponse.responseText;
            };
            
            // failure handler, display alert
            var onFailure = function RichTextControl_populateContent_onFailure(response)
            {
               // hide the whole field so incorrect content does not get re-submitted
               this._hideField();
               Alfresco.logger.debug("Hidden field '" + this.id + "' as content retrieval failed");
            };
            
            // attempt to retrieve content
            var nodeRefUrl = this.options.nodeRef.replace("://", "/");
            Alfresco.util.Ajax.request(
            {
               url: Alfresco.constants.PROXY_URI + "api/node/content/" + nodeRefUrl,
               method: "GET",
               successCallback:
               {
                  fn: onSuccess,
                  scope: this
               },
               failureCallback:
               {
                  fn: onFailure,
                  scope: this
               }
            });
         }
         else if (this.options.formMode !== "create")
         {
            this._hideField();
            Alfresco.logger.debug("Hidden field '" + this.id + "' as the nodeRef parameter is missing");
         }
      },
      
      /**
       * Returns the mimetype for the content property.
       * 
       * Returns null if the field is not a content property.
       * 
       * If a mimetype can not be determined from the content url of the property
       * the mimeType parameter is examined, if that is empty or null, null is returned.
       * 
       * @method _determineMimeType
       * @return the mimetype or null if it can not be determined
       */
      _determineMimeType: function RichTextControl__determineMimeType()
      {
         var result = null;
         
         if (this.options.contentProperty)
         {
            if (this.options.currentValue.indexOf("contentUrl=") === 0 &&
                this.options.currentValue.indexOf("mimetype=") !== -1)
            {
               // extract the mimetype from the content url
               var mtBegIdx = this.options.currentValue.indexOf("mimetype=") + 9,
                  mtEndIdx = this.options.currentValue.indexOf("|", mtBegIdx);
               result = this.options.currentValue.substring(mtBegIdx, mtEndIdx);
            }
            
            // if the content url did not contain the mimetype examine
            // the mimeType parameter
            if (this.options.mimeType !== null && this.options.mimeType.length > 0)
            {
               result = this.options.mimeType;
            }
         }
         
         Alfresco.logger.debug("Determined mimetype: " + result);
         return result;
      },
      
      /**
       * Determines whether the given mimetype is a configured 'rich' mimetype.
       * 
       * @method _isRichMimeType
       * @param mimetype {string} The mimetype to check
       * @return true if the given mimetype is a 'rich' mimetype
       */
      _isRichMimeType: function RichTextControl__isRichMimeType(mimetype)
      {
         var result = false;
         
         if (this.options.richMimeTypes !== null && this.options.richMimeTypes.length > 0 &&
             this.options.richMimeTypes.indexOf(mimetype) != -1)
         {
            result = true;
         }
         
         Alfresco.logger.debug("Testing whether '" + mimetype + "' is a configured rich mimetype: " + result);
         return result;
      },
      
      /**
       * Determines whether the given mimetype is a configured 'plain' mimetype.
       * 
       * @method _isPlainMimeType
       * @param mimetype {string} The mimetype to check
       * @return true if the given mimetype is a 'plain' mimetype
       */
      _isPlainMimeType: function RichTextControl__isPlainMimeType(mimetype)
      {
         var result = false;
         
         if (this.options.plainMimeTypes !== null && this.options.plainMimeTypes.length > 0 &&
             this.options.plainMimeTypes.indexOf(mimetype) != -1)
         {
            result = true;
         }
         
         Alfresco.logger.debug("Testing whether '" + mimetype + "' is a configured plain mimetype: " + result);
         return result;
      },
      
      /**
       * Creates and renders the TinyMCE editor
       * 
       * @method _renderEditor
       * @private
       */
      _renderEditor: function RichTextControl__renderEditor()
      {
         // create the editor instance
         this.editor = new Alfresco.util.RichEditor("tinyMCE", this.id, this.options.editorParameters);
      
         // render and register event handler
         this.editor.render();
         this.editor.subscribe("onKeyUp", this._handleContentChange, this, true);
      },
      
      /**
       * Handles the content being changed in the TinyMCE control.
       * 
       * @method _handleContentChange
       * @param type
       * @param args
       * @param obj
       * @private
       */
      _handleContentChange: function RichTextControl__handleContentChange(type, args, obj)
      {
         // save the current contents of the editor to the underlying textarea
         this.editor.save();

         // inform the forms runtime if this field is mandatory
         if (this.options.mandatory)
         {
            YAHOO.Bubbling.fire("mandatoryControlValueUpdated", this);
         }
      },
      
      /**
       * Hides the field, used when a content property can not be shown.
       * 
       * @method _hideField
       * @private
       */
      _hideField: function RichTextControl__hideField()
      {
         // change the name of the textarea so it is not submitted as new content!
         Dom.get(this.id).name = "-";
         
         // hide the whole field
         Dom.get(this.id + "-field").style.display = "none";
      }
   });
})();
