<#assign el=args.htmlid?js_string>
<script type="text/javascript">//<![CDATA[
   new Alfresco.StartWorkflow("${el}").setOptions({
      failureMessage: "message.failure",
      submitButtonMessageKey: "button.startWorkflow",
      forwardUrl: Alfresco.util.uriTemplate("userdashboardpage", { userid: Alfresco.constants.USERNAME }),
      selectedItems: "${(page.url.args.selectedItems!"")?js_string}",
      destination: "${(page.url.args.destination!"")?js_string}"
   }).setMessages(
      ${messages}
   );
//]]></script>
<div id="${el}-body" class="form-manager start-workflow">
   <h1>${msg("header")}</h1>
   <div>
      <label for="${el}-workflowDefinitions">${msg("label.workflow")}:</label>
      <select id="${el}-workflowDefinitions" tabindex="0">
         <option>${msg("option.selectWorkflow")}</option>
         <#list workflowDefinitions as workflowDefinition>
         <option value="${workflowDefinition.name?js_string}">${workflowDefinition.title?html}</option>
         </#list>
      </select>
   </div>
</div>
<div id="${el}-workflowFormContainer"></div>
