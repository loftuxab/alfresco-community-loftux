<#include "config/rule-config.lib.ftl" />
<#assign el=args.htmlid>
<script type="text/javascript">//<![CDATA[
   new Alfresco.RuleEdit("${el}").setOptions(
   {
      folderNodeRef: new Alfresco.util.NodeRef("${page.url.args.folderNodeRef!""}"),
      rule: ${rule},
      siteId: "${page.url.templateArgs.site!""}"
   }).setMessages(
      ${messages}
   );
//]]></script>
<div id="${el}-body" class="rule-edit <#if (page.url.args.ruleNodeRef?exists)>edit-mode<#else>create-mode</#if>">
   <form id="${el}-rule-form" method="" action="">
      <input id="${el}-id" type="hidden" name="id" value=""/> 
      <input type="hidden" name="action.actionDefinitionName" value="composite-action"/>
      
      <h1 class="edit-header">${msg("header.editRule")}</h1>
      <h1 class="create-header">${msg("header.newRule")}</h1>

      <div class="caption">
         <span class="mandatory-indicator">*</span> ${msg("form.required.fields")}
      </div>

      <div class="rule-form">

         <h2>${msg("header.general")}</h2>
         <hr/>

         <div class="form-field title">
            <label for="${el}-title">
               ${msg("label.title")}:
               <span class="mandatory-indicator">*</span>
            </label>
            <input id="${el}-title" type="text" title="${msg("label.title")}" value="" name="title"/>
         </div>
         <div class="form-field description">
            <label for="${el}-description">
               ${msg("label.description")}:
            </label>
            <input id="${el}-description" type="text" title="${msg("label.description")}" value="" name="description"/>
         </div>

         <h2>${msg("header.defineRule")}</h2>
         <hr/>

         <div id="${el}-configsMessage">${msg("message.loading")}</div>
         <div id="${el}-configsContainer" class="hidden">
            <div id="${el}-ruleConfigType"></div>
            <div class="configuration-separator">&nbsp;</div>
            <div id="${el}-ruleConfigIfCondition" class="if"></div>
            <div id="${el}-ruleConfigUnlessCondition" class="unless"></div>
            <div class="configuration-separator">&nbsp;</div>
            <div id="${el}-ruleConfigAction"></div>
         </div>

         <h2>${msg("header.otherOptions")}</h2>
         <hr/>
         
         <div class="form-field disabled">
            <input id="${el}-disabled" type="checkbox" title="${msg("label.disabled")}" name="disabled" value="true"/>
            <label for="${el}-disabled">${msg("label.disabled")}</label>
         </div>
         <div class="form-field applyToChildren">
            <input id="${el}-applyToChildren" type="checkbox" title="${msg("label.applyToChildren")}" name="applyToChildren" value="true"/>
            <label for="${el}-applyToChildren">${msg("label.applyToChildren")}</label>
         </div>
         <div class="form-field executeAsynchronously">
            <input id="${el}-executeAsynchronously" type="checkbox" title="${msg("label.executeAsynchronously")}" name="executeAsynchronously" value="true"/>
            <label for="${el}-executeAsynchronously">${msg("label.executeAsynchronously")}</label>
         </div>
         <div class="form-field scriptLocation">
            <label for="${el}-scriptLocation">${msg("label.scriptLocation")}</label>
            <select id="${el}-scriptLocation" name="action.compensatingAction.parameterValues.scriptLocation" title="${msg("label.scriptLocation")}">
               <#list scripts as script>
               <option value="${script.value}">
                  ${script.label}
               </option>
               </#list>
            </select>
         </div>
         <div class="clear">&nbsp;</div>

      </div>

      <div class="main-buttons">
         <span class="create-buttons">
            <button id="${el}-create-button" tabindex="0">${msg("button.create")}</button>
            <button id="${el}-createAnother-button" tabindex="0">${msg("button.createanother")}</button>
         </span>
         <span class="edit-buttons">
            <button id="${el}-save-button" tabindex="0">${msg("button.save")}</button>
         </span>
         <button id="${el}-cancel-button" tabindex="0">${msg("button.cancel")}</button>
      </div>

   </form>
</div>
