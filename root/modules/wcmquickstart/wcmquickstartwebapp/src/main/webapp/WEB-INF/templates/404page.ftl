<#include "/common/page.ftl"/>

<#assign title="${webSite.title!context.page.id} - Page Not Found"/>

<@templateBody>  
  <div class="error">
    <h2>${webSite.page404.title}</h2>
    <@streamasset asset=webSite.page404/>
  </div>
</@>