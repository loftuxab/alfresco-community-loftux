<script type="text/javascript">//<![CDATA[
   new Alfresco.DocumentInfo("${args.htmlid}").setMessages(${messages});
//]]></script>

<div id="${args.htmlid}-body" class="document-info">
   
   <div class="info-section">
      <div class="heading">${msg("document-info.heading")}</div>
      
      <div>
         <span class="meta-label">${msg("document-info.name")}:</span>
         <span id="${args.htmlid}-meta-name" class="meta-value"></span>
      </div>
      <div>
         <span class="meta-label">${msg("document-info.content-type")}:</span>
         <span id="${args.htmlid}-meta-content-type" class="meta-value"></span>
      </div>
      <!--
      <div>
         <span class="meta-encoding">${msg("document-info.encoding")}:</span>
         <span id="${args.htmlid}-meta-encoding" class="meta-value"></span>
      </div>
      -->
      <div>
         <span class="meta-label">${msg("document-info.title")}:</span>
         <span id="${args.htmlid}-meta-title" class="meta-value"></span>
      </div>
      <div>
         <span class="meta-label">${msg("document-info.description")}:</span>
         <span id="${args.htmlid}-meta-description" class="meta-value"></span>
      </div>
      <!--
      <div>
         <span class="meta-label">${msg("document-info.owner")}:</span>
         <span id="${args.htmlid}-meta-owner" class="meta-value"></span>
      </div>
      -->
      <div>
         <span class="meta-label">${msg("document-info.size")}:</span>
         <span id="${args.htmlid}-meta-size" class="meta-value"></span>
      </div>
      <p></p>
      <div>
         <span class="meta-label">${msg("document-info.creator")}:</span>
         <span id="${args.htmlid}-meta-creator" class="meta-value"></span>
      </div>
      <div>
         <span class="meta-label">${msg("document-info.createdon")}:</span>
         <span id="${args.htmlid}-meta-createdon" class="meta-value"></span>
      </div>
      <div>
         <span class="meta-label">${msg("document-info.modifier")}:</span>
         <span id="${args.htmlid}-meta-modifier" class="meta-value"></span>
      </div>
      <div>
         <span class="meta-label">${msg("document-info.modifiedon")}:</span>
         <span id="${args.htmlid}-meta-modifiedon" class="meta-value"></span>
      </div>
   </div>
   
   <div class="info-section">
      <div class="heading">${msg("document-info.tags")}</div>
      
      <div id="${args.htmlid}-tags"></div>
   </div>
   
   <div class="info-section">
      <div class="heading">${msg("document-info.permissions")}</div>
      
      <div>
         <span class="meta-label">${msg("document-info.managers")}:</span>
         <span id="${args.htmlid}-perms-managers" class="meta-value"></span>
      </div>
      <div>
         <span class="meta-label">${msg("document-info.collaborators")}:</span>
         <span id="${args.htmlid}-perms-collaborators" class="meta-value"></span>
      </div>
      <div>
         <span class="meta-label">${msg("document-info.consumers")}:</span>
         <span id="${args.htmlid}-perms-consumers" class="meta-value"></span>
      </div>
      <div>
         <span class="meta-label">${msg("document-info.everyone")}:</span>
         <span id="${args.htmlid}-perms-everyone" class="meta-value"></span>
      </div>
   </div>

</div>