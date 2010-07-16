<#assign el=args.htmlid>
<script type="text/javascript">//<![CDATA[
   new Alfresco.AdvancedSearch("${el}").setOptions(
   {
      siteId: "${siteId}",
      searchForms: [<#list searchForms as f>
      {
         id: "${f.id}",
         type: "${f.type}",
         description: "${f.description?js_string}"
      }<#if f_has_next>,</#if></#list>]
   }).setMessages(
      ${messages}
   );
//]]></script>

<div id="${el}-body" class="search">
   
   <div id="${el}-typelist"></div>
   
   <div class="share-form form-container">
      <div id="${el}-form" class="form-fields"></div>
   </div>
   
</div>