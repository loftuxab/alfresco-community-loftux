<#include "include/alfresco-template.ftl" />
<@templateHeader>
   <@link rel="stylesheet" type="text/css" href="${url.context}/templates/links/links.css" />
   <@script type="text/javascript" src="${url.context}/js/alfresco-resizer.js"></@script>
   <@script type="text/javascript" src="${url.context}/templates/links/links.js"></@script>
   <@script type="text/javascript" src="${page.url.context}/components/links/linksdiscuss-common.js"></@script>
</@>

<@templateBody>
   <div id="alf-hd">
      <@region id="header" scope="global" protected=true />
      <@region id="title" scope="template" protected=true />
      <@region id="navigation" scope="template" protected=true />
   </div>

   <div id="bd">
   	<div class="yui-t1" id="divLinksWrapper">
   		<div id="yui-main">
   			<div class="yui-b" id="divLinkList">
   				<@region id="links" scope="template" />
   			</div>
   		</div>
   		<div class="yui-b" id="divLinkFilters">
   			<@region id="filters" scope="template" protected=true />
   			<@region id="tags" scope="template" protected=true />
   		</div>
   	</div>
   </div>	
</@>

<@templateFooter>
   <div id="alf-ft">
      <@region id="footer" scope="global" protected=true />
   </div>
</@>