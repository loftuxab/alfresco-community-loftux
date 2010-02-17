<#include "include/alfresco-template.ftl" />
<@templateHeader>
   <@link rel="stylesheet" type="text/css" href="${url.context}/templates/rules/folder-rules.css" />
   <@script type="text/javascript" src="${page.url.context}/templates/rules/folder-rules.js"></@script>
</@>

<@templateBody>
   <div id="alf-hd">
      <@region id="header" scope="global" protected=true />
      <@region id=doclibType + "title" scope="template" protected=true />
      <@region id=doclibType + "navigation" scope="template" protected=true />
   </div>
   <div id="bd">
      <@region id=doclibType + "path" scope="template" protected=true />
      <@region id="rules-header" scope="template" protected=true />
      <div class="clear"></div>

      <#if rules?exists>
         <div class="yui-g">
            <div class="yui-g first">
               <div id="inherited-rules-container" class="hidden">
               <@region id="inherited-rules" scope="template" protected=true />
               </div>
               <@region id="folder-rules" scope="template" protected=true />
            </div>
            <div class="yui-g">
               <@region id="rule-details" scope="template" protected=true />
            </div>
         </div>
      <#elseif linkedFolder?exists>
         <@region id="rules-linked" scope="template" protected=true />
      <#else>
         <@region id="rules-none" scope="template" protected=true />
      </#if>
   </div>

   <script type="text/javascript">//<![CDATA[
   new Alfresco.FolderRules().setOptions(
   {
      nodeRef: "${url.args.nodeRef}",
      siteId: "${page.url.templateArgs.site!""}",
      folderName: "${folder.name}",
      pathToFolder: "${folder.path}",
      linkedFolder: <#if linkedFolder?exists>{
         nodeRef: "${linkedFolder.nodeRef}",
         name: "${linkedFolder.name}",
         path: "${linkedFolder.path}"
      }<#else>null</#if>,
      rules: <#if rules?exists>[
         <#list rules as rule>{
            id: "${rule.id}",
            title: "${rule.title}",
            description: "${rule.description}",
            inheritedFolder: <#if rule.inheritedFolder?exists>{
               nodeRef: "${rule.inheritedFolder.nodeRef}",
               name: "${rule.inheritedFolder.name}"
            }<#else>null</#if>,
            disabled: ${rule.disabled?string}
         }<#if rule_has_next>,</#if></#list>
      ]<#else>null</#if>
   });
   //]]></script>

</@>

<@templateFooter>
   <div id="alf-ft">
      <@region id="footer" scope="global" protected=true />
   </div>
</@>
