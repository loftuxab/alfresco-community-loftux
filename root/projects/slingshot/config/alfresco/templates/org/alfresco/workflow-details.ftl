<#include "include/alfresco-template.ftl" />
<@templateHeader />

<@templateBody>
   <div id="alf-hd">
      <@region id="header" scope="global" protected=true/>
      <#if page.url.args.taskId??>
      <@region id="task-title" scope="template" protected=true />
      <@region id="task-toolbar" scope="template" protected=true/>
      <#else>
      <@region id="workflow-title" scope="template" protected=true />
      <@region id="workflow-toolbar" scope="template" protected=true/>
      </#if>
   </div>
   <div id="bd">
      <div class="share-form">
         <@region id="workflow-details-header" scope="template" />
         <@region id="workflow-form" scope="template" />
         <@region id="workflow-details-actions" scope="template" />
      </div>
   </div>
</@>

<@templateFooter>
   <div id="alf-ft">
      <@region id="footer" scope="global" protected=true />
      <@region id="data-loader" scope="template" />
   </div>
</@>
