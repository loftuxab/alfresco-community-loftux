<#include "include/alfresco-template.ftl" />
<#include "include/documentlibrary.inc.ftl" />
<@templateHeader>
   <@link rel="stylesheet" type="text/css" href="${url.context}/templates/documentlibrary/documentlibrary.css" />
   <@documentLibraryJS />
   <@script type="text/javascript" src="${url.context}/js/alfresco-resizer.js"></@script>
   <@script type="text/javascript" src="${url.context}/templates/documentlibrary/documentlibrary.js"></@script>
   <@script type="text/javascript" src="${url.context}/modules/documentlibrary/doclib-actions.js"></@script>
</@>

<@templateBody>
   <div id="alf-hd">
      <@region id=appType + "header" scope="global" protected=true />
      <@region id=appType + "title" scope="template" protected=true />
   </div>
   <div id="bd">
      <@region id="actions-common" scope="template" protected=true />
      <@region id="actions" scope="template" protected=true />
      <div class="yui-t1">
         <div id="yui-main">
            <div class="yui-b" id="divDocLibraryDocs">
               <@region id="toolbar" scope="template" protected=true />
               <@region id="documentlist" scope="template" protected=true />
            </div>
         </div>
         <div class="yui-b" id="divDocLibraryFilters">
            <@region id="filter" scope="template" protected=true />
            <@region id="tree" scope="template" protected=true />
            <@region id="categories" scope="template" protected=true />
            <@region id="tags" scope="template" protected=true />
         </div>
      </div>

      <@region id="html-upload" scope="template" protected=true />
      <@region id="flash-upload" scope="template" protected=true />
      <@region id="file-upload" scope="template" protected=true />
   </div>
</@>

<@templateFooter>
   <div id="alf-ft">
      <@region id="footer" scope="global" protected=true />
   </div>
</@>