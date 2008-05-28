<div class="dashlet">
   <div class="title">Site Colleagues</div>
   <div class="toolbar">
      <a href="#">All</a> |
      <span>Find: </span><input type="text" />
   </div>
   <div class="body scrollableList">

<#if (memberships?size > 0)>
   <#list memberships as m>
      <h4>${m.person.firstName} ${m.person.lastName}</h4>
      <span>${m.role}</span>
   </#list>
<#else>
      <h3>No members in this site yet</h3>
</#if>
   </div>
</div>