<script type="text/javascript">//<![CDATA[
   new Alfresco.RecordsPermissions("${args.htmlid}").setOptions({
    siteId: "${page.url.templateArgs.site!""}",
    nodeRef: "${page.url.args.nodeRef!""}",
    docName: "${page.url.args.docName!""}"
 }).setMessages(${messages});
//]]></script>

<#assign el=args.htmlid>
<div id="${el}-body" class="permissions">
   <div class="title">${msg("label.title", '${page.url.args.docName!""}')?html}</div>
</div>