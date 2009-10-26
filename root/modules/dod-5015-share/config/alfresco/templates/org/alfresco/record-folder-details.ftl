<#include "include/alfresco-template.ftl" />
<#assign doclibType="dod5015-">
<@templateHeader>
   <@link rel="stylesheet" type="text/css" href="${url.context}/templates/folder-details/folder-details.css" />
   <@script type="text/javascript" src="${url.context}/modules/documentlibrary/doclib-actions.js"></@script>
   <@script type="text/javascript" src="${page.url.context}/templates/folder-details/folder-details.js"></@script>   
   <@script type="text/javascript" src="${page.url.context}/templates/folder-details/${doclibType}folder-details.js"></@script>
</@>

<@templateBody>
   <div id="alf-hd">
      <@region id="header" scope="global" protected=true />
      <@region id="title" scope="template" protected=true />
      <@region id="navigation" scope="template" protected=true />
   </div>
   <div id="bd">
      <@region id=doclibType + "actions-common" scope="template" protected=true />
      <@region id=doclibType + "path" scope="template" protected=true />
      <div class="yui-gb">
         <div class="yui-u first">
            <@region id=doclibType + "events" scope="template" protected=true />
         </div>
         <div class="yui-u">
            <@region id=doclibType + "folder-metadata-header" scope="template" protected=true />
            <@region id=doclibType + "folder-metadata" scope="template" protected=true />
         </div>
         <div class="yui-u">
            <@region id=doclibType + "folder-actions" scope="template" protected=true />
            <@region id=doclibType + "folder-links" scope="template" protected=true />
         </div>
      </div>
   </div>
   
   <script type="text/javascript">//<![CDATA[
   new Alfresco.RecordsFolderDetails().setOptions(
   {
      nodeRef: "${url.args.nodeRef}",
      siteId: "${page.url.templateArgs.site!""}"
   });
   //]]></script>

</@>

<@templateFooter>
   <div id="alf-ft">
      <@region id="footer" scope="global" protected=true />
   </div>
</@>
