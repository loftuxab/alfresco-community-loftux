<script type="text/javascript">//<![CDATA[
   new Alfresco.RecordsPermissions("${args.htmlid}").setMessages(${messages});
//]]></script>

<#assign el=args.htmlid>
<div id="${el}-body" class="permissions">
   <div class="title">${msg("label.title", '${page.url.args.docName!""}')?html}</div>
</div>