<script type="text/javascript">//<![CDATA[
   new Alfresco.RecordsPermissions("${args.htmlid}").setOptions({
    siteId: "${page.url.templateArgs.site!""}",
    nodeRef: "${page.url.args.nodeRef!""}",
    docName: "${page.url.args.docName!""}"
 }).setMessages(${messages});
//]]></script>

<#assign el=args.htmlid>
<div id="${el}-body" class="permissions">
   <div class="floatright">
      <!-- Add User/Group button -->
      <div class="addusergroup-button">
         <span class="yui-button yui-push-button" id="${el}-addusergroup-button">
            <span class="first-child"><button>${msg("button.addUserGroup")}</button></span>
         </span>
      </div>
   </div>
   <div class="floatright inherit">
      <!-- Inherit Permissions checkbox -->
      <input type="checkbox" id="${el}-inherit" />
      <label for="${el}-inherit">${msg("label.inherit")}</label>
   </div>
   <div class="title">${msg("label.title", '${page.url.args.docName!""}')?html}</div>
   
   <div class="list">
      Stuff here<br/>
      Blahblahblahblah
   </div>
</div>