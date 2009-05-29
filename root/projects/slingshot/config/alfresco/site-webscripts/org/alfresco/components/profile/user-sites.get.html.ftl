<#assign el=args.htmlid>
<div id="${el}-body" class="profile">
   <div id="${el}-readview">
      <div class="viewcolumn">
         <div class="header-bar">${msg("label.sites")}</div>
         <#if (numSites >0)>
         <ul class="sites">
         <#list sites as site>
            <li>
               <a href="${url.context}/page/site/${site.shortName}/dashboard"><img src="${url.context}/components/site-finder/images/site-64.png"/></a>
               <p><a href="${url.context}/page/site/${site.shortName}/dashboard">${site.title}</a>
               ${site.description}</p>
            </li>
         </#list>
         </ul>
         <#else>
         <p>${msg("label.noSiteMemberships")}</p>
         </#if>         
      </div>
   </div>
</div>