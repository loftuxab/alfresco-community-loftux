<#assign helpPages = config.scoped["HelpPages"]["help-pages"]>
<#assign helpLink = helpPages.getChildValue("share-help")!"">
<#assign siteActive><#if page.url.templateArgs.site??>true<#else>false</#if></#assign>
<#assign el=args.htmlid>
<#if !user.isGuest>
<script type="text/javascript">//<![CDATA[
   var thisHeader = new Alfresco.Header("${el}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}",
      siteTitle: "${siteTitle?js_string}",
      searchType: "${page.url.templateArgs.site!'all'}", // default search scope
      favouriteSites: {<#list favouriteSites as site>'${site.shortName}': '${site.title?js_string}'<#if site_has_next>,</#if></#list>},
      minSearchTermLength: ${args.minSearchTermLength!config.scoped['Search']['search'].getChildValue('min-search-term-length')}
   }).setMessages(
      ${messages}
   );
   Alfresco.util.createTwister.collapsed = "${collapsedTwisters?js_string}"
//]]></script>
</#if>
<#assign logo=msg("header.logo")><#if logo="header.logo"><#assign logo="app-logo.png"></#if>
<div class="header">
   <div class="logo-wrapper">
      <div class="logo">
         <a href="#" onclick="thisHeader.showAboutShare(); return false;"><img src="${url.context}/themes/${theme}/images/${logo}" alt="Alfresco Share" /></a>
      </div>
   </div>

   <div class="menu-wrapper">
      <#if !user.isGuest>
      <div class="personal-menu">   
         <span class="menu-item-icon my-dashboard"><a href="${url.context}/page/user/${user.name?url}/dashboard">${msg("link.myDashboard")}</a></span>
         <span class="menu-item-icon my-profile"><a href="${url.context}/page/user/profile">${msg("link.myProfile")}</a></span>
         <span id="${el}-sites-linkMenuButton" class="link-menu-button">
            <span class="menu-item-icon sites link-menu-button-link"><a href="${url.context}/page/site-finder">${msg("link.sites")}</a></span>
            <input id="${el}-sites" type="button"/>
         </span>
         <span class="menu-item-icon people"><a href="${url.context}/page/people-finder">${msg("link.people")}</a></span>
         <#if repoLibraryVisible><span class="menu-item-icon repository"><a href="${url.context}/page/repository">${msg("link.repository")}</a></span></#if>
      </div>
      </#if>

      <div class="util-menu" id="${el}-searchcontainer">
         <#if user.isAdmin>
         <span class="menu-item"><a href="${url.context}/page/console/admin-console/">${msg("link.console")}</a></span>
         <span class="menu-item-separator">&nbsp;</span>
         </#if>
         <span class="menu-item"><a href="${helpLink}" rel="_blank">${msg("link.help")}</a></span>
         <#if !user.isGuest>
         <span class="menu-item-separator">&nbsp;</span>
         <#if !context.externalAuthentication>
         <span class="menu-item"><a href="${url.context}/page/dologout" title="${msg("link.logout.tooltip", user.name?html)}">${msg("link.logout")}</a></span>
         <span class="menu-item-separator">&nbsp;</span>
         </#if>
         <span class="menu-item">
            <span class="search-container link-menu-button">
               <input type="text" class="search-tinput" name="${el}-searchtext" id="${el}-searchtext" value="" maxlength="1024" />
               <input id="${el}-search-tbutton" type="button"/>
            </span>
         </span>
         </#if>
      </div>
   </div>

   <div id="${el}-sites-menu" class="yui-overlay menu-with-icons">
      <div class="bd">
         <#assign favDisplay><#if favouriteSites?size != 0>block<#else>none</#if></#assign>
         <div id="${el}-favouritesContainer" class="favourite-sites" style="display: ${favDisplay}">
            <div>
               ${msg("header.site.favouriteSites")}
            </div>
         </div>
         <ul id="${el}-favouriteSites" class="favourite-sites-list separator" style="display: ${favDisplay}">
         <#if favouriteSites?size != 0>
            <#list favouriteSites as site>
            <li>
               <a href="${url.context}/page/site/${site.shortName}/dashboard">${site.title?html}</a>
            </li>
            </#list>
         <#else><li></li></#if>
         </ul>
         <#assign addFavDisplay><#if (page.url.templateArgs.site?? && !currentSiteIsFav)>block<#else>none</#if></#assign>
         <ul id="${el}-addFavourite" class="add-favourite-menuitem separator" style="display: ${addFavDisplay}">
            <li style="display: ${addFavDisplay}">
               <a href="#" onclick="thisHeader.addAsFavourite(); return false;">${msg("link.add-favourite", siteTitle?html)}</a>
            </li>
         </ul>
         <ul class="site-finder-menuitem<#if !user.isGuest> separator</#if>">
            <li>
               <a href="${url.context}/page/site-finder">${msg("header.sites.findSites")}</a>
            </li>
         </ul>
         <#if !user.isGuest>
         <ul class="create-site-menuitem">
            <li>
               <a href="#" onclick="thisHeader.showCreateSite(); return false;">${msg("header.sites.createSite")}</a>
            </li>
         </ul>
         </#if>
      </div>
   </div>

   <#if !user.isGuest>
   <div id="${el}-adv-search-menu" class="hidden">
      <div class="bd">
         <ul class="last">
            <li>
               <a href="#">${msg("header.search.advancedsearch")}</a>
            </li>
         </ul>            
      </div>
   </div>	
   </#if>
</div>
<script type="text/javascript">//<![CDATA[
(function()
{
   Alfresco.util.relToTarget("${el}");
})();
//]]></script>