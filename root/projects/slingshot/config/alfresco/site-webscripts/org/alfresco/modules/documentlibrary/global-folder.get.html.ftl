<div id="${args.htmlid}-dialog" class="global-folder">
   <div id="${args.htmlid}-title" class="hd"></div>
   <div class="bd">
      <div id="${args.htmlid}-wrapper" class="wrapper">
         <div class="mode flat-button">
            <h3>${msg("header.destination-type")}</h3>
            <div id="${args.htmlid}-modeGroup" class="yui-buttongroup">
               <input type="radio" id="${args.htmlid}-site" name="0" value="${msg("button.site")}" checked="checked" />
               <input type="radio" id="${args.htmlid}-repository" name="1" value="${msg("button.repository")}" />
               <input type="radio" id="${args.htmlid}-userhome" name="2" value="${msg("button.userhome")}" />
            </div>
         </div>
         <div class="site">
            <h3>${msg("header.site-picker")}</h3>
            <div id="${args.htmlid}-sitePicker" class="site-picker"></div>
         </div>
         <div class="container">
            <h3>${msg("header.container-picker")}</h3>
            <div id="${args.htmlid}-containerPicker" class="container-picker"></div>
         </div>
         <div class="path">
            <h3>${msg("header.path-picker")}</h3>
            <div id="${args.htmlid}-treeview" class="treeview"></div>
         </div>
         <div class="bdft">
            <input type="button" id="${args.htmlid}-ok" value="${msg("button.ok")}" />
            <input type="button" id="${args.htmlid}-cancel" value="${msg("button.cancel")}" />
         </div>
      </div>
   </div>
</div>
<#assign treeConfig = config.scoped["DocumentLibrary"]["tree"]!>
<#if treeConfig.getChildValue??>
   <#assign evaluateChildFoldersSite = treeConfig.getChildValue("evaluate-child-folders")!"true">
   <#assign maximumFolderCountSite = treeConfig.getChildValue("maximum-folder-count")!"-1">
</#if>
<#assign treeConfig = config.scoped["RepositoryLibrary"]["tree"]!>
<#if treeConfig.getChildValue??>
   <#assign evaluateChildFoldersRepo = treeConfig.getChildValue("evaluate-child-folders")!"true">
   <#assign maximumFolderCountRepo = treeConfig.getChildValue("maximum-folder-count")!"-1">
</#if>
<script type="text/javascript">//<![CDATA[
   Alfresco.util.addMessages(${messages}, "Alfresco.module.DoclibGlobalFolder");
   Alfresco.util.ComponentManager.get("${args.htmlid}").setOptions(
   {
      evaluateChildFoldersSite: ${evaluateChildFoldersSite!"true"},
      maximumFolderCountSite: ${(maximumFolderCountSite!"-1")},
      evaluateChildFoldersRepo: ${evaluateChildFoldersRepo!"true"},
      maximumFolderCountRepo: ${(maximumFolderCountRepo!"-1")}
   });
//]]></script>
