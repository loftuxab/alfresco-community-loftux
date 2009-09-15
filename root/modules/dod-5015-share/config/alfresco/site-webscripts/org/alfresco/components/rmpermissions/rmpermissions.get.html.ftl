<script type="text/javascript">//<![CDATA[
   new Alfresco.RecordsPermissions("${args.htmlid}").setOptions({
    siteId: "${page.url.templateArgs.site!""}",
    nodeRef: "${page.url.args.nodeRef!""}",
    docName: "${page.url.args.docName!""}"
 }).setMessages(${messages});
//]]></script>

<#assign el=args.htmlid>
<div id="${el}-body" class="permissions">
   <!-- Add User/Group button -->
   <div class="floatright">
      <div class="addusergroup-button">
         <span class="yui-button yui-push-button" id="${el}-addusergroup-button">
            <span class="first-child"><button>${msg("button.addUserGroup")}</button></span>
         </span>
      </div>
      <!-- Authority Picker -->
      <div class="authority-picker" id="${el}-authoritypicker"></div>
   </div>
   <!-- Inherit Permissions checkbox
   <div class="floatright inherit">
      <input type="checkbox" id="${el}-inherit" />
      <label for="${el}-inherit">${msg("label.inherit")}</label>
   </div>
   -->
   <div class="title">${msg("label.title", '${page.url.args.docName!""}')?html}</div>
   
   <!-- Permissions List -->
   <div class="list">
      <div class="list-item">
         <div class="controls-header">
            <span class="header">${msg("label.permissions")}</span>
            <div class="actions"><span class="header">${msg("label.actions")}</span></div>
         </div>
         <div class="header">${msg("label.usersgroups")}</div>
      </div>
      <div id="${el}-list"></div>
   </div>
   
   <!-- Finish button -->
   <div class="center">
      <div class="finish-button">
         <span class="yui-button yui-push-button" id="${el}-finish-button">
            <span class="first-child"><button>${msg("button.done")}</button></span>
         </span>
      </div>
   </div>
</div>