<#include "../../include/alfresco-macros.lib.ftl" />
<script type="text/javascript">//<![CDATA[
   new Alfresco.component.Path("${args.htmlid}").setOptions(
   {
      rootPage: "repository",
      rootLabelId: "path.repository"
   }).setMessages(
      ${messages}
   );
//]]></script>

<div class="path-nav">
   <span class="heading">${msg("path.location")}:</span>
   <#assign href>${url.context}/page/repository</#assign>
   <span id="${args.htmlid}-defaultPath" class="path-link"><a href="${siteURL("repository")}">${msg("path.repository")}</a></span>
   <span id="${args.htmlid}-path"></span>
</div>
<#if (args.showIconType!"true") == "true">
<div id="${args.htmlid}-iconType" class="icon-type"></div>
</#if>