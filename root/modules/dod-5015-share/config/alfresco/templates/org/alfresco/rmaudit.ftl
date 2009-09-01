<#include "include/alfresco-template.ftl" />
<@templateHeader>
<#-- <@link rel="stylesheet" type="text/css" href="${page.url.context}/yui/datatable/assets/datatable.css" />
<@link rel="stylesheet" type="text/css" href="${page.url.context}/components/console/rm-audit.css" />  
<@script type="text/javascript" src="${url.context}/js/event-delegator.js"></@script>
<@script type="text/javascript" src="${page.url.context}/components/console/rm-audit.js"></@script> -->
</@>

<@templateBody>
   <div id="bd">
      <div id="audit-popup-log" class="audit-popup-log">
         <div id="yui-main">
               <@region id="rmaudit" scope="template" protected=true />
         </div>
      </div>
   </div>
</@>

<@templateFooter>
   <div id="alf-ft">
      <@region id="footer" scope="global" protected=true />
   </div>
</@>