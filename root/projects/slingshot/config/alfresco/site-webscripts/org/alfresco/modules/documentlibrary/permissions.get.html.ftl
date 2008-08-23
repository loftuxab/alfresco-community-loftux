<script type="text/javascript">//<![CDATA[
   Alfresco.util.ComponentManager.find(
   {
      id: "${args.htmlid}"
   })[0].setOptions(
   {
      roles:
      {
         <#list siteRoles as siteRole>"${siteRole}": true<#if siteRole_has_next>,</#if></#list>
      }
   }).setMessages(
      ${messages}
   );
//]]></script>
<div id="${args.htmlid}-dialog" class="permissions">
   <div id="${args.htmlid}-title" class="hd"></div>
   <div class="bd">
      <div class="yui-g">
         <h2>${msg("header.actions")}:</h2>
      </div>
      <div class="yui-gd">
         <div class="yui-u first right"><label for="${args.htmlid}-actions">${msg("label.quick-actions")}</label></div>
         <div class="yui-u quick-actions">
            <button id="${args.htmlid}-allow-members-collaborate">${msg("label.allow-members-collaborate")}</button>
            <br /><br />
            <button id="${args.htmlid}-reset-all">${msg("label.reset-all")}</button>
            <button id="${args.htmlid}-deny-all">${msg("label.deny-all")}</button>
         </div>
      </div>
      <br />
      <div class="yui-g">
         <h2>${msg("header.manage")}:</h2>
      </div>
<#list groupNames as group>
      <div class="yui-gd">
         <div class="yui-u first right"><label>${msg("group." + group)}:</label></div>
         <div class="yui-u">
            <button id="${args.htmlid}-${group?lower_case}" value="${permissionGroups[group_index]}" class="site-group"></button>
            <select id="${args.htmlid}-${group?lower_case}-select">
   <#list siteRoles as siteRole>
                <option value="${siteRole}">${msg("role." + siteRole)}</option>
   </#list>
            </select>
         </div>
      </div>
</#list>
      <div class="bdft">
         <input type="button" id="${args.htmlid}-ok" value="${msg("button.ok")}" tabindex="6" />
         <input type="button" id="${args.htmlid}-cancel" value="${msg("button.cancel")}" tabindex="7" />
      </div>
   </div>
</div>
