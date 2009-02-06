<#if form?exists>

   <#assign formId=args.htmlid + "-form">
   <#assign formVar=formId?replace("-", "_")>
   
   <#-- Do we really need the client side JS component, can we just manually include the required files --> 
   
   <script type="text/javascript">//<![CDATA[
      new Alfresco.FormUI("${formId}");
   //]]></script>
   
   <div id="${formId}-container" class="form-container">
      
      <form id="${formId}" method="POST" action="${url.context}/proxy/alfresco${form.submissionUrl}" accept-charset="utf-8"
            <#if page.url.args.submitMode == "multipart">enctype="multipart/form-data"</#if> >
         <#list form.items as item>
            <#if item.control.template?exists>
               <#assign field=item>
               <#include "${field.control.template}" />
            </#if>
         </#list>
         <hr/>
         <#if form.mode != "view"><input type="submit" value="Submit" /></#if>
      </form>
      
   </div>
   
   <#if form.mode != "view">      
      <script type="text/javascript">//<![CDATA[
         var ${formVar} = new Alfresco.forms.Form("${formId}");
         <#if page.url.args.submitMode == "json">
         ${formVar}.setAJAXSubmit(true);
         ${formVar}.setSubmitAsJSON(true);
         </#if>
         ${formVar}.init();
      //]]></script>
   </#if>
</#if>