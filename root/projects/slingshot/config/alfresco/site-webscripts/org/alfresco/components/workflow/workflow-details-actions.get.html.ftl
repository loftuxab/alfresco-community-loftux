<#assign el=args.htmlid>
<script type="text/javascript">//<![CDATA[
new Alfresco.WorkflowDetailsActions("${el}").setMessages(
   ${messages}
);
//]]></script>
<div id="${el}-body" class="form-manager workflow-details-actions">
   <div class="actions hidden">
      <button id="${el}-cancel">${msg("button.cancelWorkflow")}</button>
   </div>
</div>
