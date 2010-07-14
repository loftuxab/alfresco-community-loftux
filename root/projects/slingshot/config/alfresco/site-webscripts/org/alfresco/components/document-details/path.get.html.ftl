<#include "../../include/alfresco-macros.lib.ftl" />
<script type="text/javascript">//<![CDATA[
   new Alfresco.component.Path("${args.htmlid}").setMessages(
      ${messages}
   );
//]]></script>

<div class="path-nav">
   <span class="heading">${msg("path.location")}:</span>
   <span id="${args.htmlid}-defaultPath" class="path-link"><a href="${siteURL("documentlibrary")}">${msg("path.documents")}</a></span>
   <span id="${args.htmlid}-path"></span>
</div>
<div id="${args.htmlid}-status" class="status-banner hidden"></div>