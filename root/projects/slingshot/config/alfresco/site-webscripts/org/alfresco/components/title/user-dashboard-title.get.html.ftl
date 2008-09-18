<#assign activePage = page.url.templateArgs.pageid!"customise-user-dashboard">
<div class="page-title"><!-- ${activePage} -->
   <div class="title">
      <h1>${user.properties["firstName"]?html} <#if user.properties["lastName"]??>${user.properties["lastName"]?html}</#if> <span class="light">${msg("header.dashboard")}</span></h1>
   </div>
   <div class="links title-button">
      <#assign linkClass><#if "customise-user-dashboard" == activePage>class="active-page"</#if></#assign>
      <span class="yui-button yui-link-button">
         <span class="first-child">
            <a href="${url.context}/page/customise-user-dashboard" ${linkClass}>${msg("link.customiseDashboard")}</a>
         </span>
      </span>

   </div>
</div>