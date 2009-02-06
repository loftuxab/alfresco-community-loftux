<#include "include/alfresco-template.ftl" />
<@templateHeader>
   <@script type="text/javascript" src="${url.context}/templates/discussions/discussions-topicview.js"></@script>
   <!-- General Discussion Assets -->
   <@script type="text/javascript" src="${page.url.context}/components/blog/blogdiscussions-common.js"></@script>
   <@script type="text/javascript" src="${page.url.context}/components/discussions/discussions-common.js"></@script>
</@>

<@templateBody>
   <div id="hd">
      <@region id="header" scope="global" protected=true />
      <@region id="title" scope="template" protected=true />
      <@region id="navigation" scope="template" protected=true />
   </div>
   <div id="bd">
      <@region id="toolbar" scope="template" protected=true />
      <@region id="topic" scope="template" protected=true />
      <@region id="replies" scope="template" protected=true />
   </div>
</@>

<@templateFooter>
   <div id="ft">
      <@region id="footer" scope="global" protected=true />
   </div>
</@>
