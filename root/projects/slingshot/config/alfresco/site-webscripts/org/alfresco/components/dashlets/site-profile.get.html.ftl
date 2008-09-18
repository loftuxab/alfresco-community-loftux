<div class="dashlet">
   <div class="title">${msg("header.siteProfile")}</div>
   <div class="body">
      <div>
         <h3><#if (profile.title != "")>${msg("text.welcome", profile.title)?html}<#else>${msg("text.welcome", profile.shortName)?html}</#if></h3>
<#if (profile.description != "")>
         <p>${profile.description?html}</p>
</#if>
<#if (sitemanagers?exists && sitemanagers?size &gt; 0)>
         <p>
            <span class="label">${msg("label.siteAdmin")}</span>
   <#list sitemanagers as sitemanager>
            <a href="${url.context}/page/user/${sitemanager.person.userName}/dashboard">${sitemanager.person.firstName?html}<#if sitemanager.person.lastName != ""> ${sitemanager.person.lastName?html}</#if></a><#if sitemanager_has_next>, </#if>
   </#list>
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
</div>