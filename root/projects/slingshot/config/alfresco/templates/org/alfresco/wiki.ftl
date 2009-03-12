<#include "include/alfresco-template.ftl" />
<@templateHeader>
   <@link rel="stylesheet" type="text/css" href="${url.context}/templates/wiki/wiki.css" />
   <@script type="text/javascript" src="${url.context}/js/alfresco-resizer.js"></@script>
   <@script type="text/javascript" src="${url.context}/templates/wiki/wiki.js"></@script>
   <@templateHtmlEditorAssets />     
</@>

<@templateBody>
   <div id="hd">
      <@region id="header" scope="global" protected=true />
      <@region id="title" scope="template" protected=true />
      <@region id="navigation" scope="template" protected=true />
   </div>
   <div id="bd">
      <div class="yui-t1" id="divTopicListWrapper">
         <div id="yui-main">
            <div class="yui-b" id="divTopicListTopics">
               <@region id="toolbar" scope="template" protected=true />
               <@region id="pagelist" scope="template" protected=true />
            </div>
         </div>
         <div class="yui-b" id="divTopicListFilters">
            <@region id="filter" scope="template" protected=true />
            <@region id="tags" scope="template" protected=true />
         </div>
      </div>

	</div>
</@>

<@templateFooter>
   <div id="ft">
      <@region id="footer" scope="global" protected=true />
   </div>
</@>