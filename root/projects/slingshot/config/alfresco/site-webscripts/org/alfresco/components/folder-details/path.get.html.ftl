<#include "../../include/alfresco-macros.lib.ftl" />
<script type="text/javascript">//<![CDATA[
   new Alfresco.component.Path("${args.htmlid}").setOptions(
   {
      showIconType: ${args.showIconType!"true"}
   }).setMessages(
      ${messages}
   );
//]]></script>

<div class="path-nav">
   <span class="heading">${msg("path.location")}:</span>
   <span id="${args.htmlid}-defaultPath" class="path-link"><a href="${siteURL("documentlibrary")}">${msg("path.documents")}</a></span>
   <span id="${args.htmlid}-path"></span>
</div>
<#if (args.showIconType!"true") == "true">
<div id="${args.htmlid}-iconType" class="icon-type"></div>
</#if>