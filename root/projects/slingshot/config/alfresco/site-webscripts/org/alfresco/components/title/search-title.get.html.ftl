<div class="page-title">	
	<div class="float-left">
		<h1>${msg("header.searchresults")}</h1>
	</div>
	<#if page.url.templateArgs.site??>
	<div class="float-right">
	  <span class="navigation-item back-icon">
	     <a href="${url.context}/page/site/${page.url.templateArgs.site}/dashboard">${msg("header.backlink", profile.title)}</a></span>
	</div>
	</#if>
</div>