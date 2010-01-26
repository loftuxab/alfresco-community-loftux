<#import "/org/alfresco/modules/taglibrary/taglibrary.lib.ftl" as taglibraryLib/>
<script type="text/javascript">//<![CDATA[
   new Alfresco.module.DoclibDetails("${args.htmlid}").setMessages(
      ${messages}
   );
//]]></script>
<div id="${args.htmlid}-dialog" class="details">
   <div id="${args.htmlid}-title" class="hd"></div>
   <div class="bd">
      <form id="${args.htmlid}-form" action="#" method="post">
         <div class="yui-u first edit-metadata flat-button">
            <button id="${args.htmlid}-editMetadata" tabindex="0">${msg("label.edit-metadata")}</button>
         </div>
         <div class="yui-g">
            <h2>${msg("header.metadata")}</h2>
         </div>
         <div class="yui-gd">
            <div class="yui-u first"><label for="${args.htmlid}-name">${msg("label.name")}:</label></div>
            <div class="yui-u"><input id="${args.htmlid}-name" type="text" name="properties.name" tabindex="0" />&nbsp;*</div>
         </div>
         <div class="yui-gd">
            <div class="yui-u first"><label for="${args.htmlid}-fileTitle">${msg("label.title")}:</label></div>
            <div class="yui-u"><input id="${args.htmlid}-fileTitle" type="text" name="properties.title" tabindex="0" /></div>
         </div>
         <div class="yui-gd">
            <div class="yui-u first"><label for="${args.htmlid}-description">${msg("label.description")}:</label></div>
            <div class="yui-u"><textarea id="${args.htmlid}-description" name="properties.description" rows="5" cols="20" tabindex="0"></textarea></div>
         </div>
         <div class="yui-g">
            <h2>${msg("header.tags")}</h2>
         </div>
         <div class="yui-gd">
            <div class="yui-u first"><label for="${args.htmlid}-tag-input-field">${msg("label.tags")}:</label></div>
            <div class="yui-u"><@taglibraryLib.renderTagLibraryHTML htmlid=args.htmlid /></div>
         </div>
         <div class="bdft">
            <input type="button" id="${args.htmlid}-ok" value="${msg("button.ok")}" tabindex="0" />
            <input type="button" id="${args.htmlid}-cancel" value="${msg("button.cancel")}" tabindex="0" />
         </div>
      </form>
   </div>
</div>
