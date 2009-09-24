<script type="text/javascript" charset="utf-8">    
    new Alfresco.RM_Audit('${htmlid}-audit').setOptions(
       {
          'siteId': "${page.url.templateArgs.site!"rm"}",
          'containerId': "${template.properties.container!"documentLibrary"}",
          'viewMode':Alfresco.RM_Audit.VIEW_MODE_COMPACT
          <#if (nodeRef?exists)>
            ,'nodeRef' : '${nodeRef}'
          </#if>          
       }
    ).setMessages(${messages});
</script>
<div id="${htmlid}-audit">
   <#-- for a specified noderef -->
   <#if (page.url.args.nodeName??)>
      <h1>${msg("label.title-for",page.url.args.nodeName)}</h1>
   <#else>
      <h1>${msg("label.title")}</h1>
   </#if>
   <div class="auditActions">
      <button id="${htmlid}-audit-export" name="audit-export" class="audit-export">${msg("label.button-export")}</button>
      <button id="${htmlid}-audit-declare-record" name="audit-declare-record" class="audit-declare-record">${msg("label.button-declare-record")}</button>            
   </div>
   <div class="audit-info">
      <span class="label">${msg('label.from')}:</span>
      <span class="value">${auditStatus.startedDate?datetime?string("EEE MMM dd yyyy HH:mm:ss 'GMT'Z")}</span>
      <span class="label">${msg('label.to')}:</span>
      <span class="value">${auditStatus.stoppedDate?datetime?string("EEE MMM dd yyyy HH:mm:ss 'GMT'Z")}</span>
   <#-- only for full log (not noderef) -->
   <#if (!page.url.args.nodeName??)>
      <span class="label">${msg('label.property')}:</span>
      <span class="value">${msg('label.all')}</span>
      <span class="label">${msg('label.user')}:</span>
      <span class="value">${msg('label.all')}</span>
      <span class="label">${msg('label.event')}:</span>
      <span class="value">${msg('label.all')}</span>
   </#if>
   </div>
   <#list auditStatus.entries as x>
      <div class="audit-entry">
         <div class="audit-entry-header">
            <span class="label">${msg('label.timestamp')}:</span>
            <span class="value">${x.timestampDate?datetime?string("EEE MMM dd yyyy HH:mm:ss 'GMT'Z")}</span>
            <span class="label">${msg('label.user')}:</span>
            <span class="value">${x.fullName}</span>
            <span class="label">${msg('label.event')}:</span>
            <span class="value">${x.event}</span>
         </div>
         <div class="audit-entry-node">
            <span class="label">${msg('label.identifier')}:</span><span class="value">${x.identifier}</span>
            <span class="label">${msg('label.type')}:</span><span class="value">${x.nodeType}</span>
            <span class="label">${msg('label.location')}:</span><span class="value">${x.path}</span>
         </div>
         <#if (x.changedValues?size >0)>
            <table class="changed-values-table" cellspacing="0">
               <thead>
                  <tr>
                     <th>${msg('label.property')}</th>
                     <th>${msg('label.previous-value')}</th>
                     <th>${msg('label.new-value')}</th>
                  </tr>
               </thead>
               <tbody>
                  <#list x.changedValues as v>
                  <tr>
                     <td>${v.name}</td>
                  <#if (v.previous == "")>
                     <td>${msg('label.no-previous')?html}</td>
                  <#else>
                     <td>${v.previous}</td>
                  </#if>
                     <td>${v.new}</td>
                  </tr>
                  </#list>
               </tbody>
            </table>      
         </#if>
      </div>
   </#list>
</div>