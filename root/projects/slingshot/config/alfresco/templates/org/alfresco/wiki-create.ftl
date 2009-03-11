<#include "include/alfresco-template.ftl" />
<@templateHeader>
  <@link rel="stylesheet" type="text/css" href="${url.context}/templates/wiki/wiki.css" />
   <@template.htmlEditorAssets />       
</@>

<@templateBody>
   <div id="hd">
      <@region id="header" scope="global" protected=true />
      <@region id="title" scope="template" protected=true />
      <@region id="navigation" scope="template" protected=true />
   </div>
   <div id="bd">
		<@region id="createform" scope="template" protected=true />
 	</div>
</@>

<@templateFooter>
   <div id="ft">
      <@region id="footer" scope="global" protected=true />
   </div>
</@>