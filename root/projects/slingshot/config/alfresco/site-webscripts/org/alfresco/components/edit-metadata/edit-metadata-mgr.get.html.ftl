<#include "../../include/alfresco-macros.lib.ftl" />
<script type="text/javascript">//<![CDATA[
   new Alfresco.FormManager("${args.htmlid}").setOptions(
   {
      failureMessage: "edit-metadata-mgr.update.failed",
      forwardUrl: "${siteURL((nodeType!"document") + "-details?nodeRef=" + (nodeRef!page.url.args.nodeRef))}"
   }).setMessages(
      ${messages}
   );
//]]></script>
<div class="form-manager">
   <h1>${msg("edit-metadata-mgr.heading")}</h1>
</div>