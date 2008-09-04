<div class="dashlet site-welcome">
   <div class="title">${msg("header.userWelcome")}</div>
   <div class="body">
      <div class="detail-list-item">
         <h4>${msg("header.siteDashboard")}</h4>
         <div>${msg("text.siteDashboard")}</div>
      </div>

      <#if userIsSiteManager>
         <div class="detail-list-item">
            <h4>${msg("header.joinSite")}</h4>
            <div>${msg("text.joinSite")}</div>
            <div><a href="${url.context}/page/site/${page.url.templateArgs.site!}/invite">${msg("link.joinSite")}</a></div>
         </div>
         <div class="detail-list-item">
            <h4>${msg("header.customiseDashboard")}</h4>
            <div>${msg("text.customiseDashboard")}</div>
            <div><a href="${url.context}/page/site/${page.url.templateArgs.site!}/customise-site-dashboard">${msg("link.customiseDashboard")}</a></div>
         </div>
         <div class="detail-list-item last">
            <h4>${msg("header.customiseSite")}</h4>
            <div>${msg("text.customiseSite")}</div>
            <div><a href="${url.context}/page/site/${page.url.templateArgs.site!}/customise-site">${msg("link.customiseSite")}</a></div>
         </div>         
      <#else>
         <div class="detail-list-item">
            <h4>${msg("header.siteMembers")}</h4>
            <div>${msg("text.siteMembers")}</div>
            <div><a href="${url.context}/page/site/${page.url.templateArgs.site!}/site-members">${msg("link.siteMembers")}</a></div>
         </div>
         <div class="detail-list-item">
            <h4>${msg("header.onlineHelp")}</h4>
            <div>${msg("text.onlineHelp")}</div>
            <div><a href="http://www.alfresco.com/help/3/EUHelp">${msg("link.onlineHelp")}</a></div>
         </div>
         <div class="detail-list-item last">
            <h4>${msg("header.featureTour")}</h4>
            <div>${msg("text.featureTour")}</div>
            <div><a href="http://www.alfresco.com/help/3/Tutorial">${msg("link.featureTour")}</a></div>
         </div>
      </#if>
   </div>                                                    
</div>
