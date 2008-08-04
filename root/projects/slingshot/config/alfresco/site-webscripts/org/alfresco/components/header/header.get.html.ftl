<#assign searchTypeLabel><#if page.url.templateArgs.site??>Search ${page.url.templateArgs.site} site<#else>Search all sites</#if></#assign>
<#assign siteActive><#if page.url.templateArgs.site??>true<#else>false</#if></#assign>

<script type="text/javascript">//<![CDATA[
   var thisHeader = new Alfresco.Header("${args.htmlid}").setOptions({
      siteId: "${page.url.templateArgs.site!""}",
      searchType: "${page.url.templateArgs.site!'all'}" // default search type
   }).setMessages(
      ${messages}
   );
//]]></script>

<div class="header">
   <div class="logo-wrapper">
      <div class="logo">
         <img src="${url.context}/themes/${theme}/images/app-logo.png" alt="Alfresco Share" />
      </div>
   </div>
   <div class="menu-wrapper">
      <div class="personal-menu">
         <span class="menu-item-icon my-dashboard"><a href="${url.context}/page/user/${user.name}/dashboard">${msg("link.myDashboard")}</a></span>
         <span class="menu-item-icon my-profile"><a href="${url.context}/page/user/${user.name}/profile">${msg("link.myProfile")}</a></span>
         <span class="menu-item-icon sites"><a href="${url.context}/page/site-finder">${msg("link.sites")}</a></span>
      </div>

      <div class="util-menu" id="${args.htmlid}-searchcontainer">
         <span class="menu-item"><a href="#">${msg("link.help")}</a></span>
         <span class="menu-item-separator">|</span>
         <span class="menu-item"><a href="${url.context}/logout">${msg("link.logout")} (${user.name})</a></span>
         <span class="menu-item-separator">|</span>
         <span class="menu-item">
            <span class="search-container">
               <input type="text" class="search-tinput" name="${args.htmlid}-searchtext" id="${args.htmlid}-searchtext" value="" />
               <!-- ><span id="${args.htmlid}-search-sbutton" class="search-icon">&nbsp;</span> -->
               <span id="${args.htmlid}-search-tbutton" class="search-site-icon">&nbsp;</span>
            </span>
         </span>
      </div>
   </div>

	<div id="${args.htmlid}-searchtogglemenu" class="searchtoggle">
	<div class="bd">
		<ul>
			<li class="searchtoggleitem">
				<a class="searchtoggleitemlabel<#if siteActive == 'false'> disabled</#if>" 
					href="<#if siteActive == 'false'>#<#else>javascript:thisHeader.doToggleSearchType('site')</#if>">${msg("header.search.searchsite", page.url.templateArgs.site!"")}
				</a>
			</li>
			<li class="searchtoggleitem"><a class="searchtoggleitemlabel" href="javascript:thisHeader.doToggleSearchType('all')">${msg("header.search.searchall")}</a></li>
		</ul>            
	</div>
	</div>	

</div>

<div class="clear"></div>