<script type="text/javascript">//<![CDATA[
   new Alfresco.WebView("${args.htmlid}").setOptions(
   {
      componentId: "${instance.object.id}",
      webviewURI: "${uri}",
      webviewTitle: "${webviewTitle?js_string}",
      webviewHeight: "${height?js_string}",
      isDefault : "${isDefault}"
   });
//]]></script>
<div class="dashlet webview">
   <div class="title">
      <a id="${args.htmlid}-title-link" class="title-link theme-color-5" <#if (isDefault == 'false')>href="${uri}"</#if> target="_blank"><#if webviewTitle != "">${webviewTitle?html}<#else>${msg('label.header')}</#if></a>
      <a id="${args.htmlid}-configWebView-link" class="configure theme-color-5" href="#">${msg("label.configure")}</a>
      <span>&nbsp;</span>
   </div>

   <div class="toolbar"></div>

   <div class="body scrollablePanel" style="<#if height??>height: ${height?html}px</#if>" id="${args.htmlid}-iframeWrapper">
       <iframe frameborder="0" scrolling="auto" width="100%" height="100%" src="${uri}"></iframe>
   </div>

</div>