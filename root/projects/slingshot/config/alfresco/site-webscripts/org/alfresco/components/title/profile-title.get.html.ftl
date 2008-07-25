<div class="page-title">
   <h1><#if !page.url.templateArgs["userid"]??>${user.properties["firstName"]} ${user.properties["lastName"]} <span class="light">${msg("header.profile")}<#else><span class="light">${msg("header.userprofile")}</#if></span></h1>
</div>