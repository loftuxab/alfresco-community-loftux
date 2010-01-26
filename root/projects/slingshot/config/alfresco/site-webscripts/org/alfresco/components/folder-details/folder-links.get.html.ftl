<script type="text/javascript">//<![CDATA[
   new Alfresco.FolderLinks("${args.htmlid}").setOptions(
   {
      <#if repositoryUrl??>repositoryUrl: "${repositoryUrl}",</#if>
      externalAuth: ${externalAuth?string("true", "false")}
   }).setMessages(${messages});
//]]></script>

<div id="${args.htmlid}-body" class="folder-links">
	
	<div class="heading">${msg("folder-links.heading")}</div>
	
<#if repositoryUrl??>
   <!-- webdav link -->
   <div id="${args.htmlid}-webdav" class="hidden">
      <div class="url-title"><label for="${args.htmlid}-webdav-url">${msg("folder-links.webdav")}</label></div>
      <input id="${args.htmlid}-webdav-url" class="link-value" />
      <input id="${args.htmlid}-webdav-button" type="button" class="copy-button" value="${msg("folder-links.copy")}" />
   </div>
</#if>

   <#-- cifs link (N/A)
   <div id="${args.htmlid}-cifs" class="hidden">
      <div class="url-title"><label for="${args.htmlid}-cifs-url">${msg("folder-links.cifs")}</label></div>
      <input id="${args.htmlid}-cifs-url" class="link-value" />
      <input id="${args.htmlid}-cifs-button" type="button" class="copy-button" value="${msg("folder-links.copy")}" />
   </div> -->

   <!-- page link -->
   <div id="${args.htmlid}-page">
   	<div class="url-title"><label for="${args.htmlid}-page-url">${msg("folder-links.page")}</label></div>
   	<input id="${args.htmlid}-page-url" class="link-value" />
   	<input id="${args.htmlid}-page-button" type="button" class="copy-button" value="${msg("folder-links.copy")}" />
   </div>

</div>