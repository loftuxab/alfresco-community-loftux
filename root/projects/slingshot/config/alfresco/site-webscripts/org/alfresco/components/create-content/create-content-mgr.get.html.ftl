<script type="text/javascript">//<![CDATA[
   new Alfresco.CreateContentMgr("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}"
   }).setMessages(
      ${messages}
   );
//]]></script>

<div class="create-content-mgr">
   <div class="heading">${msg("create-content-mgr.heading")}</div>
</div>