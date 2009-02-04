<#if form?exists>

   <script type="text/javascript">//<![CDATA[
      new Alfresco.FormUI("");
   //]]></script>
   
   <div id="-form" class="form">
      
      <form method="POST" action="${url.context}/proxy/alfresco${form.submissionUrl}"
            <#if page.url.args.submitMode == "multipart">enctype="multipart/form-data"</#if> accept-charset="utf-8">
         <!-- TODO: See if we can use absolute template paths/urls. -->
         <#list form.items as item>
            <#if item.control.template?exists>
               <#include "${item.control.template}" />
            </#if>
         </#list>
         <hr/>
         <input type="submit" value="Submit" />
      </form>
      
   </div>

</#if>