<script type="text/javascript">//<![CDATA[
   new Alfresco.DocumentLinks("${args.htmlid}").setMessages(${messages});
//]]></script>

<div id="${args.htmlid}-body" class="document-links">
	
	<div class="heading">${msg("document-links.heading")}</div>
	
	<!-- download link -->
	<div class="url-title"><label for="${args.htmlid}-download-url">${msg("document-links.download")}</label></div>
	<input id="${args.htmlid}-download-url" class="link-value" />
	<br/>
	<input id="${args.htmlid}-download-button" type="button" class="copy-button" value="${msg("document-links.copy")}" />
	
	<!-- document/view link -->
	<div class="url-title"><label for="${args.htmlid}-view-url">${msg("document-links.view")}</label></div>
	<input id="${args.htmlid}-view-url" class="link-value" />
	<br/>
	<input id="${args.htmlid}-view-button" type="button" class="copy-button" value="${msg("document-links.copy")}" />

   <!-- page link -->
	<div class="url-title"><label for="${args.htmlid}-page-url">${msg("document-links.page")}</label></div>
	<input id="${args.htmlid}-page-url" class="link-value" />
	<br/>
	<input id="${args.htmlid}-page-button" type="button" class="copy-button" value="${msg("document-links.copy")}" />


</div>