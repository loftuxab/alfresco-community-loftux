<#assign el=args.htmlid>
<script type="text/javascript">//<![CDATA[
   new Alfresco.StartWorkflow("${el}").setOptions(
   {
      nodeRefs: [new Alfresco.util.NodeRef("${page.url.args.nodeRef!""}")],
      siteId: "${page.url.templateArgs.site!""}"
   }).setMessages(
      ${messages}
   );
//]]></script>
<div id="${el}-body" class="start-workflow">
   <h1>${msg("header")}</h1>
   <label for="${el}-workflowDefinitions">${msg("label.workflow")}:</label>
   <select id="${el}-workflowDefinitions" name="taskId" tabindex="0">
      <#list workflowDefinitions as workflowDefinition>
         <option value="${workflowDefinition.startTaskId}">${workflowDefinition.title?html}</option>
      </#list>
   </select>
   <div id="${el}-workflowFormContainer"></div>
</div>
