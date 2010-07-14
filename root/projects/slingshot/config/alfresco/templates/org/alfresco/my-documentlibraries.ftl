<#include "include/alfresco-template.ftl" />
<@templateHeader>
</@>

<@templateBody>
   <div id="alf-hd"></div>
   <div id="bd">
      <@region id="my-doclibs" scope="template" />
   </div>
</@>

<@templateFooter>
   <div id="alf-ft">
      <@region id="footer" scope="global" protected=true />
   </div>
</@>