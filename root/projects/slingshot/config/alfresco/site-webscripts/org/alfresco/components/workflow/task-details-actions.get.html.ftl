<#assign el=args.htmlid?js_string>
<script type="text/javascript">//<![CDATA[
new Alfresco.TaskDetailsActions("${el}").setMessages(
   ${messages}
);
//]]></script>
<div id="${el}-body" class="form-manager task-details-actions">
   <div class="actions hidden">
      <button id="${el}-edit">${msg("button.edit")}</button>
   </div>
</div>
