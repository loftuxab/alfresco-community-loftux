<div class="dashlet">
   <div class="title">${msg("header.siteProfile")}</div>
   <div class="body">
      <h3><#if (profile.title != "")>${msg("text.welcome", profile.title)}<#else>${msg("text.welcome", profile.shortName)}</#if></h3>
      <p>${profile.description}</p>
      <#if (sitemanager.person?exists)>
         <p>
            <span class="label">${msg("label.siteAdmin")}</span>
            <a href="${url.context}/page/user/${sitemanager.person.userName}/dashboard">${sitemanager.person.firstName} ${sitemanager.person.lastName}</a>
         </p>
      </#if>
      <p><span class="label">${msg("label.visibility")}</span>&nbsp;
      <#if profile.isPublic?? && profile.isPublic>
         ${msg("text.public")}
      <#else>
         ${msg("text.private")}
      </#if>
      </p>
   </div>
</div>