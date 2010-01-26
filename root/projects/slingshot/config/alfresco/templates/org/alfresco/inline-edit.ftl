<#include "include/alfresco-template.ftl" />
<@templateHeader>
   <@link rel="stylesheet" type="text/css" href="${url.context}/templates/create-content/create-content.css" />
</@>

<@templateBody>
   <div id="alf-hd">
      <@region id="header" scope="global" protected=true />
      <@region id=doclibType + "title" scope="template" protected=true />
      <@region id=doclibType + "navigation" scope="template" protected=true />
   </div>
   <div id="bd">
      <div class="metadata">
         <@region id=doclibType + "inline-edit-mgr" scope="template" protected=true />
         <@region id=doclibType + "inline-edit" scope="template" protected=true />
      </div>
   </div>
   
</@>

<@templateFooter>
   <div id="alf-ft">
      <@region id="footer" scope="global" protected=true />
   </div>
</@>
