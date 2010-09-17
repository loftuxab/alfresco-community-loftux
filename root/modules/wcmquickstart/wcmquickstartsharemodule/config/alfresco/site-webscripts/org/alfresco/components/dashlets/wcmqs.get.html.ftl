<script type="text/javascript">//<![CDATA[
   new Alfresco.dashlet.WCMQS("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}"
   }).setMessages(${messages});
//]]></script>

<div class="dashlet">
   <div class="title">${msg("label.title")}</div>
   <div class="body theme-color-1">      
      <#if !dataloaded>             
      <div id="${args.htmlid}-load-data" class="detail-list-item last-item" >
         <a id="${args.htmlid}-load-data-link" href="#">${msg("label.load-test-data")}</a>
      </div>      
      <#else>
      <div class="detail-list-item last-item" >
         <a href="${msg("url.help")}" target="_new">${msg("label.help_link")}</a>
      </div>
      </#if>
   </div>
</div>