<#include "include/alfresco-template.ftl" />
<@templateHeader>
   <!-- General Links Assets -->
   <@script type="text/javascript" src="${page.url.context}/components/links/linksdiscuss-common.js"></@script>
</@>

<@templateBody>
   <div id="hd">
      <@region id="header" scope="global" protected=true />
      <@region id="title" scope="template" protected=true />
      <@region id="navigation" scope="template" protected=true />
   </div>

<div id="bd">
	<div class="links-main">
		<div class="links-right-panel">
			<div id="divLinkList" style="margin-left : 153px; width:auto">
				<@region id="links" scope="template" />
			</div>
		</div>
		<div id="divLinkFilters">
			<@region id="filters" scope="template" protected=true />
			<@region id="tags" scope="template" protected=true />
		</div>
	</div>
</div>	
</@>

<@templateFooter>
   <div id="ft">
      <@region id="footer" scope="global" protected=true />
   </div>
</@>