<div class="component">
  <div class="component-title">Site Colleagues</div>
  <div class="component-links">
    <nobr>
    <a href="javascript:showCreateSiteDialog();">All</a> |
    <a href="javascript:showCreateSiteDialog();">Find</a> <input type="text">
    </nobr>
  </div>
  <div class="component-list">
<#list profiles as profile>
      <div>
        <div class="header">
        ${profile.name}<br>
        <#if profile.role == "CONTRIBUTOR">
        Contributor  
        </#if>
        </div>
        <div class="text">
           Logged in at ${profile.loggedIn}<br>
           Last activity at ${profile.lastActivity}
        </div>
      </div>
</#list>
  </div>
</div>