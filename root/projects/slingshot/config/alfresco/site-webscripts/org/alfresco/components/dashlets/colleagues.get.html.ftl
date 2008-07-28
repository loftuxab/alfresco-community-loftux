<div class="dashlet">
   <div class="title">${msg("header.colleagues")}</div>
   <div class="body scrollableList">

<#if (memberships?size > 0)>
   <#list memberships as m>
   <div class="detail-list-item">
      <div>
         <h4><a href="${url.context}/page/user/${m.person.userName}/profile">${m.person.firstName} ${m.person.lastName}</a></h4>
         <span>${m.role}</span>
      </div>
   </div>
   </#list>
<#else>
      <h3>${msg("label.noMembers")}</h3>
</#if>
   </div>
</div>