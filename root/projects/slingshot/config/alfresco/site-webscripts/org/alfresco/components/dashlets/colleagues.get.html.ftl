<div class="component">
  <div class="component-title">Site Colleagues</div>
  <div class="component-links">
    <a href="#" onclick="alert('Not implemented');">All</a> |
    <a href="#" onclick="alert('Not implemented');">Find</a> <input type="text" />
  </div>
  <div class="component-list">

     <#if (memberships?size > 0) >
        <#list memberships as m>
           <div>
              <h1>${m.person.firstName} ${m.person.lastName}</h1>
              <h1>${m.role}</h1>
           </div>
        </#list>
     <#else>
        <div>
           <h1>No members in this site yet</h1>
        </div>
     </#if>
  </div>
</div>