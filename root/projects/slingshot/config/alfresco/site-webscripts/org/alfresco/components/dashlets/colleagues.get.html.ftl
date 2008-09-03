<div class="dashlet">
   <div class="title">${msg("header.colleagues")}</div>
   <div class="body scrollableList">

<#if (memberships?size > 0)>
   <#list memberships as m>
   <div class="detail-list-item  <#if (!m_has_next)>last</#if>">
      <div class="avatar">
         <img src="${url.context}<#if m.avatar??>/proxy/alfresco/api/node/${m.avatar?replace(':/','')}/content/thumbnails/avatar?c=force<#else>/components/images/no-user-photo-64.png</#if>" alt="Avatar" />
      </div>
      <div class="person">
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