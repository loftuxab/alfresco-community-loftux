<#import "form.lib.ftl" as formLib />

<#if error?exists>
   <div class="error">${error}</div>
<#elseif form?exists>
   <#assign formId=args.htmlid + "-form">
   <#assign formUI><#if args.formUI??>${args.formUI}<#else>true</#if></#assign>
      
   <#if form.viewTemplate?? && form.mode == "view">
      <#include "${form.viewTemplate}" />
   <#elseif form.editTemplate?? && form.mode == "edit">
      <#include "${form.editTemplate}" />
   <#elseif form.createTemplate?? && form.mode == "create">
      <#include "${form.createTemplate}" />
   <#else>
      <#if formUI == "true">
         <@formLib.renderFormsRuntime formId=formId />
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
                  <@formLib.renderSet set=item />
               <#else>
                  <@formLib.renderField field=item />
               </#if>
            </#list>
         </div>
            
         <#if form.mode != "view">
            <@formLib.renderFormButtons formId=formId />
            </form>
         </#if>
   
      </div>
   </#if>
</#if>


