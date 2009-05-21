<#include "include/alfresco-template.ftl" />
<@templateHeader>
  <@link rel="stylesheet" type="text/css" href="${url.context}/templates/rmreport/rmreport.css" />
  <@script type="text/javascript" src="${url.context}/js/alfresco-resizer.js"></@script>
  <@script type="text/javascript" src="${url.context}/templates/rmreport/rmreport.js"></@script>
</@>

<@templateBody>
   <div id="alf-hd">
      <@region id="header" scope="global" protected=true />
      <@region id="title" scope="template" protected=true />
      <@region id="navigation" scope="template" protected=true />
   </div>
   <div id="bd">
      <div class="yui-t1" id="divReportWrapper">
         <div id="yui-main">
            <div class="yui-b" id="divReportMain">
               <@region id="report" scope="template" protected=true />
            </div>
         </div>
   		<div class="yui-b" id="divReportFilters">
   			<@region id="filters" scope="template" protected=true />
   		</div>
      </div>
   </div>
</@>

<@templateFooter>
   <div id="alf-ft">
      <@region id="footer" scope="global" protected=true />
   </div>
</@>