<script type="text/javascript">//<![CDATA[
   new Alfresco.component.Path("${args.htmlid}").setOptions(
   {
      rootPage: "repository",
      rootLabelId: "path.repository"
   }).setMessages(
      ${messages}
   );
//]]></script>

<div class="path-nav">
   <span class="heading">${msg("path.location")}:</span>
   <span id="${args.htmlid}-defaultPath" class="path-link"><a href="${url.context}/page/repository">${msg("path.repository")}</a></span>
   <span id="${args.htmlid}-path"></span>
</div>
<div id="${args.htmlid}-iconType" class="icon-type"></div>