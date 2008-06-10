<div class="dashlet">
   <div class="title">${msg("header.siteProfile")}</div>
   <div class="body">
      <h3>${msg("text.welcome", profile.title)}</h3>
      <p>${profile.description}</p>
      <#if (sitemanager.person?exists)>
         <p><span class="label">${msg("label.siteAdmin")}</span> ${sitemanager.person.firstName} ${sitemanager.person.lastName}</p>
      </#if>
   </div>
</div>
