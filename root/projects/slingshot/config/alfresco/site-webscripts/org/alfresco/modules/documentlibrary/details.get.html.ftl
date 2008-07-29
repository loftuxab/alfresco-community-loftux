<#macro mimetypes>
   <#assign mtConfig = config.scoped["Mimetype Map"]["mimetypes"]>
   <select>
   <#list mtConfig.childrenMap["mimetype"] as mt>
         <option value="${mt.attributes["mimetype"]}">${mt.attributes["display"]}</option>
   </#list>
   </select>
</#macro>
<script type="text/javascript">//<![CDATA[
   new Alfresco.module.DoclibDetails("${args.htmlid}").setMessages(
      ${messages}
   );
//]]></script>
<div id="${args.htmlid}-dialog" class="details">
   <div id="${args.htmlid}-title" class="hd"></div>
   <div class="bd">
      <form id="${args.htmlid}-form" action="" method="post">
         <div class="yui-g">
            <h2>${msg("header.metadata")}:</h2>
         </div>
         <div class="yui-gd">
            <div class="yui-u first"><label for="${args.htmlid}-name">${msg("label.name")}:</label></div>
            <div class="yui-u"><input id="${args.htmlid}-name" type="text" name="properties.name" />&nbsp;*</div>
         </div>
         <div class="yui-gd">
            <div class="yui-u first"><label for="${args.htmlid}-title">${msg("label.title")}:</label></div>
            <div class="yui-u"><input id="${args.htmlid}-title" type="text" name="properties.title" /></div>
         </div>
         <div class="yui-gd">
            <div class="yui-u first"><label for="${args.htmlid}-description">${msg("label.description")}:</label></div>
            <div class="yui-u"><textarea id="${args.htmlid}-description" name="properties.description" rows="3" cols="20"></textarea></div>
         </div>
         <div class="yui-gd">
            <div class="yui-u first"><label for="${args.htmlid}-mimetype">${msg("label.mimetype")}:</label></div>
            <div class="yui-u">
               <select id="${args.htmlid}-mimetype" name="mimetype">
                  <option value="application/pdf">Adobe PDF Document</option>
                  <option value="audio/x-aiff">AIFF Audio</option>
                  <option value="application/acp">Alfresco Content Package</option>
                  <option value="image/x-portable-anymap">Anymap Image</option>
                  <option value="image/x-dwg">AutoCAD Drawing</option>
                  <option value="image/x-dwt">AutoCAD Template</option>
                  <option value="audio/basic">Basic Audio</option>
                  <option value="image/bmp">Bitmap Image</option>
                  <option value="image/cgm">CGM Image</option>
                  <option value="message/rfc822">EMail</option>
                  <option value="image/gif">GIF Image</option>
                  <option value="image/x-portable-graymap">Greymap Image</option>
                  <option value="application/x-gzip">GZIP</option>
                  <option value="application/x-gtar">GZIP Tarball</option>
                  <option value="text/html">HTML</option>
                  <option value="application/vnd.oasis.opendocument.text-web">HTML Document Template</option>
                  <option value="text/calendar">iCalendar File</option>
                  <option value="image/ief">IEF Image</option>
                  <option value="application/java">Java Class</option>
                  <option value="application/x-javascript">Java Script</option>
                  <option value="image/jpeg2000">JPEG 2000 Image</option>
                  <option value="image/jpeg">JPEG Image</option>
                  <option value="application/x-latex">Latex</option>
                  <option value="application/x-troff-man">Man Page</option>
                  <option value="text/mediawiki">MediaWiki Markup</option>
                  <option value="application/vnd.excel">Microsoft Excel</option>
                  <option value="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet">Microsoft Excel 2007</option>
                  <option value="application/vnd.powerpoint">Microsoft PowerPoint</option>
                  <option value="application/vnd.openxmlformats-officedocument.presentationml.presentation">Microsoft PowerPoint 2007</option>
                  <option value="application/msword">Microsoft Word</option>
                  <option value="application/vnd.openxmlformats-officedocument.wordprocessingml.document">Microsoft Word 2007</option>
                  <option value="audio/x-mpeg">MPEG Audio</option>
                  <option value="video/mpeg">MPEG Video</option>
                  <option value="video/mpeg2">MPEG2 Video</option>
                  <option value="video/mp4">MPEG4 Video</option>
                  <option value="video/x-ms-wma">MS Streaming Audio</option>
                  <option value="video/x-ms-asf">MS Streaming Video (asf)</option>
                  <option value="video/x-ms-wmv">MS Streaming Video (wmv)</option>
                  <option value="video/x-msvideo">MS Video</option>
                  <option value="application/octet-stream">Octet Stream</option>
                  <option value="application/vnd.oasis.opendocument.chart">OpenDocument Chart</option>
                  <option value="application/vnd.oasis.opendocument.database">OpenDocument Database</option>
                  <option value="application/vnd.oasis.opendocument.graphics">OpenDocument Drawing</option>
                  <option value="application/vnd.oasis.opendocument.graphics-template">OpenDocument Drawing Template</option>
                  <option value="application/vnd.oasis.opendocument.formula">OpenDocument Formula</option>
                  <option value="application/vnd.oasis.opendocument.image">OpenDocument Image</option>
                  <option value="application/vnd.oasis.opendocument.text-master">OpenDocument Master Document</option>
                  <option value="application/vnd.oasis.opendocument.presentation">OpenDocument Presentation</option>
                  <option value="application/vnd.oasis.opendocument.presentation-template">OpenDocument Presentation Template</option>
                  <option value="application/vnd.oasis.opendocument.spreadsheet">OpenDocument Spreadsheet</option>
                  <option value="application/vnd.oasis.opendocument.spreadsheet-template">OpenDocument Spreadsheet Template</option>
                  <option value="application/vnd.oasis.opendocument.text">OpenDocument Text (OpenOffice 2.0)</option>
                  <option value="application/vnd.oasis.opendocument.text-template">OpenDocument Text Template</option>
                  <option value="application/vnd.sun.xml.calc">OpenOffice 1.0/StarOffice6.0 Calc 6.0</option>
                  <option value="application/vnd.sun.xml.draw">OpenOffice 1.0/StarOffice6.0 Draw 6.0</option>
                  <option value="application/vnd.sun.xml.impress">OpenOffice 1.0/StarOffice6.0 Impress 6.0</option>
                  <option value="application/vnd.sun.xml.writer">OpenOffice 1.0/StarOffice6.0 Writer 6.0</option>
                  <option value="image/x-portable-pixmap">Pixmap Image</option>
                  <option value="text/plain">Plain Text</option>
                  <option value="image/png">PNG Image</option>
                  <option value="image/x-portable-bitmap">Portable Bitmap</option>
                  <option value="application/postscript">Postscript</option>
                  <option value="video/quicktime">Quicktime Video</option>
                  <option value="video/x-rad-screenplay">RAD Screen Display</option>
                  <option value="image/x-cmu-raster">Raster Image</option>
                  <option value="image/x-rgb">RGB Image</option>
                  <option value="text/richtext">Rich Text</option>
                  <option value="application/rtf">Rich Text Format</option>
                  <option value="image/svg">Scalable Vector Graphics Image</option>
                  <option value="video/x-sgi-movie">SGI Video</option>
                  <option value="application/sgml">SGML</option>
                  <option value="text/sgml">SGML</option>
                  <option value="application/x-sh">Shell Script</option>
                  <option value="application/x-shockwave-flash">Shockwave Flash</option>
                  <option value="application/vnd.stardivision.chart">StaChart 5.x</option>
                  <option value="application/vnd.stardivision.calc">StarCalc 5.x</option>
                  <option value="application/vnd.stardivision.draw">StarDraw 5.x</option>
                  <option value="application/vnd.stardivision.impress">StarImpress 5.x</option>
                  <option value="application/vnd.stardivision.impress-packed">StarImpress Packed 5.x</option>
                  <option value="application/vnd.stardivision.math">StarMath 5.x</option>
                  <option value="application/vnd.stardivision.writer">StarWriter 5.x</option>
                  <option value="application/vnd.stardivision.writer-global">StarWriter 5.x global</option>
                  <option value="text/css">Style Sheet</option>
                  <option value="text/tab-separated-values">Tab Separated Values</option>
                  <option value="application/x-tar">Tarball</option>
                  <option value="application/x-tex">Tex</option>
                  <option value="application/x-texinfo">Tex Info</option>
                  <option value="image/tiff">TIFF Image</option>
                  <option value="x-world/x-vrml">VRML</option>
                  <option value="audio/x-wav">WAV Audio</option>
                  <option value="application/wordperfect">WordPerfect</option>
                  <option value="image/x-xbitmap">XBitmap Image</option>
                  <option value="application/xhtml+xml">XHTML</option>
                  <option value="text/xml">XML</option>
                  <option value="image/x-xpixmap">XPixmap Image</option>
                  <option value="image/x-xwindowdump">XWindow Dump</option>
                  <option value="application/x-compress">Z Compress</option>
                  <option value="application/zip">ZIP</option>
               </select>              
            </div>
         </div>
         <div class="yui-g">
            <h2>${msg("header.tags")}:</h2>
         </div>
         <div class="yui-gd">
            <div class="yui-u first"><label for="${args.htmlid}-tags">${msg("label.tags")}:</label></div>
            <div class="yui-u"><input id="${args.htmlid}-tags" type="text" name="tags" /><br />${msg("label.tags.hint")}</div>
         </div>
         <div class="bdft">
            <input type="submit" id="${args.htmlid}-ok" value="${msg("button.ok")}" />
            <input type="button" id="${args.htmlid}-cancel" value="${msg("button.cancel")}" />
         </div>
      </form>
   </div>
</div>
