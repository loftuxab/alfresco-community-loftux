<div class="dashlet">
   <div class="title">${msg("header.colleagues")}</div>
   <div class="toolbar">
      <a href="#">${msg("link.viewAll")}</a> |
      <span>${msg("link.find")} </span><input type="text" />
   </div>
   <div class="body scrollableList">

<#if (memberships?size > 0)>
   <#list memberships as m>
      <h4>${m.person.firstName} ${m.person.lastName}</h4>
      <span>${m.role}</span>
   </#list>
<#else>
      <h3>${msg("label.noMembers")}</h3>
</#if>
   </div>
</div>