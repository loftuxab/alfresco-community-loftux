<script type="text/javascript">//<![CDATA[
   new Alfresco.FolderLinks("${args.htmlid}").setOptions(
   {
      externalAuth: ${externalAuth?string("true", "false")}
   }).setMessages(${messages});
//]]></script>

<div id="${args.htmlid}-body" class="folder-links">
	
	<div class="heading">${msg("folder-links.heading")}</div>
	
   <!-- page link -->
	<div class="url-title"><label for="${args.htmlid}-page-url">${msg("folder-links.page")}</label></div>
	<input id="${args.htmlid}-page-url" class="link-value" />
	<br/>
	<input id="${args.htmlid}-page-button" type="button" class="copy-button" value="${msg("folder-links.copy")}" />

</div>