<#-- title -->
<div class="page-title">
   <div class="title">
      <h1>${msg("header.title")}</h1>
   </div>
</div>

<#-- Body -->
<div class="accept-invite-body">
<#if (! doRedirect)>
   <h1>Processing invite acceptance failed</h1>

   <p>Unfortunately your invite acceptance could not be registered</p>

   <p>Most probably this happened because the inviter canceled the invitation.</p>
<#else>
<#-- redirect logic -->
<script type="text/javascript">//<![CDATA[
   window.location = "${page.url.context}/page/site/${siteShortName}/dashboard";
//]]></script>

   <h1>Acceptance registered. Redirecting...</h1>
   
   <p>You should automatically be redirected to the site dashboard page.
   Click following link if this is not the case:</p>
   
   <a href="${redirectUrl}">${redirectUrl}</a>
</#if>
</div>
