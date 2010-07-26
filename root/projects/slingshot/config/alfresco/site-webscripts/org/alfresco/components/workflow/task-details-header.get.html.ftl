<#assign el=args.htmlid>
<script type="text/javascript">//<![CDATA[
new Alfresco.TaskDetailsHeader("${el}").setMessages(
   ${messages}
);
//]]></script>
<div id="${el}-body" class="form-manager task-details-header">
   <div class="links hidden">
      <span>${msg("label.taskDetails")}</span>
      &nbsp;|&nbsp;
      <a href="">${msg("label.workflowDetails")}</a>
   </div>
   <h1>${msg("header")}</h1>
   <div class="clear"></div>
</div>
