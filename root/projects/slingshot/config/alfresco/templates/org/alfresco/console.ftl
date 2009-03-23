<#include "include/alfresco-template.ftl" />
<@templateHeader>
   <@link rel="stylesheet" type="text/css" href="${url.context}/templates/console/console.css" />
   <@script type="text/javascript" src="${url.context}/js/alfresco-resizer.js"></@script>
   <@script type="text/javascript" src="${url.context}/templates/console/console.js"></@script>
</@>

<@templateBody>
   <div id="alf-hd">
      <@region id="header" scope="global" protected=true />
      <@region id="title" scope="page" protected=true />
   </div>
   
   <div id="bd">
   	<div class="yui-t1" id="divConsoleWrapper">
   		<div id="yui-main">
   			<div class="yui-b" id="divConsoleMain">
   				<@region id="plugin" scope="page" protected=true />
   			</div>
   		</div>
   		<div class="yui-b" id="divConsoleTools">
   			<@region id="tools" scope="template" protected=true />
   		</div>
   	</div>
   </div>	
</@>

<@templateFooter>
   <div id="alf-ft">
      <@region id="footer" scope="global" protected=true />
   </div>
</@>