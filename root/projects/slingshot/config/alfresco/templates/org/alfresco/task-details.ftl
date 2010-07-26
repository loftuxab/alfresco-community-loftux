<#include "include/alfresco-template.ftl" />
<@templateHeader />
   <@script type="text/javascript" src="${page.url.context}/templates/workflow/task-details.js"></@script>
<@templateBody>
   <div id="alf-hd">
      <@region id="header" scope="global" protected=true/>
      <@region id="title" scope="template" protected=true />
      <@region id="toolbar" scope="template" />
   </div>
   <div id="bd">
      <div class="share-form">
         <@region id="task-header" scope="template" />
         <@region id="task-form" scope="template" />
         <@region id="task-actions" scope="template" />
      </div>
   </div>
   <script type="text/javascript">//<![CDATA[
   new Alfresco.TaskDetails().setOptions(
   {
      taskId: "${url.args.taskId?js_string}"
   });
   //]]></script>
</@>

<@templateFooter>
   <div id="alf-ft">
      <@region id="footer" scope="global" protected=true />
   </div>
</@>
