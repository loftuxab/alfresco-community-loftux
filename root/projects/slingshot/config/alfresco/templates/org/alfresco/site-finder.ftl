<#include "include/alfresco-template.ftl" />
<@templateHeader>
</@>

<@templateBody>
   <div id="hd">
      <@region id="header" scope="global" protected=true/>
      <@region id="title" scope="template" protected=true />
   </div>
   <div id="bd">
      <@region id="site-finder" scope="template" />
   </div>
</@>

<@templateFooter>
   <div id="ft">
      <@region id="footer" scope="global" protected=true />
   </div>
</@>