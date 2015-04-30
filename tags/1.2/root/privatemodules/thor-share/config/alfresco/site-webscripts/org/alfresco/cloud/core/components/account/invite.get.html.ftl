<#assign el=args.htmlid?html/>
<div id="${el}-body" class="cloud-invite">
   <div class="hd">${msg("header", site.title?html)}</div>
   <div class="bd">
      <form id="${el}-form" method="POST" action="${url.context}/proxy/alfresco/internal/cloud/sites/${args.site}/invitations" enctype="application/json" class="form-fields">
         <input type="hidden" name="inviterEmail" value="${user.name?html}"/>

         <!-- Emails -->
         <div class="form-field">
            <label for="${el}-inviteeEmail">${msg("label.inviteeEmail")}:</label>
            <div id="${el}-inviteeEmail"></div>
            <p class="tiny">${msg("help.inviteeEmail")}</p>
         </div>

         <!-- Role -->
         <div class="form-field">
            <label for="${el}-role">${msg("label.role")}:</label><br/>
            <select id="${el}-role" name="role" class="wide" tabindex="0">
               <#list roles as role>
                  <option value="${role.id}">${role.name}
               </#list>
            </select>
         </div>

         <!-- Message -->
         <div class="form-field">
            <label for="${el}-inviterMessage">${msg("label.inviterMessage")}:</label><br/>
            <textarea id="${el}-inviterMessage" name="inviterMessage" tabindex="0" class="wide">${msg("text.inviterMessage")}</textarea>
         </div>

         <!-- Buttons -->
         <div class="bdft">
            <button id="${el}-submit">${msg("button.inviteUser")}</button>
            <button id="${el}-cancel">${msg("button.cancel")}</button>
         </div>

         <!-- User suggestion template -->
         <div id="${el}-inviteeTemplate" class="hidden">
            <div class="user-suggestion">
               <img src="${url.context}/proxy/alfresco/slingshot/profile/avatar/{userName}"/>
               {firstName} {lastName}<br/>
               {userName}
               <div class="clear"></div>
            </div>
         </div>
      </form>
   </div>
</div>

<script type="text/javascript">//<![CDATA[
new Alfresco.cloud.component.Invite("${args.htmlid?js_string}").setOptions({}).setMessages(${messages});
//]]></script>
