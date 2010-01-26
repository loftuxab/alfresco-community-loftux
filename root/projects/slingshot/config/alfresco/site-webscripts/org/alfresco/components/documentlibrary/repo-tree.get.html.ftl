<#assign treeConfig = config.scoped["RepositoryLibrary"]["tree"]!>
<#if treeConfig.getChildValue??><#assign evaluateChildFolders = treeConfig.getChildValue("evaluate-child-folders")!"false"></#if>
<script type="text/javascript">//<![CDATA[
   new Alfresco.RepositoryDocListTree("${args.htmlid}").setOptions(
   {
      nodeRef: new Alfresco.util.NodeRef("${rootNode}"),
      evaluateChildFolders: ${evaluateChildFolders!"false"}
   }).setMessages(
      ${messages}
   );
//]]></script>
<div class="treeview filter">
   <h2 id="${args.htmlid}-h2">${msg("header.library")}</h2>
   <div id="${args.htmlid}-treeview" class="tree"></div>
</div>