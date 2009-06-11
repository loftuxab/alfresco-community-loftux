<#if error?exists>
   <div class="error">${error}</div>
<#elseif form?exists>

   <#if form.viewTemplate?? && form.mode == "view">
      <#include "${form.viewTemplate}" />
   <#elseif form.editTemplate?? && form.mode == "edit">
      <#include "${form.editTemplate}" />
   <#elseif form.createTemplate?? && form.mode == "create">
      <#include "${form.createTemplate}" />
   <#else>
      <#assign formId=args.htmlid + "-form">
      <#assign formUI><#if args.formUI??>${args.formUI}<#else>true</#if></#assign>
   
      <#if formUI == "true">
         <script type="text/javascript">//<![CDATA[
            new Alfresco.FormUI("${formId}", "${args.htmlid}").setOptions(
            {
               mode: "${form.mode}",
            <#if form.mode == "view">
               arguments:
               {
                  itemKind: "${form.arguments.itemKind!""}",
                  itemId: "${form.arguments.itemId!""}"
               }
            <#else>
               enctype: "${form.enctype}",
               fieldConstraints: 
               [
                  <#list form.constraints as constraint>
                  {
                     fieldId : "${args.htmlid}_${constraint.fieldId}", 
                     handler : ${constraint.validationHandler}, 
                     params : ${constraint.params}, 
                     event : "${constraint.event}",
                     message : <#if constraint.message?exists>"${constraint.message}"<#else>null</#if>
                  }
                  <#if constraint_has_next>,</#if>
                  </#list>
               ]
            </#if>
            }).setMessages(
               ${messages}
            );
         //]]></script>
      </#if>
      
      <div id="${formId}-container" class="form-container">
         
         <#if form.showCaption?exists && form.showCaption>
            <div id="${formId}-caption" class="caption"><span class="mandatory-indicator">*</span>${msg("form.required.fields")}</div>
         </#if>
            
         <#if form.mode != "view">
            <form id="${formId}" method="${form.method}" accept-charset="utf-8" enctype="${form.enctype}" action="${form.submissionUrl}">
         </#if>
         
         <div id="${formId}-fields" class="form-fields">
            <#list form.items as item>
               <#if item.kind == "set">
                  <@renderSet set=item />
               <#else>
                  <@renderField field=item />
               </#if>
            </#list>
         </div>
            
         <#if form.mode != "view">
            <div id="${formId}-buttons" class="form-buttons">
               <input id="${formId}-submit" type="submit" value="${msg("form.button.submit.label")}" />
               <#if form.showResetButton?exists && form.showResetButton>
                  &nbsp;<input id="${formId}-reset" type="reset" value="${msg("form.button.reset.label")}" />
               </#if>
               <#if form.showCancelButton?exists && form.showCancelButton>
                  &nbsp;<input id="${formId}-cancel" type="button" value="${msg("form.button.cancel.label")}" />
               </#if>
            </div>
            </form>
         </#if>
   
      </div>
   </#if>
</#if>

<#macro renderField field>
   <#if field.control.template?exists>
      <#assign fieldHtmlId=args.htmlid + "_" + field.id >
      <#include "${field.control.template}" />
   </#if>
</#macro>

<#macro renderSet set>
   <#if set.appearance?exists>
      <#if set.appearance == "fieldset">
         <fieldset><legend>${set.label}</legend>
      <#elseif set.appearance == "panel">
         <div class="form-panel">
            <div class="form-panel-heading">${set.label}</div>
            <div class="form-panel-body">
      </#if>
   </#if>
   
   <#list set.children as item>
      <#if item.kind == "set">
         <@renderSet set=item />
      <#else>
         <@renderField field=item />
      </#if>
   </#list>
   
   <#if set.appearance?exists>
      <#if set.appearance == "fieldset">
         </fieldset>
      <#elseif set.appearance == "panel">
            </div>
         </div>
      </#if>
   </#if>
</#macro>

