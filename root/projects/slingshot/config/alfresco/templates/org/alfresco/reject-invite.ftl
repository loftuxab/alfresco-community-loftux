<#include "include/alfresco-template.ftl" />
<@templateHeader>
</@>

<@templateBody>
   <div id="hd">
      <@region id="header" scope="global" protected=true />
   </div>
   <div id="bd">
      <@region id="reject-invite" scope="template" protected=true />
   </div>
</@>

<@templateFooter>
   <div id="ft">
      <@region id="footer" scope="global" protected=true />
   </div>
</@>