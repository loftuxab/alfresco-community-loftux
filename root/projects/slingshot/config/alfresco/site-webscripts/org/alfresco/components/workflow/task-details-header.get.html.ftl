<#assign el=args.htmlid?js_string>
<script type="text/javascript">//<![CDATA[
new Alfresco.component.TaskDetailsHeader("${el}").setOptions(
{
   <#if page.url.args.referrer??>referrer: "${page.url.args.referrer?js_string}"</#if>
}).setMessages(
   ${messages}
);
//]]></script>
<div id="${el}-body" class="form-manager task-details-header">
   <div class="links hidden">
      <span class="theme-color-2">${msg("label.taskDetails")}</span>
      <span class="separator">|</span>
      <a href="">${msg("label.workflowDetails")}</a>
   </div>
   <h1>${msg("header")}: <span></span></h1>
   <div class="clear"></div>
</div>
