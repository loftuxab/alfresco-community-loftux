<#assign categoryConfig = config.scoped["DocumentLibrary"]["categories"]!>
<#if categoryConfig.getChildValue??><#assign evaluateChildFolders = categoryConfig.getChildValue("evaluate-child-folders")!"true"></#if>
<script type="text/javascript">//<![CDATA[
   new Alfresco.DocListCategories("${args.htmlid}").setOptions(
   {
      nodeRef: "alfresco://category/root",
      evaluateChildFolders: ${evaluateChildFolders!"true"}
   }).setMessages(
      ${messages}
   );
//]]></script>
<div class="categoryview filter">
   <h2 id="${args.htmlid}-h2">${msg("header.library")}</h2>
   <div id="${args.htmlid}-treeview" class="category"></div>
</div>