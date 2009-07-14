<#include "include/alfresco-template.ftl" />
<@templateHeader>
   <@link rel="stylesheet" type="text/css" href="${url.context}/components/blog/postlist.css" />
   <@link rel="stylesheet" type="text/css" href="${url.context}/components/blog/postview.css" />
   <@link rel="stylesheet" type="text/css" href="${url.context}/templates/folder-details/folder-details.css" />
   <@script type="text/javascript" src="${page.url.context}/components/blog/blogdiscussions-common.js"></@script>
   <@script type="text/javascript" src="${page.url.context}/components/blog/blog-common.js"></@script>
   <@script type="text/javascript" src="${url.context}/modules/documentlibrary/doclib-actions.js"></@script>
   <@script type="text/javascript" src="${page.url.context}/templates/folder-details/folder-details.js"></@script>
   <#if doclibType != ""><@script type="text/javascript" src="${page.url.context}/templates/folder-details/${doclibType}folder-details.js"></@script></#if>
   <@templateHtmlEditorAssets />
</@>

<@templateBody>
   <div id="alf-hd">
      <@region id="header" scope="global" protected=true />
      <@region id="title" scope="template" protected=true />
      <@region id="navigation" scope="template" protected=true />
   </div>
   <div id="bd">
      <@region id="path" scope="template" protected=true />

      <div class="yui-gb">
         <div class="yui-u first">
            <div class="folder-details-comments">
               <@region id=doclibType + "record-category-metadata-header" scope="template" protected=true />
               <@region id=doclibType + "record-category-metadata" scope="template" protected=true />
               <@region id=doclibType + "record-category-info" scope="template" protected=true />
            </div>
         </div>
         <div class="yui-u">
            <@region id=doclibType + "record-category-disposition" scope="template" protected=true />                        
         </div>
         <div class="yui-u">
            <@region id=doclibType + "record-category-actions" scope="template" protected=true />
            <@region id=doclibType + "record-category-links" scope="template" protected=true />
         </div>
      </div>

   </div>

   <script type="text/javascript">//<![CDATA[
   new ${jsType}().setOptions(
   {
      nodeRef: "${url.args.nodeRef}",
      siteId: "${page.url.templateArgs.site!""}"
   });
   //]]></script>

</@>

<@templateFooter>
   <div id="alf-ft">
      <@region id="footer" scope="global" protected=true />
   </div>
</@>
