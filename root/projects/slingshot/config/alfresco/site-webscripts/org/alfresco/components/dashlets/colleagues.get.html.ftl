<div class="dashlet">
   <div class="title">Site Colleagues</div>
   <div class="menu">
      <a href="#">All</a> |
      <span>Find: </span><input type="text" />
   </div>
   <div class="body scrollableList">

<#if (memberships?size > 0)>
   <#list memberships as m>
      <h2>${m.person.firstName} ${m.person.lastName}</h2>
      <span>${m.role}</span>
   </#list>
<#else>
      <h2>No members in this site yet</h2>
</#if>
   </div>
</div>