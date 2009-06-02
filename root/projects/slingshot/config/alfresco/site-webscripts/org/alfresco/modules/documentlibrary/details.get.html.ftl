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
      <form id="${args.htmlid}-form" action="#" method="post">
         <div class="yui-u first edit-metadata flat-button">
            <button id="${args.htmlid}-editMetadata" tabindex="0">${msg("label.edit-metadata")}</button>
         </div>
         <div class="yui-g">
            <h2>${msg("header.metadata")}:</h2>
         </div>
         <div class="yui-gd">
            <div class="yui-u first"><label for="${args.htmlid}-name">${msg("label.name")}:</label></div>
            <div class="yui-u"><input id="${args.htmlid}-name" type="text" name="properties.name" tabindex="1" />&nbsp;*</div>
         </div>
         <div class="yui-gd">
            <div class="yui-u first"><label for="${args.htmlid}-fileTitle">${msg("label.title")}:</label></div>
            <div class="yui-u"><input id="${args.htmlid}-fileTitle" type="text" name="properties.title" tabindex="2" /></div>
         </div>
         <div class="yui-gd">
            <div class="yui-u first"><label for="${args.htmlid}-description">${msg("label.description")}:</label></div>
            <div class="yui-u"><textarea id="${args.htmlid}-description" name="properties.description" rows="5" cols="20" tabindex="3"></textarea></div>
         </div>
         <div class="yui-g">
            <h2>${msg("header.tags")}:</h2>
         </div>
         <div class="yui-gd">
            <div class="yui-u first"><label for="${args.htmlid}-tags">${msg("label.tags")}:</label></div>
            <div class="yui-u"><input id="${args.htmlid}-tags" type="text" name="tags" tabindex="5" /><br />${msg("label.tags.hint")}</div>
         </div>
         <div class="bdft">
            <input type="button" id="${args.htmlid}-ok" value="${msg("button.ok")}" tabindex="6" />
            <input type="button" id="${args.htmlid}-cancel" value="${msg("button.cancel")}" tabindex="7" />
         </div>
      </form>
   </div>
</div>
