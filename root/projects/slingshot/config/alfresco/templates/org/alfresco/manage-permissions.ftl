<#include "include/alfresco-template.ftl" />
<@templateHeader>
   <@script type="text/javascript" src="${url.context}/modules/documentlibrary/doclib-actions.js"></@script>
   <@script type="text/javascript" src="${page.url.context}/templates/manage-permissions/template.manage-permissions.js"></@script>   
</@>

<@templateBody>
   <div id="alf-hd">
      <@region id="header" scope="global" protected=true />
      <@region id=doclibType + "title" scope="template" protected=true />
      <@region id=doclibType + "navigation" scope="template" protected=true />
   </div>
   <div id="bd">
      <@region id=doclibType + "path" scope="template" protected=true />
      <@region id="manage-permissions" scope="template" protected=true />
   </div>

   <script type="text/javascript">//<![CDATA[
   new Alfresco.template.ManagePermissions().setOptions(
   {
      nodeRef: new Alfresco.util.NodeRef("${url.args.nodeRef}"),
      siteId: "${page.url.templateArgs.site!""}",
      rootNode: new Alfresco.util.NodeRef("${rootNode}")
   });
   //]]></script>
</@>

<@templateFooter>
   <div id="alf-ft">
      <@region id="footer" scope="global" protected=true />
   </div>
</@>
