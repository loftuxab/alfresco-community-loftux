<#assign el=args.htmlid>
<script type="text/javascript">//<![CDATA[
new Alfresco.TaskEditHeader("${el}").setOptions(
{
   submitButtonMessageKey: "button.saveandclose",
   forwardUrl: Alfresco.util.uriTemplate("userdashboardpage", { userid: Alfresco.constants.USERNAME }),
}).setMessages(
   ${messages}
);
//]]></script>
<div id="${el}-body" class="form-manager task-edit-header">
   <div class="actions">
      <span class="unassigned hidden">
         <button id="${el}-claim">${msg("button.claim")}</button>
         <button id="${el}-assign">${msg("button.assign")}</button>
      </span>
      <span class="assigned hidden">
         <button id="${el}-reassign">${msg("button.reassign")}</button>
         <button id="${el}-release">${msg("button.release")}</button>
      </span>
   </div>
   <h1>${msg("header")}<span id="${el}-title"></span></h1>
   <div class="clear"></div>
   <div class="unassigned hidden theme-bg-color-2 theme-border-4"><span>${msg("message.unassigned")}</span></div>
</div>
