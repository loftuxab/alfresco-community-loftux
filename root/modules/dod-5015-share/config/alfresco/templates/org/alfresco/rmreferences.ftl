<#include "include/alfresco-template.ftl" />
<@templateHeader>
  <@link rel="stylesheet" type="text/css" href="${url.context}/templates/rmreferences/rmreferences.css" />
  <@link rel="stylesheet" type="text/css" href="${url.context}/components/rmreferences/rmreferences.css" />  
  <@script type="text/javascript" src="${url.context}/js/event-delegator.js"></@script>
  <@script type="text/javascript" src="${url.context}/components/rmreferences/rmreferences.js"></@script>
</@>

<@templateBody>
   <div id="alf-hd">
      <@region id="header" scope="global" protected=true />
      <@region id="title" scope="template" protected=true />
      <@region id="navigation" scope="template" protected=true />
   </div>
   <div id="bd">
      <div class="yui-t1" id="manageReferences">
         <div id="yui-main">
               <@region id="references" scope="template" protected=true />
         </div>
      </div>
   </div>
</@>

<@templateFooter>
   <div id="alf-ft">
      <@region id="footer" scope="global" protected=true />
   </div>
</@>