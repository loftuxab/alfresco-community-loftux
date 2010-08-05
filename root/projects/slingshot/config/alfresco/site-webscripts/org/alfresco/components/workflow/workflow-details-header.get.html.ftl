<#assign el=args.htmlid>
<div id="${el}-body" class="form-manager workflow-details-header">
   <#if page.url.args.taskId??>
   <div class="links">
      <a href="task-details?taskId=${page.url.args.taskId?url?js_string}">${msg("label.taskDetails")}</a>
      &nbsp;|&nbsp;
      <span>${msg("label.workflowDetails")}</span>
   </div>
   </#if>
   <h1>${msg("header")}: <span></span></h1>
   <div class="clear"></div>
</div>
