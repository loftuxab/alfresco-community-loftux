<script type="text/javascript">//<![CDATA[
   new Alfresco.FolderInfo("${args.htmlid}").setMessages(${messages});
//]]></script>

<div id="${args.htmlid}-body" class="folder-info">
   
   <div class="info-section">
      <div class="heading">${msg("label.tags")}</div>
      
      <div id="${args.htmlid}-tags"></div>
   </div>
   
   <div class="info-section">
      <div class="heading">${msg("folder-info.permissions")}</div>
      
      <div class="info">
         <span class="meta-label">${msg("folder-info.managers")}:</span>
         <span id="${args.htmlid}-perms-managers" class="meta-value"></span>
      </div>
      <div class="info">
         <span class="meta-label">${msg("folder-info.collaborators")}:</span>
         <span id="${args.htmlid}-perms-collaborators" class="meta-value"></span>
      </div>
      <div class="info">
         <span class="meta-label">${msg("folder-info.consumers")}:</span>
         <span id="${args.htmlid}-perms-consumers" class="meta-value"></span>
      </div>
      <div class="info">
         <span class="meta-label">${msg("folder-info.everyone")}:</span>
         <span id="${args.htmlid}-perms-everyone" class="meta-value"></span>
      </div>
   </div>

</div>