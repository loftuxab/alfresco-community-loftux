<#assign helpPages = config.scoped["HelpPages"]["help-pages"]>
<#assign tutorialLink = helpPages.getChildValue("share-tutorial")!"">
<script type="text/javascript">//<![CDATA[
new Alfresco.UserWelcome("${args.htmlid}");
//]]></script>
<div class="dashlet user-welcome">
   <div class="title">${msg("header.userWelcome")}</div>
   <div class="body">
      <div class="detail-list-item-alt theme-bg-color-2">
         <h4>${msg("header.userDashboard")}</h4>
         <div>${msg("text.userDashboard")}</div>
      </div>
      <div class="detail-list-item">
         <h4>${msg("header.featureTour")}</h4>
         <div>${msg("text.featureTour")}</div>
         <div><a href="${tutorialLink}" class="theme-color-1" target="_blank">${msg("link.featureTour")}</a></div>
      </div>
      <div class="detail-list-item">
         <h4>${msg("header.userProfile")}</h4>
         <div>${msg("text.userProfile")}</div>
         <div><a href="${url.context}/page/user/${user.name?url}/profile" class="theme-color-1">${msg("link.userProfile")}</a></div>
      </div>
      <div class="detail-list-item">
         <h4>${msg("header.customiseDashboard")}</h4>
         <div>${msg("text.customiseDashboard")}</div>
         <div><a href="${url.context}/page/customise-user-dashboard" class="theme-color-1">${msg("link.customiseDashboard")}</a></div>
      </div>
      <div class="detail-list-item last-item">
         <h4>${msg("header.createSite")}</h4>
         <div>${msg("text.createSite")}</div>
         <div><a id="${args.htmlid}-createSite-button" href="#" class="theme-color-1">${msg("link.createSite")}</a></div>
      </div>
      <div class="clear"></div>
   </div>
</div>