<#include "include/alfresco-template.ftl" />
<@templateHeader />

<@templateBody>
   <div id="alf-hd">
      <@region id="header" scope="global" protected=true/>
      <@region id="title" scope="template" protected=true />
      <@region id="toolbar" scope="template" />
   </div>
   <div id="bd">
      <div class="share-form">
         <@region id="workflow-details-header" scope="template" />
         <@region id="workflow-form" scope="template" />
      </div>
   </div>
</@>

<@templateFooter>
   <div id="alf-ft">
      <@region id="footer" scope="global" protected=true />
   </div>
</@>
