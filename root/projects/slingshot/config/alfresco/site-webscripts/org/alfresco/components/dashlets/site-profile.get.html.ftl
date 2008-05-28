<div class="dashlet">
   <div class="title">Site profile</div>
   <div class="body">
      <h3>${profile.title}, Welcome</h3>
      <p>${profile.description}</p>
      <#if (sitemanager.person?exists)>
         <p><span class="label">Site admin</span>: ${sitemanager.person.firstName} ${sitemanager.person.lastName}</p>        
      </#if>
   </div>
</div>
