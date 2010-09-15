<#include "../../include/alfresco-macros.lib.ftl" />
<#assign el=args.htmlid?js_string>
<script type="text/javascript">//<![CDATA[
new Alfresco.component.TaskDetailsActions("${el}").setOptions(
{
   defaultUrl: "${siteURL("my-workflows")}",
   <#if page.url.args.referrer??>referrer: "${page.url.args.referrer?js_string}"</#if>  
}).setMessages(
   ${messages}
);
//]]></script>
<div id="${el}-body" class="form-manager task-details-actions">
   <div class="actions hidden">
      <button id="${el}-edit">${msg("button.edit")}</button>
   </div>
</div>
