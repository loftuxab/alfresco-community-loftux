<#assign el=args.htmlid>
<script type="text/javascript">//<![CDATA[
   new Alfresco.AdvancedSearch("${el}").setOptions(
   {
      siteId: "${siteId}"
   }).setMessages(
      ${messages}
   );
//]]></script>

<div id="${el}-body" class="search">
   
</div>