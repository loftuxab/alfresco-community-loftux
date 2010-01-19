<script type="text/javascript">//<![CDATA[
   new Alfresco.DocumentInfo("${args.htmlid}").setMessages(${messages});
//]]></script>

<div id="${args.htmlid}-body" class="document-info">
   
   <div class="info-section">
      <div class="heading">${msg("document-info.tags")}</div>
      
      <div id="${args.htmlid}-tags"></div>
   </div>
   
   <div id="${args.htmlid}-permissionSection" class="info-section hidden">
      <div class="heading">${msg("document-info.permissions")}</div>
      
      <div class="info">
         <span class="meta-label">${msg("document-info.managers")}:</span>
         <span id="${args.htmlid}-perms-managers" class="meta-value"></span>
      </div>
      <div class="info">
         <span class="meta-label">${msg("document-info.collaborators")}:</span>
         <span id="${args.htmlid}-perms-collaborators" class="meta-value"></span>
      </div>
      <div class="info">
         <span class="meta-label">${msg("document-info.consumers")}:</span>
         <span id="${args.htmlid}-perms-consumers" class="meta-value"></span>
      </div>
      <div class="info">
         <span class="meta-label">${msg("document-info.everyone")}:</span>
         <span id="${args.htmlid}-perms-everyone" class="meta-value"></span>
      </div>
   </div>

</div>