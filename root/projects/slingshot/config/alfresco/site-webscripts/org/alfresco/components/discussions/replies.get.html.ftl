
<script type="text/javascript">//<![CDATA[
   new Alfresco.TopicReplies("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site}",
      containerId: "${args.container!'discussions'}"
   }).setMessages(
      ${messages}
   );
//]]></script>

<div id="${args.htmlid}-replies-root" class="indented hidden">

</div>
