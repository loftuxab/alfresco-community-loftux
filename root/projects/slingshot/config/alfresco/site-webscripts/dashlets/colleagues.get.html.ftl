<div class="component">
  <div class="component-title">Site Colleagues</div>
  <div class="component-links">
    <a href="#" onclick="alert('Not implemented');">All</a> |
    <a href="#" onclick="alert('Not implemented');">Find</a> <input type="text" />
  </div>
  <div class="component-list">
<#list persons as person>
      <div>
        <div class="header">
        ${person.name}<br />
        <#if person.role == "CONTRIBUTOR">
        Contributor  
        </#if>
        </div>
        <div class="text">
           Logged in at ${person.loggedIn}<br />
           Last activity at ${person.lastActivity}
        </div>
      </div>
</#list>
  </div>
</div>