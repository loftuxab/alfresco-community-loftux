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
 * Document info component.
 * 
 * @namespace Alfresco
 * @class Alfresco.DocumentInfo
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      Element = YAHOO.util.Element;
   
   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML;
   
   /**
    * DocumentInfo constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.DocumentInfo} The new DocumentInfo instance
    * @constructor
    */
   Alfresco.DocumentInfo = function(htmlId)
   {
      this.name = "Alfresco.DocumentInfo";
      this.id = htmlId;
      
      // initialise prototype properties
      this.widgets = {};
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require([], this.onComponentsLoaded, this);
   
      /* Decoupled event listeners */
      YAHOO.Bubbling.on("documentDetailsAvailable", this.onDocumentDetailsAvailable, this);
      
      return this;
   }
   
   Alfresco.DocumentInfo.prototype =
   {
      /**
       * Object container for storing YUI widget instances.
       * 
       * @property widgets
       * @type object
       */
       widgets: {},
      
      /**
       * Set messages for this component.
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {Alfresco.Search} returns 'this' for method chaining
       */
      setMessages: function DocumentInfo_setMessages(obj)
      {
         Alfresco.util.addMessages(obj, this.name);
         return this;
      },
      
      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function DocumentInfo_onComponentsLoaded()
      {
         // don't need to do anything we will be informed via an event when data is ready
      },
      
      /**
       * Event handler called when the "documentDetailsAvailable" event is received
       */
      onDocumentDetailsAvailable: function DocumentInfo_onDocumentDetailsAvailable(layer, args)
      {
         var docData = args[1];
         
         // render the core metadata values
         Dom.get(this.id + "-meta-name").innerHTML = $html(docData.fileName);
         Dom.get(this.id + "-meta-content-type").innerHTML = $html(this._getMimetypeLabel(docData.mimetype));
         //Dom.get(this.id + "-meta-encoding").innerHTML = $html(docData.encoding);
         Dom.get(this.id + "-meta-title").innerHTML = $html(docData.title);
         Dom.get(this.id + "-meta-description").innerHTML = $html(docData.description);
         //Dom.get(this.id + "-meta-owner").innerHTML = $html(docData.owner);
         Dom.get(this.id + "-meta-size").innerHTML = $html(Alfresco.util.formatFileSize(docData.size));
         Dom.get(this.id + "-meta-creator").innerHTML = $html(docData.createdBy);
         Dom.get(this.id + "-meta-createdon").innerHTML = $html(Alfresco.util.formatDate(docData.createdOn));
         Dom.get(this.id + "-meta-modifier").innerHTML = $html(docData.modifiedBy);
         Dom.get(this.id + "-meta-modifiedon").innerHTML = $html(Alfresco.util.formatDate(docData.modifiedOn));
         
         // render tags values
         var tags = docData.tags;
         var tagsHtml = "";
         
         if (tags.length === 0)
         {
            tagsHtml = Alfresco.util.message("document-info.notags", "Alfresco.DocumentInfo");
         }
         else
         {
            for (var x = 0; x < tags.length; x++)
            {
               tagsHtml += '<div class="tag"><img src="' + Alfresco.constants.URL_CONTEXT + '/components/images/tag-16.png" />';
               tagsHtml += $html(tags[x]) + '</div>';
            }
         }
         
         Dom.get(this.id + "-tags").innerHTML = tagsHtml;
         
         // render permissions values
         var unknownPerm = Alfresco.util.message("document-info.unknown", "Alfresco.DocumentInfo");
         var managerPerms = unknownPerm;
         var collaboratorPerms = unknownPerm;
         var consumerPerms = unknownPerm;
         var everyonePerms = unknownPerm;
         
         var rawPerms = docData.permissions.roles;
         for (var x = 0; x < rawPerms.length; x++)
         {
            var permParts = rawPerms[x].split(";");
            var group = permParts[1];
            if (group.indexOf("_SiteManager") != -1)
            {
               managerPerms = Alfresco.util.message("document-info.role." + permParts[2], "Alfresco.DocumentInfo");
            }
            else if (group.indexOf("_SiteCollaborator") != -1)
            {
               collaboratorPerms = Alfresco.util.message("document-info.role." + permParts[2], "Alfresco.DocumentInfo");
            }
            else if (group.indexOf("_SiteConsumer") != -1)
            {
               consumerPerms = Alfresco.util.message("document-info.role." + permParts[2], "Alfresco.DocumentInfo");
            }
            else if (group === "GROUP_EVERYONE")
            {
               everyonePerms = Alfresco.util.message("document-info.role." + permParts[2], "Alfresco.DocumentInfo");
            }
         }
         
         Dom.get(this.id + "-perms-managers").innerHTML = $html(managerPerms);
         Dom.get(this.id + "-perms-collaborators").innerHTML = $html(collaboratorPerms);
         Dom.get(this.id + "-perms-consumers").innerHTML = $html(consumerPerms);
         Dom.get(this.id + "-perms-everyone").innerHTML = $html(everyonePerms);
      },
      
      /**
       * Returns the label for the given mimetype
       * 
       * @method _getMimetypeLabel
       * @param mimetype The mimetype to find label for
       * @return The mimetype label
       */
      _getMimetypeLabel: function DocumentInfo__getMimetypeLabel(mimetype)
      {
         var label = Alfresco.util.message("document-info.unknown", "Alfresco.DocumentInfo");
         
         // TODO: Obviously this need to change!!! We need to transfer the mimetype config from the server
         
         switch (mimetype)
         {
            case "text/plain":
               label = "Plain Text";
               break;
               
            case "image/png":
               label = "PNG Image";
               break;
               
            case "image/jpeg":
               label = "JPEG Image";
               break;
               
            case "image/gif":
               label = "GIF Image";
               break;
               
            case "text/html":
               label = "HTML";
               break;
            
            case "application/xhtml+xml":
               label = "XHTML";
               break;
               
            case "text/xml":
               label = "XML";
               break; 
               
            case "application/pdf":
               label = "Adobe PDF Document";
               break;
               
            case "text/css":
               label = "Style Sheet";
               break;
               
            case "application/zip":
               label = "ZIP";
               break;
               
            case "application/vnd.excel":
               label = "Microsoft Excel";
               break;
               
            case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet":
               label = "Microsoft Excel 2007";
               break;
               
            case "application/vnd.powerpoint":
               label = "Microsoft PowerPoint";
               break; 
               
            case "application/vnd.openxmlformats-officedocument.presentationml.presentation":
               label = "Microsoft PowerPoint 2007";
               break;
  
            case "application/msword":
               label = "Microsoft Word";
               break; 
            
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
               label = "Microsoft Word 2007";
               break;
               
            case "text/richtext":
               label = "Rich Text";
               break;
               
            case "application/rtf":
               label = "Rich Text Format";
               break;
            
            case "audio/x-aiff":
               label = "AIFF Audio";
               break;
            
            case "application/acp":
               label = "Alfresco Content Package";
               break;
               
            case "image/x-portable-anymap":
               label = "Anymap Image";
               break;
               
            case "image/x-dwg":
               label = "AutoCAD Drawing";
               break;
               
            case "image/x-dwt":
               label = "AutoCAD Template";
               break;
               
            case "audio/basic":
               label = "Basic Audio";
               break;
            
            case "image/bmp":
               label = "Bitmap Image";
               break;
               
            case "image/cgm":
               label = "CGM Image";
               break;
            
            case "message/rfc822":
               label = "Email";
               break;
               
            case "image/x-portable-graymap":
               label = "Greymap Image";
               break;
               
            case "application/x-gzip":
               label = "GZIP";
               break;
               
            case "application/x-gtar":
               label = "GZIP Tarball";
               break;
               
            case "application/vnd.oasis.opendocument.text-web":
               label = "HTML Document Template";
               break;
               
            case "text/calendar":
               label = "iCalendar File";
               break;
               
            case "image/ief":
               label = "IEF Image";
               break;
               
            case "application/java":
               label = "Java Class";
               break;
               
            case "application/x-javascript":
               label = "Java Script";
               break;
               
            case "image/jpeg2000":
               label = "JPEG 2000";
               break;
               
            case "application/x-latex":
               label = "Latex";
               break;
               
            case "application/x-troff-man":
               label = "Man Page";
               break;
               
            case "text/mediawiki":
               label = "MediaWiki Markup";
               break;
               
            case "audio/x-mpeg":
               label = "MPEG Audio";
               break;
               
            case "video/mpeg":
               label = "MPEG Video";
               break;
               
            case "video/mpeg2":
               label = "MPEG2 Video";
               break;
               
            case "video/mp4":
               label = "MPEG4 Video";
               break;
               
            case "video/x-ms-wma":
               label = "MS Streaming Audio";
               break;
               
            case "video/x-ms-asf":
               label = "MS Streaming Video (asf)";
               break;
         
            case "video/x-ms-wmv":
               label = "MS Streaming Video (wmv)";
               break;
               
            case "video/x-msvideo":
               label = "MS Video";
               break;  
               
            case "application/octet-stream":
               label = "Octet Stream";
               break;
            
            case "application/vnd.oasis.opendocument.chart":
               label = "OpenDocument Chart";
               break;
               
            case "application/vnd.oasis.opendocument.database":
               label = "OpenDocument Database";
               break;
               
            case "application/vnd.oasis.opendocument.graphics":
               label = "OpenDocument Drawing";
               break;
               
            case "application/vnd.oasis.opendocument.graphics-template":
               label = "OpenDocument Drawing Template";
               break;
               
            case "application/vnd.oasis.opendocument.formula":
               label = "OpenDocument Formula";
               break;
               
            case "application/vnd.oasis.opendocument.image":
               label = "OpenDocument Image";
               break;
               
            case "application/vnd.oasis.opendocument.text-master":
               label = "OpenDocument Master Document";
               break;
               
            case "application/vnd.oasis.opendocument.presentation":
               label = "OpenDocument Presentation";
               break;
               
            case "application/vnd.oasis.opendocument.presentation-template":
               label = "OpenDocument Presentation Template";
               break;
               
            case "application/vnd.oasis.opendocument.spreadsheet":
               label = "OpenDocument Spreadsheet";
               break;
               
            case "application/vnd.oasis.opendocument.spreadsheet-template":
               label = "OpenDocument Spreadsheet Template";
               break;
               
            case "application/vnd.oasis.opendocument.text":
               label = "OpenDocument Text (OpenOffice 2.0)";
               break;
               
            case "application/vnd.oasis.opendocument.text-template":
               label = "OpenDocument Text Template";
               break;
               
            case "application/vnd.sun.xml.calc":
               label = "OpenOffice 1.0/StarOffice6.0 Calc 6.0";
               break;
               
            case "application/vnd.sun.xml.draw":
               label = "OpenOffice 1.0/StarOffice6.0 Draw 6.0";
               break;
               
            case "application/vnd.sun.xml.impress":
               label = "OpenOffice 1.0/StarOffice6.0 Impress 6.0";
               break;
               
            case "application/vnd.sun.xml.writer":
               label = "OpenOffice 1.0/StarOffice6.0 Writer 6.0";
               break;
               
            case "image/x-portable-pixmap":
               label = "Pixmap Image";
               break;
               
            case "image/x-portable-bitmap":
               label = "Portable Bitmap";
               break;
               
            case "application/postscript":
               label = "Postscript";
               break;
               
            case "video/quicktime":
               label = "Quicktime Video";
               break;
               
            case "video/x-rad-screenplay":
               label = "RAD Screen Display";
               break;
               
            case "image/x-cmu-raster":
               label = "Raster Image";
               break;
               
            case "image/x-rgb":
               label = "RGB Image";
               break;   
            
            case "image/svg":
               label = "Scalable Vector Graphics Image";
               break;
               
            case "video/x-sgi-movie":
               label = "SGI Video";
               break;
               
            case "application/sgml":
               label = "SGML";
               break;
            
            case "text/sgml":
               label = "SGML";
               break;
               
            case "application/x-sh":
               label = "Shell Script";
               break;
               
            case "application/x-shockwave-flash":
               label = "Shockwave Flash";
               break;
               
            case "application/vnd.stardivision.chart":
               label = "StarChart 5.x";
               break;
               
            case "application/vnd.stardivision.calc":
               label = "StarCalc 5.x";
               break;
               
            case "application/vnd.stardivision.draw":
               label = "StarDraw 5.x";
               break;
               
            case "application/vnd.stardivision.impress":
               label = "StarImpress 5.x";
               break;
               
            case "application/vnd.stardivision.impress-packed":
               label = "StarImpress Packed 5.x";
               break;
               
            case "application/vnd.stardivision.math":
               label = "StarMath 5.x";
               break;
               
            case "application/vnd.stardivision.writer":
               label = "StarWriter 5.x";
               break;
               
            case "application/vnd.stardivision.writer-global":
               label = "StarWriter 5.x global";
               break;
               
            case "text/tab-separated-values":
               label = "Tab Separated Values";
               break;
               
            case "application/x-tar":
               label = "Tarball";
               break;
               
            case "application/x-tex":
               label = "Tex";
               break;
               
            case "application/x-texinfo":
               label = "Tex Info";
               break;
               
            case "image/tiff":
               label = "TIFF Image";
               break;
               
            case "x-world/x-vrml":
               label = "VRML";
               break;
               
            case "audio/x-wav":
               label = "WAV Audio";
               break;
               
            case "application/wordperfect":
               label = "WordPerfect";
               break;
               
            case "image/x-xbitmap":
               label = "XBitmap Image";
               break;
               
            case "image/x-xpixmap":
               label = "XPixmap Image";
               break;
               
            case "image/x-xwindowdump":
               label = "XWindow Dump";
               break;
               
            case "application/x-compress":
               label = "Z Compress";
               break;
         }
         
         return label;
      }
   };
})();
